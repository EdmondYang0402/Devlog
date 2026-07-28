<script setup>
import { onMounted, ref } from 'vue'
import { articleListService } from '@/api/article.js'
import { profileStatisticsService } from '@/api/statistics.js'
import { siteProfileService } from '@/api/site.js'
import HomeSearchBar from '@/components/home/HomeSearchBar.vue'
import HomeProfileCard from '@/components/home/HomeProfileCard.vue'
import HomeStatusCard from '@/components/home/HomeStatusCard.vue'
import HomeQuoteBar from '@/components/home/HomeQuoteBar.vue'
import HomeArticleCarousel from '@/components/home/HomeArticleCarousel.vue'
import HomeBottomBar from '@/components/home/HomeBottomBar.vue'
import '@/assets/css/home-dashboard.css'

// 默认值只用于接口失败时保持页面可用，成功响应始终覆盖这些降级内容。
const siteProfile = ref({
  siteTitle: "Hathaway's Blog",
  heroSubtitle: '',
  heroKeywords: [],
  authorName: 'Hathaway',
  authorBio: '',
  avatarUrl: '',
  profileBackgroundUrl: '',
  announcement: '',
  githubUrl: '',
  giteeUrl: ''
})
const stats = ref({ posts: 0, categories: '—', comments: 0 })
const posts = ref([])
const ARTICLE_PAGE_SIZE = 30

const normalizePost = item => ({
  id: item.id,
  title: item.title,
  summary: item.summary || '',
  categoryName: item.categoryName || item.category || '',
  createTime: item.createTime || '',
  date: item.createTime ? item.createTime.split('T')[0] : '',
  viewCount: Number(item.viewCount ?? 0),
  commentCount: Number(item.commentCount ?? 0),
  tags: Array.isArray(item.tags) ? item.tags : [],
  coverUrl: item.coverImage || item.coverUrl || ''
})

const fetchSiteProfile = async () => {
  try {
    const response = await siteProfileService()
    siteProfile.value = { ...siteProfile.value, ...(response?.data || {}) }
    if (!Array.isArray(siteProfile.value.heroKeywords)) siteProfile.value.heroKeywords = []
  } catch (error) {
    console.error('获取站点资料失败：', error)
  }
}

const fetchStatistics = async () => {
  try {
    const data = (await profileStatisticsService())?.data || {}
    stats.value = {
      posts: Number(data.articleCount ?? 0),
      categories: Number(data.categoryCount ?? 0),
      comments: Number(data.commentCount ?? 0)
    }
  } catch (error) {
    console.error('获取首页统计数据失败：', error)
  }
}

const fetchArticles = async () => {
  try {
    const loadedArticles = []
    let page = 1
    let total = 0

    do {
      const response = await articleListService({ page, size: ARTICLE_PAGE_SIZE })
      const payload = response?.data?.data || response?.data || response || {}
      const records = Array.isArray(payload.records) ? payload.records : []
      loadedArticles.push(...records)
      total = Number(payload.total ?? loadedArticles.length)
      if (records.length < ARTICLE_PAGE_SIZE) break
      page += 1
    } while (loadedArticles.length < total)

    posts.value = loadedArticles.map(normalizePost)
  } catch (error) {
    console.error('获取首页精选文章失败：', error)
    posts.value = []
  }
}

onMounted(() => {
  // 三个接口相互独立，单个请求失败不会阻止其他首页卡片显示。
  fetchSiteProfile()
  fetchStatistics()
  fetchArticles()
})
</script>

<template>
  <main class="home-stage">
    <section class="home-dashboard" :aria-label="siteProfile.siteTitle">
      <HomeSearchBar />
      <div class="home-top-row">
        <HomeProfileCard :profile="siteProfile" :stats="stats" />
        <HomeStatusCard :profile="siteProfile" />
      </div>
      <HomeQuoteBar :quote="siteProfile.heroSubtitle" />
      <HomeArticleCarousel :articles="posts" />
      <HomeBottomBar :site-title="siteProfile.siteTitle" :article-count="stats.posts" />
    </section>
  </main>
</template>
