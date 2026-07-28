<script setup>
import ArchiveGridCard from './ArchiveGridCard.vue'
defineProps({ articles: { type: Array, required: true } })
</script>

<template>
  <div class="archive-grid">
    <ArchiveGridCard v-for="article in articles" :key="article.id" :article="article" />
  </div>
</template>

<style scoped>
.archive-grid { display:grid; grid-template-columns:repeat(3,minmax(0,1fr)); gap:16px; }
@media(max-width:760px){.archive-grid{grid-template-columns:repeat(2,minmax(0,1fr));gap:14px}}
@media(max-width:520px){.archive-grid{grid-template-columns:minmax(0,1fr);gap:12px}}
</style>
