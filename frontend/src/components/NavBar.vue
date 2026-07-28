<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { userLogoutService } from '@/api/user.js'
import { useCurrentUser } from '@/composables/useCurrentUser.js'
import ThemeToggle from '@/components/ThemeToggle.vue'
import LanguageSwitcher from '@/components/common/LanguageSwitcher.vue'

const router = useRouter()
const { t } = useI18n()
const menuOpen = ref(false)
const menuRef = ref(null)
const avatarLoadFailed = ref(false)
const { token, userInfo, isLoggedIn, clearUser, loadCurrentUser } = useCurrentUser()

const avatarText = computed(() => {
  const source = userInfo.value?.username?.trim() || userInfo.value?.email?.trim() || 'U'
  return (Array.from(source)[0] || 'U').toUpperCase()
})
const showAvatarImage = computed(() => Boolean(userInfo.value?.avatar) && !avatarLoadFailed.value)
const isAdmin = computed(() => Number(userInfo.value?.role) === 1)

watch(() => userInfo.value?.avatar, () => {
  avatarLoadFailed.value = false
})

const toggleMenu = () => { menuOpen.value = !menuOpen.value }
const handleAvatarError = () => { avatarLoadFailed.value = true }
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
    try {
      await loadCurrentUser()
    } catch (error) {
      console.error('加载当前用户信息失败', error)
    }
  }
})
onUnmounted(() => document.removeEventListener('click', closeMenu))
</script>

<template>
  <nav class="nav">
    <router-link to="/" class="logo">
      <span class="logo-mark">✦</span>
      <span class="logo-text">Hathaway's Blog</span>
    </router-link>

    <div class="links">
      <router-link to="/home" active-class="is-active">{{ t('nav.home') }}</router-link>
      <router-link to="/archive" active-class="is-active">{{ t('nav.archive') }}</router-link>
      <router-link to="/notes" active-class="is-active">{{ t('nav.notes') }}</router-link>
      <router-link to="/media" active-class="is-active">{{ t('nav.media') }}</router-link>
      <router-link to="/about" active-class="is-active">{{ t('nav.about') }}</router-link>
    </div>

    <div class="right">
      <button class="icon-btn" :aria-label="t('nav.search')" :title="t('nav.search')">
        <i class="ti ti-search" aria-hidden="true"></i>
      </button>

      <LanguageSwitcher />
      <ThemeToggle />

      <button v-if="!isLoggedIn" class="btn-login" @click="router.push('/login')">{{ t('nav.login') }}</button>

      <div v-else ref="menuRef" class="menu-wrap">
        <button class="avatar" type="button" :aria-label="t('nav.userMenu')" @click="toggleMenu">
          <img
            v-if="showAvatarImage"
            :src="userInfo.avatar"
            :alt="userInfo.username || t('nav.avatar')"
            class="avatar-image"
            @error="handleAvatarError"
          />
          <span v-else>{{ avatarText }}</span>
        </button>

        <div v-if="menuOpen" class="dropdown">
          <div class="dropdown-header">
            <p class="d-name">{{ userInfo.username || t('nav.user') }}</p>
            <p class="d-bio">{{ userInfo.bio || t('nav.noBio') }}</p>
          </div>

          <router-link v-if="isAdmin" to="/admin" class="d-item" @click="menuOpen = false">
            <i class="ti ti-settings" aria-hidden="true"></i> {{ t('nav.admin') }}
          </router-link>
          <router-link to="/user/profile" class="d-item" @click="menuOpen = false">
            <i class="ti ti-user" aria-hidden="true"></i> {{ t('nav.profile') }}
          </router-link>
          <router-link to="/user/reset-password" class="d-item" @click="menuOpen = false">
            <i class="ti ti-lock" aria-hidden="true"></i> {{ t('nav.changePassword') }}
          </router-link>

          <div class="d-divider"></div>
          <button class="d-item d-logout" @click="logout">
            <i class="ti ti-logout" aria-hidden="true"></i> {{ t('nav.logout') }}
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.nav {
  position: sticky;
  top: 0;
  z-index: 100;
  height: var(--nav-h);
  display: flex;
  align-items: center;
  padding-inline: clamp(32px, 3vw, 48px);
  background: var(--nav-bg);
  -webkit-backdrop-filter: blur(16px) saturate(120%);
  backdrop-filter: blur(16px) saturate(120%);
  border-bottom: 1px solid color-mix(in srgb, var(--border) 72%, transparent);
  transition: background-color .25s ease, border-color .25s ease;
}
.logo {
  flex: 0 0 auto;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  margin-right: clamp(48px, 4vw, 64px);
  color: var(--purple-400);
  font-size: 18px;
  font-weight: 600;
  line-height: 1;
  letter-spacing: .015em;
  white-space: nowrap;
}
.logo-mark { font-size: .92em; }
.links { flex: 0 0 auto; align-self: stretch; display: flex; align-items: stretch; gap: clamp(28px, 2.4vw, 36px); }
.links a {
  position: relative;
  display: inline-flex;
  align-items: center;
  color: var(--text-2);
  font-size: 14px;
  font-weight: 450;
  white-space: nowrap;
  transition: color .24s ease;
}
.links a::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 14px;
  width: 20px;
  height: 2px;
  border-radius: 999px;
  background: var(--purple-400);
  transform: translateX(-50%) scaleX(0);
  transform-origin: center;
  opacity: 0;
  transition: transform .24s ease, opacity .24s ease;
}
.links a:hover,
.links a:focus-visible,
.links a.is-active { color: var(--purple-400); }
.links a:hover::after,
.links a:focus-visible::after,
.links a.is-active::after { transform: translateX(-50%) scaleX(1); opacity: 1; }
.links a:focus-visible { outline: none; }
.right { flex: 0 0 auto; margin-left: auto; display: flex; align-items: center; gap: 14px; }
.icon-btn,
.btn-login {
  height: 36px;
  border: 1px solid var(--border);
  border-radius: 999px;
  background: rgba(255,255,255,.16);
  color: var(--text-2);
  cursor: pointer;
  font-family: inherit;
  transition: color .24s ease, border-color .24s ease, background-color .24s ease;
}
.icon-btn { width: 36px; display: flex; align-items: center; justify-content: center; font-size: 16px; }
.icon-btn:hover,
.icon-btn:focus-visible,
.btn-login:hover,
.btn-login:focus-visible { color: var(--purple-400); border-color: var(--border-h); background: var(--purple-50); outline: none; }
.btn-login { padding-inline: 16px; font-size: 12px; letter-spacing: .02em; }
.menu-wrap { position:relative; margin-left:3px; }
.avatar { width:36px; height:36px; padding:0; overflow:hidden; border-radius:50%; background:linear-gradient(135deg,var(--purple-200),var(--purple-400)); display:flex; align-items:center; justify-content:center; font-size:13px; font-weight:500; color:#fff; cursor:pointer; border:1px solid var(--border); transition:border-color .24s ease,box-shadow .24s ease; user-select:none; }
.avatar:hover,.avatar:focus-visible { border-color:var(--border-h); box-shadow:0 0 0 3px color-mix(in srgb,var(--purple-100) 35%,transparent); outline:none; }
.avatar-image { width:100%; height:100%; object-fit:cover; display:block; }
.dropdown { position:absolute; right:0; top:42px; width:190px; background:var(--bg-card,#fff); border:.5px solid var(--border); border-radius:10px; padding:6px; box-shadow:0 4px 20px rgba(0,0,0,.08); z-index:200; }
.dropdown-header { padding:8px 10px; border-bottom:.5px solid var(--border); margin-bottom:4px; }
.d-name { font-size:13px; font-weight:500; color:var(--text-1); margin:0; }
.d-bio { font-size:11px; color:var(--text-3); margin:2px 0 0; overflow-wrap:anywhere; }
.d-item { display:flex; align-items:center; gap:8px; padding:7px 10px; border-radius:6px; font-size:13px; color:var(--text-2); cursor:pointer; text-decoration:none; transition:background .12s; width:100%; text-align:left; border:none; background:transparent; font-family:inherit; }
.d-item:hover { background:var(--bg-hover,#f5f4fc); }
.d-item i { font-size:15px; }
.d-divider { border-top:.5px solid var(--border); margin:4px 0; }
.d-logout { color:#E24B4A; }
.d-logout:hover { background:#FCEBEB; }

.right :deep(.language-switcher) {
  height: 36px;
  min-width: 82px;
  border-width: 1px;
  background: rgba(255,255,255,.16);
  box-shadow: none;
  transition-duration: .24s;
}
.right :deep(.theme-toggle) {
  width: 64px;
  height: 36px;
  box-shadow: none;
}
.right :deep(.theme-toggle:hover),
.right :deep(.theme-toggle:focus-visible) { border-color: var(--border-h); box-shadow: 0 0 0 3px color-mix(in srgb,var(--purple-100) 24%,transparent); }
.right :deep(.theme-thumb) { left: 4px; top: 4px; width: 26px; height: 26px; }
.right :deep(.theme-toggle.is-dark .theme-thumb) { transform: translateX(29px); }

@media (max-width: 960px) {
  .nav { padding-inline: 24px; }
  .logo { margin-right: 32px; }
  .links { gap: 22px; }
  .right { gap: 10px; }
}

@media (max-width: 680px) {
  .nav { height: 58px; padding-inline: 12px; }
  .logo { margin-right: 14px; font-size: 17px; }
  .logo-text { display: none; }
  .links { flex: 1 1 auto; gap: 14px; min-width: 0; overflow-x: auto; scrollbar-width: none; }
  .links::-webkit-scrollbar { display: none; }
  .links a { font-size: 12px; }
  .links a::after { bottom: 9px; width: 18px; }
  .right { gap: 6px; }
  .icon-btn { display: none; }
  .btn-login { min-width: 0; height: 34px; padding-inline: 11px; white-space: nowrap; }
  .avatar { width: 34px; height: 34px; }
  .right :deep(.language-switcher) { width: 34px; min-width: 34px; height: 34px; padding-inline: 0; }
  .right :deep(.theme-toggle) { width: 52px; height: 34px; }
  .right :deep(.theme-thumb) { width: 24px; height: 24px; }
  .right :deep(.theme-toggle.is-dark .theme-thumb) { transform: translateX(20px); }
}

@media (max-width: 420px) {
  .nav { padding-inline: 8px; }
  .logo { display: none; }
  .links { gap: 10px; }
  .right { gap: 5px; }
}

@media (max-width: 360px) {
  .links { gap: 8px; }
  .links a { font-size: 11px; }
  .right :deep(.theme-toggle) { width: 48px; }
  .right :deep(.theme-toggle.is-dark .theme-thumb) { transform: translateX(16px); }
}
</style>
