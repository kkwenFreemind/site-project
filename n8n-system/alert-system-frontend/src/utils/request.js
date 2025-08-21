import axios from 'axios'
import { ElMessage } from 'element-plus'

// 創建axios實例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 請求攔截器
api.interceptors.request.use(
  (config) => {
    // 動態獲取 token 以避免循環依賴
    const token = localStorage.getItem('token')
    
    console.log('Request interceptor - token:', token)
    console.log('Request interceptor - URL:', config.url)
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('Request interceptor - Authorization header set:', config.headers.Authorization)
    } else {
      console.log('Request interceptor - No token found')
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 響應攔截器
api.interceptors.response.use(
  (response) => {
    const { data } = response
    
    console.log('Response interceptor - Raw response:', response)
    console.log('Response interceptor - Data:', data)
    
    // 檢查業務狀態碼
    if (data.code !== 200) {
      ElMessage.error(data.message || '請求失敗')
      return Promise.reject(new Error(data.message || '請求失敗'))
    }
    
    console.log('Response interceptor - Returning data.data:', data.data)
    return data.data
  },
  async (error) => {
    if (error.response) {
      const { status } = error.response
      
      switch (status) {
        case 401:
          ElMessage.error('登入已過期，請重新登入')
          // 清除本地存儲的 token
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          // 動態導入以避免循環依賴
          const { default: router } = await import('@/router')
          router.push('/login')
          break
        case 403:
          ElMessage.error('權限不足')
          break
        case 404:
          ElMessage.error('請求的資源不存在')
          break
        case 500:
          ElMessage.error('伺服器內部錯誤')
          break
        default:
          ElMessage.error(error.response.data?.message || '網路錯誤')
      }
    } else {
      ElMessage.error('網路連接異常')
    }
    
    return Promise.reject(error)
  }
)

export default api
