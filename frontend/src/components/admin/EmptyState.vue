<script setup>
import AdminIcon from '@/components/admin/AdminIcon.vue'

defineProps({
  title: { type: String, default: '暂无数据' },
  description: { type: String, default: '' },
  icon: { type: String, default: 'ti-database-off' },
  compact: { type: Boolean, default: false }
})
</script>

<template>
  <div :class="['admin-empty-state', { compact }]">
    <div class="empty-icon"><AdminIcon :name="icon" /></div>
    <strong>{{ title }}</strong>
    <p v-if="description">{{ description }}</p>
    <slot />
  </div>
</template>

<style scoped>
.admin-empty-state { min-height: 220px; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 26px; text-align: center; }
.admin-empty-state.compact { min-height: 150px; }
.empty-icon { width: 44px; height: 44px; display: grid; place-items: center; margin-bottom: 12px; border: 1px solid var(--admin-border); border-radius: 14px; background: rgba(255,255,255,.018); color: var(--admin-text-muted); }
.empty-icon :deep(.admin-icon) { width: 19px; height: 19px; }
.admin-empty-state strong { color: var(--admin-text-secondary); font-size: 12px; font-weight: 500; }
.admin-empty-state p { max-width: 330px; margin: 5px 0 0; color: var(--admin-text-muted); font-size: 10px; line-height: 1.6; }
</style>
