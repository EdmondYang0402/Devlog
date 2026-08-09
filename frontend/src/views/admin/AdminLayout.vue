<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AdminHeader from '@/components/admin/AdminHeader.vue'
import '@/assets/css/admin-theme.css'

const route = useRoute()
const { t } = useI18n()
const collapsed = ref(localStorage.getItem('admin-sidebar-collapsed') === 'true')
const mobileOpen = ref(false)
const currentTitle = computed(() => route.meta.titleKey ? t(route.meta.titleKey) : route.meta.title || '后台管理')

const toggleSidebar = () => {
  if (window.matchMedia('(max-width: 900px)').matches) {
    mobileOpen.value = !mobileOpen.value
    return
  }
  collapsed.value = !collapsed.value
  localStorage.setItem('admin-sidebar-collapsed', String(collapsed.value))
}

onMounted(() => document.body.classList.add('admin-mode'))
onUnmounted(() => document.body.classList.remove('admin-mode'))
</script>

<template>
  <div class="admin-shell">
    <div class="admin-backdrop" aria-hidden="true"></div>
    <AdminSidebar
      :collapsed="collapsed"
      :mobile-open="mobileOpen"
      @close-mobile="mobileOpen = false"
    />
    <div class="admin-main">
      <AdminHeader :title="currentTitle" @toggle-sidebar="toggleSidebar" />
      <main class="content">
        <router-view v-slot="{ Component, route: currentRoute }">
          <Transition name="admin-page" mode="out-in">
            <component :is="Component" :key="currentRoute.fullPath" />
          </Transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-shell {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  gap: 14px;
  overflow: hidden;
  padding: 12px;
  background: var(--admin-bg);
}
.admin-backdrop {
  position: fixed;
  inset: 0;
  z-index: 0;
  background:
    var(--admin-backdrop-overlay),
    radial-gradient(circle at 77% 14%, rgba(149, 112, 177, .11), transparent 40%),
    url('/images/background.jpg') center / cover no-repeat;
  filter: saturate(.78) brightness(.8);
  transform: scale(1.02);
}
.admin-main { position: relative; z-index: 1; min-width: 0; flex: 1; display: flex; flex-direction: column; gap: 14px; overflow: hidden; }
.content { min-width: 0; flex: 1; overflow: hidden auto; padding: 0 1px 1px; scrollbar-color: rgba(170, 148, 237, .34) transparent; }

:global(.admin-page-enter-active),
:global(.admin-page-leave-active) {
  transition: opacity 240ms ease, transform 240ms ease;
}

:global(.admin-page-enter-from) {
  opacity: 0;
  transform: translateY(4px);
}

:global(.admin-page-leave-to) {
  opacity: 0;
  transform: translateY(-2px);
}

:global(.admin-page-enter-to),
:global(.admin-page-leave-from) {
  opacity: 1;
  transform: translateY(0);
}

@media (prefers-reduced-motion: reduce) {
  :global(.admin-page-enter-active),
  :global(.admin-page-leave-active) {
    transition: none;
  }

  :global(.admin-page-enter-from),
  :global(.admin-page-leave-to) {
    transform: none;
  }
}

@media (max-width: 900px) { .admin-shell { gap: 0; } }
@media (max-width: 720px) { .admin-shell { padding: 8px; } }
</style>
