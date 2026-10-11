<template>
  <div class="orders-page">
    <div class="orders-container">
      <h1 class="page-title">{{ $t('orders.title') }}</h1>

      <div class="orders-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          class="tab-btn"
          :class="{ active: activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          {{ $t('orders.tabs.' + tab.label) }}
        </button>
      </div>

      <div class="orders-list" v-if="orders.length">
        <div v-for="order in orders" :key="order.orderNo" class="order-card">
          <div class="order-header">
            <div class="order-meta">
              <span class="order-number">{{ $t('orders.orderNo', { no: order.orderNo }) }}</span>
              <span class="order-date">{{ formatDate(order.createTime) }}</span>
            </div>
            <div class="order-status" :class="statusClass(order.status)">
              {{ $t('orders.status.' + order.status) }}
            </div>
          </div>

          <div class="order-items">
            <div v-for="item in order.items" :key="item.productId" class="order-item">
              <div class="item-image">
                <img :src="item.imageUrl || '/placeholder.png'" :alt="item.productName" />
              </div>
              <div class="item-info">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-qty">{{ $t('checkout.qty', { n: item.quantity }) }}</span>
              </div>
              <div class="item-price">${{ (item.price * item.quantity).toFixed(2) }}</div>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-total">
              {{ $t('orders.total') }} <span>${{ order.totalAmount?.toFixed(2) }}</span>
            </div>
            <div class="order-actions">
              <el-button v-if="order.status === 0" type="primary" @click="payOrder(order)">
                {{ $t('orders.payNow') }}
              </el-button>
              <el-button v-if="order.status === 1 || order.status === 2" @click="trackOrder(order)">
                {{ $t('orders.trackOrder') }}
              </el-button>
              <el-button v-if="order.status === 0" type="danger" plain @click="cancelOrder(order)">
                {{ $t('orders.cancelOrder') }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <el-icon size="64" color="#d1d1d1"><Document /></el-icon>
        <h2>{{ $t('orders.emptyTitle') }}</h2>
        <p>{{ $t('orders.emptyText') }}</p>
        <el-button type="primary" @click="$router.push('/')">{{ $t('orders.startShopping') }}</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { getCustomerOrders, getCustomerOrderDetail, payOrder as payOrderApi, cancelOrder as cancelOrderApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'

const { t } = useI18n()

const tabs = [
  { label: 'all', value: '' },
  { label: 'pending', value: 0 },
  { label: 'paid', value: 1 },
  { label: 'shipped', value: 3 },
  { label: 'delivered', value: 4 },
  { label: 'cancelled', value: 5 }
]

const activeTab = ref('')
const orders = ref([])

async function loadOrders() {
  try {
    const params = { pageNum: 1, pageSize: 20 }
    // 后端订单状态为数字：0-待支付 1-已支付 2-待发货 3-已发货 4-已完成 5-已取消
    if (activeTab.value !== '') params.status = activeTab.value
    const res = await getCustomerOrders(params)
    const records = res.data?.records || []
    // 列表接口不返回订单明细，逐单补全 items 用于展示
    const enriched = await Promise.all(
      records.map(async order => {
        if (order.items && order.items.length) return order
        try {
          const detail = await getCustomerOrderDetail(order.orderNo)
          return { ...order, items: detail.data?.items || [] }
        } catch {
          return { ...order, items: [] }
        }
      })
    )
    orders.value = enriched
  } catch (e) {
    console.error('Failed to load orders', e)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric'
  })
}

// 复用现有样式类（PENDING/PAID/SHIPPED/DELIVERED/CANCELLED）
function statusClass(status) {
  const map = { 0: 'PENDING', 1: 'PAID', 2: 'PAID', 3: 'SHIPPED', 4: 'DELIVERED', 5: 'CANCELLED' }
  return map[status] ?? status
}

async function payOrder(order) {
  try {
    await payOrderApi(order.orderNo)
    ElMessage.success(t('orders.paymentSuccessful'))
    loadOrders()
  } catch (e) {
    ElMessage.error(t('orders.paymentFailed'))
  }
}

async function cancelOrder(order) {
  try {
    await ElMessageBox.confirm(t('orders.cancelConfirm'), t('orders.cancelTitle'))
    await cancelOrderApi(order.orderNo)
    ElMessage.success(t('orders.orderCancelled'))
    loadOrders()
  } catch {}
}

function trackOrder(order) {
  ElMessage.info(t('orders.tracking', { no: order.orderNo }))
}

watch(activeTab, loadOrders)
onMounted(loadOrders)
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;

.orders-page {
  background: $walmart-gray-100;
  min-height: 100vh;
  padding: 24px 0;
}

.orders-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: $walmart-gray-600;
  margin-bottom: 24px;
}

.orders-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  overflow-x: auto;
}

.tab-btn {
  padding: 10px 20px;
  border: 1px solid $walmart-gray-200;
  border-radius: 24px;
  background: $walmart-white;
  color: $walmart-gray-600;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;

  &.active {
    background: $walmart-blue;
    color: $walmart-white;
    border-color: $walmart-blue;
  }
}

.order-card {
  background: $walmart-white;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: $walmart-gray-100;
  border-bottom: 1px solid $walmart-gray-200;

  .order-meta {
    display: flex;
    gap: 16px;

    .order-number { font-weight: 600; color: $walmart-gray-600; }
    .order-date { color: $walmart-gray-400; font-size: 13px; }
  }

  .order-status {
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;

    &.PENDING { background: #fff3cd; color: #856404; }
    &.PAID { background: #d4edda; color: #155724; }
    &.SHIPPED { background: #cce5ff; color: #004085; }
    &.DELIVERED { background: #d4edda; color: #155724; }
    &.CANCELLED { background: #f8d7da; color: #721c24; }
  }
}

.order-items {
  padding: 16px 20px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid $walmart-gray-100;

  &:last-child { border-bottom: none; }

  .item-image {
    width: 60px;
    height: 60px;
    background: $walmart-gray-100;
    border-radius: 8px;
    overflow: hidden;

    img { width: 100%; height: 100%; object-fit: contain; padding: 4px; }
  }

  .item-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .item-name { font-size: 14px; color: $walmart-gray-600; }
    .item-qty { font-size: 12px; color: $walmart-gray-400; }
  }

  .item-price { font-weight: 600; color: $walmart-gray-600; }
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid $walmart-gray-200;

  .order-total {
    font-size: 16px;
    color: $walmart-gray-600;

    span { font-weight: 700; }
  }
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
  background: $walmart-white;
  border-radius: 12px;

  h2 { font-size: 20px; margin: 16px 0 8px; color: $walmart-gray-600; }
  p { color: $walmart-gray-400; margin-bottom: 24px; }
}
</style>
