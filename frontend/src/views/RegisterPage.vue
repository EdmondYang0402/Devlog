<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { userRegisterService } from '@/api/user.js'
import { useStarryBackground } from '@/composables/useStarryBackground.js'

const router = useRouter()
const { t } = useI18n()
const registerDebugEnabled = import.meta.env.DEV

useStarryBackground('bg-canvas', 'register-scene')

const registerData = ref({ email: '', username: '', password: '', rePassword: '' })
const submitting = ref(false)

const checkRePassword = (rule, value, callback) => {
    if (value !== registerData.value.password) {
        callback(new Error(t('auth.passwordMismatch')))
    } else {
        callback()
    }
}

const rules = computed(() => ({
    email: [
    { required: true, message: t('auth.requiredEmail'), trigger: 'blur' },
    { type: 'email', message: t('auth.invalidEmail'), trigger: 'blur' }
],
    username: [
        { required: true, message: t('auth.requiredUsername'), trigger: 'blur' },
        { min: 5, max: 16, message: t('auth.invalidLength'), trigger: 'blur' }
    ],
    password: [
        { required: true, message: t('auth.requiredPassword'), trigger: 'blur' },
        { min: 5, max: 16, message: t('auth.invalidLength'), trigger: 'blur' }
    ],
    rePassword: [
        { required: true, message: t('auth.requiredConfirmPassword'), trigger: 'blur' },
        { validator: checkRePassword, trigger: 'blur' }
    ]
}))

const formRef = ref(null)

const register = async () => {
    if (submitting.value) return

    if (registerDebugEnabled) console.log('[register] submit triggered')
    submitting.value = true

    try {
        const valid = await formRef.value.validate().catch(() => false)
        if (!valid) {
            if (registerDebugEnabled) console.warn('[register] validation failed')
            ElMessage.warning(t('auth.formInvalid'))
            return
        }

        const payload = {
            username: registerData.value.username.trim(),
            email: registerData.value.email.trim(),
            password: registerData.value.password
        }

        // 开发日志只记录字段名和非敏感标识，严禁输出密码。
        if (registerDebugEnabled) {
            console.log('[register] request POST /api/user/register', {
                fields: Object.keys(payload),
                username: payload.username,
                email: payload.email
            })
        }

        const response = await userRegisterService(payload)
        if (registerDebugEnabled) {
            console.log('[register] response', {
                code: response?.code,
                message: response?.message
            })
        }

        if (response?.code !== 200) {
            throw new Error(response?.message || t('auth.registerFailed'))
        }

        ElMessage.success(t('auth.registerSuccess'))
        router.push('/login')
    } catch (error) {
        const message =
            error?.response?.data?.message ||
            error?.response?.data?.msg ||
            error?.message ||
            t('auth.registerFailed')

        // AxiosError 可能包含原始请求体，因此只输出脱敏后的诊断字段。
        if (registerDebugEnabled) {
            console.error('[register] failed', {
                status: error?.response?.status,
                data: error?.response?.data,
                message
            })
        }
        ElMessage.error(message)
    } finally {
        submitting.value = false
    }
}
</script>

<template>
    <div id="register-scene" class="scene">
        <canvas id="bg-canvas" class="bg-canvas"></canvas>
        <div class="card">
            <div class="logo">
                <span class="logo-mark">✦</span>
                <span class="logo-title">Hathaway's Blog</span>
                <p class="logo-sub">{{ t('auth.createAccount') }}</p>
            </div>
            <el-form ref="formRef" :model="registerData" :rules="rules" label-position="top" class="auth-form" @submit.prevent="register">
                <el-form-item :label="t('auth.email')" prop="email">
                    <el-input v-model="registerData.email" :placeholder="t('auth.emailPlaceholder')" class="dark-input"/>
                </el-form-item>
                <el-form-item :label="t('auth.username')" prop="username">
                    <el-input v-model="registerData.username" :placeholder="t('auth.lengthHint')" class="dark-input" />
                </el-form-item>
                <el-form-item :label="t('auth.password')" prop="password">
                    <el-input v-model="registerData.password" type="password" show-password
                        :placeholder="t('auth.lengthHint')" class="dark-input" />
                </el-form-item>
                <el-form-item :label="t('auth.confirmPassword')" prop="rePassword">
                    <el-input v-model="registerData.rePassword" type="password" show-password
                        :placeholder="t('auth.confirmPasswordPlaceholder')" class="dark-input" />
                </el-form-item>
                <button class="btn-submit" type="submit" :disabled="submitting">
                    {{ submitting ? t('auth.registering') : t('auth.register') }}
                </button>
            </el-form>
            <div class="footer-link">
                {{ t('auth.hasAccount') }}<a @click="router.push('/login')">{{ t('auth.loginNow') }}</a>
            </div>
        </div>
    </div>
</template>

<style scoped src="@/assets/css/auth-shared.css"></style>
