import { computed } from 'vue'
import { createI18n } from 'vue-i18n'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import ja from 'element-plus/es/locale/lang/ja'
import en from 'element-plus/es/locale/lang/en'
import zhCNMessages from './zh-CN.js'
import jaJPMessages from './ja-JP.js'
import enUSMessages from './en-US.js'

export const LOCALE_STORAGE_KEY = 'devlog-locale'
export const SUPPORTED_LOCALES = [
  { value: 'zh-CN', label: '中文', shortLabel: '中文' },
  { value: 'ja-JP', label: '日本語', shortLabel: '日本語' },
  { value: 'en-US', label: 'English', shortLabel: 'EN' }
]

const supportedValues = new Set(SUPPORTED_LOCALES.map(item => item.value))
const normalizeLocale = value => {
  const locale = String(value || '').toLowerCase()
  if (locale.startsWith('ja')) return 'ja-JP'
  if (locale.startsWith('en')) return 'en-US'
  if (locale.startsWith('zh')) return 'zh-CN'
  return 'zh-CN'
}

const getInitialLocale = () => {
  const saved = localStorage.getItem(LOCALE_STORAGE_KEY)
  if (saved !== null) return supportedValues.has(saved) ? saved : 'zh-CN'
  return normalizeLocale(navigator.language)
}

const initialLocale = getInitialLocale()

export const i18n = createI18n({
  legacy: false,
  locale: initialLocale,
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCNMessages,
    'ja-JP': jaJPMessages,
    'en-US': enUSMessages
  }
})

const elementPlusLocales = { 'zh-CN': zhCn, 'ja-JP': ja, 'en-US': en }
export const elementPlusLocale = computed(
  () => elementPlusLocales[i18n.global.locale.value] || zhCn
)

export const setLocale = value => {
  const nextLocale = supportedValues.has(value) ? value : 'zh-CN'
  i18n.global.locale.value = nextLocale
  localStorage.setItem(LOCALE_STORAGE_KEY, nextLocale)
  document.documentElement.lang = nextLocale
}

document.documentElement.lang = initialLocale
