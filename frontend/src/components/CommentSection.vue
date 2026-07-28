<template>
  <section class="comment-section" aria-labelledby="comment-heading">
    <header class="comment-header">
      <div>
        <p class="comment-kicker">Discussion</p>
        <h2 id="comment-heading">{{ t('comment.title') }} <span>{{ totalCount }}</span></h2>
      </div>
      <button class="refresh-btn" type="button" :disabled="loading" @click="fetchComments">
        <i class="ti ti-refresh" aria-hidden="true"></i>
        {{ t('comment.refresh') }}
      </button>
    </header>

    <div class="composer">
      <div v-if="replyTarget" class="reply-banner">
        <span>{{ t('comment.replyTo', { name: replyTarget.username || t('nav.user') }) }}</span>
        <button type="button" :aria-label="t('comment.cancelReply')" @click="cancelReply">×</button>
      </div>

      <div class="editor-toolbar" :aria-label="t('comment.inputStyle')">
        <label>
          <span>{{ t('comment.fontSize') }}</span>
          <select v-model="fontSize" :aria-label="t('comment.fontSize')">
            <option value="14px">{{ t('comment.small') }}</option>
            <option value="16px">{{ t('comment.medium') }}</option>
            <option value="18px">{{ t('comment.large') }}</option>
          </select>
        </label>
        <label>
          <span>{{ t('comment.font') }}</span>
          <select v-model="fontFamily" :aria-label="t('comment.font')">
            <option value="sans">{{ t('comment.sans') }}</option>
            <option value="serif">{{ t('comment.serif') }}</option>
            <option value="mono">{{ t('comment.mono') }}</option>
          </select>
        </label>
        <span class="toolbar-tip">{{ t('comment.deviceOnly') }}</span>
      </div>

      <textarea
        v-model="content"
        class="comment-input"
        :class="`font-${fontFamily}`"
        :style="{ fontSize }"
        :placeholder="isLoggedIn ? composerPlaceholder : t('comment.loginToJoin')"
        :disabled="!isLoggedIn || submitting"
        maxlength="1000"
        rows="5"
        @keydown.ctrl.enter="submitComment"
      ></textarea>

      <div class="composer-footer">
        <span v-if="isLoggedIn" class="input-hint">{{ t('comment.shortcut', { count: content.length }) }}</span>
        <router-link v-else class="login-tip" :to="loginRoute">{{ t('comment.loginToComment') }}</router-link>
        <button
          class="submit-btn"
          type="button"
          :disabled="!canSubmit"
          @click="submitComment"
        >
          {{ submitting ? t('comment.publishing') : replyTarget ? t('comment.publishReply') : t('comment.publish') }}
        </button>
      </div>
    </div>

    <div class="comment-list" aria-live="polite">
      <div v-if="loading" class="state-box">{{ t('comment.loading') }}</div>
      <div v-else-if="loadError" class="state-box error-state">
        <span>{{ loadError }}</span>
        <button type="button" @click="fetchComments">{{ t('comment.reload') }}</button>
      </div>
      <div v-else-if="comments.length === 0" class="state-box empty-state">
        <i class="ti ti-message-circle" aria-hidden="true"></i>
        <strong>{{ t('comment.emptyTitle') }}</strong>
        <span>{{ t('comment.emptyHint') }}</span>
      </div>

      <article v-for="comment in comments" v-else :key="comment.id" class="comment-thread">
        <CommentItem
          :comment="comment"
          :current-user-id="currentUserId"
          :is-logged-in="isLoggedIn"
          @reply="startReply"
          @delete="deleteComment"
        />

        <div v-if="comment.replies?.length" class="reply-list">
          <CommentItem
            v-for="reply in comment.replies"
            :key="reply.id"
            :comment="reply"
            :current-user-id="currentUserId"
            :is-logged-in="isLoggedIn"
            compact
            @reply="startReply"
            @delete="deleteComment"
          />
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed, defineComponent, h, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import ElMessageBox from 'element-plus/es/components/message-box/index.mjs'
import {
  commentCreateService,
  commentDeleteService,
  commentListService
} from '@/api/comment.js'
import { userInfoService } from '@/api/user.js'
import { formatDateTime } from '@/utils/date.js'

const props = defineProps({
  articleId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['count-change'])
const route = useRoute()
const { locale, t } = useI18n()

const comments = ref([])
const content = ref('')
const replyTarget = ref(null)
const loading = ref(false)
const submitting = ref(false)
const loadError = ref('')
const isLoggedIn = ref(Boolean(localStorage.getItem('token')))
const currentUserId = ref(null)
const fontSize = ref(localStorage.getItem('comment-font-size') || '16px')
const fontFamily = ref(localStorage.getItem('comment-font-family') || 'sans')

const isActiveComment = comment => Number(comment.isDeleted) === 0
const totalCount = computed(() => comments.value.reduce(
  (count, comment) => count
    + (isActiveComment(comment) ? 1 : 0)
    + (comment.replies || []).filter(isActiveComment).length,
  0
))
const canSubmit = computed(() => (
  isLoggedIn.value && content.value.trim().length > 0 && !submitting.value
))
const composerPlaceholder = computed(() => (
  replyTarget.value
    ? t('comment.replyPlaceholder', { name: replyTarget.value.username || t('nav.user') })
    : t('comment.placeholder')
))
const loginRoute = computed(() => ({
  name: 'Login',
  query: { redirect: route.fullPath }
}))

watch(fontSize, value => localStorage.setItem('comment-font-size', value))
watch(fontFamily, value => localStorage.setItem('comment-font-family', value))
watch(totalCount, value => emit('count-change', value), { immediate: true })

const normalizeComments = data => Array.isArray(data)
  ? data.map(comment => ({ ...comment, replies: comment.replies || [] }))
  : []

const fetchComments = async () => {
  loading.value = true
  loadError.value = ''
  try {
    const result = await commentListService(props.articleId)
    comments.value = normalizeComments(result.data)
  } catch (error) {
    loadError.value = error.response?.data?.message || t('comment.loadFailed')
  } finally {
    loading.value = false
  }
}

const fetchCurrentUser = async () => {
  if (!isLoggedIn.value) return
  try {
    const result = await userInfoService()
    currentUserId.value = result.data?.id ?? null
  } catch {
    currentUserId.value = null
  }
}

const startReply = comment => {
  if (!isLoggedIn.value) {
    ElMessage.info(t('comment.loginRequired'))
    return
  }
  replyTarget.value = comment
  document.querySelector('.comment-input')?.focus()
}

const cancelReply = () => {
  replyTarget.value = null
}

const submitComment = async () => {
  if (!canSubmit.value) return
  submitting.value = true
  try {
    await commentCreateService({
      articleId: Number(props.articleId),
      parentId: replyTarget.value?.id ?? null,
      content: content.value.trim()
    })
    content.value = ''
    replyTarget.value = null
    ElMessage.success(t('comment.published'))
    await fetchComments()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || t('comment.publishFailed'))
  } finally {
    submitting.value = false
  }
}

const deleteComment = async comment => {
  try {
    await ElMessageBox.confirm(t('comment.deleteMessage'), t('comment.deleteTitle'), {
      confirmButtonText: t('comment.confirmDelete'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    await commentDeleteService(comment.id)
    ElMessage.success(t('comment.deleted'))
    await fetchComments()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.response?.data?.message || t('comment.deleteFailed'))
    }
  }
}

const formatTime = value => {
  return formatDateTime(value, locale.value)
}

const CommentItem = defineComponent({
  name: 'CommentItem',
  props: {
    comment: { type: Object, required: true },
    currentUserId: { type: [Number, String], default: null },
    isLoggedIn: { type: Boolean, default: false },
    compact: { type: Boolean, default: false }
  },
  emits: ['reply', 'delete'],
  setup(itemProps, { emit: itemEmit }) {
    return () => {
      const comment = itemProps.comment
      const deleted = Number(comment.isDeleted) === 1
      const displayName = comment.username || t('comment.deactivatedUser')
      const initial = (comment.username || '?').charAt(0).toUpperCase()
      const avatar = comment.avatar
        ? h('img', { class: 'comment-avatar', src: comment.avatar, alt: t('comment.avatarAlt', { name: displayName }) })
        : h('div', { class: 'comment-avatar avatar-fallback' }, initial)
      const actions = []

      if (itemProps.isLoggedIn && !deleted) {
        actions.push(h('button', {
          type: 'button',
          onClick: () => itemEmit('reply', comment)
        }, [h('i', { class: 'ti ti-corner-up-left', 'aria-hidden': 'true' }), ` ${t('comment.reply')}`]))
      }
      if (itemProps.currentUserId != null
          && String(itemProps.currentUserId) === String(comment.userId)
          && !deleted) {
        actions.push(h('button', {
          type: 'button',
          class: 'delete-action',
          onClick: () => itemEmit('delete', comment)
        }, t('comment.delete')))
      }

      return h('div', { class: ['comment-item', { compact: itemProps.compact, deleted }] }, [
        avatar,
        h('div', { class: 'comment-main' }, [
          h('div', { class: 'comment-meta' }, [
            h('strong', displayName),
            h('time', { datetime: comment.createTime || '' }, formatTime(comment.createTime))
          ]),
          h('p', { class: 'comment-content' }, [
            comment.replyUsername && !deleted
              ? h('span', { class: 'reply-user' }, `@${comment.replyUsername} `)
              : null,
            comment.content
          ]),
          actions.length ? h('div', { class: 'comment-actions' }, actions) : null
        ])
      ])
    }
  }
})

onMounted(() => {
  fetchComments()
  fetchCurrentUser()
})
</script>

<style>
.comment-section {
  background: var(--bg-card);
  border: .5px solid var(--border);
  border-radius: var(--r-lg);
  padding: 1.5rem;
}

.comment-header,
.composer-footer,
.comment-meta,
.comment-actions {
  display: flex;
  align-items: center;
}

.comment-header { justify-content: space-between; margin-bottom: 1rem; }
.comment-kicker { color: var(--purple-400); font-size: 10px; font-weight: 600; letter-spacing: .14em; text-transform: uppercase; }
.comment-header h2 { font-size: 18px; font-weight: 600; line-height: 1.4; }
.comment-header h2 span { color: var(--text-3); font-size: 12px; font-weight: 400; margin-left: 3px; }

.refresh-btn,
.comment-actions button {
  border: 0;
  background: transparent;
  color: var(--text-2);
  cursor: pointer;
  font-family: inherit;
}
.refresh-btn { font-size: 12px; padding: 5px 8px; }
.refresh-btn:hover { color: var(--purple-400); }
.refresh-btn:disabled { cursor: wait; opacity: .5; }

.composer {
  overflow: hidden;
  border: .5px solid var(--border);
  border-radius: 10px;
  background: linear-gradient(180deg, rgba(238, 237, 254, .38), #fff 42%);
  transition: border-color .18s, box-shadow .18s;
}
.composer:focus-within { border-color: var(--border-h); box-shadow: 0 0 0 3px rgba(127, 119, 221, .08); }
.reply-banner { display: flex; justify-content: space-between; padding: 8px 12px; background: var(--purple-50); color: var(--purple-600); font-size: 12px; }
.reply-banner button { border: 0; background: transparent; color: inherit; cursor: pointer; font-size: 18px; line-height: 1; }

.editor-toolbar { display: flex; align-items: center; gap: 8px; padding: 9px 10px; border-bottom: .5px solid var(--border); }
.editor-toolbar label { display: flex; align-items: center; gap: 5px; color: var(--text-3); font-size: 11px; }
.editor-toolbar select {
  border: .5px solid var(--border);
  border-radius: 6px;
  background: rgba(255, 255, 255, .85);
  color: var(--text-2);
  font-family: inherit;
  font-size: 11px;
  outline: none;
  padding: 3px 20px 3px 7px;
}
.toolbar-tip { margin-left: auto; color: var(--text-3); font-size: 10px; }

.comment-input {
  display: block;
  width: 100%;
  min-height: 118px;
  resize: vertical;
  border: 0;
  background: transparent;
  color: var(--text-1);
  line-height: 1.75;
  outline: none;
  padding: 14px;
}
.comment-input::placeholder { color: #b5b5c9; }
.comment-input:disabled { cursor: not-allowed; opacity: .65; }
.font-sans { font-family: 'Noto Sans SC', -apple-system, sans-serif; }
.font-serif { font-family: 'Noto Serif SC', 'Songti SC', serif; }
.font-mono { font-family: 'JetBrains Mono', 'SFMono-Regular', Consolas, monospace; }

.composer-footer { justify-content: space-between; border-top: .5px solid var(--border); padding: 9px 10px; }
.input-hint { color: var(--text-3); font-size: 10px; }
.login-tip { color: var(--purple-600); font-size: 12px; }
.submit-btn {
  min-width: 90px;
  border: .5px solid rgba(127, 119, 221, .42);
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(175, 169, 236, .45), rgba(212, 83, 126, .16));
  color: var(--purple-800);
  cursor: pointer;
  font-family: inherit;
  font-size: 12px;
  padding: 6px 14px;
}
.submit-btn:hover:not(:disabled) { border-color: var(--purple-400); transform: translateY(-1px); }
.submit-btn:disabled { cursor: not-allowed; opacity: .45; }

.comment-list { margin-top: 1.2rem; }
.state-box { display: flex; justify-content: center; align-items: center; min-height: 110px; color: var(--text-3); font-size: 12px; }
.error-state { flex-direction: column; gap: 8px; }
.error-state button { border: 0; background: transparent; color: var(--purple-600); cursor: pointer; }
.empty-state { flex-direction: column; gap: 3px; }
.empty-state i { color: var(--purple-200); font-size: 26px; margin-bottom: 3px; }
.empty-state strong { color: var(--text-2); font-size: 13px; }

.comment-thread { border-top: .5px solid var(--border); padding: 1rem 0; }
.comment-thread:first-child { border-top: 0; padding-top: 0; }
.comment-item { display: grid; grid-template-columns: 38px 1fr; gap: 10px; }
.comment-item.compact { grid-template-columns: 30px 1fr; }
.comment-avatar { width: 38px; height: 38px; border: 1px solid rgba(175, 169, 236, .5); border-radius: 50%; object-fit: cover; }
.compact .comment-avatar { width: 30px; height: 30px; }
.avatar-fallback { display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, var(--purple-100), var(--pink-50)); color: var(--purple-600); font-size: 12px; font-weight: 600; }
.comment-main { min-width: 0; }
.comment-meta { gap: 8px; line-height: 1.3; }
.comment-meta strong { color: var(--text-1); font-size: 12px; font-weight: 600; }
.comment-meta time { color: var(--text-3); font-size: 10px; }
.comment-content { margin-top: 5px; color: var(--text-2); font-size: 13px; line-height: 1.7; overflow-wrap: anywhere; white-space: pre-wrap; }
.reply-user { color: var(--purple-600); }
.comment-actions { gap: 12px; margin-top: 5px; }
.comment-actions button { font-size: 10px; padding: 0; }
.comment-actions button:hover { color: var(--purple-500, #655ccb); }
.comment-actions .delete-action:hover { color: #d84b4b; }
.comment-item.deleted .comment-content { color: var(--text-3); font-style: italic; }
.reply-list { margin: .8rem 0 0 48px; border-left: 2px solid var(--purple-50); padding-left: 12px; }
.reply-list .comment-item + .comment-item { margin-top: .9rem; }

@media (max-width: 560px) {
  .comment-section { padding: 1rem; }
  .toolbar-tip { display: none; }
  .reply-list { margin-left: 20px; }
  .input-hint { max-width: 150px; }
}
</style>
