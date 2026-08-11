<script setup>
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { articleListService } from '@/api/article.js'
import { getCategoryList } from '@/api/category.js'

const NOTES_CATEGORY_SLUG = 'notes'
const PAGE_SIZE = 10

const { locale, t } = useI18n()
const notes = ref([])
const total = ref(0)
const currentPage = ref(1)
const loading = ref(true)
const loadFailed = ref(false)
const categoryMissing = ref(false)
const categoryChecked = ref(false)
const failedCoverIds = ref(new Set())
let requestSequence = 0

const normalizeNote = article => ({
  ...article,
  categoryName: article.categoryName || article.category || '',
  createTime: article.createTime || article.publishTime || article.publishedAt || '',
  coverImage: article.coverImage || article.coverUrl || '',
  viewCount: Number(article.viewCount ?? 0),
  tags: Array.isArray(article.tags) ? article.tags : []
})

const toDate = value => {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? null : date
}

const formatMonth = value => {
  const date = toDate(value)
  return date
    ? new Intl.DateTimeFormat(locale.value, { year: 'numeric', month: 'long' }).format(date)
    : '—'
}

const formatDate = value => {
  const date = toDate(value)
  return date
    ? new Intl.DateTimeFormat(locale.value, { year: 'numeric', month: '2-digit', day: '2-digit' }).format(date)
    : '—'
}

// 手记仍是 Article，只在这个专属视图中按年月组织，营造轻量时间流而非复制归档页。
const noteGroups = computed(() => {
  const groups = []
  const groupMap = new Map()

  notes.value.forEach(note => {
    const date = toDate(note.createTime)
    const key = date ? `${date.getFullYear()}-${date.getMonth() + 1}` : 'unknown'
    let group = groupMap.get(key)
    if (!group) {
      group = { key, label: formatMonth(note.createTime), items: [] }
      groupMap.set(key, group)
      groups.push(group)
    }
    group.items.push(note)
  })

  return groups
})

const visibleTags = note => note.tags.slice(0, 3)
const hasCover = note => Boolean(note.coverImage?.trim()) && !failedCoverIds.value.has(note.id)
const handleCoverError = noteId => {
  failedCoverIds.value = new Set([...failedCoverIds.value, noteId])
}

async function resolveNotesCategory() {
  const response = await getCategoryList()
  const categories = response?.data
  if (!Array.isArray(categories)) {
    throw new Error('分类接口响应结构无效')
  }

  // 使用后端提供的稳定 slug，而不是写死各环境可能不同的 categoryId 或用中文名称匹配。
  const notesCategory = categories.find(
    category => category.slug?.trim().toLowerCase() === NOTES_CATEGORY_SLUG
  )
  categoryChecked.value = true
  categoryMissing.value = !notesCategory
}

async function loadNotes() {
  const sequence = ++requestSequence
  loading.value = true
  loadFailed.value = false

  try {
    if (!categoryChecked.value) await resolveNotesCategory()
    if (categoryMissing.value) {
      notes.value = []
      total.value = 0
      return
    }

    // 复用现有文章服务端分页，草稿过滤和分类过滤均由 Article 查询完成。
    const response = await articleListService({
      page: currentPage.value,
      size: PAGE_SIZE,
      categorySlug: NOTES_CATEGORY_SLUG
    })
    const payload = response?.data
    if (!payload || !Array.isArray(payload.records) || !Number.isFinite(Number(payload.total))) {
      throw new Error('文章接口响应结构无效')
    }
    const records = payload.records
    if (sequence !== requestSequence) return

    notes.value = records.map(normalizeNote)
    total.value = Number(payload.total ?? records.length)
  } catch (error) {
    if (sequence !== requestSequence) return
    console.error('加载手记失败：', error)
    notes.value = []
    total.value = 0
    loadFailed.value = true
    ElMessage.error(t('notes.loadFailed'))
  } finally {
    if (sequence === requestSequence) loading.value = false
  }
}

function changePage(page) {
  currentPage.value = page
  loadNotes()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(loadNotes)
</script>

<template>
  <main class="notes-page">
    <header class="notes-hero" aria-labelledby="notes-title">
      <div class="notes-hero-copy">
        <h1 id="notes-title">{{ t('notes.title') }}</h1>
      </div>
      <div class="bauhaus-mark" aria-hidden="true">
        <span></span><i></i><b></b>
      </div>
    </header>

    <section class="notes-content" :aria-busy="loading">
      <div v-if="loading" class="notes-state glass-state">
        <span class="notes-loader" aria-hidden="true"></span>
        <p>{{ t('common.loading') }}</p>
      </div>

      <div v-else-if="loadFailed" class="notes-state glass-state">
        <i class="ti ti-cloud-off" aria-hidden="true"></i>
        <h2>{{ t('notes.loadFailed') }}</h2>
        <button type="button" @click="loadNotes">{{ t('common.retry') }}</button>
      </div>

      <!-- 分类缺失与分类存在但文章为空是两种状态，避免给管理员错误的初始化提示。 -->
      <div v-else-if="categoryMissing" class="notes-state glass-state">
        <i class="ti ti-notebook-off" aria-hidden="true"></i>
        <h2>{{ t('notes.categoryMissing') }}</h2>
      </div>

      <div v-else-if="notes.length === 0" class="notes-state glass-state">
        <i class="ti ti-notebook-off" aria-hidden="true"></i>
        <h2>{{ t('notes.empty') }}</h2>
      </div>

      <div v-else class="notes-timeline">
        <section v-for="group in noteGroups" :key="group.key" class="notes-month">
          <div class="month-marker">
            <span>{{ group.label }}</span>
            <i aria-hidden="true"></i>
          </div>

          <div class="month-notes">
            <article v-for="note in group.items" :key="note.id" class="note-entry">
              <router-link
                :to="`/post/${note.id}`"
                class="note-card"
                :class="{ 'has-cover': hasCover(note) }"
              >
                <div class="note-copy">
                  <div class="note-meta">
                    <time :datetime="note.createTime">{{ t('notes.published') }} {{ formatDate(note.createTime) }}</time>
                    <span>{{ note.categoryName }}</span>
                  </div>
                  <h2>{{ note.title }}</h2>
                  <p>{{ note.summary || t('article.noSummary') }}</p>

                  <div class="note-footer">
                    <div v-if="visibleTags(note).length" class="note-tags">
                      <span v-for="tag in visibleTags(note)" :key="tag.id">#{{ tag.name }}</span>
                    </div>
                    <div class="note-actions">
                      <span><i class="ti ti-eye" aria-hidden="true"></i>{{ t('notes.views') }} {{ note.viewCount }}</span>
                      <strong>{{ t('notes.readMore') }} <i class="ti ti-arrow-up-right" aria-hidden="true"></i></strong>
                    </div>
                  </div>
                </div>

                <!-- 无封面时不创建媒体列，正文会自然占满卡片。 -->
                <div v-if="hasCover(note)" class="note-cover">
                  <img
                    :src="note.coverImage"
                    :alt="note.title"
                    loading="lazy"
                    decoding="async"
                    @error="handleCoverError(note.id)"
                  />
                </div>
              </router-link>
            </article>
          </div>
        </section>
      </div>

      <el-pagination
        v-if="!loading && !loadFailed && total > PAGE_SIZE"
        class="notes-pagination"
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="PAGE_SIZE"
        :total="total"
        @current-change="changePage"
      />
    </section>
  </main>
</template>

<style scoped>
.notes-page { width:min(1080px,calc(100% - 40px)); min-height:calc(100vh - var(--nav-h)); margin:0 auto; padding:44px 0 76px; }
.notes-hero { position:relative; display:flex; align-items:center; justify-content:space-between; min-height:190px; padding:32px clamp(40px,5vw,56px); overflow:hidden; background:linear-gradient(135deg,color-mix(in srgb,var(--bg-card) 62%,transparent),color-mix(in srgb,var(--bg-card) 48%,transparent)); border:1px solid color-mix(in srgb,var(--text-1) 10%,transparent); border-radius:30px; box-shadow:0 14px 34px color-mix(in srgb,var(--shadow-color) 38%,transparent); -webkit-backdrop-filter:blur(14px) saturate(106%); backdrop-filter:blur(14px) saturate(106%); }
.notes-hero::after { position:absolute; right:18%; bottom:12%; width:1px; height:62%; content:''; background:color-mix(in srgb,var(--text-2) 34%,transparent); }
.notes-hero-copy { position:relative; z-index:1; max-width:680px; }
.notes-hero h1 { margin:0; color:color-mix(in srgb,var(--text-1) 92%,transparent); font-size:clamp(64px,7vw,82px); font-weight:760; letter-spacing:-.055em; line-height:.96; }
.bauhaus-mark { position:relative; z-index:1; width:88px; height:88px; flex:0 0 auto; margin-right:4px; opacity:.82; }
.bauhaus-mark span,.bauhaus-mark i,.bauhaus-mark b { position:absolute; display:block; }
.bauhaus-mark span { top:0; right:0; width:50px; height:50px; border:1px solid color-mix(in srgb,var(--text-2) 54%,transparent); border-radius:50%; }
.bauhaus-mark i { right:40px; bottom:0; width:38px; height:38px; background:color-mix(in srgb,var(--text-1) 28%,transparent); border:1px solid color-mix(in srgb,var(--text-2) 28%,transparent); }
.bauhaus-mark b { right:0; bottom:10px; width:28px; height:1px; background:color-mix(in srgb,var(--text-2) 52%,transparent); transform:rotate(-45deg); }
.notes-content { margin-top:34px; }
.notes-timeline { position:relative; }
.notes-month { display:grid; grid-template-columns:160px minmax(0,1fr); gap:30px; }
.month-marker { position:relative; padding-top:20px; color:var(--text-2); font-size:12px; font-weight:650; letter-spacing:.04em; text-align:right; }
.month-marker::after { position:absolute; top:0; right:-16px; bottom:0; width:1px; content:''; background:var(--border); }
.month-marker i { position:absolute; z-index:1; top:23px; right:-20px; width:9px; height:9px; background:var(--bg); border:2px solid var(--text-2); border-radius:50%; }
.month-notes { min-width:0; padding-bottom:30px; }
.note-entry+ .note-entry { margin-top:14px; }
.note-card { display:grid; min-height:176px; overflow:hidden; background:color-mix(in srgb,var(--article-card-bg) 90%,transparent); border:1px solid var(--article-card-border); border-radius:18px; box-shadow:0 7px 22px color-mix(in srgb,var(--shadow-color) 52%,transparent); backdrop-filter:blur(14px); transition:transform .22s ease,border-color .22s ease,box-shadow .22s ease; }
.note-card.has-cover { grid-template-columns:minmax(0,1fr) minmax(170px,29%); }
.note-card:hover,.note-card:focus-visible { border-color:var(--border-h); box-shadow:0 10px 26px color-mix(in srgb,var(--shadow-color) 72%,transparent); transform:translateY(-2px); outline:none; }
.note-copy { display:flex; flex-direction:column; min-width:0; padding:22px 24px; }
.note-meta { display:flex; align-items:center; gap:10px; color:var(--text-3); font-size:10px; }
.note-meta span { padding:2px 8px; border:1px solid var(--border); border-radius:999px; color:var(--text-2); }
.note-copy h2 { margin:10px 0 6px; color:var(--text-1); font-size:clamp(18px,2.3vw,24px); font-weight:620; line-height:1.35; }
.note-copy>p { display:-webkit-box; overflow:hidden; color:var(--text-2); font-size:12px; line-height:1.75; -webkit-box-orient:vertical; -webkit-line-clamp:3; }
.note-footer { display:flex; align-items:flex-end; justify-content:space-between; gap:16px; margin-top:auto; padding-top:16px; }
.note-tags { display:flex; min-width:0; gap:6px; overflow:hidden; }
.note-tags span { max-width:100px; overflow:hidden; color:var(--text-3); font-size:9px; text-overflow:ellipsis; white-space:nowrap; }
.note-actions { display:flex; align-items:center; gap:16px; flex:0 0 auto; color:var(--text-3); font-size:10px; }
.note-actions span,.note-actions strong { display:inline-flex; align-items:center; gap:4px; }
.note-actions strong { color:var(--text-1); font-weight:600; }
.note-cover { min-width:0; overflow:hidden; border-left:1px solid var(--border); }
.note-cover img { display:block; width:100%; height:100%; min-height:176px; object-fit:cover; transition:transform .3s ease; }
.note-card:hover .note-cover img { transform:scale(1.025); }
.glass-state { display:flex; flex-direction:column; align-items:center; justify-content:center; min-height:300px; padding:36px; background:var(--article-card-bg); border:1px solid var(--article-card-border); border-radius:20px; color:var(--text-2); text-align:center; backdrop-filter:blur(14px); }
.notes-state>i { color:var(--text-2); font-size:36px; }
.notes-state h2 { margin:10px 0 0; color:var(--text-1); font-size:17px; }
.notes-state p { margin:6px 0 0; color:var(--text-3); font-size:12px; }
.notes-state button { margin-top:16px; padding:7px 18px; border:1px solid var(--border-h); border-radius:999px; background:transparent; color:var(--text-1); cursor:pointer; }
.notes-loader { width:30px; height:30px; margin-bottom:12px; border:2px solid var(--border); border-top-color:var(--text-1); border-radius:50%; animation:notes-spin .8s linear infinite; }
.notes-pagination { justify-content:center; margin-top:8px; }
@keyframes notes-spin { to { transform:rotate(360deg); } }
@media(max-width:767px){
  .notes-page{width:calc(100% - 24px);padding:24px 0 54px}.notes-hero{min-height:168px;padding:28px 30px;border-radius:24px}.notes-hero h1{font-size:clamp(52px,11vw,68px)}.bauhaus-mark{width:68px;height:68px;margin-right:0;opacity:.64}.bauhaus-mark span{width:40px;height:40px}.bauhaus-mark i{right:32px;width:29px;height:29px}.notes-content{margin-top:24px}.notes-month{display:block}.month-marker{display:flex;align-items:center;gap:10px;padding:0 2px 10px;text-align:left}.month-marker::after{display:none}.month-marker i{position:static;width:7px;height:7px;order:-1}.month-notes{padding-bottom:24px}.note-card.has-cover{grid-template-columns:minmax(0,1fr) 150px}.note-copy{padding:18px}.note-footer{align-items:flex-start;flex-direction:column;gap:10px}.note-actions{width:100%;justify-content:space-between}
}
@media(max-width:520px){
  .notes-hero{min-height:148px;padding:24px;border-radius:22px}.notes-hero::after{display:none}.notes-hero h1{font-size:clamp(46px,14vw,58px)}.bauhaus-mark{position:absolute;right:18px;top:18px;transform:scale(.82);transform-origin:top right;opacity:.28}.note-card.has-cover{grid-template-columns:1fr}.note-cover{grid-row:1;height:150px;border-bottom:1px solid var(--border);border-left:0}.note-cover img{min-height:0}.note-meta{align-items:flex-start;flex-direction:column;gap:5px}.note-copy h2{font-size:19px}
}
@media(prefers-reduced-motion:reduce){.note-card,.note-cover img{transition:none}.notes-loader{animation:none}}
</style>
