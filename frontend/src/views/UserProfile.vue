<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { userAvatarUploadService, userInfoService, userUpdateProfileService } from '@/api/user.js'
import { useCurrentUser } from '@/composables/useCurrentUser.js'

const { t } = useI18n()
const { updateCurrentUser } = useCurrentUser()
const formRef = ref()
const loading = ref(true)
const saving = ref(false)
const avatarUploading = ref(false)
const avatarPreviewUrl = ref('')
const avatarLoadFailed = ref(false)

const form = reactive({
  username: '',
  email: '',
  nickname: '',
  avatar: ''
})

const avatarInitial = computed(() => {
  const source = form.nickname?.trim() || form.username?.trim() || form.email?.trim() || 'U'
  return (Array.from(source)[0] || 'U').toUpperCase()
})
const avatarDisplay = computed(() => (
  avatarLoadFailed.value ? '' : (avatarPreviewUrl.value || form.avatar)
))

const validateNickname = (_rule, value, callback) => {
  const normalized = value?.trim() || ''
  if (!normalized) return callback(new Error(t('profile.nicknameRequired')))
  if (Array.from(normalized).length > 30) return callback(new Error(t('profile.nicknameTooLong')))
  callback()
}

const rules = {
  nickname: [{ validator: validateNickname, trigger: ['blur', 'change'] }]
}

const loadProfile = async () => {
  loading.value = true
  try {
    const result = await userInfoService()
    const current = result?.data || {}
    Object.assign(form, current, {
      nickname: current.nickname?.trim() || current.username || ''
    })
    avatarLoadFailed.value = false
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || t('profile.loadFailed'))
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    const result = await userUpdateProfileService({
      nickname: form.nickname.trim(),
      avatar: form.avatar || ''
    })
    const latestUser = result?.data
    if (!latestUser) throw new Error('Updated user profile is empty')
    Object.assign(form, latestUser)
    avatarLoadFailed.value = false
    updateCurrentUser(latestUser)
    ElMessage.success(t('profile.saveSuccess'))
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || t('profile.saveFailed'))
  } finally {
    saving.value = false
  }
}

const AVATAR_TYPES = new Set(['image/jpeg', 'image/png', 'image/webp'])
const AVATAR_EXTENSIONS = new Set(['jpg', 'jpeg', 'png', 'webp'])
const MAX_AVATAR_SIZE = 2 * 1024 * 1024

const releaseAvatarPreview = () => {
  if (!avatarPreviewUrl.value) return
  URL.revokeObjectURL(avatarPreviewUrl.value)
  avatarPreviewUrl.value = ''
}

const validateAvatar = file => {
  const extension = file.name?.split('.').pop()?.toLowerCase() || ''
  if (!AVATAR_TYPES.has(file.type) || !AVATAR_EXTENSIONS.has(extension)) {
    ElMessage.warning(t('profile.avatarTypeInvalid'))
    return false
  }
  if (file.size > MAX_AVATAR_SIZE) {
    ElMessage.warning(t('profile.avatarSizeInvalid'))
    return false
  }
  return true
}

const uploadAvatar = async ({ file, onSuccess, onError }) => {
  const previousAvatar = form.avatar
  releaseAvatarPreview()
  avatarPreviewUrl.value = URL.createObjectURL(file)
  avatarLoadFailed.value = false
  avatarUploading.value = true

  try {
    const result = await userAvatarUploadService(file)
    const uploadedUrl = result?.data?.url
    if (!uploadedUrl) throw new Error('Avatar upload URL is empty')
    form.avatar = uploadedUrl
    releaseAvatarPreview()
    avatarLoadFailed.value = false
    onSuccess?.(result)
    ElMessage.success(t('profile.avatarUploadSuccess'))
  } catch (error) {
    form.avatar = previousAvatar
    releaseAvatarPreview()
    avatarLoadFailed.value = false
    onError?.(error)
    ElMessage.error(error?.response?.data?.message || t('profile.avatarUploadFailed'))
  } finally {
    avatarUploading.value = false
  }
}

onMounted(loadProfile)
onBeforeUnmount(releaseAvatarPreview)
</script>

<template>
  <div class="profile-page">
    <el-card v-loading="loading" class="profile-card" shadow="never">
      <template #header>
        <div class="profile-heading">
          <strong>{{ t('profile.title') }}</strong>
          <span>{{ t('profile.hint') }}</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
        <div class="account-avatar">
          <el-upload
            class="account-avatar-upload"
            :show-file-list="false"
            :before-upload="validateAvatar"
            :http-request="uploadAvatar"
            accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp"
            :disabled="avatarUploading || saving"
          >
            <button
              class="account-avatar-trigger"
              type="button"
              :aria-label="t('profile.changeAvatar')"
              :disabled="avatarUploading || saving"
            >
              <img
                v-if="avatarDisplay"
                :src="avatarDisplay"
                class="avatar-image"
                :alt="t('profile.loginAvatar')"
                @error="avatarLoadFailed = true"
              />
              <span v-else class="avatar-fallback">{{ avatarInitial }}</span>
              <span class="account-avatar-overlay">
                <i class="ti ti-camera" aria-hidden="true"></i>
                {{ avatarUploading ? t('profile.avatarUploading') : t('profile.changeAvatar') }}
              </span>
            </button>
          </el-upload>
          <div class="avatar-copy">
            <b>{{ form.nickname || form.username }}</b>
            <p>{{ t('profile.avatarHint') }}</p>
          </div>
        </div>

        <el-form-item :label="t('profile.nickname')" prop="nickname">
          <el-input v-model="form.nickname" maxlength="30" show-word-limit />
        </el-form-item>
        <div class="readonly-grid">
          <el-form-item :label="t('profile.username')">
            <el-input :model-value="form.username" disabled />
          </el-form-item>
          <el-form-item :label="t('profile.email')">
            <el-input :model-value="form.email" disabled />
          </el-form-item>
        </div>
        <el-button
          type="primary"
          :loading="saving"
          :disabled="loading || avatarUploading"
          @click="saveProfile"
        >
          {{ t('profile.save') }}
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.profile-page { max-width: 760px; margin: 0 auto; }
.profile-card { background: var(--bg-card); border-color: var(--border); }
.profile-heading { display: flex; align-items: baseline; gap: 12px; }
.profile-heading span { color: var(--text-secondary); font-size: 12px; }
.account-avatar { display: flex; align-items: center; gap: 18px; margin-bottom: 24px; }
.account-avatar-upload { width: 112px; height: 112px; flex: none; }
.account-avatar-upload :deep(.el-upload) { display: block; width: 100%; height: 100%; border-radius: 50%; }
.account-avatar-trigger { position: relative; display: block; width: 112px; height: 112px; padding: 0; overflow: hidden; border: 2px solid var(--border); border-radius: 50%; background: transparent; color: #fff; cursor: pointer; }
.account-avatar-trigger:disabled { cursor: wait; }
.avatar-image,.avatar-fallback { width: 100%; height: 100%; }
.avatar-image { display: block; object-fit: cover; }
.avatar-fallback { display: grid; place-items: center; background: linear-gradient(135deg,#afa9ec,#ed93b1); color: #fff; font-size: 30px; }
.account-avatar-overlay { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; flex-direction: column; gap: 4px; background: rgba(20,18,38,.62); color: #fff; font-size: 12px; opacity: 0; transition: opacity .18s ease; }
.account-avatar-overlay i { font-size: 20px; }
.account-avatar-trigger:hover .account-avatar-overlay,.account-avatar-trigger:focus-visible .account-avatar-overlay,.account-avatar-trigger:disabled .account-avatar-overlay { opacity: 1; }
.account-avatar-trigger:focus-visible { outline: 3px solid rgba(175,169,236,.45); outline-offset: 3px; }
.avatar-copy { min-width: 0; }
.avatar-copy b { overflow-wrap: anywhere; }
.avatar-copy p { margin: 5px 0 0; color: var(--text-secondary); font-size: 12px; }
.readonly-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }

@media (max-width: 640px) {
  .profile-page { padding: 0; }
  .profile-heading { align-items: flex-start; flex-direction: column; gap: 3px; }
  .readonly-grid { grid-template-columns: 1fr; gap: 0; }
  .account-avatar { align-items: flex-start; }
}

@media (prefers-reduced-motion: reduce) {
  .account-avatar-overlay { transition: none; }
}
</style>
