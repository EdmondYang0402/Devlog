<script setup>
import { computed } from 'vue'
import { getArticleStatusLabel, isArticlePublished } from '@/constants/articleStatus.js'

const props = defineProps({ status: { type: [String, Number], default: 0 } })
const published = computed(() => isArticlePublished(props.status))
</script>

<template>
  <span :class="['admin-status-tag', published ? 'published' : 'draft']">
    <i aria-hidden="true"></i>{{ getArticleStatusLabel(status) }}
  </span>
</template>

<style scoped>
.admin-status-tag { display: inline-flex; align-items: center; gap: 6px; padding: 4px 8px; border: 1px solid currentColor; border-radius: 999px; font-size: 9px; white-space: nowrap; }
.admin-status-tag i { width: 5px; height: 5px; border-radius: 50%; background: currentColor; }
.published { border-color: rgba(104,197,155,.24); background: rgba(104,197,155,.075); color: #83d6b1; }
.draft { border-color: rgba(223,175,101,.24); background: rgba(223,175,101,.07); color: #e7bd7c; }
</style>
