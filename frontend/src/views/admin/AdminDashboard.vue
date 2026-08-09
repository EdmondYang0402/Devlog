<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import { deleteAdminArticle, getAdminArticlePage } from '@/api/article.js'
import { getAdminMediaReviewPage } from '@/api/mediaReview.js'
import { adminSiteProfileService } from '@/api/site.js'
import { profileStatisticsService } from '@/api/statistics.js'
import AdminIcon from '@/components/admin/AdminIcon.vue'
import EmptyState from '@/components/admin/EmptyState.vue'

const router = useRouter()
const loading = ref(true)
const articles = ref([])
const counts = ref({ articles: '--', comments: '--', categories: '--', works: '--' })
const siteProfile = ref(null)

const siteDescription = computed(() => siteProfile.value?.heroSubtitle || '暂无数据')

const formatDate = value => {
  if (!value) return '暂无数据'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '暂无数据'
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit'
  }).format(date)
}

const formatArticleDate = value => {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return new Intl.DateTimeFormat('zh-CN', {
    month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
  }).format(date)
}

const runningTime = computed(() => {
  const createdAt = siteProfile.value?.createTime
  if (!createdAt) return '暂无数据'
  const start = new Date(createdAt)
  const now = new Date()
  if (Number.isNaN(start.getTime()) || start > now) return '暂无数据'
  const days = Math.floor((now.getTime() - start.getTime()) / 86400000)
  return days > 0 ? `${days} 天` : '不足 1 天'
})

const applyArticlePage = response => {
  const pageData = response?.data
  articles.value = Array.isArray(pageData?.records) ? pageData.records : []
  counts.value.articles = Number(pageData?.total || 0)
}

const loadArticles = async () => {
  const response = await getAdminArticlePage({ page: 1, size: 6 })
  applyArticlePage(response)
}

const loadDashboard = async () => {
  loading.value = true
  const [articleResult, statisticsResult, worksResult, siteResult] = await Promise.allSettled([
    getAdminArticlePage({ page: 1, size: 6 }),
    profileStatisticsService(),
    getAdminMediaReviewPage({ page: 1, size: 1 }),
    adminSiteProfileService()
  ])

  if (articleResult.status === 'fulfilled') {
    applyArticlePage(articleResult.value)
  } else {
    console.error('仪表盘文章加载失败', articleResult.reason)
    articles.value = []
    counts.value.articles = '--'
  }

  if (statisticsResult.status === 'fulfilled') {
    counts.value.comments = statisticsResult.value?.data?.commentCount ?? '--'
    counts.value.categories = statisticsResult.value?.data?.categoryCount ?? '--'
  } else {
    console.error('仪表盘统计加载失败', statisticsResult.reason)
    counts.value.comments = '--'
    counts.value.categories = '--'
  }

  if (worksResult.status === 'fulfilled') {
    counts.value.works = Number(worksResult.value?.data?.total || 0)
  } else {
    console.error('仪表盘作品统计加载失败', worksResult.reason)
    counts.value.works = '--'
  }

  if (siteResult.status === 'fulfilled') {
    siteProfile.value = siteResult.value?.data || null
  } else {
    console.error('站点信息加载失败', siteResult.reason)
    siteProfile.value = null
  }

  loading.value = false
}

const removeArticle = async article => {
  try {
    await ElMessageBox.confirm(`确定删除“${article.title}”吗？`, '删除文章', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
    await deleteAdminArticle(article.id)
    await loadArticles()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error?.response?.data?.message || '删除文章失败')
    }
  }
}

onMounted(loadDashboard)
</script>

<template>
  <div class="admin-dashboard">
    <section class="stats-grid" aria-label="站点统计">
      <article class="metric-card metric-card--article">
        <div class="metric-icon"><AdminIcon name="ti-article" /></div>
        <div class="metric-copy"><span>文章总数</span><strong>{{ loading ? '—' : counts.articles }}</strong></div>
      </article>
      <article class="metric-card metric-card--comment">
        <div class="metric-icon"><AdminIcon name="ti-message-circle" /></div>
        <div class="metric-copy"><span>评论总数</span><strong>{{ loading ? '—' : counts.comments }}</strong></div>
      </article>
      <article class="metric-card metric-card--category">
        <div class="metric-icon"><AdminIcon name="ti-folders" /></div>
        <div class="metric-copy"><span>分类总数</span><strong>{{ loading ? '—' : counts.categories }}</strong></div>
      </article>
      <article class="metric-card metric-card--works">
        <div class="metric-icon"><AdminIcon name="ti-books" /></div>
        <div class="metric-copy"><span>作品总数</span><strong>{{ loading ? '—' : counts.works }}</strong></div>
      </article>
    </section>

    <section class="dashboard-main-grid">
      <section class="dashboard-panel recent-panel">
        <header class="panel-header">
          <div>
            <span class="panel-eyebrow">LATEST CONTENT</span>
            <h2>最近文章</h2>
          </div>
          <router-link class="text-link" to="/admin/articles">查看全部<AdminIcon name="ti-arrow-right" /></router-link>
        </header>

        <div v-if="loading" class="dashboard-loading">正在读取文章…</div>
        <div v-else-if="articles.length" class="article-table-wrap">
          <table class="dashboard-table">
            <thead><tr><th>文章</th><th>分类</th><th>更新时间</th><th><span class="sr-only">操作</span></th></tr></thead>
            <tbody>
              <tr v-for="article in articles" :key="article.id">
                <td>
                  <div class="article-cell">
                    <div class="article-cover">
                      <img v-if="article.coverImage" :src="article.coverImage" alt="" loading="lazy" />
                      <AdminIcon v-else name="ti-photo" />
                    </div>
                    <strong :title="article.title">{{ article.title }}</strong>
                  </div>
                </td>
                <td class="secondary-cell">{{ article.categoryName || article.category || '未分类' }}</td>
                <td class="secondary-cell date-cell">{{ formatArticleDate(article.updateTime || article.createTime) }}</td>
                <td>
                  <div class="row-actions">
                    <button type="button" title="编辑" aria-label="编辑文章" @click="router.push(`/admin/articles/edit/${article.id}`)"><AdminIcon name="ti-pencil" /></button>
                    <button type="button" title="查看" aria-label="查看文章" @click="router.push(`/post/${article.id}`)"><AdminIcon name="ti-eye" /></button>
                    <button type="button" class="danger" title="删除" aria-label="删除文章" @click="removeArticle(article)"><AdminIcon name="ti-trash" /></button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <EmptyState v-else title="还没有文章" description="发布第一篇文章后，它会出现在这里。" icon="ti-article-off" />
      </section>

      <aside class="dashboard-panel site-panel">
        <header class="panel-header">
          <div>
            <span class="panel-eyebrow">SITE PROFILE</span>
            <h2>站点信息</h2>
          </div>
        </header>

        <dl class="site-info">
          <div><dt>站点名称</dt><dd>{{ siteProfile?.siteTitle || '暂无数据' }}</dd></div>
          <div class="site-info-description"><dt>站点描述</dt><dd>{{ siteDescription }}</dd></div>
          <div><dt>创建时间</dt><dd>{{ formatDate(siteProfile?.createTime) }}</dd></div>
          <div><dt>运行时间</dt><dd>{{ runningTime }}</dd></div>
          <div><dt>文章总数</dt><dd>{{ counts.articles }}</dd></div>
          <div><dt>评论总数</dt><dd>{{ counts.comments }}</dd></div>
          <div><dt>分类总数</dt><dd>{{ counts.categories }}</dd></div>
          <div><dt>作品总数</dt><dd>{{ counts.works }}</dd></div>
        </dl>

        <router-link class="visit-site" to="/home" target="_blank">
          访问站点<AdminIcon name="ti-arrow-up-right" />
        </router-link>
      </aside>
    </section>
  </div>
</template>

<style scoped>
.admin-dashboard {
  width: 100%;
  max-width: 1720px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric-card {
  --metric-tone: #a795d8;
  position: relative;
  min-height: 124px;
  display: flex;
  align-items: center;
  gap: 16px;
  overflow: hidden;
  padding: 22px;
  border: 1px solid var(--admin-border);
  border-radius: 15px;
  background: rgba(25, 24, 43, .58);
  box-shadow: 0 14px 34px rgba(4, 5, 18, .12);
}

.metric-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  background: var(--metric-tone);
  opacity: .78;
}

.metric-card--comment { --metric-tone: #c58ca8; }
.metric-card--category { --metric-tone: #7f9fbd; }
.metric-card--works { --metric-tone: #c0a06d; }

.metric-icon {
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  display: grid;
  place-items: center;
  border: 1px solid color-mix(in srgb, var(--metric-tone) 34%, transparent);
  border-radius: 10px;
  background: color-mix(in srgb, var(--metric-tone) 10%, transparent);
  color: var(--metric-tone);
  font-size: 20px;
}

.metric-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.metric-copy span {
  color: var(--admin-text-secondary);
  font-size: 10px;
  letter-spacing: .08em;
}

.metric-copy strong {
  color: var(--admin-text-primary);
  font-size: clamp(26px, 2vw, 34px);
  font-weight: 560;
  font-variant-numeric: tabular-nums;
  line-height: 1;
}

.dashboard-main-grid {
  display: grid;
  grid-template-columns: minmax(0, 7fr) minmax(280px, 3fr);
  gap: 18px;
  align-items: stretch;
}

.dashboard-panel {
  min-width: 0;
  overflow: hidden;
  border: 1px solid var(--admin-border);
  border-radius: 15px;
  background: rgba(25, 24, 43, .58);
  box-shadow: 0 16px 38px rgba(4, 5, 18, .13);
  backdrop-filter: blur(18px);
}

.panel-header {
  min-height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 15px 20px;
  border-bottom: 1px solid var(--admin-border);
}

.panel-header > div {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.panel-eyebrow {
  color: var(--admin-text-muted);
  font-size: 8px;
  letter-spacing: .18em;
}

.panel-header h2 {
  margin: 0;
  color: var(--admin-text-primary);
  font-size: 14px;
  font-weight: 600;
  letter-spacing: .04em;
}

.text-link,
.visit-site {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: var(--admin-accent);
  font-size: 10px;
  text-decoration: none;
  transition: color .16s ease, border-color .16s ease, background .16s ease;
}

.text-link:hover { color: var(--admin-text-primary); }

.dashboard-loading {
  min-height: 390px;
  display: grid;
  place-items: center;
  color: var(--admin-text-muted);
  font-size: 11px;
}

.article-table-wrap {
  max-width: 100%;
  overflow-x: auto;
}

.dashboard-table {
  width: 100%;
  min-width: 650px;
  border-collapse: collapse;
  table-layout: fixed;
}

.dashboard-table th {
  height: 42px;
  padding: 0 14px;
  border-bottom: 1px solid var(--admin-border);
  background: rgba(255, 255, 255, .018);
  color: var(--admin-text-muted);
  font-size: 9px;
  font-weight: 500;
  letter-spacing: .08em;
  text-align: left;
}

.dashboard-table th:first-child { width: 48%; padding-left: 20px; }
.dashboard-table th:nth-child(2) { width: 16%; }
.dashboard-table th:nth-child(3) { width: 20%; }
.dashboard-table th:last-child { width: 16%; }

.dashboard-table td {
  height: 67px;
  padding: 10px 14px;
  border-bottom: 1px solid rgba(255, 255, 255, .065);
  vertical-align: middle;
}

.dashboard-table td:first-child { padding-left: 20px; }
.dashboard-table tbody tr { transition: background .16s ease; }
.dashboard-table tbody tr:hover { background: rgba(255, 255, 255, .026); }
.dashboard-table tbody tr:last-child td { border-bottom: 0; }

.article-cell {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 11px;
}

.article-cover {
  width: 46px;
  height: 38px;
  flex: 0 0 46px;
  display: grid;
  place-items: center;
  overflow: hidden;
  border: 1px solid var(--admin-border);
  border-radius: 8px;
  background: rgba(255, 255, 255, .018);
  color: var(--admin-text-muted);
}

.article-cover img { width: 100%; height: 100%; object-fit: cover; }

.article-cell strong {
  overflow: hidden;
  color: var(--admin-text-primary);
  font-size: 10px;
  font-weight: 520;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.secondary-cell {
  overflow: hidden;
  color: var(--admin-text-secondary);
  font-size: 9px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.date-cell { color: var(--admin-text-muted); font-variant-numeric: tabular-nums; }

.row-actions {
  display: flex;
  justify-content: flex-end;
  gap: 5px;
}

.row-actions button {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border: 1px solid var(--admin-border);
  border-radius: 7px;
  background: transparent;
  color: var(--admin-text-secondary);
  cursor: pointer;
  transition: color .16s ease, background .16s ease, border-color .16s ease;
}

.row-actions button:hover {
  color: var(--admin-text-primary);
  background: rgba(255, 255, 255, .04);
  border-color: var(--admin-border-strong);
}

.row-actions button.danger:hover {
  color: #f19aa3;
  border-color: rgba(223, 116, 128, .34);
  background: rgba(223, 116, 128, .07);
}

.site-panel {
  display: flex;
  flex-direction: column;
}

.site-info {
  margin: 0;
  padding: 5px 20px 12px;
}

.site-info > div {
  min-height: 43px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  border-bottom: 1px solid rgba(255, 255, 255, .06);
}

.site-info dt {
  flex: 0 0 auto;
  color: var(--admin-text-muted);
  font-size: 9px;
}

.site-info dd {
  max-width: 190px;
  overflow: hidden;
  margin: 0;
  color: var(--admin-text-secondary);
  font-size: 9px;
  font-variant-numeric: tabular-nums;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.site-info .site-info-description {
  min-height: 62px;
  align-items: flex-start;
  padding: 13px 0;
}

.site-info-description dd {
  display: -webkit-box;
  overflow: hidden;
  white-space: normal;
  line-height: 1.65;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.visit-site {
  min-height: 40px;
  margin: auto 20px 20px;
  border: 1px solid color-mix(in srgb, var(--admin-accent) 36%, transparent);
  border-radius: 9px;
  background: color-mix(in srgb, var(--admin-accent) 8%, transparent);
}

.visit-site:hover {
  color: var(--admin-text-primary);
  border-color: color-mix(in srgb, var(--admin-accent) 54%, transparent);
  background: color-mix(in srgb, var(--admin-accent) 12%, transparent);
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
}

@media (max-width: 1180px) {
  .stats-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .dashboard-main-grid { grid-template-columns: 1fr; }
}

@media (max-width: 560px) {
  .stats-grid { grid-template-columns: 1fr; }
  .metric-card { min-height: 104px; padding: 18px; }
  .panel-header { min-height: 66px; padding-inline: 16px; }
  .site-info { padding-inline: 16px; }
  .visit-site { margin-inline: 16px; }
}
</style>
