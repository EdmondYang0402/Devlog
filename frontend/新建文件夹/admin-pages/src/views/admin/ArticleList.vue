<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const searchForm = ref({ title: '', category: '', status: '' })

// 对接后端时替换为 API 调用
const articles = ref([
    { id: 1, title: 'JWT 认证的那些坑——从原理到安全实践', category: '技术', status: '已发布', views: 342, date: '2026-06-20', icon: '🔐', bg: 'linear-gradient(135deg,#AFA9EC,#7F77DD)' },
    { id: 2, title: '在东京生活是什么感觉', category: '随笔', status: '已发布', views: 891, date: '2026-06-15', icon: '🗼', bg: 'linear-gradient(135deg,#9FE1CB,#1D9E75)' },
    { id: 3, title: '重读《人间失格》——太宰治写的其实是我们', category: '阅读', status: '草稿', views: 567, date: '2026-06-08', icon: '📖', bg: 'linear-gradient(135deg,#ED93B1,#D4537E)' },
    { id: 4, title: 'Spring Boot + Redis 实现接口限流', category: '技术', status: '已发布', views: 423, date: '2026-05-30', icon: '⚡', bg: 'linear-gradient(135deg,#AFA9EC,#7F77DD)' },
    { id: 5, title: '关于"懂安全的开发者"这条路', category: '随笔', status: '草稿', views: 712, date: '2026-05-18', icon: '🛡️', bg: 'linear-gradient(135deg,#9FE1CB,#1D9E75)' },
])

const filtered = computed(() => {
    return articles.value.filter(a => {
        const matchTitle = !searchForm.value.title || a.title.includes(searchForm.value.title)
        const matchCat = !searchForm.value.category || a.category === searchForm.value.category
        const matchStatus = !searchForm.value.status || a.status === searchForm.value.status
        return matchTitle && matchCat && matchStatus
    })
})

const resetSearch = () => { searchForm.value = { title: '', category: '', status: '' } }

const deleteArticle = (id) => {
    ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
        confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    }).then(() => {
        articles.value = articles.value.filter(a => a.id !== id)
        ElMessage.success('删除成功')
    })
}

const editArticle = (id) => router.push(`/admin/articles/edit/${id}`)
</script>

<template>
    <div class="article-list">

        <!-- 搜索栏 -->
        <div class="search-bar">
            <input v-model="searchForm.title" class="s-input" placeholder="搜索文章标题..." />
            <select v-model="searchForm.category" class="s-select">
                <option value="">全部分类</option>
                <option>技术</option>
                <option>随笔</option>
                <option>阅读</option>
            </select>
            <select v-model="searchForm.status" class="s-select">
                <option value="">全部状态</option>
                <option>已发布</option>
                <option>草稿</option>
            </select>
            <button class="btn-search">
                <i class="ti ti-search" aria-hidden="true"></i> 搜索
            </button>
            <button class="btn-reset" @click="resetSearch">重置</button>
            <button class="btn-create" @click="router.push('/admin/articles/create')">
                <i class="ti ti-plus" aria-hidden="true"></i> 发布文章
            </button>
        </div>

        <!-- 表格 -->
        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr>
                        <th style="width:40px"><input type="checkbox" /></th>
                        <th style="width:60px">封面</th>
                        <th>标题</th>
                        <th style="width:70px">分类</th>
                        <th style="width:70px">状态</th>
                        <th style="width:70px">访问量</th>
                        <th style="width:100px">发布时间</th>
                        <th style="width:110px;text-align:center">操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="a in filtered" :key="a.id" class="table-row">
                        <td><input type="checkbox" /></td>
                        <td>
                            <div class="cover" :style="{ background: a.bg }">{{ a.icon }}</div>
                        </td>
                        <td class="title-cell">{{ a.title }}</td>
                        <td class="muted">{{ a.category }}</td>
                        <td>
                            <span :class="['status-badge', a.status === '已发布' ? 'pub' : 'draft']">
                                {{ a.status }}
                            </span>
                        </td>
                        <td class="muted">{{ a.views }}</td>
                        <td class="muted">{{ a.date }}</td>
                        <td style="text-align:center">
                            <button class="btn-edit" @click="editArticle(a.id)">编辑</button>
                            <button class="btn-del" @click="deleteArticle(a.id)">删除</button>
                        </td>
                    </tr>
                    <tr v-if="filtered.length === 0">
                        <td colspan="8" style="text-align:center;padding:2rem;color:var(--text-muted)">暂无数据</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 分页 -->
        <div class="pagination">
            <span class="page-info">共 {{ filtered.length }} 篇文章</span>
            <div class="page-btns">
                <button class="page-btn">«</button>
                <button class="page-btn active">1</button>
                <button class="page-btn">»</button>
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
.table th { padding:10px 10px; text-align:left; font-weight:500; color:var(--text-muted,#aaa); white-space:nowrap; }
.table-row { border-bottom:0.5px solid var(--border,#eee); transition:background .12s; }
.table-row:hover { background:var(--surface-1,#f9f9f9); }
.table td { padding:10px 10px; vertical-align:middle; }

.cover { width:44px; height:36px; border-radius:6px; display:flex; align-items:center; justify-content:center; font-size:18px; }
.title-cell { color:var(--text-primary,#222); font-weight:500; max-width:220px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.muted { color:var(--text-secondary,#888); }

.status-badge { font-size:11px; padding:2px 8px; border-radius:12px; }
.status-badge.pub   { background:#E1F5EE; color:#0F6E56; border:0.5px solid #9FE1CB; }
.status-badge.draft { background:var(--surface-1,#f5f5f5); color:var(--text-muted,#aaa); border:0.5px solid var(--border,#eee); }

.btn-edit { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #85B7EB; background:#E6F1FB; color:#185FA5; cursor:pointer; margin-right:4px; }
.btn-del  { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #F09595; background:#FCEBEB; color:#A32D2D; cursor:pointer; }

.pagination { display:flex; align-items:center; justify-content:space-between; background:var(--surface-2,#fff); padding:.6rem 1rem; border-radius:10px; border:0.5px solid var(--border,#eee); }
.page-info { font-size:11px; color:var(--text-muted,#aaa); }
.page-btns { display:flex; gap:4px; }
.page-btn { width:28px; height:28px; border-radius:6px; border:0.5px solid var(--border,#eee); background:transparent; font-size:12px; cursor:pointer; color:var(--text-secondary,#666); }
.page-btn.active { background:#E6F1FB; border-color:#85B7EB; color:#185FA5; }
</style>
