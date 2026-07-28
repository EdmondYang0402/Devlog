<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import {
  createBackground,
  deleteBackground,
  getAdminBackgroundDetail,
  getAdminBackgroundPage,
  updateBackground
} from '@/api/siteBackground.js'
import { adminImageUploadService } from '@/api/site.js'
import { formatDateTime } from '@/utils/date.js'

const { t, locale } = useI18n()
const formRef = ref(null)
const records = ref([])
const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const switchingIds = ref(new Set())
const previewFailed = ref(false)
const query = reactive({ keyword: '', enabled: '' })
const emptyForm = () => ({ imageUrl: '', title: '', enabled: 1, sortOrder: 0 })
const form = reactive(emptyForm())

const validateEnabled = (_rule, value, callback) => Number(value) === 0 || Number(value) === 1
  ? callback()
  : callback(new Error(t('siteBackground.validation.enabled')))
const validateSortOrder = (_rule, value, callback) => {
  const normalized = Number(value)
  if (!Number.isInteger(normalized)) return callback(new Error(t('siteBackground.validation.sortOrder')))
  if (normalized < -100000 || normalized > 100000) return callback(new Error(t('siteBackground.validation.sortRange')))
  callback()
}
const rules = {
  imageUrl: [
    { required: true, whitespace: true, message: () => t('siteBackground.validation.imageRequired'), trigger: 'blur' },
    { max: 500, message: () => t('siteBackground.validation.imageLength'), trigger: 'blur' },
    { pattern: /^https?:\/\/[^\s]+$/i, message: () => t('siteBackground.validation.imageUrl'), trigger: 'blur' }
  ],
  title: [{ max: 100, message: () => t('siteBackground.validation.titleLength'), trigger: 'blur' }],
  enabled: [{ validator: validateEnabled, trigger: 'change' }],
  sortOrder: [{ validator: validateSortOrder, trigger: 'change' }]
}

const normalizeRecord = item => ({
  ...item,
  enabled: Number(item?.enabled) === 1 ? 1 : 0,
  sortOrder: Number.isInteger(Number(item?.sortOrder)) ? Number(item.sortOrder) : 0
})
const payloadFrom = source => ({
  imageUrl: String(source.imageUrl || '').trim(),
  title: String(source.title || '').trim() || null,
  enabled: Number(source.enabled) === 1 ? 1 : 0,
  sortOrder: Number(source.sortOrder)
})
const setSwitching = (id, active) => {
  const next = new Set(switchingIds.value)
  active ? next.add(id) : next.delete(id)
  switchingIds.value = next
}

const load = async () => {
  loading.value = true
  try {
    const result = await getAdminBackgroundPage({
      page: page.value,
      size: size.value,
      keyword: query.keyword.trim() || undefined,
      enabled: query.enabled === '' ? undefined : Number(query.enabled)
    })
    const payload = result?.data ?? result ?? {}
    records.value = Array.isArray(payload.records) ? payload.records.map(normalizeRecord) : []
    total.value = Number(payload.total ?? records.value.length)
  } catch (error) {
    records.value = []
    total.value = 0
    ElMessage.error(error.response?.data?.message || t('siteBackground.loadFailed'))
  } finally {
    loading.value = false
  }
}

const search = () => { page.value = 1; load() }
const reset = () => { Object.assign(query, { keyword: '', enabled: '' }); page.value = 1; load() }
const openCreate = () => {
  editingId.value = null
  previewFailed.value = false
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}
const openEdit = async id => {
  try {
    const result = await getAdminBackgroundDetail(id)
    const detail = result?.data ?? result
    editingId.value = id
    previewFailed.value = false
    Object.assign(form, emptyForm(), normalizeRecord(detail || {}))
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error(error.response?.data?.message || t('siteBackground.detailFailed'))
  }
}

const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const payload = payloadFrom(form)
    if (editingId.value !== null) await updateBackground(editingId.value, payload)
    else await createBackground(payload)
    ElMessage.success(t('siteBackground.saveSuccess'))
    dialogVisible.value = false
    await load()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || t('siteBackground.saveFailed'))
  } finally {
    saving.value = false
  }
}

const toggleEnabled = async (row, value) => {
  if (switchingIds.value.has(row.id)) return
  const previous = Number(row.enabled) === 1 ? 1 : 0
  row.enabled = Number(value) === 1 ? 1 : 0
  setSwitching(row.id, true)
  try {
    await updateBackground(row.id, payloadFrom(row))
    await load()
  } catch (error) {
    // 开关先乐观更新；接口失败必须回滚，避免后台显示与服务端状态不一致。
    row.enabled = previous
    ElMessage.error(error.response?.data?.message || t('siteBackground.updateStatusFailed'))
  } finally {
    setSwitching(row.id, false)
  }
}

const remove = async row => {
  try {
    // 删除只移除背景配置记录，不删除可能仍被其他业务引用的文件。
    await ElMessageBox.confirm(
      `${t('siteBackground.deleteConfirm', { title: row.title || row.id })}\n${t('siteBackground.deleteOssNotice')}`,
      t('siteBackground.deleteTitle'),
      { type: 'warning', confirmButtonText: t('common.delete'), cancelButtonText: t('common.cancel') }
    )
    await deleteBackground(row.id)
    if (records.value.length === 1 && page.value > 1) page.value -= 1
    ElMessage.success(t('siteBackground.deleteSuccess'))
    await load()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.response?.data?.message || t('siteBackground.deleteFailed'))
    }
  }
}

const uploadImage = async ({ file, onSuccess, onError }) => {
  if (!file.type?.startsWith('image/') || file.size > 5 * 1024 * 1024) {
    const error = new Error(t('siteBackground.imageInvalid'))
    onError?.(error)
    return ElMessage.error(error.message)
  }
  uploading.value = true
  try {
    const result = await adminImageUploadService(file)
    const imageUrl = result?.data?.url
    if (!imageUrl) throw new Error(t('siteBackground.uploadFailed'))
    form.imageUrl = imageUrl
    previewFailed.value = false
    onSuccess?.(result)
    ElMessage.success(t('siteBackground.uploadSuccess'))
  } catch (error) {
    onError?.(error)
    ElMessage.error(error.response?.data?.message || error.message || t('siteBackground.uploadFailed'))
  } finally {
    uploading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="background-admin">
    <header class="page-header">
      <div><h1>{{ t('siteBackground.title') }}</h1><p>{{ t('siteBackground.subtitle') }}</p></div>
      <el-button type="primary" @click="openCreate"><i class="ti ti-plus" aria-hidden="true"></i>{{ t('siteBackground.create') }}</el-button>
    </header>

    <section class="search-bar" :aria-label="t('siteBackground.filters')">
      <el-input v-model="query.keyword" clearable :placeholder="t('siteBackground.searchPlaceholder')" @keyup.enter="search" />
      <el-select v-model="query.enabled" clearable :placeholder="t('siteBackground.allStatus')" @change="search">
        <el-option :label="t('siteBackground.enabled')" :value="1" />
        <el-option :label="t('siteBackground.disabled')" :value="0" />
      </el-select>
      <el-button type="primary" plain @click="search">{{ t('common.search') }}</el-button>
      <el-button @click="reset">{{ t('common.refresh') }}</el-button>
    </section>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="records" height="100%">
        <el-table-column width="126" :label="t('siteBackground.image')">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" class="thumbnail" :src="row.imageUrl" :preview-src-list="[row.imageUrl]" preview-teleported fit="cover">
              <template #error><span class="image-placeholder"><i class="ti ti-photo-off" aria-hidden="true"></i></span></template>
            </el-image>
            <span v-else class="image-placeholder"><i class="ti ti-photo-off" aria-hidden="true"></i></span>
          </template>
        </el-table-column>
        <el-table-column prop="title" min-width="170" show-overflow-tooltip :label="t('siteBackground.backendTitle')">
          <template #default="{ row }">{{ row.title || '—' }}</template>
        </el-table-column>
        <el-table-column width="96" :label="t('siteBackground.enabledLabel')">
          <template #default="{ row }">
            <el-switch :model-value="row.enabled" :active-value="1" :inactive-value="0" :loading="switchingIds.has(row.id)" :disabled="switchingIds.has(row.id)" @change="value => toggleEnabled(row, value)" />
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" width="110" :label="t('siteBackground.sortOrder')" />
        <el-table-column width="170" :label="t('siteBackground.createdAt')"><template #default="{ row }">{{ formatDateTime(row.createTime, locale) }}</template></el-table-column>
        <el-table-column width="170" :label="t('siteBackground.updatedAt')"><template #default="{ row }">{{ formatDateTime(row.updateTime, locale) }}</template></el-table-column>
        <el-table-column fixed="right" width="130" :label="t('siteBackground.actions')">
          <template #default="{ row }"><el-button link type="primary" @click="openEdit(row.id)">{{ t('siteBackground.edit') }}</el-button><el-button link type="danger" @click="remove(row)">{{ t('common.delete') }}</el-button></template>
        </el-table-column>
        <template #empty>{{ t('siteBackground.empty') }}</template>
      </el-table>
    </div>

    <div class="table-footer">
      <span>{{ t('siteBackground.sortHint') }}</span>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="load" />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId !== null ? t('siteBackground.editTitle') : t('siteBackground.createTitle')" width="min(640px, calc(100vw - 24px))" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item :label="t('siteBackground.image')" prop="imageUrl">
          <div class="upload-row">
            <el-input v-model="form.imageUrl" :placeholder="t('siteBackground.imageUrlPlaceholder')" @input="previewFailed = false" />
            <el-upload accept="image/*" :show-file-list="false" :http-request="uploadImage">
              <el-button :loading="uploading">{{ form.imageUrl ? t('siteBackground.replaceImage') : t('siteBackground.uploadImage') }}</el-button>
            </el-upload>
          </div>
          <div v-if="form.imageUrl" class="form-preview">
            <img v-if="!previewFailed" :src="form.imageUrl" :alt="form.title || t('siteBackground.preview')" @error="previewFailed = true" />
            <span v-else class="preview-placeholder"><i class="ti ti-photo-off" aria-hidden="true"></i>{{ t('siteBackground.previewFailed') }}</span>
          </div>
        </el-form-item>
        <el-form-item :label="t('siteBackground.backendTitle')" prop="title"><el-input v-model="form.title" maxlength="100" show-word-limit :placeholder="t('siteBackground.titlePlaceholder')" /></el-form-item>
        <div class="form-grid">
          <el-form-item :label="t('siteBackground.enabledLabel')" prop="enabled"><el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" :active-text="t('siteBackground.enabled')" :inactive-text="t('siteBackground.disabled')" /></el-form-item>
          <el-form-item :label="t('siteBackground.sortOrder')" prop="sortOrder"><el-input-number v-model="form.sortOrder" :min="-100000" :max="100000" :precision="0" :step="1" controls-position="right" /><small>{{ t('siteBackground.sortHint') }}</small></el-form-item>
        </div>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button><el-button type="primary" :loading="saving" :disabled="uploading" @click="submit">{{ t('common.save') }}</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.background-admin{display:flex;height:100%;min-height:520px;flex-direction:column;gap:14px}.page-header{display:flex;align-items:flex-start;justify-content:space-between;gap:20px}.page-header h1{margin:0;color:var(--text-primary);font-size:22px}.page-header p{margin:3px 0 0;color:var(--text-muted);font-size:12px}.page-header .el-button{display:inline-flex;align-items:center;gap:5px}.search-bar{display:flex;align-items:center;gap:8px;padding:12px;background:var(--surface-2);border:1px solid var(--border);border-radius:10px}.search-bar>.el-input{width:240px}.search-bar>.el-select{width:150px}.table-wrap{min-height:340px;flex:1;overflow:hidden;background:var(--surface-2);border:1px solid var(--border);border-radius:10px}.thumbnail,.image-placeholder{display:flex;width:96px;height:54px;align-items:center;justify-content:center;border-radius:6px;background:var(--surface-1);color:var(--text-muted);font-size:20px}.thumbnail{cursor:zoom-in}.table-footer{display:flex;align-items:center;justify-content:space-between;gap:20px;color:var(--text-muted);font-size:11px}.upload-row{display:flex;width:100%;gap:8px}.form-preview{width:100%;margin-top:10px}.form-preview img,.preview-placeholder{display:flex;width:100%;height:180px;align-items:center;justify-content:center;gap:7px;border-radius:9px;background:var(--surface-1);color:var(--text-muted);object-fit:cover}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:0 18px}.form-grid small{display:block;margin-top:5px;color:var(--text-muted);font-size:10px}@media(max-width:720px){.background-admin{height:auto}.page-header{align-items:stretch;flex-direction:column}.search-bar{align-items:stretch;flex-direction:column}.search-bar>.el-input,.search-bar>.el-select{width:100%}.table-wrap{overflow:auto}.table-footer{align-items:flex-start;flex-direction:column}.upload-row{align-items:stretch;flex-direction:column}.form-grid{grid-template-columns:1fr}}
</style>
