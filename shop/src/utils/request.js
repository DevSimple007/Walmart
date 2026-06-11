import axios from 'axios'
import { getToken, removeToken } from './auth'
import { showToast } from 'vant'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200 && res.code !== 0) {
      showToast(res.message || '请求失败')
      if (res.code === 401) {
        removeToken()
        localStorage.removeItem('user_info')
        const redirect = encodeURIComponent(window.location.pathname + window.location.search)
        window.location.href = '/login?redirect=' + redirect
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const status = error.response?.status
    if (status === 401) {
      removeToken()
      localStorage.removeItem('user_info')
      showToast('登录已过期，请重新登录')
      const redirect = encodeURIComponent(window.location.pathname + window.location.search)
      window.location.href = '/login?redirect=' + redirect
      return Promise.reject(error)
    }
    showToast(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
