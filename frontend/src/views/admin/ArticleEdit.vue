<template>
  <div class="article-edit-container">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span>网站管理</span> / <span>文章管理</span> / <span class="active">{{ isEdit ? '编辑文章' : '新增文章' }}</span>
    </div>

    <!-- 表单配置区 -->
    <div class="form-container">
      <el-form :model="articleForm" label-width="80px" inline size="default">
        <!-- 第一行 -->
        <el-form-item label="标题：">
          <el-input v-model="articleForm.title" placeholder="输入文章标题" class="w-200" />
        </el-form-item>

        <el-form-item label="分类：">
          <el-select
            v-model="articleForm.categoryId"
            :loading="categoriesLoading"
            :disabled="categoriesLoading || categories.length === 0"
            placeholder="请选择分类"
            class="w-200"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <span v-if="!categoriesLoading && categories.length === 0" class="category-empty-tip">
            请先在分类管理中新增分类
          </span>
        </el-form-item>

        <el-form-item :label="`${t('adminTag.selectLabel')}：`">
          <el-select
            v-model="articleForm.tagIds"
            multiple
            filterable
            clearable
            collapse-tags
            collapse-tags-tooltip
            :loading="tagsLoading"
            :disabled="tagsLoading || tagOptions.length === 0"
            :placeholder="t('adminTag.selectPlaceholder')"
            class="w-200"
          >
            <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
          <span v-if="!tagsLoading && tagOptions.length === 0" class="category-empty-tip">
            {{ t('adminTag.noOptions') }}
          </span>
        </el-form-item>

        <el-form-item label="类型：">
          <el-select v-model="articleForm.type" placeholder="请选择" class="w-150">
            <el-option label="原创" value="original" />
            <el-option label="转载" value="reprint" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态：">
          <el-select v-model="articleForm.status" placeholder="请选择" class="w-150">
            <el-option v-for="option in ARTICLE_STATUS_OPTIONS" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>

        <!-- 第二行 -->
        <div class="form-row-2">
          <el-form-item label="是否顶置：">
            <el-select v-model="articleForm.isTop" placeholder="请选择" class="w-150">
              <el-option label="是" :value="true" />
              <el-option label="否" :value="false" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-upload
              :show-file-list="false"
              :http-request="handleCoverUpload"
              :before-upload="beforeCoverUpload"
              accept="image/jpeg,image/png,image/webp,image/gif"
            >
              <el-button type="info" plain :icon="Upload" :loading="coverUploading">
                {{ coverUploading ? '上传中' : '上传封面' }}
              </el-button>
            </el-upload>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :icon="Position"
              :loading="submitting"
              :disabled="coverUploading"
              @click="handleSave"
            >保存</el-button>
            <el-button type="warning" :icon="Close" @click="handleClose">关闭</el-button>
          </el-form-item>
        </div>

        <div v-if="articleForm.coverImage" class="cover-preview">
          <img :src="articleForm.coverImage" alt="文章封面预览" />
        </div>
      </el-form>
    </div>

    <!-- 双栏 Markdown 编辑器 -->
    <div class="editor-container">
      <!-- 编辑器工具栏 -->
      <div class="editor-toolbar">
        <div class="tool-group" aria-label="Markdown 格式">
          <button class="tool-btn" type="button" title="粗体" @click="insertMarkdown('**', '**', '粗体文字')"><b>B</b></button>
          <button class="tool-btn" type="button" title="斜体" @click="insertMarkdown('*', '*', '斜体文字')"><i>I</i></button>
          <button class="tool-btn" type="button" title="二级标题" @click="insertLinePrefix('## ')">H2</button>
          <button class="tool-btn" type="button" title="链接" @click="insertMarkdown('[', '](https://)', '链接文字')">🔗</button>
          <button class="tool-btn" type="button" title="图片" @click="insertMarkdown('![', '](https://)', '图片描述')">🖼️</button>
          <button class="tool-btn" type="button" title="行内代码" @click="insertMarkdown('`', '`', '代码')">&lt;/&gt;</button>
          <label class="code-language-field">
            <span class="sr-only">代码语言</span>
            <select v-model="selectedCodeLanguage" title="代码块语言" aria-label="代码块语言">
              <option v-for="option in CODE_LANGUAGE_OPTIONS" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </label>
          <button class="code-block-btn" type="button" title="插入代码块" @click="insertCodeBlock">
            代码块
          </button>
        </div>

        <div class="tool-divider"></div>

        <div class="typography-tools" aria-label="字体设置">
          <label class="tool-field">
            <span>字号</span>
            <select v-model="contentFontSize" title="应用到选中文字" @change="applyTypography">
              <option v-for="option in fontSizeOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </label>
          <label class="tool-field">
            <span>字体</span>
            <select v-model="contentFontFamily" title="应用到选中文字" @change="applyTypography">
              <option v-for="option in fontFamilyOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </label>
          <button class="apply-font-btn" type="button" title="将字号和字体保存到选中的文章内容" @click="applyTypography">
            应用到选中文字
          </button>
        </div>

        <div class="toolbar-spacer"></div>
        <button class="punctuation-toggle" type="button" :class="{ active: punctuationOpen }" @click="punctuationOpen = !punctuationOpen">
          中文标点
        </button>
      </div>

      <div v-if="punctuationOpen" class="punctuation-panel" aria-label="常用中文标点">
        <span class="punctuation-label">点击插入</span>
        <button
          v-for="mark in punctuationMarks"
          :key="mark"
          class="punctuation-btn"
          type="button"
          :title="`插入 ${mark}`"
          @click="insertPunctuation(mark)"
        >{{ mark }}</button>
      </div>
      
      <!-- 主体：左写右看 -->
      <div class="editor-body">
        <div class="editor-left">
          <textarea 
            ref="textareaRef"
            v-model="articleForm.content"
            placeholder="开始撰写你的 Markdown 文章内容..."
            @focus="saveSelection"
            @select="saveSelection"
            @keyup="saveSelection"
            @mouseup="saveSelection"
            @input="saveSelection"
          ></textarea>
        </div>
        <div class="editor-right">
          <div v-if="!articleForm.content" class="preview-placeholder">实时预览区域...</div>
          <div
            v-else
            class="preview-content markdown-body"
            v-html="renderedContent"
            @click="handleCodeCopy"
          ></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { Upload, Position, Close } from '@element-plus/icons-vue'
import { articlePublishService, articleUpdateService, getAdminArticleDetail } from '@/api/article'
import { getAdminCategoryList } from '@/api/category'
import { getAdminTagOptions } from '@/api/tag'
import { adminImageUploadService } from '@/api/site'
import { ARTICLE_STATUS, ARTICLE_STATUS_OPTIONS } from '@/constants/articleStatus'
import { CODE_LANGUAGE_OPTIONS, handleCodeCopy, renderMarkdown } from '@/utils/markdown.js'
import { applyTypographyToSelection } from '@/utils/articleTypography.js'


const articleForm = reactive({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: null,
  tagIds: [],
  status: ARTICLE_STATUS.DRAFT
})

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const articleId = computed(() => route.params.id ? Number(route.params.id) : null)
const isEdit = computed(() => articleId.value !== null)

const categories = ref([])
const categoriesLoading = ref(false)
const tagOptions = ref([])
const tagsLoading = ref(false)
const coverUploading = ref(false)
const submitting = ref(false)

const allowedCoverTypes = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/gif'
])
const maxCoverSize = 5 * 1024 * 1024

const beforeCoverUpload = file => {
  if (!allowedCoverTypes.has(file.type)) {
    ElMessage.error('仅支持 JPG、PNG、WebP、GIF 图片')
    return false
  }
  if (file.size > maxCoverSize) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleCoverUpload = async options => {
  const previousCover = articleForm.coverImage
  coverUploading.value = true
  try {
    const result = await adminImageUploadService(options.file)
    const url = result?.data?.url ?? result?.data?.data?.url
    if (!url) throw new Error('上传接口未返回图片地址')

    articleForm.coverImage = url
    options.onSuccess?.(result)
    ElMessage.success('封面上传成功')
  } catch (error) {
    articleForm.coverImage = previousCover
    options.onError?.(error)
    ElMessage.error(getUploadErrorMessage(error))
  } finally {
    coverUploading.value = false
  }
}

const getUploadErrorMessage = error => {
  const status = error?.response?.status
  const serverMessage = error?.response?.data?.message
  if (status === 401) return '登录已失效，请重新登录后上传'
  if (status === 403) return serverMessage || '没有图片上传权限'
  if (status === 413) return '图片过大，请选择 5MB 以内的文件'
  if (status === 415) return serverMessage || '图片格式不受支持'
  if (status >= 500) return serverMessage || '服务器保存图片失败'
  if (!error?.response) return '网络连接失败，请检查服务状态'
  return serverMessage || error?.message || '封面上传失败'
}

const loadCategories = async () => {
  categoriesLoading.value = true
  try {
    const result = await getAdminCategoryList()
    categories.value = Array.isArray(result?.data) ? result.data : []

    if (articleForm.categoryId === null && categories.value.length > 0) {
      articleForm.categoryId = categories.value[0].id
    }
  } catch (error) {
    categories.value = []
    ElMessage.error(error?.response?.data?.message || '加载分类失败')
  } finally {
    categoriesLoading.value = false
  }
}

const loadTagOptions = async () => {
  tagsLoading.value = true
  try {
    const result = await getAdminTagOptions()
    tagOptions.value = Array.isArray(result?.data) ? result.data : []
  } catch (error) {
    tagOptions.value = []
    ElMessage.error(error?.response?.data?.message || t('adminTag.optionsFailed'))
  } finally {
    tagsLoading.value = false
  }
}

const loadArticle = async () => {
  if (!isEdit.value) return

  const result = await getAdminArticleDetail(articleId.value)
  const article = result?.data
  if (!article) throw new Error('Article detail is empty')

  Object.assign(articleForm, {
    title: article.title || '',
    summary: article.summary || '',
    content: article.content || '',
    coverImage: article.coverImage || '',
    categoryId: article.categoryId ?? null,
    tagIds: Array.isArray(article.tags) ? article.tags.map(tag => Number(tag.id)) : [],
    status: Number(article.status ?? ARTICLE_STATUS.DRAFT)
  })
}

onMounted(async () => {
  try {
    await loadArticle()
    await Promise.all([loadCategories(), loadTagOptions()])
  } catch (error) {
    console.error(error)
    ElMessage.error(error?.response?.data?.message || '加载文章信息失败')
  }
})

const textareaRef = ref(null)
const punctuationOpen = ref(false)
const selectedCodeLanguage = ref('java')
const contentFontSize = ref('14')
const contentFontFamily = ref('sans')
const savedSelection = reactive({
  start: 0,
  end: 0,
  valid: false
})

const fontSizeOptions = [
  { label: '12px', value: '12' },
  { label: '14px', value: '14' },
  { label: '16px', value: '16' },
  { label: '18px', value: '18' },
  { label: '20px', value: '20' },
  { label: '24px', value: '24' },
  { label: '28px', value: '28' },
  { label: '32px', value: '32' }
]

const fontFamilyOptions = [
  { label: '等宽体', value: 'mono' },
  { label: '无衬线体', value: 'sans' },
  { label: '宋体', value: 'serif' },
  { label: '黑体', value: 'heiti' },
  { label: '楷体', value: 'kaiti' }
]

const punctuationMarks = [
  '，', '。', '！', '？', '；', '：', '、',
  '“”', '‘’', '（）', '《》', '【】',
  '——', '……', '·'
]

const renderedContent = computed(() => renderMarkdown(articleForm.content))

const saveSelection = () => {
  const textarea = textareaRef.value
  if (!textarea) return
  savedSelection.start = textarea.selectionStart
  savedSelection.end = textarea.selectionEnd
  savedSelection.valid = true
}

const replaceSelection = async (before, after = '', fallback = '') => {
  const textarea = textareaRef.value
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selected = articleForm.content.slice(start, end) || fallback
  const replacement = `${before}${selected}${after}`

  articleForm.content = `${articleForm.content.slice(0, start)}${replacement}${articleForm.content.slice(end)}`
  await nextTick()
  textarea.focus()
  textarea.setSelectionRange(start + before.length, start + before.length + selected.length)
}

const insertMarkdown = (before, after, fallback) => {
  replaceSelection(before, after, fallback)
}

const insertLinePrefix = async prefix => {
  const textarea = textareaRef.value
  if (!textarea) return
  const cursor = textarea.selectionStart
  const lineStart = articleForm.content.lastIndexOf('\n', cursor - 1) + 1
  articleForm.content = `${articleForm.content.slice(0, lineStart)}${prefix}${articleForm.content.slice(lineStart)}`
  await nextTick()
  textarea.focus()
  textarea.setSelectionRange(cursor + prefix.length, cursor + prefix.length)
}

const insertCodeBlock = async () => {
  const textarea = textareaRef.value
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selected = articleForm.content.slice(start, end) || '// 在这里输入代码'
  const longestBacktickRun = Math.max(
    0,
    ...(selected.match(/`+/g) || []).map(sequence => sequence.length)
  )
  const fence = '`'.repeat(Math.max(3, longestBacktickRun + 1))
  const needsLeadingBreak = start > 0 && articleForm.content[start - 1] !== '\n'
  const needsTrailingBreak = end < articleForm.content.length && articleForm.content[end] !== '\n'
  const before = `${needsLeadingBreak ? '\n' : ''}${fence}${selectedCodeLanguage.value}\n`
  const after = `${selected.endsWith('\n') ? '' : '\n'}${fence}${needsTrailingBreak ? '\n' : ''}`

  articleForm.content = `${articleForm.content.slice(0, start)}${before}${selected}${after}${articleForm.content.slice(end)}`
  await nextTick()
  textarea.focus()
  const selectionStart = start + before.length
  textarea.setSelectionRange(selectionStart, selectionStart + selected.length)
}

const applyTypography = async () => {
  const textarea = textareaRef.value
  if (!textarea || !savedSelection.valid) {
    ElMessage.info('请先在正文中选择需要设置字号的文字')
    return
  }

  const result = applyTypographyToSelection({
    content: articleForm.content,
    start: savedSelection.start,
    end: savedSelection.end,
    fontSize: contentFontSize.value,
    fontFamily: contentFontFamily.value
  })

  if (!result.ok && result.reason === 'fenced-code') {
    ElMessage.warning('代码块内容不支持设置字号')
    return
  }
  if (!result.ok) {
    ElMessage.info('请先选中要设置字体的文字')
    return
  }

  articleForm.content = result.content
  await nextTick()
  textarea.focus()
  textarea.setSelectionRange(result.selectionStart, result.selectionEnd)
  saveSelection()
}

const insertPunctuation = async mark => {
  const textarea = textareaRef.value
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  let insertion = mark

  if (mark.length === 2 && ['“”', '‘’', '（）', '《》', '【】'].includes(mark)) {
    const selected = articleForm.content.slice(start, end)
    insertion = `${mark[0]}${selected}${mark[1]}`
  }

  articleForm.content = `${articleForm.content.slice(0, start)}${insertion}${articleForm.content.slice(end)}`
  await nextTick()
  const cursor = start + insertion.length
  textarea.focus()
  textarea.setSelectionRange(cursor, cursor)
}


const handleSave = async () => {

  if (coverUploading.value) {
    ElMessage.warning('封面正在上传，请稍后')
    return
  }

  if (!articleForm.title) {
    ElMessage.warning('请输入文章标题！')
    return
  }

  if (articleForm.categoryId === null) {
    ElMessage.warning('请选择文章分类')
    return
  }

  submitting.value = true
  try {

    if (isEdit.value) {
      await articleUpdateService({ ...articleForm, tagIds: [...articleForm.tagIds], status: Number(articleForm.status), id: articleId.value })
    } else {
      await articlePublishService({ ...articleForm, tagIds: [...articleForm.tagIds], status: Number(articleForm.status) })
    }

    await router.push('/admin/articles')

    ElMessage.success('保存成功')

  } catch(error) {

    console.error(error)
    ElMessage.error(error?.response?.data?.message || '保存失败')

  } finally {
    submitting.value = false
  }
}


const handleClose = () => {
  ElMessage.info('关闭')
}

</script>

<style scoped>
.article-edit-container {
  padding: 16px;
  background-color: #fff;
  min-height: calc(100vh - 110px);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 面包屑 */
.breadcrumb {
  font-size: 13px;
  color: #606266;
  margin-bottom: 20px;
}
.breadcrumb .active {
  color: #409EFF;
  font-weight: bold;
}

/* 表单控宽 */
.w-150 { width: 130px !important; }
.w-200 { width: 180px !important; }
.category-empty-tip { margin-left:8px; color:#e6a23c; font-size:12px; }

.form-row-2 {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

/* 编辑器容器 */
.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  height: 500px;
}

/* 工具栏 */
.editor-toolbar {
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.cover-preview {
  width: min(420px, 100%);
  margin: 10px 0 0 80px;
  overflow: hidden;
  border: 1px solid var(--border-color, #dcdfe6);
  border-radius: 8px;
  background: var(--bg-secondary, #f5f7fa);
  aspect-ratio: 16 / 9;
}
.cover-preview img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.tool-group, .typography-tools { display: flex; align-items: center; gap: 6px; }
.tool-btn {
  width: 30px;
  height: 28px;
  border: 1px solid transparent;
  border-radius: 5px;
  background: transparent;
  cursor: pointer;
  color: #606266;
  font-family: inherit;
  font-size: 12px;
}
.tool-btn:hover {
  color: #409EFF;
  border-color: #b9d9f7;
  background: #ecf5ff;
}
.code-language-field select {
  height: 28px;
  max-width: 112px;
  border: 1px solid #dcdfe6;
  border-radius: 5px;
  background: #fff;
  color: #606266;
  font-family: inherit;
  font-size: 11px;
  outline: none;
  padding: 0 5px;
}
.code-block-btn {
  height: 28px;
  border: 1px solid #b9d9f7;
  border-radius: 5px;
  background: #ecf5ff;
  color: #337ecc;
  cursor: pointer;
  font-family: inherit;
  font-size: 11px;
  padding: 0 8px;
}
.code-block-btn:hover { border-color: #409eff; background: #d9ecff; }
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
.tool-divider { width: 1px; height: 24px; background: #dcdfe6; }
.tool-field { display: flex; align-items: center; gap: 4px; color: #909399; font-size: 11px; }
.tool-field select {
  height: 28px;
  border: 1px solid #dcdfe6;
  border-radius: 5px;
  background: #fff;
  color: #606266;
  font-size: 11px;
  outline: none;
  padding: 0 6px;
}
.apply-font-btn, .punctuation-toggle {
  height: 28px;
  border: 1px solid #c6e2ff;
  border-radius: 5px;
  background: #ecf5ff;
  color: #337ecc;
  cursor: pointer;
  font-family: inherit;
  font-size: 11px;
  padding: 0 9px;
}
.toolbar-spacer { flex: 1; }
.punctuation-toggle.active { border-color: #409eff; background: #409eff; color: #fff; }
.punctuation-panel {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-bottom: 1px solid #dcdfe6;
  background: #fff;
  flex-wrap: wrap;
}
.punctuation-label { margin-right: 4px; color: #909399; font-size: 11px; }
.punctuation-btn {
  min-width: 28px;
  height: 27px;
  border: 1px solid #e4e7ed;
  border-radius: 5px;
  background: #fafafa;
  color: #303133;
  cursor: pointer;
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 14px;
  padding: 0 6px;
}
.punctuation-btn:hover { border-color: #409eff; background: #ecf5ff; color: #337ecc; }

/* 编辑器左右分栏 */
.editor-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.editor-left {
  flex: 1;
  border-right: 1px solid #dcdfe6;
}
.editor-left textarea {
  width: 100%;
  height: 100%;
  border: none;
  resize: none;
  padding: 16px;
  outline: none;
  line-height: 1.75;
  tab-size: 4;
}

.editor-right {
  flex: 1;
  background-color: #fafafa;
  padding: 16px;
  overflow-y: auto;
}
.preview-placeholder {
  color: #c0c4cc;
  font-style: italic;
  text-align: center;
  margin-top: 100px;
}
.preview-content {
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  overflow-wrap: anywhere;
}
.preview-content :deep(h1),
.preview-content :deep(h2),
.preview-content :deep(h3) { margin: 1.1em 0 .55em; line-height: 1.35; }
.preview-content :deep(p) { margin: 0 0 .85em; }
.preview-content :deep(ul),
.preview-content :deep(ol) { margin: .7em 0 .9em 1.4em; }
.preview-content :deep(blockquote) { margin: .8em 0; border-left: 3px solid #a0cfff; padding: .4em .8em; color: #606266; background: #f4f8fc; }
.preview-content :deep(img) { max-width: 100%; }

@media (max-width: 900px) {
  .cover-preview { margin-left: 0; }
  .editor-container { height: auto; min-height: 680px; }
  .editor-body { flex-direction: column; }
  .editor-left { min-height: 330px; border-right: 0; border-bottom: 1px solid #dcdfe6; }
  .editor-right { min-height: 280px; }
  .toolbar-spacer { display: none; }
}
</style>
