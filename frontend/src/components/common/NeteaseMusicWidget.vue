<script setup>
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ArrowDown, ArrowUp, Headset } from '@element-plus/icons-vue'
import { createNeteasePlaylistUrl, neteaseMusicConfig } from '@/constants/music.js'

const { t } = useI18n()
const expanded = ref(false)
const hasLoaded = ref(false)
const unavailable = ref(false)

const playerUrl = computed(() => createNeteasePlaylistUrl())
const panelId = 'devlog-netease-player'

const togglePlayer = () => {
  if (!hasLoaded.value) hasLoaded.value = true
  expanded.value = !expanded.value
}

const markUnavailable = () => {
  unavailable.value = true
}
</script>

<template>
  <aside v-if="neteaseMusicConfig.enabled && playerUrl" class="music-widget" aria-live="polite">
    <div v-if="hasLoaded" class="music-widget__panel-host">
      <Transition name="music-panel">
        <section
          v-show="expanded"
          :id="panelId"
          class="music-widget__panel"
          :aria-label="t('music.playerLabel')"
        >
          <div v-if="unavailable" class="music-widget__unavailable">
            <Headset aria-hidden="true" />
            <span>{{ t('music.unavailable') }}</span>
          </div>
          <iframe
            v-else
            class="music-widget__iframe"
            :src="playerUrl"
            :title="t('music.playerLabel')"
            frameborder="0"
            scrolling="no"
            loading="lazy"
            allow="autoplay; encrypted-media"
            @error="markUnavailable"
          />
        </section>
      </Transition>
    </div>

    <button
      type="button"
      class="music-widget__trigger"
      :aria-expanded="expanded"
      :aria-controls="panelId"
      :aria-label="expanded ? t('music.collapse') : t('music.expand')"
      :title="expanded ? t('music.collapse') : t('music.expand')"
      @click="togglePlayer"
    >
      <span class="music-widget__icon" aria-hidden="true">
        <Headset />
      </span>
      <span class="music-widget__copy">
        <strong>{{ neteaseMusicConfig.title }}</strong>
      </span>
      <span class="music-widget__toggle" aria-hidden="true">
        <ArrowDown v-if="expanded" />
        <ArrowUp v-else />
      </span>
    </button>
  </aside>
</template>

<style scoped>
.music-widget {
  position: fixed;
  left: 24px;
  bottom: calc(32px + env(safe-area-inset-bottom, 0px));
  z-index: 95;
  display: flex;
  width: min(352px, calc(100vw - 48px));
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.music-widget__panel-host,
.music-widget__panel {
  width: 100%;
}

.music-widget__panel {
  overflow: hidden;
  border: 1px solid color-mix(in srgb, var(--glass-border) 92%, transparent);
  border-radius: 20px;
  background: color-mix(in srgb, var(--glass-bg) 92%, rgba(18, 19, 34, .46));
  box-shadow: 0 16px 42px rgba(9, 10, 24, .24), inset 0 1px 0 rgba(255, 255, 255, .1);
  -webkit-backdrop-filter: blur(16px) saturate(112%);
  backdrop-filter: blur(16px) saturate(112%);
}

.music-widget__iframe {
  display: block;
  width: 100%;
  height: min(450px, calc(100vh - 148px));
  border: 0;
  background: transparent;
}

.music-widget__unavailable {
  display: flex;
  min-height: 112px;
  align-items: center;
  justify-content: center;
  gap: 9px;
  padding: 20px;
  color: var(--glass-muted);
  font-size: 13px;
}

.music-widget__unavailable svg {
  width: 20px;
  height: 20px;
}

.music-widget__trigger {
  display: grid;
  grid-template-columns: 32px minmax(0, 1fr) 20px;
  width: 138px;
  min-height: 48px;
  align-items: center;
  gap: 8px;
  padding: 7px 9px;
  border: 1px solid color-mix(in srgb, var(--glass-border) 92%, transparent);
  border-radius: 18px;
  background: color-mix(in srgb, var(--glass-bg) 94%, rgba(18, 19, 34, .4));
  box-shadow: 0 12px 32px rgba(9, 10, 24, .2), inset 0 1px 0 rgba(255, 255, 255, .12);
  color: var(--glass-text);
  font: inherit;
  text-align: left;
  cursor: pointer;
  -webkit-backdrop-filter: blur(15px) saturate(112%);
  backdrop-filter: blur(15px) saturate(112%);
  transition: background-color 190ms ease, border-color 190ms ease, box-shadow 190ms ease, transform 190ms ease;
}

.music-widget__trigger:hover {
  border-color: color-mix(in srgb, var(--purple-200) 42%, transparent);
  background: color-mix(in srgb, var(--glass-bg) 88%, var(--purple-800) 12%);
  box-shadow: 0 14px 34px rgba(9, 10, 24, .26), inset 0 1px 0 rgba(255, 255, 255, .14);
  transform: translateY(-1px);
}

.music-widget__trigger:focus-visible {
  outline: 2px solid color-mix(in srgb, var(--purple-200) 78%, transparent);
  outline-offset: 3px;
}

.music-widget__icon {
  display: grid;
  width: 32px;
  height: 32px;
  place-items: center;
  border: 1px solid color-mix(in srgb, var(--purple-200) 28%, transparent);
  border-radius: 13px;
  background: linear-gradient(145deg, rgba(164, 154, 255, .2), rgba(212, 83, 126, .1));
  color: var(--purple-200);
}

.music-widget__icon svg {
  width: 17px;
  height: 17px;
}

.music-widget__copy {
  display: flex;
  min-width: 0;
  line-height: 1.35;
}

.music-widget__copy strong {
  overflow: hidden;
  font-size: 12px;
  font-weight: 680;
  letter-spacing: .025em;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.music-widget__toggle {
  display: grid;
  width: 20px;
  height: 20px;
  place-items: center;
  border: 1px solid color-mix(in srgb, var(--glass-border) 82%, transparent);
  border-radius: 50%;
  color: var(--glass-muted);
}

.music-widget__toggle svg {
  width: 11px;
  height: 11px;
}

.music-panel-enter-active,
.music-panel-leave-active {
  transition: opacity 210ms ease, transform 210ms ease;
}

.music-panel-enter-from,
.music-panel-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}

@media (max-width: 640px) {
  .music-widget {
    left: 16px;
    bottom: calc(22px + env(safe-area-inset-bottom, 0px));
    width: min(360px, calc(100vw - 32px));
  }

  .music-widget__trigger {
    width: 44px;
    min-height: 44px;
    grid-template-columns: 1fr;
    padding: 5px;
    border-radius: 14px;
  }

  .music-widget__icon {
    width: 34px;
    height: 34px;
    border-radius: 11px;
  }

  .music-widget__copy,
  .music-widget__toggle {
    display: none;
  }

  .music-widget__iframe {
    height: min(450px, calc(100vh - 128px));
  }
}

@media (prefers-reduced-motion: reduce) {
  .music-widget__trigger,
  .music-panel-enter-active,
  .music-panel-leave-active {
    transition: none;
  }

  .music-widget__trigger:hover {
    transform: none;
  }
}
</style>
