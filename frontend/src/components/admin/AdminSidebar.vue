<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCurrentUser } from '@/composables/useCurrentUser.js'
import AdminIcon from '@/components/admin/AdminIcon.vue'

defineProps({
  collapsed: { type: Boolean, default: false },
  mobileOpen: { type: Boolean, default: false }
})

const emit = defineEmits(['close-mobile'])
const route = useRoute()
const { t } = useI18n()
const { userInfo } = useCurrentUser()
const avatarLoadFailed = ref(false)

watch(() => userInfo.value?.avatar, () => { avatarLoadFailed.value = false })

const navGroups = computed(() => [
  {
    label: '概览',
    items: [
      { label: '仪表盘', icon: 'ti-layout-dashboard', to: '/admin/dashboard' }
    ]
  },
  {
    label: '内容',
    items: [
      { label: '文章管理', icon: 'ti-article', to: '/admin/articles' },
      { label: '发布文章', icon: 'ti-pencil-plus', to: '/admin/articles/create' },
      { label: t('media.admin.menu'), icon: 'ti-books', to: '/admin/media-reviews' },
      { label: '评论管理', icon: 'ti-message-circle', to: '/admin/comments' },
      { label: '分类管理', icon: 'ti-folders', to: '/admin/categories' },
      { label: t('adminTag.menu'), icon: 'ti-tags', to: '/admin/tags' }
    ]
  },
  {
    label: '站点',
    items: [
      { label: t('siteBackground.menu'), icon: 'ti-photo-cog', to: '/admin/site-backgrounds' },
      { label: '黑名单', icon: 'ti-user-shield', to: '/admin/blacklist' },
      { label: '个人资料', icon: 'ti-user-circle', to: '/admin/profile' }
    ]
  }
])

const isActive = to => route.path === to || route.path.startsWith(`${to}/`)
const avatarText = computed(() => {
  const value = userInfo.value?.nickname?.trim() || userInfo.value?.username?.trim() || userInfo.value?.email?.trim() || 'A'
  return (Array.from(value)[0] || 'A').toUpperCase()
})
const showAvatarImage = computed(() => Boolean(userInfo.value?.avatar) && !avatarLoadFailed.value)
</script>

<template>
    <button
      v-if="mobileOpen"
      class="sidebar-scrim"
      type="button"
      aria-label="关闭导航"
      @click="emit('close-mobile')"
    ></button>
    <aside :class="['admin-sidebar', { collapsed, 'mobile-open': mobileOpen }]" aria-label="后台导航">
      <div class="sidebar-brand">
        <div class="brand-mark" aria-hidden="true">H</div>
        <div class="brand-copy">
          <strong>Hathaway’s Blog</strong>
          <span>管理控制台</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <section v-for="group in navGroups" :key="group.label" class="nav-group">
          <p class="nav-group-label">{{ group.label }}</p>
          <router-link
            v-for="item in group.items"
            :key="item.to"
            :to="item.to"
            :class="['nav-item', { active: isActive(item.to) }]"
            :title="collapsed ? item.label : undefined"
            :aria-label="item.label"
            @click="emit('close-mobile')"
          >
            <AdminIcon :name="item.icon" />
            <span>{{ item.label }}</span>
          </router-link>
        </section>
      </nav>

      <div class="sidebar-footer">
        <div class="admin-identity">
          <div class="sidebar-avatar">
            <img
              v-if="showAvatarImage"
              :src="userInfo.avatar"
              alt=""
              @error="avatarLoadFailed = true"
            />
            <span v-else>{{ avatarText }}</span>
          </div>
          <div class="identity-copy">
            <strong>{{ userInfo?.nickname || userInfo?.username || '管理员' }}</strong>
            <span>Hathaway’s Blog</span>
          </div>
        </div>
        <router-link to="/home" class="visit-site" title="访问站点" @click="emit('close-mobile')">
          <AdminIcon name="ti-external-link" />
          <span>访问站点</span>
        </router-link>
      </div>
    </aside>
</template>

<style scoped>
.admin-sidebar {
  position: relative;
  z-index: 30;
  width: var(--admin-sidebar-width);
  height: 100%;
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  background: var(--admin-sidebar-bg);
  overflow: hidden;
  border: 1px solid var(--admin-border);
  border-radius: 19px;
  box-shadow: 8px 0 24px rgba(4, 5, 14, .12);
  -webkit-backdrop-filter: blur(var(--admin-blur));
  backdrop-filter: blur(var(--admin-blur));
  transition: width .22s ease, transform .22s ease;
}

.admin-sidebar.collapsed { width: var(--admin-sidebar-collapsed); }

.sidebar-brand {
  min-height: var(--admin-header-height);
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid var(--admin-border);
  overflow: hidden;
}

.brand-mark {
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(211, 198, 255, .3);
  border-radius: 13px;
  background: linear-gradient(145deg, rgba(178, 153, 244, .2), rgba(215, 140, 175, .09));
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, .16), 0 6px 15px rgba(3, 4, 12, .11);
  color: #f2ebff;
  font-family: Georgia, serif;
  font-size: 18px;
}

.brand-copy,
.identity-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  white-space: nowrap;
  transition: opacity .16s ease, transform .2s ease;
}

.brand-copy strong { color: var(--admin-text-primary); font-family: Georgia, 'Noto Serif SC', serif; font-size: 14px; letter-spacing: .02em; }
.brand-copy span { color: var(--admin-text-muted); font-size: 10px; letter-spacing: .16em; }

.sidebar-nav {
  flex: 1;
  overflow: hidden auto;
  padding: 16px 12px;
  scrollbar-width: thin;
  scrollbar-color: rgba(170, 148, 237, .28) transparent;
}

.nav-group + .nav-group { margin-top: 17px; }
.nav-group-label { margin: 0 10px 7px; color: var(--admin-text-muted); font-size: 10px; letter-spacing: .18em; }

.nav-item {
  position: relative;
  height: 42px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 3px 0;
  padding: 0 12px;
  overflow: hidden;
  border: 1px solid transparent;
  border-radius: 12px;
  color: var(--admin-text-secondary);
  font-size: 13px;
  text-decoration: none;
  white-space: nowrap;
  transition: color .18s ease, background .18s ease, border-color .18s ease;
}

.nav-item::before {
  position: absolute;
  inset: 8px auto 8px 0;
  width: 3px;
  border-radius: 99px;
  background: var(--admin-accent);
  box-shadow: 0 0 9px rgba(170, 148, 237, .45);
  content: '';
  opacity: 0;
}

.nav-item:hover { color: var(--admin-text-primary); background: rgba(255, 255, 255, .035); }
.nav-item.active { color: #f0eaff; background: var(--admin-accent-soft); border-color: rgba(203, 188, 255, .18); }
.nav-item.active::before { opacity: 1; }
.nav-item :deep(.admin-icon) { width: 18px; height: 18px; flex: 0 0 18px; }

.sidebar-footer { padding: 14px 12px 18px; border-top: 1px solid var(--admin-border); }
.admin-identity { display: flex; align-items: center; gap: 10px; min-height: 48px; padding: 4px 8px 10px; overflow: hidden; }
.sidebar-avatar { width: 34px; height: 34px; flex: 0 0 34px; display: grid; place-items: center; overflow: hidden; border: 1px solid rgba(255,255,255,.16); border-radius: 50%; background: linear-gradient(145deg, #9d86df, #c37fa1); color: white; font-size: 12px; font-weight: 700; }
.sidebar-avatar img { width: 100%; height: 100%; object-fit: cover; }
.identity-copy strong { overflow: hidden; color: var(--admin-text-primary); font-size: 12px; font-weight: 600; text-overflow: ellipsis; }
.identity-copy span { color: var(--admin-text-muted); font-size: 10px; }
.visit-site { height: 38px; display: flex; align-items: center; justify-content: center; gap: 9px; border: 1px solid var(--admin-border); border-radius: 11px; background: rgba(255,255,255,.022); color: var(--admin-text-secondary); font-size: 12px; text-decoration: none; transition: background .18s ease, color .18s ease; }
.visit-site:hover { color: var(--admin-text-primary); background: rgba(255,255,255,.05); }

.admin-sidebar.collapsed .brand-copy,
.admin-sidebar.collapsed .identity-copy,
.admin-sidebar.collapsed .nav-item span,
.admin-sidebar.collapsed .nav-group-label,
.admin-sidebar.collapsed .visit-site span { opacity: 0; pointer-events: none; transform: translateX(-6px); }
.admin-sidebar.collapsed .sidebar-brand { padding: 0 20px; }
.admin-sidebar.collapsed .sidebar-nav { padding-inline: 12px; }
.admin-sidebar.collapsed .nav-item { justify-content: center; padding-inline: 0; }
.admin-sidebar.collapsed .admin-identity { justify-content: center; padding-inline: 0; }

.sidebar-scrim { display: none; }

@media (max-width: 900px) {
  .admin-sidebar,
  .admin-sidebar.collapsed { position: fixed; inset: 12px auto 12px 12px; width: min(286px, calc(84vw - 12px)); height: auto; transform: translateX(calc(-102% - 12px)); }
  .admin-sidebar.mobile-open { transform: translateX(0); }
  .admin-sidebar.collapsed .brand-copy,
  .admin-sidebar.collapsed .identity-copy,
  .admin-sidebar.collapsed .nav-item span,
  .admin-sidebar.collapsed .nav-group-label,
  .admin-sidebar.collapsed .visit-site span { opacity: 1; pointer-events: auto; transform: none; }
  .admin-sidebar.collapsed .nav-item { justify-content: flex-start; padding-inline: 12px; }
  .sidebar-scrim { position: fixed; inset: 0; z-index: 29; display: block; border: 0; background: rgba(5, 6, 15, .66); -webkit-backdrop-filter: blur(3px); backdrop-filter: blur(3px); }
}
</style>
