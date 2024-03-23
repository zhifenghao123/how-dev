#!/usr/bin/env bash
# 构建脚本，主要用于将biz、manage等模块构建成jar包

# Be sure your script exit whenever encounter errors
set -e

# Set the locale to en_US.UTF-8 for proper UTF-8 support in maven
# 确保每次你打开一个新的终端会话时，这些环境变量都会被设置。
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
export LANGUAGE=en_US.UTF-8

# 构建目录
BUILD_HOME=$(cd "$(dirname "$0")"; pwd)
# 构建的模块名
BUILD_MODULE_NAME=${1}

# 进入到构建目录
# 其实不用也可以，因为本身就在当前构建目中执行构建动作呢
cd ${BUILD_HOME}

# Build the project using maven
mvn -U clean package -Dmaven.test.skip=true

# 构建完成后，做一些清理工作
# 如：删除一些不需要的临时文件或目录；创建输出目录，并移动文件
rm -rf output
mkdir output

# 拷贝jar包到output目录
# 注意，这里的源目录target，其实是与pom.xml中配置的<build><directory></directory><build>配置的目录一致，如果配置的是output，则这里就是output；如果没有配置（默认target），则这里就是默认的target
cp ${BUILD_MODULE_NAME}/target/*.jar ./output/${BUILD_MODULE_NAME}.jar
# 拷贝启动脚本
cp ${BUILD_MODULE_NAME}/startup.sh ./output/
# 拷贝启动配置文件
cp -r ${BUILD_MODULE_NAME}/deploy ./output