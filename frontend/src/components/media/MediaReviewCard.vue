<script setup>
import { useI18n } from 'vue-i18n'
import { typeKey, statusKey } from '@/constants/mediaReview.js'
import MediaRating from './MediaRating.vue'

defineProps({ item: { type: Object, required: true } })
const { t, locale } = useI18n()
const formatDate = value => value
  ? new Intl.DateTimeFormat(locale.value, { year: 'numeric', month: 'short', day: 'numeric' }).format(new Date(`${value}T00:00:00`))
  : t('media.noFinishedDate')
</script>

<template>
  <router-link :to="`/media/${item.id}`" class="media-card">
    <div class="cover-wrap">
      <img v-if="item.coverUrl" :src="item.coverUrl" :alt="item.title" class="cover" loading="lazy" />
      <div v-else class="cover-placeholder"><i class="ti ti-photo" aria-hidden="true"></i></div>
      <span class="type-chip">{{ t(`media.type.${typeKey(item.mediaType)}`) }}</span>
    </div>
    <div class="card-body">
      <h2>{{ item.title }}</h2>
      <div class="meta">
        <span>{{ t(statusKey(item.mediaType, item.status)) }}</span>
        <span>{{ formatDate(item.finishedDate) }}</span>
      </div>
      <MediaRating :model-value="item.rating" readonly />
      <p class="review">{{ item.shortReview || t('media.noShortReview') }}</p>
    </div>
  </router-link>
</template>

<style scoped>
.media-card { display:flex; flex-direction:column; min-width:0; overflow:hidden; border:1px solid var(--glass-border); border-radius:18px; background:var(--glass-bg); box-shadow:0 8px 24px color-mix(in srgb,var(--shadow-color) 45%,transparent); backdrop-filter:blur(16px);-webkit-backdrop-filter:blur(16px);transition:transform .22s ease,border-color .22s ease; }
.media-card:hover { transform:translateY(-3px); border-color:var(--border-h); }
.cover-wrap { position:relative; aspect-ratio:3/4; overflow:hidden; background:var(--purple-50); }
.cover { width:100%; height:100%; display:block; object-fit:cover; }
.cover-placeholder { height:100%; display:grid; place-items:center; color:var(--purple-200); font-size:36px; }
.type-chip { position:absolute; left:10px; top:10px; padding:3px 9px; border-radius:999px; background:color-mix(in srgb,var(--bg-card) 86%,transparent); color:var(--purple-600); font-size:11px; backdrop-filter:blur(8px); }
.card-body { display:flex; flex-direction:column; gap:9px; padding:14px; flex:1; }
h2 { overflow:hidden; color:var(--text-1); font-size:16px; font-weight:600; line-height:1.4; text-overflow:ellipsis; white-space:nowrap; }
.meta { display:flex; justify-content:space-between; gap:8px; color:var(--text-3); font-size:11px; }
.review { display:-webkit-box; overflow:hidden; color:var(--text-2); font-size:13px; line-height:1.7; -webkit-box-orient:vertical; -webkit-line-clamp:3; min-height:66px; }
</style>
