<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import {
  createMediaReview, deleteMediaReview, getAdminMediaReviewDetail,
  getAdminMediaReviewPage, updateMediaReview
} from '@/api/mediaReview.js'
import { adminImageUploadService } from '@/api/site.js'
import { mediaTypeOptions, statusOptions, typeKey, statusKey } from '@/constants/mediaReview.js'
import MediaRating from '@/components/media/MediaRating.vue'

const { t } = useI18n()
const formRef = ref()
const records = ref([])
const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const query = reactive({ title: '', mediaType: '', status: '' })
const emptyForm = () => ({ title:'', mediaType:0, status:0, coverUrl:'', rating:null, shortReview:'', content:'', finishedDate:null })
const form = reactive(emptyForm())
const rules = {
  title: [{ required:true, message:() => t('media.admin.titleRequired'), trigger:'blur' }, { max:200, message:() => t('media.admin.titleTooLong'), trigger:'blur' }],
  mediaType: [{ required:true, message:() => t('media.admin.typeRequired'), trigger:'change' }],
  status: [{ required:true, message:() => t('media.admin.statusRequired'), trigger:'change' }],
  coverUrl: [{ max:500, message:() => t('media.admin.coverTooLong'), trigger:'blur' }, { pattern:/^(https?:\/\/.*)?$/, message:() => t('media.admin.coverInvalid'), trigger:'blur' }],
  shortReview: [{ max:500, message:() => t('media.admin.shortReviewTooLong'), trigger:'blur' }]
}

const load = async () => {
  loading.value = true
  try {
    const result = await getAdminMediaReviewPage({ page:page.value, size:size.value, title:query.title || undefined, mediaType:query.mediaType === '' ? undefined : query.mediaType, status:query.status === '' ? undefined : query.status })
    records.value = result.data?.records || []
    total.value = Number(result.data?.total || 0)
  } catch (error) {
    records.value = []; total.value = 0
    ElMessage.error(error.response?.data?.message || t('media.admin.loadFailed'))
  } finally { loading.value = false }
}
const search = () => { page.value = 1; load() }
const reset = () => { Object.assign(query,{title:'',mediaType:'',status:''}); page.value=1; load() }
const openCreate = () => { editingId.value=null; Object.assign(form,emptyForm()); dialogVisible.value=true }
const openEdit = async id => {
  try {
    const result = await getAdminMediaReviewDetail(id)
    editingId.value=id; Object.assign(form,emptyForm(),result.data || {}); dialogVisible.value=true
  } catch (error) { ElMessage.error(error.response?.data?.message || t('media.admin.detailFailed')) }
}
const submit = async () => {
  await formRef.value?.validate()
  if (form.rating != null && (!Number.isInteger(form.rating) || form.rating < 1 || form.rating > 10)) return ElMessage.error(t('media.admin.ratingInvalid'))
  saving.value=true
  try {
    const payload={...form,coverUrl:form.coverUrl?.trim() || null,shortReview:form.shortReview?.trim() || null,content:form.content?.trim() || null}
    if (editingId.value) await updateMediaReview(editingId.value,payload)
    else await createMediaReview(payload)
    ElMessage.success(t('media.admin.saveSuccess')); dialogVisible.value=false; load()
  } catch (error) { ElMessage.error(error.response?.data?.message || t('media.admin.saveFailed')) }
  finally { saving.value=false }
}
const remove = async row => {
  try {
    await ElMessageBox.confirm(t('media.admin.deleteConfirm',{title:row.title}),t('media.admin.deleteTitle'),{type:'warning',confirmButtonText:t('common.delete'),cancelButtonText:t('common.cancel')})
    await deleteMediaReview(row.id); ElMessage.success(t('media.admin.deleteSuccess')); load()
  } catch (error) { if (error !== 'cancel' && error !== 'close') ElMessage.error(error.response?.data?.message || t('media.admin.deleteFailed')) }
}
const uploadCover = async ({ file, onSuccess, onError }) => {
  if (!file.type.startsWith('image/') || file.size > 5*1024*1024) return ElMessage.error(t('media.admin.imageInvalid'))
  uploading.value=true
  try { const result=await adminImageUploadService(file); const url=result?.data?.url; if(!url) throw new Error(t('media.admin.uploadFailed')); form.coverUrl=url; onSuccess?.(result); ElMessage.success(t('media.admin.uploadSuccess')) }
  catch(error){onError?.(error);ElMessage.error(error.response?.data?.message || error.message || t('media.admin.uploadFailed'))}
  finally{uploading.value=false}
}
const formatRating = rating => rating == null
  ? t('media.unrated')
  : t('media.admin.ratingDisplay', { stars: Number(rating) / 2, score: rating })
onMounted(load)
</script>

<template>
  <div class="admin-media">
    <div class="search-bar">
      <el-input v-model="query.title" clearable :placeholder="t('media.admin.searchPlaceholder')" @keyup.enter="search" />
      <el-select v-model="query.mediaType" clearable :placeholder="t('media.typeLabel')"><el-option v-for="type in mediaTypeOptions" :key="type.value" :label="t(`media.type.${type.key}`)" :value="type.value" /></el-select>
      <el-select v-model="query.status" clearable :placeholder="t('media.statusLabel')"><el-option v-for="status in statusOptions" :key="status.value" :label="t(`media.status.generic.${status.key}`)" :value="status.value" /></el-select>
      <el-button type="primary" plain @click="search">{{ t('common.search') }}</el-button><el-button @click="reset">{{ t('common.refresh') }}</el-button>
      <el-button class="create" type="primary" @click="openCreate"><i class="ti ti-plus"></i>{{ t('media.admin.create') }}</el-button>
    </div>
    <div class="table-wrap">
      <el-table v-loading="loading" :data="records" height="100%">
        <el-table-column width="72" :label="t('media.cover')"><template #default="{row}"><img v-if="row.coverUrl" class="thumb" :src="row.coverUrl" :alt="row.title" /><span v-else>—</span></template></el-table-column>
        <el-table-column prop="title" min-width="180" show-overflow-tooltip :label="t('media.workTitle')" />
        <el-table-column width="90" :label="t('media.typeLabel')"><template #default="{row}">{{ t(`media.type.${typeKey(row.mediaType)}`) }}</template></el-table-column>
        <el-table-column width="100" :label="t('media.statusLabel')"><template #default="{row}">{{ t(statusKey(row.mediaType,row.status)) }}</template></el-table-column>
        <el-table-column width="130" :label="t('media.rating')"><template #default="{row}">{{ formatRating(row.rating) }}</template></el-table-column>
        <el-table-column prop="finishedDate" width="120" :label="t('media.finishedDate')" />
        <el-table-column prop="updateTime" width="170" :label="t('media.admin.updatedAt')" />
        <el-table-column fixed="right" width="130" :label="t('media.admin.actions')"><template #default="{row}"><el-button link type="primary" @click="openEdit(row.id)">{{ t('media.admin.edit') }}</el-button><el-button link type="danger" @click="remove(row)">{{ t('common.delete') }}</el-button></template></el-table-column>
        <template #empty>{{ t('media.empty') }}</template>
      </el-table>
    </div>
    <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="load" />

    <el-dialog v-model="dialogVisible" :title="editingId ? t('media.admin.editTitle') : t('media.admin.createTitle')" width="min(680px, calc(100vw - 24px))" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="media-form">
        <div class="form-grid"><el-form-item :label="t('media.workTitle')" prop="title"><el-input v-model="form.title" maxlength="200" show-word-limit /></el-form-item>
        <el-form-item :label="t('media.typeLabel')" prop="mediaType"><el-select v-model="form.mediaType"><el-option v-for="type in mediaTypeOptions" :key="type.value" :label="t(`media.type.${type.key}`)" :value="type.value" /></el-select></el-form-item>
        <el-form-item :label="t('media.statusLabel')" prop="status"><el-select v-model="form.status"><el-option v-for="status in statusOptions" :key="status.value" :label="t(statusKey(form.mediaType,status.value))" :value="status.value" /></el-select></el-form-item>
        <el-form-item :label="t('media.finishedDate')"><el-date-picker v-model="form.finishedDate" type="date" value-format="YYYY-MM-DD" :placeholder="t('media.admin.datePlaceholder')" /></el-form-item></div>
        <el-form-item :label="t('media.rating')"><MediaRating v-model="form.rating" /><small class="hint">{{ t('media.admin.ratingHint') }}</small></el-form-item>
        <el-form-item :label="t('media.cover')" prop="coverUrl"><div class="cover-field"><el-input v-model="form.coverUrl" :placeholder="t('media.admin.coverPlaceholder')" /><el-upload :show-file-list="false" :http-request="uploadCover"><el-button :loading="uploading">{{ t('media.admin.uploadCover') }}</el-button></el-upload></div><img v-if="form.coverUrl" class="preview" :src="form.coverUrl" :alt="form.title" /></el-form-item>
        <el-form-item :label="t('media.shortReview')" prop="shortReview"><el-input v-model="form.shortReview" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item>
        <el-form-item :label="t('media.longReview')"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">{{ t('common.cancel') }}</el-button><el-button type="primary" :loading="saving" @click="submit">{{ t('common.save') }}</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-media{display:flex;flex-direction:column;gap:14px;height:100%}.search-bar{display:flex;align-items:center;gap:8px;padding:12px;border:1px solid var(--border);border-radius:10px;background:var(--surface-2)}.search-bar>.el-input{width:220px}.search-bar>.el-select{width:130px}.create{margin-left:auto}.table-wrap{min-height:360px;flex:1;overflow:hidden;border:1px solid var(--border);border-radius:10px;background:var(--surface-2)}.thumb{width:38px;height:52px;border-radius:5px;object-fit:cover}.form-grid{display:grid;grid-template-columns:2fr 1fr;gap:0 14px}.media-form :deep(.el-select),.media-form :deep(.el-date-editor){width:100%}.cover-field{display:flex;width:100%;gap:8px}.preview{display:block;width:76px;height:100px;margin-top:10px;border-radius:7px;object-fit:cover}.hint{margin-left:10px;color:var(--text-3)}@media(max-width:700px){.admin-media{height:auto}.search-bar{align-items:stretch;flex-direction:column}.search-bar>.el-input,.search-bar>.el-select{width:100%}.create{margin-left:0}.form-grid{grid-template-columns:1fr}.cover-field{align-items:stretch;flex-direction:column}.table-wrap{overflow:auto}.hint{display:block;margin:6px 0 0}}
</style>
