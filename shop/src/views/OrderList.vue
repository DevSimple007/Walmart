<template>
  <div class="order-list-page">
    <van-nav-bar title="我的订单" left-arrow @click-left="$router.back()" />

    <van-tabs v-model:active="activeTab" sticky offset-top="0">
      <van-tab title="全部" :name="-1"></van-tab>
      <van-tab title="待付款" :name="0"></van-tab>
      <van-tab title="待发货" :name="1"></van-tab>
      <van-tab title="待收货" :name="3"></van-tab>
      <van-tab title="已完成" :name="4"></van-tab>
    </van-tabs>

    <div v-if="orders.length" class="order-list">
      <div class="order-card" v-for="order in orders" :key="order.id" @click="$router.push(`/order/detail/${order.orderNo}`)">
        <div class="order-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <span class="order-status" :class="'status-' + order.status">{{ statusMap[order.status] || '未知' }}</span>
        </div>
        <div class="order-body">
          <div class="order-amount">
            <span class="amount-label">实付金额</span>
            <span class="amount-value"><span class="unit">¥</span>{{ order.actualAmount || order.totalAmount }}</span>
          </div>
          <div class="order-time">{{ order.createTime }}</div>
        </div>
        <div class="order-footer">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
          <line x1="16" y1="13" x2="8" y2="13"/>
          <line x1="16" y1="17" x2="8" y2="17"/>
        </svg>
      </div>
      <p>暂无订单</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getCustomerOrders } from '@/api'

const route = useRoute()
const orders = ref([])
const loading = ref(true)
const activeTab = ref(route.query.status ? Number(route.query.status) : -1)

const statusMap = {
  0: '待付款', 1: '待发货', 2: '待发货', 3: '待收货', 4: '已完成', 5: '已取消'
}

async function loadOrders() {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 20 }
    if (activeTab.value !== -1) params.status = activeTab.value
    const res = await getCustomerOrders(params)
    orders.value = res.data?.records || res.page?.list || []
  } catch (e) {
    orders.value = []
  } finally {
    loading.value = false
  }
}

watch(activeTab, loadOrders)
onMounted(loadOrders)
</script>

<style lang="scss" scoped>
.order-list {
  padding: var(--shop-gap) var(--shop-page-pad);
}

.order-card {
  background: var(--shop-card-bg);
  border-radius: var(--shop-card-radius);
  margin-bottom: var(--shop-gap);
  overflow: hidden;
  transition: transform var(--shop-duration) var(--shop-ease);
  cursor: pointer;

  &:active {
    transform: scale(0.99);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 0.5px solid var(--shop-separator);
}

.order-no {
  font-size: 13px;
  color: var(--shop-text-secondary);
}

.order-status {
  font-size: 14px;
  font-weight: 600;
  color: var(--shop-accent);

  &.status-0 { color: var(--shop-warning); }
  &.status-4 { color: var(--shop-success); }
  &.status-5 { color: var(--shop-text-tertiary); }
}

.order-body {
  padding: 14px 16px;
}

.order-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.amount-label {
  font-size: 15px;
  color: var(--shop-text-secondary);
}

.amount-value {
  color: var(--shop-danger);
  font-weight: 700;
  font-size: 20px;
  letter-spacing: -0.3px;

  .unit {
    font-size: 13px;
  }
}

.order-time {
  font-size: 13px;
  color: var(--shop-text-tertiary);
  margin-top: 6px;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  padding: 0 16px 12px;
}
</style>
