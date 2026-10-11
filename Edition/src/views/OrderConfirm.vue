<template>
  <div class="order-confirm-page">
    <div class="order-confirm-container">
      <h1 class="page-title">{{ $t('orderFlow.confirmTitle') }}</h1>

      <div class="confirm-content" v-if="cartStore.checkedItems.length">
        <div class="confirm-main">
          <!-- 配送方式 -->
          <div class="confirm-section">
            <h2>{{ $t('orderFlow.deliveryType') }}</h2>
            <el-radio-group v-model="deliveryType">
              <el-radio-button label="delivery">{{ $t('orderFlow.delivery') }}</el-radio-button>
              <el-radio-button label="pickup">{{ $t('orderFlow.pickup') }}</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 收货地址 / 自提门店 -->
          <div class="confirm-section" v-if="deliveryType === 'delivery'">
            <h2>{{ $t('orderFlow.addressTitle') }}</h2>
            <div class="address-list" v-if="addressList.length">
              <div
                v-for="addr in addressList"
                :key="addr.id"
                class="address-option"
                :class="{ active: selectedAddressId === addr.id }"
                @click="selectedAddressId = addr.id"
              >
                <el-radio v-model="selectedAddressId" :label="addr.id">
                  <span class="addr-head">
                    <span class="addr-name">{{ addr.name }}</span>
                    <span class="addr-phone">{{ addr.phone }}</span>
                    <el-tag v-if="addr.isDefault === 1 || addr.isDefault === true" size="small" type="success">
                      {{ $t('addresses.defaultTag') }}
                    </el-tag>
                  </span>
                  <span class="addr-detail">{{ formatAddress(addr) }}</span>
                </el-radio>
              </div>
            </div>
            <el-button v-else type="primary" plain @click="$router.push('/account/addresses')">
              {{ $t('orderFlow.addAddress') }}
            </el-button>
          </div>
          <div class="confirm-section" v-else>
            <h2>{{ $t('orderFlow.pickup') }}</h2>
            <p class="pickup-store">{{ $t('orderFlow.pickupStore') }}</p>
            <p class="pickup-hint">{{ $t('orderFlow.pickupHint') }}</p>
          </div>

          <!-- 送达时间 -->
          <div class="confirm-section" v-if="deliveryType === 'delivery'">
            <h2>{{ $t('orderFlow.deliveryTime') }}</h2>
            <el-date-picker
              v-model="deliveryTime"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="$t('orderFlow.selectTime')"
              style="width: 240px;"
            />
          </div>

          <!-- 商品清单 -->
          <div class="confirm-section">
            <h2>{{ $t('orderFlow.itemsTitle') }} · {{ $t('orderFlow.itemsCount', { n: cartStore.checkedItems.length }) }}</h2>
            <div class="confirm-items">
              <div v-for="item in cartStore.checkedItems" :key="item.productId" class="confirm-item">
                <div class="ci-image">
                  <img :src="item.imageUrl || '/placeholder.png'" :alt="item.productName" />
                </div>
                <div class="ci-info">
                  <span class="ci-name">{{ item.productName }}</span>
                  <span class="ci-qty">{{ $t('checkout.qty', { n: item.quantity }) }}</span>
                </div>
                <span class="ci-price">${{ formatPrice((item.price || 0) * (item.quantity || 0)) }}</span>
              </div>
            </div>
          </div>

          <!-- 优惠券 + 备注 -->
          <div class="confirm-section">
            <h2>{{ $t('orderFlow.couponTitle') }}</h2>
            <el-select v-model="selectedCouponId" :placeholder="couponPlaceholder" style="width: 100%; max-width: 360px;">
              <el-option :value="null" :label="$t('orderFlow.couponUnused')" />
              <el-option v-for="c in usableCoupons" :key="c.id" :value="c.id" :label="couponLabel(c)" />
            </el-select>
            <div class="remark-row">
              <span class="remark-label">{{ $t('orderFlow.remark') }}</span>
              <el-input v-model="remark" maxlength="50" :placeholder="$t('orderFlow.remarkPlaceholder')" />
            </div>
          </div>
        </div>

        <!-- 金额明细 -->
        <div class="confirm-summary">
          <div class="summary-card">
            <h3>{{ $t('orderFlow.amountDetail') }}</h3>
            <div class="summary-row">
              <span>{{ $t('orderFlow.subtotal') }}</span>
              <span>${{ formatPrice(subtotal) }}</span>
            </div>
            <div class="summary-row">
              <span>{{ $t('orderFlow.shipping') }}</span>
              <span class="free">{{ $t('orderFlow.free') }}</span>
            </div>
            <div class="summary-row" v-if="couponDiscount > 0">
              <span>{{ $t('orderFlow.couponCut') }}</span>
              <span class="cut">-${{ formatPrice(couponDiscount) }}</span>
            </div>
            <div class="summary-total">
              <span>{{ $t('orderFlow.payTotal') }}</span>
              <span>${{ formatPrice(payTotal) }}</span>
            </div>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="submitting"
              @click="submitOrder"
            >
              {{ submitting ? $t('orderFlow.submitting') : $t('orderFlow.submit') }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空态 -->
      <div class="empty-state" v-else>
        <el-icon size="64" color="#d1d1d1"><ShoppingCart /></el-icon>
        <h2>{{ $t('orderFlow.empty') }}</h2>
        <el-button type="primary" size="large" @click="$router.push('/')">
          {{ $t('orderFlow.goShopping') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { createCustomerOrder, getAddressList, getCustomerCoupons, clearCartApi } from '@/api'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const cartStore = useCartStore()

const deliveryType = ref('delivery')
const addressList = ref([])
const selectedAddressId = ref(null)
const deliveryTime = ref('')
const remark = ref('')
const submitting = ref(false)
const couponList = ref([])
const selectedCouponId = ref(null)

function formatPrice(v) {
  return Number(v || 0).toFixed(2)
}

const subtotal = computed(() => cartStore.checkedTotal)

const usableCoupons = computed(() => {
  const total = subtotal.value
  return couponList.value.filter(c => {
    if (c.status !== 0) return false
    if (c.minAmount && total < Number(c.minAmount)) return false
    if (c.startTime && new Date(c.startTime) > new Date()) return false
    if (c.endTime && new Date(c.endTime) < new Date()) return false
    return true
  })
})

const selectedCoupon = computed(
  () => usableCoupons.value.find(c => c.id === selectedCouponId.value) || null
)

const couponDiscount = computed(() => {
  const c = selectedCoupon.value
  if (!c) return 0
  const total = subtotal.value
  if (c.couponType === 2) return Math.min(total, total * Number(c.couponValue))
  const min = Number(c.minAmount || 0)
  if (min > 0 && total < min) return 0
  return Math.min(total, Number(c.couponValue || 0))
})

const payTotal = computed(() => Math.max(0, subtotal.value - couponDiscount.value))

const couponPlaceholder = computed(() =>
  usableCoupons.value.length
    ? t('orderFlow.couponUsable', { n: usableCoupons.value.length })
    : t('orderFlow.couponTitle')
)

const selectedAddress = computed(
  () => addressList.value.find(a => a.id === selectedAddressId.value) || null
)

function couponLabel(c) {
  const min = Number(c.minAmount || 0)
  if (c.couponType === 2) return `${c.couponName} · ${t('orderFlow.couponPercent')}`
  return min > 0 ? `${c.couponName} · ${t('orderFlow.couponFull', { min })}` : c.couponName
}

// 后端 ShippingAddress 由 province/city/district/address 组成完整地址
function formatAddress(addr) {
  if (!addr) return ''
  return [addr.province, addr.city, addr.district, addr.address].filter(Boolean).join(' ')
}

async function loadAddresses() {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
    const def =
      addressList.value.find(a => a.isDefault === 1 || a.isDefault === true) || addressList.value[0]
    if (def) selectedAddressId.value = def.id
  } catch {}
}

async function submitOrder() {
  if (deliveryType.value === 'delivery') {
    if (!selectedAddress.value) {
      ElMessage.warning(t('orderFlow.pleaseAddress'))
      return
    }
    if (!deliveryTime.value) {
      ElMessage.warning(t('orderFlow.pleaseTime'))
      return
    }
  }
  submitting.value = true
  try {
    const addr = selectedAddress.value
    // 后端 OrderCreateRequest 结构：items[{productId,quantity}]/address/phone/deliveryType/remark
    const order = {
      remark: remark.value,
      deliveryType: deliveryType.value,
      address: deliveryType.value === 'delivery' && addr ? formatAddress(addr) : '',
      phone: deliveryType.value === 'delivery' && addr ? addr.phone : '',
      deliveryTime: deliveryType.value === 'delivery' ? deliveryTime.value : '',
      couponId: selectedCoupon.value ? selectedCoupon.value.couponId : null,
      couponMemberId: selectedCoupon.value ? selectedCoupon.value.id : null,
      items: cartStore.checkedItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))
    }
    const res = await createCustomerOrder(order)
    try { await clearCartApi() } catch {}
    cartStore.loadCart()
    ElMessage.success(t('orderFlow.orderPlaced'))
    router.replace(`/order/result/${res.data?.orderNo || Date.now()}`)
  } catch (e) {
    ElMessage.error(t('orderFlow.orderFailed'))
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  if (!cartStore.checkedItems.length) {
    ElMessage.warning(t('orderFlow.empty'))
    router.replace('/cart')
    return
  }
  loadAddresses()
  try {
    const res = await getCustomerCoupons()
    couponList.value = res.data || []
  } catch {}
})
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

.order-confirm-page {
  background: $walmart-gray-100;
  min-height: 100vh;
  padding: 24px 0;
}

.order-confirm-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: $walmart-gray-600;
  margin-bottom: 24px;
}

.confirm-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.confirm-section {
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

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-option {
  border: 2px solid $walmart-gray-200;
  border-radius: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: border-color 0.2s;

  &:hover {
    border-color: $walmart-gray-400;
  }

  &.active {
    border-color: $walmart-blue;
  }
}

.addr-head {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: $walmart-gray-600;
}

.addr-phone {
  font-size: 13px;
  font-weight: 400;
  color: $walmart-gray-400;
}

.addr-detail {
  display: block;
  font-size: 13px;
  color: $walmart-gray-400;
  margin-top: 2px;
}

.pickup-store {
  font-weight: 600;
  color: $walmart-gray-600;
}

.pickup-hint {
  margin-top: 4px;
  font-size: 13px;
  color: $walmart-gray-400;
}

.confirm-items {
  display: flex;
  flex-direction: column;
}

.confirm-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid $walmart-gray-100;

  &:last-child {
    border-bottom: none;
  }
}

.ci-image {
  width: 60px;
  height: 60px;
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

.ci-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;

  .ci-name {
    font-size: 14px;
    color: $walmart-gray-600;
  }

  .ci-qty {
    font-size: 12px;
    color: $walmart-gray-400;
  }
}

.ci-price {
  font-weight: 600;
  color: $walmart-gray-600;
  white-space: nowrap;
}

.remark-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;

  .remark-label {
    flex-shrink: 0;
    color: $walmart-gray-600;
    font-weight: 600;
  }
}

.confirm-summary {
  position: sticky;
  top: 100px;
  align-self: start;
}

.summary-card {
  background: $walmart-white;
  border-radius: 12px;
  padding: 24px;

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 16px;
  }
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  color: $walmart-gray-500;

  .free {
    color: $walmart-green;
    font-weight: 600;
  }

  .cut {
    color: $walmart-green;
    font-weight: 600;
  }
}

.summary-total {
  display: flex;
  justify-content: space-between;
  padding: 16px 0;
  margin-top: 8px;
  border-top: 2px solid $walmart-gray-200;
  font-size: 18px;
  font-weight: 700;
  color: $walmart-gray-600;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 24px;
  margin-top: 16px;
  background: $walmart-blue;
  border-color: $walmart-blue;

  &:hover {
    background: $walmart-blue-dark;
    border-color: $walmart-blue-dark;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  text-align: center;
  background: $walmart-white;
  border-radius: 12px;

  h2 {
    font-size: 22px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin: 16px 0 24px;
  }
}
</style>
