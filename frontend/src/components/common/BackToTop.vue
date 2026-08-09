<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ArrowUp } from '@element-plus/icons-vue'

const props = defineProps({
  threshold: {
    type: Number,
    default: 500
  }
})

const { t } = useI18n()
const visible = ref(false)

const getScrollTop = () => (
  window.scrollY
  || document.documentElement.scrollTop
  || document.body.scrollTop
  || 0
)

const updateVisibility = () => {
  visible.value = getScrollTop() > props.threshold
}

const scrollToTop = () => {
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  window.scrollTo({
    top: 0,
    behavior: reduceMotion ? 'auto' : 'smooth'
  })
}

onMounted(() => {
  updateVisibility()
  window.addEventListener('scroll', updateVisibility, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateVisibility)
})
</script>

<template>
  <Transition name="back-to-top">
    <button
      v-if="visible"
      type="button"
      class="back-to-top"
      :aria-label="t('common.backToTop')"
      :title="t('common.backToTop')"
      @click="scrollToTop"
    >
      <ArrowUp aria-hidden="true" />
    </button>
  </Transition>
</template>

<style scoped>
.back-to-top {
  position: fixed;
  right: 32px;
  bottom: calc(32px + env(safe-area-inset-bottom, 0px));
  z-index: 90;
  display: grid;
  place-items: center;
  width: 46px;
  height: 46px;
  padding: 0;
  border: 1px solid rgba(202, 201, 238, .24);
  border-radius: 50%;
  background: rgba(22, 24, 40, .68);
  box-shadow: 0 10px 28px rgba(8, 10, 22, .24);
  color: rgba(239, 237, 255, .92);
  cursor: pointer;
  -webkit-backdrop-filter: blur(14px) saturate(120%);
  backdrop-filter: blur(14px) saturate(120%);
  transition: background-color 180ms ease, border-color 180ms ease, box-shadow 180ms ease;
}

.back-to-top svg {
  width: 18px;
  height: 18px;
  transition: transform 180ms ease;
}

.back-to-top:hover {
  background: rgba(52, 50, 82, .82);
  border-color: rgba(175, 169, 236, .46);
  box-shadow: 0 12px 30px rgba(8, 10, 22, .3);
}

.back-to-top:hover svg { transform: translateY(-2px); }

.back-to-top:focus-visible {
  outline: 2px solid rgba(175, 169, 236, .78);
  outline-offset: 3px;
}

.back-to-top-enter-active,
.back-to-top-leave-active {
  transition: opacity 200ms ease, transform 200ms ease;
}

.back-to-top-enter-from,
.back-to-top-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

@media (max-width: 640px) {
  .back-to-top {
    right: 18px;
    bottom: calc(22px + env(safe-area-inset-bottom, 0px));
    width: 42px;
    height: 42px;
  }

  .back-to-top svg { width: 17px; height: 17px; }
}

@media (prefers-reduced-motion: reduce) {
  .back-to-top,
  .back-to-top svg,
  .back-to-top-enter-active,
  .back-to-top-leave-active {
    transition: none;
  }
}
</style>
