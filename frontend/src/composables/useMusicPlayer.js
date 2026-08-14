import { computed, ref } from 'vue'
import { musicPlayerConfig } from '@/constants/music.js'
import { parseLrc } from '@/utils/lrc.js'

const normalizeLyrics = track => {
  if (Array.isArray(track?.lyrics)) {
    return track.lyrics
      .map(item => ({ time: Number(item?.time), text: String(item?.text || '').trim() }))
      .filter(item => Number.isFinite(item.time))
      .sort((a, b) => a.time - b.time)
  }
  return parseLrc(track?.lrc)
}

const normalizeTrack = (track, index) => ({
  id: String(track?.id || `track-${index}`),
  title: String(track?.title || '').trim(),
  artist: String(track?.artist || '').trim(),
  src: String(track?.src || '').trim(),
  cover: String(track?.cover || '').trim(),
  lyrics: normalizeLyrics(track)
})

const playlist = ref(musicPlayerConfig.playlist.map(normalizeTrack).filter(track => track.src))
const currentIndex = ref(playlist.value.length ? 0 : -1)
const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const loadFailed = ref(false)

let audioElement = null

const currentSong = computed(() => playlist.value[currentIndex.value] || null)
const hasTracks = computed(() => playlist.value.length > 0)
const currentLyric = computed(() => {
  const lyrics = currentSong.value?.lyrics || []
  let left = 0
  let right = lyrics.length - 1
  let match = -1

  while (left <= right) {
    const middle = Math.floor((left + right) / 2)
    if (lyrics[middle].time <= currentTime.value) {
      match = middle
      left = middle + 1
    } else {
      right = middle - 1
    }
  }

  return match >= 0 ? lyrics[match].text : ''
})

const resetTimeline = () => {
  currentTime.value = 0
  duration.value = 0
  loadFailed.value = false
}

const syncAudioSource = async (autoplay = false) => {
  if (!audioElement) return
  audioElement.pause()
  isPlaying.value = false
  resetTimeline()

  const source = currentSong.value?.src
  if (!source) {
    audioElement.removeAttribute('src')
    audioElement.load()
    return
  }

  audioElement.src = source
  audioElement.load()
  if (autoplay) {
    try {
      await audioElement.play()
    } catch {
      loadFailed.value = true
    }
  }
}

const attachAudio = element => {
  audioElement = element
  syncAudioSource(false)
}

const detachAudio = element => {
  if (audioElement !== element) return
  audioElement.pause()
  audioElement = null
  isPlaying.value = false
}

const play = async () => {
  if (!audioElement || !currentSong.value?.src) return
  loadFailed.value = false
  try {
    await audioElement.play()
  } catch {
    loadFailed.value = true
  }
}

const pause = () => audioElement?.pause()
const togglePlayback = () => isPlaying.value ? pause() : play()

const selectTrack = (index, autoplay = isPlaying.value) => {
  if (!playlist.value.length) return
  const normalizedIndex = (index + playlist.value.length) % playlist.value.length
  currentIndex.value = normalizedIndex
  syncAudioSource(autoplay)
}

const previous = () => selectTrack(currentIndex.value - 1)
const next = (autoplay = isPlaying.value) => selectTrack(currentIndex.value + 1, autoplay)

const seek = value => {
  if (!audioElement || !Number.isFinite(duration.value) || duration.value <= 0) return
  const nextTime = Math.min(duration.value, Math.max(0, Number(value) || 0))
  audioElement.currentTime = nextTime
  currentTime.value = nextTime
}

const syncMetadata = event => {
  const nextDuration = Number(event.currentTarget?.duration)
  duration.value = Number.isFinite(nextDuration) ? nextDuration : 0
}
const syncTime = event => {
  const nextTime = Number(event.currentTarget?.currentTime)
  currentTime.value = Number.isFinite(nextTime) ? nextTime : 0
}
const handlePlay = () => { isPlaying.value = true; loadFailed.value = false }
const handlePause = () => { isPlaying.value = false }
const handleEnded = () => next(true)
const handleError = () => { isPlaying.value = false; loadFailed.value = true }

export const useMusicPlayer = () => ({
  playerTitle: musicPlayerConfig.title,
  playlist,
  currentIndex,
  currentSong,
  currentLyric,
  hasTracks,
  isPlaying,
  currentTime,
  duration,
  loadFailed,
  attachAudio,
  detachAudio,
  play,
  pause,
  togglePlayback,
  previous,
  next,
  seek,
  syncMetadata,
  syncTime,
  handlePlay,
  handlePause,
  handleEnded,
  handleError
})
