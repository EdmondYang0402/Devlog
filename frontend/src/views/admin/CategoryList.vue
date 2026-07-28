<script setup>
import { onMounted, ref } from 'vue'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import {
  createCategory,
  deleteCategory,
  getAdminCategoryList,
  updateCategory
} from '@/api/category'

const categories = ref([])
const form = ref(createEmptyForm())
const editId = ref(null)
const showForm = ref(false)
const loading = ref(false)
const saving = ref(false)
const deletingId = ref(null)

function createEmptyForm() {
  return { name: '', description: '', sortOrder: 0 }
}

function errorMessage(error, fallback) {
  return error?.response?.data?.message || error?.message || fallback
}

async function loadCategories() {
  loading.value = true
  try {
    const result = await getAdminCategoryList()
    categories.value = Array.isArray(result?.data) ? result.data : []
  } catch (error) {
    categories.value = []
    ElMessage.error(errorMessage(error, '加载分类失败'))
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.value = createEmptyForm()
  editId.value = null
  showForm.value = true
}

function openEdit(category) {
  form.value = {
    name: category.name || '',
    description: category.description || '',
    sortOrder: category.sortOrder ?? 0
  }
  editId.value = category.id
  showForm.value = true
}

function closeForm(force = false) {
  if (saving.value && !force) return
  showForm.value = false
  editId.value = null
  form.value = createEmptyForm()
}

async function save() {
  const name = form.value.name.trim()
  const description = form.value.description.trim()

  if (!name) return ElMessage.warning('请输入分类名称')
  if (name.length > 50) return ElMessage.warning('分类名称不能超过 50 个字符')
  if (description.length > 255) return ElMessage.warning('分类说明不能超过 255 个字符')

  const payload = {
    name,
    description: description || null,
    sortOrder: Number(form.value.sortOrder) || 0
  }

  saving.value = true
  try {
    if (editId.value !== null) {
      await updateCategory(editId.value, payload)
      ElMessage.success('分类修改成功')
    } else {
      await createCategory(payload)
      ElMessage.success('分类新增成功')
    }
    closeForm(true)
    await loadCategories()
  } catch (error) {
    ElMessage.error(errorMessage(error, editId.value !== null ? '修改分类失败' : '新增分类失败'))
  } finally {
    saving.value = false
  }
}

async function remove(category) {
  try {
    await ElMessageBox.confirm(
      `确定删除分类“${category.name}”吗？`,
      '删除分类',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    deletingId.value = category.id
    await deleteCategory(category.id)
    ElMessage.success('分类删除成功')
    await loadCategories()
  } catch (error) {
    if (error === 'cancel' || error === 'close') return
    ElMessage.error(errorMessage(error, '删除分类失败'))
  } finally {
    deletingId.value = null
  }
}

onMounted(loadCategories)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <button class="btn-create" :disabled="loading" @click="openCreate">
        <i class="ti ti-plus" aria-hidden="true"></i> 新增分类
      </button>
    </div>

    <div v-if="showForm" class="form-card">
      <p class="form-title">{{ editId !== null ? '编辑分类' : '新增分类' }}</p>
      <div class="form-row">
        <label for="category-name">分类名称</label>
        <input
          id="category-name"
          v-model="form.name"
          class="f-input"
          maxlength="50"
          placeholder="请输入分类名称"
          @keyup.enter="save"
        />
      </div>
      <div class="form-row">
        <label for="category-description">分类说明</label>
        <input
          id="category-description"
          v-model="form.description"
          class="f-input"
          maxlength="255"
          placeholder="简单描述一下这个分类"
          @keyup.enter="save"
        />
      </div>
      <div class="form-row">
        <label for="category-sort">排序值</label>
        <input
          id="category-sort"
          v-model.number="form.sortOrder"
          class="f-input sort-input"
          type="number"
          placeholder="0"
          @keyup.enter="save"
        />
        <span class="form-tip">数值越小越靠前</span>
      </div>
      <div class="form-actions">
        <button class="btn-save" :disabled="saving" @click="save">
          {{ saving ? '保存中…' : '保存' }}
        </button>
        <button class="btn-cancel" :disabled="saving" @click="closeForm">取消</button>
      </div>
    </div>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th style="width:60px">ID</th>
            <th style="width:130px">分类名称</th>
            <th>分类说明</th>
            <th style="width:80px">排序值</th>
            <th style="width:80px">文章数</th>
            <th style="width:130px;text-align:center">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="state-cell">正在加载分类…</td>
          </tr>
          <tr v-else-if="categories.length === 0">
            <td colspan="6" class="state-cell">暂无分类</td>
          </tr>
          <tr v-for="category in categories" v-else :key="category.id" class="table-row">
            <td class="muted">{{ category.id }}</td>
            <td class="category-name">{{ category.name }}</td>
            <td class="muted description-cell">{{ category.description || '—' }}</td>
            <td class="muted">{{ category.sortOrder ?? 0 }}</td>
            <td class="muted">{{ category.articleCount ?? 0 }}</td>
            <td style="text-align:center">
              <button class="btn-edit" @click="openEdit(category)">编辑</button>
              <button
                class="btn-del"
                :disabled="deletingId === category.id"
                @click="remove(category)"
              >
                {{ deletingId === category.id ? '删除中…' : '删除' }}
              </button>
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
.btn-create { height:30px; padding:0 14px; font-size:12px; border-radius:6px; background:rgba(127,119,221,.12); border:.5px solid #AFA9EC; color:#534AB7; cursor:pointer; }
.form-card { background:var(--surface-2,#fff); border-radius:10px; border:.5px solid var(--border,#eee); padding:1rem 1.2rem; }
.form-title { font-size:13px; font-weight:500; color:var(--text-primary,#222); margin:0 0 1rem; }
.form-row { display:flex; align-items:center; gap:12px; margin-bottom:.8rem; }
.form-row label { font-size:12px; color:var(--text-muted,#777); width:64px; flex-shrink:0; }
.f-input { flex:1; height:30px; padding:0 10px; font-size:12px; border:.5px solid var(--border,#ddd); border-radius:6px; background:var(--surface-1,#f9f9f9); color:var(--text-primary,#222); outline:none; }
.f-input:focus { border-color:#AFA9EC; box-shadow:0 0 0 2px rgba(175,169,236,.12); }
.sort-input { max-width:140px; flex:none; }
.form-tip { font-size:11px; color:var(--text-muted,#999); }
.form-actions { display:flex; gap:8px; margin-top:.5rem; }
.btn-save, .btn-cancel { height:30px; padding:0 16px; font-size:12px; border-radius:6px; cursor:pointer; }
.btn-save { background:rgba(127,119,221,.12); border:.5px solid #AFA9EC; color:#534AB7; }
.btn-cancel { border:.5px solid var(--border,#ddd); background:transparent; color:var(--text-secondary,#666); }
.table-wrap { background:var(--surface-2,#fff); border-radius:10px; border:.5px solid var(--border,#eee); overflow:auto; }
.table { width:100%; border-collapse:collapse; font-size:12px; }
.table thead tr { background:var(--surface-1,#f9f9f9); border-bottom:.5px solid var(--border,#eee); }
.table th { padding:10px; text-align:left; font-weight:500; color:var(--text-muted,#777); white-space:nowrap; }
.table-row { border-bottom:.5px solid var(--border,#eee); transition:background .12s; }
.table-row:hover { background:var(--surface-1,#f9f9f9); }
.table td { padding:10px; vertical-align:middle; }
.category-name { font-weight:500; color:var(--text-primary,#222); }
.description-cell { max-width:360px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.muted { color:var(--text-secondary,#777); }
.state-cell { padding:2.5rem !important; text-align:center; color:var(--text-muted,#999); }
.btn-edit, .btn-del { font-size:11px; padding:3px 10px; border-radius:5px; cursor:pointer; }
.btn-edit { border:.5px solid #85B7EB; background:#E6F1FB; color:#185FA5; margin-right:4px; }
.btn-del { border:.5px solid #F09595; background:#FCEBEB; color:#A32D2D; }
button:disabled { opacity:.55; cursor:not-allowed; }
</style>
