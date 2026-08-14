<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useSeasonEffect } from '@/composables/useSeasonEffect.js'

const { t } = useI18n()
const { selectedEffect, reducedMotion, setEffect } = useSeasonEffect()
const options = computed(() => [
  { value: 'none', icon: 'ti-ban', label: t('seasonEffect.none') },
  { value: 'sakura', icon: 'ti-flower', label: t('seasonEffect.sakura') },
  { value: 'rain', icon: 'ti-cloud-rain', label: t('seasonEffect.rain') },
  { value: 'leaf', icon: 'ti-leaf', label: t('seasonEffect.leaf') },
  { value: 'snow', icon: 'ti-snowflake', label: t('seasonEffect.snow') }
])
const currentOption = computed(() => options.value.find(option => option.value === selectedEffect.value) || options.value[0])
const selectorTitle = computed(() => reducedMotion.value && selectedEffect.value !== 'none'
  ? t('seasonEffect.reducedMotion')
  : `${t('seasonEffect.label')}：${currentOption.value.label}`)
</script>

<template>
  <el-dropdown trigger="click" placement="bottom-end" popper-class="season-effect-menu-popper" @command="setEffect">
    <button class="season-effect-selector" type="button" :class="{ 'has-effect': selectedEffect !== 'none' }" :aria-label="selectorTitle" :title="selectorTitle">
      <i class="ti" :class="currentOption.icon" aria-hidden="true"></i>
      <span class="season-effect-selector__label">{{ t('seasonEffect.shortLabel') }}</span>
      <i class="ti ti-chevron-down season-effect-selector__chevron" aria-hidden="true"></i>
    </button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item v-if="reducedMotion" disabled class="season-effect-menu__note">{{ t('seasonEffect.reducedMotion') }}</el-dropdown-item>
        <el-dropdown-item v-for="option in options" :key="option.value" :command="option.value" :class="{ 'is-season-selected': selectedEffect === option.value }">
          <i class="ti" :class="option.icon" aria-hidden="true"></i>
          <span>{{ option.label }}</span>
          <i v-if="selectedEffect === option.value" class="ti ti-check season-effect-menu__check" aria-hidden="true"></i>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped>
.season-effect-selector{display:inline-flex;align-items:center;justify-content:center;gap:6px;height:36px;padding:0 11px;border:1px solid var(--border);border-radius:999px;background:rgba(255,255,255,.16);color:var(--text-2);cursor:pointer;font-family:inherit;font-size:12px;white-space:nowrap;transition:color .24s ease,border-color .24s ease,background-color .24s ease,box-shadow .24s ease}
.season-effect-selector:hover,.season-effect-selector:focus-visible,.season-effect-selector.has-effect{color:var(--purple-400);border-color:var(--border-h);background:color-mix(in srgb,var(--purple-50) 74%,transparent)}
.season-effect-selector:focus-visible{outline:none;box-shadow:0 0 0 3px color-mix(in srgb,var(--purple-100) 24%,transparent)}
.season-effect-selector>.ti:first-child{font-size:16px}.season-effect-selector__chevron{font-size:11px;opacity:.62}
:global(.season-effect-menu-popper .el-dropdown-menu){min-width:166px;padding:6px}
:global(.season-effect-menu-popper .el-dropdown-menu__item){gap:9px;border-radius:7px}
:global(.season-effect-menu-popper .el-dropdown-menu__item .ti){width:16px;color:var(--text-3);font-size:15px}
:global(.season-effect-menu-popper .el-dropdown-menu__item.is-season-selected){color:var(--purple-400);background:var(--purple-50)}
:global(.season-effect-menu-popper .el-dropdown-menu__item.is-season-selected .ti){color:currentColor}
:global(.season-effect-menu-popper .season-effect-menu__check){margin-left:auto}
:global(.season-effect-menu-popper .season-effect-menu__note){max-width:230px;height:auto;padding-block:7px;color:var(--text-3);font-size:11px;line-height:1.45;white-space:normal}
@media (max-width:960px){.season-effect-selector{width:36px;padding:0}.season-effect-selector__label,.season-effect-selector__chevron{display:none}}
@media (max-width:680px){.season-effect-selector{width:34px;height:34px}}
</style>
