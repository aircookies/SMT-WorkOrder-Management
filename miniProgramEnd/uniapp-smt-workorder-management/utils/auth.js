/**
 * Token 与用户信息管理工具
 * 使用 uni 的本地存储 API 持久化登录状态
 */

const TOKEN_KEY = 'JWT_TOKEN'
const USER_KEY = 'USER_INFO'

// ========== Token 管理 ==========

export const getToken = () => {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export const setToken = (token) => {
  uni.setStorageSync(TOKEN_KEY, token)
}

export const removeToken = () => {
  uni.removeStorageSync(TOKEN_KEY)
}

// ========== 用户信息管理 ==========

export const getUserInfo = () => {
  const info = uni.getStorageSync(USER_KEY)
  return info ? (typeof info === 'string' ? JSON.parse(info) : info) : null
}

export const setUserInfo = (info) => {
  uni.setStorageSync(USER_KEY, JSON.stringify(info))
}

export const removeUserInfo = () => {
  uni.removeStorageSync(USER_KEY)
}

// ========== 登录状态检查 ==========

export const isLoggedIn = () => {
  const token = getToken()
  return !!token && token.length > 0
}

// ========== 清除所有登录信息 ==========

export const clearAuth = () => {
  removeToken()
  removeUserInfo()
}
