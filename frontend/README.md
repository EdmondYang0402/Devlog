# Hathaway's Blog — Frontend

Vue 3 + Vite 博客前端，配合 Spring Boot 后端使用。

## 启动

```bash
npm install
npm run dev
```

## 构建部署

```bash
npm run build
# 产物在 dist/，丢到 Nginx 即可
```

Nginx 反向代理配置：

```nginx
location /api/ {
    proxy_pass http://localhost:8080/api/;
}
```

## 🖼️ 替换壁纸

把你喜欢的图片放到 `public/images/` 目录下，命名为 `hero.jpg`（或任意格式），然后修改 `src/views/Home.vue` 里的：

```html
<img src="/images/hero.jpg" ... />
```

图片不存在时会自动显示紫色渐变占位，不会报错。

## 🖼️ 文章封面图

每篇文章数据里有 `coverUrl` 字段：

```js
{
  coverUrl: '/uploads/jwt.jpg',  // 填写图片路径
  coverIcon: '🔐',               // 无封面时的 emoji 占位
}
```

后端返回封面图 URL，前端自动显示；为空时显示渐变色 + emoji。

## 目录结构

```
src/
├── api/            # Axios 封装
├── assets/css/     # 全局样式
├── components/
│   ├── NavBar.vue
│   ├── FooterBar.vue
│   ├── StarCanvas.vue
│   ├── PostCard.vue    ← 支持封面图
│   └── SidebarClock.vue
├── router/
└── views/
    ├── Home.vue        ← Hero 壁纸框架 + 侧边栏
    ├── PostDetail.vue  ← 文章大图 Banner
    ├── Archive.vue
    └── About.vue
```

## 对接后端

编辑 `src/api/index.js`，baseURL 默认 `/api`，开发时通过 `vite.config.js` proxy 转发到 `http://localhost:8080`。

`Home.vue` 和 `PostDetail.vue` 里有注释标记 `📌 对接后端`，按注释替换 mock 数据即可。
