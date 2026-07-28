import { computed, ref } from 'vue'
import { getPublicBackgrounds } from '@/api/siteBackground.js'

// 本地图仅用于接口失败、空数据或远程图片全部失效时兜底，不是正常业务数据源。
export const DEFAULT_SITE_BACKGROUND = Object.freeze({
  id: 'local-default',
  imageUrl: '/images/hero.jpg',
  title: ''
})

const backgrounds = ref([])
const loading = ref(false)
const loaded = ref(false)
const error = ref(null)
const currentIndex = ref(0)
const failedUrls = ref(new Set())

const normalizeBackgrounds = value => {
  if (!Array.isArray(value)) return []
  return value.map((item, index) => ({
    id: item?.id ?? `background-${index}`,
    imageUrl: typeof item?.imageUrl === 'string' ? item.imageUrl.trim() : '',
    title: typeof item?.title === 'string' ? item.title : ''
  })).filter(item => item.imageUrl)
}

const availableBackgrounds = computed(() => {
  const available = backgrounds.value.filter(item => !failedUrls.value.has(item.imageUrl))
  return available.length ? available : [DEFAULT_SITE_BACKGROUND]
})

const loadBackgrounds = async ({ force = false } = {}) => {
  // loaded 是全站单例标记：路由、主题和语言切换都不会因此重复请求公开接口。
  if ((loaded.value || loading.value) && !force) return availableBackgrounds.value
  loading.value = true
  error.value = null
  try {
    const result = await getPublicBackgrounds()
    const payload = result?.data ?? result
    backgrounds.value = normalizeBackgrounds(payload)
    failedUrls.value = new Set()
    if (currentIndex.value >= availableBackgrounds.value.length) currentIndex.value = 0
  } catch (requestError) {
    error.value = requestError
    backgrounds.value = []
    currentIndex.value = 0
  } finally {
    // 即使失败也标记已加载，避免每次前台路由切换反复冲击故障接口。
    loaded.value = true
    loading.value = false
  }
  return availableBackgrounds.value
}

const nextBackground = () => {
  const count = availableBackgrounds.value.length
  currentIndex.value = count > 1 ? (currentIndex.value + 1) % count : 0
}

const markBackgroundFailed = imageUrl => {
  if (!imageUrl || imageUrl === DEFAULT_SITE_BACKGROUND.imageUrl) return
  const nextFailedUrls = new Set(failedUrls.value)
  nextFailedUrls.add(imageUrl)
  failedUrls.value = nextFailedUrls
  if (currentIndex.value >= availableBackgrounds.value.length) currentIndex.value = 0
}

const resetBackgrounds = () => {
  backgrounds.value = []
  failedUrls.value = new Set()
  currentIndex.value = 0
  error.value = null
  loaded.value = false
}

export const useSiteBackgrounds = () => ({
  backgrounds,
  availableBackgrounds,
  loading,
  loaded,
  error,
  currentIndex,
  loadBackgrounds,
  nextBackground,
  markBackgroundFailed,
  resetBackgrounds
})
