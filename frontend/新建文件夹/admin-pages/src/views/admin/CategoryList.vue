<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([
    { id: 1, name: '技术', description: '编程、开发、工具相关', count: 12 },
    { id: 2, name: '随笔', description: '日常感悟、生活记录', count: 7 },
    { id: 3, name: '阅读', description: '读书笔记与书评', count: 5 },
])

const form = ref({ name: '', description: '' })
const editId = ref(null)
const showForm = ref(false)

const openCreate = () => { form.value = { name: '', description: '' }; editId.value = null; showForm.value = true }
const openEdit = (c) => { form.value = { name: c.name, description: c.description }; editId.value = c.id; showForm.value = true }

const save = () => {
    if (!form.value.name) return ElMessage.warning('请输入分类名称')
    if (editId.value) {
        const c = categories.value.find(c => c.id === editId.value)
        if (c) { c.name = form.value.name; c.description = form.value.description }
        ElMessage.success('修改成功')
    } else {
        categories.value.push({ id: Date.now(), name: form.value.name, description: form.value.description, count: 0 })
        ElMessage.success('添加成功')
    }
    showForm.value = false
}

const remove = (id) => {
    ElMessageBox.confirm('确定删除该分类？', '提示', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
        .then(() => { categories.value = categories.value.filter(c => c.id !== id); ElMessage.success('删除成功') })
}
</script>

<template>
    <div class="page">
        <div class="toolbar">
            <button class="btn-create" @click="openCreate">
                <i class="ti ti-plus" aria-hidden="true"></i> 新增分类
            </button>
        </div>

        <div v-if="showForm" class="form-card">
            <p class="form-title">{{ editId ? '编辑分类' : '新增分类' }}</p>
            <div class="form-row">
                <label>分类名称</label>
                <input v-model="form.name" class="f-input" placeholder="请输入分类名称" />
            </div>
            <div class="form-row">
                <label>描述</label>
                <input v-model="form.description" class="f-input" placeholder="简单描述一下这个分类" />
            </div>
            <div class="form-actions">
                <button class="btn-save" @click="save">保存</button>
                <button class="btn-cancel" @click="showForm = false">取消</button>
            </div>
        </div>

        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr>
                        <th style="width:40px">ID</th>
                        <th style="width:100px">分类名称</th>
                        <th>描述</th>
                        <th style="width:80px">文章数</th>
                        <th style="width:110px;text-align:center">操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="c in categories" :key="c.id" class="table-row">
                        <td class="muted">{{ c.id }}</td>
                        <td style="font-weight:500;color:var(--text-primary,#222)">{{ c.name }}</td>
                        <td class="muted">{{ c.description }}</td>
                        <td class="muted">{{ c.count }}</td>
                        <td style="text-align:center">
                            <button class="btn-edit" @click="openEdit(c)">编辑</button>
                            <button class="btn-del" @click="remove(c.id)">删除</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<style scoped>
.page { display:flex; flex-direction:column; gap:1rem; }
.toolbar { display:flex; justify-content:flex-end; }
.btn-create { height:30px; padding:0 14px; font-size:12px; border-radius:6px; background:rgba(127,119,221,.12); border:0.5px solid #AFA9EC; color:#534AB7; cursor:pointer; }
.form-card { background:var(--surface-2,#fff); border-radius:10px; border:0.5px solid var(--border,#eee); padding:1rem 1.2rem; }
.form-title { font-size:13px; font-weight:500; color:var(--text-primary,#222); margin:0 0 1rem; }
.form-row { display:flex; align-items:center; gap:12px; margin-bottom:.8rem; }
.form-row label { font-size:12px; color:var(--text-muted,#aaa); width:60px; flex-shrink:0; }
.f-input { flex:1; height:30px; padding:0 10px; font-size:12px; border:0.5px solid var(--border,#eee); border-radius:6px; background:var(--surface-1,#f9f9f9); color:var(--text-primary,#222); outline:none; }
.form-actions { display:flex; gap:8px; margin-top:.5rem; }
.btn-save   { height:30px; padding:0 18px; font-size:12px; border-radius:6px; background:rgba(127,119,221,.12); border:0.5px solid #AFA9EC; color:#534AB7; cursor:pointer; }
.btn-cancel { height:30px; padding:0 14px; font-size:12px; border-radius:6px; border:0.5px solid var(--border,#eee); background:transparent; color:var(--text-secondary,#666); cursor:pointer; }
.table-wrap { background:var(--surface-2,#fff); border-radius:10px; border:0.5px solid var(--border,#eee); overflow:auto; }
.table { width:100%; border-collapse:collapse; font-size:12px; }
.table thead tr { background:var(--surface-1,#f9f9f9); border-bottom:0.5px solid var(--border,#eee); }
.table th { padding:10px; text-align:left; font-weight:500; color:var(--text-muted,#aaa); }
.table-row { border-bottom:0.5px solid var(--border,#eee); transition:background .12s; }
.table-row:hover { background:var(--surface-1,#f9f9f9); }
.table td { padding:10px; vertical-align:middle; }
.muted { color:var(--text-secondary,#888); }
.btn-edit { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #85B7EB; background:#E6F1FB; color:#185FA5; cursor:pointer; margin-right:4px; }
.btn-del  { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #F09595; background:#FCEBEB; color:#A32D2D; cursor:pointer; }
</style>
