#!/bin/bash
<<INFO
SCRIPT:ci_trigger.sh
AUTHOR:haozhifeng
DATE:2024-03-20
DESCRIBE: 触发ci流程
note: 本脚本用于触发ci流程，在这个过程中，需要通过解析ci_config.yaml文件，获取到需要执行的脚本。而在解析ci_config.yaml文件时，需要用到yq工具，所以需要提前安装好yq工具。
      https://github.com/mikefarah/yq
INFO

# ci_config.yaml 文件路径
YAML_FILE="ci_config.yaml"

# 检查配置文件是否存在
if [ ! -f "$YAML_FILE" ]; then
    echo "配置文件 $YAML_FILE 不存在。"
    exit 1
fi

# 读取默认的构建配置名称
PROFILE_NAME=$(yq e '.build.profile' "$YAML_FILE")

# 检查是否找到了profile
if [ -z "$PROFILE_NAME" ]; then
    echo "配置文件 $YAML_FILE 中未找到 'build: profile' 的值。"
    exit 1
fi

# 获取指定profile的命令
COMMAND=$(yq e ".profiles[] | select(.name == \"$PROFILE_NAME\").command" "$YAML_FILE")

# 获取指定profile的用户
USER=$(yq e ".profiles[] | select(.name == \"$PROFILE_NAME\").user" "$YAML_FILE")

# 获取指定profile的运行环境（可选，如果需要的话）
RUNS_ON=$(yq e ".profiles[] | select(.name == \"$PROFILE_NAME\").runs-on" "$YAML_FILE")

# 获取指定profile的maven版本（可选，如果需要的话）
MAVEN_VERSION=$(yq e ".profiles[] | select(.name == \"$PROFILE_NAME\").with.maven-version" "$YAML_FILE")

# 获取指定profile的jdk版本（可选，如果需要的话）
JDK_VERSION=$(yq e ".profiles[] | select(.name == \"$PROFILE_NAME\").with.jdk-version" "$YAML_FILE")

# 打印获取到的配置信息（可选）
echo "Profile Name: $PROFILE_NAME"
echo "Command: $COMMAND"
echo "User: $USER"
echo "Runs On: $RUNS_ON"
echo "Maven Version: $MAVEN_VERSION"
echo "JDK Version: $JDK_VERSION"

# 切换到用户并执行命令
sudo -u "$USER" bash -c "$COMMAND"