<template>
  <footer class="footer">
    <span>© {{ year }} Hathaway · {{ t('footer.builtWith') }}</span>
    <span>{{ t('footer.location') }}</span>
  </footer>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const year = new Date().getFullYear()
</script>

<style scoped>
.footer {
  margin-top: auto;
  border-top: 1px solid var(--glass-border, var(--border));
  background: var(--nav-bg);
  -webkit-backdrop-filter: blur(16px);
  backdrop-filter: blur(16px);
  padding: 1rem 1.5rem;
  display: flex; justify-content: space-between; flex-wrap: wrap; gap: .4rem 1rem;
  font-size: 12px; color: var(--text-3);
}
@media (max-width: 480px) {
  .footer { flex-direction: column; align-items: center; padding-inline: 1rem; text-align: center; }
}
</style>
