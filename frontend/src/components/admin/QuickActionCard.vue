<script setup>
import AdminIcon from '@/components/admin/AdminIcon.vue'

defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  icon: { type: String, default: 'ti-arrow-up-right' },
  to: { type: String, required: true },
  tone: { type: String, default: 'purple' }
})
</script>

<template>
  <router-link :to="to" :class="['quick-action', `tone-${tone}`]">
    <span class="quick-icon"><AdminIcon :name="icon" /></span>
    <span class="quick-copy"><strong>{{ title }}</strong><small>{{ description }}</small></span>
    <AdminIcon name="ti-arrow-up-right" class="action-arrow" />
  </router-link>
</template>

<style scoped>
.quick-action { position: relative; min-width: 0; min-height: 108px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 7px; padding: 12px 8px; border: 1px solid var(--admin-border); border-radius: 13px; background: rgba(255,255,255,.016); color: var(--admin-text-secondary); text-align: center; text-decoration: none; transition: background .18s ease, border-color .18s ease, transform .18s ease; }
.quick-action:hover { background: rgba(255,255,255,.04); border-color: var(--admin-border-strong); transform: translateY(-1px); }
.quick-icon { width: 38px; height: 38px; display: grid; place-items: center; border-radius: 12px; background: color-mix(in srgb, var(--tone) 8%, transparent); color: var(--tone); }
.quick-icon :deep(.admin-icon) { width: 17px; height: 17px; }
.quick-copy { min-width: 0; display: flex; flex-direction: column; align-items: center; }
.quick-copy strong { color: var(--admin-text-primary); font-size: 11px; font-weight: 500; }
.quick-copy small { overflow: hidden; color: var(--admin-text-muted); font-size: 9px; text-overflow: ellipsis; white-space: nowrap; }
.action-arrow { position: absolute; top: 9px; right: 9px; width: 12px; height: 12px; color: var(--admin-text-muted); }
.tone-purple { --tone: var(--admin-accent); }
.tone-pink { --tone: var(--admin-pink); }
.tone-blue { --tone: var(--admin-blue); }
.tone-amber { --tone: var(--admin-warning); }
</style>
