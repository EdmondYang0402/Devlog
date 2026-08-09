<script setup>
import { onMounted, ref } from 'vue'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import {
  createCategory,
  deleteCategory,
  getAdminCategoryTags,
  getAdminCategoryList,
  updateAdminCategoryTags,
  updateCategory
} from '@/api/category'
import { getAdminTagOptions } from '@/api/tag'

const categories = ref([])
const form = ref(createEmptyForm())
const editId = ref(null)
const showForm = ref(false)
const loading = ref(false)
const saving = ref(false)
const deletingId = ref(null)
const tagDialogVisible = ref(false)
const bindingCategory = ref(null)
const tagOptions = ref([])
const tagOptionsLoaded = ref(false)
const selectedTagIds = ref([])
const bindingLoading = ref(false)
const bindingSaving = ref(false)

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

async function openTagBinding(category) {
  bindingCategory.value = category
  selectedTagIds.value = []
  tagDialogVisible.value = true
  bindingLoading.value = true

  try {
    const optionsRequest = tagOptionsLoaded.value
      ? Promise.resolve(null)
      : getAdminTagOptions()
    const [bindingResult, optionsResult] = await Promise.all([
      getAdminCategoryTags(category.id),
      optionsRequest
    ])

    if (optionsResult) {
      tagOptions.value = Array.isArray(optionsResult?.data) ? optionsResult.data : []
      tagOptionsLoaded.value = true
    }
    selectedTagIds.value = Array.isArray(bindingResult?.data)
      ? bindingResult.data.map(tag => Number(tag.id))
      : []
  } catch (error) {
    tagDialogVisible.value = false
    bindingCategory.value = null
    ElMessage.error(errorMessage(error, '加载分类标签关系失败'))
  } finally {
    bindingLoading.value = false
  }
}

function selectAllTags() {
  selectedTagIds.value = tagOptions.value.slice(0, 100).map(tag => Number(tag.id))
  if (tagOptions.value.length > 100) {
    ElMessage.warning('一个分类最多关联 100 个标签，已选择前 100 个')
  }
}

function clearSelectedTags() {
  selectedTagIds.value = []
}

async function saveTagBinding() {
  if (!bindingCategory.value || bindingLoading.value) return

  bindingSaving.value = true
  try {
    await updateAdminCategoryTags(bindingCategory.value.id, [...selectedTagIds.value])
    ElMessage.success(`“${bindingCategory.value.name}”的标签绑定已保存`)
    tagDialogVisible.value = false
    bindingCategory.value = null
  } catch (error) {
    ElMessage.error(errorMessage(error, '保存分类标签关系失败'))
  } finally {
    bindingSaving.value = false
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
            <th style="width:220px;text-align:center">操作</th>
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
            <td class="actions-cell">
              <button class="btn-bind" @click="openTagBinding(category)">绑定标签</button>
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

    <el-dialog
      v-model="tagDialogVisible"
      :title="bindingCategory ? `绑定标签 · ${bindingCategory.name}` : '绑定标签'"
      width="min(620px, calc(100vw - 24px))"
      :close-on-click-modal="!bindingSaving"
      :close-on-press-escape="!bindingSaving"
      class="category-tag-dialog"
    >
      <div v-if="bindingLoading" class="tag-binding-state">正在加载标签…</div>
      <div v-else-if="tagOptions.length === 0" class="tag-binding-state">
        <span>还没有可绑定的标签</span>
        <router-link to="/admin/tags">前往标签管理</router-link>
      </div>
      <div v-else class="tag-binding-content">
        <div class="tag-binding-toolbar">
          <span>已选择 {{ selectedTagIds.length }} / {{ tagOptions.length }}</span>
          <div>
            <button type="button" :disabled="bindingSaving" @click="selectAllTags">全选</button>
            <button type="button" :disabled="bindingSaving || selectedTagIds.length === 0" @click="clearSelectedTags">清空</button>
          </div>
        </div>
        <div class="tag-option-grid">
          <label v-for="tag in tagOptions" :key="tag.id" class="tag-option">
            <input
              v-model="selectedTagIds"
              type="checkbox"
              :value="Number(tag.id)"
              :disabled="bindingSaving || (selectedTagIds.length >= 100 && !selectedTagIds.includes(Number(tag.id)))"
            />
            <span>#{{ tag.name }}</span>
          </label>
        </div>
        <p class="tag-binding-tip">保存后会整体替换该分类当前绑定的标签，不会修改文章自身的标签。</p>
      </div>
      <template #footer>
        <el-button :disabled="bindingSaving" @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="bindingSaving" :disabled="bindingLoading || tagOptions.length === 0" @click="saveTagBinding">保存绑定</el-button>
      </template>
    </el-dialog>
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
.table { width:100%; min-width:860px; border-collapse:collapse; font-size:12px; }
.table thead tr { background:var(--surface-1,#f9f9f9); border-bottom:.5px solid var(--border,#eee); }
.table th { padding:10px; text-align:left; font-weight:500; color:var(--text-muted,#777); white-space:nowrap; }
.table-row { border-bottom:.5px solid var(--border,#eee); transition:background .12s; }
.table-row:hover { background:var(--surface-1,#f9f9f9); }
.table td { padding:10px; vertical-align:middle; }
.category-name { font-weight:500; color:var(--text-primary,#222); }
.description-cell { max-width:360px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.muted { color:var(--text-secondary,#777); }
.state-cell { padding:2.5rem !important; text-align:center; color:var(--text-muted,#999); }
.actions-cell { text-align:center; white-space:nowrap; }
.btn-bind, .btn-edit, .btn-del { font-size:11px; padding:3px 10px; border-radius:5px; cursor:pointer; }
.btn-bind { margin-right:4px; border:.5px solid rgba(175,169,236,.8); background:rgba(127,119,221,.12); color:var(--purple-600); }
.btn-edit { border:.5px solid #85B7EB; background:#E6F1FB; color:#185FA5; margin-right:4px; }
.btn-del { border:.5px solid #F09595; background:#FCEBEB; color:#A32D2D; }
.tag-binding-state { min-height:180px; display:flex; align-items:center; justify-content:center; flex-direction:column; gap:10px; color:var(--text-muted,#999); font-size:12px; }
.tag-binding-state a { color:var(--admin-accent,var(--purple-600)); }
.tag-binding-content { display:flex; flex-direction:column; gap:14px; }
.tag-binding-toolbar { display:flex; align-items:center; justify-content:space-between; gap:12px; padding-bottom:12px; border-bottom:1px solid var(--border,#eee); color:var(--text-secondary,#777); font-size:11px; }
.tag-binding-toolbar > div { display:flex; gap:6px; }
.tag-binding-toolbar button { height:28px; padding:0 10px; border:1px solid var(--border,#ddd); border-radius:7px; background:transparent; color:var(--text-secondary,#666); cursor:pointer; }
.tag-option-grid { max-height:320px; display:grid; grid-template-columns:repeat(3,minmax(0,1fr)); gap:8px; overflow:hidden auto; padding:2px; }
.tag-option { min-width:0; min-height:38px; display:flex; align-items:center; gap:8px; padding:7px 10px; border:1px solid var(--border,#ddd); border-radius:9px; background:rgba(255,255,255,.025); color:var(--text-secondary,#666); cursor:pointer; transition:background .16s ease,border-color .16s ease,color .16s ease; }
.tag-option:hover { border-color:var(--border-h,#AFA9EC); background:rgba(127,119,221,.07); color:var(--text-primary,#222); }
.tag-option:has(input:checked) { border-color:rgba(175,169,236,.62); background:rgba(127,119,221,.12); color:var(--text-primary,#222); }
.tag-option input { width:14px; height:14px; flex:0 0 14px; accent-color:var(--admin-accent,var(--purple-400)); }
.tag-option span { overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.tag-binding-tip { margin:0; color:var(--text-muted,#999); font-size:10px; line-height:1.6; }
button:disabled { opacity:.55; cursor:not-allowed; }
@media(max-width:700px){.tag-option-grid{grid-template-columns:repeat(2,minmax(0,1fr))}.tag-binding-toolbar{align-items:flex-start;flex-direction:column}}
@media(max-width:430px){.tag-option-grid{grid-template-columns:1fr}}
</style>
