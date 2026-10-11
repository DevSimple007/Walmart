<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <h1 class="page-title">{{ $t('checkout.title') }}</h1>

      <div class="checkout-content">
        <div class="checkout-main">
          <!-- Delivery Address -->
          <div class="checkout-section">
            <h2>{{ $t('checkout.deliveryAddress') }}</h2>
            <div class="address-card" v-if="selectedAddress">
              <div class="address-info">
                <span class="address-name">{{ selectedAddress.name }}</span>
                <span class="address-detail">{{ fullAddress(selectedAddress) }}</span>
                <span class="address-phone">{{ selectedAddress.phone }}</span>
              </div>
              <el-button text @click="changeAddress">{{ $t('checkout.change') }}</el-button>
            </div>
            <el-button v-else @click="changeAddress">{{ $t('checkout.addAddress') }}</el-button>
          </div>

          <!-- Payment Method -->
          <div class="checkout-section">
            <h2>{{ $t('checkout.paymentMethod') }}</h2>
            <div class="payment-options">
              <el-radio-group v-model="paymentMethod">
                <el-radio label="cod">{{ $t('checkout.cod') }}</el-radio>
                <el-radio label="online">{{ $t('checkout.online') }}</el-radio>
              </el-radio-group>
            </div>
          </div>

          <!-- Order Items -->
          <div class="checkout-section">
            <h2>{{ $t('checkout.orderItems', { n: cartStore.totalCount }) }}</h2>
            <div class="checkout-items">
              <div v-for="item in cartStore.checkedItems" :key="item.productId" class="checkout-item">
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
          </div>
        </div>

        <!-- Summary -->
        <div class="checkout-summary">
          <div class="summary-card">
            <h3>{{ $t('cart.orderSummary') }}</h3>
            <div class="summary-row">
              <span>{{ $t('checkout.subtotal') }}</span>
              <span>${{ cartStore.checkedTotal.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>{{ $t('checkout.shipping') }}</span>
              <span class="free">{{ $t('checkout.free') }}</span>
            </div>
            <div class="summary-row">
              <span>{{ $t('checkout.tax') }}</span>
              <span>${{ tax.toFixed(2) }}</span>
            </div>
            <div class="summary-total">
              <span>{{ $t('checkout.estimatedTotal') }}</span>
              <span>${{ total.toFixed(2) }}</span>
            </div>
            <el-button
              type="primary"
              size="large"
              class="place-order-btn"
              @click="placeOrder"
              :loading="loading"
              :disabled="!selectedAddress"
            >
              {{ $t('checkout.placeOrder') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { getAddressList, createCustomerOrder, clearCartApi } from '@/api'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const router = useRouter()
const cartStore = useCartStore()

const paymentMethod = ref('cod')
const selectedAddress = ref(null)
const loading = ref(false)

const tax = computed(() => cartStore.checkedTotal * 0.08)
const total = computed(() => cartStore.checkedTotal + tax.value)

// 后端 ShippingAddress 由 province/city/district/address 组成完整地址
function fullAddress(addr) {
  if (!addr) return ''
  return [addr.province, addr.city, addr.district, addr.address].filter(Boolean).join(' ')
}

function changeAddress() {
  router.push('/account/addresses')
}

async function placeOrder() {
  if (!selectedAddress.value) {
    ElMessage.warning(t('checkout.selectAddress'))
    return
  }
  loading.value = true
  try {
    // 后端 OrderCreateRequest 结构：items[{productId,quantity}]/address/phone/deliveryType/remark
    const orderData = {
      items: cartStore.checkedItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      })),
      address: fullAddress(selectedAddress.value),
      phone: selectedAddress.value.phone,
      deliveryType: 'delivery',
      remark: ''
    }
    await createCustomerOrder(orderData)
    ElMessage.success(t('checkout.orderPlaced'))
    try { await clearCartApi() } catch {}
    cartStore.loadCart()
    router.push('/orders')
  } catch (e) {
    ElMessage.error(t('checkout.failedOrder'))
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getAddressList()
    const addresses = res.data || []
    selectedAddress.value = addresses.find(a => a.isDefault) || addresses[0] || null
  } catch {}
  cartStore.loadCart()
})
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;

.checkout-page {
  background: $walmart-gray-100;
  min-height: 100vh;
  padding: 24px 0;
}

.checkout-container {
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

.checkout-content {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.checkout-section {
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

.address-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: $walmart-gray-100;
  border-radius: 8px;

  .address-info {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .address-name { font-weight: 600; color: $walmart-gray-600; }
    .address-detail { font-size: 14px; color: $walmart-gray-400; }
    .address-phone { font-size: 13px; color: $walmart-gray-400; }
  }
}

.payment-options {
  padding: 8px 0;
}

.checkout-items {
  display: flex;
  flex-direction: column;
}

.checkout-item {
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

.checkout-summary {
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
  color: $walmart-gray-400;

  .free { color: #2a8703; font-weight: 600; }
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

.place-order-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 24px;
  margin-top: 16px;
  background: $walmart-blue;
  border-color: $walmart-blue;
}
</style>
