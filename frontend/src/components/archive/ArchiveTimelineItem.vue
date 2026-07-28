<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const DEFAULT_COVER = '/images/hero.jpg'
const { t } = useI18n()
const props = defineProps({
  article: { type: Object, required: true },
  side: { type: String, default: 'left' }
})
const normalizeCover = value => typeof value === 'string' ? value.trim() : ''
const coverSource = computed(() => normalizeCover(props.article.coverImage) || normalizeCover(props.article.coverUrl) || DEFAULT_COVER)
const useFallbackCover = event => {
  if (!event.target.src.endsWith(DEFAULT_COVER)) event.target.src = DEFAULT_COVER
}
const publishedAt = computed(() => props.article.publishTime || props.article.createTime || props.article.createdAt || '')
const normalizedTags = computed(() => {
  const source = props.article.tags ?? props.article.tagList ?? props.article.articleTags ?? []
  const values = Array.isArray(source)
    ? source
    : typeof source === 'string'
      ? source.split(/[,，]/)
      : []

  return values.map((tag, index) => {
    const name = typeof tag === 'string' ? tag.trim() : String(tag?.name || '').trim()
    if (!name) return null
    return { key: `${tag?.id ?? name}-${index}`, name }
  }).filter(Boolean)
})
const visibleTags = computed(() => normalizedTags.value.slice(0, 4))
const hiddenTagCount = computed(() => Math.max(normalizedTags.value.length - visibleTags.value.length, 0))
const formatExactDateTime = value => {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  const pad = number => String(number).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}
</script>

<template>
  <article class="timeline-item" :class="`is-${side}`">
    <span class="timeline-node" aria-hidden="true"></span>
    <router-link :to="`/post/${article.id}`" class="timeline-card">
      <img class="timeline-cover" :src="coverSource" :alt="article.title" loading="lazy" decoding="async" @error="useFallbackCover" />
      <div class="timeline-content">
        <div class="timeline-card-meta">
          <time class="timeline-card-time" :datetime="publishedAt || undefined">{{ formatExactDateTime(publishedAt) }}</time>
          <span class="category-pill">{{ article.categoryName || t('article.uncategorized') }}</span>
        </div>
        <h2>{{ article.title }}</h2>
        <div v-if="visibleTags.length" class="timeline-tags">
          <span v-for="tag in visibleTags" :key="tag.key" class="timeline-tag">#{{ tag.name }}</span>
          <span v-if="hiddenTagCount" class="timeline-tag timeline-tag-more">+{{ hiddenTagCount }}</span>
        </div>
        <p>{{ article.summary || t('article.noSummary') }}</p>
      </div>
    </router-link>
  </article>
</template>

<style scoped>
.timeline-item {
  position: relative; display: grid; grid-template-columns: minmax(0, 1fr) 72px minmax(0, 1fr);
  align-items: center; margin-bottom: 22px;
}
.timeline-node {
  position: relative; z-index: 3; grid-column: 2; grid-row: 1; justify-self: center;
  display: grid; place-items: center; width: 20px; height: 20px; background: var(--bg);
  border: 2px solid var(--purple-200); border-radius: 50%;
  box-shadow: 0 0 0 5px rgba(175,169,236,.14), 0 0 18px rgba(127,119,221,.3);
}
.timeline-node span { width: 7px; height: 7px; background: var(--pink-400); border-radius: 50%; }
.timeline-card {
  position: relative; display: grid; grid-template-columns: minmax(116px, 35%) minmax(0, 1fr);
  min-width: 0; min-height: 190px; overflow: visible; background: var(--glass-bg);
  border: 1px solid var(--glass-border); border-radius: 20px;
  box-shadow: 0 10px 28px var(--shadow-color); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px);
  transition: transform .24s ease, box-shadow .24s ease, border-color .24s ease;
}
.timeline-card::after { position: absolute; top: 50%; width: 28px; height: 1px; content: ''; background: linear-gradient(90deg, var(--purple-200), transparent); }
.is-right .timeline-card { grid-column: 3; grid-row: 1; }
.is-right .timeline-card::after { left: -29px; }
.is-left .timeline-card { grid-column: 1; grid-row: 1; }
.is-left .timeline-card::after { right: -29px; transform: rotate(180deg); }
.timeline-card:hover, .timeline-card:focus-visible { transform: translateY(-4px); border-color: var(--border-h); box-shadow: 0 16px 38px var(--shadow-color); outline: none; }
.timeline-cover { min-width: 0; overflow: hidden; border-radius: 19px 0 0 19px; }
.timeline-cover img { display: block; width: 100%; height: 100%; min-height: 190px; object-fit: cover; object-position: center; transition: transform .35s ease; }
.timeline-card:hover img { transform: scale(1.04); }
.timeline-content { display: flex; flex-direction: column; min-width: 0; padding: 20px; }
.published-time { display: inline-flex; align-items: center; gap: 5px; color: var(--pink-600); font-size: 10px; font-weight: 650; }
.timeline-content h2 { display: -webkit-box; margin: 8px 0 5px; overflow: hidden; color: var(--text-1); font-size: clamp(16px, 1.5vw, 20px); line-height: 1.45; -webkit-box-orient: vertical; -webkit-line-clamp: 2; }
.timeline-content > p { display: -webkit-box; margin: 0; overflow: hidden; color: var(--text-2); font-size: 12px; line-height: 1.7; -webkit-box-orient: vertical; -webkit-line-clamp: 3; }
.timeline-tags { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 8px; max-height: 22px; overflow: hidden; }
.timeline-tags span { max-width: 110px; padding: 1px 7px; overflow: hidden; background: rgba(212,83,126,.1); border-radius: 999px; color: var(--pink-600); font-size: 9px; text-overflow: ellipsis; white-space: nowrap; }
.timeline-tags .more { background: var(--purple-50); color: var(--purple-600); }
.timeline-content footer { display: flex; align-items: center; gap: 10px; margin-top: auto; padding-top: 12px; color: var(--text-3); }
.category-pill { max-width: 55%; padding: 3px 9px; overflow: hidden; background: var(--purple-50); border-radius: 999px; color: var(--purple-600); font-size: 10px; font-weight: 600; text-overflow: ellipsis; white-space: nowrap; }
.stat { display: inline-flex; align-items: center; gap: 3px; font-size: 10px; }
@media (max-width: 1199px) {
  .timeline-item { grid-template-columns: minmax(0, 1fr) 58px minmax(0, 1fr); }
  .timeline-card { grid-template-columns: 1fr; }
  .timeline-cover { height: 128px; border-radius: 19px 19px 0 0; }
  .timeline-cover img { min-height: 0; }
}
@media (max-width: 1023px) {
  .timeline-item { display: block; margin-bottom: 18px; }
  .timeline-node { position: absolute; top: 34px; left: -38px; }
  .timeline-card, .is-right .timeline-card, .is-left .timeline-card { display: grid; grid-template-columns: minmax(150px, 32%) minmax(0, 1fr); width: 100%; }
  .is-right .timeline-card::after, .is-left .timeline-card::after { top: 43px; right: auto; left: -24px; width: 23px; transform: none; }
  .timeline-cover { height: auto; border-radius: 19px 0 0 19px; }
}
@media (max-width: 640px) {
  .timeline-card, .is-right .timeline-card, .is-left .timeline-card { grid-template-columns: 1fr; }
  .timeline-cover { height: auto; aspect-ratio: 16 / 8.5; border-radius: 19px 19px 0 0; }
  .timeline-content { padding: 16px; }
}
@media (max-width: 479px) {
  .timeline-node { left: -30px; width: 16px; height: 16px; }
  .timeline-node span { width: 5px; height: 5px; }
  .timeline-card { border-radius: 16px; }
  .timeline-cover { border-radius: 15px 15px 0 0; }
  .timeline-content > p { -webkit-line-clamp: 2; }
}
@media (prefers-reduced-motion: reduce) { .timeline-card, .timeline-cover img { transition: none; } }

/* 紧凑单行时间记录覆盖旧版交错卡片布局。 */
.timeline-item { position:relative; display:block; margin:0; }
.timeline-node { position:absolute; z-index:2; top:50%; left:-17px; display:block; width:7px; height:7px; background:var(--pink-400); border:2px solid rgba(255,255,255,.7); border-radius:50%; box-shadow:0 0 0 2px rgba(175,169,236,.18); transform:translate(-50%,-50%); }
.timeline-card,.is-right .timeline-card,.is-left .timeline-card { display:grid; grid-template-columns:54px minmax(0,1fr) auto 14px; align-items:center; gap:10px; width:100%; min-height:62px; padding:9px 12px; overflow:hidden; background:rgba(255,255,255,.2); border:1px solid rgba(255,255,255,.26); border-radius:13px; box-shadow:0 6px 18px rgba(20,18,34,.08); backdrop-filter:blur(12px);-webkit-backdrop-filter:blur(12px);transition:transform .22s ease,background .22s ease,border-color .22s ease; }
.timeline-card::after,.is-right .timeline-card::after,.is-left .timeline-card::after { display:none; }
.timeline-card:hover,.timeline-card:focus-visible { transform:translateX(3px); background:rgba(255,255,255,.27); border-color:var(--border-h); box-shadow:0 7px 20px rgba(20,18,34,.1); }
.timeline-card time { color:var(--pink-600); font-size:10px; font-weight:650; }
.timeline-content { display:block; min-width:0; padding:0; }
.timeline-content h2 { display:block; margin:0; overflow:hidden; color:var(--text-1); font-size:14px; font-weight:600; line-height:1.4; text-overflow:ellipsis; white-space:nowrap; }
.timeline-content > p { display:block; margin:2px 0 0; overflow:hidden; color:var(--text-3); font-size:10px; line-height:1.35; text-overflow:ellipsis; white-space:nowrap; }
.category-pill { max-width:120px; padding:2px 8px; background:rgba(127,119,221,.1); font-size:9px; }
.row-arrow { color:var(--text-3); font-size:12px; }
:global(html[data-theme='dark']) .timeline-card{background:rgba(24,24,34,.34);border-color:rgba(255,255,255,.1)}
@media(max-width:640px){.timeline-node{left:-11px}.timeline-card,.is-right .timeline-card,.is-left .timeline-card{grid-template-columns:44px minmax(0,1fr) 12px;min-height:58px;padding:8px 10px}.category-pill{display:none}.timeline-content>p{max-width:100%}}

/* 恢复左右交错的动态封面卡片。 */
.timeline-item { position:relative; display:flex; width:100%; min-height:330px; margin:0; }
.timeline-item.is-left { justify-content:flex-start; padding-right:calc(50% + 34px); }
.timeline-item.is-right { justify-content:flex-end; padding-left:calc(50% + 34px); }
.timeline-node { position:absolute; z-index:3; top:50%; left:50%; width:14px; height:14px; background:#fff; border:3px solid #7c6cf2; border-radius:50%; box-shadow:0 0 0 4px rgba(124,108,242,.18); transform:translate(-50%,-50%); }
.timeline-card,.is-right .timeline-card,.is-left .timeline-card { display:block; width:min(360px,100%); min-height:0; padding:0; overflow:hidden; background:rgba(255,255,255,.58); border:1px solid rgba(255,255,255,.62); border-radius:18px; box-shadow:0 12px 30px rgba(30,24,50,.14); backdrop-filter:blur(16px) saturate(110%);-webkit-backdrop-filter:blur(16px) saturate(110%); }
.timeline-card:hover,.timeline-card:focus-visible { transform:translateY(-4px); background:rgba(255,255,255,.66); box-shadow:0 15px 34px rgba(30,24,50,.16); }
.timeline-cover { display:block; width:100%; height:170px; min-height:0; object-fit:cover; object-position:center; border-radius:0; transition:transform .3s ease; }
.timeline-card:hover .timeline-cover { transform:scale(1.02); }
.timeline-content { display:flex; min-height:150px; flex-direction:column; padding:14px 16px 16px; }
.timeline-card-meta { display:flex; align-items:center; justify-content:space-between; gap:8px; min-width:0; }
.timeline-card-time { flex:1 1 auto; color:var(--archive-text-secondary,rgba(17,24,39,.72)); font-size:10px; font-variant-numeric:tabular-nums; white-space:nowrap; }
.timeline-content h2 { display:-webkit-box; margin:4px 0 5px; color:var(--archive-text-primary,#111827); font-size:16px; line-height:1.4; white-space:normal; -webkit-box-orient:vertical; -webkit-line-clamp:2; }
.timeline-content > p { display:-webkit-box; margin:8px 0 0; color:var(--archive-text-secondary,rgba(17,24,39,.72)); font-size:11px; line-height:1.55; white-space:normal; -webkit-box-orient:vertical; -webkit-line-clamp:2; }
.timeline-tags { display:flex; flex-wrap:wrap; gap:6px; max-height:44px; margin:3px 0 0; overflow:hidden; }
.timeline-tags .timeline-tag { max-width:112px; padding:4px 8px; overflow:hidden; background:rgba(124,108,242,.13); border:1px solid rgba(124,108,242,.2); border-radius:999px; color:#312e81; font-size:10px; line-height:1; text-overflow:ellipsis; white-space:nowrap; }
.timeline-tags .timeline-tag-more { flex:0 0 auto; background:rgba(255,255,255,.48); }
.category-pill { display:inline-block; flex:0 1 auto; max-width:110px; padding:3px 8px; overflow:hidden; background:rgba(124,108,242,.12); color:#443a91; font-size:9px; text-overflow:ellipsis; white-space:nowrap; }
:global(html[data-theme='dark']) .timeline-card { background:rgba(242,243,249,.82); border-color:rgba(255,255,255,.58); }
:global(html[data-theme='dark']) .timeline-content h2 { color:#111827; }
:global(html[data-theme='dark']) .timeline-content time,
:global(html[data-theme='dark']) .timeline-content > p { color:rgba(17,24,39,.72); }
@media(max-width:820px){.timeline-item{min-height:310px}.timeline-item.is-left{padding-right:calc(50% + 24px)}.timeline-item.is-right{padding-left:calc(50% + 24px)}.timeline-cover{height:150px}}
@media(max-width:700px){.timeline-item,.timeline-item.is-left,.timeline-item.is-right{justify-content:flex-start;min-height:0;margin-bottom:16px;padding:0 0 0 36px}.timeline-node{top:36px;left:12px}.timeline-card,.is-right .timeline-card,.is-left .timeline-card{width:100%}.timeline-cover{height:clamp(150px,45vw,210px)}.timeline-content{min-height:138px}}
</style>
