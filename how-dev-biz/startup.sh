#!/bin/bash

export LANG=zh_CN.UTF-8
# app name
APP_NAME=how-dev-biz

# app http port
APP_PORT=8080

# app home
APP_HOME=$(dirname $(readlink -f "$0"))

# apollo env(online/offline)
#ENV=$2
ENV=$(echo "$2" | tr [A-Z] [a-z])

# JVM heap
XMS_CONFIG=2G

# OOM path
OOM_HOME=$APP_HOME/logs

# 服务运行环境目录, 包含jdk/tomcat等
SERVICE_RUN_ENV_HOME=/home/hao_root/service_run_env

# 设置java环境
readonly WORK_DIR=$(dirname $(readlink -f $0))
readonly BASE_DIR=$WORK_DIR

if [ -f "$BASE_DIR/env/profile" ];then
    readonly PROFILE="${BASE_DIR}/env/profile"
    . ${PROFILE}
fi

# 打印日志
log(){
    echo -e "\033[31m$@\033[0m"
}

# 校验参数个数
if [ $# != 2 ];then
    log 'please ensure the count of params'
    exit -1
fi

# 查看是否存在logs目录，没有则创建
if [ ! -d "$APP_HOME/logs" ];then
    mkdir -p $APP_HOME/logs
fi

# 判断启动环境
environment(){
    if [ "$ENV" == "prod" ]; then
      XMS_CONFIG=4G
    fi
}

# 刷新环境
refresh(){
    log "the ENV is $ENV !!!"
    # 可以在Java应用程序中读取，做一下差异化的处理
    export HAO_ENV=$ENV

    if [ -z "$JAVA_HOME" ];then
      export JAVA_HOME=${SERVICE_RUN_ENV_HOME}/java/jdk1.8.0_51
      export PATH=$JAVA_HOME/bin:$PATH
      export CLASSPATH=.:$JAVA_HOME/lib
    fi
}



# 备份console文件
bak_console(){
    log_directory=`date "+%Y%m%d"`
    if [ ! -f "$APP_HOME/logs/console.log" ];then
        log 'has no console.log file to backup'
        return
    else
        if [ ! -d "$APP_HOME/logs/$log_directory" ];then
                mkdir -p $APP_HOME/logs/$log_directory
        fi
        # 进入日志目录开始备份
        cd logs
        file_name=${APP_HOME}-console-`date "+%Y%m%d%H%M%S"`.tar.gz
        # 压缩备份文件
        tar -zcvf $log_directory/$file_name console.log >> /dev/null
        # 备份dump文件
        dump_file=`ls|grep hprof`
        if [ -f "$dump" ];then
            dump_tmp=`date "+%Y%m%d%H%M%S"`_$dump_file;
            mv $dump_file $log_directory/$dump_tmp
        fi
        # 退回上级目录
        cd ..
    fi
}


# 启动服务
start(){

    cd $APP_HOME

    # 备份console.log
    #bak_console

    JVM_OPTS="-Xms$XMS_CONFIG -Xmx$XMS_CONFIG -server -XX:+UseG1GC -XX:NewRatio=1
    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$OOM_HOME -verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCDetails
    -Xloggc:$OOM_HOME/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=512M
    -XX:ParallelGCThreads=11 -XX:ConcGCThreads=4"

    #DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8082"

    echo "java -Dspring.profiles.active=$ENV $JVM_OPTS -jar $APP_NAME.jar &"
    nohup ${JAVA_HOME}/bin/java -jar -Dspring.profiles.active=$ENV $JVM_OPTS $DEBUG_OPTS ./$APP_NAME.jar > logs/console.log 2>&1 &

    log 'start successfully, please check app logs!!!'
    exit 0
}

# 停止服务
stop(){

    PID=`ps -ef| grep $APP_NAME.jar |grep java | grep -v grep |awk '{print $2}'`

    if [ ! -z "$PID" ];then
        kill -15 $PID
        log 'the app will be shutdown now...'
    else
        log 'no found the PID for app, please start currently!'
        return
    fi

    # 检测进程是否shutdown成功，如果90s内还未关闭则执行 kill -9
    i=0;
    while [ $i -le 90 ]; do
        PID=`ps -ef| grep $APP_NAME.jar |grep java | grep -v grep |awk '{print $2}'`
        if [ -z "$PID" ];then
            log "$APP_NAME has shutdown done successfully!!!"
            return
        else
            let i+=3
            sleep 3
            log "shutdown command already waiting $i s!!!"
        fi
    done

    log "$APP_NAME shutdown fail in $i s, it will be kill -9 !!!"
    PID=`ps -ef| grep $APP_NAME.jar |grep java | grep -v grep |awk '{print $2}'`
    if [ ! -z "$PID" ];then
        kill -9 $PID
    fi
}


#ENV=$(echo "$2" | tr [A-Z] [a-z])
case C"$1" in
    Cstop)
        stop
        log "Done!!!"
        ;;
    Cstart|Crestart)
        environment
        stop
        refresh
        start
        log "Done!!!"
        ;;
    C*)
        log "Usage: $0 [start|stop|restart]"
        ;;
esac