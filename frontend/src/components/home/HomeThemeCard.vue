<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useTheme } from '@/composables/useTheme.js'

const { t } = useI18n()
const { isDark, toggleTheme } = useTheme()
const label = computed(() => t(isDark.value ? 'theme.toLight' : 'theme.toDark'))
</script>

<template>
  <button class="theme-card glass-panel" type="button" :aria-label="label" @click="toggleTheme">
    <i :class="['ti', isDark ? 'ti-moon' : 'ti-sun']"></i>
    <span>{{ label }}</span>
    <small>{{ t('home.themeHint') }}</small>
  </button>
</template>
