import request from '@/utils/request'; // 引入带JWT拦截的请求工具
import { getCurrentUser } from './user'; // 获取当前用户信息

// ------------------------------ 链码函数映射 ------------------------------
const CHAINCODE_FUNCTIONS = {
  CREATE_ASSET: 'createAsset',
  TRANSFER_ASSET: 'transferAsset',
  QUERY_ASSET: 'readAsset',
  QUERY_ALL_ASSETS: 'queryAllAssets'
};

// ------------------------------ 通用链码调用 ------------------------------
/**
 * 调用Hyperledger Fabric链码函数（自动注入用户信息）
 * @param {string} functionName - 链码函数名（参考CHAINCODE_FUNCTIONS）
 * @param {Object} params - 函数参数（键值对形式，如{ assetId: 'BOOK_123', owner: 'user123' }）
 * @returns {Promise} - 包含交易哈希（成功时）或查询结果的响应
 */
export function invokeChaincode(functionName, params = {}) {
  return getCurrentUser().then(user => {
    return request({
      url: '/api/blockchain/invoke', // 对应后端链码调用接口
      method: 'post',
      data: {
        function: functionName,
        args: Object.entries(params).flatMap(([k, v]) => [k, v]), // 转换为键值对数组
        userId: user.userId, // 传递当前用户ID（用于链码权限校验）
        organization: 'org1' // 组织信息（需与后端配置一致）
      }
    });
  });
}

// ------------------------------ 具体业务封装 ------------------------------
/**
 * 创建书籍资产（普通/盲盒）
 * @param {Object} bookData - 书籍数据（包含assetId, owner, type, timestamp）
 * @returns {Promise<string>} - 区块链交易哈希
 */
export function createBookAsset(bookData) {
  return invokeChaincode(CHAINCODE_FUNCTIONS.CREATE_ASSET, {
    ...bookData,
    type: bookData.isBlindBox ? 'BLIND_BOX' : 'NORMAL' // 映射链码资产类型
  });
}

/**
 * 转移书籍所有权
 * @param {string} assetId - 资产ID
 * @param {string} newOwner - 新所有者ID
 * @returns {Promise<string>} - 交易哈希
 */
export function transferBookOwnership(assetId, newOwner) {
  return invokeChaincode(CHAINCODE_FUNCTIONS.TRANSFER_ASSET, {
    assetId,
    owner: newOwner // 链码函数参数为'owner'（对应transferAsset的newOwner参数）
  });
}

/**
 * 查询单本书籍资产
 * @param {string} assetId - 资产ID
 * @returns {Promise<Object>} - 书籍资产信息（JSON格式）
 */
export function queryBookAsset(assetId) {
  return invokeChaincode(CHAINCODE_FUNCTIONS.QUERY_ASSET, { assetId });
}

/**
 * 查询所有书籍资产（可按类型过滤）
 * @param {string} type - 资产类型（NORMAL/BLIND_BOX，可选）
 * @returns {Promise<Array>} - 资产列表
 */
export function queryAllBookAssets(type) {
  return invokeChaincode(CHAINCODE_FUNCTIONS.QUERY_ALL_ASSETS, { type });
}

// ------------------------------ 交易查询 ------------------------------
/**
 * 查询区块链交易状态
 * @param {string} txHash - 交易哈希
 * @returns {Promise<Object>} - 交易状态（COMPLETED/PENDING/FAILED）
 */
export function getTransactionStatus(txHash) {
  return request({
    url: `/api/blockchain/transactions/${txHash}`, // 对应后端交易查询接口
    method: 'get'
  });
}

// ------------------------------ 事件监听（示例） ------------------------------
/**
 * 监听链码事件（需后端支持WebSocket）
 * @param {Function} callback - 事件回调函数（参数为事件数据）
 */
export function listenToChaincodeEvents(callback) {
  const socket = new WebSocket('ws://localhost:8080/chaincode-events');
  socket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    callback(data); // 处理事件（如资产转移通知）
  };
  return () => socket.close(); // 返回取消监听函数
}