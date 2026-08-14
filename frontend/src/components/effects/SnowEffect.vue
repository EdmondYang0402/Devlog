<script setup>
import { useSeasonEffect } from '@/composables/useSeasonEffect.js'
import { randomBetween, randomItem, useEffectParticles } from './useEffectParticles.js'

const { densityTier, densityScale } = useSeasonEffect()
const colors = ['rgba(255,255,255,.9)', 'rgba(226,241,255,.86)', 'rgba(205,228,250,.8)']

const { particles } = useEffectParticles({
  type: 'snow',
  counts: { desktop: 36, tablet: 27, mobile: 18 },
  densityTier,
  densityScale,
  createParticle: index => {
    const size = randomBetween(3, 10)
    const duration = randomBetween(13, 27)
    return {
      flake: index % 7 === 0,
      style: {
        left: `${randomBetween(0, 100).toFixed(2)}vw`,
        width: `${size.toFixed(1)}px`,
        height: `${size.toFixed(1)}px`,
        animationDuration: `${duration.toFixed(2)}s`,
        animationDelay: `${(-randomBetween(0, duration)).toFixed(2)}s`,
        '--season-color': randomItem(colors),
        '--season-alpha': randomBetween(.3, .74).toFixed(2),
        '--season-drift': `${randomBetween(-90, 90).toFixed(0)}px`,
        '--season-sway': `${randomBetween(18, 60).toFixed(0)}px`
      }
    }
  }
})
</script>

<template>
  <div class="season-particle-field snow-field" aria-hidden="true">
    <span v-for="particle in particles" :key="particle.id" class="snow-particle" :class="{ 'is-flake': particle.flake }" :style="particle.style"></span>
  </div>
</template>

<style scoped>
.snow-particle{position:absolute;top:-10vh;display:block;border-radius:50%;background:var(--season-color);box-shadow:0 0 6px color-mix(in srgb,var(--season-color) 55%,transparent);opacity:0;animation:season-snow-fall linear infinite;will-change:transform,opacity}.snow-particle.is-flake{border-radius:2px;clip-path:polygon(42% 0,58% 0,58% 38%,100% 24%,100% 42%,66% 50%,100% 58%,100% 76%,58% 62%,58% 100%,42% 100%,42% 62%,0 76%,0 58%,34% 50%,0 42%,0 24%,42% 38%)}
@keyframes season-snow-fall{0%{opacity:0;transform:translate3d(0,-8vh,0) rotate(0)}15%{opacity:var(--season-alpha)}48%{transform:translate3d(var(--season-sway),50vh,0) rotate(120deg)}78%{opacity:var(--season-alpha);transform:translate3d(calc(var(--season-drift) - var(--season-sway)),84vh,0) rotate(235deg)}100%{opacity:0;transform:translate3d(var(--season-drift),114vh,0) rotate(340deg)}}
</style>
