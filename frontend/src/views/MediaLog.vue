<script setup>
import { onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { getMediaReviewPage } from '@/api/mediaReview.js'
import { mediaTypeOptions } from '@/constants/mediaReview.js'
import MediaReviewCard from '@/components/media/MediaReviewCard.vue'
import MediaReviewTimeline from '@/components/media/MediaReviewTimeline.vue'

const { t } = useI18n()
const records = ref([])
const total = ref(0)
const loading = ref(false)
const error = ref('')
const page = ref(1)
const size = ref(12)
const mediaType = ref('')
const sort = ref('latest')
const view = ref(localStorage.getItem('media-log-view') || 'timeline')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await getMediaReviewPage({
      page: page.value,
      size: size.value,
      mediaType: mediaType.value === '' ? undefined : mediaType.value,
      sort: sort.value
    })
    records.value = response.data?.records || []
    total.value = response.data?.total || 0
  } catch (err) {
    records.value = []
    total.value = 0
    error.value = err.response?.data?.message || t('media.loadFailed')
  } finally {
    loading.value = false
  }
}

const changeFilter = () => {
  // 非第一页时交给 page watcher 发起唯一请求；第一页没有变化事件，才直接加载。
  if (page.value === 1) load()
  else page.value = 1
}
const changeView = next => {
  // 时间轴和矩阵只切换展示组件，共用 records、筛选条件和分页状态，不重复请求。
  view.value = next
  localStorage.setItem('media-log-view', next)
}

watch(page, load)
onMounted(load)
</script>

<template>
  <main class="media-page">
    <header class="page-head glass-panel">
      <p class="eyebrow">MEDIA · MEMORY · MOMENTS</p>
      <h1>{{ t('media.title') }}</h1>
      <p>{{ t('media.subtitle') }}</p>
    </header>

    <section class="toolbar glass-panel" :aria-label="t('media.filters')">
      <div class="type-filter">
        <button :class="{ active: mediaType === '' }" @click="mediaType = ''; changeFilter()">{{ t('media.all') }}</button>
        <button v-for="type in mediaTypeOptions" :key="type.value" :class="{ active: mediaType === type.value }" @click="mediaType = type.value; changeFilter()">
          {{ t(`media.type.${type.key}`) }}
        </button>
      </div>
      <div class="toolbar-right">
        <el-select v-model="sort" class="sort-select" :aria-label="t('media.sort')" @change="changeFilter">
          <el-option :label="t('media.sortLatest')" value="latest" />
          <el-option :label="t('media.sortRating')" value="rating" />
        </el-select>
        <div class="view-switch" :aria-label="t('media.viewMode')">
          <button :class="{ active: view === 'timeline' }" @click="changeView('timeline')"><i class="ti ti-timeline"></i>{{ t('media.timeline') }}</button>
          <button :class="{ active: view === 'grid' }" @click="changeView('grid')"><i class="ti ti-layout-grid"></i>{{ t('media.grid') }}</button>
        </div>
      </div>
    </section>

    <section v-loading="loading" class="media-content">
      <div v-if="error" class="state-card error-state">
        <p>{{ error }}</p><el-button plain @click="load">{{ t('common.retry') }}</el-button>
      </div>
      <div v-else-if="!loading && !records.length" class="state-card">
        <i class="ti ti-books"></i><h2>{{ t('media.empty') }}</h2><p>{{ t('media.emptyHint') }}</p>
      </div>
      <Transition name="view-fade" mode="out-in">
        <MediaReviewTimeline v-if="records.length && view === 'timeline'" :key="'timeline'" :items="records" />
        <div v-else-if="records.length" :key="'grid'" class="media-grid">
          <MediaReviewCard v-for="item in records" :key="item.id" :item="item" />
        </div>
      </Transition>
    </section>

    <el-pagination v-if="total > size" v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" class="pager" />
  </main>
</template>

<style scoped>
.media-page { width:min(1180px,calc(100% - 40px)); margin:0 auto; padding:56px 0 72px; flex:1; }
.page-head { max-width:720px; margin-bottom:22px; padding:24px 28px; }
.eyebrow { color:var(--purple-400)!important; font-size:11px!important; letter-spacing:.16em; }
.page-head h1 { margin:7px 0; font-size:clamp(30px,5vw,46px); font-weight:650; letter-spacing:-.035em; }
.page-head>p { color:var(--text-2); font-size:14px; }
.toolbar { display:flex; align-items:center; justify-content:space-between; gap:18px; margin-bottom:28px; padding:10px; border-color:var(--glass-border); border-radius:16px; background:var(--glass-bg-soft); }
.type-filter,.toolbar-right,.view-switch { display:flex; align-items:center; gap:6px; flex-wrap:wrap; }
.toolbar button { padding:7px 12px; border:0; border-radius:9px; background:transparent; color:var(--text-2); cursor:pointer; font:inherit; font-size:12px; }
.toolbar button.active { background:var(--purple-50); color:var(--purple-600); }
.view-switch { padding-left:8px; border-left:1px solid var(--border); }
.view-switch button { display:flex; align-items:center; gap:5px; }
.sort-select { width:142px; }
.media-content { min-height:260px; }
.media-grid { display:grid; grid-template-columns:repeat(5,minmax(0,1fr)); gap:18px; }
.state-card { display:grid; place-items:center; gap:7px; min-height:260px; padding:30px; border:1px dashed var(--border); border-radius:16px; color:var(--text-3); text-align:center; }
.state-card i { color:var(--purple-200); font-size:38px; }.state-card h2{color:var(--text-2);font-size:17px}.state-card p{font-size:13px}
.pager { justify-content:center; margin-top:32px; }
.view-fade-enter-active,.view-fade-leave-active{transition:opacity .16s ease,transform .16s ease}.view-fade-enter-from{opacity:0;transform:translateY(4px)}.view-fade-leave-to{opacity:0}
@media(max-width:1020px){.media-grid{grid-template-columns:repeat(4,minmax(0,1fr))}.toolbar{align-items:flex-start;flex-direction:column}.toolbar-right{width:100%;justify-content:space-between}}
@media(max-width:760px){.media-page{width:min(100% - 24px,680px);padding:36px 0 52px}.media-grid{grid-template-columns:repeat(2,minmax(0,1fr));gap:12px}.type-filter{width:100%}.toolbar-right{align-items:stretch;flex-direction:column}.sort-select{width:100%}.view-switch{border-left:0;padding-left:0}.view-switch button{flex:1;justify-content:center}}
@media(max-width:400px){.media-grid{grid-template-columns:1fr 1fr}.media-page{width:calc(100% - 16px)}.toolbar{padding:8px}.toolbar button{padding:6px 9px}}
</style>
