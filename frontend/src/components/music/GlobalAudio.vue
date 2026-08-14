<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useMusicPlayer } from '@/composables/useMusicPlayer.js'

const audioRef = ref(null)
const audioInstanceId = globalThis.crypto?.randomUUID?.() || `audio-${Date.now()}`
const {
  attachAudio,
  detachAudio,
  syncMetadata,
  syncTime,
  handlePlay,
  handlePause,
  handleEnded,
  handleError
} = useMusicPlayer()

onMounted(() => attachAudio(audioRef.value))
onBeforeUnmount(() => detachAudio(audioRef.value))
</script>

<template>
  <audio
    id="global-music-audio"
    ref="audioRef"
    class="global-music-audio"
    :data-audio-instance="audioInstanceId"
    preload="metadata"
    playsinline
    @loadedmetadata="syncMetadata"
    @durationchange="syncMetadata"
    @timeupdate="syncTime"
    @play="handlePlay"
    @pause="handlePause"
    @ended="handleEnded"
    @error="handleError"
  ></audio>
</template>

<style scoped>
.global-music-audio { display: none; }
</style>
