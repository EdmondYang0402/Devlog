<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  profile: { type: Object, required: true },
  stats: { type: Object, required: true }
})
const { t } = useI18n()
const initial = computed(() => props.profile.authorName?.trim()?.charAt(0).toUpperCase() || '?')
const cardStyle = computed(() => props.profile.profileBackgroundUrl ? {
  backgroundImage: `linear-gradient(var(--profile-overlay), var(--profile-overlay)), url(${props.profile.profileBackgroundUrl})`
} : {})
</script>

<template>
  <article class="profile-panel glass-panel" :style="cardStyle">
    <div class="profile-avatar-shell">
      <img v-if="profile.avatarUrl" :src="profile.avatarUrl" :alt="profile.authorName || 'Hathaway'" />
      <span v-else class="profile-fallback">{{ initial }}</span>
    </div>

    <div class="profile-content">
      <h1 class="profile-title">Hathaway’s Blog</h1>
      <dl class="profile-stats">
        <div class="profile-stat">
          <i class="ti ti-file-text" aria-hidden="true"></i>
          <dd>{{ stats.posts }}</dd>
          <dt>{{ t('home.articles') }}</dt>
        </div>
        <div class="profile-stat">
          <i class="ti ti-folders" aria-hidden="true"></i>
          <dd>{{ stats.categories }}</dd>
          <dt>{{ t('home.categories') }}</dt>
        </div>
        <div class="profile-stat">
          <i class="ti ti-message-circle" aria-hidden="true"></i>
          <dd>{{ stats.comments }}</dd>
          <dt>{{ t('home.comments') }}</dt>
        </div>
      </dl>
    </div>
  </article>
</template>
