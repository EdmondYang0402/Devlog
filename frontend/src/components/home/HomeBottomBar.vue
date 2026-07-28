<script setup>
defineProps({ siteTitle: { type: String, default: '' }, articleCount: { type: [Number, String], default: 0 } })
</script>

<template>
  <div class="bottom-panel glass-panel">
    <span>{{ siteTitle }}</span><i></i><span>{{ articleCount }} ARTICLES</span><i></i><span>SPRING BOOT</span><i></i><span>VUE 3</span><i></i><span>MYSQL · REDIS</span>
  </div>
</template>
