<script setup>
import { computed } from 'vue'
import { useSeasonEffect } from '@/composables/useSeasonEffect.js'
import SakuraEffect from './SakuraEffect.vue'
import RainEffect from './RainEffect.vue'
import LeafEffect from './LeafEffect.vue'
import SnowEffect from './SnowEffect.vue'

const effects = { sakura: SakuraEffect, rain: RainEffect, leaf: LeafEffect, snow: SnowEffect }
const { effectiveEffect } = useSeasonEffect()
const activeEffect = computed(() => effects[effectiveEffect.value] || null)
</script>

<template>
  <div v-if="activeEffect" class="season-effect-layer" :data-season-effect="effectiveEffect" aria-hidden="true">
    <component :is="activeEffect" />
  </div>
</template>

<style>
.season-effect-layer,.season-particle-field{position:fixed;inset:0;overflow:hidden;pointer-events:none}
.season-effect-layer{z-index:60;contain:strict}
.season-particle-field{z-index:0}
@media (prefers-reduced-motion:reduce){.season-effect-layer{display:none}}
</style>
