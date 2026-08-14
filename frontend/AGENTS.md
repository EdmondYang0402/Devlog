# DevLog 前端指令

本文件适用于 `frontend/`。同时遵守仓库根目录 `AGENTS.md`。

## Implementation

- 技术栈为 Vue 3 + Vite 5 + Element Plus + Axios + Vue Router + Vue I18n；沿用现有 Composition API、路由懒加载、API 模块与全局主题变量。
- 修改前先定位真实页面、子组件、composable、API 封装和 CSS。优先复用 `src/components`、`src/composables`、`src/api` 与 `src/assets/css`，避免建立功能重复的平行实现。
- 用户只要求某个区域时只改该区域，保留正常业务逻辑；可以清理确定废弃的样式，不用大量 `display:none` 掩盖旧实现。
- 面向用户的新增文案接入现有 Vue I18n，并同步维护项目支持的语言；不要在组件中散落只适用于一种语言的字符串。

## Visual direction

- 前台方向：轻量玻璃拟态、克制的包豪斯/编辑式几何、动漫插画背景、深蓝/灰/低饱和紫、充足留白、强信息层级、低视觉噪声。
- 背景图负责氛围，内容可读性始终优先于完整显露背景。
- 包豪斯重点是 grid、比例、留白、字体、少量几何元素与清晰层级；不要理解成到处加圆/方块、强行红黄蓝、游戏 HUD、赛博朋克或 SaaS dashboard。
- 玻璃面板优先半透明、轻边框、适量 `backdrop-filter`、柔和阴影和统一圆角；避免大黑框、高不透明黑面板、强发光、多层玻璃套娃与过度模糊。
- 后台 UI 比前台更直接、紧凑、克制；不要把前台装饰性设计无差别搬进管理界面。

## Interaction, animation, and responsive behavior

- 简单动画优先 `opacity` 与 `transform`，避免动画化 `width`、`height`、`margin`、`padding`、`top`、`left` 等易触发布局计算的属性。
- 不为普通 transition 引入动画库。前台路由/区域过渡柔和淡入淡出，后台反馈更快速克制；尊重 `prefers-reduced-motion`。
- 新 UI 至少检查 desktop、medium、mobile；移动端不能直接套用桌面固定尺寸，也不能把 hover 作为唯一操作方式。
- 维持键盘可达性、焦点样式、语义元素及必要的 ARIA；交互不能只依靠颜色区分。

## Performance

- 对大图片、图片 decode、重复 API、重复组件初始化、timer、watcher、`backdrop-filter` 与不必要预加载保持敏感；localhost 下流畅不代表线上媒体无成本。
- 复用已取得的数据，避免同一路由或父子组件重复请求。新增 timer、监听器和全局事件时必须在卸载时清理。
- 图片按真实显示需求选择尺寸与加载策略；不要为了展示背景或缩略图无条件加载原图。

## Stable frontend decisions

### Archive and Notes

- 中文归档 Hero 保持紧凑、透明、轻盈，主要显示“归档”和“共 X 篇文章”；不要恢复英文 `Archive`、“归档 · 写作轨迹”或说明性副标题。
- 手记继续复用 Article，页面 Hero 主要只显示“手记”；不要恢复 `NOTES · 沿途记录` 或描述性副标题，允许保留少量几何装饰。

### Navbar

- 前台 Navbar 在所有前台页面始终显示，并由公共 Layout 统一预留顶部安全区域；保持当前轻量玻璃视觉，不使用吊绳、hover 唤出或自动隐藏机制。
- Navbar 不显示 `Hathaway's Blog`；语言切换左侧不恢复无意义圆形装饰。

### Home profile and floating controls

- 首页博主玻璃卡片保持较大的头像视觉占比，主标题为 `Hathaway's Blog`，可使用可读的 script/花体；保留文章、分类、评论统计，不重复堆叠 bio、技术标签或人名信息。
- 前台不使用沉浸模式拉链或只显示背景模式；背景轮播保持为公共 Layout 的常驻氛围层。
- 前台使用通用 `BackToTop`；新增或移动浮动控件时与现有控件相互避让，检查窄屏重叠。

### Music

- 音乐播放器使用前台公共 Layout 中唯一的原生 `<audio>`，播放状态由模块级全局状态统一管理；页面 UI 不直接操作 Audio，前台路由切换不得销毁或重新创建播放器，后台不显示播放器。
- 播放器固定在左侧中部，桌面端默认收起并通过 hover 展开，移动端通过点击切换；曲目使用可直接播放的 URL 与可选封面/LRC，不引入第三方播放器库或非官方音乐 API。

### Seasonal effects

- 季节特效由前台公共 Layout 统一挂载，支持无特效、樱花、雨滴、落叶和雪花；选择保存在本地并跨前台路由生效，后台不挂载。
- 特效层必须保持点击穿透、控制粒子数量，并为移动端、低性能设备和 `prefers-reduced-motion` 提供降级；新增特效继续复用统一状态与挂载层。

## Verification

- 修改后在 `frontend/` 运行 `npm run build`；涉及 `src/utils/articleTypography.test.mjs` 或相关工具逻辑时运行 `npm test`。
- 用实际页面检查受影响状态与 desktop/medium/mobile 布局；视觉任务应确认亮暗主题及交互状态。项目没有 lint script，除非先经用户授权新增，否则不要假设存在。
