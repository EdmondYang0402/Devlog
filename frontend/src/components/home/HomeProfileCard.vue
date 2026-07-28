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
    <div class="profile-identity">
      <img v-if="profile.avatarUrl" :src="profile.avatarUrl" :alt="profile.authorName" />
      <span v-else class="profile-fallback">{{ initial }}</span>
      <div>
        <p class="panel-kicker">{{ profile.siteTitle }}</p>
        <h1>{{ profile.authorName }}</h1>
        <p class="profile-bio">{{ profile.authorBio }}</p>
      </div>
    </div>
    <div class="profile-foot">
      <div class="profile-stats">
        <span><strong>{{ stats.posts }}</strong>{{ t('home.articles') }}</span>
        <span><strong>{{ stats.categories }}</strong>{{ t('home.categories') }}</span>
        <span><strong>{{ stats.comments }}</strong>{{ t('home.comments') }}</span>
      </div>
      <div v-if="profile.githubUrl || profile.giteeUrl" class="profile-links">
        <a v-if="profile.githubUrl" :href="profile.githubUrl" target="_blank" rel="noopener noreferrer">GitHub</a>
        <a v-if="profile.giteeUrl" :href="profile.giteeUrl" target="_blank" rel="noopener noreferrer">Gitee</a>
      </div>
    </div>
  </article>
</template>
