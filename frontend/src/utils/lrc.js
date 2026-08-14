const TIME_TAG_PATTERN = /\[(\d{1,3}):(\d{2})(?:[.:](\d{1,3}))?\]/g
const OFFSET_PATTERN = /\[offset:([+-]?\d+)\]/i

const fractionToSeconds = fraction => {
  if (!fraction) return 0
  return Number(fraction) / (10 ** fraction.length)
}

export const parseLrc = source => {
  if (typeof source !== 'string' || !source.trim()) return []

  const offsetMatch = source.match(OFFSET_PATTERN)
  const offsetSeconds = offsetMatch ? Number(offsetMatch[1]) / 1000 : 0
  const entries = []

  source.split(/\r?\n/).forEach(line => {
    const tags = Array.from(line.matchAll(TIME_TAG_PATTERN))
    if (!tags.length) return

    const text = line.replace(TIME_TAG_PATTERN, '').trim()
    tags.forEach(match => {
      const time = Number(match[1]) * 60 + Number(match[2]) + fractionToSeconds(match[3]) + offsetSeconds
      if (Number.isFinite(time)) entries.push({ time: Math.max(0, time), text })
    })
  })

  return entries.sort((a, b) => a.time - b.time)
}
