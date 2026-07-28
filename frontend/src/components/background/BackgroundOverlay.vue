<script setup>
defineProps({ reading: { type: Boolean, default: false } })
</script>
<template><div class="front-overlay" :class="{ 'is-reading': reading }" aria-hidden="true"></div></template>
<style scoped>
.front-overlay{position:fixed;inset:0;z-index:-2;background:linear-gradient(180deg,rgba(18,18,32,.22),rgba(25,24,42,.36))}.front-overlay::after{position:absolute;inset:0;content:'';background:radial-gradient(circle at 14% 8%,rgba(255,255,255,.14),transparent 35%),linear-gradient(120deg,rgba(102,91,158,.08),rgba(32,76,76,.08))}.front-overlay.is-reading{background:linear-gradient(180deg,rgba(18,20,32,.46),rgba(20,21,34,.62))}:global(html[data-theme='dark']) .front-overlay{background:linear-gradient(180deg,rgba(5,7,15,.5),rgba(7,9,18,.68))}:global(html[data-theme='dark']) .front-overlay.is-reading{background:linear-gradient(180deg,rgba(3,5,12,.68),rgba(5,7,14,.8))}
</style>
