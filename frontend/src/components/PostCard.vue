<template>
  <article class='article-card'>
    <div class='article-content'>
      <router-link :to='postLink' class='article-content__link'>
        <header class='article-header'>
          <span class='article-category' :class='categoryClass'>{{ post.categoryName || t('article.uncategorized') }}</span>
        </header>
        <div class='article-main'>
          <h2 class='article-title'>{{ post.title }}</h2>
          <p class='article-summary'>{{ post.summary || t('article.noSummary') }}</p>
          <div v-if='visibleTags.length' class='article-tags' :aria-label='t("adminTag.selectLabel")'>
            <span v-for='tag in visibleTags' :key='tag.id' class='article-tag'>#{{ tag.name }}</span>
            <span v-if='hiddenTagCount > 0' class='article-tag more'>+{{ hiddenTagCount }}</span>
          </div>
        </div>
        <footer class='article-footer'>
          <div class='article-stats' :aria-label='t("article.statistics")'>
            <span class='article-stat' :aria-label='t("article.views")'><i class='ti ti-eye' aria-hidden='true'></i>{{ post.viewCount ?? 0 }}</span>
            <span class='article-stat' :aria-label='t("article.comments")'><i class='ti ti-message' aria-hidden='true'></i>{{ post.commentCount ?? 0 }}</span>
          </div>
          <div class='article-times'>
            <time :datetime='post.createTime || ``'>{{ t('article.publishedAt') }}: {{ formatDateTime(post.createTime, locale) }}</time>
            <time :datetime='post.updateTime || ``'>{{ t('article.updatedAt') }}: {{ formatDateTime(post.updateTime, locale) }}</time>
          </div>
        </footer>
      </router-link>
    </div>
    <div class='article-cover'>
      <router-link :to='postLink' class='article-cover__link' :aria-label='post.title'>
        <img :src='coverSource' :alt='post.title' class='article-cover__image' loading='lazy' decoding='async' @error='useFallbackCover' />
      </router-link>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { formatDateTime } from '@/utils/date.js'

const DEFAULT_COVER = '/images/hero.jpg'
const { locale, t } = useI18n()
const props = defineProps({
  post: { type: Object, required: true }
})
const postLink = computed(() => `/post/${props.post.id}`)
const coverSource = computed(() => props.post.coverImage?.trim() || DEFAULT_COVER)
const visibleTags = computed(() => Array.isArray(props.post.tags) ? props.post.tags.slice(0, 4) : [])
const hiddenTagCount = computed(() => Math.max((props.post.tags?.length || 0) - visibleTags.value.length, 0))
const categoryClass = computed(() => ({
  tech: props.post.categoryName === '\u6280\u672f',
  life: props.post.categoryName === '\u968f\u7b14',
  read: props.post.categoryName === '\u9605\u8bfb'
}))
const useFallbackCover = event => {
  if (!event.target.src.endsWith(DEFAULT_COVER)) event.target.src = DEFAULT_COVER
}
</script>

<style scoped>
.article-card {
  display: flex;
  width: 100%;
  min-width: 0;
  height: clamp(320px, 32vw, 380px);
  overflow: hidden;
  background: var(--article-card-bg);
  border: 1px solid var(--article-card-border);
  border-radius: var(--r-lg);
  box-shadow: 0 6px 20px var(--shadow-color);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition: transform .25s ease, box-shadow .25s ease, border-color .25s ease;
}
.article-card:nth-of-type(even) { flex-direction: row-reverse; }
.article-card:hover,
.article-card:focus-within {
  transform: translateY(-3px);
  border-color: var(--border-h);
  box-shadow: 0 12px 30px var(--shadow-color);
}
.article-content {
  display: flex;
  flex-direction: column;
  flex: 1 1 59%;
  min-width: 0;
  overflow: hidden;
}
.article-content__link {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  padding: 1.5rem 1.6rem;
  overflow: hidden;
  outline: none;
}
.article-header {
  display: flex;
  align-items: center;
  flex: 0 0 auto;
  margin-bottom: 1rem;
}
.article-category {
  flex: 0 0 auto;
  padding: 2px 10px;
  border-radius: 999px;
  background: var(--purple-50);
  color: var(--purple-600);
  font-size: 11px;
  font-weight: 600;
}
.article-category.life { background: var(--teal-50); color: var(--teal-600); }
.article-category.read { background: var(--pink-50); color: var(--pink-600); }
.article-main {
  flex: 1 1 auto;
  min-height: 0;
  overflow: hidden;
}
.article-title {
  display: -webkit-box;
  overflow: hidden;
  margin-bottom: .65rem;
  color: var(--text-1);
  font-size: clamp(18px, 2vw, 22px);
  font-weight: 600;
  line-height: 1.4;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  transition: color .2s ease;
}
.article-content__link:hover .article-title,
.article-content__link:focus-visible .article-title { color: var(--accent-color); }
.article-summary {
  display: -webkit-box;
  overflow: hidden;
  color: var(--text-2);
  font-size: 13px;
  line-height: 1.8;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 4;
}
.article-tags { display: flex; flex-wrap: wrap; gap: 5px; margin-top: .75rem; max-height: 48px; overflow: hidden; }
.article-tag { max-width: 150px; padding: 2px 8px; overflow: hidden; background: rgba(212,83,126,.1); border: 1px solid rgba(212,83,126,.2); border-radius: 999px; color: var(--pink-600); font-size: 10px; text-overflow: ellipsis; white-space: nowrap; }
.article-tag.more { background: var(--purple-50); border-color: var(--purple-100); color: var(--purple-600); }
.article-footer {
  margin-top: auto;
  padding-top: 1rem;
  border-top: 1px solid var(--article-card-border);
  color: var(--text-3);
}
.article-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: .65rem;
}
.article-stat {
  display: inline-flex;
  align-items: center;
  gap: .3rem;
  font-size: 12px;
}
.article-stat i { font-size: 14px; }
.article-times {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: .25rem 1rem;
  font-size: 10px;
}
.article-cover {
  flex: 0 0 41%;
  min-width: 0;
  overflow: hidden;
}
.article-cover__link { display: block; width: 100%; height: 100%; outline: none; }
.article-cover__image {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 25%;
  transition: transform .4s ease;
}
.article-card:hover .article-cover__image,
.article-cover__link:focus-visible .article-cover__image { transform: scale(1.03); }

@media (max-width: 1199px) {
  .article-content__link { padding: 1.25rem 1.35rem; }
}
@media (max-width: 767px) {
  .article-card,
  .article-card:nth-of-type(even) {
    flex-direction: column-reverse;
    height: auto;
  }
  .article-cover {
    flex: none;
    width: 100%;
    height: auto;
    aspect-ratio: 16 / 9;
  }
  .article-content { width: 100%; }
  .article-content__link { padding: 1.2rem 1.25rem; }
  .article-summary { -webkit-line-clamp: 3; }
  .article-times { flex-direction: column; }
}
@media (max-width: 479px) {
  .article-content__link { padding: 1rem; }
  .article-title { font-size: 17px; }
  .article-summary { font-size: 12px; }
  .article-stats { gap: .75rem; }
}
@media (prefers-reduced-motion: reduce) {
  .article-card,
  .article-cover__image { transition: none; }
}
</style>
