<template>
  <el-config-provider :locale="elementPlusLocale">
    <div v-if="isAdminRoute" id="root" class="app-root">
      <router-view />
    </div>
    <FrontLayout v-else><router-view /></FrontLayout>
  </el-config-provider>
</template>

<script setup>
import { computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import FrontLayout from './layouts/FrontLayout.vue'
import { elementPlusLocale } from './locales/index.js'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
const { locale, t } = useI18n()
const defaultDescription = document.querySelector('meta[name="description"]')?.content || ''
const updateTitle = () => {
  const pageTitle = route.meta.titleKey
    ? t(route.meta.titleKey)
    : route.meta.title
  document.title = pageTitle
    ? `${pageTitle} - Hathaway's Blog`
    : "Hathaway's Blog"

  // 路由切换与语言切换时同步轻量 SEO 描述，不额外引入 SEO 依赖。
  const description = route.meta.descriptionKey
    ? t(route.meta.descriptionKey)
    : defaultDescription
  const descriptionMeta = document.querySelector('meta[name="description"]')
  if (descriptionMeta) descriptionMeta.setAttribute('content', description)
}
watch([() => route.fullPath, locale], updateTitle, { immediate: true })

const COLORS  = ['#7F77DD','#D4537E','#AFA9EC','#1D9E75','#F0997B','#534AB7']

function spawnBurst(x, y) {
  const w = document.createElement('div')
  w.className = 'burst-wrap'
  w.style.cssText = `left:${x}px;top:${y}px`
  document.body.appendChild(w)
  for (let i = 0; i < 10; i++) {
    const d = document.createElement('div')
    d.className = 'burst-dot'
    const angle = (i / 10) * Math.PI * 2
    const dist  = 28 + Math.random() * 30
    d.style.setProperty('--tx', Math.cos(angle) * dist + 'px')
    d.style.setProperty('--ty', Math.sin(angle) * dist + 'px')
    d.style.background = COLORS[Math.floor(Math.random() * COLORS.length)]
    d.style.animationDuration = (0.4 + Math.random() * 0.15) + 's'
    w.appendChild(d)
  }
  setTimeout(() => w.remove(), 700)
}

// 首页已经由全屏背景和 Bento 网格建立视觉重点，不再叠加花瓣与点击粒子。
const onClick = e => {
  if (!isAdminRoute.value && route.name !== 'Home') spawnBurst(e.clientX, e.clientY)
}

onMounted(() => {
  document.addEventListener('click', onClick)
})
onUnmounted(() => {
  document.removeEventListener('click', onClick)
})
</script>

<style scoped>
#root { min-height: 100vh; display: flex; flex-direction: column; }
</style>
