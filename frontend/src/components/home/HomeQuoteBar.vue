<script setup>
import { useI18n } from 'vue-i18n'
defineProps({ quote: { type: String, default: '' } })
const { t } = useI18n()
</script>

<template>
  <div class="quote-panel glass-panel">
    <span aria-hidden="true"></span>
    <p :title="quote">{{ quote || t('home.noQuote') }}</p>
    <span aria-hidden="true"></span>
  </div>
</template>
