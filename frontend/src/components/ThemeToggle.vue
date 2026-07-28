<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useTheme } from '@/composables/useTheme.js'

const { isDark, toggleTheme } = useTheme()
const { t } = useI18n()
const label = computed(() => t(isDark.value ? 'theme.toLight' : 'theme.toDark'))
</script>

<template>
  <button
    class="theme-toggle"
    :class="{ 'is-dark': isDark }"
    type="button"
    :aria-label="label"
    :title="label"
    @click="toggleTheme"
  >
    <span class="cloud cloud-one"></span>
    <span class="cloud cloud-two"></span>
    <span class="stars"><i></i><i></i><i></i></span>
    <span class="theme-thumb">
      <span class="sun-rays"></span>
      <span class="moon-crater crater-one"></span>
      <span class="moon-crater crater-two"></span>
    </span>
  </button>
</template>

<style scoped>
.theme-toggle {
  position: relative; width: 58px; height: 29px; padding: 0;
  overflow: hidden; flex: 0 0 auto; cursor: pointer;
  border: 1px solid rgba(126,177,224,.55); border-radius: 999px;
  background: linear-gradient(160deg,#8fd2f5,#cbeeff);
  box-shadow: inset 0 1px 4px rgba(38,105,151,.18),0 2px 7px rgba(83,74,183,.13);
  transition: background-color .3s ease,border-color .3s ease,box-shadow .3s ease;
}
.theme-toggle:focus-visible { outline: 2px solid var(--accent-color); outline-offset: 2px; }
.theme-thumb {
  position: absolute; left: 3px; top: 3px; z-index: 3;
  width: 21px; height: 21px; border-radius: 50%;
  background: #ffd65a; box-shadow: 0 0 8px rgba(255,214,90,.75);
  transition: transform .35s cubic-bezier(.4,0,.2,1),background-color .3s ease,box-shadow .3s ease;
}
.sun-rays { position:absolute; inset:-3px; border:1px dotted rgba(255,239,154,.8); border-radius:50%; }
.cloud { position:absolute; z-index:2; height:4px; border-radius:8px; background:rgba(255,255,255,.88); transition:opacity .25s ease,transform .35s ease; }
.cloud::before,.cloud::after { content:''; position:absolute; bottom:0; border-radius:50%; background:inherit; }
.cloud::before { width:7px; height:7px; left:4px; }
.cloud::after { width:5px; height:5px; left:10px; }
.cloud-one { width:17px; right:5px; bottom:7px; }
.cloud-two { width:12px; right:14px; top:7px; opacity:.75; }
.stars { opacity:0; transition:opacity .25s ease; }
.stars i { position:absolute; z-index:2; width:2px; height:2px; border-radius:50%; background:#fff; box-shadow:0 0 3px #fff; }
.stars i:nth-child(1) { left:8px; top:7px; }
.stars i:nth-child(2) { left:17px; top:15px; }
.stars i:nth-child(3) { left:26px; top:6px; }
.moon-crater { position:absolute; opacity:0; border-radius:50%; background:rgba(133,143,177,.38); transition:opacity .25s ease; }
.crater-one { width:6px; height:6px; left:4px; top:5px; }
.crater-two { width:4px; height:4px; right:4px; bottom:4px; }
.theme-toggle.is-dark { border-color:rgba(118,126,185,.6); background:linear-gradient(155deg,#202947,#10162c); box-shadow:inset 0 1px 5px rgba(0,0,0,.4),0 2px 8px rgba(0,0,0,.24); }
.theme-toggle.is-dark .theme-thumb { transform:translateX(29px); background:#e7e9f5; box-shadow:0 0 8px rgba(210,216,246,.55); }
.theme-toggle.is-dark .sun-rays { opacity:0; }
.theme-toggle.is-dark .cloud { opacity:.18; transform:translateX(-8px); }
.theme-toggle.is-dark .stars,.theme-toggle.is-dark .moon-crater { opacity:1; }
@media (max-width:680px) {
  .theme-toggle { width:50px; height:26px; }
  .theme-thumb { width:18px; height:18px; }
  .theme-toggle.is-dark .theme-thumb { transform:translateX(25px); }
}
</style>
