<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const petals = ref([])

const MOTIONS = [
  'sakura-drift-left',
  'sakura-drift-right',
  'sakura-drift-wave',
  'sakura-drift-sweep',
  'sakura-drift-near'
]

const DEPTHS = {
  far: { size: [5, 8], duration: [18, 30], opacity: [0.25, 0.45], blur: [0, 1] },
  middle: { size: [9, 15], duration: [12, 22], opacity: [0.5, 0.75], blur: [0, 0.8] },
  near: { size: [16, 26], duration: [8, 16], opacity: [0.65, 0.9], blur: [1, 2.8] }
}

const random = (min, max) => min + Math.random() * (max - min)
const randomInt = (min, max) => Math.floor(random(min, max + 1))

const densityForWidth = width => {
  if (width < 768) return { key: 'mobile', far: 9, middle: 5, near: 2 }
  if (width < 1200) return { key: 'tablet', far: 14, middle: 9, near: 3 }
  return { key: 'desktop', far: 20, middle: 12, near: 4 }
}

const createPetal = (depth, index, depthIndex) => {
  const config = DEPTHS[depth]
  const isLargeNearPetal = depth === 'near' && (
    (window.innerWidth >= 768 && depthIndex === 0) || Math.random() < 0.3
  )
  const size = isLargeNearPetal ? random(28, 34) : random(...config.size)
  const duration = random(...config.duration)
  const drift = random(70, depth === 'near' ? 330 : 230)
  const sway = random(18, depth === 'far' ? 52 : 86)
  const motionIndex = depth === 'near' && Math.random() < 0.55
    ? 4
    : randomInt(0, depth === 'far' ? 3 : 4)

  return {
    id: `${Date.now()}-${index}-${Math.random().toString(36).slice(2)}`,
    depth,
    shape: randomInt(0, 3),
    motion: MOTIONS[motionIndex],
    style: {
      left: `${random(-4, 104).toFixed(2)}vw`,
      top: `${(-size * random(1.2, 3.8)).toFixed(1)}px`,
      width: `${size.toFixed(1)}px`,
      height: `${(size / random(0.62, 0.78)).toFixed(1)}px`,
      opacity: '0',
      filter: `blur(${random(...config.blur).toFixed(2)}px)`,
      animationName: MOTIONS[motionIndex],
      animationDuration: `${duration.toFixed(2)}s`,
      animationDelay: `${(-random(0, duration)).toFixed(2)}s`,
      '--petal-alpha': random(...config.opacity).toFixed(2),
      '--drift-p22': `${(drift * 0.22).toFixed(0)}px`,
      '--drift-p30': `${(drift * 0.3).toFixed(0)}px`,
      '--drift-p38': `${(drift * 0.38).toFixed(0)}px`,
      '--drift-p50': `${(drift * 0.5).toFixed(0)}px`,
      '--drift-p72': `${(drift * 0.72).toFixed(0)}px`,
      '--drift-p88': `${(drift * 0.88).toFixed(0)}px`,
      '--drift-pos': `${drift.toFixed(0)}px`,
      '--drift-n18': `${(-drift * 0.18).toFixed(0)}px`,
      '--drift-n22': `${(-drift * 0.22).toFixed(0)}px`,
      '--drift-n46': `${(-drift * 0.46).toFixed(0)}px`,
      '--drift-n72': `${(-drift * 0.72).toFixed(0)}px`,
      '--drift-neg': `${(-drift).toFixed(0)}px`,
      '--drift-n125': `${(-drift * 1.25).toFixed(0)}px`,
      '--sway-small': `${(sway * 0.45).toFixed(0)}px`,
      '--sway-small-neg': `${(-sway * 0.6).toFixed(0)}px`,
      '--sway-pos': `${sway.toFixed(0)}px`,
      '--sway-neg': `${(-sway).toFixed(0)}px`,
      '--sway-wide': `${(sway * 1.25).toFixed(0)}px`,
      '--rotate-start': `${random(-160, 160).toFixed(0)}deg`,
      '--rotate-end': `${random(420, 980).toFixed(0)}deg`,
      '--rest-y': `${random(5, 92).toFixed(1)}vh`,
      '--rest-rotate': `${random(-35, 35).toFixed(0)}deg`,
      '--flutter-duration': `${random(2.4, 5.8).toFixed(2)}s`,
      '--flutter-delay': `${(-random(0, 5)).toFixed(2)}s`,
      '--flip-min': random(0.18, 0.52).toFixed(2),
      '--flip-direction': Math.random() > 0.5 ? 1 : -1
    }
  }
}

let densityKey = ''

const generatePetals = () => {
  const density = densityForWidth(window.innerWidth)
  densityKey = density.key
  const nextPetals = []

  ;['far', 'middle', 'near'].forEach(depth => {
    for (let index = 0; index < density[depth]; index += 1) {
      nextPetals.push(createPetal(depth, nextPetals.length, index))
    }
  })

  petals.value = nextPetals
}

const handleResize = () => {
  const nextDensity = densityForWidth(window.innerWidth)
  if (nextDensity.key !== densityKey) generatePetals()
}

onMounted(() => {
  generatePetals()
  window.addEventListener('resize', handleResize, { passive: true })
})

onUnmounted(() => window.removeEventListener('resize', handleResize))
</script>

<template>
  <div class="sakura-petal-layer" aria-hidden="true">
    <span
      v-for="petal in petals"
      :key="petal.id"
      class="sakura-petal"
      :class="`is-${petal.depth}`"
      :style="petal.style"
    >
      <svg class="sakura-petal-art" viewBox="0 0 24 34" role="presentation">
        <defs>
          <linearGradient :id="`sakura-fill-${petal.id}`" x1="15%" y1="5%" x2="82%" y2="94%">
            <stop offset="0%" stop-color="#ffd0dc" />
            <stop offset="48%" stop-color="#f8b7cc" />
            <stop offset="100%" stop-color="#eaa0bb" />
          </linearGradient>
        </defs>
        <path
          v-if="petal.shape === 0"
          d="M12 32C8.2 27.3 3.2 22.4 3.8 14.4 4.2 8.3 8.2 3.4 12 1.2c3.1 3 8.6 6.6 8.3 14.3-.3 7.3-4.6 12.4-8.3 16.5Z"
          :fill="`url(#sakura-fill-${petal.id})`"
        />
        <path
          v-else-if="petal.shape === 1"
          d="M10.6 32.5C8.8 27.1 3.1 23 3.4 15.2 3.7 8.6 8.7 3.1 14 1c1.1 4.7 7.7 8.9 6.5 16.7-1 6.4-5.8 11.5-9.9 14.8Z"
          :fill="`url(#sakura-fill-${petal.id})`"
        />
        <path
          v-else-if="petal.shape === 2"
          d="M12.1 32.4C7.7 28.7 2.2 22.8 2.9 15.3 3.5 8.9 7.6 4.5 11.7 1.4c4.4 2.5 9.1 7.2 9.4 13.3.4 7.6-4.4 13.8-9 17.7Z"
          :fill="`url(#sakura-fill-${petal.id})`"
        />
        <path
          v-else
          d="M11.8 32.7C9.1 27.6 5.2 23.2 5.4 15.4 5.5 9 8.4 3.7 12.8 1c2.4 4.2 6.2 8.4 5.8 15.5-.4 7.4-3.8 12.5-6.8 16.2Z"
          :fill="`url(#sakura-fill-${petal.id})`"
        />
        <path d="M12 30.5c-.5-8.2-.1-17.1.2-26.2" fill="none" stroke="#fff4f7" stroke-width=".7" stroke-linecap="round" opacity=".48" />
      </svg>
    </span>
  </div>
</template>

<style>
.sakura-petal-layer {
  position: fixed;
  z-index: 60;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  user-select: none;
  contain: strict;
}

.sakura-petal {
  position: absolute;
  display: block;
  transform-origin: 50% 50%;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  will-change: transform, opacity;
}

.sakura-petal-art {
  display: block;
  width: 100%;
  height: 100%;
  overflow: visible;
  transform-origin: 50% 54%;
  animation: sakura-petal-flutter var(--flutter-duration) ease-in-out var(--flutter-delay) infinite alternate;
  filter: drop-shadow(0 1px 1.5px rgba(178, 92, 124, 0.13));
  will-change: transform;
}

.sakura-petal.is-far .sakura-petal-art { filter: none; }
.sakura-petal.is-near .sakura-petal-art { filter: drop-shadow(0 1px 2px rgba(155, 72, 108, 0.16)); }

@keyframes sakura-petal-flutter {
  0% { transform: rotateX(8deg) rotateY(-12deg) rotateZ(-7deg) scaleX(1); }
  48% { transform: rotateX(44deg) rotateY(18deg) rotateZ(9deg) scaleX(var(--flip-min)); }
  100% { transform: rotateX(-14deg) rotateY(38deg) rotateZ(-4deg) scaleX(var(--flip-direction)); }
}

@keyframes sakura-drift-left {
  0% { opacity: 0; transform: translate3d(0, -5vh, 0) rotate(var(--rotate-start)); }
  16% { opacity: var(--petal-alpha); transform: translate3d(var(--sway-small), 17vh, 0) rotate(90deg); }
  48% { opacity: var(--petal-alpha); transform: translate3d(var(--drift-n46), 54vh, 0) rotate(280deg); }
  78% { opacity: var(--petal-alpha); transform: translate3d(calc(var(--drift-n72) + var(--sway-pos)), 88vh, 0) rotate(460deg); }
  100% { opacity: 0; transform: translate3d(var(--drift-neg), 116vh, 0) rotate(var(--rotate-end)); }
}

@keyframes sakura-drift-right {
  0% { opacity: 0; transform: translate3d(0, -7vh, 0) rotate(var(--rotate-start)); }
  18% { opacity: var(--petal-alpha); transform: translate3d(var(--sway-small-neg), 19vh, 0) rotate(120deg); }
  50% { opacity: var(--petal-alpha); transform: translate3d(calc(var(--drift-p38) + var(--sway-pos)), 56vh, 0) rotate(310deg); }
  82% { opacity: var(--petal-alpha); transform: translate3d(calc(var(--drift-p72) + var(--sway-neg)), 91vh, 0) rotate(520deg); }
  100% { opacity: 0; transform: translate3d(var(--drift-pos), 116vh, 0) rotate(var(--rotate-end)); }
}

@keyframes sakura-drift-wave {
  0% { opacity: 0; transform: translate3d(0, -6vh, 0) rotate(var(--rotate-start)); }
  20% { opacity: var(--petal-alpha); transform: translate3d(var(--sway-neg), 20vh, 0) rotate(100deg); }
  44% { transform: translate3d(var(--sway-wide), 47vh, 0) rotate(250deg); }
  68% { opacity: var(--petal-alpha); transform: translate3d(var(--sway-small-neg), 75vh, 0) rotate(430deg); }
  100% { opacity: 0; transform: translate3d(var(--drift-p30), 116vh, 0) rotate(var(--rotate-end)); }
}

@keyframes sakura-drift-sweep {
  0% { opacity: 0; transform: translate3d(var(--sway-small), -10vh, 0) rotate(var(--rotate-start)); }
  14% { opacity: var(--petal-alpha); transform: translate3d(0, 13vh, 0) rotate(110deg); }
  42% { transform: translate3d(calc(var(--drift-n22) + var(--sway-pos)), 45vh, 0) rotate(300deg); }
  76% { opacity: var(--petal-alpha); transform: translate3d(var(--drift-n72), 84vh, 0) rotate(540deg); }
  100% { opacity: 0; transform: translate3d(var(--drift-n125), 116vh, 0) rotate(var(--rotate-end)); }
}

@keyframes sakura-drift-near {
  0% { opacity: 0; transform: translate3d(var(--sway-small-neg), -14vh, 0) rotate(var(--rotate-start)); }
  12% { opacity: var(--petal-alpha); transform: translate3d(var(--drift-p22), 11vh, 0) rotate(130deg); }
  38% { transform: translate3d(var(--drift-n18), 42vh, 0) rotate(330deg); }
  70% { opacity: var(--petal-alpha); transform: translate3d(var(--drift-p50), 80vh, 0) rotate(570deg); }
  100% { opacity: 0; transform: translate3d(var(--drift-p88), 122vh, 0) rotate(var(--rotate-end)); }
}

@media (prefers-reduced-motion: reduce) {
  .sakura-petal { display: none; }
  .sakura-petal:nth-child(-n + 6) {
    display: block;
    top: var(--rest-y);
    opacity: .24;
    transform: rotate(var(--rest-rotate));
    animation: none;
    filter: blur(.4px);
  }
  .sakura-petal:nth-child(-n + 6) .sakura-petal-art { animation: none; }
}
</style>
