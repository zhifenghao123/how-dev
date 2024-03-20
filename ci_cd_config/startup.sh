#!/bin/bash

# 获取工作目录
readonly WORK_DIR=$(dirname $(readlink -f $0))
readonly BASE_DIR=$(dirname ${WORK_DIR})
readonly PROFILE_JDK="${BASE_DIR}/env/profile"
if [ -f "${PROFILE_JDK}" ];then
    . ${PROFILE_JDK}
fi
readonly JAVA_BIN="${JAVA_HOME}/bin/java"

echo ${JAVA_BIN}

nohup ${JAVA_BIN} -jar xx.jar >> jdk.log 2>&1 &

# 配置启动命令
start() {
    echo "start"
    echo ${JAVA_HOME}
}

# 配置停止命令
stop() {
    echo "stop"
    echo ${JAVA_HOME}
}

# 配置重启命令
restart() {
    echo "restart"
    echo ${JAVA_HOME}
}

status() {
    echo "status"
    echo ${JAVA_HOME}
}

case C"$1" in
    Cstart)
        start
        ;;
    Crestart)
        restart
        ;;
    Cstop)
        stop
        ;;
    Cstatus)
        status
        ;;
    C*)
        echo "Usage: $0 {start|stop|restart|status}"
        ;;
esac

exit 0