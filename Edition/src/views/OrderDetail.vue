<template>
  <div class="order-detail-page">
    <div class="order-detail-container">
      <h1 class="page-title">{{ $t('orderFlow.detailTitle') }}</h1>

      <div v-if="order" class="detail-content">
        <!-- 状态区 -->
        <div class="status-banner">
          <div class="status-info">
            <h2>{{ statusText }}</h2>
            <p>{{ $t('orderFlow.orderNo') }}：{{ order.orderNo }}</p>
          </div>
          <el-icon size="36"><Van /></el-icon>
        </div>

        <!-- 商品清单 -->
        <div class="detail-section">
          <h2>{{ $t('orderFlow.buyList') }} · {{ $t('orderFlow.itemsCount', { n: (order.items || []).length }) }}</h2>
          <div class="detail-items">
            <div v-for="item in (order.items || [])" :key="item.id || item.productId" class="detail-item">
              <div class="di-image">
                <img :src="item.imageUrl || '/placeholder.png'" :alt="item.productName" />
              </div>
              <div class="di-info">
                <span class="di-name">{{ item.productName }}</span>
                <span class="di-spec" v-if="item.spec || item.subtitle">{{ item.spec || item.subtitle }}</span>
              </div>
              <div class="di-price">
                <span class="di-unit">${{ formatPrice(item.price) }} × {{ item.quantity }}</span>
                <strong>${{ formatPrice((item.price || 0) * (item.quantity || 0)) }}</strong>
              </div>
            </div>
          </div>
        </div>

        <!-- 金额明细 -->
        <div class="detail-section">
          <h2>{{ $t('orderFlow.feeDetail') }}</h2>
          <div class="summary-row">
            <span>{{ $t('orderFlow.subtotal') }}</span>
            <span>${{ formatPrice(order.totalAmount) }}</span>
          </div>
          <div class="summary-row" v-if="Number(order.couponDiscount) > 0">
            <span>{{ $t('orderFlow.couponCut') }}</span>
            <span class="cut">-${{ formatPrice(order.couponDiscount) }}</span>
          </div>
          <div class="summary-total">
            <span>{{ $t('orderFlow.payTotal') }}</span>
            <span>${{ formatPrice(order.actualAmount || order.totalAmount) }}</span>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="detail-section">
          <h2>{{ $t('orderFlow.orderNo') }}</h2>
          <div class="info-row">
            <span class="info-label">{{ $t('orderFlow.orderNo') }}</span>
            <span class="info-value">
              <span class="mono">{{ order.orderNo }}</span>
              <el-button text type="primary" @click="copyOrderNo">{{ $t('orderFlow.copyNo') }}</el-button>
            </span>
          </div>
          <div class="info-row">
            <span class="info-label">{{ $t('orderFlow.orderTime') }}</span>
            <span class="info-value">{{ order.createTime }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">{{ $t('orderFlow.payMethod') }}</span>
            <span class="info-value">{{ $t('orderFlow.payMethodValue') }}</span>
          </div>
          <div class="info-row" v-if="order.address">
            <span class="info-label">{{ $t('orderFlow.addressLabel') }}</span>
            <span class="info-value">{{ order.address }}</span>
          </div>
          <div class="info-row" v-if="order.remark">
            <span class="info-label">{{ $t('orderFlow.orderRemark') }}</span>
            <span class="info-value">{{ order.remark }}</span>
          </div>
        </div>

        <!-- 操作 -->
        <div class="detail-actions">
          <el-button @click="router.push('/orders')">{{ $t('orders.trackOrder') }}</el-button>
          <template v-if="order.status === 0">
            <el-button @click="handleCancel">{{ $t('orders.cancelOrder') }}</el-button>
            <el-button type="primary" @click="handlePay">{{ $t('orders.payNow') }}</el-button>
          </template>
          <el-button v-else-if="order.status === 3" type="primary" @click="handleConfirm">
            {{ $t('orderFlow.confirmReceive') }}
          </el-button>
          <el-button v-else type="primary" @click="router.push('/')">
            {{ $t('orderFlow.buyAgain') }}
          </el-button>
        </div>
      </div>

      <!-- 加载态 -->
      <div v-else class="loading-state">
        <el-icon class="is-loading" size="28"><Loading /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getCustomerOrderDetail, payOrder, cancelOrder, confirmOrder } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Van } from '@element-plus/icons-vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const order = ref(null)

const statusText = computed(() => {
  if (!order.value) return ''
  return t('orderFlow.status.' + order.value.status)
})

function formatPrice(v) {
  return Number(v || 0).toFixed(2)
}

async function loadOrder() {
  try {
    const res = await getCustomerOrderDetail(route.params.orderNo)
    order.value = res.data
  } catch (e) {
    ElMessage.error(t('orderFlow.loadFailed'))
    router.back()
  }
}

function copyOrderNo() {
  const text = String(order.value?.orderNo ?? '')
  if (navigator.clipboard?.writeText) {
    navigator.clipboard
      .writeText(text)
      .then(() => ElMessage.success(t('orderFlow.copied')))
      .catch(() => ElMessage.error(t('orderFlow.copyFailed')))
  } else {
    ElMessage.info(text)
  }
}

async function handlePay() {
  try {
    await payOrder(order.value.orderNo)
    ElMessage.success(t('orders.paymentSuccessful'))
    loadOrder()
  } catch (e) {
    ElMessage.error(t('orders.paymentFailed'))
  }
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm(t('orders.cancelConfirm'), t('orders.cancelTitle'))
    await cancelOrder(order.value.orderNo)
    ElMessage.success(t('orders.orderCancelled'))
    loadOrder()
  } catch {}
}

async function handleConfirm() {
  try {
    await confirmOrder(order.value.orderNo)
    ElMessage.success(t('orderFlow.received'))
    loadOrder()
  } catch (e) {
    ElMessage.error(t('orderFlow.confirmFailed'))
  }
}

onMounted(loadOrder)
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-blue-dark: #004f9a;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676;
$walmart-gray-500: #46474a;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;
$walmart-green: #2a8703;

.order-detail-page {
  background: $walmart-gray-100;
  min-height: 100vh;
  padding: 24px 0;
}

.order-detail-container {
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

.status-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  background: linear-gradient(135deg, $walmart-blue 0%, $walmart-blue-dark 100%);
  color: $walmart-white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;

  .status-info {
    h2 {
      font-size: 22px;
      font-weight: 700;
      margin-bottom: 4px;
    }

    p {
      font-size: 13px;
      opacity: 0.85;
    }
  }
}

.detail-section {
  background: $walmart-white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;

  h2 {
    font-size: 18px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid $walmart-gray-200;
  }
}

.detail-items {
  display: flex;
  flex-direction: column;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid $walmart-gray-100;

  &:last-child {
    border-bottom: none;
  }
}

.di-image {
  width: 64px;
  height: 64px;
  flex-shrink: 0;
  background: $walmart-gray-100;
  border-radius: 8px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    padding: 4px;
  }
}

.di-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;

  .di-name {
    font-size: 14px;
    font-weight: 600;
    color: $walmart-gray-600;
  }

  .di-spec {
    font-size: 12px;
    color: $walmart-gray-400;
  }
}

.di-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  white-space: nowrap;

  .di-unit {
    font-size: 12px;
    color: $walmart-gray-400;
  }

  strong {
    color: $walmart-gray-600;
  }
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  color: $walmart-gray-500;

  .cut {
    color: $walmart-green;
    font-weight: 600;
  }
}

.summary-total {
  display: flex;
  justify-content: space-between;
  padding: 16px 0 0;
  margin-top: 8px;
  border-top: 2px solid $walmart-gray-200;
  font-size: 18px;
  font-weight: 700;
  color: $walmart-gray-600;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
  font-size: 14px;

  .info-label {
    color: $walmart-gray-400;
    flex-shrink: 0;
  }

  .info-value {
    color: $walmart-gray-600;
    text-align: right;
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }

  .mono {
    font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  }
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: $walmart-blue;
}
</style>
