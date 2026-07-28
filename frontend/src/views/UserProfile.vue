<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { userAvatarUploadService, userInfoService, userUpdateProfileService } from '@/api/user.js'
import { useCurrentUser } from '@/composables/useCurrentUser.js'
import {
  adminImageUploadService,
  adminSiteProfileService,
  adminSiteProfileUpdateService
} from '@/api/site.js'

const { t } = useI18n()
const { updateCurrentUser } = useCurrentUser()
const activeTab = ref('account')
const accountFormRef = ref()
const siteFormRef = ref()
const accountSaving = ref(false)
const siteLoading = ref(false)
const siteSaving = ref(false)
const accountAvatarUploading = ref(false)
const accountAvatarPreviewUrl = ref('')
const accountAvatarLoadFailed = ref(false)
const accountAvatarDirty = ref(false)
const siteAvatarUploading = ref(false)
const backgroundUploading = ref(false)
const keywordInput = ref('')

const userInfo = reactive({ username: '', email: '', avatar: '', bio: '', role: 0 })
// 博客公开头像继续使用独立的 siteForm.avatarUrl 和站点配置接口，不与登录头像联动。
const siteForm = reactive({
  siteTitle: '', heroSubtitle: '', heroKeywords: [], authorName: '', authorBio: '',
  avatarUrl: '', profileBackgroundUrl: '', announcement: '', githubUrl: '', giteeUrl: ''
})

const isAdmin = computed(() => Number(userInfo.role) === 1)
const avatarInitial = computed(() => {
  const source = userInfo.username?.trim() || userInfo.email?.trim() || 'U'
  return (Array.from(source)[0] || 'U').toUpperCase()
})
const authorInitial = computed(() => siteForm.authorName?.trim()?.charAt(0).toUpperCase() || '?')
const accountAvatarDisplay = computed(() => (
  !accountAvatarLoadFailed.value
    ? (accountAvatarPreviewUrl.value || userInfo.avatar)
    : ''
))
const accountRules = { email: [{ type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }] }
const urlValidator = (_rule, value, callback) => {
  if (!value || /^https?:\/\/[^\s]+$/i.test(value.trim())) callback()
  else callback(new Error('请输入有效的 http 或 https 地址'))
}
const siteRules = {
  siteTitle: [{ required: true, message: '请输入站点标题', trigger: 'blur' }, { max: 100, message: '最多100个字符', trigger: 'blur' }],
  heroSubtitle: [{ max: 255, message: '最多255个字符', trigger: 'blur' }],
  authorName: [{ required: true, message: '请输入博主展示名称', trigger: 'blur' }, { max: 50, message: '最多50个字符', trigger: 'blur' }],
  authorBio: [{ max: 500, message: '最多500个字符', trigger: 'blur' }],
  announcement: [{ max: 1000, message: '最多1000个字符', trigger: 'blur' }],
  avatarUrl: [{ validator: urlValidator, trigger: 'blur' }],
  profileBackgroundUrl: [{ validator: urlValidator, trigger: 'blur' }],
  githubUrl: [{ validator: urlValidator, trigger: 'blur' }],
  giteeUrl: [{ validator: urlValidator, trigger: 'blur' }]
}

const loadAccount = async () => {
  const result = await userInfoService()
  Object.assign(userInfo, result?.data || {})
  accountAvatarLoadFailed.value = false
  if (isAdmin.value) await loadSiteConfig()
}

const loadSiteConfig = async () => {
  siteLoading.value = true
  try {
    const result = await adminSiteProfileService()
    Object.assign(siteForm, result?.data || {})
    siteForm.heroKeywords = Array.isArray(result?.data?.heroKeywords) ? [...result.data.heroKeywords] : []
  } catch (error) {
    ElMessage.error('站点资料加载失败')
  } finally {
    siteLoading.value = false
  }
}

const saveAccount = async () => {
  await accountFormRef.value.validate()
  accountSaving.value = true
  try {
    const result = await userUpdateProfileService({
      email: userInfo.email,
      avatar: userInfo.avatar || '',
      bio: userInfo.bio
    })
    const latestUser = result?.data
    if (!latestUser) throw new Error('Updated user profile is empty')

    Object.assign(userInfo, latestUser)
    accountAvatarLoadFailed.value = false
    // 保存成功后更新共享用户状态，导航栏无需刷新或重新登录即可同步头像。
    updateCurrentUser(latestUser)
    ElMessage.success(accountAvatarDirty.value ? t('profile.avatarSaveSuccess') : t('profile.saveSuccess'))
    accountAvatarDirty.value = false
  } catch (error) {
    ElMessage.error(error?.response?.data?.message
      || (accountAvatarDirty.value ? t('profile.avatarSaveFailed') : t('profile.saveFailed')))
  } finally {
    accountSaving.value = false
  }
}

const ACCOUNT_AVATAR_TYPES = new Set(['image/jpeg', 'image/png', 'image/webp'])
const ACCOUNT_AVATAR_EXTENSIONS = new Set(['jpg', 'jpeg', 'png', 'webp'])
const MAX_ACCOUNT_AVATAR_SIZE = 2 * 1024 * 1024

const releaseAccountAvatarPreview = () => {
  if (!accountAvatarPreviewUrl.value) return
  // object URL 只用于本次本地预览，替换和卸载时及时释放，避免长期占用内存。
  URL.revokeObjectURL(accountAvatarPreviewUrl.value)
  accountAvatarPreviewUrl.value = ''
}

const validateAccountAvatar = file => {
  const extension = file.name?.split('.').pop()?.toLowerCase() || ''
  if (!ACCOUNT_AVATAR_TYPES.has(file.type) || !ACCOUNT_AVATAR_EXTENSIONS.has(extension)) {
    ElMessage.warning(t('profile.avatarTypeInvalid'))
    return false
  }
  if (file.size > MAX_ACCOUNT_AVATAR_SIZE) {
    ElMessage.warning(t('profile.avatarSizeInvalid'))
    return false
  }
  return true
}

const inspectAccountAvatarDimensions = previewUrl => new Promise(resolve => {
  const image = new Image()
  image.onload = () => {
    if (image.naturalWidth < 100 || image.naturalHeight < 100) {
      ElMessage.warning(t('profile.avatarResolutionLow'))
    }
    resolve()
  }
  image.onerror = resolve
  image.src = previewUrl
})

const uploadAccountAvatar = async ({ file, onSuccess, onError }) => {
  const previousAvatar = userInfo.avatar
  releaseAccountAvatarPreview()
  accountAvatarPreviewUrl.value = URL.createObjectURL(file)
  accountAvatarLoadFailed.value = false
  accountAvatarUploading.value = true

  try {
    // 先展示本地预览，再上传到持久化目录；保存账号资料时才写入 avatar URL。
    await inspectAccountAvatarDimensions(accountAvatarPreviewUrl.value)
    const result = await userAvatarUploadService(file)
    const uploadedUrl = result?.data?.url
    if (!uploadedUrl) throw new Error('Avatar upload URL is empty')

    userInfo.avatar = uploadedUrl
    accountAvatarDirty.value = uploadedUrl !== previousAvatar
    releaseAccountAvatarPreview()
    accountAvatarLoadFailed.value = false
    onSuccess?.(result)
    ElMessage.success(t('profile.avatarUploadSuccess'))
    // 旧头像不自动删除，避免误删被其他内容引用的文件。
  } catch (error) {
    userInfo.avatar = previousAvatar
    releaseAccountAvatarPreview()
    accountAvatarLoadFailed.value = false
    onError?.(error)
    ElMessage.error(error?.response?.data?.message || t('profile.avatarUploadFailed'))
  } finally {
    accountAvatarUploading.value = false
  }
}

const addKeyword = () => {
  const value = keywordInput.value.trim()
  if (!value) return
  if (value.length > 30) return ElMessage.warning('每个关键词最多30个字符')
  if (siteForm.heroKeywords.length >= 8) return ElMessage.warning('最多添加8个关键词')
  if (!siteForm.heroKeywords.includes(value)) siteForm.heroKeywords.push(value)
  keywordInput.value = ''
}

const removeKeyword = index => siteForm.heroKeywords.splice(index, 1)

const uploadImage = async (file, field) => {
  if (!file.type?.startsWith('image/')) return ElMessage.warning('请选择图片文件')
  if (file.size > 5 * 1024 * 1024) return ElMessage.warning('图片不能超过5MB')
  const loadingRef = field === 'avatarUrl' ? siteAvatarUploading : backgroundUploading
  loadingRef.value = true
  try {
    const result = await adminImageUploadService(file)
    siteForm[field] = result?.data?.url || ''
    ElMessage.success('上传成功，请点击保存使配置生效')
  } catch (error) {
    ElMessage.error('图片上传失败')
  } finally {
    loadingRef.value = false
  }
}

const saveSite = async () => {
  await siteFormRef.value.validate()
  siteSaving.value = true
  try {
    await adminSiteProfileUpdateService({ ...siteForm, heroKeywords: [...siteForm.heroKeywords] })
    ElMessage.success('博客展示资料保存成功')
    await loadSiteConfig()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '站点资料保存失败')
  } finally {
    siteSaving.value = false
  }
}

onMounted(() => loadAccount().catch(() => ElMessage.error(t('profile.loadFailed'))))
onBeforeUnmount(releaseAccountAvatarPreview)
</script>

<template>
  <div class="profile-page">
    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="账号资料" name="account">
        <el-card class="profile-card" shadow="never">
          <template #header><strong>账号资料</strong><span class="section-hint">用于登录和账号管理</span></template>
          <el-form ref="accountFormRef" :model="userInfo" :rules="accountRules" label-position="top">
            <div class="account-avatar">
              <el-upload
                class="account-avatar-upload"
                :show-file-list="false"
                :before-upload="validateAccountAvatar"
                :http-request="uploadAccountAvatar"
                accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp"
                :disabled="accountAvatarUploading || accountSaving"
              >
                <button
                  class="account-avatar-trigger"
                  type="button"
                  :aria-label="t('profile.changeAvatar')"
                  :disabled="accountAvatarUploading || accountSaving"
                >
                  <img
                    v-if="accountAvatarDisplay"
                    :src="accountAvatarDisplay"
                    class="round-preview"
                    :alt="t('profile.loginAvatar')"
                    @error="accountAvatarLoadFailed = true"
                  />
                  <span v-else class="avatar-fallback">{{ avatarInitial }}</span>
                  <span class="account-avatar-overlay">
                    <i class="ti ti-camera" aria-hidden="true"></i>
                    {{ accountAvatarUploading ? t('profile.avatarUploading') : t('profile.changeAvatar') }}
                  </span>
                </button>
              </el-upload>
              <div><b>{{ userInfo.username }}</b><p>登录头像属于账号资料，与博客公开头像相互独立。</p></div>
            </div>
            <el-form-item label="用户名"><el-input :model-value="userInfo.username" disabled /></el-form-item>
            <el-form-item label="邮箱" prop="email"><el-input v-model="userInfo.email" /></el-form-item>
            <el-form-item label="个人简介"><el-input v-model="userInfo.bio" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item>
            <el-button type="primary" :loading="accountSaving" :disabled="accountAvatarUploading" @click="saveAccount">保存账号资料</el-button>
            <router-link class="password-link" to="/user/reset-password">修改密码</router-link>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane v-if="isAdmin" label="博客展示资料" name="site">
        <el-card v-loading="siteLoading" class="profile-card" shadow="never">
          <template #header><strong>博客展示资料</strong><span class="section-hint">用于首页公开展示，不影响登录账号</span></template>
          <el-form ref="siteFormRef" :model="siteForm" :rules="siteRules" label-position="top">
            <div class="form-grid">
              <el-form-item label="站点标题" prop="siteTitle"><el-input v-model="siteForm.siteTitle" maxlength="100" /></el-form-item>
              <el-form-item label="首页诗句 / 副标题" prop="heroSubtitle"><el-input v-model="siteForm.heroSubtitle" maxlength="255" /></el-form-item>
            </div>
            <el-form-item label="首页展示关键词">
              <div class="keyword-editor">
                <div class="keyword-list">
                  <el-tag v-for="(keyword, index) in siteForm.heroKeywords" :key="keyword" closable @close="removeKeyword(index)">{{ keyword }}</el-tag>
                </div>
                <div class="keyword-add"><el-input v-model="keywordInput" maxlength="30" placeholder="输入关键词后回车" @keyup.enter="addKeyword" /><el-button @click="addKeyword">添加</el-button></div>
                <small>{{ siteForm.heroKeywords.length }}/8，关键词仅用于首页展示，不等于文章分类。</small>
              </div>
            </el-form-item>
            <div class="form-grid">
              <el-form-item label="博主展示名称" prop="authorName"><el-input v-model="siteForm.authorName" maxlength="50" /></el-form-item>
              <el-form-item label="博主公开简介" prop="authorBio"><el-input v-model="siteForm.authorBio" type="textarea" :rows="4" maxlength="500" show-word-limit /></el-form-item>
            </div>

            <div class="media-grid">
              <el-form-item label="博主资料卡头像" prop="avatarUrl">
                <div class="media-field">
                  <img v-if="siteForm.avatarUrl" :src="siteForm.avatarUrl" class="round-preview" alt="博主头像预览" />
                  <div v-else class="avatar-fallback">{{ authorInitial }}</div>
                  <el-upload :show-file-list="false" :http-request="({ file }) => uploadImage(file, 'avatarUrl')">
                    <el-button :loading="siteAvatarUploading">更换头像</el-button>
                  </el-upload>
                  <el-input v-model="siteForm.avatarUrl" placeholder="https://..." />
                </div>
              </el-form-item>
              <el-form-item label="博主资料卡背景图" prop="profileBackgroundUrl">
                <div class="media-field">
                  <div class="background-preview" :style="siteForm.profileBackgroundUrl ? { backgroundImage: `url(${siteForm.profileBackgroundUrl})` } : {}"><span v-if="!siteForm.profileBackgroundUrl">暂无背景图</span></div>
                  <el-upload :show-file-list="false" :http-request="({ file }) => uploadImage(file, 'profileBackgroundUrl')">
                    <el-button :loading="backgroundUploading">更换背景</el-button>
                  </el-upload>
                  <el-input v-model="siteForm.profileBackgroundUrl" placeholder="https://..." />
                </div>
              </el-form-item>
            </div>

            <el-form-item label="首页公告" prop="announcement"><el-input v-model="siteForm.announcement" type="textarea" :rows="4" maxlength="1000" show-word-limit /></el-form-item>
            <div class="form-grid">
              <el-form-item label="GitHub 地址" prop="githubUrl"><el-input v-model="siteForm.githubUrl" placeholder="https://github.com/..." /></el-form-item>
              <el-form-item label="Gitee 地址" prop="giteeUrl"><el-input v-model="siteForm.giteeUrl" placeholder="https://gitee.com/..." /></el-form-item>
            </div>
            <el-button type="primary" :loading="siteSaving" @click="saveSite">保存博客展示资料</el-button>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.profile-page { max-width: 980px; margin: 0 auto; }
.profile-card { background: var(--bg-card); border-color: var(--border-color); }
.section-hint { margin-left: 12px; color: var(--text-secondary); font-size: 12px; font-weight: normal; }
.account-avatar { display:flex; align-items:center; gap:18px; margin-bottom:22px; }
.account-avatar p { margin:5px 0 0; color:var(--text-secondary); font-size:12px; }
.account-avatar-upload { flex:none; width:112px; height:112px; }
.account-avatar-upload :deep(.el-upload) { display:block; width:100%; height:100%; border-radius:50%; }
.account-avatar-trigger { position:relative; display:block; width:112px; height:112px; padding:0; overflow:hidden; border:2px solid var(--border-color); border-radius:50%; background:transparent; color:#fff; cursor:pointer; }
.account-avatar-trigger:disabled { cursor:wait; }
.account-avatar-trigger .round-preview,.account-avatar-trigger .avatar-fallback { width:100%; height:100%; border:0; }
.account-avatar-overlay { position:absolute; inset:0; display:flex; align-items:center; justify-content:center; flex-direction:column; gap:4px; background:rgba(20,18,38,.62); color:#fff; font-size:12px; opacity:0; transition:opacity .18s ease; }
.account-avatar-overlay i { font-size:20px; }
.account-avatar-trigger:hover .account-avatar-overlay,.account-avatar-trigger:focus-visible .account-avatar-overlay,.account-avatar-trigger:disabled .account-avatar-overlay { opacity:1; }
.account-avatar-trigger:focus-visible { outline:3px solid rgba(175,169,236,.45); outline-offset:3px; }
.round-preview,.avatar-fallback { width:76px; height:76px; border-radius:50%; flex:none; }
.round-preview { object-fit:cover; border:2px solid var(--border-color); }
.avatar-fallback { display:grid; place-items:center; background:linear-gradient(135deg,#afa9ec,#ed93b1); color:white; font-size:26px; }
.password-link { margin-left:16px; color:var(--accent-color); font-size:13px; }
.form-grid,.media-grid { display:grid; grid-template-columns:1fr 1fr; gap:20px; }
.keyword-editor,.media-field { width:100%; display:flex; flex-direction:column; gap:10px; }
.keyword-list { min-height:32px; display:flex; flex-wrap:wrap; gap:8px; }
.keyword-add { display:flex; gap:8px; max-width:420px; }
.keyword-editor small { color:var(--text-secondary); }
.background-preview { height:140px; border-radius:10px; border:1px dashed var(--border-color); background-size:cover; background-position:center; display:grid; place-items:center; color:var(--text-secondary); }
@media (max-width: 700px) {
  .form-grid,.media-grid { grid-template-columns:1fr; gap:0; }
  .profile-page { padding:0; }
  .section-hint { display:block; margin:4px 0 0; }
  .account-avatar { align-items:flex-start; }
}
</style>
