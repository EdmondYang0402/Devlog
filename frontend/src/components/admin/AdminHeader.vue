<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { userLogoutService } from '@/api/user.js'
import { useCurrentUser } from '@/composables/useCurrentUser.js'
import { useTheme } from '@/composables/useTheme.js'
import AdminIcon from '@/components/admin/AdminIcon.vue'

defineProps({ title: { type: String, default: '后台管理' } })
const emit = defineEmits(['toggle-sidebar'])

const router = useRouter()
const { t } = useI18n()
const { theme, toggleTheme } = useTheme()
const { token, userInfo, clearUser, loadCurrentUser } = useCurrentUser()
const menuOpen = ref(false)
const menuRef = ref(null)
const avatarLoadFailed = ref(false)
const searchValue = ref('')

watch(() => userInfo.value?.avatar, () => { avatarLoadFailed.value = false })

const avatarText = computed(() => {
  const value = userInfo.value?.nickname?.trim() || userInfo.value?.username?.trim() || userInfo.value?.email?.trim() || 'A'
  return (Array.from(value)[0] || 'A').toUpperCase()
})
const showAvatarImage = computed(() => Boolean(userInfo.value?.avatar) && !avatarLoadFailed.value)
const roleLabel = computed(() => Number(userInfo.value?.role) === 1 ? '管理员' : '用户')

const closeMenu = event => {
  if (menuRef.value && !menuRef.value.contains(event.target)) menuOpen.value = false
}

const logout = async () => {
  try {
    await userLogoutService()
  } catch (error) {
    console.error('退出登录接口调用失败', error)
  } finally {
    clearUser()
    menuOpen.value = false
    ElMessage.success(t('message.loggedOut'))
    await router.push('/login')
  }
}

onMounted(async () => {
  document.addEventListener('click', closeMenu)
  if (token.value && !userInfo.value) {
    try { await loadCurrentUser() } catch (error) { console.error('加载管理员信息失败', error) }
  }
})
onUnmounted(() => document.removeEventListener('click', closeMenu))
</script>

<template>
  <header class="admin-header">
    <div class="header-leading">
      <button class="icon-button sidebar-toggle" type="button" aria-label="切换侧边栏" @click="emit('toggle-sidebar')">
        <AdminIcon name="ti-menu-2" />
      </button>
      <div class="page-title">
        <span>管理控制台</span>
        <h1>{{ title || '后台管理' }}</h1>
      </div>
    </div>

    <label class="admin-search">
      <AdminIcon name="ti-search" />
      <input v-model="searchValue" type="search" placeholder="搜索文章、评论、用户……" aria-label="全局搜索（功能建设中）" />
      <span>仅界面</span>
    </label>

    <div class="header-actions">
      <button class="icon-button" type="button" aria-label="通知（暂无通知接口）" title="通知（暂无通知接口）">
        <AdminIcon name="ti-bell" />
      </button>
      <button class="icon-button" type="button" :aria-label="theme === 'dark' ? '切换到浅色模式' : '切换到深色模式'" @click="toggleTheme">
        <AdminIcon :name="theme === 'dark' ? 'ti-sun' : 'ti-moon'" />
      </button>

      <div ref="menuRef" class="user-menu">
        <button class="user-trigger" type="button" :aria-expanded="menuOpen" aria-haspopup="menu" @click.stop="menuOpen = !menuOpen">
          <span class="header-avatar">
            <img v-if="showAvatarImage" :src="userInfo.avatar" alt="" @error="avatarLoadFailed = true" />
            <span v-else>{{ avatarText }}</span>
          </span>
          <span class="user-copy">
            <strong>{{ userInfo?.nickname || userInfo?.username || '管理员' }}</strong>
            <small>{{ roleLabel }}</small>
          </span>
          <AdminIcon name="ti-chevron-down" />
        </button>

        <div v-if="menuOpen" class="user-dropdown admin-panel" role="menu">
          <div class="dropdown-profile">
            <strong>{{ userInfo?.nickname || userInfo?.username || '管理员' }}</strong>
            <span>{{ userInfo?.email || roleLabel }}</span>
          </div>
          <router-link to="/admin/profile" role="menuitem" @click="menuOpen = false">
            <AdminIcon name="ti-user-circle" />个人资料
          </router-link>
          <router-link to="/home" role="menuitem" @click="menuOpen = false">
            <AdminIcon name="ti-external-link" />访问站点
          </router-link>
          <div class="dropdown-divider"></div>
          <button type="button" role="menuitem" class="logout-button" @click="logout">
            <AdminIcon name="ti-logout" />退出登录
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.admin-header {
  position: sticky;
  top: 0;
  z-index: 20;
  min-height: var(--admin-header-height);
  display: grid;
  grid-template-columns: minmax(220px, 1fr) minmax(260px, 440px) minmax(220px, 1fr);
  align-items: center;
  gap: 20px;
  padding: 0 24px;
  background: var(--admin-header-bg);
  border: 1px solid var(--admin-border);
  border-radius: 18px;
  box-shadow: 0 8px 22px rgba(4, 5, 14, .1);
  -webkit-backdrop-filter: blur(var(--admin-blur));
  backdrop-filter: blur(var(--admin-blur));
}
.header-leading, .header-actions { display: flex; align-items: center; }
.header-leading { min-width: 0; gap: 13px; }
.header-actions { justify-content: flex-end; gap: 8px; }
.icon-button { width: 38px; height: 38px; flex: 0 0 38px; display: grid; place-items: center; border: 1px solid var(--admin-border); border-radius: 12px; background: rgba(255,255,255,.022); color: var(--admin-text-secondary); cursor: pointer; transition: color .18s ease, background .18s ease, border-color .18s ease; }
.icon-button:hover { color: var(--admin-text-primary); background: rgba(255,255,255,.052); border-color: var(--admin-border-strong); }
.icon-button :deep(.admin-icon) { width: 18px; height: 18px; }
.page-title { min-width: 0; }
.page-title span { display: block; color: var(--admin-text-muted); font-size: 9px; letter-spacing: .17em; }
.page-title h1 { overflow: hidden; margin: 1px 0 0; color: var(--admin-text-primary); font-family: Georgia, 'Noto Serif SC', serif; font-size: clamp(17px, 1.5vw, 21px); font-weight: 500; text-overflow: ellipsis; white-space: nowrap; }
.admin-search { height: 40px; display: flex; align-items: center; gap: 10px; padding: 0 13px; border: 1px solid var(--admin-border); border-radius: 13px; background: rgba(255,255,255,.025); color: var(--admin-text-muted); transition: border-color .18s ease, background .18s ease, box-shadow .18s ease; }
.admin-search:focus-within { border-color: var(--admin-border-strong); background: rgba(255,255,255,.045); box-shadow: 0 0 0 3px rgba(170,148,237,.07); }
.admin-search input { min-width: 0; flex: 1; border: 0; outline: 0; background: transparent; color: var(--admin-text-primary); font-size: 12px; }
.admin-search input::placeholder { color: var(--admin-text-muted); }
.admin-search span { padding: 2px 6px; border: 1px solid var(--admin-border); border-radius: 6px; color: var(--admin-text-muted); font-size: 9px; white-space: nowrap; }
.user-menu { position: relative; margin-left: 4px; }
.user-trigger { min-width: 154px; height: 44px; display: flex; align-items: center; gap: 9px; padding: 4px 8px 4px 5px; border: 1px solid transparent; border-radius: 14px; background: transparent; color: var(--admin-text-primary); cursor: pointer; transition: background .18s ease, border-color .18s ease; }
.user-trigger:hover { background: rgba(255,255,255,.032); border-color: var(--admin-border); }
.header-avatar { width: 34px; height: 34px; flex: 0 0 34px; display: grid; place-items: center; overflow: hidden; border: 1px solid rgba(255,255,255,.16); border-radius: 50%; background: linear-gradient(145deg, #9d86df, #c37fa1); color: #fff; font-size: 12px; font-weight: 700; }
.header-avatar img { width: 100%; height: 100%; object-fit: cover; }
.user-copy { min-width: 0; flex: 1; display: flex; flex-direction: column; text-align: left; }
.user-copy strong { max-width: 90px; overflow: hidden; font-size: 11px; font-weight: 600; text-overflow: ellipsis; white-space: nowrap; }
.user-copy small { color: var(--admin-text-muted); font-size: 9px; }
.user-trigger > :deep(.admin-icon) { width: 13px; height: 13px; color: var(--admin-text-muted); }
.user-dropdown { position: absolute; top: calc(100% + 10px); right: 0; width: 210px; padding: 8px; }
.dropdown-profile { display: flex; flex-direction: column; gap: 2px; padding: 9px 10px 11px; }
.dropdown-profile strong { font-size: 12px; }
.dropdown-profile span { overflow: hidden; color: var(--admin-text-muted); font-size: 10px; text-overflow: ellipsis; }
.user-dropdown a, .user-dropdown button { width: 100%; min-height: 36px; display: flex; align-items: center; gap: 9px; padding: 0 10px; border: 0; border-radius: 9px; background: transparent; color: var(--admin-text-secondary); font-size: 11px; text-decoration: none; cursor: pointer; transition: background .16s ease, color .16s ease; }
.user-dropdown a:hover, .user-dropdown button:hover { color: var(--admin-text-primary); background: rgba(255,255,255,.045); }
.dropdown-divider { height: 1px; margin: 6px 3px; background: var(--admin-border); }
.user-dropdown .logout-button { color: #ef929b; }

@media (max-width: 1080px) {
  .admin-header { grid-template-columns: minmax(210px, 1fr) minmax(230px, 360px) auto; }
  .user-trigger { min-width: auto; }
  .user-copy, .user-trigger > :deep(.admin-icon) { display: none; }
}
@media (max-width: 720px) {
  .admin-header { grid-template-columns: minmax(0, 1fr) auto; gap: 10px; padding: 0 14px; }
  .admin-search { display: none; }
  .header-actions { gap: 5px; }
  .header-actions > .icon-button:first-child { display: none; }
}
</style>
