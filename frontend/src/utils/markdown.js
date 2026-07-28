import { Marked } from 'marked'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js/lib/core'
import bash from 'highlight.js/lib/languages/bash'
import c from 'highlight.js/lib/languages/c'
import cpp from 'highlight.js/lib/languages/cpp'
import csharp from 'highlight.js/lib/languages/csharp'
import css from 'highlight.js/lib/languages/css'
import go from 'highlight.js/lib/languages/go'
import java from 'highlight.js/lib/languages/java'
import javascript from 'highlight.js/lib/languages/javascript'
import json from 'highlight.js/lib/languages/json'
import kotlin from 'highlight.js/lib/languages/kotlin'
import python from 'highlight.js/lib/languages/python'
import rust from 'highlight.js/lib/languages/rust'
import sql from 'highlight.js/lib/languages/sql'
import typescript from 'highlight.js/lib/languages/typescript'
import xml from 'highlight.js/lib/languages/xml'
import yaml from 'highlight.js/lib/languages/yaml'

const languages = {
  bash,
  c,
  cpp,
  csharp,
  css,
  go,
  java,
  javascript,
  json,
  kotlin,
  python,
  rust,
  sql,
  typescript,
  xml,
  yaml
}

Object.entries(languages).forEach(([name, definition]) => {
  hljs.registerLanguage(name, definition)
})

const LANGUAGE_ALIASES = {
  'c#': 'csharp',
  'c++': 'cpp',
  cs: 'csharp',
  golang: 'go',
  html: 'xml',
  js: 'javascript',
  jsx: 'javascript',
  kt: 'kotlin',
  plain: 'plaintext',
  py: 'python',
  rs: 'rust',
  sh: 'bash',
  shell: 'bash',
  text: 'plaintext',
  ts: 'typescript',
  tsx: 'typescript',
  txt: 'plaintext',
  vue: 'xml',
  yml: 'yaml'
}

const LANGUAGE_LABELS = {
  bash: 'Bash / Shell',
  c: 'C',
  cpp: 'C++',
  csharp: 'C#',
  css: 'CSS',
  go: 'Go',
  html: 'HTML',
  java: 'Java',
  javascript: 'JavaScript',
  json: 'JSON',
  kotlin: 'Kotlin',
  plaintext: 'Plain Text',
  python: 'Python',
  rust: 'Rust',
  sql: 'SQL',
  typescript: 'TypeScript',
  vue: 'Vue',
  xml: 'XML',
  yaml: 'YAML'
}

const escapeHtml = value => String(value)
  .replaceAll('&', '&amp;')
  .replaceAll('<', '&lt;')
  .replaceAll('>', '&gt;')
  .replaceAll('"', '&quot;')
  .replaceAll("'", '&#39;')

const resolveLanguage = infoString => {
  const requested = String(infoString || '').trim().split(/\s+/, 1)[0].toLowerCase()
  if (!requested) {
    return { marker: 'plaintext', highlighter: 'plaintext', label: LANGUAGE_LABELS.plaintext }
  }

  const normalized = LANGUAGE_ALIASES[requested] || requested
  if (!hljs.getLanguage(normalized)) {
    return { marker: 'plaintext', highlighter: 'plaintext', label: LANGUAGE_LABELS.plaintext }
  }

  const marker = ['html', 'vue'].includes(requested) ? requested : normalized
  return {
    marker,
    highlighter: normalized,
    label: LANGUAGE_LABELS[marker] || LANGUAGE_LABELS[normalized] || normalized
  }
}

const renderer = {
  code(code, infoString) {
    const { marker, highlighter, label } = resolveLanguage(infoString)
    const highlighted = highlighter === 'plaintext'
      ? escapeHtml(code)
      : hljs.highlight(code, { language: highlighter, ignoreIllegals: true }).value

    return `
<div class="code-block" data-language="${escapeHtml(marker)}">
  <div class="code-block__toolbar">
    <span class="code-block__language">${escapeHtml(label)}</span>
    <button type="button" class="code-block__copy" data-copy-code aria-label="复制 ${escapeHtml(label)} 代码" aria-live="polite">复制</button>
  </div>
  <pre><code class="hljs language-${escapeHtml(marker)}">${highlighted}</code></pre>
</div>`
  }
}

const markdown = new Marked({
  gfm: true,
  breaks: true,
  renderer
})

const LEGACY_FONT_SIZES = new Map([
  ['12px', 'font-size-12'],
  ['14px', 'font-size-14'],
  ['16px', 'font-size-16'],
  ['18px', 'font-size-18'],
  ['20px', 'font-size-20'],
  ['24px', 'font-size-24'],
  ['28px', 'font-size-28'],
  ['32px', 'font-size-32']
])

const LEGACY_FONT_FAMILIES = new Map([
  ["'jetbrainsmono',consolas,monospace", 'font-family-mono'],
  ["'notosanssc',-apple-system,sans-serif", 'font-family-sans'],
  ["'notoserifsc','songtisc',simsun,serif", 'font-family-serif'],
  ["'microsoftyahei','heitisc',sans-serif", 'font-family-heiti'],
  ["kaiti,'stkaiti',serif", 'font-family-kaiti']
])

const normalizeLegacyTypography = html => html.replace(
  /<span style="([^"]*)">/gi,
  (tag, style) => {
    const declarations = style
      .split(';')
      .map(declaration => declaration.trim())
      .filter(Boolean)
    const classes = []

    for (const declaration of declarations) {
      const separator = declaration.indexOf(':')
      if (separator < 1) return tag
      const property = declaration.slice(0, separator).trim().toLowerCase()
      const value = declaration.slice(separator + 1).trim()

      if (property === 'font-size') {
        const sizeClass = LEGACY_FONT_SIZES.get(value.toLowerCase())
        if (!sizeClass) return tag
        classes.push(sizeClass)
      } else if (property === 'font-family') {
        const familyClass = LEGACY_FONT_FAMILIES.get(value.replace(/\s+/g, '').toLowerCase())
        if (!familyClass) return tag
        classes.push(familyClass)
      } else {
        return tag
      }
    }

    return classes.length ? `<span class="${classes.join(' ')}">` : tag
  }
)

export const renderMarkdown = source => {
  const rendered = normalizeLegacyTypography(markdown.parse(String(source || '')))
  return typeof window === 'undefined'
    ? rendered
    : DOMPurify.sanitize(rendered, {
        FORBID_ATTR: ['style']
      })
}

const writeClipboardText = async text => {
  if (navigator.clipboard && window.isSecureContext) {
    await navigator.clipboard.writeText(text)
    return
  }

  const textarea = document.createElement('textarea')
  textarea.value = text
  textarea.setAttribute('readonly', '')
  textarea.style.cssText = 'position:fixed;inset:auto auto 0 -9999px;opacity:0'
  document.body.appendChild(textarea)
  textarea.select()
  const copied = document.execCommand('copy')
  textarea.remove()
  if (!copied) throw new Error('Clipboard copy failed')
}

export const handleCodeCopy = async event => {
  const target = event.target instanceof Element
    ? event.target.closest('[data-copy-code]')
    : null
  if (!target || !event.currentTarget?.contains(target)) return

  const code = target.closest('.code-block')?.querySelector('code')?.textContent
  if (code === undefined) return

  window.clearTimeout(Number(target.dataset.feedbackTimer || 0))
  try {
    await writeClipboardText(code)
    target.textContent = '已复制'
    target.classList.add('is-copied')
  } catch {
    target.textContent = '复制失败'
    target.classList.remove('is-copied')
  }

  const timer = window.setTimeout(() => {
    if (!target.isConnected) return
    target.textContent = '复制'
    target.classList.remove('is-copied')
    delete target.dataset.feedbackTimer
  }, 1600)
  target.dataset.feedbackTimer = String(timer)
}

export const CODE_LANGUAGE_OPTIONS = Object.freeze([
  { label: 'Java', value: 'java' },
  { label: 'JavaScript', value: 'javascript' },
  { label: 'TypeScript', value: 'typescript' },
  { label: 'HTML', value: 'html' },
  { label: 'CSS', value: 'css' },
  { label: 'Vue', value: 'vue' },
  { label: 'SQL', value: 'sql' },
  { label: 'JSON', value: 'json' },
  { label: 'XML', value: 'xml' },
  { label: 'YAML', value: 'yaml' },
  { label: 'Bash / Shell', value: 'bash' },
  { label: 'Python', value: 'python' },
  { label: 'C', value: 'c' },
  { label: 'C++', value: 'cpp' },
  { label: 'C#', value: 'csharp' },
  { label: 'Kotlin', value: 'kotlin' },
  { label: 'Go', value: 'go' },
  { label: 'Rust', value: 'rust' },
  { label: 'Plain Text', value: 'plaintext' }
])
