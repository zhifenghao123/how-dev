#!/usr/bin/env bash
# 发布脚本，主要用于将api、common等模块构建并发布到maven仓库中

# Be sure your script exit whenever encounter errors
set -e

export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
export LANGUAGE=en_US.UTF-8

# 构建目录
BUILD_HOME=$(cd "$(dirname "$0")"; pwd)
# 发布的模块名
PUBLISH_MODULE_NAME=${1}

# 进入到构建目录
cd ${BUILD_HOME}

# 构建并发布
mvn -U clean deploy -Dmaven.test.skip=true -pl ${PUBLISH_MODULE_NAME} -am
mvn -U clean package -Dmaven.test.skip=true