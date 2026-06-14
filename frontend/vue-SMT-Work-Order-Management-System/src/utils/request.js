import axios from "axios";
import router from "@/router";
import { ElMessage } from "element-plus";

// 创建axios实例
const request = axios.create({
  baseURL: "/api",
  timeout: 30000, // 请求超时时间
  withCredentials: true, // 允许携带cookie
});

// 添加请求拦截器
request.interceptors.request.use(function (config) {
  return config;
}, function (error) {
  // 对请求错误做些什么
  console.error('请求错误:', error)
  ElMessage.error('网络请求错误：' + error)
  return Promise.reject(error);
});

// 添加响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的状态码不是 200，则判断为错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')

      // 401: 未授权，跳转到登录页
      if (res.code === 401) {
        localStorage.clear()
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  error => {
    console.error('响应错误:', error)

    let message = '网络错误，请检查网络连接'

    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = error.response.data.message || '请求参数错误'
          break
        case 401:
          message = error.response.data.message || '未授权，请重新登录'
          localStorage.clear()
          setTimeout(() => {
            router.push('/login')
          }, 1000)
          break
        case 403:
          console.error('权限不足', error)
          message = error.response.data.message || '权限不足，拒绝访问'
          break
        case 404:
          message = error.response.data.message || '请求资源不存在'
          break
        case 500:
          message = error.response.data.message || '服务器异常'
          break
        case 502:
          message = error.response.data.message || '网关错误'
          break
        case 503:
          message = error.response.data.message || '服务不可用'
          break
        default:
          message = `连接错误 ${error.response.status}`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request;