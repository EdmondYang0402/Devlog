<template>
  <div class="wrap">

    <!-- Article banner cover -->
    <div class="banner" :style="bannerBg">
      <img v-if="post.coverUrl" :src="post.coverUrl" :alt="post.title" class="banner-img" />
      <div class="banner-overlay"></div>
      <div class="banner-info">
        <div class="banner-meta">
          <span class="bcat" :class="catCls">{{ post.category }}</span>
          <span class="bdate">
            {{ t('article.publishedAt') }}: {{ formatDateTime(post.createTime, locale) }}
            · {{ t('article.updatedAt') }}: {{ formatDateTime(post.updateTime, locale) }}
          </span>
        </div>
        <h1 class="banner-title">{{ post.title }}</h1>
        <div v-if="post.tags.length" class="banner-tags" :aria-label="t('adminTag.selectLabel')">
          <span v-for="tag in post.tags" :key="tag.id" class="banner-tag">#{{ tag.name }}</span>
        </div>
        <div class="banner-stats">
          <span><i class="ti ti-file-text" aria-hidden="true"></i> {{ post.wordCount ?? 0 }} {{ t('article.words') }}</span>
          <span><i class="ti ti-eye" aria-hidden="true"></i> {{ post.views }} {{ t('article.views') }}</span>
          <span><i class="ti ti-message" aria-hidden="true"></i> {{ post.comments ?? 0 }} {{ t('article.comments') }}</span>
          <span><i class="ti ti-heart" aria-hidden="true"></i> {{ post.likes ?? 0 }}</span>
        </div>
      </div>
    </div>

    <div class="layout">
      <!-- Main article -->
      <main class="main-column">
        <article class="article glass-panel glass-panel--reading">
          <div class="article-body markdown-body" v-html="rendered" @click="handleCodeCopy"></div>
          <div class="article-footer">
            <router-link to="/" class="back">← {{ t('article.backHome') }}</router-link>
          </div>
        </article>
        <CommentSection
          v-if="post.id"
          class="reading-comments"
          :article-id="post.id"
          @count-change="post.comments = $event"
        />
      </main>

      <!-- Sidebar -->
      <aside class="side">
        <!-- Author card -->
        <div class="scard glass-panel glass-panel--reading">
          <div class="author">
            <div class="a-avatar">🌸</div>
            <div class="a-name">Hathaway</div>
            <div class="a-bio">{{ t('about.role') }}</div>
            <div class="a-stats">
              <div class="as"><strong>{{ profileStats.articleCount }}</strong><span>{{ t('home.articles') }}</span></div>
              <div class="as"><strong>{{ profileStats.categoryCount }}</strong><span>{{ t('home.categories') }}</span></div>
              <div class="as"><strong>{{ profileStats.commentCount }}</strong><span>{{ t('home.comments') }}</span></div>
            </div>
          </div>
        </div>

        <!-- TOC -->
        <div class="scard glass-panel glass-panel--reading">
          <div class="stitle"><i class="ti ti-list" aria-hidden="true"></i> {{ t('article.toc') }}</div>
          <div v-if="!toc.length" class="toc-empty">{{ t('article.noToc') }}</div>
          <a v-for="h in toc" :key="h.id" :href="'#'+h.id"
             class="toc-item" :class="'toc-h'+h.level">{{ h.text }}</a>
        </div>

        <!-- Related -->
        <div class="scard glass-panel glass-panel--reading">
          <div class="stitle"><i class="ti ti-books" aria-hidden="true"></i> {{ t('article.related') }}</div>
          <div class="rec-empty">{{ t('article.noRelated') }}</div>
        </div>
      </aside>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
// 🌟 引入你已有的详情接口
import { articleDetailService } from '@/api/article'
import { profileStatisticsService } from '@/api/statistics'
import CommentSection from '@/components/CommentSection.vue'
import { formatDateTime } from '@/utils/date.js'
import { handleCodeCopy, renderMarkdown } from '@/utils/markdown.js'

const route = useRoute()
const { locale, t } = useI18n()

// 1. 初始结构设置为空，等待后端填充
const post = ref({
  id: null,
  category: '',
  createTime: '',
  updateTime: '',
  title: '',
  tags: [],
  views: 0,
  comments: 0,
  likes: 0,
  wordCount: 0,
  coverUrl: '',
  content: ''
})

const toc = ref([])
const profileStats = ref({ articleCount: 0, categoryCount: '—', commentCount: 0 })

const fetchStatistics = async () => {
  try {
    const res = await profileStatisticsService()
    const data = res?.data || {}
    profileStats.value = {
      articleCount: Number(data.articleCount ?? 0),
      categoryCount: data.categoryCount ?? '—',
      commentCount: Number(data.commentCount ?? 0)
    }
  } catch (error) {
    console.error('获取文章侧栏统计数据失败：', error)
  }
}

// 2. 核心对接函数：请求后端并动态解包
const fetchPostDetail = async () => {
  try {
    // 从当前路由中动态获取文章 ID
    const id = route.params.id
    const res = await articleDetailService(id)
    
    // 解析后端 Result<PageResult<ArticleDetailVO>> 或 Result<ArticleDetailVO> 的三层结构
    const data = res?.data?.data || res?.data || null
    
    if (data) {
      post.value = {
        id: data.id,
        title: data.title,
        content: data.content || '', // 后端返回的 Markdown 文本
        category: data.categoryName || data.category || t('article.uncategorized'),
        createTime: data.createTime || '',
        updateTime: data.updateTime || '',
        views: data.viewCount || 0,
        coverUrl: data.coverImage || '',
        
        // 以下字段若后端暂未提供，则先填充默认值
        tags: Array.isArray(data.tags) ? data.tags : [],
        wordCount: data.content ? data.content.length : 0, 
        comments: 0,
        likes: 0
      }
      
      // 3. 数据渲染到 DOM 后，动态生成文章目录 (TOC)
      await nextTick()
      toc.value = [] // 清开上一次的缓存
      document.querySelectorAll('.article-body h2, .article-body h3').forEach((h, i) => {
        const idStr = 'h-' + i
        h.id = idStr
        toc.value.push({ id: idStr, text: h.textContent, level: parseInt(h.tagName[1]) })
      })
    }
  } catch (error) {
    console.error('获取文章详情失败：', error)
  }
}

onMounted(() => {
  fetchPostDetail()
  fetchStatistics()
})

const FALLBACK_BG = {
  '技术': 'linear-gradient(135deg,#1a0e3a 0%,#3C3489 100%)',
  '随笔': 'linear-gradient(135deg,#04342C 0%,#1D9E75 100%)',
  '阅读': 'linear-gradient(135deg,#4B1528 0%,#D4537E 100%)',
}
const bannerBg = computed(() =>
  post.value.coverUrl ? {} : { background: FALLBACK_BG[post.value.category] || FALLBACK_BG['技术'] }
)

const catCls = computed(() => ({
  tech: post.value.category === '技术',
  life: post.value.category === '随笔',
  read: post.value.category === '阅读',
}))

const rendered = computed(() => renderMarkdown(post.value.content))
</script>

<style scoped>
.wrap { flex: 1; padding:28px 0 56px; }

/* Banner */
.banner {
  position: relative; width:min(1120px,calc(100% - 40px)); min-height: 280px; overflow: hidden;
  background: #0d0d1a;
  display:flex; align-items:flex-end; margin:0 auto; border:1px solid var(--glass-border); border-radius:24px; box-shadow:var(--glass-shadow);
}
.banner-img {
  position: absolute; inset: 0;
  width: 100%; height: 100%; object-fit: cover; opacity: .5;
}
.banner-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,.1) 0%, rgba(0,0,0,.65) 100%);
}
.banner-info {
  position: relative; z-index: 2;
  padding: 2rem 2rem 1.5rem; width: 100%;
  max-width: 960px; margin: 0 auto;
}
.banner-meta { display: flex; align-items: center; flex-wrap: wrap; gap: 10px; margin-bottom: 10px; }
.bcat { font-size: 11px; padding: 2px 10px; border-radius: 12px; font-weight: 500; }
.bcat.tech { background: rgba(127,119,221,.35); color: #AFA9EC; border: .5px solid rgba(175,169,236,.4); }
.bcat.life { background: rgba(29,158,117,.3);  color: #9FE1CB; border: .5px solid rgba(93,202,165,.4); }
.bcat.read { background: rgba(212,83,126,.3);  color: #ED93B1; border: .5px solid rgba(212,83,126,.4); }
.bdate { font-size: 12px; color: rgba(255,255,255,.6); }
.banner-title { font-size: 22px; font-weight: 500; color: #fff; line-height: 1.4; margin-bottom: 12px; }
.banner-tags { display: flex; flex-wrap: wrap; gap: 6px; margin: -3px 0 12px; }
.banner-tag { max-width: 180px; padding: 3px 9px; overflow: hidden; background: rgba(255,255,255,.11); border: .5px solid rgba(237,147,177,.55); border-radius: 999px; color: #ffd3e1; font-size: 10px; text-overflow: ellipsis; white-space: nowrap; backdrop-filter: blur(6px); }
.banner-stats { display: flex; flex-wrap: wrap; gap: 6px 14px; font-size: 12px; color: rgba(255,255,255,.6); }
.banner-stats i { font-size: 13px; vertical-align: -1px; margin-right: 3px; }

/* Layout */
.layout {
  display: grid; grid-template-columns: 1fr 210px;
  gap: 1.2rem; max-width: 1100px;
  margin: 1.2rem auto; padding: 0 1.5rem;
}

/* Article */
.main-column { display: flex; flex-direction: column; gap: 1.2rem; min-width: 0; }
.article {
  background: var(--glass-bg-reading);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
  padding: 1.8rem 2rem;
}
.article-body { line-height: 1.85; color: var(--text-1); }
.article-body :deep(h2) { font-size: 18px; font-weight: 500; margin: 1.8rem 0 .8rem; color: var(--purple-800); }
.article-body :deep(h3) { font-size: 15px; font-weight: 500; margin: 1.4rem 0 .6rem; }
.article-body :deep(p)  { margin-bottom: 1rem; font-size: 14px; }
.article-body :deep(ul), .article-body :deep(ol) { margin: .8rem 0 1rem 1.4rem; font-size: 14px; }
.article-body :deep(li) { margin-bottom: 4px; }
.article-body :deep(blockquote) {
  border-left: 3px solid var(--purple-200);
  padding: .5rem 1rem; margin: 1rem 0;
  background: var(--purple-50); color: var(--text-2);
  border-radius: 0 var(--r-md) var(--r-md) 0; font-size: 14px;
}
.article-footer { margin-top: 2rem; padding-top: 1.2rem; border-top: .5px solid var(--border); }
.back { font-size: 13px; color: var(--purple-400); }
.back:hover { opacity: .7; }

/* Sidebar */
.side { display: flex; flex-direction: column; gap: .8rem; position: sticky; top: calc(var(--nav-h) + 1rem); align-self: start; }
.scard { background:var(--glass-bg-reading); border:1px solid var(--glass-border); border-radius:18px; padding:.85rem 1rem; }
.reading-comments { background:var(--glass-bg-reading); border-color:var(--glass-border); backdrop-filter:blur(18px); -webkit-backdrop-filter:blur(18px); }
.stitle { font-size: 11px; font-weight: 500; color: var(--text-3); text-transform: uppercase; letter-spacing: .07em; margin-bottom: .7rem; }
.stitle i { font-size: 14px; color: var(--purple-400); vertical-align: -2px; margin-right: 4px; }

.author { display: flex; flex-direction: column; align-items: center; }
.a-avatar {
  width: 52px; height: 52px; border-radius: 50%;
  background: linear-gradient(135deg, var(--purple-400), var(--pink-400));
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; border: 2px solid var(--purple-200); margin-bottom: 8px;
}
.a-name { font-size: 13px; font-weight: 500; margin-bottom: 3px; }
.a-bio { font-size: 11px; color: var(--text-2); margin-bottom: 10px; }
.a-stats { display: flex; gap: 12px; }
.as { text-align: center; }
.as strong { display: block; font-size: 14px; font-weight: 500; }
.as span { font-size: 10px; color: var(--text-3); }

.toc-item {
  display: block; font-size: 12px; color: var(--text-2);
  padding: 4px 0 4px 8px;
  border-left: 2px solid transparent;
  transition: all .15s; line-height: 1.4;
}
.toc-item:hover { color: var(--purple-400); border-left-color: var(--purple-400); }
.toc-h3 { padding-left: 18px; color: var(--text-3); }
.toc-empty, .rec-empty { font-size: 12px; color: var(--text-3); }

@media (max-width: 680px) {
  .wrap { padding-top:16px; }
  .banner { width:calc(100% - 24px); min-height:240px; border-radius:20px; }
  .banner-info { padding:1.4rem; }
  .layout { grid-template-columns: 1fr; padding: 0 12px; }
  .article { padding:1.3rem 1.2rem; }
  .side { display: none; }
}
</style>
