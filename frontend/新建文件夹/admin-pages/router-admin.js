// 把以下路由添加到你的 router/index.js 的 routes 数组里

{
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
        { path: '', redirect: '/admin/articles' },
        { path: 'articles', component: () => import('@/views/admin/ArticleList.vue'), meta: { title: '文章列表' } },
        { path: 'articles/create', component: () => import('@/views/admin/ArticleEdit.vue'), meta: { title: '发布文章' } },
        { path: 'articles/edit/:id', component: () => import('@/views/admin/ArticleEdit.vue'), meta: { title: '编辑文章' }, props: true },
        { path: 'comments', component: () => import('@/views/admin/CommentList.vue'), meta: { title: '评论管理' } },
        { path: 'categories', component: () => import('@/views/admin/CategoryList.vue'), meta: { title: '分类管理' } },
        { path: 'tags', component: () => import('@/views/admin/TagList.vue'), meta: { title: '标签管理' } },
        { path: 'blacklist', component: () => import('@/views/admin/Blacklist.vue'), meta: { title: '黑名单管理' } },
        { path: 'profile', component: () => import('@/views/UserProfile.vue'), meta: { title: '个人资料' } },
        { path: 'settings', component: () => import('@/views/admin/SiteSettings.vue'), meta: { title: '站点设置' } },
    ]
},
