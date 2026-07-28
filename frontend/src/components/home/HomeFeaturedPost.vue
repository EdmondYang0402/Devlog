<script setup>
import { useI18n } from 'vue-i18n'
defineProps({ post: { type: Object, default: null } })
const { t } = useI18n()
</script>

<template>
  <router-link v-if="post" :to="`/post/${post.id}`" class="featured-post glass-panel">
    <img v-if="post.coverUrl" :src="post.coverUrl" :alt="post.title" loading="lazy" />
    <div class="post-shade"></div>
    <div class="featured-copy">
      <p>{{ post.categoryName || t('article.uncategorized') }} · {{ post.date }}</p>
      <h2>{{ post.title }}</h2>
      <span>{{ post.summary || t('article.noSummary') }}</span>
      <small><i class="ti ti-eye"></i>{{ post.viewCount }} <i class="ti ti-message"></i>{{ post.commentCount }}</small>
    </div>
  </router-link>
</template>
