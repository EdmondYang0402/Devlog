<script setup>
import { computed, provide, readonly, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import SakuraPetalLayer from '@/components/SakuraPetalLayer.vue'
import BackToTop from '@/components/common/BackToTop.vue'
import BackgroundCarousel from '@/components/background/BackgroundCarousel.vue'
import BackgroundOverlay from '@/components/background/BackgroundOverlay.vue'

const route = useRoute()
const readingMode = computed(() => route.name === 'PostDetail' || route.name === 'MediaDetail')
const showPetals = computed(() => route.name !== 'Home' && !readingMode.value)
const backgroundOnly = ref(false)
const curtainProgress = ref(0)
const curtainDragging = ref(false)
const isHome = computed(() => route.name === 'Home')
const isBackgroundOnly = computed(() => isHome.value && backgroundOnly.value)
const showHomeForeground = computed(() => !isBackgroundOnly.value || curtainDragging.value)
const curtainStyle = computed(() => ({ '--curtain-progress': curtainProgress.value }))

const enterBackgroundOnly = () => {
  if (!isHome.value) return
  backgroundOnly.value = true
  curtainProgress.value = 1
}

const exitBackgroundOnly = () => {
  backgroundOnly.value = false
  curtainProgress.value = 0
}

provide('homeImmersiveMode', {
  backgroundOnly: readonly(backgroundOnly),
  curtainProgress: readonly(curtainProgress),
  curtainDragging: readonly(curtainDragging),
  enter: enterBackgroundOnly,
  exit: exitBackgroundOnly,
  setCurtainProgress: value => { curtainProgress.value = Math.min(1, Math.max(0, Number(value) || 0)) },
  setCurtainDragging: value => { curtainDragging.value = Boolean(value) }
})

watch(isHome, homeRouteActive => {
  if (homeRouteActive) return
  curtainDragging.value = false
  exitBackgroundOnly()
})
</script>

<template>
  <div
    class="front-layout"
    :class="{
      'front-layout--reading': readingMode,
      'front-layout--background-only': isBackgroundOnly
    }"
  >
    <BackgroundCarousel />
    <BackgroundOverlay
      v-if="showHomeForeground"
      class="curtain-overlay"
      :style="curtainStyle"
      :reading="readingMode"
    />
    <SakuraPetalLayer v-if="showPetals" />
    <NavBar
      v-if="showHomeForeground"
      class="curtain-reactive"
      :style="curtainStyle"
      :inert="curtainDragging || isBackgroundOnly"
    />
    <div class="front-layout__content">
      <router-view v-slot="{ Component, route: currentRoute }">
        <Transition name="front-page" mode="out-in">
          <component :is="Component" :key="currentRoute.fullPath" />
        </Transition>
      </router-view>
    </div>
    <BackToTop v-if="showHomeForeground" class="curtain-reactive" :style="curtainStyle" />
    <FooterBar v-if="showHomeForeground" class="curtain-reactive" :style="curtainStyle" />
  </div>
</template>

<style scoped>
.front-layout{--article-card-bg:var(--glass-bg);--article-card-border:var(--glass-border);position:relative;z-index:0;display:flex;min-height:100vh;flex-direction:column;isolation:isolate}.front-layout__content{display:flex;min-height:calc(100vh - var(--nav-h));flex:1;flex-direction:column}
.front-layout--background-only .front-layout__content{min-height:100vh}

:global(.curtain-reactive){opacity:calc(1 - var(--curtain-progress));transition:opacity 320ms cubic-bezier(.2,.75,.25,1);will-change:opacity}
:global(.curtain-overlay){opacity:calc(1 - var(--curtain-progress));transition:opacity 320ms cubic-bezier(.2,.75,.25,1);will-change:opacity}

:global(.front-page-enter-active),
:global(.front-page-leave-active) {
  transition: opacity 320ms ease, transform 320ms ease;
}

:global(.front-page-enter-from) {
  opacity: 0;
  transform: translateY(6px);
}

:global(.front-page-leave-to) {
  opacity: 0;
  transform: translateY(-3px);
}

:global(.front-page-enter-to),
:global(.front-page-leave-from) {
  opacity: 1;
  transform: translateY(0);
}

@media (prefers-reduced-motion: reduce) {
  :global(.front-page-enter-active),
  :global(.front-page-leave-active) {
    transition: none;
  }

  :global(.curtain-reactive),
  :global(.curtain-overlay) { transition-duration:1ms; }

  :global(.front-page-enter-from),
  :global(.front-page-leave-to) {
    transform: none;
  }
}
</style>
