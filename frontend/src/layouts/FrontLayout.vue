<script setup>
import { computed } from 'vue'
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
</script>

<template>
  <div class="front-layout" :class="{ 'front-layout--reading': readingMode }">
    <BackgroundCarousel />
    <BackgroundOverlay :reading="readingMode" />
    <SakuraPetalLayer v-if="showPetals" />
    <NavBar />
    <div class="front-layout__content">
      <router-view v-slot="{ Component, route: currentRoute }">
        <Transition name="front-page" mode="out-in">
          <component :is="Component" :key="currentRoute.fullPath" />
        </Transition>
      </router-view>
    </div>
    <BackToTop />
    <FooterBar />
  </div>
</template>

<style scoped>
.front-layout{--article-card-bg:var(--glass-bg);--article-card-border:var(--glass-border);position:relative;z-index:0;display:flex;min-height:100vh;flex-direction:column;isolation:isolate}.front-layout__content{display:flex;min-height:calc(100vh - var(--nav-h));flex:1;flex-direction:column}

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

  :global(.front-page-enter-from),
  :global(.front-page-leave-to) {
    transform: none;
  }
}
</style>
