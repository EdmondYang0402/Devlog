export default {
  common: {
    confirm: '确认', cancel: '取消', save: '保存', submit: '提交', close: '关闭',
    loading: '加载中…', noData: '暂无数据', retry: '重试', back: '返回',
    search: '搜索', refresh: '刷新', delete: '删除'
  },
  nav: {
    media: '作品档案', notes: '手记',
    home: '首页', archive: '归档', about: '关于', login: '登录', logout: '退出登录',
    admin: '后台管理', profile: '个人资料', changePassword: '修改密码',
    search: '搜索', userMenu: '用户菜单', user: '用户', avatar: '用户头像',
    noBio: '暂无个人简介'
  },
  home: {
    latestArticles: '最新文章', all: '全部', articles: '文章', categories: '分类',
    comments: '评论', clock: '电子时钟', announcement: '公告',
    noAnnouncement: '暂无公告', randomArticles: '随机文章',
    searchPlaceholder: '搜索文章、摘要或标签…', currentStatus: '当前状态', noQuote: '在这里记录思考与生活。',
    noFeaturedPost: '暂时没有可推荐的文章', browseArchive: '浏览完整归档', archiveHint: '从时间轴继续阅读', themeHint: '切换页面明暗主题'
  },
  article: {
    views: '观看', comments: '评论', words: '字', publishedAt: '发布于',
    updatedAt: '更新于', noSummary: '这篇文章暂时没有摘要。', readMore: '阅读全文',
    backHome: '返回首页', toc: '目录', noToc: '暂无目录',
    related: '相关推荐', noRelated: '暂无推荐', noArticles: '暂无文章',
    previous: '上一页', next: '下一页', uncategorized: '未分类', statistics: '文章统计'
  },
  archive: {
    title: '归档 / Archive', eyebrow: '归档 · 写作轨迹', subtitle: '沿着时间的微光，回看每一次记录与思考。',
    total: '共 {count} 篇文章', articleUnit: '篇文章', search: '搜索归档',
    searchPlaceholder: '搜索标题或摘要…', clearSearch: '清空搜索', viewMode: '归档视图',
    timeline: '时间轴', grid: '矩阵', monthLabel: '月', categoryFilter: '分类筛选', allCategories: '全部分类',
    showing: '当前显示 {count} 篇文章', empty: '暂无相关文章',
    emptyHint: '换个关键词或分类试试看吧。', loadFailed: '归档加载失败'
  },
  notes: {
    title: '手记', eyebrow: 'NOTES · 沿途记录',
    subtitle: '记录开发、学习、生活与沿途所想。',
    description: '项目开发、计算机学习、个人生活与阶段思考记录。',
    readMore: '阅读全文', empty: '这里还没有手记。', loadFailed: '手记加载失败，请稍后重试。',
    published: '发布于', views: '阅读', categoryMissing: '“手记”分类尚未初始化。'
  },
  adminTag: {
    menu: '标签管理', title: '标签管理', subtitle: '维护文章使用的主题标签与关联数量。',
    create: '新增标签', edit: '编辑', name: '标签名称', namePlaceholder: '请输入标签名称',
    articleCount: '关联文章数', createdAt: '创建时间', updatedAt: '更新时间', actions: '操作',
    empty: '暂无标签', deleting: '删除中…', deleteTitle: '删除标签',
    deleteConfirm: '确定删除标签“{name}”吗？', nameRequired: '请输入标签名称',
    nameTooLong: '标签名称不能超过 50 个字符', loadFailed: '加载标签失败',
    createSuccess: '标签新增成功', updateSuccess: '标签修改成功', deleteSuccess: '标签删除成功',
    createFailed: '新增标签失败', updateFailed: '修改标签失败', deleteFailed: '删除标签失败',
    selectLabel: '标签', selectPlaceholder: '请选择标签', optionsFailed: '加载标签选项失败',
    noOptions: '请先在标签管理中新增标签'
  },
  tagFilter: {
    label: '标签筛选', placeholder: '请选择标签', selected: '已选择 {count} 个标签',
    clear: '清空标签', empty: '暂无符合条件的文章'
  },
  auth: {
    login: '登录', register: '注册', username: '用户名', email: '邮箱',
    password: '密码', confirmPassword: '确认密码',
    welcomeBack: '欢迎回来，请登录你的账号', createAccount: '创建一个新账号，开始记录',
    forgotPassword: '忘记密码？', noAccount: '还没有账号？', createOne: '立即注册',
    hasAccount: '已有账号？', loginNow: '立即登录',
    usernamePlaceholder: '请输入用户名', emailPlaceholder: '请输入邮箱地址',
    passwordPlaceholder: '请输入密码', confirmPasswordPlaceholder: '请再次输入密码',
    lengthHint: '5~16 位非空字符', requiredUsername: '请输入用户名',
    requiredEmail: '请输入邮箱', invalidEmail: '请输入有效的邮箱地址',
    requiredPassword: '请输入密码', requiredConfirmPassword: '请再次输入密码',
    invalidLength: '长度为 5~16 位非空字符', passwordMismatch: '两次输入的密码不一致',
    loginSuccess: '登录成功', loginFailed: '登录失败', requestFailed: '请求失败，请重试',
    registering: '注册中…', formInvalid: '请检查并修正注册信息',
    registerFailed: '注册失败，请稍后重试', registerSuccess: '注册成功，请登录'
  },
  profile: {
    loginAvatar: '登录头像', changeAvatar: '更换头像', avatarUploading: '上传中…',
    avatarTypeInvalid: '仅支持 JPG、PNG、WebP', avatarSizeInvalid: '图片大小不能超过 2MB',
    avatarResolutionLow: '图片清晰度较低，建议至少使用 200 × 200 的图片',
    avatarUploadSuccess: '头像上传成功，请保存账号资料', avatarUploadFailed: '图片上传失败',
    avatarSaveSuccess: '头像保存成功', avatarSaveFailed: '头像保存失败',
    saveSuccess: '账号资料保存成功', saveFailed: '账号资料保存失败', loadFailed: '账号资料加载失败'
  },
  comment: {
    title: '评论', refresh: '刷新', replyTo: '回复 @{name}', cancelReply: '取消回复',
    inputStyle: '评论输入样式', fontSize: '字号', font: '字体',
    small: '小', medium: '中', large: '大', sans: '无衬线', serif: '衬线', mono: '等宽',
    deviceOnly: '输入样式仅保存在当前设备', loginToJoin: '登录后参与评论',
    shortcut: 'Ctrl + Enter 快速发布 · {count}/1000', loginToComment: '登录后发表评论',
    publishing: '发布中…', publishReply: '发布回复', publish: '发表评论',
    loading: '正在加载评论…', reload: '重新加载', emptyTitle: '还没有评论',
    emptyHint: '来留下第一条想法吧。', placeholder: '写下你的想法，友善交流会让讨论更有价值…',
    replyPlaceholder: '回复 @{name}，说说你的想法…', loginRequired: '请先登录再回复评论',
    published: '评论发布成功', publishFailed: '评论发布失败',
    loadFailed: '评论加载失败，请稍后重试', deleteTitle: '删除评论',
    deleteMessage: '删除后评论内容将不可恢复，确认删除吗？', confirmDelete: '确认删除',
    deleted: '评论已删除', deleteFailed: '删除失败', reply: '回复',
    delete: '删除', deactivatedUser: '已注销用户', avatarAlt: '{name}的头像'
  },
  message: {
    loadFailed: '加载失败', operationSuccess: '操作成功', operationFailed: '操作失败',
    loginRequired: '请先登录', loggedOut: '已退出登录'
  },
  language: { label: '语言', chinese: '中文', japanese: '日本語', english: 'English' },
  theme: { toLight: '切换为亮色模式', toDark: '切换为暗色模式' },
  footer: { builtWith: '使用 Spring Boot + Vue 3 构建', location: '东京 🌸' },
  about: {
    aboutMe: '关于我', introPending: '（自我介绍待填写）',
    techStack: '技术栈', contactMe: '联系我', role: 'Java 后端开发者 · 东京'
  },
  siteBackground: {
    menu: '背景管理', title: '站点背景图片', subtitle: '管理前台全站轮播使用的背景图片。', filters: '背景筛选',
    create: '新增背景', edit: '编辑', actions: '操作', createTitle: '新增背景', editTitle: '编辑背景',
    image: '背景图片', backendTitle: '后台名称', enabledLabel: '是否启用', sortOrder: '排序权重',
    sortHint: '数值越大越靠前', uploadImage: '上传图片', replaceImage: '更换图片', preview: '图片预览',
    previewFailed: '图片预览失败', enabled: '启用', disabled: '停用', allStatus: '全部状态',
    searchPlaceholder: '搜索名称', imageUrlPlaceholder: '上传图片或输入图片地址', titlePlaceholder: '可选，仅用于后台识别',
    createdAt: '创建时间', updatedAt: '更新时间', saveSuccess: '保存成功', saveFailed: '保存失败',
    deleteSuccess: '删除成功', deleteFailed: '删除失败', deleteTitle: '删除背景',
    deleteConfirm: '确定删除背景“{title}”吗？', deleteOssNotice: '删除记录不会自动删除已上传文件。',
    empty: '暂无背景图片', loadFailed: '背景列表加载失败', detailFailed: '背景详情加载失败',
    updateStatusFailed: '启用状态更新失败，已恢复原状态', uploadSuccess: '图片上传成功', uploadFailed: '上传失败',
    imageInvalid: '请选择不超过 5MB 的图片文件',
    validation: { imageRequired: '请上传背景图片或填写图片地址', imageLength: '背景图片地址不能超过 500 个字符', imageUrl: '请输入有效的 HTTP(S) 图片地址', titleLength: '后台名称不能超过 100 个字符', enabled: '启用状态必须为 0 或 1', sortOrder: '排序权重必须为整数', sortRange: '排序权重必须在 -100000 到 100000 之间' }
  },
  media: {
    title: '作品档案', detailTitle: '作品详情', subtitle: '收藏读过、看过与玩过的时光。', filters: '作品筛选', all: '全部',
    timeline: '时间轴', grid: '矩阵', viewMode: '视图模式', sort: '排序', sortLatest: '最新完成', sortRating: '评分最高',
    unrated: '暂无评分', scoreOutOfTen: '{score}/10 分', noFinishedDate: '尚未完成', pendingSection: '进行中 / 计划中',
    noShortReview: '暂未留下短评', empty: '暂无作品记录', emptyHint: '换个筛选条件，或稍后再来看看。',
    loadFailed: '作品档案暂时无法加载', notFound: '没有找到这条作品记录', backToLog: '返回作品档案',
    cover: '封面', workTitle: '作品名称', typeLabel: '类型', statusLabel: '状态', rating: '评分', shortReview: '短评',
    longReview: '详细评价', finishedDate: '完成日期',
    type: { book: '书籍', movie: '电影', anime: '番剧', game: '游戏', unknown: '未知类型' },
    status: {
      unknown: '未知状态',
      generic: { planned: '计划', inProgress: '进行中', completed: '已完成', dropped: '搁置' },
      book: { planned: '想读', inProgress: '在读', completed: '已读', dropped: '搁置' },
      movie: { planned: '想看', inProgress: '在看', completed: '已看', dropped: '搁置' },
      anime: { planned: '想看', inProgress: '在看', completed: '已看', dropped: '搁置' },
      game: { planned: '想玩', inProgress: '在玩', completed: '已完成', dropped: '搁置' }
    },
    admin: {
      menu: '作品档案', create: '新增作品', edit: '编辑', actions: '操作', createTitle: '新增作品', editTitle: '编辑作品',
      searchPlaceholder: '按标题搜索', datePlaceholder: '选择完成日期', coverPlaceholder: '填写 http / https 封面地址', uploadCover: '上传封面',
      ratingHint: '半星 = 1 分，五星 = 10 分', ratingDisplay: '{stars} 星 / {score} 分', updatedAt: '更新时间', titleRequired: '请输入作品名称', titleTooLong: '作品名称不能超过 200 字符',
      typeRequired: '请选择作品类型', statusRequired: '请选择状态', coverTooLong: '封面地址不能超过 500 字符',
      coverInvalid: '封面地址仅支持 http / https', shortReviewTooLong: '短评不能超过 500 字符', ratingInvalid: '评分必须为 1～10 的整数或留空',
      loadFailed: '后台作品列表加载失败', detailFailed: '作品详情加载失败', saveSuccess: '保存成功', saveFailed: '保存失败',
      deleteTitle: '删除作品', deleteConfirm: '确定删除“{title}”吗？', deleteSuccess: '删除成功', deleteFailed: '删除失败',
      imageInvalid: '仅支持不超过 5MB 的图片', uploadSuccess: '封面上传成功', uploadFailed: '封面上传失败'
    }
  },
  page: {
    home: '首页', login: '登录', register: '注册', about: '关于',
    archive: '归档', articleDetail: '文章详情', profile: '个人资料',
    changePassword: '修改密码', admin: '后台管理', adminMedia: '作品档案', adminBackground: '背景管理', notes: '手记'
  }
}
