import { computed, onMounted, onUnmounted, ref } from 'vue'

export const SEASON_EFFECT_STORAGE_KEY = 'devlog-season-effect'
export const SEASON_EFFECT_TYPES = Object.freeze(['none', 'sakura', 'rain', 'leaf', 'snow'])
export const DEFAULT_SEASON_EFFECT = 'none'

const selectedEffect = ref(DEFAULT_SEASON_EFFECT)
const reducedMotion = ref(false)
const viewportWidth = ref(typeof window === 'undefined' ? 1280 : window.innerWidth)
const lowPerformanceDevice = ref(false)

let consumers = 0
let mediaQuery = null

const isValidEffect = value => SEASON_EFFECT_TYPES.includes(value)

const readStoredEffect = () => {
  if (typeof window === 'undefined') return DEFAULT_SEASON_EFFECT
  try {
    const stored = window.localStorage.getItem(SEASON_EFFECT_STORAGE_KEY)
    return isValidEffect(stored) ? stored : DEFAULT_SEASON_EFFECT
  } catch {
    return DEFAULT_SEASON_EFFECT
  }
}

selectedEffect.value = readStoredEffect()

const updateEnvironment = () => {
  viewportWidth.value = window.innerWidth
  const memory = Number(navigator.deviceMemory || 0)
  const cores = Number(navigator.hardwareConcurrency || 0)
  lowPerformanceDevice.value = (memory > 0 && memory <= 4) || (cores > 0 && cores <= 4)
}

const updateMotionPreference = event => {
  reducedMotion.value = event.matches
}

const handleStorage = event => {
  if (event.key !== SEASON_EFFECT_STORAGE_KEY) return
  selectedEffect.value = isValidEffect(event.newValue) ? event.newValue : DEFAULT_SEASON_EFFECT
}

const acquireEnvironment = () => {
  consumers += 1
  if (consumers !== 1) return

  updateEnvironment()
  mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)')
  reducedMotion.value = mediaQuery.matches
  mediaQuery.addEventListener?.('change', updateMotionPreference)
  window.addEventListener('resize', updateEnvironment, { passive: true })
  window.addEventListener('storage', handleStorage)
}

const releaseEnvironment = () => {
  consumers = Math.max(0, consumers - 1)
  if (consumers !== 0) return

  mediaQuery?.removeEventListener?.('change', updateMotionPreference)
  window.removeEventListener('resize', updateEnvironment)
  window.removeEventListener('storage', handleStorage)
  mediaQuery = null
}

export const useSeasonEffect = () => {
  const setEffect = effect => {
    if (!isValidEffect(effect)) return
    selectedEffect.value = effect
    try {
      window.localStorage.setItem(SEASON_EFFECT_STORAGE_KEY, effect)
    } catch {
      // The visual preference still works for the current session when storage is unavailable.
    }
  }

  const densityTier = computed(() => {
    if (viewportWidth.value < 680) return 'mobile'
    if (viewportWidth.value < 1100) return 'tablet'
    return 'desktop'
  })
  const densityScale = computed(() => lowPerformanceDevice.value ? 0.7 : 1)
  const effectiveEffect = computed(() => reducedMotion.value ? 'none' : selectedEffect.value)

  onMounted(acquireEnvironment)
  onUnmounted(releaseEnvironment)

  return {
    selectedEffect,
    effectiveEffect,
    reducedMotion,
    densityTier,
    densityScale,
    setEffect
  }
}
