<template>
  <div class="login-page">
    <div class="login-header">
      <div class="logo-circle">
        <img src="https://www.walmart.com/favicon.ico" alt="Walmart" style="width: 40px; height: 40px; border-radius: 50%;" />
      </div>
      <div class="logo-text">Welcome Back</div>
      <p class="slogan">登录以继续购物</p>
    </div>

    <div class="login-form">
      <div class="tab-bar">
        <span :class="{ active: mode === 'login' }" @click="mode = 'login'">登录</span>
        <span :class="{ active: mode === 'register' }" @click="mode = 'register'">注册</span>
      </div>

      <div class="form-fields">
        <div class="field-group" v-if="mode === 'register'">
          <input v-model="form.memberName" placeholder="昵称" class="field-input" />
        </div>
        <div class="field-group">
          <input v-model="form.phone" placeholder="手机号" type="tel" maxlength="11" class="field-input" />
        </div>
        <div class="field-group">
          <input v-model="form.password" type="password" placeholder="密码" class="field-input" />
        </div>
        <div class="field-group" v-if="mode === 'register'">
          <input v-model="form.confirmPassword" type="password" placeholder="确认密码" class="field-input" />
        </div>
      </div>

      <button class="submit-btn" :class="{ loading: loading }" @click="handleSubmit" :disabled="loading">
        {{ loading ? '' : (mode === 'login' ? '登录' : '注册') }}
      </button>

      <div class="tips" v-if="mode === 'login'">
        测试账号: 13900139001 / 123456
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const mode = ref('login')

const form = reactive({
  memberName: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

async function handleSubmit() {
  if (!form.phone || !form.password) { showToast('请输入手机号和密码'); return }
  if (form.phone.length !== 11) { showToast('请输入正确的手机号'); return }
  if (mode.value === 'register') {
    if (!form.memberName) { showToast('请输入昵称'); return }
    if (form.password !== form.confirmPassword) { showToast('两次密码不一致'); return }
  }
  loading.value = true
  try {
    if (mode.value === 'login') {
      await userStore.login(form.phone, form.password)
    } else {
      await userStore.register(form.phone, form.password, form.memberName)
    }
    showToast(mode.value === 'login' ? '登录成功' : '注册成功')
    router.replace(route.query.redirect || '/')
  } catch (e) {
    showToast(e.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: var(--shop-bg);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 var(--shop-page-pad);
}

.login-header {
  text-align: center;
  padding: 64px 0 36px;

  .logo-circle {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    background: var(--shop-accent-light);
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 16px;
  }

  .logo-text {
    font-size: 26px;
    font-weight: 800;
    color: var(--shop-text-primary);
  }

  .slogan {
    font-size: 14px;
    color: var(--shop-text-secondary);
    margin-top: 6px;
  }
}

.login-form {
  width: 100%;
  max-width: 400px;
}

.tab-bar {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;

  span {
    font-size: 20px;
    font-weight: 600;
    color: var(--shop-text-tertiary);
    cursor: pointer;
    padding-bottom: 4px;
    border-bottom: 3px solid transparent;
    transition: all var(--shop-duration) var(--shop-ease);

    &.active {
      color: var(--shop-accent);
      border-bottom-color: var(--shop-accent);
    }
  }
}

.form-fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field-group {
  position: relative;
}

.field-input {
  width: 100%;
  height: 50px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 10px;
  padding: 0 16px;
  background: var(--shop-bg);
  font-size: 15px;
  color: var(--shop-text-primary);
  outline: none;
  font-family: var(--shop-font);
  transition: border-color var(--shop-duration) var(--shop-ease);

  &::placeholder {
    color: var(--shop-text-tertiary);
  }

  &:focus {
    border-color: var(--shop-accent);
  }
}

.submit-btn {
  width: 100%;
  height: 50px;
  border: none;
  border-radius: 10px;
  background: var(--shop-accent);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  font-family: var(--shop-font);
  cursor: pointer;
  margin-top: 20px;
  transition: background var(--shop-duration) var(--shop-ease);

  &:active {
    background: var(--shop-accent-hover);
  }

  &.loading {
    background: var(--shop-accent);
    opacity: 0.7;
  }
}

.tips {
  text-align: center;
  font-size: 12px;
  color: var(--shop-text-tertiary);
  margin-top: 16px;
}
</style>
