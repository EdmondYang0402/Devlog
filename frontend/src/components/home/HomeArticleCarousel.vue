<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import HomeFeaturedPost from '@/components/home/HomeFeaturedPost.vue'
import HomeHorizontalPost from '@/components/home/HomeHorizontalPost.vue'
import HomeSmallPost from '@/components/home/HomeSmallPost.vue'
import HomeThemeCard from '@/components/home/HomeThemeCard.vue'

const props = defineProps({
  articles: { type: Array, default: () => [] }
})

const ARTICLE_CAROUSEL_INTERVAL = 8000
const currentArticleGroupIndex = ref(0)
const isPointerInside = ref(false)
let articleCarouselTimer = null

const articleGroups = computed(() => {
  const groups = []
  for (let index = 0; index < props.articles.length; index += 3) {
    groups.push(props.articles.slice(index, index + 3))
  }
  return groups
})

const currentArticleGroup = computed(() => (
  articleGroups.value[currentArticleGroupIndex.value] ?? []
))
const mainArticle = computed(() => currentArticleGroup.value[0] ?? null)
const horizontalArticle = computed(() => currentArticleGroup.value[1] ?? null)
const smallArticle = computed(() => currentArticleGroup.value[2] ?? null)
const hasMultipleGroups = computed(() => articleGroups.value.length > 1)

function stopArticleCarousel() {
  if (articleCarouselTimer !== null) {
    window.clearInterval(articleCarouselTimer)
    articleCarouselTimer = null
  }
}

function startArticleCarousel() {
  stopArticleCarousel()
  if (!hasMultipleGroups.value || isPointerInside.value) return

  articleCarouselTimer = window.setInterval(() => {
    currentArticleGroupIndex.value =
      (currentArticleGroupIndex.value + 1) % articleGroups.value.length
  }, ARTICLE_CAROUSEL_INTERVAL)
}

function changeArticleGroup(index) {
  if (!hasMultipleGroups.value || index === currentArticleGroupIndex.value) return
  currentArticleGroupIndex.value = index
  startArticleCarousel()
}

function nextArticleGroup() {
  if (!hasMultipleGroups.value) return
  changeArticleGroup((currentArticleGroupIndex.value + 1) % articleGroups.value.length)
}

function previousArticleGroup() {
  if (!hasMultipleGroups.value) return
  changeArticleGroup(
    (currentArticleGroupIndex.value - 1 + articleGroups.value.length) % articleGroups.value.length
  )
}

function pauseArticleCarousel() {
  isPointerInside.value = true
  stopArticleCarousel()
}

function resumeArticleCarousel() {
  isPointerInside.value = false
  startArticleCarousel()
}

watch(articleGroups, groups => {
  if (currentArticleGroupIndex.value >= groups.length) currentArticleGroupIndex.value = 0
  startArticleCarousel()
}, { immediate: true })

onBeforeUnmount(stopArticleCarousel)
</script>

<template>
  <section
    class="article-carousel"
    aria-label="首页文章轮播"
    @mouseenter="pauseArticleCarousel"
    @mouseleave="resumeArticleCarousel"
  >
    <div class="article-carousel-grid">
      <Transition name="article-group" mode="out-in">
        <div :key="currentArticleGroupIndex" class="article-group-panel">
          <template v-if="currentArticleGroup.length">
            <HomeFeaturedPost v-if="mainArticle" :post="mainArticle" />
            <HomeHorizontalPost v-if="horizontalArticle" :post="horizontalArticle" />
            <HomeSmallPost v-if="smallArticle" :post="smallArticle" />
          </template>
          <div v-else class="article-empty glass-panel">暂无已发布文章</div>
        </div>
      </Transition>

      <HomeThemeCard />
    </div>

    <template v-if="hasMultipleGroups">
      <button
        class="article-carousel-arrow article-carousel-arrow--previous"
        type="button"
        aria-label="上一组文章"
        @click="previousArticleGroup"
      ><i class="ti ti-chevron-left"></i></button>
      <button
        class="article-carousel-arrow article-carousel-arrow--next"
        type="button"
        aria-label="下一组文章"
        @click="nextArticleGroup"
      ><i class="ti ti-chevron-right"></i></button>

      <div class="article-carousel-dots" aria-label="选择文章组">
        <button
          v-for="(_group, index) in articleGroups"
          :key="index"
          type="button"
          :class="{ active: index === currentArticleGroupIndex }"
          :aria-label="`显示第 ${index + 1} 组文章`"
          :aria-current="index === currentArticleGroupIndex ? 'true' : undefined"
          @click="changeArticleGroup(index)"
        ></button>
      </div>
    </template>
  </section>
</template>
