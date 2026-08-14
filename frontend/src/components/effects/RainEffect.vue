<script setup>
import { useSeasonEffect } from '@/composables/useSeasonEffect.js'
import { randomBetween, useEffectParticles } from './useEffectParticles.js'

const { densityTier, densityScale } = useSeasonEffect()
const { particles } = useEffectParticles({
  type: 'rain',
  counts: { desktop: 48, tablet: 34, mobile: 22 },
  densityTier,
  densityScale,
  createParticle: () => {
    const duration = randomBetween(.85, 1.55)
    return {
      style: {
        left: `${randomBetween(-4, 104).toFixed(2)}vw`,
        height: `${randomBetween(34, 78).toFixed(1)}px`,
        animationDuration: `${duration.toFixed(2)}s`,
        animationDelay: `${(-randomBetween(0, duration)).toFixed(2)}s`,
        '--season-alpha': randomBetween(.12, .34).toFixed(2),
        '--season-slant': `${randomBetween(32, 82).toFixed(0)}px`
      }
    }
  }
})
</script>

<template>
  <div class="season-particle-field rain-field" aria-hidden="true">
    <span
      v-for="particle in particles"
      :key="particle.id"
      class="rain-particle"
      :style="particle.style"
    ></span>
  </div>
</template>

<style scoped>
.rain-particle {
  position: absolute;
  top: -16vh;
  display: block;
  width: 1px;
  border-radius: 999px;
  background: linear-gradient(to bottom, transparent, rgba(194, 218, 237, .7));
  opacity: 0;
  filter: drop-shadow(0 0 1px rgba(173, 207, 233, .14));
  animation: season-rain-fall linear infinite;
  will-change: transform, opacity;
}

@keyframes season-rain-fall {
  0% { opacity: 0; transform: translate3d(0, -18vh, 0) rotate(-7deg); }
  12%, 82% { opacity: var(--season-alpha); }
  100% { opacity: 0; transform: translate3d(var(--season-slant), 126vh, 0) rotate(-7deg); }
}
</style>
