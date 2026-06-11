<template>
  <div class="order-result-page">
    <div class="result-icon">
      <svg width="72" height="72" viewBox="0 0 24 24" fill="none" stroke="#34C759" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
        <polyline points="22 4 12 14.01 9 11.01"/>
      </svg>
    </div>
    <div class="result-title">下单成功</div>
    <div class="result-order-no" v-if="orderNo">{{ orderNo }}</div>
    <div class="result-desc">您的订单正在处理中，请耐心等待</div>
    <div class="result-actions">
      <button class="btn-primary" @click="$router.push('/order')">查看订单</button>
      <button class="btn-secondary" @click="$router.push('/')">继续购物</button>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const orderNo = route.params.orderNo
</script>

<style lang="scss" scoped>
.order-result-page {
  text-align: center;
  padding: 100px var(--shop-page-pad) 40px;
  min-height: 100vh;
  background: var(--shop-bg);
}

.result-icon {
  margin-bottom: 20px;
}

.result-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--shop-text-primary);
  letter-spacing: -0.5px;
  margin-bottom: 10px;
}

.result-order-no {
  font-size: 14px;
  color: var(--shop-text-secondary);
  background: var(--shop-bg-secondary);
  display: inline-block;
  padding: 8px 20px;
  border-radius: 10px;
  margin-bottom: 10px;
}

.result-desc {
  color: var(--shop-text-tertiary);
  font-size: 15px;
  margin-bottom: 40px;
}

.result-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-primary {
  min-width: 130px;
  height: 48px;
  border: none;
  border-radius: 14px;
  background: var(--shop-accent);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;
  transition: background var(--shop-duration) var(--shop-ease);

  &:active {
    background: var(--shop-accent-hover);
  }
}

.btn-secondary {
  min-width: 130px;
  height: 48px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 14px;
  background: transparent;
  color: var(--shop-text-primary);
  font-size: 16px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;

  &:active {
    background: var(--shop-bg-secondary);
  }
}
</style>
