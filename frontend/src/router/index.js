import { createRouter, createWebHistory } from 'vue-router'
import { useCurrentUser } from '@/composables/useCurrentUser.js'
import { i18n } from '@/locales/index.js'

const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { titleKey: 'page.home' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue'),
    meta: { titleKey: 'page.login' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterPage.vue'),
    meta: { titleKey: 'page.register' }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue'),
    meta: { titleKey: 'page.about' }
  },
  {
    path: '/archive',
    name: 'Archive',
    component: () => import('@/views/Archive.vue'),
    meta: { titleKey: 'page.archive' }
  },
  {
    path: '/notes',
    name: 'Notes',
    component: () => import('@/views/NotesView.vue'),
    meta: { titleKey: 'page.notes', descriptionKey: 'notes.description' }
  },
  {
    path: '/media',
    name: 'MediaLog',
    component: () => import('@/views/MediaLog.vue'),
    meta: { titleKey: 'media.title' }
  },
  {
    path: '/media/:id',
    name: 'MediaDetail',
    component: () => import('@/views/MediaDetail.vue'),
    props: true,
    meta: { titleKey: 'media.detailTitle' }
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/PostDetail.vue'),
    props: true,
    meta: { titleKey: 'page.articleDetail' }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('@/views/UserProfile.vue'),
    meta: { titleKey: 'page.profile', requiresAuth: true }
  },
  {
    path: '/user/reset-password',
    name: 'UserResetPassword',
    component: () => import('@/views/UserResetPassword.vue'),
    meta: { titleKey: 'page.changePassword', requiresAuth: true }
  },
  

  // ── 后台管理 ──
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/articles' },
      {
        path: 'articles',
        component: () => import('@/views/admin/ArticleList.vue'),
        meta: { title: '文章列表', requiresAuth: true }
      },
      {
        path: 'articles/create',
        component: () => import('@/views/admin/ArticleEdit.vue'),
        meta: { title: '发布文章', requiresAuth: true }
      },
      {
        path: 'articles/edit/:id',
        component: () => import('@/views/admin/ArticleEdit.vue'),
        props: true,
        meta: { title: '编辑文章', requiresAuth: true }
      },
      {
        path: 'comments',
        component: () => import('@/views/admin/CommentList.vue'),
        meta: { title: '评论管理', requiresAuth: true }
      },
      {
        path: 'categories',
        component: () => import('@/views/admin/CategoryList.vue'),
        meta: { title: '分类管理', requiresAuth: true }
      },
      {
        path: 'tags',
        component: () => import('@/views/admin/TagList.vue'),
        meta: { title: '标签管理', requiresAuth: true }
      },
      {
        path: 'media-reviews',
        component: () => import('@/views/admin/MediaReviewList.vue'),
        meta: { titleKey: 'page.adminMedia', title: '作品档案', requiresAuth: true }
      },
      {
        path: 'site-backgrounds',
        name: 'AdminSiteBackgrounds',
        component: () => import('@/views/admin/SiteBackgroundList.vue'),
        meta: { titleKey: 'page.adminBackground', title: '背景管理', requiresAuth: true }
      },
      {
        path: 'blacklist',
        component: () => import('@/views/admin/Blacklist.vue'),
        meta: { title: '黑名单管理', requiresAuth: true }
      },
      {
        path: 'profile',
        component: () => import('@/views/UserProfile.vue'),
        meta: { title: '个人资料', requiresAuth: true }
      },
    ]
  },

  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const pageTitle = to.meta.titleKey
    ? i18n.global.t(to.meta.titleKey)
    : to.meta.title
  document.title = pageTitle
    ? `${pageTitle} - Hathaway's Blog`
    : "Hathaway's Blog"

  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      return next({ name: 'Login', query: { redirect: to.fullPath } })
    }
  }

  if (to.meta.requiresAdmin) {
    try {
      const { loadCurrentUser } = useCurrentUser()
      const user = await loadCurrentUser()
      if (Number(user?.role) !== 1) return next('/home')
    } catch (error) {
      console.error('管理员身份校验失败', error)
      return next({ name: 'Login', query: { redirect: to.fullPath } })
    }
  }

  next()
})

export default router
