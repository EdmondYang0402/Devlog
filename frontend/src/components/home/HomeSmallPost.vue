<script setup>
import { useI18n } from 'vue-i18n'
defineProps({ post: { type: Object, default: null } })
const { t } = useI18n()
</script>

<template>
  <router-link :to="post ? `/post/${post.id}` : '/archive'" class="small-post glass-panel">
    <p>{{ post?.categoryName || t('home.latestArticles') }}</p>
    <h2>{{ post?.title || t('home.browseArchive') }}</h2>
    <span>{{ post?.date || t('home.archiveHint') }}</span>
    <i class="ti ti-arrow-up-right"></i>
  </router-link>
</template>
