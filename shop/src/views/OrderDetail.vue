<template>
  <div class="order-detail-page">
    <van-nav-bar title="订单详情" left-arrow @click-left="$router.back()" />

    <div v-if="order" class="detail-content">
      <!-- 状态 -->
      <div class="status-bar" :class="'status-' + order.status">
        <div class="status-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <template v-if="order.status === 0">
              <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
            </template>
            <template v-else-if="order.status === 3 || order.status === 1 || order.status === 2">
              <rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/>
            </template>
            <template v-else-if="order.status === 4">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
            </template>
            <template v-else>
              <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
            </template>
          </svg>
        </div>
        <span>{{ statusMap[order.status] || '未知状态' }}</span>
      </div>

      <!-- 商品列表 -->
      <div class="section">
        <div class="goods-item" v-for="item in (order.items || [])" :key="item.id">
          <div class="goods-img">
            <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName || '商品'" />
            <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#D1D1D6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <polyline points="21 15 16 10 5 21"/>
            </svg>
          </div>
          <div class="goods-info">
            <div class="goods-name text-ellipsis">{{ item.productName || '商品' }}</div>
            <div class="goods-meta">
              <span class="goods-price">¥{{ formatPrice(item.price) }}</span>
              <span class="goods-qty">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="section">
        <div class="info-row"><span class="label">订单号</span><span>{{ order.orderNo }}</span></div>
        <div class="info-row"><span class="label">下单时间</span><span>{{ order.createTime }}</span></div>
        <div class="info-row"><span class="label">商品金额</span><span>¥{{ formatPrice(order.totalAmount) }}</span></div>
        <div class="info-row" v-if="order.couponDiscount && Number(order.couponDiscount) > 0">
          <span class="label">优惠券</span>
          <span class="coupon-discount">-¥{{ formatPrice(order.couponDiscount) }}</span>
        </div>
        <div class="info-row total"><span class="label">实付金额</span><span class="total-price">¥{{ formatPrice(order.actualAmount || order.totalAmount) }}</span></div>
        <div class="info-row" v-if="order.remark"><span class="label">备注</span><span>{{ order.remark }}</span></div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-bar safe-bottom">
        <button v-if="order.status === 0" class="action-btn primary" @click="handlePay">去付款</button>
        <button v-if="order.status === 0" class="action-btn outline" @click="handleCancel">取消订单</button>
        <button v-if="order.status === 3" class="action-btn primary" @click="handleConfirm">确认收货</button>
        <button class="action-btn outline" @click="$router.push('/')">返回首页</button>
      </div>
    </div>

    <van-loading v-else class="page-loading" size="24px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCustomerOrderDetail, payOrder, cancelOrder, confirmOrder } from '@/api'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const order = ref(null)

const statusMap = { 0: '待付款', 1: '待发货', 2: '待发货', 3: '待收货', 4: '已完成', 5: '已取消' }

function formatPrice(price) { return Number(price || 0).toFixed(2) }

async function loadOrder() {
  try {
    const res = await getCustomerOrderDetail(route.params.orderNo)
    order.value = res.data
  } catch (e) {
    showToast('订单不存在')
    router.back()
  }
}

async function handlePay() {
  try { await payOrder(order.value.orderNo); showToast('付款成功'); loadOrder() }
  catch (e) { showToast(e.message || '付款失败') }
}

async function handleCancel() {
  try { await cancelOrder(order.value.orderNo); showToast('已取消'); loadOrder() }
  catch (e) { showToast(e.message || '取消失败') }
}

async function handleConfirm() {
  try { await confirmOrder(order.value.orderNo); showToast('已确认收货'); loadOrder() }
  catch (e) { showToast(e.message || '确认失败') }
}

onMounted(loadOrder)
</script>

<style lang="scss" scoped>
.detail-content {
  padding-bottom: 80px;
}

.status-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 28px var(--shop-page-pad);
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.3px;
  background: var(--shop-accent);

  &.status-0 { background: var(--shop-warning); }
  &.status-3 { background: var(--shop-success); }
  &.status-4 { background: #86868B; }
  &.status-5 { background: #86868B; }
}

.status-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.section {
  background: var(--shop-card-bg);
  margin: var(--shop-gap) var(--shop-page-pad) 0;
  border-radius: var(--shop-card-radius);
  padding: 16px;
}

.goods-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 0.5px solid var(--shop-separator);

  &:last-child {
    border-bottom: none;
  }
}

.goods-img {
  width: 56px;
  height: 56px;
  background: var(--shop-bg-secondary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.goods-info {
  flex: 1;
  min-width: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-name {
  font-size: 15px;
  flex: 1;
  color: var(--shop-text-primary);
}

.goods-meta {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-shrink: 0;
  margin-left: 12px;
}

.goods-price {
  color: var(--shop-danger);
  font-weight: 600;
  font-size: 15px;
}

.goods-qty {
  color: var(--shop-text-tertiary);
  font-size: 13px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 15px;
  color: var(--shop-text-secondary);
  border-bottom: 0.5px solid var(--shop-separator);

  .label {
    color: var(--shop-text-secondary);
  }

  &:last-child {
    border-bottom: none;
  }

  &.total {
    border-top: 0.5px solid var(--shop-separator);
    margin-top: 4px;
    padding-top: 14px;
  }
}

.total-price {
  color: var(--shop-danger);
  font-weight: 700;
  font-size: 20px;
}

.coupon-discount {
  color: #FF3B30;
  font-weight: 600;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px var(--shop-page-pad);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 0.5px solid var(--shop-separator);
}

.action-btn {
  min-width: 100px;
  height: 40px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;
  transition: all var(--shop-duration) var(--shop-ease);

  &.primary {
    background: var(--shop-accent);
    color: #fff;
    border: none;

    &:active {
      background: var(--shop-accent-hover);
    }
  }

  &.outline {
    background: transparent;
    color: var(--shop-text-primary);
    border: 1.5px solid var(--shop-separator);

    &:active {
      background: var(--shop-bg-secondary);
    }
  }
}

.page-loading {
  text-align: center;
  padding: 60px;
}
</style>
