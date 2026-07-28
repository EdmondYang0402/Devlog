<template>
  <div class="wrap">
    <GlassPanel class="inner">
      <div class="hero-sec">
        <div class="av">🌸</div>
        <div class="name">Hathaway</div>
        <div class="sub">{{ t('about.role') }}</div>
        <div class="tags">
          <span v-for="t in tags" :key="t" class="tag">{{ t }}</span>
        </div>
      </div>

      <!-- 自我介绍：在这里填写 -->
      <div class="section">
        <div class="sec-title">{{ t('about.aboutMe') }}</div>
        <p class="placeholder">{{ t('about.introPending') }}</p>
      </div>

      <div class="section">
        <div class="sec-title">{{ t('about.techStack') }}</div>
        <div class="skills">
          <div v-for="s in skills" :key="s.name" class="skill">
            <span class="sname">{{ s.name }}</span>
            <div class="sbar"><div class="sfill" :style="{ width: s.level+'%', background: s.color }"></div></div>
            <span class="slevel">{{ s.level }}%</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="sec-title">{{ t('about.contactMe') }}</div>
        <div class="contacts">
          <a v-for="c in contacts" :key="c.label" :href="c.href" class="contact" target="_blank">
            <i :class="'ti ti-'+c.icon" aria-hidden="true"></i>
            <span class="cl">{{ c.label }}</span>
            <span class="cv">{{ c.value }}</span>
          </a>
        </div>
      </div>
    </GlassPanel>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import GlassPanel from '@/components/common/GlassPanel.vue'

const { t } = useI18n()
const tags    = ['Spring Boot','Java','Security','Redis','Vue3','Tokyo']
const skills  = [
  { name:'Java / Spring Boot', level:75, color:'#7F77DD' },
  { name:'Python',             level:80, color:'#1D9E75' },
  { name:'MySQL / Redis',      level:70, color:'#D4537E' },
  { name:'Vue 3',              level:60, color:'#AFA9EC' },
  { name:'Linux / DevOps',     level:55, color:'#F0997B' },
]
const contacts = [
  { label:'GitHub', icon:'brand-github', href:'https://github.com/', value:'@hathaway' },
  { label:'Email',  icon:'mail',         href:'mailto:you@example.com', value:'you@example.com' },
]
</script>

<style scoped>
.wrap { flex:1; padding:2.5rem 1.5rem 4rem; }
.inner { max-width:700px; margin:0 auto; padding:clamp(24px,5vw,48px); }

.hero-sec {
  display:flex; flex-direction:column; align-items:center;
  padding:.5rem 0 1.8rem; border-bottom:.5px solid var(--glass-border); margin-bottom:2rem;
}
.av {
  width:78px; height:78px; border-radius:50%;
  background:linear-gradient(135deg,var(--purple-400),var(--pink-400));
  display:flex; align-items:center; justify-content:center;
  font-size:34px; border:2px solid var(--purple-200);
  box-shadow:0 0 28px rgba(127,119,221,.2); margin-bottom:12px;
}
.name { font-size:22px; font-weight:500; margin-bottom:4px; }
.sub  { font-size:13px; color:var(--text-2); margin-bottom:12px; }
.tags { display:flex; flex-wrap:wrap; gap:6px; justify-content:center; }
.tag  { font-size:11px; padding:3px 10px; border-radius:20px; background:var(--purple-50); color:var(--purple-600); border:.5px solid var(--purple-200); }

.section { margin-bottom:2rem; }
.sec-title {
  font-size:11px; font-weight:500; color:var(--text-3);
  text-transform:uppercase; letter-spacing:.07em;
  padding-bottom:.5rem; margin-bottom:1rem;
  border-bottom:.5px solid var(--border);
}
.placeholder { font-size:14px; color:var(--text-3); font-style:italic; }

.skills { display:flex; flex-direction:column; gap:10px; }
.skill  { display:flex; align-items:center; gap:10px; }
.sname  { font-size:13px; color:var(--text-2); width:160px; flex-shrink:0; }
.sbar   { flex:1; height:5px; background:var(--border); border-radius:10px; overflow:hidden; }
.sfill  { height:100%; border-radius:10px; }
.slevel { font-size:11px; color:var(--text-3); width:32px; text-align:right; }

.contacts { display:flex; flex-direction:column; gap:8px; }
.contact {
  display:flex; align-items:center; gap:12px;
  padding:10px 14px; background:rgba(255,255,255,.16);
  border:1px solid var(--glass-border); border-radius:12px;
  transition:border-color .15s;
}
.contact:hover { border-color:var(--border-h); }
.contact i { font-size:17px; color:var(--purple-400); }
.cl { font-size:12px; color:var(--text-3); width:52px; }
.cv { font-size:13px; color:var(--purple-400); }
</style>
