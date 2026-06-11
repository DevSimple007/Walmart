<template>
  <div class="coupon-page">
    <van-nav-bar title="我的优惠券" left-arrow @click-left="$router.back()" />

    <div v-if="loading" class="page-loading">
      <van-loading size="24px" />
    </div>

    <div v-else-if="coupons.length" class="coupon-list">
      <div
        class="coupon-card"
        v-for="item in coupons"
        :key="item.id"
        :class="{ used: item.status === 1, expired: isExpired(item) }"
      >
        <div class="coupon-left">
          <div class="coupon-value">
            <span class="value-unit">¥</span>
            <span class="value-num">{{ item.couponValue }}</span>
          </div>
          <div class="coupon-condition">满{{ item.minAmount }}可用</div>
        </div>
        <div class="coupon-divider"></div>
        <div class="coupon-right">
          <div class="coupon-name">{{ item.couponName }}</div>
          <div class="coupon-type">{{ getTypeText(item.couponType) }}</div>
          <div class="coupon-time">{{ item.startTime }} ~ {{ item.endTime }}</div>
          <div class="coupon-status" v-if="item.status === 1">已使用</div>
          <div class="coupon-status expired-tag" v-else-if="isExpired(item)">已过期</div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20 12V8H6a2 2 0 0 1-2-2c0-1.1.9-2 2-2h12v4"/>
          <path d="M4 6v12c0 1.1.9 2 2 2h14v-4"/>
          <path d="M18 12a2 2 0 0 0-2 2c0 1.1.9 2 2 2h4v-4h-4z"/>
        </svg>
      </div>
      <p>暂无优惠券</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCustomerCoupons } from '@/api'
import { showToast } from 'vant'

const coupons = ref([])
const loading = ref(true)

function getTypeText(type) {
  const map = { 1: '满减券', 2: '折扣券', 3: '立减券' }
  return map[type] || '优惠券'
}

function isExpired(item) {
  return item.endTime && new Date(item.endTime) < new Date()
}

onMounted(async () => {
  try {
    const res = await getCustomerCoupons()
    coupons.value = res.data || []
  } catch (e) {
    showToast('加载优惠券失败')
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.coupon-page {
  background: var(--shop-bg-secondary);
  min-height: 100vh;
}

.coupon-list {
  padding: var(--shop-gap) var(--shop-page-pad);
}

.coupon-card {
  display: flex;
  align-items: stretch;
  background: var(--shop-card-bg);
  border-radius: var(--shop-card-radius);
  margin-bottom: 10px;
  overflow: hidden;
  box-shadow: var(--shop-card-shadow);

  &.used, &.expired {
    opacity: 0.5;

    .coupon-value, .coupon-name {
      text-decoration: line-through;
    }
  }
}

.coupon-left {
  width: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 0;
  background: var(--shop-accent-light);
}

.coupon-value {
  color: var(--shop-accent);
  font-weight: 800;
  line-height: 1;

  .value-unit {
    font-size: 14px;
  }

  .value-num {
    font-size: 32px;
  }
}

.coupon-condition {
  font-size: 11px;
  color: var(--shop-text-secondary);
  margin-top: 4px;
}

.coupon-divider {
  width: 1px;
  background: repeating-linear-gradient(
    to bottom,
    var(--shop-separator) 0,
    var(--shop-separator) 4px,
    transparent 4px,
    transparent 8px
  );
}

.coupon-right {
  flex: 1;
  padding: 14px 16px;
}

.coupon-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--shop-text-primary);
  margin-bottom: 4px;
}

.coupon-type {
  font-size: 12px;
  color: var(--shop-accent);
  font-weight: 500;
  margin-bottom: 4px;
}

.coupon-time {
  font-size: 11px;
  color: var(--shop-text-tertiary);
}

.coupon-status {
  display: inline-block;
  margin-top: 6px;
  font-size: 11px;
  color: var(--shop-warning);
  font-weight: 600;

  &.expired-tag {
    color: var(--shop-text-tertiary);
  }
}
</style>
