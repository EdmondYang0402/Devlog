<template>
    <!-- ✅ 添加外层容器实现居中 -->
    <div class="page-wrapper">
        <div class="pwd-wrap">
            <el-form ref="formRef" :model="pwdData" :rules="rules" label-position="top" class="pwd-form">

                <el-form-item label="原密码" prop="oldPassword">
                    <el-input v-model="pwdData.oldPassword" type="password" show-password
                        placeholder="请输入原密码" class="field-input" />
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="pwdData.newPassword" type="password" show-password
                        placeholder="5~16 位非空字符" class="field-input"
                        @input="calcStrength(pwdData.newPassword)" />
                    <!-- 强度条 -->
                    <div v-if="pwdData.newPassword" class="strength-bar">
                        <div v-for="i in 3" :key="i" class="strength-seg"
                            :style="{ background: i <= strength ? strengthColors[strength] : '#eee' }"></div>
                    </div>
                    <p v-if="pwdData.newPassword" class="strength-label"
                        :style="{ color: strengthColors[strength] }">
                        {{ strengthText[strength] }}
                    </p>
                </el-form-item>

                <el-form-item label="确认新密码" prop="repeatedPassword">
                    <el-input v-model="pwdData.repeatedPassword" type="password" show-password
                        placeholder="请再次输入新密码" class="field-input" />
                </el-form-item>

                <div class="info-tip">
                    修改密码后将退出登录，需重新登录。
                </div>

                <div class="action-row">
                    <button class="btn-save" type="button" @click="submitResetPwd">确认修改</button>
                    <button class="btn-cancel" type="button" @click="router.back()">取消</button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import ElMessage from 'element-plus/es/components/message/index.mjs'
import { userUpdatePasswordService } from '@/api/user.js'

const router = useRouter()

const pwdData = ref({ oldPassword: '', newPassword: '', repeatedPassword: '' })

const checkRePwd = (rule, value, callback) => {
    if (value !== pwdData.value.newPassword) {
        callback(new Error('两次填写的新密码不一致'))
    } else {
        callback()
    }
}

const rules = {
    oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 5, max: 16, message: '长度为 5~16 位', trigger: 'blur' }
    ],
    repeatedPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        { validator: checkRePwd, trigger: 'blur' }
    ]
}

const formRef = ref(null)

const strength = ref(0)
const strengthText = ['', '强度：弱', '强度：中', '强度：强']
const strengthColors = ['', '#E24B4A', '#EF9F27', '#1D9E75']

const calcStrength = (val) => {
    let s = 0
    if (val.length >= 5) s++
    if (/[A-Z]/.test(val) || /[0-9]/.test(val)) s++
    if (/[^A-Za-z0-9]/.test(val) && val.length >= 8) s++
    strength.value = s
}

const submitResetPwd = async () => {
    await formRef.value.validate()
    await userUpdatePasswordService(pwdData.value)
    ElMessage.success('密码修改成功，请重新登录')
    router.push('/login')
}
</script>

<style scoped>
/* ✅ 新增：页面居中 */
.page-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: #f8f6f3;
}

.pwd-wrap {
    max-width: 420px;
    width: 100%;
    padding: 2rem;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.pwd-form :deep(.el-form-item__label) {
    font-size: 13px;
    color: #888;
    font-weight: 500;
}

.field-input {
    width: 100%;
}

.strength-bar {
    display: flex;
    gap: 4px;
    margin-top: 8px;
    width: 100%;
}

.strength-seg {
    flex: 1;
    height: 3px;
    border-radius: 2px;
    transition: background 0.2s;
}

.strength-label {
    font-size: 12px;
    margin: 4px 0 0;
    transition: color 0.2s;
}

.info-tip {
    font-size: 12px;
    color: #bbb;
    margin: 0.5rem 0 1.5rem;
    padding: 10px 12px;
    background: #fafafa;
    border-left: 2px solid rgba(255, 182, 193, 0.6);
    border-radius: 0 6px 6px 0;
}

.action-row {
    display: flex;
    gap: 10px;
}

.btn-save {
    padding: 8px 28px;
    font-size: 14px;
    border-radius: 8px;
    background: linear-gradient(135deg, rgba(255, 182, 193, 0.3), rgba(180, 160, 220, 0.3));
    border: 0.5px solid rgba(255, 182, 193, 0.5);
    color: #333;
    cursor: pointer;
    transition: all 0.2s;
    letter-spacing: 0.04em;
}

.btn-save:hover {
    background: linear-gradient(135deg, rgba(255, 182, 193, 0.5), rgba(180, 160, 220, 0.5));
    transform: translateY(-1px);
}

.btn-save:active {
    transform: scale(0.98);
}

.btn-cancel {
    padding: 8px 20px;
    font-size: 14px;
    border-radius: 8px;
    border: 0.5px solid #ddd;
    background: transparent;
    color: #888;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-cancel:hover {
    background: #f5f5f5;
}
</style>
