<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import { getAdminArticlePage, deleteAdminArticle } from '@/api/article'
import { ARTICLE_STATUS_OPTIONS, getArticleStatusLabel, isArticlePublished } from '@/constants/articleStatus'

const formatDate = (dateStr) => {
    if (!dateStr) return ''
    const d = new Date(dateStr)
    const pad = n => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const router = useRouter()

const searchForm = ref({ title: '', status: '' })
const articles = ref([])

const page = ref(1)
const size = ref(10)
const total = ref(0)

const loadArticles = async () => {
  try {
    const res = await getAdminArticlePage({
      page: page.value,
      size: size.value,
      title: searchForm.value.title,
      status: searchForm.value.status
    })
    const pageData = res?.data
    articles.value = Array.isArray(pageData?.records) ? pageData.records : []
    total.value = Number(pageData?.total || 0)
  } catch (error) {
    console.error(error)
    articles.value = []
    total.value = 0
    ElMessage.error(error.response?.data?.message || "加载文章失败")
  }
}

onMounted(() => { loadArticles() })

const search = () => { page.value = 1; loadArticles() }

const resetSearch = () => {
  searchForm.value = { title: '', status: '' }
  page.value = 1
  loadArticles()
}

const createArticle = () => { router.push('/admin/articles/create') }
const editArticle = (id) => { router.push(`/admin/articles/edit/${id}`) }

const deleteArticle = (id) => {
    ElMessageBox.confirm('确定删除该文章吗？', '提示', {
        confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    }).then(async () => {
        await deleteAdminArticle(id)
        ElMessage.success("删除成功")
        loadArticles()
    })
}

const changePage = (newPage) => { page.value = newPage; loadArticles() }
</script>

<template>
  <div class="article-list">
    <section class="article-panel" aria-label="文章管理列表">
      <div class="search-bar">
        <input v-model="searchForm.title" class="s-input" placeholder="搜索文章标题" />
        <select v-model="searchForm.status" class="s-select">
          <option value="">全部状态</option>
          <option v-for="option in ARTICLE_STATUS_OPTIONS" :key="option.value" :value="option.value">{{ option.label }}</option>
        </select>
        <button type="button" class="btn-search" @click="search">
          <i class="ti ti-search" aria-hidden="true"></i> 搜索
        </button>
        <button type="button" class="btn-reset" @click="resetSearch">重置</button>
        <button type="button" class="btn-create" @click="createArticle">
          <i class="ti ti-plus" aria-hidden="true"></i> 新增文章
        </button>
      </div>

      <div class="table-wrap">
        <table class="table">
          <colgroup>
            <col class="col-id" />
            <col class="col-title" />
            <col class="col-status" />
            <col class="col-views" />
            <col class="col-created" />
            <col class="col-actions" />
          </colgroup>
          <thead>
            <tr>
              <th>ID</th>
              <th>标题</th>
              <th class="center-cell">状态</th>
              <th class="center-cell">浏览量</th>
              <th>创建时间</th>
              <th class="center-cell">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="article in articles" :key="article.id" class="table-row">
              <td class="muted id-cell">{{ article.id }}</td>
              <td class="title-cell" :title="article.title">{{ article.title }}</td>
              <td class="center-cell">
                <span :class="['status-badge', isArticlePublished(article.status) ? 'pub' : 'draft']">
                  {{ getArticleStatusLabel(article.status) }}
                </span>
              </td>
              <td class="muted center-cell numeric-cell">{{ article.viewCount }}</td>
              <td class="muted date-cell">{{ formatDate(article.createTime) }}</td>
              <td class="actions-cell">
                <div class="action-buttons">
                  <button type="button" class="btn-edit" @click="editArticle(article.id)">编辑</button>
                  <button type="button" class="btn-del" @click="deleteArticle(article.id)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="articles.length === 0">
              <td colspan="6" class="empty-cell">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="pagination">
        <span class="page-info">共 {{ total }} 篇</span>
        <div class="page-btns">
          <button type="button" class="page-btn" @click="changePage(page - 1)" :disabled="page === 1">上一页</button>
          <button type="button" class="page-btn active">{{ page }}</button>
          <button type="button" class="page-btn" @click="changePage(page + 1)" :disabled="page * size >= total">下一页</button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.article-list {
  width: 100%;
  min-height: 0;
  height: 100%;
}

.article-panel {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 18px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, .09);
  border-radius: 20px;
  background: rgba(15, 18, 32, .82);
  box-shadow: 0 18px 42px rgba(3, 5, 16, .2);
  -webkit-backdrop-filter: blur(18px) saturate(105%);
  backdrop-filter: blur(18px) saturate(105%);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 9px;
  flex-wrap: wrap;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, .075);
}

.s-input,
.s-select {
  height: 36px;
  padding: 0 11px;
  border: 1px solid rgba(255, 255, 255, .1);
  border-radius: 9px;
  outline: none;
  background: rgba(255, 255, 255, .045);
  color: var(--admin-text-primary);
  font-size: 11px;
  transition: border-color .16s ease, background .16s ease, box-shadow .16s ease;
}

.s-input { width: min(230px, 100%); }
.s-select { min-width: 118px; color-scheme: dark; }
.s-input::placeholder { color: var(--admin-text-muted); }
.s-input:focus,
.s-select:focus {
  border-color: var(--admin-border-strong);
  background: rgba(255, 255, 255, .065);
  box-shadow: 0 0 0 3px rgba(170, 148, 237, .07);
}

.search-bar button,
.action-buttons button,
.page-btn {
  border-radius: 8px;
  font: inherit;
  cursor: pointer;
  transition: color .16s ease, background .16s ease, border-color .16s ease;
}

.btn-search,
.btn-reset,
.btn-create {
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 0 14px;
  font-size: 11px;
}

.btn-search {
  border: 1px solid rgba(120, 165, 229, .34);
  background: rgba(120, 165, 229, .1);
  color: #a9c8f2;
}

.btn-reset {
  border: 1px solid var(--admin-border);
  background: transparent;
  color: var(--admin-text-secondary);
}

.btn-create {
  margin-left: auto;
  border: 1px solid rgba(170, 148, 237, .38);
  background: rgba(170, 148, 237, .14);
  color: #cabcf5;
}

.btn-search:hover,
.btn-create:hover {
  border-color: rgba(199, 181, 255, .5);
  background: rgba(170, 148, 237, .19);
  color: #eee9ff;
}

.btn-reset:hover {
  border-color: var(--admin-border-strong);
  background: rgba(255, 255, 255, .045);
  color: var(--admin-text-primary);
}

.table-wrap {
  min-height: 360px;
  flex: 1;
  overflow: auto;
  border: 1px solid rgba(255, 255, 255, .075);
  border-radius: 14px;
  background: rgba(8, 11, 23, .68);
}

.table {
  width: 100%;
  min-width: 790px;
  border-collapse: collapse;
  table-layout: fixed;
  color: var(--admin-text-secondary);
  font-size: 11px;
}

.col-id { width: 68px; }
.col-title { width: auto; }
.col-status { width: 112px; }
.col-views { width: 86px; }
.col-created { width: 168px; }
.col-actions { width: 148px; }

.table thead {
  position: sticky;
  z-index: 1;
  top: 0;
  background: rgba(29, 32, 51, .96);
  box-shadow: 0 1px 0 rgba(255, 255, 255, .09);
}

.table th {
  height: 46px;
  padding: 0 16px;
  color: rgba(238, 235, 249, .7);
  font-size: 9px;
  font-weight: 650;
  letter-spacing: .09em;
  text-align: left;
  white-space: nowrap;
}

.table-row {
  border-bottom: 1px solid rgba(255, 255, 255, .065);
  transition: background .14s ease;
}

.table-row:last-child { border-bottom: 0; }
.table-row:hover { background: rgba(255, 255, 255, .035); }

.table td {
  height: 62px;
  padding: 12px 16px;
  vertical-align: middle;
}

.center-cell { text-align: center; }

.title-cell {
  overflow: hidden;
  color: var(--admin-text-primary);
  font-size: 11px;
  font-weight: 560;
  line-height: 1.45;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.muted { color: rgba(236, 234, 248, .58); }
.id-cell,
.numeric-cell,
.date-cell { font-variant-numeric: tabular-nums; }
.numeric-cell { color: rgba(236, 234, 248, .7); }
.date-cell { white-space: nowrap; }

.status-badge {
  min-width: 64px;
  min-height: 25px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 3px 10px;
  border: 1px solid currentColor;
  border-radius: 999px;
  font-size: 9px;
  font-weight: 560;
  line-height: 1;
  white-space: nowrap;
}

.status-badge.pub {
  border-color: rgba(104, 197, 155, .28);
  background: rgba(104, 197, 155, .09);
  color: #8dd9b8;
}

.status-badge.draft {
  border-color: rgba(223, 175, 101, .28);
  background: rgba(223, 175, 101, .085);
  color: #e7bd7c;
}

.actions-cell { text-align: center; }

.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-edit,
.btn-del {
  width: 54px;
  height: 30px;
  padding: 0;
  font-size: 10px;
}

.btn-edit {
  border: 1px solid rgba(120, 165, 229, .3);
  background: rgba(120, 165, 229, .09);
  color: #a8c7ef;
}

.btn-del {
  border: 1px solid rgba(223, 116, 128, .3);
  background: rgba(223, 116, 128, .085);
  color: #efa0a8;
}

.btn-edit:hover {
  border-color: rgba(120, 165, 229, .48);
  background: rgba(120, 165, 229, .15);
  color: #d6e7ff;
}

.btn-del:hover {
  border-color: rgba(223, 116, 128, .48);
  background: rgba(223, 116, 128, .14);
  color: #ffc4ca;
}

.empty-cell {
  height: 180px;
  padding: 32px;
  color: var(--admin-text-muted);
  text-align: center;
}

.pagination {
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-top: 2px;
}

.page-info { color: var(--admin-text-muted); font-size: 10px; }
.page-btns { display: flex; align-items: center; gap: 7px; }

.page-btn {
  min-width: 32px;
  height: 30px;
  padding: 0 11px;
  border: 1px solid var(--admin-border);
  background: transparent;
  color: var(--admin-text-secondary);
  font-size: 10px;
}

.page-btn:not(:disabled):hover {
  border-color: var(--admin-border-strong);
  background: rgba(255, 255, 255, .045);
  color: var(--admin-text-primary);
}

.page-btn:disabled { opacity: .35; cursor: not-allowed; }
.page-btn.active {
  border-color: rgba(170, 148, 237, .36);
  background: rgba(170, 148, 237, .13);
  color: #cabcf5;
  cursor: default;
}

@supports not ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
  .article-panel { background: var(--admin-panel-fallback); }
}

@media (max-width: 1100px) {
  .table { min-width: 720px; }
  .col-id,
  .table th:first-child,
  .table td:first-child { display: none; }
  .col-status { width: 100px; }
  .col-created { width: 154px; }
  .col-actions { width: 138px; }
}

@media (max-width: 700px) {
  .article-list { height: auto; }
  .article-panel { min-height: 0; padding: 14px; border-radius: 16px; }
  .search-bar { align-items: stretch; }
  .s-input { width: 100%; }
  .s-select { flex: 1; }
  .btn-create { margin-left: 0; }
  .table-wrap { min-height: 340px; }
  .pagination { align-items: flex-start; flex-direction: column; }
}

@media (max-width: 460px) {
  .s-select { width: 100%; flex-basis: 100%; }
  .btn-search,
  .btn-reset,
  .btn-create { flex: 1; }
  .page-btn { padding-inline: 9px; }
}
</style>
