<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { typeKey, statusKey } from '@/constants/mediaReview.js'
import MediaRating from './MediaRating.vue'

const props = defineProps({ items: { type: Array, default: () => [] } })
const { t, locale } = useI18n()

const groups = computed(() => {
  const grouped = new Map()
  props.items.forEach(item => {
    const year = item.finishedDate ? String(item.finishedDate).slice(0, 4) : 'pending'
    if (!grouped.has(year)) grouped.set(year, [])
    grouped.get(year).push(item)
  })
  return [...grouped].map(([year, items]) => ({ year, items }))
})

const formatDate = value => value
  ? new Intl.DateTimeFormat(locale.value, { month: 'short', day: 'numeric' }).format(new Date(`${value}T00:00:00`))
  : t('media.noFinishedDate')
</script>

<template>
  <div class="timeline">
    <section v-for="group in groups" :key="group.year" class="year-group">
      <h2>{{ group.year === 'pending' ? t('media.pendingSection') : group.year }}</h2>
      <div class="entries">
        <router-link v-for="item in group.items" :key="item.id" :to="`/media/${item.id}`" class="entry">
          <span class="dot" aria-hidden="true"></span>
          <time>{{ formatDate(item.finishedDate) }}</time>
          <div class="cover-box">
            <img v-if="item.coverUrl" :src="item.coverUrl" :alt="item.title" loading="lazy" />
            <i v-else class="ti ti-photo" aria-hidden="true"></i>
          </div>
          <div class="entry-main">
            <div class="entry-heading">
              <h3>{{ item.title }}</h3>
              <span>{{ t(`media.type.${typeKey(item.mediaType)}`) }} · {{ t(statusKey(item.mediaType, item.status)) }}</span>
            </div>
            <MediaRating :model-value="item.rating" readonly />
            <p>{{ item.shortReview || t('media.noShortReview') }}</p>
          </div>
        </router-link>
      </div>
    </section>
  </div>
</template>

<style scoped>
.timeline { display:flex; flex-direction:column; gap:32px; }
.year-group { display:grid; grid-template-columns:90px minmax(0,1fr); gap:24px; }
.year-group>h2 { position:sticky; top:84px; align-self:start; color:var(--purple-400); font-size:20px; font-weight:600; }
.entries { position:relative; display:flex; flex-direction:column; gap:14px; padding-left:26px; }
.entries::before { content:''; position:absolute; left:5px; top:8px; bottom:8px; width:1px; background:linear-gradient(var(--purple-200),color-mix(in srgb,var(--purple-200) 10%,transparent)); }
.entry { position:relative; display:grid; grid-template-columns:80px 76px minmax(0,1fr); gap:16px; padding:14px; border:1px solid var(--glass-border); border-radius:18px; background:var(--glass-bg); backdrop-filter:blur(16px);-webkit-backdrop-filter:blur(16px);transition:border-color .2s ease,transform .2s ease; }
.entry:hover { transform:translateX(3px); border-color:var(--border-h); }
.dot { position:absolute; left:-26px; top:25px; width:11px; height:11px; border:3px solid var(--bg); border-radius:50%; background:var(--purple-400); box-shadow:0 0 0 1px var(--purple-200); }
time { padding-top:4px; color:var(--text-3); font-size:12px; }
.cover-box { display:grid; place-items:center; width:76px; height:100px; overflow:hidden; border-radius:9px; background:var(--purple-50); color:var(--purple-200); }
.cover-box img { width:100%; height:100%; object-fit:cover; }
.entry-main { min-width:0; }
.entry-heading { display:flex; align-items:baseline; justify-content:space-between; gap:12px; }
.entry-heading h3 { overflow:hidden; color:var(--text-1); font-size:17px; text-overflow:ellipsis; white-space:nowrap; }
.entry-heading span { flex:none; color:var(--text-3); font-size:11px; }
.entry-main p { display:-webkit-box; overflow:hidden; margin-top:8px; color:var(--text-2); font-size:13px; -webkit-box-orient:vertical; -webkit-line-clamp:2; }
@media(max-width:720px){.year-group{display:block}.year-group>h2{position:static;margin-bottom:12px}.entry{grid-template-columns:58px minmax(0,1fr);gap:12px}.entry time{grid-column:1/-1;padding:0}.cover-box{width:58px;height:78px}.entry-heading{display:block}.entry-heading span{display:block;margin-top:3px}.entries{padding-left:20px}.dot{left:-20px}}
</style>
