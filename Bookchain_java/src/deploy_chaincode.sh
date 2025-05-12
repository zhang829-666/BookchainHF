#!/bin/bash

# 脚本参数配置
CHAINCODE_NAME="bookchain"          # 链码名称
CHAINCODE_VERSION="1.0"              # 链码版本
CHAINCODE_PATH="org.bookchain"       # 链码Go路径（需与链码包结构一致）
CHANNEL_NAME="mychannel"             # 目标通道名称
ORG_ADMIN="Admin@org1.example.com"   # 组织管理员MSP标识
PEER_ADDRESS="peer0.org1.example.com:7051"  # Peer节点地址

# 检查环境变量（确保已加载Fabric环境）
if [ -z "$CORE_PEER_MSPCONFIGPATH" ]; then
    echo "错误：未检测到Fabric环境变量，需先source fabric-samples/bin/env.sh"
    exit 1
fi

# 安装链码到当前节点
echo "==================== 安装链码 ===================="
peer chaincode install \
    -n "$CHAINCODE_NAME" \
    -v "$CHAINCODE_VERSION" \
    -p "$CHAINCODE_PATH"
if [ $? -ne 0 ]; then
    echo "链码安装失败"
    exit 1
fi
echo "链码安装成功"

# 实例化链码到通道（需管理员权限）
echo "==================== 实例化链码 ===================="
peer chaincode instantiate \
    -o orderer.example.com:7050 \
    -C "$CHANNEL_NAME" \
    -n "$CHAINCODE_NAME" \
    -v "$CHAINCODE_VERSION" \
    -c '{"Args":[]}' \
    -P "OR('Org1MSP.member')"  # 背书策略：Org1成员签名
if [ $? -ne 0 ]; then
    echo "链码实例化失败"
    exit 1
fi
echo "链码实例化成功"

# 验证链码调用（查询链码信息）
echo "==================== 验证链码 ===================="
peer chaincode query \
    -C "$CHANNEL_NAME" \
    -n "$CHAINCODE_NAME" \
    -c '{"function":"getChaincodeInfo","Args":[]}'
if [ $? -eq 0 ]; then
    echo "链码部署验证通过"
else
    echo "链码部署验证失败"
    exit 1
fi

echo "==================== 部署完成 ===================="