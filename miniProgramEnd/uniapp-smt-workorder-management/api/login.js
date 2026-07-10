/**
 * 登录相关 API
 */

import { post } from '../utils/request'

/**
 * 小程序登录
 * @param {string} username - 用户名
 * @param {string} password - 明文密码
 * @returns {Promise<Object>} 包含 token、userId、username、name、roleId、roleName
 */
export const login = (username, password) => {
  return post('/miniprogram/login', { username, password })
}

/**
 * 退出登录（通知服务端使 Token 失效）
 */
export const logout = () => {
  return post('/logout', {}, { loading: false })
}
