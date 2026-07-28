<script setup>
import { computed } from 'vue'
import ArchiveTimelineItem from './ArchiveTimelineItem.vue'

const props = defineProps({ articles: { type: Array, required: true } })

const timelineYears = computed(() => {
  const years = []
  const yearMap = new Map()

  props.articles.forEach((article, index) => {
    const date = new Date(article.createTime)
    const validDate = !Number.isNaN(date.getTime())
    const year = validDate ? String(date.getFullYear()) : '—'
    const month = validDate ? String(date.getMonth() + 1).padStart(2, '0') : '—'

    if (!yearMap.has(year)) {
      const yearGroup = { year, months: [] }
      yearMap.set(year, { group: yearGroup, months: new Map() })
      years.push(yearGroup)
    }

    const yearEntry = yearMap.get(year)
    if (!yearEntry.months.has(month)) {
      const monthGroup = { month, articles: [] }
      yearEntry.months.set(month, monthGroup)
      yearEntry.group.months.push(monthGroup)
    }
    yearEntry.months.get(month).articles.push({ ...article, timelineIndex: index })
  })
  return years
})
</script>

<template>
  <div class="archive-timeline">
    <section v-for="yearGroup in timelineYears" :key="yearGroup.year" class="timeline-year">
      <header class="year-marker"><span>{{ yearGroup.year }}</span></header>
      <div class="year-records">
        <template v-for="monthGroup in yearGroup.months" :key="`${yearGroup.year}-${monthGroup.month}`">
          <ArchiveTimelineItem
            v-for="article in monthGroup.articles"
            :key="article.id"
            :article="article"
            :side="article.timelineIndex % 2 === 0 ? 'right' : 'left'"
          />
        </template>
      </div>
    </section>
  </div>
</template>

<style scoped>
.archive-timeline { position: relative; padding: 0 0 12px; }
.archive-timeline::before {
  position: absolute; z-index: 0; top: 32px; bottom: 0; left: 50%; width: 2px;
  content: ''; transform: translateX(-50%);
  background: linear-gradient(to bottom, transparent, var(--purple-200) 3%, rgba(175,169,236,.42) 94%, transparent);
  box-shadow: 0 0 16px rgba(127,119,221,.28);
}
.timeline-year { position: relative; }
.timeline-year + .timeline-year { margin-top: 34px; }
.year-marker { position: relative; z-index: 2; display: flex; justify-content: center; margin-bottom: 22px; }
.year-marker span {
  padding: 7px 22px; background: linear-gradient(135deg, var(--purple-400), var(--purple-600));
  border: 4px solid var(--bg); border-radius: 999px; box-shadow: 0 7px 20px rgba(83,74,183,.25);
  color: #fff; font-size: 18px; font-weight: 800; letter-spacing: .06em;
}
.timeline-month { position: relative; }
.timeline-month + .timeline-month { margin-top: 22px; }
.month-marker {
  position: relative; z-index: 2; display: flex; align-items: baseline; justify-content: center; gap: 3px;
  width: fit-content; margin: 0 auto 18px; padding: 4px 10px; background: var(--glass-bg);
  border: 1px solid var(--purple-100); border-radius: 999px; box-shadow: 0 5px 14px var(--shadow-color);
  color: var(--purple-600);
}
.month-marker span { font-size: 13px; font-weight: 800; }
.month-marker small { font-size: 8px; letter-spacing: .03em; }
@media (max-width: 1023px) {
  .archive-timeline { padding-left: 44px; }
  .archive-timeline::before { left: 15px; transform: none; }
  .year-marker, .month-marker { justify-content: flex-start; margin-left: -44px; }
  .year-marker { margin-bottom: 18px; }
  .year-marker span { padding: 6px 16px; font-size: 16px; }
  .month-marker { margin-right: 0; margin-bottom: 14px; }
}
@media (max-width: 479px) {
  .archive-timeline { padding-left: 28px; }
  .archive-timeline::before { left: 7px; }
  .year-marker, .month-marker { margin-left: -28px; }
  .year-marker span { border-width: 3px; }
}

/* 由左右大卡片时间轴收敛为单列紧凑记录。 */
.archive-timeline { padding:0; }
.archive-timeline::before { top:38px; bottom:12px; left:58px; width:1px; transform:none; background:linear-gradient(to bottom,var(--purple-200),rgba(175,169,236,.12)); box-shadow:none; }
.timeline-year + .timeline-year { margin-top:22px; }
.year-marker { justify-content:flex-start; margin:0 0 8px; }
.year-marker span { min-width:58px; padding:4px 10px; background:rgba(127,119,221,.18); border:1px solid rgba(175,169,236,.42); border-radius:999px; box-shadow:none; color:var(--purple-600); font-size:14px; font-weight:700; text-align:center; }
.year-records { display:flex; flex-direction:column; gap:7px; padding-left:72px; }
@media(max-width:520px){.archive-timeline{padding-left:0}.archive-timeline::before{left:45px}.year-marker,.month-marker{margin-left:0}.year-marker span{min-width:46px;font-size:12px}.year-records{padding-left:56px}}

/* 封面卡片时间轴 */
.archive-timeline::before { top:48px; bottom:16px; left:50%; width:2px; transform:translateX(-50%); background:linear-gradient(to bottom,rgba(111,91,255,.78),rgba(255,255,255,.2)); }
.timeline-year + .timeline-year { margin-top:28px; }
.year-marker { justify-content:center; margin:0 0 20px; }
.year-marker span { min-width:0; padding:7px 16px; background:rgba(255,255,255,.5); border:1px solid rgba(255,255,255,.58); color:var(--archive-text-primary,#111827); font-size:14px; backdrop-filter:blur(12px);-webkit-backdrop-filter:blur(12px); }
.year-records { gap:20px; padding:0; }
@media(max-width:700px){.archive-timeline::before{left:12px;transform:none}.year-marker{justify-content:flex-start;padding-left:0}.year-marker span{min-width:58px}.year-records{padding:0}}
</style>
