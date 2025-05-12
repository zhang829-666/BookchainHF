import request from '@/utils/request'; // 引入封装好的Axios请求工具

// ------------------------------ 书籍基本操作 ------------------------------
/**
 * 上传书籍（对应bookUpload.vue的handleUpload）
 * @param {FormData} formData - 包含书籍信息和封面文件的FormData
 * @returns {Promise} - 包含区块链交易哈希的响应
 */
export function uploadBook(formData) {
  return request({
    url: '/api/books/upload', // 对应后端书籍上传接口
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' } // 明确设置请求头
  });
}

/**
 * 查询单本书籍详情（对应书籍详情页）
 * @param {string} bookId - 书籍ID
 * @returns {Promise} - 书籍详细信息（包含owner、blockchainTxHash等）
 */
export function getBookDetail(bookId) {
  return request({
    url: `/api/books/${bookId}`,
    method: 'get'
  });
}

/**
 * 更新书籍信息（如所有权转移，对应myBooks.vue的handleTransfer）
 * @param {string} bookId - 书籍ID
 * @param {Object} data - 包含新所有者ID等数据
 * @returns {Promise} - 操作结果
 */
export function updateBookOwnership(bookId, data) {
  return request({
    url: `/api/books/${bookId}/transfer`, // 对应后端所有权转移接口
    method: 'put',
    data
  });
}

// ------------------------------ 盲盒相关操作 ------------------------------
/**
 * 创建盲盒书籍（对应bookUpload.vue的盲盒选项）
 * @param {FormData} formData - 包含盲盒信息的FormData（isBlindBox=true）
 * @returns {Promise} - 盲盒ID和链上交易哈希
 */
export function createBlindBox(formData) {
  return request({
    url: '/api/blind-boxes',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}

/**
 * 打开盲盒（揭秘书籍信息，对应BlindBoxMarket.vue的revealBox）
 * @param {string} boxId - 盲盒ID
 * @returns {Promise} - 书籍真实信息（title、author等）
 */
export function revealBlindBox(boxId) {
  return request({
    url: `/api/blind-boxes/${boxId}/reveal`, // 对应后端揭秘接口
    method: 'get'
  });
}

// ------------------------------ 列表查询 ------------------------------
/**
 * 获取盲盒市场列表（对应BlindBoxMarket.vue）
 * @param {object} params - 筛选参数（category、page等）
 * @returns {Promise} - 盲盒列表
 */
export function getBlindBoxMarketList(params = {}) {
  return request({
    url: '/api/blind-boxes/market', // 假设后端提供市场列表接口
    method: 'get',
    params: { ...params, type: 'BLIND_BOX' } // 强制筛选盲盒类型
  });
}

/**
 * 获取所有书籍列表（对应书籍浏览页）
 * @param {object} params - 分页和筛选参数（page、category等）
 * @returns {Promise} - 书籍列表和总数
 */
export function getAllBooks(params = {}) {
  return request({
    url: '/api/books',
    method: 'get',
    params
  });
}

// ------------------------------ 区块链交互 ------------------------------
/**
 * 查询书籍的区块链交易记录（对应myBooks.vue的viewTransaction）
 * @param {string} txHash - 交易哈希
 * @returns {Promise} - 链上交易详情
 */
export function getBlockchainTransaction(txHash) {
  return request({
    url: `/api/transactions/${txHash}`, // 对应TransactionController的按哈希查询接口
    method: 'get'
  });
}