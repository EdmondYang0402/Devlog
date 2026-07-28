<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import { createTag, deleteTag, getAdminTags, updateTag } from '@/api/tag'
import { formatDateTime } from '@/utils/date.js'

const { locale, t } = useI18n()
const tags = ref([])
const loading = ref(false)
const saving = ref(false)
const deletingId = ref(null)
const dialogVisible = ref(false)
const editId = ref(null)
const form = reactive({ name: '' })

const errorMessage = (error, fallback) =>
  error?.response?.data?.message || error?.message || fallback

async function loadTags() {
  loading.value = true
  try {
    const result = await getAdminTags()
    tags.value = Array.isArray(result?.data) ? result.data : []
  } catch (error) {
    tags.value = []
    ElMessage.error(errorMessage(error, t('adminTag.loadFailed')))
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editId.value = null
  form.name = ''
  dialogVisible.value = true
}

function openEdit(tag) {
  editId.value = tag.id
  form.name = tag.name || ''
  dialogVisible.value = true
}

async function saveTag() {
  const name = form.name.trim()
  if (!name) return ElMessage.warning(t('adminTag.nameRequired'))
  if (name.length > 50) return ElMessage.warning(t('adminTag.nameTooLong'))

  saving.value = true
  try {
    if (editId.value === null) {
      await createTag({ name })
      ElMessage.success(t('adminTag.createSuccess'))
    } else {
      await updateTag(editId.value, { name })
      ElMessage.success(t('adminTag.updateSuccess'))
    }
    dialogVisible.value = false
    await loadTags()
  } catch (error) {
    ElMessage.error(errorMessage(error,
      editId.value === null ? t('adminTag.createFailed') : t('adminTag.updateFailed')))
  } finally {
    saving.value = false
  }
}

async function removeTag(tag) {
  try {
    await ElMessageBox.confirm(
      t('adminTag.deleteConfirm', { name: tag.name }),
      t('adminTag.deleteTitle'),
      { confirmButtonText: t('common.delete'), cancelButtonText: t('common.cancel'), type: 'warning' }
    )
    deletingId.value = tag.id
    await deleteTag(tag.id)
    ElMessage.success(t('adminTag.deleteSuccess'))
    await loadTags()
  } catch (error) {
    if (error === 'cancel' || error === 'close') return
    ElMessage.error(errorMessage(error, t('adminTag.deleteFailed')))
  } finally {
    deletingId.value = null
  }
}

onMounted(loadTags)
</script>

<template>
  <div class="tag-page">
    <div class="tag-toolbar">
      <div>
        <h1>{{ t('adminTag.title') }}</h1>
        <p>{{ t('adminTag.subtitle') }}</p>
      </div>
      <button class="btn-create" :disabled="loading" @click="openCreate">
        <i class="ti ti-plus" aria-hidden="true"></i>{{ t('adminTag.create') }}
      </button>
    </div>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>{{ t('adminTag.name') }}</th>
            <th>{{ t('adminTag.articleCount') }}</th>
            <th>{{ t('adminTag.createdAt') }}</th>
            <th>{{ t('adminTag.updatedAt') }}</th>
            <th class="actions-column">{{ t('adminTag.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading"><td colspan="6" class="state-cell">{{ t('common.loading') }}</td></tr>
          <tr v-else-if="tags.length === 0"><td colspan="6" class="state-cell">{{ t('adminTag.empty') }}</td></tr>
          <tr v-for="tag in tags" v-else :key="tag.id" class="table-row">
            <td class="muted">{{ tag.id }}</td>
            <td><span class="tag-chip">#{{ tag.name }}</span></td>
            <td class="muted">{{ tag.articleCount ?? 0 }}</td>
            <td class="muted nowrap">{{ formatDateTime(tag.createTime, locale) }}</td>
            <td class="muted nowrap">{{ formatDateTime(tag.updateTime, locale) }}</td>
            <td class="actions-column">
              <button class="btn-edit" @click="openEdit(tag)">{{ t('adminTag.edit') }}</button>
              <button class="btn-delete" :disabled="deletingId === tag.id" @click="removeTag(tag)">
                {{ deletingId === tag.id ? t('adminTag.deleting') : t('common.delete') }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editId === null ? t('adminTag.create') : t('adminTag.edit')" width="min(420px, 92vw)" :close-on-click-modal="!saving">
      <label class="dialog-field">
        <span>{{ t('adminTag.name') }}</span>
        <el-input v-model="form.name" maxlength="50" show-word-limit :placeholder="t('adminTag.namePlaceholder')" @keyup.enter="saveTag" />
      </label>
      <template #footer>
        <el-button :disabled="saving" @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="saving" @click="saveTag">{{ t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.tag-page { display: flex; flex-direction: column; gap: 1rem; }
.tag-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 1rem; padding: 1rem 1.2rem; background: var(--surface-2,#fff); border: .5px solid var(--border,#eee); border-radius: 12px; }
.tag-toolbar h1 { margin: 0; color: var(--text-primary,#222); font-size: 16px; }
.tag-toolbar p { margin: 3px 0 0; color: var(--text-muted,#999); font-size: 11px; }
.btn-create { display: inline-flex; align-items: center; gap: 5px; flex: 0 0 auto; height: 32px; padding: 0 14px; border: .5px solid #AFA9EC; border-radius: 7px; background: rgba(127,119,221,.12); color: #534AB7; cursor: pointer; }
.table-wrap { overflow-x: auto; background: var(--surface-2,#fff); border: .5px solid var(--border,#eee); border-radius: 12px; }
.table { width: 100%; min-width: 760px; border-collapse: collapse; font-size: 12px; }
.table thead { background: var(--surface-1,#f9f9f9); color: var(--text-muted,#777); }
.table th, .table td { padding: 11px 12px; text-align: left; border-bottom: .5px solid var(--border,#eee); }
.table th { font-weight: 500; white-space: nowrap; }
.table-row:hover { background: var(--surface-1,#f9f9f9); }
.muted { color: var(--text-secondary,#777); }
.nowrap { white-space: nowrap; }
.tag-chip { display: inline-block; max-width: 240px; padding: 3px 10px; overflow: hidden; background: var(--purple-50); border: .5px solid var(--purple-200); border-radius: 999px; color: var(--purple-600); font-weight: 600; text-overflow: ellipsis; white-space: nowrap; }
.actions-column { text-align: center !important; white-space: nowrap; }
.btn-edit, .btn-delete { padding: 3px 10px; border-radius: 5px; font-size: 11px; cursor: pointer; }
.btn-edit { margin-right: 5px; border: .5px solid #85B7EB; background: #E6F1FB; color: #185FA5; }
.btn-delete { border: .5px solid #F09595; background: #FCEBEB; color: #A32D2D; }
.state-cell { padding: 3rem !important; text-align: center !important; color: var(--text-muted,#999); }
.dialog-field { display: flex; flex-direction: column; gap: 8px; color: var(--text-secondary,#666); font-size: 12px; }
button:disabled { opacity: .55; cursor: not-allowed; }
@media (max-width: 599px) { .tag-toolbar { align-items: flex-start; } .tag-toolbar p { max-width: 190px; } }
</style>
