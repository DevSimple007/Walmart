import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { customerLogin, customerRegister, getCustomerInfo } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  let parsed = null
  try { parsed = JSON.parse(localStorage.getItem('user_info') || 'null') } catch {}
  const userInfo = ref(parsed)

  async function login(phone, password) {
    const res = await customerLogin({ phone, password })
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    setToken(res.data.token)
    localStorage.setItem('user_info', JSON.stringify(res.data.userInfo))
    return res
  }

  async function register(phone, password, memberName) {
    const res = await customerRegister({ phone, password, memberName })
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    setToken(res.data.token)
    localStorage.setItem('user_info', JSON.stringify(res.data.userInfo))
    return res
  }

  async function fetchUserInfo() {
    try {
      const res = await getCustomerInfo()
      userInfo.value = res.data
      localStorage.setItem('user_info', JSON.stringify(res.data))
    } catch (e) { console.error('获取用户信息失败', e) }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    removeToken()
    localStorage.removeItem('user_info')
  }

  return { token, userInfo, login, register, fetchUserInfo, logout }
})
