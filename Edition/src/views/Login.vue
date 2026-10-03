<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <!-- Logo -->
        <div class="login-logo">
          <img src="https://www.walmart.com/favicon.ico" alt="Walmart" class="spark" />
          <span class="logo-text">Walmart</span>
        </div>

        <!-- Sign In Form -->
        <div class="login-form" v-if="!isRegister">
          <h1>{{ $t('login.title') }}</h1>
          <p class="login-subtitle">
            {{ $t('login.subtitle') }}
          </p>

          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
            <el-form-item prop="email">
              <el-input
                v-model="loginForm.email"
                :placeholder="$t('login.phonePlaceholder')"
                size="large"
                prefix-icon="Message"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                :placeholder="$t('login.passwordPlaceholder')"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="submit-btn"
                @click="handleLogin"
                :loading="loading"
              >
                {{ $t('login.signin') }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="login-links">
            <a href="#" @click.prevent="isRegister = true">{{ $t('login.createAccount') }}</a>
            <a href="#" @click.prevent="forgotPassword">{{ $t('login.forgot') }}</a>
          </div>

          <div class="divider">
            <span>{{ $t('login.or') }}</span>
          </div>

          <el-button size="large" class="social-btn">
            <img src="https://www.walmart.com/favicon.ico" width="20" height="20" />
            {{ $t('login.continueWalmart') }}
          </el-button>
        </div>

        <!-- Register Form -->
        <div class="register-form" v-else>
          <h1>{{ $t('register.title') }}</h1>
          <p class="login-subtitle">
            {{ $t('register.alreadyHave') }} <a href="#" @click.prevent="isRegister = false">{{ $t('login.signin') }}</a>
          </p>

          <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                :placeholder="$t('register.namePlaceholder')"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                :placeholder="$t('register.emailPlaceholder')"
                size="large"
                prefix-icon="Message"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                :placeholder="$t('register.phonePlaceholder')"
                size="large"
                prefix-icon="Phone"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                :placeholder="$t('register.passwordPlaceholder')"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                :placeholder="$t('register.confirmPlaceholder')"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="submit-btn"
                @click="handleRegister"
                :loading="loading"
              >
                {{ $t('register.createBtn') }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { register } from '@/api'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isRegister = ref(false)
const loading = ref(false)

const loginForm = reactive({
  email: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const loginRules = {
  email: [
    { required: true, message: () => t('loginRules.pleaseEnterPhone'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: () => t('loginRules.pleaseEnterPassword'), trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: () => t('register.pleaseEnterName'), trigger: 'blur' }
  ],
  email: [
    { required: true, message: () => t('register.pleaseEnterEmail'), trigger: 'blur' },
    { type: 'email', message: () => t('register.invalidEmail'), trigger: 'blur' }
  ],
  phone: [
    { required: true, message: () => t('register.pleaseEnterPhone'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: () => t('register.pleaseCreatePassword'), trigger: 'blur' },
    { min: 6, message: () => t('register.minSix'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: () => t('register.pleaseConfirm'), trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error(t('register.mismatch')))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loginFormRef = ref()
const registerFormRef = ref()

async function handleLogin() {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    // 后端 CustomerAuthController 期望 { phone, password }
    await userStore.login({
      phone: loginForm.email,
      password: loginForm.password
    })
    ElMessage.success(t('login.welcomeBack'))
    router.push(route.query.redirect || '/')
  } catch (e) {
    if (e !== false) {
      ElMessage.error(t('login.loginFailed'))
    }
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  try {
    await registerFormRef.value.validate()
    loading.value = true
    // 后端注册接口期望 { phone, password, memberName }，注册成功后自动登录
    await register({
      phone: registerForm.phone,
      password: registerForm.password,
      memberName: registerForm.username
    })
    await userStore.login({
      phone: registerForm.phone,
      password: registerForm.password
    })
    ElMessage.success(t('register.accountCreated'))
    router.push(route.query.redirect || '/')
  } catch (e) {
    if (e !== false) {
      ElMessage.error(t('register.failed'))
    }
  } finally {
    loading.value = false
  }
}

function forgotPassword() {
  ElMessage.info(t('login.forgotInfo'))
}
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-blue-dark: #004f9a;
$walmart-yellow: #ffc220;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-300: #d1d1d1;
$walmart-gray-400: #767676;
$walmart-gray-500: #46474a;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;

.login-page {
  min-height: 100vh;
  background: $walmart-gray-100;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
}

.login-container {
  width: 100%;
  max-width: 420px;
}

.login-card {
  background: $walmart-white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 32px;

  .spark {
    width: 40px;
    height: 40px;
  }

  .logo-text {
    font-size: 28px;
    font-weight: 800;
    color: $walmart-blue;
  }
}

.login-form, .register-form {
  h1 {
    font-size: 22px;
    font-weight: 700;
    color: $walmart-gray-600;
    text-align: center;
    margin-bottom: 8px;
  }

  .login-subtitle {
    font-size: 14px;
    color: $walmart-gray-400;
    text-align: center;
    margin-bottom: 24px;

    a {
      color: $walmart-blue;
      text-decoration: none;
      font-weight: 600;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 24px;
  background: $walmart-blue;
  border-color: $walmart-blue;

  &:hover {
    background: $walmart-blue-dark;
    border-color: $walmart-blue-dark;
  }
}

.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;

  a {
    font-size: 14px;
    color: $walmart-blue;
    text-decoration: none;
    font-weight: 600;

    &:hover {
      text-decoration: underline;
    }
  }
}

.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;

  &::before, &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: $walmart-gray-200;
  }

  span {
    padding: 0 16px;
    font-size: 14px;
    color: $walmart-gray-400;
  }
}

.social-btn {
  width: 100%;
  height: 48px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 24px;
  border: 1px solid $walmart-gray-300;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &:hover {
    background: $walmart-gray-100;
  }
}
</style>
