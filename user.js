import request from '@/utils/request'; // 引入请求工具（需包含JWT拦截器）

// ------------------------------ 用户认证 ------------------------------
/**
 * 用户登录（对应后端JwtTokenUtil和登录接口）
 * @param {Object} data - 包含username/password的对象
 * @returns {Promise} - 包含token的响应
 */
export function login(data) {
  return request({
    url: '/api/auth/login', // 对应后端登录接口路径（假设为/api/auth/login）
    method: 'post',
    data
  });
}

// ------------------------------ 用户信息 ------------------------------
/**
 * 获取当前用户信息（需认证，对应@PreAuthorize('hasRole('USER')')）
 * @returns {Promise} - 用户信息（包含userId、username、角色等）
 */
export function getCurrentUser() {
  return request({
    url: '/api/users/me',
    method: 'get'
  });
}

// ------------------------------ 书籍相关 ------------------------------
/**
 * 获取用户的书籍列表（对应myBooks.vue的getUserBooks）
 * @param {number} page - 页码（默认1）
 * @returns {Promise} - 包含书籍列表和总数的响应
 */
export function getUserBooks(page = 1) {
  return request({
    url: '/api/books/user', // 对应后端/books/user接口（假设为用户专属书籍列表）
    method: 'get',
    params: { page }
  });
}

// ------------------------------ 交易记录 ------------------------------
/**
 * 获取用户的交易记录（对应TransactionList组件）
 * @param {number} page - 页码
 * @param {string} type - 交易类型（如TRANSFER、CREATE_BLIND_BOX）
 * @param {Array} dateRange - 时间范围数组（如[startDate, endDate]）
 * @returns {Promise} - 交易记录列表
 */
export function getUserTransactions(page = 1, type = '', dateRange = []) {
  return request({
    url: '/api/transactions/user', // 对应TransactionController的/user/{userId}接口
    method: 'get',
    params: { page, type, startDate: dateRange[0], endDate: dateRange[1] }
  });
}

// ------------------------------ 盲盒市场 ------------------------------
/**
 * 购买盲盒（对应BlindBoxMarket.vue的purchaseBox）
 * @param {string} boxId - 盲盒ID
 * @returns {Promise} - 交易哈希和状态
 */
export function purchaseBlindBox(boxId) {
  return request({
    url: `/api/blind-boxes/${boxId}/purchase`, // 对应盲盒购买接口
    method: 'post'
  });
}

// ------------------------------ 通用工具 ------------------------------
/**
 * 生成UUID（对应后端UUIDGenerator工具类）
 * @returns {Promise} - 32位UUID
 */
export function generateUUID() {
  return request({
    url: '/api/utils/uuid', // 假设后端提供UUID生成接口
    method: 'get'
  });
}