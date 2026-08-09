<script setup>
defineProps({
  eyebrow: { type: String, default: '' },
  title: { type: String, required: true },
  description: { type: String, default: '' }
})
</script>

<template>
  <header class="admin-page-header">
    <div>
      <span v-if="eyebrow">{{ eyebrow }}</span>
      <h2>{{ title }}</h2>
      <p v-if="description">{{ description }}</p>
    </div>
    <div v-if="$slots.actions" class="page-actions"><slot name="actions" /></div>
  </header>
</template>

<style scoped>
.admin-page-header { display: flex; align-items: flex-end; justify-content: space-between; gap: 24px; padding: 2px 2px 4px; }
.admin-page-header > div:first-child { min-width: 0; }
.admin-page-header span { color: var(--admin-accent); font-size: 9px; letter-spacing: .18em; }
.admin-page-header h2 { margin: 3px 0 0; color: var(--admin-text-primary); font-family: Georgia, 'Noto Serif SC', serif; font-size: clamp(22px, 2.5vw, 31px); font-weight: 500; line-height: 1.18; }
.admin-page-header p { max-width: 640px; margin: 7px 0 0; color: var(--admin-text-secondary); font-size: 11px; }
.page-actions { flex: 0 0 auto; }
@media (max-width: 640px) { .admin-page-header { align-items: flex-start; flex-direction: column; gap: 14px; } }
</style>
