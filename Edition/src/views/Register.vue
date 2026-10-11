<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="register-logo">
          <img src="https://www.walmart.com/favicon.ico" alt="Walmart" class="spark" />
          <span class="logo-text">Walmart</span>
        </div>
        <h1>{{ $t('register.title') }}</h1>
        <p class="subtitle">{{ $t('register.alreadyHave') }} <router-link to="/login">{{ $t('login.signin') }}</router-link></p>

        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" :placeholder="$t('register.namePlaceholder')" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" :placeholder="$t('register.emailPlaceholder')" size="large" prefix-icon="Message" />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" :placeholder="$t('register.phonePlaceholder')" size="large" prefix-icon="Phone" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" :placeholder="$t('register.passwordPlaceholder')" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" :placeholder="$t('register.confirmPlaceholder')" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="handleRegister" :loading="loading">{{ $t('register.createBtn') }}</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { customerRegister } from '@/api'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref()

const form = reactive({ username: '', email: '', phone: '', password: '', confirmPassword: '' })

const rules = {
  username: [{ required: true, message: () => t('register.pleaseEnterName'), trigger: 'blur' }],
  email: [{ required: true, message: () => t('register.pleaseEnterEmail'), trigger: 'blur' }, { type: 'email', message: () => t('register.invalidEmail'), trigger: 'blur' }],
  phone: [{ required: true, message: () => t('register.pleaseEnterPhone'), trigger: 'blur' }],
  password: [{ required: true, message: () => t('register.pleaseCreatePassword'), trigger: 'blur' }, { min: 6, message: () => t('register.minSix'), trigger: 'blur' }],
  confirmPassword: [{ required: true, message: () => t('register.pleaseConfirm'), trigger: 'blur' },
    { validator: (r, v, cb) => v !== form.password ? cb(new Error(t('register.mismatch'))) : cb(), trigger: 'blur' }]
}

async function handleRegister() {
  try {
    await formRef.value.validate()
    loading.value = true
    // 后端注册接口期望 { phone, password, memberName }，注册成功后自动登录
    await customerRegister({
      phone: form.phone,
      password: form.password,
      memberName: form.username
    })
    await userStore.login({ phone: form.phone, password: form.password })
    ElMessage.success(t('register.accountCreated'))
    router.push(route.query.redirect || '/')
  } catch (e) { if (e !== false) ElMessage.error(t('register.failed')) }
  finally { loading.value = false }
}
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-blue-dark: #004f9a; $walmart-gray-100: #f5f5f5; $walmart-gray-200: #e6e6e6;
$walmart-gray-300: #d1d1d1; $walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.register-page { min-height: 100vh; background: $walmart-gray-100; display: flex; align-items: center; justify-content: center; padding: 40px 24px; }
.register-container { width: 100%; max-width: 420px; }
.register-card { background: $walmart-white; border-radius: 12px; padding: 40px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  .register-logo { display: flex; align-items: center; justify-content: center; gap: 8px; margin-bottom: 32px;
    .spark { width: 40px; height: 40px; }
    .logo-text { font-size: 28px; font-weight: 800; color: $walmart-blue; }
  }
  h1 { font-size: 22px; font-weight: 700; color: $walmart-gray-600; text-align: center; margin-bottom: 8px; }
  .subtitle { font-size: 14px; color: $walmart-gray-400; text-align: center; margin-bottom: 24px;
    a { color: $walmart-blue; text-decoration: none; font-weight: 600; &:hover { text-decoration: underline; } }
  }
}
.submit-btn { width: 100%; height: 48px; font-size: 16px; font-weight: 600; border-radius: 24px; background: $walmart-blue; border-color: $walmart-blue;
  &:hover { background: $walmart-blue-dark; border-color: $walmart-blue-dark; }
}
</style>
