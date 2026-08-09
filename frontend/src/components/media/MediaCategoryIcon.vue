<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: { type: String, required: true },
  size: { type: String, default: 'default' }
})

const iconSources = Object.freeze({
  game: '/images/media-categories/game.png',
  anime: '/images/media-categories/anime.png',
  movie: '/images/media-categories/movie.png',
  book: '/images/media-categories/book.png'
})

const iconSource = computed(() => iconSources[props.type] || iconSources.anime)
</script>

<template>
  <span class="category-icon" :class="[`is-${type}`, `is-${size}`]" aria-hidden="true">
    <img :src="iconSource" alt="" draggable="false" decoding="async" />
  </span>
</template>

<style scoped>
.category-icon {
  display:block;
  width:54px;
  height:54px;
  flex:0 0 auto;
  border-radius:18px;
  filter:drop-shadow(0 7px 12px rgba(57,48,94,.13));
  transition:filter .24s ease,transform .24s ease;
}
.category-icon img { display:block; width:100%; height:100%; border-radius:inherit; object-fit:contain; object-position:center; user-select:none; }
.is-small { width:38px; height:38px; border-radius:13px; }
@media(prefers-reduced-motion:reduce){.category-icon{transition:none}}
</style>
