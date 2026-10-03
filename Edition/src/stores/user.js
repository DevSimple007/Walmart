import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken, setUser, getUser, removeUser } from '@/utils/auth'
import { login as loginApi, getUserInfo } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref(getUser())

  async function login(credentials) {
    const res = await loginApi(credentials)
    token.value = res.data.token || res.data
    setToken(token.value)
    await fetchUserInfo()
    return res
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      setUser(res.data)
    } catch (e) {
      console.error('Failed to fetch user info', e)
    }
  }

  function logout() {
    token.value = null
    userInfo.value = null
    removeToken()
    removeUser()
  }

  return {
    token,
    userInfo,
    login,
    fetchUserInfo,
    logout
  }
})
