import { computed, ref } from 'vue'
import { userInfoService } from '@/api/user.js'

const token = ref(localStorage.getItem('token'))
const userInfo = ref(null)
let loadingPromise = null

const setToken = value => {
  token.value = value || null
  userInfo.value = null
  if (value) localStorage.setItem('token', value)
  else localStorage.removeItem('token')
}

const clearUser = () => {
  token.value = null
  userInfo.value = null
  loadingPromise = null
  localStorage.removeItem('token')
  localStorage.removeItem('username')
}

const updateCurrentUser = value => {
  userInfo.value = value ? { ...value } : null
  if (userInfo.value?.username) localStorage.setItem('username', userInfo.value.username)
}

const loadCurrentUser = async () => {
  token.value = localStorage.getItem('token')
  if (!token.value) {
    userInfo.value = null
    return null
  }
  if (userInfo.value) return userInfo.value
  if (loadingPromise) return loadingPromise

  loadingPromise = userInfoService()
    .then(result => {
      updateCurrentUser(result?.data || null)
      return userInfo.value
    })
    .catch(error => {
      if ([401, 403].includes(error?.response?.status)) clearUser()
      throw error
    })
    .finally(() => {
      loadingPromise = null
    })

  return loadingPromise
}

const isLoggedIn = computed(() => Boolean(token.value && userInfo.value))

export function useCurrentUser() {
  return { token, userInfo, isLoggedIn, setToken, clearUser, loadCurrentUser, updateCurrentUser }
}
