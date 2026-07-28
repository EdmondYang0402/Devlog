<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ratingToStars, starsToRating } from '@/constants/mediaReview.js'

const props = defineProps({
  modelValue: { type: Number, default: null },
  readonly: { type: Boolean, default: false },
  showScore: { type: Boolean, default: true }
})
const emit = defineEmits(['update:modelValue'])
const { t } = useI18n()

const stars = computed({
  get: () => ratingToStars(props.modelValue),
  set: value => emit('update:modelValue', starsToRating(value))
})
</script>

<template>
  <div class="media-rating" :class="{ 'is-empty': modelValue == null }">
    <el-rate v-model="stars" allow-half :disabled="readonly" :clearable="!readonly" />
    <span v-if="showScore" class="rating-score">
      {{ modelValue == null ? t('media.unrated') : t('media.scoreOutOfTen', { score: modelValue }) }}
    </span>
  </div>
</template>

<style scoped>
.media-rating { display:flex; align-items:center; flex-wrap:wrap; gap:8px; min-width:0; }
.media-rating :deep(.el-rate) { height:auto; }
.rating-score { color:var(--text-2); font-size:12px; white-space:nowrap; }
.is-empty .rating-score { color:var(--text-3); }
</style>
