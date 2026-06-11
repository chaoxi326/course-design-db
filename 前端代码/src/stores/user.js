import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => user.value !== null)
  const isAdmin = computed(() => user.value?.eLevel === '管理员')
  const username = computed(() => user.value?.eName || '')

  function setUser(u) {
    user.value = u
    localStorage.setItem('user', JSON.stringify(u))
  }

  function logout() {
    user.value = null
    localStorage.removeItem('user')
  }

  return { user, isLoggedIn, isAdmin, username, setUser, logout }
})
