<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({ profile: { type: Object, required: true } })
const { t, locale } = useI18n()
const now = ref(new Date())
let timer = null

const time = computed(() => new Intl.DateTimeFormat(locale.value, {
  hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false
}).format(now.value))
const date = computed(() => new Intl.DateTimeFormat(locale.value, {
  month: 'long', day: 'numeric', weekday: 'short'
}).format(now.value))

watch(locale, () => { now.value = new Date() })
onMounted(() => { timer = window.setInterval(() => { now.value = new Date() }, 1000) })
onUnmounted(() => { if (timer !== null) window.clearInterval(timer) })
</script>

<template>
  <article class="status-panel glass-panel">
    <div class="status-heading"><span>{{ t('home.currentStatus') }}</span><i class="status-dot"></i></div>
    <time>{{ time }}</time>
    <p class="status-date">{{ date }}</p>
    <p class="status-copy">{{ profile.announcement || t('home.noAnnouncement') }}</p>
    <div v-if="profile.heroKeywords?.length" class="status-tags">
      <span v-for="keyword in profile.heroKeywords.slice(0, 3)" :key="keyword">{{ keyword }}</span>
    </div>
  </article>
</template>
