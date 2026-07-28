import { computed, ref } from 'vue'

const STORAGE_KEY = 'devlog-theme'
const theme = ref('light')
let initialized = false

const applyTheme = value => {
  const nextTheme = value === 'dark' ? 'dark' : 'light'
  theme.value = nextTheme
  document.documentElement.dataset.theme = nextTheme
  document.documentElement.classList.toggle('dark', nextTheme === 'dark')
}

export const initTheme = () => {
  if (initialized) return theme.value
  const savedTheme = localStorage.getItem(STORAGE_KEY)
  const systemPrefersDark = window.matchMedia?.('(prefers-color-scheme: dark)').matches
  applyTheme(savedTheme === 'light' || savedTheme === 'dark'
    ? savedTheme
    : systemPrefersDark ? 'dark' : 'light')
  initialized = true
  return theme.value
}

export function useTheme() {
  const setTheme = value => {
    applyTheme(value)
    localStorage.setItem(STORAGE_KEY, theme.value)
  }
  const toggleTheme = () => setTheme(theme.value === 'dark' ? 'light' : 'dark')
  return {
    theme,
    isDark: computed(() => theme.value === 'dark'),
    setTheme,
    toggleTheme,
    initTheme
  }
}
