<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import { getArticleList } from '@/api/article.js'
import { getCategoryList } from '@/api/category.js'
import { getPublicTags } from '@/api/tag.js'
import ArchiveTimeline from '@/components/archive/ArchiveTimeline.vue'
import ArchiveGrid from '@/components/archive/ArchiveGrid.vue'
import GlassPanel from '@/components/common/GlassPanel.vue'

const ARCHIVE_VIEW_KEY = 'devlog-archive-view'
const DEFAULT_VIEW = 'timeline'
const PAGE_SIZE = 9

const { t } = useI18n()
const route = useRoute()
const articles = ref([])
const total = ref(0)
const page = ref(1)
const searchKeyword = ref(typeof route.query.keyword === 'string' ? route.query.keyword : '')
const selectedCategoryId = ref(null)
const categories = ref([])
const selectedTagIds = ref([])
const tagOptions = ref([])
const tagsLoading = ref(false)
const isLoading = ref(true)
const loadFailed = ref(false)
const archiveResultsRef = ref(null)

const savedView = localStorage.getItem(ARCHIVE_VIEW_KEY)
const currentView = ref(savedView === 'grid' || savedView === 'timeline' ? savedView : DEFAULT_VIEW)

const normalizeArticle = item => ({
  ...item,
  categoryName: item.categoryName || item.category || '',
  createTime: item.createTime || item.publishTime || item.publishedAt || '',
  viewCount: Number(item.viewCount ?? 0),
  commentCount: Number(item.commentCount ?? 0)
})

const sortedArticles = computed(() => [...articles.value].sort((left, right) => {
  const rightTime = new Date(right.createTime || 0).getTime() || 0
  const leftTime = new Date(left.createTime || 0).getTime() || 0
  return rightTime - leftTime
}))

const filteredArticles = computed(() => sortedArticles.value)

let requestSequence = 0
const loadArticles = async () => {
  const sequence = ++requestSequence
  isLoading.value = true
  loadFailed.value = false
  try {
    const response = await getArticleList(page.value, PAGE_SIZE, selectedCategoryId.value, {
      keyword: searchKeyword.value.trim(),
      tagIds: selectedTagIds.value
    })
    const payload = response?.data || response || {}
    const records = Array.isArray(payload.records) ? payload.records : []
    if (sequence !== requestSequence) return
    articles.value = records.map(normalizeArticle)
    total.value = Number(payload.total ?? records.length)
  } catch (error) {
    if (sequence !== requestSequence) return
    console.error('Failed to load archive articles:', error)
    articles.value = []
    total.value = 0
    loadFailed.value = true
  } finally {
    if (sequence === requestSequence) isLoading.value = false
  }
}

const loadFilterOptions = async () => {
  tagsLoading.value = true
  try {
    const [categoryResult, tagResult] = await Promise.all([getCategoryList(), getPublicTags()])
    categories.value = Array.isArray(categoryResult?.data) ? categoryResult.data : []
    tagOptions.value = Array.isArray(tagResult?.data) ? tagResult.data : []
  } catch (error) {
    console.error('Failed to load archive filter options:', error)
    categories.value = []
    tagOptions.value = []
  } finally {
    tagsLoading.value = false
  }
}

const resetPageAndLoad = () => {
  page.value = 1
  loadArticles()
}

const selectCategory = categoryId => {
  selectedCategoryId.value = categoryId
  resetPageAndLoad()
}

const handleTagFilterChange = () => resetPageAndLoad()
const clearTags = () => {
  selectedTagIds.value = []
  resetPageAndLoad()
}

const changePage = async nextPage => {
  page.value = nextPage
  await loadArticles()
  archiveResultsRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

let searchTimer
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(resetPageAndLoad, 300)
})

watch(() => route.query.keyword, keyword => {
  const nextKeyword = typeof keyword === 'string' ? keyword : ''
  if (nextKeyword !== searchKeyword.value) searchKeyword.value = nextKeyword
})

watch(currentView, view => localStorage.setItem(ARCHIVE_VIEW_KEY, view))
onMounted(() => Promise.all([loadArticles(), loadFilterOptions()]))
onUnmounted(() => clearTimeout(searchTimer))
</script>

<template>
  <main class="archive-page">
    <header class="archive-header" aria-labelledby="archive-title">
      <div class="archive-heading">
        <h1 id="archive-title">{{ t('archive.title') }}</h1>
        <div class="archive-meta">
          <span class="archive-rule" aria-hidden="true"></span>
          <p class="article-count" :aria-label="t('archive.total', { count: total })">
            {{ t('archive.total', { count: total }) }}
          </p>
        </div>
      </div>
    </header>

    <label class="archive-search">
      <i class="ti ti-search" aria-hidden="true"></i>
      <span class="sr-only">{{ t('archive.search') }}</span>
      <input v-model="searchKeyword" type="search" :placeholder="t('archive.searchPlaceholder')" />
      <button v-if="searchKeyword" type="button" class="clear-search" :aria-label="t('archive.clearSearch')" @click="searchKeyword = ''">
        <i class="ti ti-x" aria-hidden="true"></i>
      </button>
    </label>

    <GlassPanel class="filter-toolbar" :aria-label="t('archive.categoryFilter')">
      <div class="category-filter">
        <button type="button" :class="{ active: selectedCategoryId === null }" @click="selectCategory(null)">
          {{ t('archive.allCategories') }}
        </button>
        <button v-for="category in categories" :key="category.id" type="button" :class="{ active: selectedCategoryId === category.id }" @click="selectCategory(category.id)">
          {{ category.name }}
        </button>
      </div>

      <div class="filter-actions">
        <div class="tag-filter-control">
          <i class="ti ti-tags" aria-hidden="true"></i>
          <el-select
            v-model="selectedTagIds"
            multiple
            filterable
            clearable
            collapse-tags
            collapse-tags-tooltip
            :max-collapse-tags="1"
            :loading="tagsLoading"
            :placeholder="t('tagFilter.placeholder')"
            class="tag-filter-select"
            @change="handleTagFilterChange"
          >
            <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
          <button v-if="selectedTagIds.length" type="button" class="clear-tags" :aria-label="t('tagFilter.clear')" @click="clearTags">
            <i class="ti ti-x" aria-hidden="true"></i>
          </button>
        </div>

        <div class="view-switch" role="group" :aria-label="t('archive.viewMode')">
          <button type="button" :class="{ active: currentView === 'timeline' }" :aria-pressed="currentView === 'timeline'" @click="currentView = 'timeline'">
            <i class="ti ti-timeline" aria-hidden="true"></i><span>{{ t('archive.timeline') }}</span>
          </button>
          <button type="button" :class="{ active: currentView === 'grid' }" :aria-pressed="currentView === 'grid'" @click="currentView = 'grid'">
            <i class="ti ti-layout-grid" aria-hidden="true"></i><span>{{ t('archive.grid') }}</span>
          </button>
        </div>
      </div>
    </GlassPanel>

    <div class="result-meta">
      <span aria-live="polite">{{ t('archive.showing', { count: filteredArticles.length }) }}</span>
      <span v-if="selectedTagIds.length">{{ t('tagFilter.selected', { count: selectedTagIds.length }) }}</span>
    </div>

    <section ref="archiveResultsRef" class="archive-results" :aria-busy="isLoading">
      <div v-if="isLoading" class="archive-state glass-panel">
        <span class="loading-orbit" aria-hidden="true"></span>
        <p>{{ t('common.loading') }}</p>
      </div>

      <div v-else-if="loadFailed" class="archive-state glass-panel">
        <i class="ti ti-cloud-off" aria-hidden="true"></i>
        <h2>{{ t('archive.loadFailed') }}</h2>
        <button type="button" class="retry-button" @click="loadArticles">{{ t('common.retry') }}</button>
      </div>

      <div v-else-if="filteredArticles.length === 0" class="archive-state empty-state glass-panel">
        <span class="empty-sparkle" aria-hidden="true">✦</span>
        <i class="ti ti-notes-off" aria-hidden="true"></i>
        <h2>{{ t('tagFilter.empty') }}</h2>
        <p>{{ t('archive.emptyHint') }}</p>
      </div>

      <ArchiveTimeline v-else-if="currentView === 'timeline'" :articles="filteredArticles" />
      <ArchiveGrid v-else :articles="filteredArticles" />
    </section>

    <el-pagination
      v-if="!isLoading && !loadFailed && total > PAGE_SIZE"
      class="archive-pagination"
      layout="prev, pager, next"
      :current-page="page"
      :page-size="PAGE_SIZE"
      :total="total"
      @current-change="changePage"
    />
  </main>
</template>

<style scoped>
.archive-page {
  width: min(1400px, calc(100% - 40px));
  margin: 0 auto;
  padding: 42px 0 72px;
  overflow: clip;
}

.archive-toolbar { display: flex; align-items: center; gap: 14px; margin-top: 30px; }
.archive-search {
  display: flex;
  align-items: center;
  flex: 1 1 420px;
  min-width: 0;
  height: 46px;
  padding: 0 15px;
  background: rgba(255,255,255,.26);
  border: 1px solid var(--border);
  border-radius: 15px;
  color: var(--text-3);
  transition: border-color .2s ease, box-shadow .2s ease, background-color .2s ease;
}
.archive-search:focus-within { border-color: var(--purple-200); box-shadow: 0 0 0 4px rgba(127,119,221,.1); }
.archive-search i { flex: 0 0 auto; font-size: 18px; }
.archive-search input {
  width: 100%;
  min-width: 0;
  height: 100%;
  padding: 0 10px;
  background: transparent !important;
  border: 0;
  outline: 0;
  color: var(--text-1);
  font: inherit;
}
.archive-search input::placeholder { color: var(--text-3); }
.clear-search { display: grid; place-items: center; flex: 0 0 auto; width: 28px; height: 28px; border: 0; border-radius: 50%; background: var(--purple-50); color: var(--purple-600); cursor: pointer; }
.view-switch { display: grid; grid-template-columns: 1fr 1fr; flex: 0 0 auto; padding: 4px; background: rgba(255,255,255,.2); border: 1px solid var(--glass-border); border-radius: 15px; }
.view-switch button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-width: 100px;
  height: 36px;
  padding: 0 12px;
  border: 0;
  border-radius: 11px;
  background: transparent;
  color: var(--text-2);
  font: inherit;
  font-size: 12px;
  cursor: pointer;
  transition: .2s ease;
}
.view-switch button.active { background: linear-gradient(135deg, var(--purple-400), var(--purple-600)); color: #fff; box-shadow: 0 5px 14px rgba(83,74,183,.25); }
.tag-filter-panel {
  position: relative; z-index: 1; display: flex; align-items: center; justify-content: space-between;
  gap: 16px; margin-top: 18px; padding: 12px 14px; background: rgba(255,255,255,.16);
  border: 1px solid var(--border); border-radius: 16px;
}
.tag-filter-heading { display: flex; flex-direction: column; flex: 0 0 auto; color: var(--text-2); }
.tag-filter-heading > span { display: inline-flex; align-items: center; gap: 6px; font-size: 12px; font-weight: 650; }
.tag-filter-heading i { color: var(--pink-400); font-size: 16px; }
.tag-filter-heading small { margin-top: 2px; color: var(--text-3); font-size: 9px; }
.tag-filter-controls { display: flex; align-items: center; justify-content: flex-end; gap: 8px; flex: 1 1 auto; min-width: 0; }
.tag-filter-select { width: min(100%, 520px); }
.tag-filter-select :deep(.el-select__wrapper) {
  min-height: 38px; background: rgba(255,255,255,.65); border-radius: 12px;
  box-shadow: 0 0 0 1px var(--border) inset !important;
}
.tag-filter-select :deep(.el-tag) { max-width: 150px; background: var(--pink-50); border-color: rgba(212,83,126,.22); color: var(--pink-600); }
.clear-tags { flex: 0 0 auto; padding: 6px 10px; background: transparent; border: 0; color: var(--purple-600); font-size: 10px; cursor: pointer; }
.clear-tags:hover { text-decoration: underline; }
.category-filter { display: flex; gap: 8px; margin-top: 18px; padding-bottom: 3px; overflow-x: auto; scrollbar-width: none; }
.category-filter::-webkit-scrollbar { display: none; }
.category-filter button {
  flex: 0 0 auto;
  max-width: 220px;
  padding: 6px 14px;
  overflow: hidden;
  background: rgba(255,255,255,.18);
  border: 1px solid var(--border);
  border-radius: 999px;
  color: var(--text-2);
  font: inherit;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  transition: .2s ease;
}
.category-filter button:hover { color: var(--purple-600); border-color: var(--purple-200); }
.category-filter button.active { background: var(--purple-50); border-color: var(--purple-200); color: var(--purple-600); font-weight: 600; }
.result-count { margin: 12px 2px 0; color: var(--text-3); font-size: 11px; }
.archive-results { margin-top: 34px; }
.archive-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  padding: 48px 24px;
  text-align: center;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 24px;
  box-shadow: 0 12px 34px var(--shadow-color);
  backdrop-filter: blur(14px);
}
.archive-state > i { color: var(--purple-400); font-size: 42px; }
.archive-state h2 { margin: 12px 0 0; color: var(--text-1); font-size: 18px; }
.archive-state p { margin: 6px 0 0; color: var(--text-3); font-size: 13px; }
.empty-state { position: relative; overflow: hidden; }
.empty-sparkle { position: absolute; top: 30px; right: 12%; color: var(--pink-400); font-size: 30px; opacity: .7; }
.retry-button { margin-top: 18px; padding: 8px 20px; border: 0; border-radius: 12px; background: var(--purple-400); color: #fff; cursor: pointer; }
.loading-orbit { width: 34px; height: 34px; border: 3px solid var(--purple-100); border-top-color: var(--purple-600); border-radius: 50%; animation: spin .8s linear infinite; }
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; overflow: hidden; clip: rect(0,0,0,0); white-space: nowrap; border: 0; }
@keyframes spin { to { transform: rotate(360deg); } }

:global(html[data-theme='dark']) .archive-search,
:global(html[data-theme='dark']) .view-switch,
:global(html[data-theme='dark']) .category-filter button,
:global(html[data-theme='dark']) .tag-filter-panel { background: rgba(17,21,34,.54); }
:global(html[data-theme='dark']) .tag-filter-select :deep(.el-select__wrapper) { background: rgba(25,30,46,.88); }

@media (max-width: 767px) {
  .archive-page { width: min(100% - 24px, 1400px); padding: 24px 0 54px; }
  .archive-toolbar { align-items: stretch; flex-direction: column; margin-top: 22px; }
  .archive-search { flex-basis: 44px; width: 100%; }
  .view-switch { width: 100%; }
  .view-switch button { width: 100%; min-width: 0; }
  .tag-filter-panel { align-items: stretch; flex-direction: column; gap: 8px; }
  .tag-filter-heading { flex-direction: row; align-items: center; justify-content: space-between; gap: 12px; }
  .tag-filter-controls { width: 100%; }
  .tag-filter-select { width: 100%; }
  .archive-results { margin-top: 24px; }
}
@media (prefers-reduced-motion: reduce) { .loading-orbit { animation: none; } }

/* 轻量中央内容岛：以下规则覆盖归档页旧版大面板尺寸。 */
.archive-page {
  --archive-gap: 16px;
  --archive-text-primary: #111827;
  --archive-text-secondary: rgba(17,24,39,.76);
  --archive-text-muted: rgba(17,24,39,.58);
  --text-1: var(--archive-text-primary);
  --text-2: var(--archive-text-secondary);
  --text-3: var(--archive-text-muted);
  width: min(960px, calc(100vw - 40px));
  padding: 28px 0 56px;
  overflow: hidden;
}
.archive-header {
  position: relative;
  isolation: isolate;
  display: grid;
  align-items: center;
  min-height: clamp(280px, 32vw, 340px);
  padding: clamp(42px, 7vw, 72px);
  overflow: hidden;
  background:
    linear-gradient(90deg, rgba(8,11,19,.86) 0%, rgba(8,11,19,.62) 46%, rgba(8,11,19,.18) 100%),
    linear-gradient(0deg, rgba(8,11,19,.32), transparent 56%);
  border: 1px solid rgba(255,255,255,.14);
  border-radius: 26px;
  box-shadow: 0 18px 44px rgba(5,8,16,.18);
}
.archive-header::after {
  position: absolute;
  right: 7%;
  bottom: -82px;
  z-index: 0;
  width: 176px;
  height: 176px;
  border: 1px solid rgba(244,238,217,.36);
  border-left-color: transparent;
  border-radius: 50%;
  content: '';
  pointer-events: none;
}
.archive-heading {
  position: relative;
  z-index: 1;
  width: min(100%, 620px);
  padding-left: clamp(28px, 4vw, 46px);
}
.archive-heading::before {
  position: absolute;
  top: .1em;
  bottom: .2em;
  left: 0;
  width: 1px;
  background: rgba(255,255,255,.48);
  content: '';
}
.archive-heading::after {
  position: absolute;
  top: .1em;
  left: -4px;
  width: 9px;
  height: 28px;
  background: #d8b562;
  content: '';
}
.archive-heading h1 {
  margin: 0;
  color: #f7f5ef;
  font-size: clamp(64px, 8.5vw, 88px);
  font-weight: 800;
  line-height: .96;
  letter-spacing: -.065em;
}
.archive-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 26px;
}
.archive-rule {
  width: clamp(54px, 8vw, 84px);
  height: 1px;
  background: rgba(255,255,255,.44);
}
.article-count {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  margin: 0;
  padding: 0;
  background: transparent;
  border: 0;
  color: rgba(247,245,239,.82);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: .08em;
  white-space: nowrap;
}
.article-count::before {
  width: 6px;
  height: 6px;
  background: #d8b562;
  border-radius: 50%;
  content: '';
}
.archive-search {
  width: min(620px, 100%);
  height: 44px;
  margin: 18px auto 12px;
  padding: 0 13px;
  background: rgba(255,255,255,.24);
  border-color: rgba(255,255,255,.28);
  border-radius: 14px;
  box-shadow: 0 8px 22px rgba(20,18,34,.08);
  -webkit-backdrop-filter: blur(14px);
  backdrop-filter: blur(14px);
}
.archive-search i { font-size: 16px; }
.archive-search input { font-size: 13px; }
.clear-search { width: 25px; height: 25px; background: rgba(127,119,221,.12); }
.filter-toolbar {
  width:min(900px,100%);
  margin-inline:auto;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: 12px;
  padding: 9px 11px;
  background: rgba(255,255,255,.18);
  border-color: rgba(255,255,255,.24);
  border-radius: 18px;
  box-shadow: 0 9px 24px rgba(20,18,34,.09);
}
.category-filter { min-width: 0; gap: 6px; margin: 0; padding: 0 2px 0 0; }
.category-filter button { height: 30px; padding: 0 12px; background: rgba(255,255,255,.12); border-color: rgba(255,255,255,.2); font-size: 11px; }
.category-filter button.active { background: rgba(127,119,221,.14); border-color: var(--purple-200); box-shadow: inset 0 0 0 1px rgba(127,119,221,.05); }
.filter-actions { display: flex; align-items: center; gap: 8px; flex: 0 0 auto; }
.tag-filter-control { position: relative; display: flex; align-items: center; gap: 5px; }
.tag-filter-control > i { color: var(--pink-400); font-size: 14px; }
.tag-filter-select { width: 180px; }
.tag-filter-select :deep(.el-select__wrapper) { min-height: 34px; padding: 3px 28px 3px 10px; background: rgba(255,255,255,.18); border-radius: 10px; box-shadow: 0 0 0 1px rgba(255,255,255,.2) inset !important; }
.tag-filter-select :deep(.el-select__selected-item),
.tag-filter-select :deep(.el-select__placeholder) { color:var(--archive-text-primary); }
.tag-filter-select :deep(.el-tag) { max-width: 94px; }
.clear-tags { position: absolute; z-index: 2; right: 7px; display: grid; place-items: center; width: 20px; height: 20px; padding: 0; border-radius: 50%; background: rgba(127,119,221,.1); }
.view-switch { height: 34px; padding: 3px; border-radius: 11px; background: rgba(255,255,255,.12); }
.view-switch button { min-width: 72px; height: 26px; padding: 0 8px; border-radius: 8px; font-size: 10px; }
.view-switch button.active { background: rgba(127,119,221,.18); color: var(--purple-600); box-shadow: none; }
.result-meta { display:flex; justify-content:space-between; gap:12px; width:min(900px,100%); margin:8px auto 0; color:var(--archive-text-secondary); font-size:10px; }
.archive-results { scroll-margin-top: calc(var(--nav-h) + 16px); margin-top: 14px; }
.archive-state { min-height: 220px; padding: 32px 20px; border-radius: 18px; box-shadow: 0 9px 24px rgba(20,18,34,.1); }
.archive-pagination { justify-content: center; margin-top: 22px; }
.archive-pagination :deep(.el-pager li),
.archive-pagination :deep(button) { background: rgba(255,255,255,.2) !important; border: 1px solid rgba(255,255,255,.22); color: var(--text-2); -webkit-backdrop-filter: blur(10px); backdrop-filter: blur(10px); }
.archive-pagination :deep(.el-pager li.is-active) { background: rgba(127,119,221,.72) !important; border-color: rgba(175,169,236,.7); color: #fff; }
:global(html[data-theme='dark']) .archive-page { --archive-text-primary:#f4f5fb; --archive-text-secondary:rgba(244,245,251,.82); --archive-text-muted:rgba(244,245,251,.66); }
:global(html[data-theme='dark']) .archive-search,
:global(html[data-theme='dark']) .filter-toolbar { background: rgba(24,24,34,.3); border-color: rgba(255,255,255,.12); }
:global(html[data-theme='dark']) .category-filter button,
:global(html[data-theme='dark']) .view-switch { background: rgba(20,21,31,.22); }
:global(html[data-theme='dark']) .tag-filter-select :deep(.el-select__wrapper) { background: rgba(20,21,31,.28); box-shadow: 0 0 0 1px rgba(255,255,255,.12) inset !important; }

@media (max-width: 780px) {
  .archive-page { width: min(100% - 24px, 960px); padding: 22px 0 48px; }
  .archive-header { min-height: 250px; padding: 38px 34px; border-radius: 22px; }
  .archive-heading h1 { font-size: clamp(54px, 11vw, 68px); }
  .filter-toolbar { display: block; }
  .filter-actions { justify-content: space-between; margin-top: 8px; padding-top: 8px; border-top: 1px solid rgba(255,255,255,.16); }
  .tag-filter-select { width: min(240px, 46vw); }
  .archive-results { margin-top: 12px; }
}
@media (max-width: 520px) {
  .archive-page { width: calc(100% - 20px); padding-top: 16px; }
  .archive-header { min-height: 210px; padding: 30px 24px; border-radius: 18px; }
  .archive-header::after { display: none; }
  .archive-heading { padding-left: 24px; }
  .archive-heading::after { height: 22px; }
  .archive-heading h1 { font-size: clamp(44px, 14vw, 56px); }
  .archive-meta { gap: 10px; margin-top: 20px; }
  .archive-rule { width: 40px; }
  .article-count { font-size: 11px; letter-spacing: .04em; }
  .archive-search { width: 100%; margin-top: 10px; }
  .filter-actions { align-items: stretch; flex-direction: column; }
  .tag-filter-control { width: 100%; }
  .tag-filter-select { width: 100%; }
  .view-switch { align-self: flex-end; width: auto; }
  .view-switch button { min-width: 74px; }
  .result-meta { margin-top: 7px; }
}
</style>
