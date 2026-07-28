<script setup>
defineProps({
  tag: { type: String, default: 'section' },
  reading: { type: Boolean, default: false }
})
</script>

<template>
  <component :is="tag" class="glass-panel" :class="{ 'glass-panel--reading': reading }">
    <slot />
  </component>
</template>
