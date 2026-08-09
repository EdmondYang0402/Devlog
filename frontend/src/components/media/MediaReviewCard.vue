<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const DEFAULT_COVER = '/images/hero.jpg'
const props = defineProps({
  item: { type: Object, required: true },
  coverVariant: { type: String, default: 'landscape' }
})
const { t } = useI18n()
const coverSource = computed(() => props.item.coverUrl?.trim() || DEFAULT_COVER)
const useFallbackCover = event => {
  if (!event.target.src.endsWith(DEFAULT_COVER)) event.target.src = DEFAULT_COVER
}
</script>

<template>
  <router-link :to="`/media/${item.id}`" class="media-card" :class="`is-${coverVariant}`">
    <div class="cover-wrap">
      <img
        :src="coverSource"
        :alt="item.title"
        class="cover"
        loading="lazy"
        decoding="async"
        @error="useFallbackCover"
      />
    </div>
    <div class="card-body">
      <h2>{{ item.title }}</h2>
      <p class="subtitle">{{ item.shortReview || t('media.noShortReview') }}</p>
    </div>
  </router-link>
</template>

<style scoped>
.media-card { --media-card-background:rgba(245,245,250,.5); --media-card-background-hover:rgba(245,245,250,.6); display:flex; min-width:0; height:100%; flex-direction:column; overflow:hidden; border:1px solid rgba(255,255,255,.34); border-radius:18px; background:var(--media-card-background); box-shadow:0 8px 22px rgba(30,24,50,.09); -webkit-backdrop-filter:blur(10px) saturate(108%); backdrop-filter:blur(10px) saturate(108%); transition:transform .22s ease,border-color .22s ease,background-color .22s ease,box-shadow .22s ease; }
.media-card:hover,.media-card:focus-visible { transform:translateY(-3px) scale(1.008); border-color:rgba(255,255,255,.52); background:var(--media-card-background-hover); box-shadow:0 12px 28px rgba(30,24,50,.12); outline:none; }
.cover-wrap { position:relative; aspect-ratio:3/2; overflow:hidden; background:var(--purple-50); }
.media-card.is-book .cover-wrap { aspect-ratio:3/4; }
.cover { display:block; width:100%; height:100%; opacity:1; object-fit:cover; object-position:center; transition:transform .32s ease; }
.media-card:hover .cover,.media-card:focus-visible .cover { transform:scale(1.025); }
.card-body { display:flex; min-height:76px; flex:1; flex-direction:column; gap:5px; padding:12px 14px 14px; }
h2 { display:-webkit-box; overflow:hidden; color:#171625; font-size:15px; font-weight:650; line-height:1.42; -webkit-box-orient:vertical; -webkit-line-clamp:2; }
.subtitle { display:-webkit-box; overflow:hidden; color:rgba(30,28,43,.68); font-size:11px; line-height:1.55; -webkit-box-orient:vertical; -webkit-line-clamp:2; }
:global(html[data-theme='dark']) h2 { color:#171625; }
:global(html[data-theme='dark']) .subtitle { color:rgba(30,28,43,.7); }
:global(html[data-theme='dark']) .media-card { --media-card-background:rgba(235,236,244,.62); --media-card-background-hover:rgba(235,236,244,.68); }
@media(max-width:560px){.card-body{min-height:72px;padding:12px 13px 13px}h2{font-size:16px}.subtitle{font-size:12px}}
@media(prefers-reduced-motion:reduce){.media-card,.cover{transition:none}.media-card:hover,.media-card:focus-visible{transform:none}}
</style>
