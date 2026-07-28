<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useSiteBackgrounds } from '@/composables/useSiteBackgrounds.js'

const SLIDE_INTERVAL = 10000
const FADE_DURATION = 1200

const {
  availableBackgrounds,
  currentIndex,
  loadBackgrounds,
  markBackgroundFailed
} = useSiteBackgrounds()

// 两个固定图层循环复用，避免替换单个 img 时 background/src 瞬间跳变。
const layers = ref([
  { id: 0, url: '', active: false, leaving: false },
  { id: 1, url: '', active: false, leaving: false }
])
const activeLayer = ref(0)

let backgroundTimer = null
let fadeTimer = null
let mounted = false
let switching = false
let preparedForUrl = ''
let preparedBackground = null
let preparePromise = null

const clearBackgroundTimer = () => {
  if (backgroundTimer !== null) window.clearTimeout(backgroundTimer)
  backgroundTimer = null
}

const clearFadeTimer = () => {
  if (fadeTimer !== null) window.clearTimeout(fadeTimer)
  fadeTimer = null
}

const preloadImage = url => new Promise((resolve, reject) => {
  const image = new Image()
  image.onload = () => resolve(url)
  image.onerror = () => reject(new Error(`Unable to load background: ${url}`))
  image.src = url
})

const waitForPaint = async () => {
  await nextTick()
  await new Promise(resolve => window.requestAnimationFrame(resolve))
  await new Promise(resolve => window.requestAnimationFrame(resolve))
}

const syncCurrentIndex = url => {
  const index = availableBackgrounds.value.findIndex(item => item.imageUrl === url)
  currentIndex.value = index >= 0 ? index : 0
}

const scheduleNextBackground = () => {
  clearBackgroundTimer()
  if (
    !mounted ||
    document.hidden ||
    availableBackgrounds.value.length < 2
  ) return

  backgroundTimer = window.setTimeout(switchBackground, SLIDE_INTERVAL)
}

const getNextCandidates = currentUrl => {
  const backgrounds = [...availableBackgrounds.value]
  if (backgrounds.length < 2) return []

  const currentPosition = backgrounds.findIndex(item => item.imageUrl === currentUrl)
  const startPosition = currentPosition >= 0 ? currentPosition : 0
  const candidates = []

  for (let offset = 1; offset < backgrounds.length; offset += 1) {
    candidates.push(backgrounds[(startPosition + offset) % backgrounds.length])
  }
  return candidates
}

const loadFirstAvailable = async backgrounds => {
  for (const background of backgrounds) {
    try {
      await preloadImage(background.imageUrl)
      return background
    } catch (error) {
      console.warn('Background image preload failed:', background.imageUrl, error)
      markBackgroundFailed(background.imageUrl)
    }
  }
  return null
}

const prepareNextBackground = currentUrl => {
  if (!mounted) return Promise.resolve(null)
  if (preparedForUrl === currentUrl) {
    if (preparedBackground) return Promise.resolve(preparedBackground)
    if (preparePromise) return preparePromise
  }

  preparedForUrl = currentUrl
  preparedBackground = null
  const request = loadFirstAvailable(getNextCandidates(currentUrl))
    .then(background => {
      if (!mounted || preparedForUrl !== currentUrl) return background
      preparedBackground = background
      if (background) {
        const nextLayerIndex = activeLayer.value === 0 ? 1 : 0
        const nextLayer = layers.value[nextLayerIndex]
        if (!nextLayer.active && !nextLayer.leaving) {
          // 隐藏层只保留紧邻的下一张，浏览期间完成网络传输和图片解码。
          nextLayer.url = background.imageUrl
        }
      }
      return background
    })
    .finally(() => {
      if (preparedForUrl === currentUrl) preparePromise = null
    })

  preparePromise = request
  return request
}

async function switchBackground() {
  clearBackgroundTimer()
  if (switching || !mounted || document.hidden) return

  const previousLayerIndex = activeLayer.value
  const previousUrl = layers.value[previousLayerIndex].url
  const candidates = getNextCandidates(previousUrl)
  if (!candidates.length) return

  switching = true
  let nextBackground = null

  try {
    nextBackground = preparedForUrl === previousUrl && preparedBackground
      ? preparedBackground
      : await prepareNextBackground(previousUrl)
    if (!nextBackground || !mounted || document.hidden) return

    const nextLayerIndex = previousLayerIndex === 0 ? 1 : 0
    const nextLayer = layers.value[nextLayerIndex]
    const previousLayer = layers.value[previousLayerIndex]

    // 先用隐藏层承载已加载图片，留出一帧后再加 active，保证淡入与缩放从头触发。
    nextLayer.active = false
    nextLayer.leaving = false
    nextLayer.url = nextBackground.imageUrl
    await waitForPaint()
    if (!mounted || document.hidden) return

    nextLayer.active = true
    previousLayer.leaving = true
    activeLayer.value = nextLayerIndex
    syncCurrentIndex(nextBackground.imageUrl)
    preparedForUrl = ''
    preparedBackground = null
    preparePromise = null

    clearFadeTimer()
    fadeTimer = window.setTimeout(() => {
      previousLayer.active = false
      previousLayer.leaving = false
      previousLayer.url = ''
      fadeTimer = null
      void prepareNextBackground(nextBackground.imageUrl)
    }, FADE_DURATION)
  } finally {
    switching = false
    scheduleNextBackground()
  }
}

const showInitialBackground = async () => {
  switching = true
  try {
    const background = await loadFirstAvailable([...availableBackgrounds.value])
    if (!background || !mounted) return

    const firstLayer = layers.value[activeLayer.value]
    firstLayer.url = background.imageUrl
    firstLayer.active = false
    await waitForPaint()
    if (!mounted) return

    firstLayer.active = true
    syncCurrentIndex(background.imageUrl)
    void prepareNextBackground(background.imageUrl)
  } finally {
    switching = false
    scheduleNextBackground()
  }
}

const handleVisibilityChange = () => {
  if (document.hidden) {
    clearBackgroundTimer()
  } else {
    scheduleNextBackground()
  }
}

onMounted(async () => {
  mounted = true
  document.addEventListener('visibilitychange', handleVisibilityChange)
  await loadBackgrounds()
  if (mounted) await showInitialBackground()
})

onBeforeUnmount(() => {
  mounted = false
  clearBackgroundTimer()
  clearFadeTimer()
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<template>
  <div class="front-background" aria-hidden="true">
    <div
      v-for="layer in layers"
      :key="layer.id"
      class="front-background__slide"
      :class="{
        'is-active': layer.active,
        'is-leaving': layer.leaving
      }"
      :style="{ backgroundImage: layer.url ? `url(${layer.url})` : undefined }"
    />
  </div>
</template>

<style scoped>
.front-background {
  position: fixed;
  inset: 0;
  z-index: -3;
  overflow: hidden;
  background: linear-gradient(145deg, #d8d5ed, #8c91aa 48%, #5e627a);
}

.front-background__slide {
  position: absolute;
  inset: -2%;
  z-index: 0;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  opacity: 0;
  transform: scale(1);
  transition: opacity 1.2s ease-in-out;
  will-change: opacity, transform;
}

.front-background__slide.is-active {
  z-index: 2;
  opacity: 1;
  animation: background-zoom 12s ease-out forwards;
}

.front-background__slide.is-leaving {
  z-index: 1;
  opacity: 0;
}

@keyframes background-zoom {
  from { transform: scale(1); }
  to { transform: scale(1.05); }
}

@media (max-width: 767px) {
  .front-background__slide.is-active {
    animation-name: background-zoom-mobile;
  }

  @keyframes background-zoom-mobile {
    from { transform: scale(1); }
    to { transform: scale(1.04); }
  }
}

@media (prefers-reduced-motion: reduce) {
  .front-background__slide {
    transform: scale(1);
    transition: opacity .2s linear;
    animation: none !important;
  }
}
</style>
