<template>
  <canvas ref="cvs" class="star-canvas"></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const cvs = ref(null)
let raf, stars = []

function init() {
  const c = cvs.value
  const p = c.parentElement
  c.width = p.offsetWidth
  c.height = p.offsetHeight
  stars = Array.from({ length: 160 }, () => ({
    x: Math.random() * c.width,
    y: Math.random() * c.height,
    r: Math.random() * 1.4 + .25,
    phase: Math.random() * Math.PI * 2,
    sp: Math.random() * .012 + .004,
  }))
}

function draw() {
  const c = cvs.value; if (!c) return
  const ctx = c.getContext('2d')
  ctx.clearRect(0, 0, c.width, c.height)
  const t = performance.now() / 1000
  for (const s of stars) {
    const a = .3 + .6 * Math.abs(Math.sin(t * s.sp * 10 + s.phase))
    ctx.beginPath(); ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(210,200,255,${a})`; ctx.fill()
  }
  raf = requestAnimationFrame(draw)
}

onMounted(() => { init(); draw() })
onUnmounted(() => cancelAnimationFrame(raf))
</script>

<style scoped>
.star-canvas {
  position: absolute; inset: 0;
  width: 100%; height: 100%;
  pointer-events: none;
}
</style>
