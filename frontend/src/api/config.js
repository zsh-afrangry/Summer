import axios from 'axios'

// API基础配置
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 打印请求信息（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log('🚀 API Request:', config.method?.toUpperCase(), config.url, config.data)
    }
    
    return config
  },
  error => {
    console.error('❌ Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    // 打印响应信息（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log('✅ API Response:', response.config.url, response.data)
    }
    
    // 统一处理响应格式
    const { code, message, data } = response.data
    
    if (code === 200) {
      return { success: true, data, message }
    } else {
      // 业务错误
      console.warn('⚠️ Business Error:', message)
      return { success: false, error: message, code }
    }
  },
  error => {
    // 网络错误或HTTP错误
    console.error('❌ Response Error:', error)
    
    let errorMessage = '网络请求失败'
    
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response
      switch (status) {
        case 401:
          errorMessage = '未授权，请重新登录'
          // 可以在这里清除token并跳转到登录页
          localStorage.removeItem('token')
          break
        case 403:
          errorMessage = '禁止访问'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = data?.message || `请求失败 (${status})`
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      errorMessage = '网络连接失败，请检查网络'
    } else {
      // 其他错误
      errorMessage = error.message || '请求配置错误'
    }
    
    return { success: false, error: errorMessage }
  }
)

// 通用API请求方法
export const request = {
  get: (url, params) => apiClient.get(url, { params }),
  post: (url, data) => apiClient.post(url, data),
  put: (url, data) => apiClient.put(url, data),
  delete: (url) => apiClient.delete(url),
  patch: (url, data) => apiClient.patch(url, data)
}

// 导出配置
export { API_BASE_URL, apiClient }
export default apiClient 