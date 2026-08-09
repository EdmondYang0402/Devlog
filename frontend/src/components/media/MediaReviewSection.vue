<script setup>
import { useI18n } from 'vue-i18n'
import MediaCategoryIcon from './MediaCategoryIcon.vue'
import MediaReviewCard from './MediaReviewCard.vue'

defineProps({
  section: { type: Object, required: true },
  items: { type: Array, default: () => [] },
  showHeading: { type: Boolean, default: true }
})

const { t } = useI18n()
</script>

<template>
  <section
    class="media-section"
    :aria-labelledby="showHeading ? `media-section-${section.key}` : undefined"
    :aria-label="showHeading ? undefined : t(section.titleKey)"
  >
    <header v-if="showHeading" class="section-heading">
      <MediaCategoryIcon :type="section.iconType" size="small" />
      <h2 :id="`media-section-${section.key}`">{{ t(section.titleKey) }}</h2>
      <span class="section-count">{{ items.length }}</span>
    </header>

    <div v-if="items.length" class="section-grid">
      <MediaReviewCard
        v-for="item in items"
        :key="item.id"
        :item="item"
        :cover-variant="section.coverVariant"
      />
    </div>
    <div v-else class="section-empty glass-panel">
      <MediaCategoryIcon :type="section.iconType" size="small" />
      <span>{{ t('media.section.empty') }}</span>
    </div>
  </section>
</template>

<style scoped>
.media-section { min-width:0; }
.media-section + .media-section { margin-top:48px; }
.section-heading { display:flex; align-items:center; gap:10px; margin:0 0 16px; padding:0 2px; }
.section-heading h2 { color:var(--text-1); font-size:20px; font-weight:650; line-height:1.3; letter-spacing:-.015em; }
.section-count { min-width:24px; padding:2px 8px; border-radius:999px; background:rgba(127,119,221,.12); color:var(--purple-600); font-size:10px; font-variant-numeric:tabular-nums; text-align:center; }
.section-grid { display:grid; grid-template-columns:repeat(4,minmax(0,1fr)); gap:18px; }
.section-empty { display:flex; min-height:108px; align-items:center; justify-content:center; gap:8px; border-style:dashed; color:var(--text-3); font-size:13px; }
@media(max-width:1100px){.section-grid{grid-template-columns:repeat(3,minmax(0,1fr))}}
@media(max-width:820px){.media-section+.media-section{margin-top:38px}.section-grid{grid-template-columns:repeat(2,minmax(0,1fr));gap:14px}}
@media(max-width:560px){.media-section+.media-section{margin-top:34px}.section-heading{margin-bottom:12px}.section-heading h2{font-size:18px}.section-grid{grid-template-columns:minmax(0,1fr);gap:14px}}
</style>
