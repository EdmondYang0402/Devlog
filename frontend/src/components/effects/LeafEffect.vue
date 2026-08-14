<script setup>
import { useSeasonEffect } from '@/composables/useSeasonEffect.js'
import { randomBetween, randomItem, useEffectParticles } from './useEffectParticles.js'

const { densityTier, densityScale } = useSeasonEffect()
const colors = ['#c9933f', '#b77432', '#db8a43', '#9d6134', '#cf6f45']
const { particles } = useEffectParticles({
  type: 'leaf',
  counts: { desktop: 20, tablet: 15, mobile: 10 },
  densityTier,
  densityScale,
  createParticle: index => {
    const size = randomBetween(10, 23)
    const duration = randomBetween(13, 25)
    return {
      shape: index % 3,
      style: {
        left: `${randomBetween(-3, 102).toFixed(2)}vw`,
        width: `${size.toFixed(1)}px`,
        height: `${(size * randomBetween(.55, .82)).toFixed(1)}px`,
        animationDuration: `${duration.toFixed(2)}s`,
        animationDelay: `${(-randomBetween(0, duration)).toFixed(2)}s`,
        '--season-color': randomItem(colors),
        '--season-alpha': randomBetween(.34, .68).toFixed(2),
        '--season-drift': `${randomBetween(-170, 170).toFixed(0)}px`,
        '--season-sway': `${randomBetween(38, 105).toFixed(0)}px`,
        '--season-spin': `${randomBetween(640, 1080).toFixed(0)}deg`,
        '--season-flutter': `${randomBetween(2.8, 5.6).toFixed(2)}s`
      }
    }
  }
})
</script>

<template>
  <div class="season-particle-field leaf-field" aria-hidden="true">
    <span v-for="particle in particles" :key="particle.id" class="leaf-particle" :class="`leaf-shape-${particle.shape}`" :style="particle.style"><i></i></span>
  </div>
</template>

<style scoped>
.leaf-particle{position:absolute;top:-14vh;display:block;opacity:0;animation:season-leaf-fall linear infinite;will-change:transform,opacity}
.leaf-particle i{position:relative;display:block;width:100%;height:100%;border-radius:85% 12% 78% 18%;background:linear-gradient(145deg,color-mix(in srgb,var(--season-color) 65%,#f3c96d),var(--season-color) 56%,color-mix(in srgb,var(--season-color) 76%,#633918));box-shadow:0 1px 2px rgba(86,48,21,.12);animation:season-leaf-flutter var(--season-flutter) ease-in-out infinite alternate}
.leaf-particle i::after{content:'';position:absolute;left:14%;top:48%;width:76%;height:1px;background:rgba(91,49,22,.26);transform:rotate(-13deg)}
.leaf-shape-1 i{border-radius:18% 82% 18% 82%}.leaf-shape-2 i{border-radius:16%;clip-path:polygon(50% 0,69% 23%,100% 31%,75% 53%,74% 88%,50% 73%,24% 91%,28% 55%,0 34%,31% 24%)}
@keyframes season-leaf-fall{0%{opacity:0;transform:translate3d(0,-10vh,0) rotate(0)}14%{opacity:var(--season-alpha)}44%{transform:translate3d(var(--season-sway),46vh,0) rotate(310deg)}76%{opacity:var(--season-alpha);transform:translate3d(calc(var(--season-drift) - var(--season-sway)),82vh,0) rotate(570deg)}100%{opacity:0;transform:translate3d(var(--season-drift),118vh,0) rotate(var(--season-spin))}}
@keyframes season-leaf-flutter{0%{transform:rotateX(10deg) rotateY(-18deg) scaleX(.92)}50%{transform:rotateX(42deg) rotateY(24deg) scaleX(.42)}100%{transform:rotateX(-12deg) rotateY(48deg) scaleX(-.82)}}
</style>
