import { createApp, defineAsyncComponent } from 'vue'
import App from './App.vue'
import router from './router/index.js'
import './assets/css/global.css'
import './assets/css/code-highlight.css'
import './assets/css/markdown-typography.css'
import ElConfigProvider from 'element-plus/es/components/config-provider/index.mjs'
import ElDropdown from 'element-plus/es/components/dropdown/index.mjs'
import ElLoading from 'element-plus/es/components/loading/index.mjs'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import { initTheme } from './composables/useTheme.js'
import { i18n } from './locales/index.js'

initTheme()

const app = createApp(App)
app.use(i18n)
app.use(router)
app.use(ElConfigProvider)
app.use(ElDropdown)
app.use(ElLoading)

// 后台表格、表单等组件不进入前台首屏；对应页面首次使用时再加载。
const asyncElementComponents = {
  ElButton: () => import('element-plus/es/components/button/index.mjs').then(module => module.default),
  ElCard: () => import('element-plus/es/components/card/index.mjs').then(module => module.default),
  ElDatePicker: () => import('element-plus/es/components/date-picker/index.mjs').then(module => module.default),
  ElDialog: () => import('element-plus/es/components/dialog/index.mjs').then(module => module.default),
  ElForm: () => import('element-plus/es/components/form/index.mjs').then(module => module.default),
  ElFormItem: () => import('element-plus/es/components/form/index.mjs').then(module => module.ElFormItem),
  ElImage: () => import('element-plus/es/components/image/index.mjs').then(module => module.default),
  ElInput: () => import('element-plus/es/components/input/index.mjs').then(module => module.default),
  ElInputNumber: () => import('element-plus/es/components/input-number/index.mjs').then(module => module.default),
  ElOption: () => import('element-plus/es/components/select/index.mjs').then(module => module.ElOption),
  ElPagination: () => import('element-plus/es/components/pagination/index.mjs').then(module => module.default),
  ElRate: () => import('element-plus/es/components/rate/index.mjs').then(module => module.default),
  ElSelect: () => import('element-plus/es/components/select/index.mjs').then(module => module.default),
  ElSwitch: () => import('element-plus/es/components/switch/index.mjs').then(module => module.default),
  ElTabPane: () => import('element-plus/es/components/tabs/index.mjs').then(module => module.ElTabPane),
  ElTable: () => import('element-plus/es/components/table/index.mjs').then(module => module.default),
  ElTableColumn: () => import('element-plus/es/components/table/index.mjs').then(module => module.ElTableColumn),
  ElTabs: () => import('element-plus/es/components/tabs/index.mjs').then(module => module.default),
  ElTag: () => import('element-plus/es/components/tag/index.mjs').then(module => module.default),
  ElUpload: () => import('element-plus/es/components/upload/index.mjs').then(module => module.default)
}

Object.entries(asyncElementComponents).forEach(([name, loader]) => {
  app.component(name, defineAsyncComponent(loader))
})
app.mount('#app')
