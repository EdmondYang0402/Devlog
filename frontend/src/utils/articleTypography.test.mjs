import test from 'node:test'
import assert from 'node:assert/strict'
import { applyTypographyToSelection } from './articleTypography.js'
import { renderMarkdown } from './markdown.js'

const apply = (content, selected, fontSize = 20, fontFamily = 'sans') => {
  const start = content.indexOf(selected)
  return applyTypographyToSelection({
    content,
    start,
    end: start + selected.length,
    fontSize,
    fontFamily
  })
}

test('only wraps an ordinary text selection', () => {
  const result = apply('第一段文字，第二段文字。', '第二段文字')
  assert.equal(result.ok, true)
  assert.equal(result.content, '第一段文字，<span class="font-size-20 font-family-sans">第二段文字</span>。')
})

test('does not change content without a selection', () => {
  const content = '已有全文不能改变'
  const result = applyTypographyToSelection({
    content,
    start: 2,
    end: 2,
    fontSize: 20,
    fontFamily: 'sans'
  })
  assert.deepEqual(result, { ok: false, reason: 'empty-selection', content })
})

test('preserves line breaks in a multiline selection', () => {
  const content = '前文\n第一行\n第二行\n第三行\n后文'
  const result = apply(content, '第一行\n第二行\n第三行', 18)
  assert.match(result.content, /font-size-18[^>]*>第一行\n第二行\n第三行<\/span>/)
})

test('preserves Markdown emphasis inside the selection', () => {
  const result = apply('这里是 **重要文字**。', '**重要文字**', 24)
  assert.match(result.content, />\*\*重要文字\*\*<\/span>/)
  const rendered = renderMarkdown(result.content)
  assert.match(rendered, /<span class="font-size-24 font-family-sans"><strong>重要文字<\/strong><\/span>/)
})

test('rejects selections overlapping fenced code blocks', () => {
  const content = '正文\n```js\nconst value = 1\n```\n结尾'
  const result = apply(content, 'const value = 1')
  assert.equal(result.ok, false)
  assert.equal(result.reason, 'fenced-code')
  assert.equal(result.content, content)
})

test('keeps the controlled marker after a save and reload round trip', () => {
  const result = apply('保存后仍保留', '仍保留', 18, 'serif')
  const reloaded = JSON.parse(JSON.stringify({ content: result.content }))
  assert.match(reloaded.content, /class="font-size-18 font-family-serif"/)
})

test('applies independent sizes to two different selections', () => {
  const first = apply('甲处和乙处', '甲处', 18)
  const second = apply(first.content, '乙处', 28)
  assert.match(second.content, /font-size-18[^>]*>甲处<\/span>/)
  assert.match(second.content, /font-size-28[^>]*>乙处<\/span>/)
})

test('replaces an existing generated wrapper instead of nesting it', () => {
  const first = apply('重复设置', '重复', 18, 'mono')
  const second = apply(first.content, '重复', 24, 'kaiti')
  assert.equal((second.content.match(/<span/g) || []).length, 1)
  assert.match(second.content, /class="font-size-24 font-family-kaiti"/)
})

test('converts legacy safe inline typography to controlled classes', () => {
  const legacy = '<span style="font-size:18px;font-family:\'Noto Sans SC\', -apple-system, sans-serif">旧内容</span>'
  const rendered = renderMarkdown(legacy)
  assert.match(rendered, /<span class="font-size-18 font-family-sans">旧内容<\/span>/)
})
