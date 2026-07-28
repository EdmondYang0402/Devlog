<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { marked } from 'marked'
import { getMediaReviewDetail } from '@/api/mediaReview.js'
import { typeKey, statusKey } from '@/constants/mediaReview.js'
import MediaRating from '@/components/media/MediaRating.vue'

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()
const item = ref(null)
const loading = ref(false)
const error = ref('')
const renderedContent = computed(() => item.value?.content ? marked(item.value.content) : '')
const formatDate = value => value ? new Intl.DateTimeFormat(locale.value, { year:'numeric', month:'long', day:'numeric' }).format(new Date(`${value}T00:00:00`)) : t('media.noFinishedDate')

const load = async () => {
  loading.value = true
  try {
    const response = await getMediaReviewDetail(route.params.id)
    item.value = response.data || null
    if (!item.value) error.value = t('media.notFound')
  } catch (err) {
    error.value = err.response?.data?.message || t('media.loadFailed')
  } finally { loading.value = false }
}
onMounted(load)
</script>

<template>
  <main v-loading="loading" class="detail-page">
    <button class="back" type="button" @click="router.back()"><i class="ti ti-arrow-left"></i>{{ t('common.back') }}</button>
    <div v-if="error" class="detail-state"><p>{{ error }}</p><router-link to="/media">{{ t('media.backToLog') }}</router-link></div>
    <article v-else-if="item" class="detail-card glass-panel glass-panel--reading">
      <div class="cover-panel">
        <img v-if="item.coverUrl" :src="item.coverUrl" :alt="item.title" />
        <div v-else class="cover-empty"><i class="ti ti-photo"></i></div>
      </div>
      <div class="intro">
        <p class="kind">{{ t(`media.type.${typeKey(item.mediaType)}`) }}</p>
        <h1>{{ item.title }}</h1>
        <span class="status">{{ t(statusKey(item.mediaType, item.status)) }}</span>
        <MediaRating :model-value="item.rating" readonly />
        <dl><div><dt>{{ t('media.finishedDate') }}</dt><dd>{{ formatDate(item.finishedDate) }}</dd></div></dl>
        <blockquote v-if="item.shortReview">{{ item.shortReview }}</blockquote>
      </div>
      <section v-if="item.content" class="content-section">
        <h2>{{ t('media.longReview') }}</h2>
        <div class="prose" v-html="renderedContent"></div>
      </section>
    </article>
  </main>
</template>

<style scoped>
.detail-page{width:min(960px,calc(100% - 40px));min-height:60vh;margin:0 auto;padding:42px 0 72px;flex:1}.back{display:flex;align-items:center;gap:6px;margin-bottom:20px;border:0;background:transparent;color:var(--text-2);cursor:pointer;font:inherit}.detail-card{display:grid;grid-template-columns:260px minmax(0,1fr);gap:38px;padding:28px;border:1px solid var(--border);border-radius:20px;background:color-mix(in srgb,var(--bg-card) 88%,transparent);box-shadow:0 14px 38px color-mix(in srgb,var(--shadow-color) 45%,transparent)}.cover-panel{aspect-ratio:3/4;overflow:hidden;border-radius:14px;background:var(--purple-50)}.cover-panel img{width:100%;height:100%;object-fit:cover}.cover-empty{height:100%;display:grid;place-items:center;color:var(--purple-200);font-size:42px}.intro{min-width:0;padding-top:8px}.kind{color:var(--purple-400);font-size:11px;letter-spacing:.12em;text-transform:uppercase}.intro h1{margin:7px 0 10px;font-size:clamp(28px,5vw,42px);line-height:1.2}.status{display:inline-block;margin-bottom:18px;padding:4px 10px;border-radius:999px;background:var(--purple-50);color:var(--purple-600);font-size:12px}.intro dl{margin-top:20px}.intro dl div{display:flex;gap:20px;padding:10px 0;border-top:1px solid var(--border);font-size:13px}.intro dt{color:var(--text-3)}.intro dd{color:var(--text-2)}blockquote{margin-top:20px;padding:14px 18px;border-left:3px solid var(--purple-200);border-radius:0 10px 10px 0;background:var(--purple-50);color:var(--text-2);font-size:14px}.content-section{grid-column:1/-1;padding-top:28px;border-top:1px solid var(--border)}.content-section h2{margin-bottom:16px;font-size:18px}.prose{color:var(--text-2);font-size:15px;line-height:1.9}.prose :deep(p){margin:0 0 1em}.detail-state{display:grid;place-items:center;gap:10px;min-height:300px;color:var(--text-2)}.detail-state a{color:var(--purple-400)}@media(max-width:680px){.detail-page{width:calc(100% - 24px);padding-top:28px}.detail-card{grid-template-columns:100px minmax(0,1fr);gap:18px;padding:16px}.intro{padding:0}.intro h1{font-size:24px}.content-section{padding-top:20px}.cover-panel{align-self:start}.intro dl div{display:block}.intro dd{margin-top:3px}}
.detail-card.glass-panel{background:var(--glass-bg-reading);border-color:var(--glass-border);box-shadow:var(--glass-shadow);backdrop-filter:blur(18px);-webkit-backdrop-filter:blur(18px)}
.back{padding:7px 12px;border-radius:999px!important;background:rgba(255,255,255,.16)!important;color:var(--text-1)!important;backdrop-filter:blur(10px)}
</style>
