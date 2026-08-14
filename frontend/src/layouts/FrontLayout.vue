<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import SeasonEffectLayer from '@/components/effects/SeasonEffectLayer.vue'
import BackToTop from '@/components/common/BackToTop.vue'
import FloatingMusicPlayer from '@/components/music/FloatingMusicPlayer.vue'
import GlobalAudio from '@/components/music/GlobalAudio.vue'
import BackgroundCarousel from '@/components/background/BackgroundCarousel.vue'
import BackgroundOverlay from '@/components/background/BackgroundOverlay.vue'

const route = useRoute()
const readingMode = computed(() => route.name === 'PostDetail' || route.name === 'MediaDetail')
</script>

<template>
  <div class="front-layout" :class="{ 'front-layout--reading': readingMode }">
    <BackgroundCarousel />
    <BackgroundOverlay :reading="readingMode" />
    <SeasonEffectLayer />
    <NavBar />
    <div class="front-layout__content">
      <router-view v-slot="{ Component, route: currentRoute }">
        <Transition name="front-page" mode="out-in">
          <component :is="Component" :key="currentRoute.fullPath" />
        </Transition>
      </router-view>
    </div>
    <FloatingMusicPlayer />
    <GlobalAudio />
    <BackToTop />
  </div>
</template>

<style scoped>
.front-layout{--front-nav-clearance:96px;--article-card-bg:var(--glass-bg);--article-card-border:var(--glass-border);position:relative;z-index:0;display:flex;min-height:100vh;flex-direction:column;isolation:isolate}.front-layout__content{box-sizing:border-box;display:flex;min-height:100vh;flex:1;flex-direction:column;padding-top:var(--front-nav-clearance)}

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

@media (max-width: 680px) {
  .front-layout { --front-nav-clearance:148px; }
}
</style>
