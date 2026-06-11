<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
    </div>

    <div class="login-card">
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="28" height="28">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
        </div>
        <h1 class="brand-title">超市进销存管理系统</h1>
        <p class="brand-subtitle">登录以继续使用</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" class="login-form">
        <el-form-item prop="eId">
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <el-input
              v-model="form.eId"
              placeholder="请输入工号"
              size="large"
              :prefix-icon="null"
            />
          </div>
        </el-form-item>

        <el-form-item prop="ePassword">
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <el-input
              v-model="form.ePassword"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="null"
              show-password
              @keyup.enter="handleLogin"
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>超市进销存管理系统 v1.0</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { login } from '../../api/employee'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  eId: '',
  ePassword: ''
})

const rules = {
  eId: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  ePassword: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form)
    store.setUser(res.data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    // error handled in interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f172a;
  position: relative;
  overflow: hidden;
}

/* Animated background orbs */
.login-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: orbFloat 20s ease-in-out infinite;
}

.bg-orb-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  top: -200px;
  left: -200px;
  animation-delay: 0s;
}

.bg-orb-2 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  bottom: -150px;
  right: -150px;
  animation-delay: -7s;
}

.bg-orb-3 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #ec4899, #8b5cf6);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
  opacity: 0.2;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.05); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(40px, 30px) scale(1.02); }
}

/* Card */
.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 20px;
  box-shadow:
    0 0 0 1px rgba(255, 255, 255, 0.1),
    0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(20px);
}

.login-brand {
  text-align: center;
  margin-bottom: 36px;
}

.brand-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}

.brand-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px;
  letter-spacing: 0.3px;
}

.brand-subtitle {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

/* Form */
.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  color: #94a3b8;
  font-size: 16px;
  pointer-events: none;
}

.input-wrapper :deep(.el-input__wrapper) {
  padding-left: 42px !important;
  height: 48px;
  border-radius: 12px !important;
  box-shadow: 0 0 0 1.5px #e2e8f0 inset !important;
  background: #f8fafc;
  transition: all 0.2s ease;
}

.input-wrapper :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1.5px #cbd5e1 inset !important;
  background: #fff;
}

.input-wrapper :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #6366f1 inset !important;
  background: #fff;
}

.input-wrapper :deep(.el-input__inner) {
  font-size: 14px;
  height: 48px;
}

.input-wrapper :deep(.el-input__suffix) {
  right: 8px;
}

.login-btn {
  width: 100%;
  height: 48px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
  letter-spacing: 2px;
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.3) !important;
  transition: all 0.2s ease !important;
}

.login-btn:hover {
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4) !important;
  transform: translateY(-1px);
}

.login-btn:active {
  transform: translateY(0);
}

/* Footer */
.login-footer {
  text-align: center;
  font-size: 12px;
  color: #cbd5e1;
}
</style>
