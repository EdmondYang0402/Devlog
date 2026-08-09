<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { userLoginService } from '@/api/user.js'
import { useStarryBackground } from '@/composables/useStarryBackground.js'
import { useCurrentUser } from '@/composables/useCurrentUser.js'

const router = useRouter()
const { t } = useI18n()
const { setToken, loadCurrentUser } = useCurrentUser()

useStarryBackground('bg-canvas', 'login-scene')

const loginData = ref({ username: '', password: '' })

const rules = computed(() => ({
    username: [{ required: true, message: t('auth.requiredUsername'), trigger: 'blur' }],
    password: [
        { required: true, message: t('auth.requiredPassword'), trigger: 'blur' },
        { min: 5, max: 16, message: t('auth.invalidLength'), trigger: 'blur' }
    ]
}))

const formRef = ref(null)

const login = async () => {
  try {
    await formRef.value.validate()
    const result = await userLoginService(loginData.value)
    
    if (result.code === 200) {
      setToken(result.data.token)
      localStorage.setItem('username', result.data.username || loginData.value.username)

      try {
        await loadCurrentUser()
      } catch (error) {
        console.error('登录后加载当前用户信息失败', error)
      }
      
      ElMessage.success(result.msg || t('auth.loginSuccess'))
      router.push('/')
    } else {
      ElMessage.error(result.msg || t('auth.loginFailed'))
    }
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data?.msg || t('auth.requestFailed'))
    }
  }
}

</script>

<template>
    <div id="login-scene" class="scene scene--background-carousel">
        <canvas id="bg-canvas" class="bg-canvas"></canvas>
        <div class="card">
            <div class="logo">
                <span class="logo-title">Hathaway's Blog</span>
                <p class="logo-sub">{{ t('auth.welcomeBack') }}</p>
            </div>
            <el-form ref="formRef" :model="loginData" :rules="rules" label-position="top" class="auth-form" @submit.prevent="login">
                <el-form-item :label="t('auth.username')" prop="username">
                    <el-input v-model="loginData.username" :placeholder="t('auth.usernamePlaceholder')" class="dark-input" />
                </el-form-item>
                <el-form-item :label="t('auth.password')" prop="password">
                    <el-input v-model="loginData.password" type="password" show-password
                        :placeholder="t('auth.passwordPlaceholder')" class="dark-input" />
                </el-form-item>
                
                <!-- ✅ 新增：忘记密码链接 -->
                <div style="text-align: right; margin: -8px 0 12px 0;">
                    <a @click="router.push('/user-reset-password')" 
                       style="color: #8b8b8b; font-size: 13px; cursor: pointer; text-decoration: none;">
                        {{ t('auth.forgotPassword') }}
                    </a>
                </div>
                
                <button class="btn-submit" type="submit">{{ t('auth.login') }}</button>
            </el-form>
            <div class="footer-link">
                {{ t('auth.noAccount') }}<a @click="router.push('/register')">{{ t('auth.createOne') }}</a>
            </div>
        </div>
    </div>
</template>

<style scoped src="@/assets/css/auth-shared.css"></style>
