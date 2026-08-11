<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { SUPPORTED_LOCALES, setLocale } from '@/locales/index.js'

const { locale, t } = useI18n()
const emit = defineEmits(['visibility-change'])
const currentLocale = computed(
  () => SUPPORTED_LOCALES.find(item => item.value === locale.value) || SUPPORTED_LOCALES[0]
)
const handleCommand = value => setLocale(value)
</script>

<template>
  <el-dropdown
    trigger="click"
    placement="bottom-end"
    popper-class="language-menu-popper"
    @command="handleCommand"
    @visible-change="visible => emit('visibility-change', visible)"
  >
    <button
      class="language-switcher"
      type="button"
      :aria-label="t('language.label')"
      :title="t('language.label')"
    >
      <i class="ti ti-world" aria-hidden="true"></i>
      <span class="language-current">{{ currentLocale.shortLabel }}</span>
      <i class="ti ti-chevron-down language-chevron" aria-hidden="true"></i>
    </button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in SUPPORTED_LOCALES"
          :key="item.value"
          :command="item.value"
          :class="{ 'is-current-locale': locale === item.value }"
        >
          <i v-if="locale === item.value" class="ti ti-check" aria-hidden="true"></i>
          <span v-else class="check-placeholder"></span>
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped>
.language-switcher {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  min-width: 76px;
  height: 30px;
  padding-inline: 10px;
  border: .5px solid var(--border);
  border-radius: 999px;
  background: rgba(255,255,255,.16);
  box-shadow: 0 2px 8px var(--shadow-color);
  color: var(--text-2);
  cursor: pointer;
  font-family: inherit;
  white-space: nowrap;
  transition: color .15s ease, border-color .15s ease, background-color .15s ease;
}
.language-switcher:hover,
.language-switcher:focus-visible {
  color: var(--accent-color);
  border-color: var(--border-h);
  background: var(--purple-50);
  outline: none;
}
.language-switcher > .ti-world { font-size: 15px; }
.language-current { font-size: 11px; }
.language-chevron { font-size: 11px; opacity: .65; }
.check-placeholder { display: inline-block; width: 14px; }

:global(.language-menu-popper .el-dropdown-menu) {
  min-width: 136px;
  padding: 6px;
  border-radius: 10px;
  background: var(--bg-card);
  box-shadow: 0 8px 24px var(--shadow-color);
}
:global(.language-menu-popper .el-dropdown-menu__item) {
  gap: 7px;
  border-radius: 6px;
  color: var(--text-2);
  white-space: nowrap;
}
:global(.language-menu-popper .el-dropdown-menu__item:hover),
:global(.language-menu-popper .el-dropdown-menu__item.is-current-locale) {
  background: var(--purple-50);
  color: var(--accent-color);
}

@media (max-width: 680px) {
  .language-switcher {
    min-width: 30px;
    width: 30px;
    padding-inline: 0;
  }
  .language-current,
  .language-chevron { display: none; }
}
</style>
