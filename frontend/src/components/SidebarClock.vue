<template>
  <div class="clock">
    <div class="date">{{ dateStr }}</div>
    <div class="time">{{ timeStr }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()
const dateStr = ref('')
const timeStr = ref('')

function tick() {
  const n = new Date()
  dateStr.value = new Intl.DateTimeFormat(locale.value, {
    year: 'numeric', month: '2-digit', day: '2-digit', weekday: 'long'
  }).format(n)
  timeStr.value = new Intl.DateTimeFormat(locale.value, {
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  }).format(n)
}

let t
watch(locale, tick)
onMounted(() => { tick(); t = setInterval(tick, 1000) })
onUnmounted(() => clearInterval(t))
</script>

<style scoped>
.clock { text-align: center; padding: .3rem 0; }
.date { font-size: 11px; color: var(--text-3); letter-spacing: .03em; margin-bottom: 4px; }
.time { font-size: 22px; font-weight: 500; color: var(--purple-400); letter-spacing: .1em; font-family: 'JetBrains Mono', monospace; }
</style>
