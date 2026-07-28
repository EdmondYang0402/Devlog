<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { formatDateTime } from '@/utils/date.js'

const DEFAULT_COVER = '/images/hero.jpg'
const { locale, t } = useI18n()
const props = defineProps({ article: { type: Object, required: true } })
const normalizeCover = value => typeof value === 'string' ? value.trim() : ''
const coverSource = computed(() => normalizeCover(props.article.coverImage) || normalizeCover(props.article.coverUrl) || DEFAULT_COVER)
const useFallbackCover = event => {
  if (!event.target.src.endsWith(DEFAULT_COVER)) event.target.src = DEFAULT_COVER
}
const visibleTags = computed(() => Array.isArray(props.article.tags) ? props.article.tags.filter(Boolean).slice(0, 2) : [])
const hiddenTagCount = computed(() => Math.max((props.article.tags?.length || 0) - 2, 0))
</script>

<template>
  <article class="grid-card">
    <router-link :to="`/post/${article.id}`" class="grid-card-link">
      <div class="grid-cover">
        <img :src="coverSource" :alt="article.title" loading="lazy" decoding="async" @error="useFallbackCover" />
        <span class="category-pill">{{ article.categoryName || t('article.uncategorized') }}</span>
      </div>
      <div class="grid-content">
        <time :datetime="article.createTime">{{ formatDateTime(article.createTime, locale) }}</time>
        <h2>{{ article.title }}</h2>
        <p>{{ article.summary || t('article.noSummary') }}</p>
        <div v-if="visibleTags.length" class="grid-tags">
          <span v-for="tag in visibleTags" :key="tag.id">#{{ tag.name }}</span>
          <span v-if="hiddenTagCount" class="more">+{{ hiddenTagCount }}</span>
        </div>
        <footer>
          <span><i class="ti ti-eye" aria-hidden="true"></i>{{ article.viewCount }}</span>
          <span><i class="ti ti-message-circle" aria-hidden="true"></i>{{ article.commentCount }}</span>
          <i class="ti ti-arrow-up-right card-arrow" aria-hidden="true"></i>
        </footer>
      </div>
    </router-link>
  </article>
</template>

<style scoped>
.grid-card { min-width:0; overflow:hidden; background:rgba(255,255,255,.22); border:1px solid rgba(255,255,255,.28); border-radius:16px; box-shadow:0 10px 28px rgba(20,18,34,.12); backdrop-filter:blur(14px) saturate(110%); -webkit-backdrop-filter:blur(14px) saturate(110%); transition:transform .25s ease,background .25s ease,border-color .25s ease; }
.grid-card:hover, .grid-card:focus-within { transform:translateY(-4px); border-color:var(--border-h); box-shadow:0 12px 30px rgba(20,18,34,.14); }
.grid-card-link { display:flex; flex-direction:column; min-height:292px; height:100%; outline:none; }
.grid-cover { position:relative; height:140px; flex:0 0 140px; overflow:hidden; background:var(--purple-50); }
.grid-cover::after { position: absolute; inset: auto 0 0; height: 45%; content: ''; background: linear-gradient(transparent, rgba(14,16,30,.38)); pointer-events: none; }
.grid-cover img { display: block; width: 100%; height: 100%; object-fit: cover; transition: transform .4s ease; }
.grid-card:hover .grid-cover img { transform:scale(1.025); }
.category-pill { position:absolute; z-index:1; right:9px; bottom:9px; max-width:calc(100% - 18px); padding:3px 8px; overflow:hidden; background:rgba(255,255,255,.74); border:1px solid rgba(255,255,255,.58); border-radius:999px; color:#534AB7; font-size:9px; font-weight:650; text-overflow:ellipsis; white-space:nowrap; backdrop-filter:blur(7px); }
.grid-content { display:flex; flex:1; flex-direction:column; min-width:0; padding:13px 14px 12px; }
.grid-content time { color:var(--pink-600); font-size:10px; font-weight:600; }
.grid-content h2 { display:-webkit-box; margin:5px 0 4px; overflow:hidden; color:var(--text-1); font-size:16px; line-height:1.4; -webkit-box-orient:vertical; -webkit-line-clamp:2; }
.grid-content p { display:-webkit-box; margin:0; overflow:hidden; color:var(--text-2); font-size:11px; line-height:1.55; -webkit-box-orient:vertical; -webkit-line-clamp:2; }
.grid-tags { display:flex; flex-wrap:wrap; gap:4px; margin-top:7px; max-height:19px; overflow:hidden; }
.grid-tags span { max-width: 100px; padding: 1px 7px; overflow: hidden; background: rgba(212,83,126,.1); border-radius: 999px; color: var(--pink-600); font-size: 9px; text-overflow: ellipsis; white-space: nowrap; }
.grid-tags .more { background: var(--purple-50); color: var(--purple-600); }
.grid-content footer { display:flex; align-items:center; gap:10px; margin-top:auto; padding-top:9px; color:var(--text-3); font-size:9px; }
.grid-content footer span { display: inline-flex; align-items: center; gap: 4px; }
.card-arrow { margin-left: auto; color: var(--purple-400); font-size: 17px; transition: transform .2s ease; }
.grid-card:hover .card-arrow { transform: translate(2px, -2px); }
@media(max-width:760px){.grid-card-link{min-height:286px}.grid-cover{height:134px;flex-basis:134px}}
@media(max-width:520px){.grid-card{border-radius:15px}.grid-card-link{min-height:0}.grid-cover{height:150px;flex-basis:150px}.grid-content{padding:13px 14px}}
:global(html[data-theme='dark']) .grid-card{background:rgba(24,24,34,.38);border-color:rgba(255,255,255,.12)}
.grid-card { background:rgba(255,255,255,.58); border-color:rgba(255,255,255,.6); }
.grid-content time { color:var(--archive-text-secondary,rgba(17,24,39,.72)); }
.grid-content h2 { color:var(--archive-text-primary,#111827); }
.grid-content p,
.grid-content footer { color:var(--archive-text-secondary,rgba(17,24,39,.72)); }
.grid-tags span { background:rgba(124,108,242,.1); color:#5146a8; }
.grid-tags .more { background:rgba(124,108,242,.14); color:#443a91; }
:global(html[data-theme='dark']) .grid-card { background:rgba(242,243,249,.82); border-color:rgba(255,255,255,.58); }
:global(html[data-theme='dark']) .grid-content h2 { color:#111827; }
:global(html[data-theme='dark']) .grid-content time,
:global(html[data-theme='dark']) .grid-content p,
:global(html[data-theme='dark']) .grid-content footer { color:rgba(17,24,39,.72); }
@media (prefers-reduced-motion: reduce) { .grid-card, .grid-cover img, .card-arrow { transition: none; } }
</style>
