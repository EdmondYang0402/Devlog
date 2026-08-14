<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMusicPlayer } from '@/composables/useMusicPlayer.js'

const { t } = useI18n()
const {
  playerTitle,
  currentSong,
  currentLyric,
  hasTracks,
  isPlaying,
  currentTime,
  duration,
  loadFailed,
  togglePlayback,
  previous,
  next,
  seek
} = useMusicPlayer()

const expanded = ref(false)
const isMobile = ref(false)
const coverFailed = ref(false)
const panelId = 'floating-music-player-panel'
let closeTimer = null
let mobileQuery = null

const displayTitle = computed(() => currentSong.value?.title || playerTitle)
const displayArtist = computed(() => currentSong.value
  ? currentSong.value.artist || t('music.unknownArtist')
  : t('music.emptyPlaylist'))
const displayLyric = computed(() => {
  if (loadFailed.value) return t('music.loadFailed')
  return currentLyric.value || t('music.noLyrics')
})
const progressStyle = computed(() => ({
  '--music-progress': duration.value > 0 ? `${Math.min(100, currentTime.value / duration.value * 100)}%` : '0%'
}))

const formatTime = value => {
  const seconds = Number.isFinite(Number(value)) ? Math.max(0, Math.floor(Number(value))) : 0
  return `${String(Math.floor(seconds / 60)).padStart(2, '0')}:${String(seconds % 60).padStart(2, '0')}`
}

const clearCloseTimer = () => {
  if (!closeTimer) return
  window.clearTimeout(closeTimer)
  closeTimer = null
}
const openPlayer = () => {
  clearCloseTimer()
  expanded.value = true
}
const closePlayer = () => {
  clearCloseTimer()
  expanded.value = false
}
const scheduleClose = () => {
  if (isMobile.value) return
  clearCloseTimer()
  closeTimer = window.setTimeout(closePlayer, 300)
}
const handlePointerEnter = () => {
  if (!isMobile.value) openPlayer()
}
const handleFocusIn = () => {
  if (!isMobile.value) openPlayer()
}
const handleTrigger = () => {
  if (isMobile.value) expanded.value = !expanded.value
  else openPlayer()
}
const handleFocusOut = event => {
  if (event.currentTarget.contains(event.relatedTarget)) return
  scheduleClose()
}
const updateMobileMode = event => {
  isMobile.value = event.matches
  closePlayer()
}
const handleSeek = event => seek(event.target.value)

watch(() => currentSong.value?.id, () => { coverFailed.value = false })

onMounted(() => {
  mobileQuery = window.matchMedia('(max-width: 680px)')
  isMobile.value = mobileQuery.matches
  mobileQuery.addEventListener?.('change', updateMobileMode)
})
onUnmounted(() => {
  clearCloseTimer()
  mobileQuery?.removeEventListener?.('change', updateMobileMode)
})
</script>

<template>
  <aside
    class="floating-music-player"
    :class="{ 'is-expanded': expanded, 'is-mobile': isMobile }"
    :aria-label="t('music.playerLabel')"
    @mouseenter="handlePointerEnter"
    @mouseleave="scheduleClose"
    @focusin="handleFocusIn"
    @focusout="handleFocusOut"
    @keydown.esc="closePlayer"
  >
    <div class="music-player-shell">
      <section
        :id="panelId"
        class="music-player-panel"
        :aria-hidden="!expanded"
        :inert="!expanded"
      >
        <div class="music-player-summary">
          <div class="music-player-cover" :class="{ 'is-playing': isPlaying }">
            <img
              v-if="currentSong?.cover && !coverFailed"
              :src="currentSong.cover"
              :alt="t('music.coverAlt', { title: displayTitle })"
              @error="coverFailed = true"
            />
            <span v-else class="music-player-cover__fallback" aria-hidden="true">
              <i class="ti ti-music"></i>
            </span>
          </div>

          <div class="music-player-meta">
            <strong :title="displayTitle">{{ displayTitle }}</strong>
            <span :title="displayArtist">{{ displayArtist }}</span>
          </div>
        </div>

        <div class="music-player-lyric" aria-live="polite">
          <Transition name="lyric-fade" mode="out-in">
            <span :key="displayLyric">{{ displayLyric }}</span>
          </Transition>
        </div>

        <div class="music-player-progress">
          <time>{{ formatTime(currentTime) }}</time>
          <input
            type="range"
            min="0"
            :max="duration || 0"
            step="0.1"
            :value="currentTime"
            :style="progressStyle"
            :disabled="!hasTracks || duration <= 0"
            :aria-label="t('music.seek')"
            @input="handleSeek"
          />
          <time>{{ formatTime(duration) }}</time>
        </div>

        <div class="music-player-controls">
          <button type="button" :disabled="!hasTracks" :aria-label="t('music.previous')" :title="t('music.previous')" @click="previous">
            <i class="ti ti-player-skip-back" aria-hidden="true"></i>
          </button>
          <button class="music-player-controls__primary" type="button" :disabled="!hasTracks" :aria-label="isPlaying ? t('music.pause') : t('music.play')" :title="isPlaying ? t('music.pause') : t('music.play')" @click="togglePlayback">
            <i class="ti" :class="isPlaying ? 'ti-player-pause-filled' : 'ti-player-play-filled'" aria-hidden="true"></i>
          </button>
          <button type="button" :disabled="!hasTracks" :aria-label="t('music.next')" :title="t('music.next')" @click="next()">
            <i class="ti ti-player-skip-forward" aria-hidden="true"></i>
          </button>
        </div>
      </section>

      <button
        type="button"
        class="music-player-handle"
        :aria-expanded="expanded"
        :aria-controls="panelId"
        :aria-label="expanded ? t('music.collapse') : t('music.expand')"
        :title="expanded ? t('music.collapse') : t('music.expand')"
        @click="handleTrigger"
      >
        <span class="music-player-handle__disc" :class="{ 'is-playing': isPlaying }" aria-hidden="true">
          <i class="ti ti-music"></i>
        </span>
      </button>
    </div>
  </aside>
</template>

<style scoped>
.floating-music-player{position:fixed;left:0;top:50%;z-index:120;width:min(328px,calc(100vw - 16px));transform:translateY(-50%);pointer-events:none}
.music-player-shell{display:grid;grid-template-columns:minmax(0,1fr) 48px;width:100%;min-height:174px;overflow:hidden;border:1px solid color-mix(in srgb,var(--glass-border) 88%,transparent);border-left:0;border-radius:0 22px 22px 0;background:color-mix(in srgb,var(--glass-bg) 80%,rgba(17,18,33,.5));box-shadow:0 18px 44px rgba(8,9,23,.25),inset 0 1px 0 rgba(255,255,255,.1);-webkit-backdrop-filter:blur(14px) saturate(112%);backdrop-filter:blur(14px) saturate(112%);pointer-events:auto;transform:translateX(calc(-100% + 48px));transition:transform 260ms ease}
.floating-music-player.is-expanded .music-player-shell{transform:translateX(0)}
.music-player-panel{display:grid;grid-template-rows:auto minmax(34px,auto) auto auto;gap:8px;min-width:0;padding:14px 10px 12px 14px;color:var(--glass-text);opacity:0;transition:opacity 180ms ease}
.floating-music-player.is-expanded .music-player-panel{opacity:1;transition-delay:70ms}
.music-player-summary{display:grid;grid-template-columns:56px minmax(0,1fr);align-items:center;gap:11px;min-width:0}
.music-player-cover{width:56px;height:56px;overflow:hidden;border:1px solid color-mix(in srgb,var(--purple-200) 26%,transparent);border-radius:14px;background:linear-gradient(145deg,rgba(164,154,255,.22),rgba(212,83,126,.13));box-shadow:0 8px 20px rgba(9,10,24,.18)}
.music-player-cover img,.music-player-cover__fallback{display:block;width:100%;height:100%;object-fit:cover}
.music-player-cover__fallback{display:grid;place-items:center;color:var(--purple-200);font-size:24px;background:radial-gradient(circle at 35% 28%,rgba(255,255,255,.18),transparent 34%),linear-gradient(145deg,rgba(127,119,221,.5),rgba(64,58,105,.32))}
.music-player-cover.is-playing img,.music-player-cover.is-playing .music-player-cover__fallback{animation:music-cover-breathe 8s ease-in-out infinite}
.music-player-meta{display:flex;min-width:0;flex-direction:column;gap:4px}.music-player-meta strong,.music-player-meta span{overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.music-player-meta strong{font-size:14px;font-weight:680;letter-spacing:.01em}.music-player-meta span{color:var(--glass-muted);font-size:11px}
.music-player-lyric{display:grid;min-height:34px;place-items:center start;color:color-mix(in srgb,var(--glass-text) 84%,var(--purple-100));font-size:12px;line-height:1.45}.music-player-lyric span{display:-webkit-box;overflow:hidden;-webkit-box-orient:vertical;-webkit-line-clamp:2}
.music-player-progress{display:grid;grid-template-columns:34px minmax(0,1fr) 34px;align-items:center;gap:7px}.music-player-progress time{color:var(--glass-muted);font-family:'JetBrains Mono',monospace;font-size:9px;font-variant-numeric:tabular-nums}.music-player-progress time:last-child{text-align:right}
.music-player-progress input{width:100%;height:14px;margin:0;appearance:none;background:transparent;cursor:pointer}.music-player-progress input:disabled{cursor:not-allowed;opacity:.42}.music-player-progress input::-webkit-slider-runnable-track{height:3px;border-radius:999px;background:linear-gradient(to right,var(--purple-300) 0 var(--music-progress),color-mix(in srgb,var(--glass-border) 86%,transparent) var(--music-progress) 100%)}.music-player-progress input::-webkit-slider-thumb{width:10px;height:10px;margin-top:-3.5px;appearance:none;border:2px solid rgba(255,255,255,.78);border-radius:50%;background:var(--purple-400);box-shadow:0 2px 6px rgba(27,21,67,.26)}.music-player-progress input::-moz-range-track{height:3px;border-radius:999px;background:color-mix(in srgb,var(--glass-border) 86%,transparent)}.music-player-progress input::-moz-range-progress{height:3px;border-radius:999px;background:var(--purple-300)}.music-player-progress input::-moz-range-thumb{width:8px;height:8px;border:2px solid rgba(255,255,255,.78);border-radius:50%;background:var(--purple-400)}
.music-player-controls{display:flex;align-items:center;justify-content:center;gap:17px}.music-player-controls button{display:grid;width:28px;height:28px;padding:0;place-items:center;border:1px solid transparent;border-radius:50%;background:transparent;color:var(--glass-muted);cursor:pointer;transition:color 180ms ease,background-color 180ms ease,border-color 180ms ease,transform 180ms ease}.music-player-controls button:hover:not(:disabled),.music-player-controls button:focus-visible{color:var(--purple-200);background:rgba(164,154,255,.12);outline:none;transform:translateY(-1px)}.music-player-controls button:focus-visible{border-color:color-mix(in srgb,var(--purple-200) 58%,transparent)}.music-player-controls button:disabled{cursor:not-allowed;opacity:.32}.music-player-controls .music-player-controls__primary{width:38px;height:38px;border-color:color-mix(in srgb,var(--purple-200) 34%,transparent);background:linear-gradient(145deg,rgba(127,119,221,.72),rgba(83,74,183,.56));color:#fff;box-shadow:0 7px 18px rgba(65,54,142,.22)}
.music-player-handle{display:grid;width:48px;min-height:100%;padding:0;place-items:center;border:0;border-left:1px solid color-mix(in srgb,var(--glass-border) 70%,transparent);background:linear-gradient(180deg,rgba(127,119,221,.16),rgba(212,83,126,.07));color:var(--purple-200);cursor:pointer}.music-player-handle:focus-visible{outline:2px solid color-mix(in srgb,var(--purple-200) 76%,transparent);outline-offset:-4px}.music-player-handle__disc{position:relative;display:grid;width:32px;height:32px;place-items:center;border:1px solid color-mix(in srgb,var(--purple-200) 42%,transparent);border-radius:50%;background:radial-gradient(circle,rgba(17,18,33,.72) 0 16%,rgba(164,154,255,.3) 17% 33%,rgba(31,30,54,.72) 34% 100%);box-shadow:0 6px 18px rgba(7,8,21,.24);font-size:13px}.music-player-handle__disc.is-playing{animation:music-disc-spin 9s linear infinite}
.lyric-fade-enter-active,.lyric-fade-leave-active{transition:opacity 160ms ease,transform 160ms ease}.lyric-fade-enter-from{opacity:0;transform:translateY(3px)}.lyric-fade-leave-to{opacity:0;transform:translateY(-2px)}
@keyframes music-disc-spin{to{transform:rotate(360deg)}}@keyframes music-cover-breathe{0%,100%{transform:scale(1)}50%{transform:scale(1.035)}}
@media (max-width:680px){.floating-music-player{width:min(306px,calc(100vw - 12px))}.music-player-shell{grid-template-columns:minmax(0,1fr) 44px;min-height:168px;border-radius:0 20px 20px 0;transform:translateX(calc(-100% + 44px))}.music-player-handle{width:44px}.music-player-panel{padding:12px 9px 11px 12px}.music-player-summary{grid-template-columns:52px minmax(0,1fr)}.music-player-cover{width:52px;height:52px}}
@media (prefers-reduced-motion:reduce){.music-player-shell,.music-player-panel,.lyric-fade-enter-active,.lyric-fade-leave-active{transition:none}.floating-music-player.is-expanded .music-player-panel{transition-delay:0}.music-player-handle__disc.is-playing,.music-player-cover.is-playing img,.music-player-cover.is-playing .music-player-cover__fallback{animation:none}}
</style>
