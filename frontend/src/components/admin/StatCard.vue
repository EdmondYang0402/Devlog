<script setup>
import AdminIcon from '@/components/admin/AdminIcon.vue'

defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], default: '--' },
  hint: { type: String, default: '' },
  icon: { type: String, default: 'ti-chart-bar' },
  tone: { type: String, default: 'purple' },
  loading: { type: Boolean, default: false }
})
</script>

<template>
  <article :class="['stat-card', `tone-${tone}`]">
    <div class="stat-icon"><AdminIcon :name="icon" /></div>
    <div class="stat-copy">
      <span>{{ label }}</span>
      <strong :class="{ loading }">{{ loading ? '···' : value }}</strong>
      <small v-if="hint">{{ hint }}</small>
    </div>
    <div class="stat-orbit" aria-hidden="true"></div>
  </article>
</template>

<style scoped>
.stat-card { position: relative; min-width: 0; min-height: 142px; display: flex; align-items: flex-start; gap: 14px; overflow: hidden; padding: 19px; border: 1px solid var(--admin-border); border-radius: var(--admin-radius); background: linear-gradient(145deg, var(--admin-panel-strong), var(--admin-panel)); box-shadow: var(--admin-shadow); -webkit-backdrop-filter: blur(var(--admin-blur)); backdrop-filter: blur(var(--admin-blur)); transition: background .2s ease, border-color .2s ease, transform .2s ease; }
.stat-card:hover { background: linear-gradient(145deg, var(--admin-panel-hover), var(--admin-panel)); border-color: var(--admin-border-strong); transform: translateY(-1px); }
.stat-icon { position: relative; z-index: 1; width: 42px; height: 42px; flex: 0 0 42px; display: grid; place-items: center; border: 1px solid color-mix(in srgb, currentColor 22%, transparent); border-radius: 13px; background: color-mix(in srgb, currentColor 9%, transparent); color: var(--tone); }
.stat-icon :deep(.admin-icon) { width: 19px; height: 19px; }
.stat-copy { position: relative; z-index: 1; min-width: 0; display: flex; flex-direction: column; }
.stat-copy > span { color: var(--admin-text-secondary); font-size: 11px; }
.stat-copy strong { margin: 2px 0 1px; color: var(--admin-text-primary); font-family: Georgia, serif; font-size: 30px; font-weight: 500; line-height: 1.15; }
.stat-copy strong.loading { color: var(--admin-text-muted); }
.stat-copy small { overflow: hidden; color: var(--admin-text-muted); font-size: 9px; text-overflow: ellipsis; white-space: nowrap; }
.stat-orbit { position: absolute; right: -24px; bottom: -36px; width: 108px; height: 108px; border: 1px solid color-mix(in srgb, var(--tone) 15%, transparent); border-radius: 50%; box-shadow: 0 0 30px color-mix(in srgb, var(--tone) 6%, transparent); }
.tone-purple { --tone: var(--admin-accent); }
.tone-pink { --tone: var(--admin-pink); }
.tone-blue { --tone: var(--admin-blue); }
.tone-amber { --tone: var(--admin-warning); }
</style>
