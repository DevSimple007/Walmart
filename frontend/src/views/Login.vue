<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="login-bg">
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
    </div>

    <!-- 登录表单 -->
    <div class="login-form">
      <div class="login-header">
        <img src="https://www.walmart.com/favicon.ico" alt="Walmart" class="login-logo" />
        <h2>沃尔玛OMS系统</h2>
        <p>欢迎回来，请登录您的账户</p>
      </div>

      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            class="login-input"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            class="login-input"
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <div class="form-footer">
            <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住我</el-checkbox>
            <a href="#" class="forgot-password">忘记密码？</a>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleLogin"
            class="login-button"
            :loading="loading"
            size="large"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import axios from '../utils/axios'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  rememberMe: false
})

const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await axios.post('/auth/login', loginForm)
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data.userInfo)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        ElMessage.error('登录失败，请检查用户名和密码')
        console.error('登录失败', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  overflow: hidden;
}

/* 动态背景 */
.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0071dc 0%, #004a9e 50%, #002d6b 100%);
  z-index: 0;
}

.bg-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -80px;
  left: -80px;
  animation-delay: 0s;
}

.shape-2 {
  width: 220px;
  height: 220px;
  bottom: -60px;
  right: -60px;
  animation-delay: 2s;
}

.shape-3 {
  width: 160px;
  height: 160px;
  top: 50%;
  left: 8%;
  animation-delay: 4s;
}

.shape-4 {
  width: 120px;
  height: 120px;
  bottom: 20%;
  right: 15%;
  animation-delay: 6s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 登录表单 */
.login-form {
  position: relative;
  width: 420px;
  max-width: 90vw;
  padding: 40px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--wm-radius-xl);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  z-index: 1;
  transition: all var(--wm-transition-normal);
}

.login-form:hover {
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.18);
  transform: translateY(-4px);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(0, 113, 220, 0.25);
}

.login-header h2 {
  font-size: var(--wm-text-2xl);
  font-weight: 700;
  color: var(--wm-primary);
  margin-bottom: var(--wm-space-sm);
}

.login-header p {
  font-size: var(--wm-text-sm);
  color: var(--wm-text-secondary);
  margin: 0;
}

.login-input {
  border-radius: var(--wm-radius-sm);
}

.login-input :deep(.el-input__wrapper) {
  border-radius: var(--wm-radius-sm);
  box-shadow: 0 0 0 1px var(--wm-border);
  transition: all var(--wm-transition-fast);
  padding: 8px 12px;
}

.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--wm-primary);
}

.login-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px var(--wm-primary-glow), 0 0 0 1px var(--wm-primary);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--wm-space-md);
  width: 100%;
}

.remember-me {
  font-size: var(--wm-text-sm);
  color: var(--wm-text-secondary);
}

.forgot-password {
  font-size: var(--wm-text-sm);
  color: var(--wm-primary);
  text-decoration: none;
  transition: color var(--wm-transition-fast);
}

.forgot-password:hover {
  color: var(--wm-primary-hover);
  text-decoration: underline;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: var(--wm-text-base);
  font-weight: 600;
  border-radius: var(--wm-radius-sm);
  background: linear-gradient(135deg, #0071dc, #0056b3);
  border: none;
  transition: all var(--wm-transition-fast);
  letter-spacing: 0.5px;
}

.login-button:hover {
  background: linear-gradient(135deg, #0056b3, #004a9e);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(0, 113, 220, 0.35);
}

.login-button:active {
  transform: translateY(0);
}

/* ============================
   平板端
   ============================ */
@media screen and (max-width: 1024px) {
  .login-form {
    width: 400px;
    padding: 36px;
  }
}

/* ============================
   移动端
   ============================ */
@media screen and (max-width: 768px) {
  .login-form {
    width: 92%;
    padding: 28px 24px;
    border-radius: var(--wm-radius-lg);
  }

  .login-logo {
    width: 56px;
    height: 56px;
  }

  .login-header h2 {
    font-size: var(--wm-text-xl);
  }

  .shape-1 { width: 180px; height: 180px; }
  .shape-2 { width: 140px; height: 140px; }
  .shape-3 { width: 100px; height: 100px; }
  .shape-4 { width: 80px; height: 80px; }

  .login-button {
    height: 52px;
    font-size: var(--wm-text-lg);
  }
}

/* ============================
   超小屏幕
   ============================ */
@media screen and (max-width: 375px) {
  .login-form {
    width: 96%;
    padding: 24px 20px;
  }

  .login-header {
    margin-bottom: 24px;
  }

  .form-footer {
    flex-direction: column;
    gap: var(--wm-space-sm);
    align-items: flex-start;
  }
}
</style>
