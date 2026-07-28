<script setup>
import { onMounted, ref } from 'vue'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import { getAdminArticlePage } from '@/api/article'
import { commentDeleteService, commentListService } from '@/api/comment'

const comments = ref([])
const loading = ref(false)

const formatTime = value => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  }).format(date)
}

const flattenComments = (items, article) => items.flatMap(comment => [
  { ...comment, articleId: article.id, articleTitle: article.title },
  ...(comment.replies || []).map(reply => ({
    ...reply,
    articleId: article.id,
    articleTitle: article.title
  }))
])

const loadComments = async () => {
  loading.value = true
  try {
    const articleResult = await getAdminArticlePage({ page: 1, size: 10000 })
    const articles = articleResult?.data?.records || []
    const results = await Promise.all(articles.map(async article => {
      const commentResult = await commentListService(article.id)
      return flattenComments(commentResult?.data || [], article)
    }))
    comments.value = results.flat().sort((a, b) =>
      new Date(b.createTime || 0) - new Date(a.createTime || 0))
  } catch (error) {
    console.error(error)
    comments.value = []
    ElMessage.error(error?.response?.data?.message || '加载评论失败')
  } finally {
    loading.value = false
  }
}

const deleteComment = async comment => {
  if (Number(comment.isDeleted) === 1) return
  try {
    await ElMessageBox.confirm('确定删除该评论吗？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await commentDeleteService(comment.id)
    ElMessage.success('删除成功')
    await loadComments()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error?.response?.data?.message || '删除评论失败')
    }
  }
}

onMounted(loadComments)
</script>

<template>
  <div class="page" v-loading="loading">
    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>评论内容</th>
            <th style="width:100px">评论者</th>
            <th style="width:180px">所属文章</th>
            <th style="width:145px">时间</th>
            <th style="width:70px">状态</th>
            <th style="width:80px;text-align:center">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="comment in comments" :key="comment.id" class="table-row">
            <td class="content-cell">{{ comment.content }}</td>
            <td class="muted">{{ comment.username || '已注销用户' }}</td>
            <td class="muted ellipsis" :title="comment.articleTitle">{{ comment.articleTitle }}</td>
            <td class="muted">{{ formatTime(comment.createTime) }}</td>
            <td>
              <span :class="['status-badge', Number(comment.isDeleted) === 1 ? 'blocked' : 'ok']">
                {{ Number(comment.isDeleted) === 1 ? '已删除' : '正常' }}
              </span>
            </td>
            <td style="text-align:center">
              <button
                class="btn-del"
                :disabled="Number(comment.isDeleted) === 1"
                @click="deleteComment(comment)"
              >删除</button>
            </td>
          </tr>
          <tr v-if="!loading && comments.length === 0">
            <td colspan="6" class="empty">暂无评论</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.page { height:100%; }
.table-wrap { background:var(--surface-2,#fff); border-radius:10px; border:0.5px solid var(--border,#eee); overflow:auto; }
.table { width:100%; border-collapse:collapse; font-size:12px; }
.table thead tr { background:var(--surface-1,#f9f9f9); border-bottom:0.5px solid var(--border,#eee); }
.table th { padding:10px; text-align:left; font-weight:500; color:var(--text-muted,#aaa); }
.table-row { border-bottom:0.5px solid var(--border,#eee); transition:background .12s; }
.table-row:hover { background:var(--surface-1,#f9f9f9); }
.table td { padding:10px; vertical-align:middle; }
.content-cell { color:var(--text-primary,#222); max-width:280px; overflow-wrap:anywhere; }
.muted { color:var(--text-secondary,#888); }
.ellipsis { white-space:nowrap; overflow:hidden; text-overflow:ellipsis; max-width:180px; }
.status-badge { font-size:11px; padding:2px 8px; border-radius:12px; }
.status-badge.ok { background:#E1F5EE; color:#0F6E56; border:0.5px solid #9FE1CB; }
.status-badge.blocked { background:#FCEBEB; color:#A32D2D; border:0.5px solid #F09595; }
.btn-del { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #F09595; background:#FCEBEB; color:#A32D2D; cursor:pointer; }
.btn-del:disabled { opacity:.45; cursor:not-allowed; }
.empty { text-align:center; padding:2rem !important; color:var(--text-muted,#aaa); }
</style>
