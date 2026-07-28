<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import SakuraPetalLayer from '@/components/SakuraPetalLayer.vue'
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
    <div class="front-layout__content"><slot /></div>
    <FooterBar />
  </div>
</template>

<style scoped>
.front-layout{--article-card-bg:var(--glass-bg);--article-card-border:var(--glass-border);position:relative;z-index:0;display:flex;min-height:100vh;flex-direction:column;isolation:isolate}.front-layout__content{display:flex;min-height:calc(100vh - var(--nav-h));flex:1;flex-direction:column}
</style>
