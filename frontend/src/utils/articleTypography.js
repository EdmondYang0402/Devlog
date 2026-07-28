export const ARTICLE_FONT_SIZES = Object.freeze([12, 14, 16, 18, 20, 24, 28, 32])

export const ARTICLE_FONT_FAMILIES = Object.freeze({
  mono: 'font-family-mono',
  sans: 'font-family-sans',
  serif: 'font-family-serif',
  heiti: 'font-family-heiti',
  kaiti: 'font-family-kaiti'
})

const TYPOGRAPHY_SPAN_PATTERN = /<span class="((?:font-size-(?:12|14|16|18|20|24|28|32)|font-family-(?:mono|sans|serif|heiti|kaiti))(?: (?:font-size-(?:12|14|16|18|20|24|28|32)|font-family-(?:mono|sans|serif|heiti|kaiti)))*)">$/

const normalizeSelection = (content, start, end) => {
  const length = content.length
  const normalizedStart = Math.max(0, Math.min(Number(start) || 0, length))
  const normalizedEnd = Math.max(normalizedStart, Math.min(Number(end) || 0, length))
  return { start: normalizedStart, end: normalizedEnd }
}

const fencedCodeRanges = content => {
  const ranges = []
  const lines = content.match(/.*(?:\n|$)/g) || []
  let offset = 0
  let openFence = null

  for (const line of lines) {
    const marker = line.match(/^[ \t]{0,3}(`{3,}|~{3,})/)
    if (marker) {
      const markerChar = marker[1][0]
      if (!openFence) {
        openFence = { char: markerChar, length: marker[1].length, start: offset }
      } else if (openFence.char === markerChar && marker[1].length >= openFence.length) {
        ranges.push({ start: openFence.start, end: offset + line.length })
        openFence = null
      }
    }
    offset += line.length
  }

  if (openFence) ranges.push({ start: openFence.start, end: content.length })
  return ranges
}

export const selectionOverlapsFencedCode = (content, start, end) => {
  const selection = normalizeSelection(content, start, end)
  return fencedCodeRanges(content).some(range => (
    selection.start < range.end && selection.end > range.start
  ))
}

const typographyClasses = ({ fontSize, fontFamily }) => {
  const size = Number(fontSize)
  if (!ARTICLE_FONT_SIZES.includes(size)) return null
  const familyClass = ARTICLE_FONT_FAMILIES[fontFamily]
  if (!familyClass) return null
  return `font-size-${size} ${familyClass}`
}

const existingTypographyWrapper = (content, start, end) => {
  if (!content.startsWith('</span>', end)) return null

  const beforeSelection = content.slice(0, start)
  const opening = beforeSelection.match(TYPOGRAPHY_SPAN_PATTERN)
  if (!opening) return null

  return {
    start: start - opening[0].length,
    end: end + '</span>'.length
  }
}

export const applyTypographyToSelection = ({
  content,
  start,
  end,
  fontSize,
  fontFamily
}) => {
  const source = String(content || '')
  const selection = normalizeSelection(source, start, end)

  if (selection.start === selection.end) {
    return { ok: false, reason: 'empty-selection', content: source }
  }
  if (selectionOverlapsFencedCode(source, selection.start, selection.end)) {
    return { ok: false, reason: 'fenced-code', content: source }
  }

  const classNames = typographyClasses({ fontSize, fontFamily })
  if (!classNames) {
    return { ok: false, reason: 'invalid-typography', content: source }
  }

  const selectedText = source.slice(selection.start, selection.end)
  const openingTag = `<span class="${classNames}">`
  const existingWrapper = existingTypographyWrapper(source, selection.start, selection.end)

  if (existingWrapper) {
    const replacement = `${openingTag}${selectedText}</span>`
    return {
      ok: true,
      content: `${source.slice(0, existingWrapper.start)}${replacement}${source.slice(existingWrapper.end)}`,
      selectionStart: existingWrapper.start + openingTag.length,
      selectionEnd: existingWrapper.start + openingTag.length + selectedText.length
    }
  }

  const replacement = `${openingTag}${selectedText}</span>`
  return {
    ok: true,
    content: `${source.slice(0, selection.start)}${replacement}${source.slice(selection.end)}`,
    selectionStart: selection.start + openingTag.length,
    selectionEnd: selection.start + openingTag.length + selectedText.length
  }
}
