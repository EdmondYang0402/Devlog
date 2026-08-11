<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'

const props = defineProps({
  active: { type: Boolean, default: false },
  label: { type: String, required: true }
})

const emit = defineEmits(['activate', 'deactivate', 'progress', 'dragging-change'])

const MAX_DRAG = 180
const TRIGGER_DISTANCE = 80
const RETURN_DURATION = 320
const CLICK_TOLERANCE = 6

const dragDistance = ref(0)
const dragging = ref(false)
const returning = ref(false)
const pointerId = ref(null)
let startY = 0
let returnTimer = null

const clamp = value => Math.min(MAX_DRAG, Math.max(0, value))
const pullProgress = computed(() => Math.min(1, dragDistance.value / TRIGGER_DISTANCE))
const hiddenProgress = computed(() => props.active ? 1 - pullProgress.value : pullProgress.value)
const controlStyle = computed(() => ({ '--pull-distance': `${dragDistance.value}px` }))

const clearReturnTimer = () => {
  if (returnTimer !== null) window.clearTimeout(returnTimer)
  returnTimer = null
}

const setDragging = value => {
  dragging.value = value
  emit('dragging-change', value)
}

const emitProgress = () => emit('progress', hiddenProgress.value)

const beginReturn = targetProgress => {
  clearReturnTimer()
  returning.value = true
  dragDistance.value = 0
  emit('progress', targetProgress)
  returnTimer = window.setTimeout(() => {
    returning.value = false
    setDragging(false)
    returnTimer = null
  }, RETURN_DURATION)
}

const handlePointerDown = event => {
  if (dragging.value || returning.value || !event.isPrimary) return
  clearReturnTimer()
  pointerId.value = event.pointerId
  startY = event.clientY
  dragDistance.value = 0
  event.currentTarget.setPointerCapture(event.pointerId)
  setDragging(true)
  emitProgress()
}

const handlePointerMove = event => {
  if (!dragging.value || event.pointerId !== pointerId.value) return
  dragDistance.value = clamp(event.clientY - startY)
  emitProgress()
}

const releasePointer = event => {
  pointerId.value = null
  if (event.currentTarget.hasPointerCapture?.(event.pointerId)) {
    event.currentTarget.releasePointerCapture(event.pointerId)
  }
}

const handlePointerUp = event => {
  if (!dragging.value || event.pointerId !== pointerId.value) return
  const distance = dragDistance.value
  releasePointer(event)
  event.currentTarget.blur()

  if (props.active && distance < CLICK_TOLERANCE) {
    dragDistance.value = 0
    emit('progress', 0)
    emit('deactivate')
    setDragging(false)
    return
  }

  if (distance >= TRIGGER_DISTANCE) {
    dragDistance.value = 0
    emit('progress', props.active ? 0 : 1)
    emit(props.active ? 'deactivate' : 'activate')
    setDragging(false)
    return
  }

  beginReturn(props.active ? 1 : 0)
}

const handlePointerCancel = event => {
  if (!dragging.value || event.pointerId !== pointerId.value) return
  releasePointer(event)
  beginReturn(props.active ? 1 : 0)
}

const handleLostPointerCapture = event => {
  if (!dragging.value || event.pointerId !== pointerId.value) return
  pointerId.value = null
  beginReturn(props.active ? 1 : 0)
}

const handleKeyboardClick = event => {
  if (event.detail !== 0 || dragging.value || returning.value) return
  emit('progress', props.active ? 0 : 1)
  emit(props.active ? 'deactivate' : 'activate')
}

watch(() => props.active, active => {
  if (dragging.value || returning.value) return
  dragDistance.value = 0
  emit('progress', active ? 1 : 0)
})

onBeforeUnmount(() => clearReturnTimer())
</script>

<template>
  <div
    class="curtain-pull"
    :class="{
      'is-active': active,
      'is-dragging': dragging,
      'is-returning': returning
    }"
    :style="controlStyle"
  >
    <span class="curtain-pull__chain" aria-hidden="true"></span>
    <span class="curtain-pull__link" aria-hidden="true"></span>
    <button
      class="curtain-pull__pendant"
      type="button"
      :aria-label="label"
      :title="label"
      @pointerdown="handlePointerDown"
      @pointermove="handlePointerMove"
      @pointerup="handlePointerUp"
      @pointercancel="handlePointerCancel"
      @lostpointercapture="handleLostPointerCapture"
      @click="handleKeyboardClick"
    >
      <svg viewBox="0 0 54 68" aria-hidden="true">
        <defs>
          <linearGradient id="curtain-metal" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stop-color="#f2f0f7" stop-opacity=".92" />
            <stop offset=".42" stop-color="#8d8a9a" stop-opacity=".9" />
            <stop offset=".7" stop-color="#e8e4ef" stop-opacity=".84" />
            <stop offset="1" stop-color="#6e6a7b" stop-opacity=".92" />
          </linearGradient>
          <linearGradient id="curtain-gem" x1=".15" y1="0" x2=".85" y2="1">
            <stop offset="0" stop-color="#d8d0ff" />
            <stop offset=".38" stop-color="#8171dc" />
            <stop offset="1" stop-color="#3e326f" />
          </linearGradient>
        </defs>
        <path d="M27 2 33 8 27 14 21 8Z" fill="url(#curtain-metal)" stroke="#f7f5fb" stroke-opacity=".62" />
        <path d="M27 13 42 24 38 45 27 64 16 45 12 24Z" fill="rgba(24,21,35,.72)" stroke="url(#curtain-metal)" stroke-width="2" />
        <path d="m27 19 9 9-3 16-6 12-6-12-3-16Z" fill="url(#curtain-gem)" stroke="#ded8ff" stroke-opacity=".72" />
        <path d="m27 20 3 25-3 10-3-10Z" fill="#f4f0ff" fill-opacity=".18" />
        <path d="M12 24 5 31l8 5M42 24l7 7-8 5" fill="none" stroke="url(#curtain-metal)" stroke-width="1.5" stroke-linecap="round" />
      </svg>
    </button>
  </div>
</template>

<style scoped>
.curtain-pull {
  position: fixed;
  top: 0;
  left: 50%;
  z-index: 140;
  width: 64px;
  height: calc(150px + var(--pull-distance));
  pointer-events: none;
  transform: translateX(-50%);
  opacity: .78;
  transition: opacity .2s ease;
}
.curtain-pull.is-active { opacity: .46; }
.curtain-pull:hover,.curtain-pull:focus-within,.curtain-pull.is-dragging { opacity: 1; }
.curtain-pull__chain {
  position: absolute;
  top: 0;
  left: 50%;
  width: 3px;
  height: calc(88px + var(--pull-distance));
  transform: translateX(-50%);
  background: linear-gradient(90deg,rgba(255,255,255,.16),rgba(242,240,248,.82) 45%,rgba(91,88,103,.58) 58%,rgba(255,255,255,.22));
  box-shadow: 0 0 5px rgba(222,216,244,.22);
}
.curtain-pull__chain::after {
  position:absolute;
  inset:0 -2px;
  content:'';
  background:repeating-linear-gradient(155deg,transparent 0 5px,rgba(255,255,255,.34) 6px 7px,transparent 8px 11px);
}
.curtain-pull__link {
  position:absolute;
  top:calc(81px + var(--pull-distance));
  left:50%;
  width:12px;
  height:12px;
  border:1px solid rgba(239,236,247,.74);
  border-radius:2px;
  background:rgba(80,75,95,.48);
  box-shadow:0 2px 8px rgba(24,20,40,.28);
  transform:translateX(-50%) rotate(45deg);
}
.curtain-pull__pendant {
  position:absolute;
  top:calc(88px + var(--pull-distance));
  left:50%;
  width:58px;
  height:68px;
  padding:0 2px;
  border:0;
  background:transparent;
  color:inherit;
  pointer-events:auto;
  cursor:grab;
  touch-action:none;
  user-select:none;
  transform:translateX(-50%);
  filter:drop-shadow(0 8px 10px rgba(20,15,38,.32));
}
.curtain-pull__pendant svg { display:block; width:100%; height:100%; overflow:visible; }
.curtain-pull__pendant:active,.is-dragging .curtain-pull__pendant { cursor:grabbing; }
.curtain-pull__pendant:focus-visible { outline:2px solid rgba(218,210,255,.88); outline-offset:1px; border-radius:16px; }
.curtain-pull.is-returning .curtain-pull__chain,
.curtain-pull.is-returning .curtain-pull__link,
.curtain-pull.is-returning .curtain-pull__pendant { transition:top 320ms cubic-bezier(.2,.75,.25,1),height 320ms cubic-bezier(.2,.75,.25,1); }
@media(max-width:680px){.curtain-pull{z-index:140}.curtain-pull__pendant{width:54px}}
@media(prefers-reduced-motion:reduce){.curtain-pull,.curtain-pull__chain,.curtain-pull__link,.curtain-pull__pendant{transition-duration:1ms!important}}
</style>
