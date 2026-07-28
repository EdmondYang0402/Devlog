<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([
    { id: 1, content: '写得很好，受益匪浅！', author: '读者A', article: 'JWT 认证的那些坑', date: '2026-06-21', status: '正常' },
    { id: 2, content: '东京生活真的很辛苦，加油！', author: '读者B', article: '在东京生活是什么感觉', date: '2026-06-16', status: '正常' },
    { id: 3, content: '广告推广内容...', author: '垃圾用户', article: '重读《人间失格》', date: '2026-06-10', status: '已屏蔽' },
])

const deleteComment = (id) => {
    ElMessageBox.confirm('确定删除该评论？', '提示', {
        confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    }).then(() => {
        comments.value = comments.value.filter(c => c.id !== id)
        ElMessage.success('删除成功')
    })
}

const toggleStatus = (c) => {
    c.status = c.status === '正常' ? '已屏蔽' : '正常'
    ElMessage.success(c.status === '正常' ? '已恢复显示' : '已屏蔽')
}
</script>

<template>
    <div class="page">
        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr>
                        <th>评论内容</th>
                        <th style="width:80px">评论者</th>
                        <th style="width:160px">所属文章</th>
                        <th style="width:100px">时间</th>
                        <th style="width:70px">状态</th>
                        <th style="width:110px;text-align:center">操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="c in comments" :key="c.id" class="table-row">
                        <td class="content-cell">{{ c.content }}</td>
                        <td class="muted">{{ c.author }}</td>
                        <td class="muted ellipsis">{{ c.article }}</td>
                        <td class="muted">{{ c.date }}</td>
                        <td>
                            <span :class="['status-badge', c.status === '正常' ? 'ok' : 'blocked']">{{ c.status }}</span>
                        </td>
                        <td style="text-align:center">
                            <button class="btn-edit" @click="toggleStatus(c)">
                                {{ c.status === '正常' ? '屏蔽' : '恢复' }}
                            </button>
                            <button class="btn-del" @click="deleteComment(c.id)">删除</button>
                        </td>
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
.content-cell { color:var(--text-primary,#222); max-width:200px; }
.muted { color:var(--text-secondary,#888); }
.ellipsis { white-space:nowrap; overflow:hidden; text-overflow:ellipsis; max-width:160px; }
.status-badge { font-size:11px; padding:2px 8px; border-radius:12px; }
.status-badge.ok      { background:#E1F5EE; color:#0F6E56; border:0.5px solid #9FE1CB; }
.status-badge.blocked { background:#FCEBEB; color:#A32D2D; border:0.5px solid #F09595; }
.btn-edit { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #85B7EB; background:#E6F1FB; color:#185FA5; cursor:pointer; margin-right:4px; }
.btn-del  { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #F09595; background:#FCEBEB; color:#A32D2D; cursor:pointer; }
</style>
