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

    <!-- 搜索栏 -->
    <div class="search-bar">
      <input v-model="searchForm.title" class="s-input" placeholder="搜索文章标题" />
      <select v-model="searchForm.status" class="s-select">
        <option value="">全部状态</option>
        <option v-for="option in ARTICLE_STATUS_OPTIONS" :key="option.value" :value="option.value">{{ option.label }}</option>
      </select>
      <button class="btn-search" @click="search">
        <i class="ti ti-search" aria-hidden="true"></i> 搜索
      </button>
      <button class="btn-reset" @click="resetSearch">重置</button>
      <button class="btn-create" @click="createArticle">
        <i class="ti ti-plus" aria-hidden="true"></i> 新增文章
      </button>
    </div>

    <!-- 表格 -->
    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th style="width:60px">ID</th>
            <th>标题</th>
            <th style="width:70px">状态</th>
            <th style="width:70px">浏览量</th>
            <th style="width:140px">创建时间</th>
            <th style="width:110px;text-align:center">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="article in articles" :key="article.id" class="table-row">
            <td class="muted">{{ article.id }}</td>
            <td class="title-cell">{{ article.title }}</td>
            <td>
              <span :class="['status-badge', isArticlePublished(article.status) ? 'pub' : 'draft']">
                {{ getArticleStatusLabel(article.status) }}
              </span>
            </td>
            <td class="muted">{{ article.viewCount }}</td>
            <td class="muted">{{ formatDate(article.createTime) }}</td>
            <td style="text-align:center">
              <button class="btn-edit" @click="editArticle(article.id)">编辑</button>
              <button class="btn-del" @click="deleteArticle(article.id)">删除</button>
            </td>
          </tr>
          <tr v-if="articles.length === 0">
            <td colspan="6" style="text-align:center;padding:2rem;color:var(--text-muted,#aaa)">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <span class="page-info">共 {{ total }} 篇</span>
      <div class="page-btns">
        <button class="page-btn" @click="changePage(page - 1)" :disabled="page === 1">上一页</button>
        <button class="page-btn active">{{ page }}</button>
        <button class="page-btn" @click="changePage(page + 1)" :disabled="page * size >= total">下一页</button>
      </div>
    </div>

  </div>
</template>

<style scoped>
.article-list { display:flex; flex-direction:column; gap:1rem; height:100%; }

.search-bar { display:flex; align-items:center; gap:8px; background:var(--surface-2,#fff); padding:.75rem 1rem; border-radius:10px; border:0.5px solid var(--border,#eee); flex-wrap:wrap; }
.s-input { height:30px; padding:0 10px; font-size:12px; border:0.5px solid var(--border,#eee); border-radius:6px; background:var(--surface-1,#f9f9f9); color:var(--text-primary,#222); width:180px; outline:none; }
.s-select { height:30px; padding:0 8px; font-size:12px; border:0.5px solid var(--border,#eee); border-radius:6px; background:var(--surface-1,#f9f9f9); color:var(--text-primary,#222); outline:none; }
.btn-search { height:30px; padding:0 14px; font-size:12px; border-radius:6px; background:#E6F1FB; border:0.5px solid #85B7EB; color:#185FA5; cursor:pointer; }
.btn-reset  { height:30px; padding:0 12px; font-size:12px; border-radius:6px; border:0.5px solid var(--border,#eee); background:transparent; color:var(--text-secondary,#666); cursor:pointer; }
.btn-create { height:30px; padding:0 14px; font-size:12px; border-radius:6px; background:rgba(127,119,221,.12); border:0.5px solid #AFA9EC; color:#534AB7; cursor:pointer; margin-left:auto; }

.table-wrap { background:var(--surface-2,#fff); border-radius:10px; border:0.5px solid var(--border,#eee); overflow:auto; flex:1; }
.table { width:100%; border-collapse:collapse; font-size:12px; }
.table thead tr { background:var(--surface-1,#f9f9f9); border-bottom:0.5px solid var(--border,#eee); }
.table th { padding:10px; text-align:left; font-weight:500; color:var(--text-muted,#aaa); white-space:nowrap; }
.table-row { border-bottom:0.5px solid var(--border,#eee); transition:background .12s; }
.table-row:hover { background:var(--surface-1,#f9f9f9); }
.table td { padding:10px; vertical-align:middle; }

.title-cell { color:var(--text-primary,#222); font-weight:500; max-width:260px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.muted { color:var(--text-secondary,#888); }

.status-badge { font-size:11px; padding:2px 8px; border-radius:12px; }
.status-badge.pub   { background:#E1F5EE; color:#0F6E56; border:0.5px solid #9FE1CB; }
.status-badge.draft { background:var(--surface-1,#f5f5f5); color:var(--text-muted,#aaa); border:0.5px solid var(--border,#eee); }

.btn-edit { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #85B7EB; background:#E6F1FB; color:#185FA5; cursor:pointer; margin-right:4px; }
.btn-del  { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #F09595; background:#FCEBEB; color:#A32D2D; cursor:pointer; }

.pagination { display:flex; align-items:center; justify-content:space-between; background:var(--surface-2,#fff); padding:.6rem 1rem; border-radius:10px; border:0.5px solid var(--border,#eee); }
.page-info { font-size:11px; color:var(--text-muted,#aaa); }
.page-btns { display:flex; align-items:center; gap:8px; }
.page-btn { padding:0 12px; height:28px; border-radius:6px; border:0.5px solid var(--border,#eee); background:transparent; font-size:12px; cursor:pointer; color:var(--text-secondary,#666); }
.page-btn:disabled { opacity:.4; cursor:not-allowed; }
.page-btn.active { background:#E6F1FB; border-color:#85B7EB; color:#185FA5; cursor:default; }
</style>
