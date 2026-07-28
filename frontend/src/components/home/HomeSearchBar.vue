<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()
const keyword = ref('')

const submit = () => {
  const value = keyword.value.trim()
  router.push({ name: 'Archive', query: value ? { keyword: value } : {} })
}
</script>

<template>
  <form class="home-search glass-panel" role="search" @submit.prevent="submit">
    <i class="ti ti-search" aria-hidden="true"></i>
    <input v-model="keyword" type="search" :placeholder="t('home.searchPlaceholder')" />
    <button type="submit" :aria-label="t('common.search')"><i class="ti ti-arrow-right"></i></button>
  </form>
</template>
