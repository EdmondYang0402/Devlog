<script setup>
import { ref } from 'vue'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'

const list = ref([
    { id: 1, ip: '192.168.1.100', reason: '恶意刷评论', date: '2026-06-10' },
    { id: 2, ip: '10.0.0.55', reason: '发布垃圾广告', date: '2026-06-08' },
])

const form = ref({ ip: '', reason: '' })
const showForm = ref(false)

const add = () => {
    if (!form.value.ip) return ElMessage.warning('请输入 IP 地址')
    list.value.push({ id: Date.now(), ip: form.value.ip, reason: form.value.reason, date: new Date().toISOString().slice(0, 10) })
    form.value = { ip: '', reason: '' }
    showForm.value = false
    ElMessage.success('已加入黑名单')
}

const remove = (id) => {
    ElMessageBox.confirm('确定从黑名单移除？', '提示', { confirmButtonText: '移除', cancelButtonText: '取消', type: 'warning' })
        .then(() => { list.value = list.value.filter(i => i.id !== id); ElMessage.success('已移除') })
}
</script>

<template>
    <div class="page">
        <div class="toolbar">
            <button class="btn-create" @click="showForm = !showForm">
                <i class="ti ti-plus" aria-hidden="true"></i> 添加黑名单
            </button>
        </div>

        <div v-if="showForm" class="form-card">
            <p class="form-title">添加黑名单</p>
            <div class="form-row">
                <label>IP 地址</label>
                <input v-model="form.ip" class="f-input" placeholder="例：192.168.1.100" />
            </div>
            <div class="form-row">
                <label>封禁原因</label>
                <input v-model="form.reason" class="f-input" placeholder="简单说明原因" />
            </div>
            <div class="form-actions">
                <button class="btn-save" @click="add">确认添加</button>
                <button class="btn-cancel" @click="showForm = false">取消</button>
            </div>
        </div>

        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr>
                        <th style="width:150px">IP 地址</th>
                        <th>封禁原因</th>
                        <th style="width:110px">添加时间</th>
                        <th style="width:80px;text-align:center">操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in list" :key="item.id" class="table-row">
                        <td style="font-weight:500;color:var(--text-primary,#222);font-family:monospace">{{ item.ip }}</td>
                        <td class="muted">{{ item.reason }}</td>
                        <td class="muted">{{ item.date }}</td>
                        <td style="text-align:center">
                            <button class="btn-del" @click="remove(item.id)">移除</button>
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
.btn-save   { height:30px; padding:0 18px; font-size:12px; border-radius:6px; background:#FCEBEB; border:0.5px solid #F09595; color:#A32D2D; cursor:pointer; }
.btn-cancel { height:30px; padding:0 14px; font-size:12px; border-radius:6px; border:0.5px solid var(--border,#eee); background:transparent; color:var(--text-secondary,#666); cursor:pointer; }
.table-wrap { background:var(--surface-2,#fff); border-radius:10px; border:0.5px solid var(--border,#eee); overflow:auto; }
.table { width:100%; border-collapse:collapse; font-size:12px; }
.table thead tr { background:var(--surface-1,#f9f9f9); border-bottom:0.5px solid var(--border,#eee); }
.table th { padding:10px; text-align:left; font-weight:500; color:var(--text-muted,#aaa); }
.table-row { border-bottom:0.5px solid var(--border,#eee); transition:background .12s; }
.table-row:hover { background:var(--surface-1,#f9f9f9); }
.table td { padding:10px; vertical-align:middle; }
.muted { color:var(--text-secondary,#888); }
.btn-del { font-size:11px; padding:3px 10px; border-radius:5px; border:0.5px solid #F09595; background:#FCEBEB; color:#A32D2D; cursor:pointer; }
</style>
