/**
 * 网络请求封装
 * 基于 uni.request 封装，自动携带 JWT Token，统一错误处理
 */

import { getToken, clearAuth } from './auth'

// 后端服务地址 - 通过 Nginx 反向代理，HTTPS + /api 前缀
const BASE_URL = 'https://154.37.222.20/api'

/**
 * 通用请求方法
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求路径（不含 BASE_URL）
 * @param {string} [options.method='GET'] - 请求方法
 * @param {Object} [options.data] - 请求数据
 * @param {boolean} [options.loading=true] - 是否显示加载提示
 * @returns {Promise<Object>} 响应数据（已解包 Result 对象）
 */
const request = (options) => {
  const { url, method = 'GET', data, loading = true, silent = false } = options

  // 显示加载提示
  if (loading) {
    uni.showLoading({ title: '加载中...', mask: true })
  }

  // 构建请求头，自动携带 Token
  const header = {
    'Content-Type': 'application/json'
  }
  const token = getToken()
  if (token) {
    header['Authorization'] = `Bearer ${token}`
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header,
      timeout: 30000,

      success: (res) => {
        if (loading) uni.hideLoading()

        const { statusCode, data: resData } = res

        // HTTP 状态码非 2xx
        if (statusCode < 200 || statusCode >= 300) {
          if (statusCode === 401) {
            handleUnauthorized()
          }
          const errMsg = resData?.message || `请求失败 (${statusCode})`
          if (!silent) uni.showToast({ title: errMsg, icon: 'none', duration: 2000 })
          reject(new Error(errMsg))
          return
        }

        // 业务状态码校验
        if (resData.code !== 200) {
          // 401 - Token 过期或无效
          if (resData.code === 401) {
            handleUnauthorized()
            reject(new Error('登录已过期，请重新登录'))
            return
          }
          if (!silent) uni.showToast({ title: resData.message || '操作失败', icon: 'none', duration: 2000 })
          reject(new Error(resData.message || '操作失败'))
          return
        }

        resolve(resData)
      },

      fail: (err) => {
        if (loading) uni.hideLoading()
        uni.showToast({ title: '网络连接失败，请检查网络', icon: 'none', duration: 2000 })
        reject(new Error('网络连接失败'))
      }
    })
  })
}

/**
 * 处理 401 未授权 - 清除登录信息并跳转登录页
 */
const handleUnauthorized = () => {
  clearAuth()
  uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
  setTimeout(() => {
    uni.reLaunch({ url: '/pages/login/index' })
  }, 1500)
}

// ========== 快捷方法 ==========

export const get = (url, data, options = {}) => {
  return request({ url, method: 'GET', data, ...options })
}

export const post = (url, data, options = {}) => {
  return request({ url, method: 'POST', data, ...options })
}

export const put = (url, data, options = {}) => {
  return request({ url, method: 'PUT', data, ...options })
}

export const del = (url, data, options = {}) => {
  return request({ url, method: 'DELETE', data, ...options })
}

export default request
