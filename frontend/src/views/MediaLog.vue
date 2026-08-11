<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getMediaReviewPage } from '@/api/mediaReview.js'
import { mediaArchiveSections } from '@/constants/mediaReview.js'
import MediaCategoryIcon from '@/components/media/MediaCategoryIcon.vue'
import MediaReviewSection from '@/components/media/MediaReviewSection.vue'

const PAGE_SIZE = 100
const { t } = useI18n()
const records = ref([])
const loading = ref(false)
const error = ref('')
const expandedSectionKey = ref(null)

const sections = computed(() => mediaArchiveSections.map(section => ({
  ...section,
  items: records.value.filter(item => Number(item.mediaType) === section.mediaType)
})))

const isExpanded = key => expandedSectionKey.value === key

const prefersReducedMotion = () => window.matchMedia?.('(prefers-reduced-motion: reduce)').matches

const setExpandedSection = key => {
  if (expandedSectionKey.value === key) return

  const update = () => {
    expandedSectionKey.value = key
    return nextTick()
  }

  if (document.startViewTransition && !prefersReducedMotion()) {
    document.startViewTransition(update)
  } else {
    update()
  }
}

const closeExpandedSection = () => setExpandedSection(null)

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const firstResponse = await getMediaReviewPage({ page: 1, size: PAGE_SIZE, sort: 'latest' })
    const firstPage = firstResponse?.data || {}
    const firstRecords = Array.isArray(firstPage.records) ? firstPage.records : []
    const total = Number(firstPage.total ?? firstRecords.length)
    const pageCount = Math.ceil(total / PAGE_SIZE)

    if (pageCount <= 1) {
      records.value = firstRecords
      return
    }

    const remainingPages = await Promise.all(
      Array.from({ length: pageCount - 1 }, (_, index) =>
        getMediaReviewPage({ page: index + 2, size: PAGE_SIZE, sort: 'latest' })
      )
    )
    records.value = [
      ...firstRecords,
      ...remainingPages.flatMap(response =>
        Array.isArray(response?.data?.records) ? response.data.records : []
      )
    ]
  } catch (err) {
    records.value = []
    error.value = err.response?.data?.message || t('media.loadFailed')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <main class="media-page">
    <header class="page-head glass-panel">
      <p class="eyebrow">MEDIA · MEMORY · MOMENTS</p>
      <h1>{{ t('media.title') }}</h1>
      <p>{{ t('media.subtitle') }}</p>
    </header>

    <div v-loading="loading" class="media-content" :aria-busy="loading">
      <div v-if="error" class="state-card glass-panel error-state">
        <i class="ti ti-cloud-off" aria-hidden="true"></i>
        <p>{{ error }}</p>
        <el-button plain @click="load">{{ t('common.retry') }}</el-button>
      </div>
      <template v-else>
        <div class="category-grid" :aria-label="t('media.section.label')">
          <article
            v-for="section in sections"
            :key="section.key"
            class="archive-card"
            :class="{ 'is-active': isExpanded(section.key) }"
            :style="{ viewTransitionName: `media-archive-${section.key}` }"
          >
            <button
              type="button"
              class="archive-card-trigger"
              :aria-expanded="isExpanded(section.key)"
              :aria-controls="`media-panel-${section.key}`"
              @click="setExpandedSection(section.key)"
            >
              <MediaCategoryIcon :type="section.iconType" />
              <span class="category-copy">
                <strong>{{ t(section.titleKey) }}</strong>
                <span>{{ t('media.section.archive') }}</span>
              </span>
              <span class="category-side">
                <span class="category-count">{{ section.items.length }}</span>
                <span v-if="!isExpanded(section.key)" class="category-chevron" aria-hidden="true"></span>
              </span>
            </button>

            <button
              v-if="isExpanded(section.key)"
              type="button"
              class="archive-card-close"
              :aria-label="t('common.close')"
              @click="closeExpandedSection"
            >
              <span aria-hidden="true">×</span>
            </button>

            <Transition name="archive-content">
              <div
                v-show="isExpanded(section.key)"
                :id="`media-panel-${section.key}`"
                class="archive-card-content"
              >
                <div class="archive-card-content-inner">
                  <MediaReviewSection
                    :section="section"
                    :items="section.items"
                    :show-heading="false"
                  />
                </div>
              </div>
            </Transition>
          </article>
        </div>
      </template>
    </div>
  </main>
</template>

<style scoped>
.media-page { width:min(920px,calc(100% - 40px)); margin:0 auto; padding:48px 0 76px; flex:1; }
.page-head { max-width:720px; margin-bottom:38px; padding:24px 28px; background:rgba(245,245,250,.36); border-color:rgba(255,255,255,.34); }
.eyebrow { color:var(--purple-400)!important; font-size:11px!important; letter-spacing:.16em; }
.page-head h1 { margin:7px 0; font-size:clamp(30px,5vw,46px); font-weight:650; letter-spacing:-.035em; }
.page-head>p { color:var(--text-2); font-size:14px; }
.media-content { min-height:320px; }
.category-grid { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:16px; align-items:start; }
.archive-card { position:relative; display:grid; min-width:0; min-height:104px; grid-template-rows:auto 0fr; overflow:hidden; border:1px solid rgba(255,255,255,.38); border-radius:22px; background:rgba(245,245,250,.32); box-shadow:0 9px 24px rgba(30,24,50,.075); color:var(--text-1); -webkit-backdrop-filter:blur(14px) saturate(112%); backdrop-filter:blur(14px) saturate(112%); transition:grid-template-rows .48s cubic-bezier(.22,1,.36,1),border-radius .42s ease,border-color .3s ease,background-color .3s ease,box-shadow .3s ease; }
.archive-card::before { position:absolute; right:-34px; bottom:-46px; width:112px; height:112px; border-radius:50%; background:radial-gradient(circle,rgba(177,134,246,.2),transparent 68%); content:''; opacity:.36; pointer-events:none; transition:opacity .3s ease,transform .48s cubic-bezier(.22,1,.36,1); }
.archive-card:not(.is-active):hover { border-color:rgba(255,255,255,.58); background:rgba(247,245,252,.43); box-shadow:0 14px 30px rgba(30,24,50,.105); }
.archive-card.is-active { grid-column:1/-1; grid-template-rows:auto 1fr; border-color:rgba(167,135,241,.68); border-radius:28px; background:linear-gradient(135deg,rgba(244,239,255,.5),rgba(237,246,255,.36)); box-shadow:0 18px 42px rgba(92,72,151,.15),inset 0 0 0 1px rgba(255,255,255,.28); }
.archive-card.is-active::before { opacity:.72; transform:scale(2.4); }
.archive-card.is-active .category-copy strong { color:var(--purple-700); }
.archive-card.is-active :deep(.category-icon) { filter:brightness(1.07) saturate(1.08); transform:scale(1.035); }
.archive-card-trigger { position:relative; z-index:1; display:grid; width:100%; min-width:0; min-height:102px; grid-template-columns:auto minmax(0,1fr) auto; align-items:center; gap:15px; padding:17px 18px; border:0; background:transparent; color:inherit; font:inherit; text-align:left; cursor:pointer; }
.archive-card-trigger:focus-visible,.archive-card-close:focus-visible { outline:2px solid color-mix(in srgb,var(--purple-400) 76%,white); outline-offset:-4px; }
.archive-card.is-active .archive-card-trigger { padding-right:70px; cursor:default; }
.category-copy { display:flex; min-width:0; flex-direction:column; gap:5px; }
.category-copy strong { overflow:hidden; color:var(--text-1); font-size:18px; font-weight:680; letter-spacing:-.015em; text-overflow:ellipsis; white-space:nowrap; transition:color .24s ease; }
.category-copy>span { color:var(--text-3); font-size:11px; letter-spacing:.06em; }
.category-side { position:relative; z-index:1; display:flex; align-items:center; gap:10px; }
.category-count { display:grid; min-width:30px; height:26px; place-items:center; padding:0 9px; border:1px solid rgba(255,255,255,.34); border-radius:999px; background:rgba(127,119,221,.1); color:var(--purple-700); font-size:11px; font-weight:650; font-variant-numeric:tabular-nums; }
.category-chevron { width:9px; height:9px; border-right:1.5px solid currentColor; border-bottom:1.5px solid currentColor; color:var(--text-3); transform:translateY(-2px) rotate(45deg); transition:transform .28s ease,color .24s ease; }
.archive-card-close { position:absolute; z-index:3; top:25px; right:22px; display:grid; width:36px; height:36px; place-items:center; padding:0; border:1px solid rgba(255,255,255,.42); border-radius:50%; background:rgba(255,255,255,.18); color:var(--text-2); font-family:inherit; font-size:25px; font-weight:300; line-height:1; cursor:pointer; transition:transform .2s ease,background-color .2s ease,color .2s ease; }
.archive-card-close:hover { background:rgba(255,255,255,.34); color:var(--text-1); transform:rotate(6deg) scale(1.04); }
.archive-card-content { position:relative; z-index:1; min-height:0; overflow:hidden; }
.archive-card-content-inner { min-height:0; padding:0 22px 22px; }
.archive-content-enter-active { transition:opacity .3s ease .16s,transform .38s cubic-bezier(.22,1,.36,1) .12s; }
.archive-content-leave-active { transition:opacity .18s ease,transform .22s ease; }
.archive-content-enter-from,.archive-content-leave-to { opacity:0; transform:translateY(-10px); }
.state-card { display:grid; min-height:260px; place-items:center; align-content:center; gap:9px; padding:30px; border-style:dashed; color:var(--text-3); text-align:center; }
.state-card i { color:var(--purple-200); font-size:38px; }
.state-card p { font-size:13px; }
@media(max-width:820px){.media-page{width:min(100% - 28px,720px);padding:38px 0 58px}.page-head{margin-bottom:32px}}
@media(max-width:560px){.media-page{width:calc(100% - 24px);padding:30px 0 48px}.page-head{margin-bottom:28px;padding:20px;border-radius:18px}.page-head>p{font-size:13px}.category-grid{gap:10px}.archive-card{min-height:92px;border-radius:18px}.archive-card.is-active{border-radius:22px}.archive-card-trigger{min-height:90px;gap:10px;padding:13px 12px}.archive-card.is-active .archive-card-trigger{padding-right:56px}.archive-card-trigger :deep(.category-icon){width:44px;height:44px;border-radius:15px}.category-copy strong{font-size:16px}.category-copy>span{display:none}.category-side{gap:7px}.category-count{min-width:26px;height:23px;padding:0 7px}.category-chevron{width:7px;height:7px}.archive-card-close{top:26px;right:13px;width:32px;height:32px;font-size:22px}.archive-card-content-inner{padding:0 14px 14px}}
@media(max-width:360px){.category-grid{grid-template-columns:minmax(0,1fr)}}
:global(html[data-theme='dark']) .archive-card { border-color:rgba(226,219,255,.16); background:rgba(21,24,43,.36); box-shadow:0 12px 30px rgba(6,8,20,.16); }
:global(html[data-theme='dark']) .archive-card:not(.is-active):hover { border-color:rgba(211,195,255,.31); background:rgba(36,36,66,.46); }
:global(html[data-theme='dark']) .archive-card.is-active { border-color:rgba(184,153,255,.58); background:linear-gradient(135deg,rgba(77,62,119,.48),rgba(44,66,104,.38)); }
:global(html[data-theme='dark']) .archive-card.is-active .category-copy strong { color:#d9c9ff; }
:global(html[data-theme='dark']) .category-count { border-color:rgba(226,219,255,.14); background:rgba(165,132,239,.17); color:#d5c2ff; }
:global(html[data-theme='dark']) .archive-card-close { border-color:rgba(226,219,255,.18); background:rgba(255,255,255,.08); }
:global(::view-transition-group(*)) { animation-duration:.48s; animation-timing-function:cubic-bezier(.22,1,.36,1); }
@media(prefers-reduced-motion:reduce){.archive-card,.archive-card::before,.archive-card-close,.archive-card-content{transition:none}.archive-card-close:hover{transform:none}}
</style>
