<script setup>
import { useSeasonEffect } from '@/composables/useSeasonEffect.js'
import { randomBetween, randomItem, useEffectParticles } from './useEffectParticles.js'

const { densityTier, densityScale } = useSeasonEffect()
const colors = ['#ffd7e2', '#f9bfd1', '#fff0f4', '#edabc2']

const { particles } = useEffectParticles({
  type: 'sakura',
  counts: { desktop: 30, tablet: 22, mobile: 14 },
  densityTier,
  densityScale,
  createParticle: () => {
    const size = randomBetween(7, 18)
    const duration = randomBetween(11, 22)
    return {
      style: {
        left: `${randomBetween(-4, 102).toFixed(2)}vw`,
        width: `${size.toFixed(1)}px`,
        height: `${(size * randomBetween(1.25, 1.55)).toFixed(1)}px`,
        animationDuration: `${duration.toFixed(2)}s`,
        animationDelay: `${(-randomBetween(0, duration)).toFixed(2)}s`,
        '--season-color': randomItem(colors),
        '--season-alpha': randomBetween(.34, .7).toFixed(2),
        '--season-drift': `${randomBetween(-150, 150).toFixed(0)}px`,
        '--season-sway': `${randomBetween(28, 90).toFixed(0)}px`,
        '--season-spin': `${randomBetween(420, 900).toFixed(0)}deg`,
        '--season-flutter': `${randomBetween(2.2, 4.8).toFixed(2)}s`
      }
    }
  }
})
</script>

<template>
  <div class="season-particle-field sakura-field" aria-hidden="true">
    <span v-for="particle in particles" :key="particle.id" class="sakura-particle" :style="particle.style">
      <i></i>
    </span>
  </div>
</template>

<style scoped>
.sakura-particle{position:absolute;top:-14vh;display:block;opacity:0;animation:season-sakura-fall linear infinite;will-change:transform,opacity}.sakura-particle i{display:block;width:100%;height:100%;border-radius:75% 20% 72% 28%;background:linear-gradient(145deg,rgba(255,255,255,.72),var(--season-color) 45%,color-mix(in srgb,var(--season-color) 78%,#d989a8));box-shadow:0 1px 2px rgba(135,67,94,.1);transform-origin:55% 45%;animation:season-sakura-flutter var(--season-flutter) ease-in-out infinite alternate}
@keyframes season-sakura-fall{0%{opacity:0;transform:translate3d(0,-8vh,0) rotate(0)}12%{opacity:var(--season-alpha)}45%{transform:translate3d(var(--season-sway),48vh,0) rotate(260deg)}75%{opacity:var(--season-alpha);transform:translate3d(calc(var(--season-drift) - var(--season-sway)),83vh,0) rotate(470deg)}100%{opacity:0;transform:translate3d(var(--season-drift),118vh,0) rotate(var(--season-spin))}}
@keyframes season-sakura-flutter{0%{transform:rotateX(12deg) rotateY(-18deg) rotateZ(-8deg) scaleX(.9)}50%{transform:rotateX(54deg) rotateY(24deg) rotateZ(8deg) scaleX(.38)}100%{transform:rotateX(-16deg) rotateY(44deg) rotateZ(-4deg) scaleX(-.78)}}
</style>
