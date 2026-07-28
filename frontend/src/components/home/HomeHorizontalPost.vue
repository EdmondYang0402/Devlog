<script setup>
import { useI18n } from 'vue-i18n'
defineProps({ post: { type: Object, default: null } })
const { t } = useI18n()
</script>

<template>
  <router-link v-if="post" :to="`/post/${post.id}`" class="horizontal-post glass-panel">
    <img v-if="post.coverUrl" :src="post.coverUrl" :alt="post.title" loading="lazy" />
    <div class="post-shade"></div>
    <div><p>{{ post.categoryName || t('article.uncategorized') }} · {{ post.date }}</p><h2>{{ post.title }}</h2><span>{{ post.summary || t('article.noSummary') }}</span></div>
  </router-link>
</template>
