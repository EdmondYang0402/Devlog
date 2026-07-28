<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const tags = ref([
    { id: 1, name: 'Java', count: 8 },
    { id: 2, name: 'Spring Boot', count: 6 },
    { id: 3, name: 'Redis', count: 3 },
    { id: 4, name: '安全', count: 4 },
    { id: 5, name: '日本', count: 5 },
    { id: 6, name: '随笔', count: 7 },
])

const newTag = ref('')

const addTag = () => {
    if (!newTag.value.trim()) return ElMessage.warning('请输入标签名')
    tags.value.push({ id: Date.now(), name: newTag.value.trim(), count: 0 })
    newTag.value = ''
    ElMessage.success('添加成功')
}

const remove = (id) => {
    ElMessageBox.confirm('确定删除该标签？', '提示', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
        .then(() => { tags.value = tags.value.filter(t => t.id !== id); ElMessage.success('删除成功') })
}
</script>

<template>
    <div class="page">
        <div class="add-bar">
            <input v-model="newTag" class="f-input" placeholder="输入新标签名称" @keyup.enter="addTag" />
            <button class="btn-add" @click="addTag">
                <i class="ti ti-plus" aria-hidden="true"></i> 添加
            </button>
        </div>

        <div class="tags-wrap">
            <div v-for="t in tags" :key="t.id" class="tag-item">
                <span class="tag-name">{{ t.name }}</span>
                <span class="tag-count">{{ t.count }} 篇</span>
                <button class="tag-del" @click="remove(t.id)" aria-label="删除标签">
                    <i class="ti ti-x" aria-hidden="true"></i>
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.page { display:flex; flex-direction:column; gap:1rem; }
.add-bar { display:flex; gap:8px; background:var(--surface-2,#fff); padding:.75rem 1rem; border-radius:10px; border:0.5px solid var(--border,#eee); }
.f-input { flex:1; height:30px; padding:0 10px; font-size:12px; border:0.5px solid var(--border,#eee); border-radius:6px; background:var(--surface-1,#f9f9f9); color:var(--text-primary,#222); outline:none; }
.btn-add { height:30px; padding:0 14px; font-size:12px; border-radius:6px; background:rgba(127,119,221,.12); border:0.5px solid #AFA9EC; color:#534AB7; cursor:pointer; }
.tags-wrap { display:flex; flex-wrap:wrap; gap:8px; background:var(--surface-2,#fff); padding:1rem; border-radius:10px; border:0.5px solid var(--border,#eee); }
.tag-item { display:flex; align-items:center; gap:6px; padding:5px 10px; background:#EEEDFE; border:0.5px solid #AFA9EC; border-radius:20px; }
.tag-name { font-size:12px; color:#534AB7; font-weight:500; }
.tag-count { font-size:11px; color:#7F77DD; }
.tag-del { background:none; border:none; cursor:pointer; color:#AFA9EC; padding:0; display:flex; align-items:center; font-size:13px; transition:color .15s; }
.tag-del:hover { color:#E24B4A; }
</style>
