<script setup>
defineProps({
  title: { type: String, default: '' },
  eyebrow: { type: String, default: '' },
  flush: { type: Boolean, default: false }
})
</script>

<template>
  <section :class="['admin-panel', 'panel', { flush }]">
    <header v-if="title || eyebrow || $slots.actions" class="panel-header">
      <div>
        <span v-if="eyebrow">{{ eyebrow }}</span>
        <h2 v-if="title">{{ title }}</h2>
      </div>
      <div v-if="$slots.actions" class="panel-actions"><slot name="actions" /></div>
    </header>
    <div class="panel-body"><slot /></div>
  </section>
</template>

<style scoped>
.panel { min-width: 0; overflow: hidden; }
.panel-header { min-height: 67px; display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 15px 18px; border-bottom: 1px solid var(--admin-border); }
.panel-header span { display: block; margin-bottom: 2px; color: var(--admin-text-muted); font-size: 9px; letter-spacing: .16em; }
.panel-header h2 { margin: 0; color: var(--admin-text-primary); font-family: Georgia, 'Noto Serif SC', serif; font-size: 16px; font-weight: 500; }
.panel-actions { display: flex; align-items: center; gap: 8px; }
.panel-body { padding: 18px; }
.flush .panel-body { padding: 0; }
</style>
