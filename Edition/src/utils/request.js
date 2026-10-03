import axios from 'axios'
import { getToken, removeToken } from './auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200 || res.code === 0) {
      return res
    }
    ElMessage.error(res.message || 'Request failed')
    return Promise.reject(new Error(res.message || 'Request failed'))
  },
  error => {
    if (error.response) {
      const { status } = error.response
      if (status === 401) {
        removeToken()
        router.push('/login')
        ElMessage.error('Please sign in again')
      } else if (status === 403) {
        ElMessage.error('Access denied')
      } else if (status === 500) {
        ElMessage.error('Server error')
      } else {
        ElMessage.error(error.message || 'Request failed')
      }
    } else {
      ElMessage.error('Network error')
    }
    return Promise.reject(error)
  }
)

export default request
