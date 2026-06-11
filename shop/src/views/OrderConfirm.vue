<template>
  <div class="order-confirm-page">
    <van-nav-bar title="确认订单" left-arrow @click-left="$router.back()" />

    <div class="confirm-content" v-if="cartStore.orderItems.length">
      <!-- 配送方式 -->
      <div class="section">
        <div class="section-label">配送方式</div>
        <div class="delivery-options">
          <button
            class="delivery-option"
            :class="{ active: deliveryType === 'pickup' }"
            @click="deliveryType = 'pickup'"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" :stroke="deliveryType === 'pickup' ? '#0071DC' : '#86868B'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            <span>到店自提</span>
          </button>
          <button
            class="delivery-option"
            :class="{ active: deliveryType === 'delivery' }"
            @click="deliveryType = 'delivery'"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" :stroke="deliveryType === 'delivery' ? '#0071DC' : '#86868B'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <rect x="1" y="3" width="15" height="13"/>
              <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/>
              <circle cx="5.5" cy="18.5" r="2.5"/>
              <circle cx="18.5" cy="18.5" r="2.5"/>
            </svg>
            <span>送货上门</span>
          </button>
        </div>
      </div>

      <!-- 配送信息 -->
      <div class="section" v-if="deliveryType === 'delivery'">
        <div class="section-label">收货信息</div>

        <!-- 选择已有地址 -->
        <div v-if="addressList.length" class="saved-addresses">
          <div
            class="saved-addr-item"
            v-for="addr in addressList"
            :key="addr.id"
            :class="{ active: selectedAddressId === addr.id }"
            @click="selectAddress(addr)"
          >
            <div class="addr-info">
              <div class="addr-row">
                <span class="addr-name">{{ addr.name }}</span>
                <span class="addr-phone">{{ addr.phone }}</span>
                <span v-if="addr.isDefault" class="addr-default">默认</span>
              </div>
              <div class="addr-detail">{{ formatAddress(addr) }}</div>
            </div>
            <svg v-if="selectedAddressId === addr.id" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0071DC" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
          </div>
          <div class="use-manual" @click="selectedAddressId = 0">使用其他地址</div>
        </div>

        <!-- 手动填写 -->
        <div class="form-fields" v-if="!addressList.length || selectedAddressId === 0">
          <div class="field-row">
            <span class="field-label">收货人</span>
            <input v-model="addressName" placeholder="请输入收货人姓名" class="field-input" />
          </div>
          <div class="field-row">
            <span class="field-label">联系电话</span>
            <input v-model="phone" placeholder="请输入手机号" type="tel" class="field-input" />
          </div>
          <div class="field-row">
            <span class="field-label">收货地址</span>
            <input v-model="address" placeholder="请输入详细收货地址" class="field-input" />
          </div>
        </div>

        <div class="field-row" @click="showTimePicker = true" style="margin-top: 8px;">
          <span class="field-label">送达时间</span>
          <span class="field-value" :class="{ placeholder: !deliveryTime }">{{ deliveryTime || '请选择期望送达时间' }}</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </div>

        <van-popup v-model:show="showTimePicker" position="bottom" round teleport="body" :z-index="3000" :overlay-style="{ zIndex: 2999 }">
          <van-date-picker
            v-model="tempDate"
            title="选择日期"
            :min-date="minDate"
            @confirm="onDateConfirm"
            @cancel="showTimePicker = false"
          />
        </van-popup>
      </div>

      <!-- 商品清单 -->
      <div class="section">
        <div class="section-label">商品清单</div>
        <div class="goods-item" v-for="item in cartStore.orderItems" :key="item.productId">
          <div class="goods-img">
            <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName" />
            <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#D1D1D6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <polyline points="21 15 16 10 5 21"/>
            </svg>
          </div>
          <div class="goods-info">
            <div class="goods-name text-ellipsis">{{ item.productName }}</div>
            <div class="goods-meta">
              <span class="goods-price"><span class="unit">¥</span>{{ formatPrice(item.price) }}</span>
              <span class="goods-qty">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 备注 -->
      <div class="section">
        <div class="field-row">
          <span class="field-label">备注</span>
          <input v-model="remark" placeholder="选填，对商品有什么要求" class="field-input" />
        </div>
      </div>

      <!-- 优惠券 -->
      <div class="section" @click="showCouponPopup = true" style="cursor: pointer;">
        <div class="field-row" style="border-bottom: none;">
          <span class="field-label">优惠券</span>
          <span class="field-value" :class="{ placeholder: !selectedCoupon }">
            {{ selectedCoupon ? '已选优惠券' : (usableCoupons.length ? `${usableCoupons.length}张可用` : '暂无可用优惠券') }}
          </span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </div>
      </div>

      <!-- 优惠券弹窗 -->
      <van-popup v-model:show="showCouponPopup" position="bottom" round teleport="body" :z-index="3000" :overlay-style="{ zIndex: 2999 }" style="max-height: 60vh;">
        <div class="coupon-popup">
          <div class="coupon-popup-header">
            <span>选择优惠券</span>
            <svg @click="showCouponPopup = false" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#86868B" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="cursor: pointer;">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </div>
          <div class="coupon-list">
            <div class="coupon-none" @click="selectCoupon(null)" :class="{ active: !selectedCoupon }">不使用优惠券</div>
            <div
              v-for="c in usableCoupons"
              :key="c.id"
              class="coupon-item"
              :class="{ active: selectedCoupon?.id === c.id }"
              @click="selectCoupon(c)"
            >
              <div class="coupon-left">
                <div class="coupon-value">
                  <span v-if="c.couponType === 2" class="coupon-num">{{ ((1 - Number(c.couponValue)) * 10).toFixed(1) }}</span>
                  <span v-else class="coupon-num">¥{{ formatCouponValue(c.couponValue) }}</span>
                  <span v-if="c.couponType === 2" class="coupon-unit">折</span>
                </div>
              </div>
              <div class="coupon-right">
                <div class="coupon-name">{{ c.couponName }}</div>
                <div class="coupon-desc">{{ formatCouponDesc(c) }}</div>
              </div>
              <svg v-if="selectedCoupon?.id === c.id" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0071DC" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="coupon-check">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
            </div>
            <div v-if="!usableCoupons.length" class="coupon-empty">暂无可用优惠券</div>
          </div>
        </div>
      </van-popup>

      <!-- 金额 -->
      <div class="section amount-section">
        <div class="amount-row">
          <span>商品金额</span>
          <span>¥{{ cartStore.orderTotal.toFixed(2) }}</span>
        </div>
        <div class="amount-row" v-if="deliveryType === 'delivery'">
          <span>配送费</span>
          <span>¥{{ deliveryFee.toFixed(2) }}</span>
        </div>
        <div class="amount-row" v-if="couponDiscount > 0">
          <span>优惠券</span>
          <span class="coupon-discount">-¥{{ couponDiscount.toFixed(2) }}</span>
        </div>
        <div class="amount-row total">
          <span>合计</span>
          <span class="total-price"><span class="unit">¥</span>{{ totalWithFee.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 提交 -->
      <div class="submit-footer safe-bottom">
        <div class="submit-total">
          <span class="total-label">实付</span>
          <span class="total-amount"><span class="unit">¥</span>{{ totalWithFee.toFixed(2) }}</span>
        </div>
        <button class="submit-btn" @click="submitOrder" :disabled="submitting">{{ submitting ? '提交中...' : '提交订单' }}</button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="9" cy="21" r="1"/>
          <circle cx="20" cy="21" r="1"/>
          <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
        </svg>
      </div>
      <p>没有选中的商品</p>
      <button class="shop-btn-plain" style="border-radius:12px;height:44px;padding:0 28px;border:1.5px solid rgba(0,0,0,0.06);background:transparent;font-weight:600;font-size:15px;cursor:pointer;font-family:var(--shop-font);" @click="$router.push('/')">去选购</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { createCustomerOrder, getAddressList, getCustomerCoupons } from '@/api'
import { showToast } from 'vant'

const router = useRouter()
const cartStore = useCartStore()
const remark = ref('')
const submitting = ref(false)
const deliveryType = ref('pickup')
const address = ref('')
const phone = ref('')
const addressName = ref('')
const deliveryTime = ref('')
const addressList = ref([])
const selectedAddressId = ref(0)
const showTimePicker = ref(false)
const deliveryFee = 5

// 优惠券相关
const couponList = ref([])
const selectedCoupon = ref(null)
const showCouponPopup = ref(false)

const usableCoupons = computed(() => {
  const total = cartStore.orderTotal
  return couponList.value.filter(c => {
    if (c.status !== 0) return false
    if (c.minAmount && total < Number(c.minAmount)) return false
    if (c.startTime && new Date(c.startTime) > new Date()) return false
    if (c.endTime && new Date(c.endTime) < new Date()) return false
    return true
  })
})

const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  const total = cartStore.orderTotal
  const c = selectedCoupon.value
  if (c.couponType === 2) {
    // 折扣券: couponValue 是折扣比例(如0.15表示减15%)
    const discount = total * Number(c.couponValue)
    return Math.min(total, discount)
  } else {
    // 满减/立减券
    const minAmount = Number(c.minAmount || 0)
    if (minAmount > 0 && total < minAmount) return 0
    return Math.min(total, Number(c.couponValue || 0))
  }
})

const now = new Date()
const minDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1)
const tempDate = ref([
  String(tomorrow.getFullYear()),
  String(tomorrow.getMonth() + 1).padStart(2, '0'),
  String(tomorrow.getDate()).padStart(2, '0')
])

const totalWithFee = computed(() => {
  const fee = deliveryType.value === 'delivery' ? deliveryFee : 0
  return Math.max(0, cartStore.orderTotal + fee - couponDiscount.value)
})

function formatPrice(price) { return Number(price || 0).toFixed(2) }

function formatCouponValue(val) {
  return Number(val || 0).toFixed(0)
}

function formatCouponDesc(c) {
  const min = Number(c.minAmount || 0)
  if (c.couponType === 2) return min > 0 ? `满${min}可用` : '无门槛'
  return min > 0 ? `满${min}减${formatCouponValue(c.couponValue)}` : `立减${formatCouponValue(c.couponValue)}元`
}

function selectCoupon(coupon) {
  selectedCoupon.value = coupon
  showCouponPopup.value = false
}

function formatAddress(addr) {
  return `${addr.province || ''}${addr.city || ''}${addr.district || ''}${addr.address || ''}`
}

function selectAddress(addr) {
  selectedAddressId.value = addr.id
  addressName.value = addr.name
  phone.value = addr.phone
  address.value = formatAddress(addr)
}

async function loadAddresses() {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
    // 自动选中默认地址
    const defaultAddr = addressList.value.find(a => a.isDefault === 1)
    if (defaultAddr) selectAddress(defaultAddr)
  } catch {}
}

function onDateConfirm({ selectedValues }) {
  deliveryTime.value = `${selectedValues[0]}-${selectedValues[1]}-${selectedValues[2]}`
  showTimePicker.value = false
}

async function submitOrder() {
  if (deliveryType.value === 'delivery') {
    if (!address.value.trim()) { showToast('请选择或输入收货地址'); return }
    if (!phone.value.trim()) { showToast('请输入联系电话'); return }
    if (!deliveryTime.value) { showToast('请选择送达时间'); return }
  }
  submitting.value = true
  try {
    const order = {
      remark: remark.value,
      deliveryType: deliveryType.value,
      address: deliveryType.value === 'delivery' ? address.value : '',
      phone: deliveryType.value === 'delivery' ? phone.value : '',
      deliveryTime: deliveryType.value === 'delivery' ? deliveryTime.value : '',
      couponId: selectedCoupon.value ? selectedCoupon.value.couponId : null,
      couponMemberId: selectedCoupon.value ? selectedCoupon.value.id : null,
      items: cartStore.orderItems.map(item => ({
        productId: item.productId, quantity: item.quantity
      }))
    }
    const res = await createCustomerOrder(order)
    if (cartStore.buyNowItems.length) {
      cartStore.clearBuyNowItem()
    } else {
      cartStore.removeChecked()
    }
    showToast('下单成功')
    router.replace(`/order/result/${res.data?.orderNo || Date.now()}`)
  } catch (e) {
    showToast(e.message || '下单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loadAddresses()
  // 加载可用优惠券
  try {
    const res = await getCustomerCoupons()
    couponList.value = res.data || []
  } catch {}
  if (cartStore.buyNowItems.length === 0 && cartStore.checkedItems.length === 0) {
    showToast('请先选择商品')
    router.replace('/cart')
  }
})
</script>

<style lang="scss" scoped>
.confirm-content {
  padding-bottom: 80px;
}

.section {
  background: var(--shop-card-bg);
  margin: var(--shop-gap) var(--shop-page-pad) 0;
  border-radius: var(--shop-card-radius);
  padding: 16px;
}

.section-label {
  font-size: 17px;
  font-weight: 700;
  color: var(--shop-text-primary);
  margin-bottom: 12px;
}

.delivery-options {
  display: flex;
  gap: 12px;
}

.delivery-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 12px;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  color: var(--shop-text-secondary);
  font-family: var(--shop-font);
  cursor: pointer;
  transition: all var(--shop-duration) var(--shop-ease);

  &.active {
    border-color: var(--shop-accent);
    color: var(--shop-accent);
    background: var(--shop-accent-light);
  }
}

.saved-addresses {
  margin-bottom: 8px;
}

.saved-addr-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &.active {
    border-color: var(--shop-accent);
    background: var(--shop-accent-light);
  }

  .addr-info {
    flex: 1;
    min-width: 0;
  }

  .addr-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 4px;
  }

  .addr-name {
    font-size: 15px;
    font-weight: 600;
    color: var(--shop-text-primary);
  }

  .addr-phone {
    font-size: 13px;
    color: var(--shop-text-secondary);
  }

  .addr-default {
    font-size: 11px;
    background: var(--shop-accent);
    color: #fff;
    padding: 1px 6px;
    border-radius: 4px;
  }

  .addr-detail {
    font-size: 13px;
    color: var(--shop-text-secondary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.use-manual {
  font-size: 13px;
  color: var(--shop-accent);
  text-align: center;
  padding: 8px;
  cursor: pointer;
}

.form-fields {
  display: flex;
  flex-direction: column;
}

.field-row {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 0.5px solid var(--shop-separator);
  cursor: pointer;

  &:last-child {
    border-bottom: none;
  }
}

.field-label {
  font-size: 14px;
  color: var(--shop-text-secondary);
  width: 80px;
  flex-shrink: 0;
}

.field-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  color: var(--shop-text-primary);
  background: transparent;
  font-family: var(--shop-font);

  &::placeholder {
    color: var(--shop-text-tertiary);
  }
}

.field-value {
  flex: 1;
  font-size: 15px;
  color: var(--shop-text-primary);

  &.placeholder {
    color: var(--shop-text-tertiary);
  }
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
  width: 64px;
  height: 64px;
  background: var(--shop-bg-secondary);
  border-radius: 12px;
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
}

.goods-name {
  font-size: 15px;
  color: var(--shop-text-primary);
  margin-bottom: 8px;
}

.goods-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-price {
  color: var(--shop-danger);
  font-weight: 600;
  font-size: 16px;

  .unit {
    font-size: 12px;
  }
}

.goods-qty {
  color: var(--shop-text-tertiary);
  font-size: 14px;
}

.amount-section {
  .amount-row {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    font-size: 15px;
    color: var(--shop-text-secondary);

    &.total {
      font-size: 17px;
      font-weight: 700;
      color: var(--shop-text-primary);
      border-top: 0.5px solid var(--shop-separator);
      padding-top: 12px;
      margin-top: 4px;
    }
  }
}

.total-price {
  color: var(--shop-danger);
}

.submit-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px var(--shop-page-pad);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 0.5px solid var(--shop-separator);
}

.submit-total {
  .total-label {
    font-size: 14px;
    color: var(--shop-text-secondary);
    margin-right: 4px;
  }

  .total-amount {
    color: var(--shop-danger);
    font-size: 24px;
    font-weight: 700;
    letter-spacing: -0.5px;

    .unit {
      font-size: 14px;
    }
  }
}

.submit-btn {
  height: 48px;
  padding: 0 32px;
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

  &:disabled {
    opacity: 0.6;
  }
}

.coupon-discount {
  color: #FF3B30;
  font-weight: 500;
}

.coupon-popup {
  padding: 16px;
}

.coupon-popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 17px;
  font-weight: 700;
  color: var(--shop-text-primary);
  margin-bottom: 16px;
}

.coupon-list {
  max-height: 50vh;
  overflow-y: auto;
}

.coupon-none {
  padding: 14px 16px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 12px;
  text-align: center;
  font-size: 14px;
  color: var(--shop-text-secondary);
  margin-bottom: 8px;
  cursor: pointer;

  &.active {
    border-color: var(--shop-accent);
    color: var(--shop-accent);
    background: var(--shop-accent-light);
  }
}

.coupon-item {
  display: flex;
  align-items: center;
  padding: 14px 12px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &.active {
    border-color: var(--shop-accent);
    background: var(--shop-accent-light);
  }
}

.coupon-left {
  width: 72px;
  text-align: center;
  flex-shrink: 0;
}

.coupon-num {
  font-size: 24px;
  font-weight: 700;
  color: var(--shop-danger);
}

.coupon-unit {
  font-size: 12px;
  color: var(--shop-danger);
  margin-left: 2px;
}

.coupon-right {
  flex: 1;
  min-width: 0;
  margin-left: 12px;
}

.coupon-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--shop-text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-desc {
  font-size: 12px;
  color: var(--shop-text-tertiary);
}

.coupon-check {
  flex-shrink: 0;
  margin-left: 8px;
}

.coupon-empty {
  text-align: center;
  padding: 32px 0;
  color: var(--shop-text-tertiary);
  font-size: 14px;
}
</style>
