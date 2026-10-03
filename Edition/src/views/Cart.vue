<template>
  <div class="cart-page">
    <div class="cart-container">
      <h1 class="cart-title">{{ $t('cart.title') }}</h1>
      <p class="cart-count" v-if="cartStore.items.length">
        {{ $t('cart.items', { n: cartStore.totalCount }) }}
      </p>

      <div class="cart-content" v-if="cartStore.items.length">
        <div class="cart-items">
          <div
            v-for="item in cartStore.items"
            :key="item.productId"
            class="cart-item"
          >
            <el-checkbox
              :model-value="item.checked"
              @change="cartStore.toggleCheck(item.productId)"
              size="large"
            />

            <div class="item-image" @click="$router.push(`/ip/${item.productId}`)">
              <img :src="item.imageUrl || '/placeholder.png'" :alt="item.productName" />
            </div>

            <div class="item-details">
              <h3 class="item-name" @click="$router.push(`/ip/${item.productId}`)">
                {{ item.productName }}
              </h3>
              <div class="item-stock">
                <span class="in-stock" v-if="item.stock > 5">{{ $t('common.inStock') }}</span>
                <span class="low-stock" v-else-if="item.stock > 0">{{ $t('common.onlyLeft', { n: item.stock }) }}</span>
                <span class="out-stock" v-else>{{ $t('common.outOfStock') }}</span>
              </div>
              <div class="item-delivery">
                <el-icon size="14"><Van /></el-icon>
                <span>{{ $t('cart.freeDeliveryTomorrow') }}</span>
              </div>
              <div class="item-actions">
                <el-input-number
                  :model-value="item.quantity"
                  :min="1"
                  :max="item.stock || 99"
                  @change="(val) => cartStore.updateQuantity(item.productId, val)"
                  size="small"
                />
                <el-button
                  text
                  type="danger"
                  @click="removeItem(item.productId)"
                >
                  <el-icon><Delete /></el-icon>
                  {{ $t('cart.remove') }}
                </el-button>
                <el-button text type="primary">
                  <el-icon><DocumentCopy /></el-icon>
                  {{ $t('cart.saveForLater') }}
                </el-button>
              </div>
            </div>

            <div class="item-price">
              <span class="dollar">$</span>
              <span class="amount">{{ formatPrice(item.price) }}</span>
            </div>
          </div>
        </div>

        <!-- Order Summary -->
        <div class="order-summary">
          <div class="summary-card">
            <div class="summary-header">
              <h3>{{ $t('cart.orderSummary') }}</h3>
            </div>
            <div class="summary-row">
              <span>{{ $t('cart.itemsCount', { n: cartStore.totalCount }) }}</span>
              <span>${{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>{{ $t('cart.shipping') }}</span>
              <span class="free">FREE</span>
            </div>
            <div class="summary-row">
              <span>{{ $t('cart.estimatedTax') }}</span>
              <span>${{ estimatedTax.toFixed(2) }}</span>
            </div>
            <div class="summary-total">
              <span>{{ $t('cart.estimatedTotal') }}</span>
              <span>${{ estimatedTotal.toFixed(2) }}</span>
            </div>

            <el-button
              type="primary"
              size="large"
              class="checkout-btn"
              @click="goToCheckout"
              :disabled="!cartStore.checkedItems.length"
            >
              {{ $t('cart.continueCheckout') }}
            </el-button>

            <div class="summary-note">
              <el-icon><Lock /></el-icon>
              <span>{{ $t('cart.secureCheckout') }}</span>
            </div>
          </div>

          <!-- Promo Code -->
          <div class="promo-section">
            <h4>{{ $t('cart.applyPromo') }}</h4>
            <div class="promo-input">
              <el-input :placeholder="$t('cart.enterPromo')" size="large" />
              <el-button size="large">{{ $t('cart.apply') }}</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty Cart -->
      <div class="empty-cart" v-else>
        <div class="empty-icon">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="#d1d1d1" stroke-width="1.5">
            <circle cx="9" cy="21" r="1"/>
            <circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
          </svg>
        </div>
        <h2>{{ $t('cart.emptyTitle') }}</h2>
        <p>{{ $t('cart.emptyText') }}</p>
        <el-button type="primary" size="large" @click="$router.push('/')">
          {{ $t('cart.continueShopping') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Van, Delete, DocumentCopy, Lock } from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const cartStore = useCartStore()

const estimatedTax = computed(() => {
  return cartStore.totalPrice * 0.08
})

const estimatedTotal = computed(() => {
  return cartStore.totalPrice + estimatedTax.value
})

function formatPrice(price) {
  return Number(price).toFixed(2)
}

async function removeItem(productId) {
  try {
    await ElMessageBox.confirm(
      t('cart.removeConfirm'),
      t('cart.removeItem'),
      {
        confirmButtonText: t('cart.remove'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    await cartStore.removeItem(productId)
    ElMessage.success(t('cart.removed'))
  } catch {
    // Cancelled
  }
}

function goToCheckout() {
  router.push('/checkout')
}

onMounted(() => {
  cartStore.loadCart()
})
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-blue-dark: #004f9a;
$walmart-yellow: #ffc220;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-300: #d1d1d1;
$walmart-gray-400: #767676;
$walmart-gray-500: #46474a;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;

.cart-page {
  background: $walmart-gray-100;
  min-height: 100vh;
  padding: 24px 0;
}

.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.cart-title {
  font-size: 28px;
  font-weight: 700;
  color: $walmart-gray-600;
  margin-bottom: 4px;
}

.cart-count {
  font-size: 14px;
  color: $walmart-gray-400;
  margin-bottom: 24px;
}

.cart-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

// Cart Items
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1px;
  background: $walmart-gray-200;
  border-radius: 12px;
  overflow: hidden;
}

.cart-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: $walmart-white;
}

.item-image {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  background: $walmart-gray-100;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    padding: 8px;
  }
}

.item-details {
  flex: 1;
  min-width: 0;

  .item-name {
    font-size: 16px;
    font-weight: 600;
    color: $walmart-gray-600;
    margin-bottom: 8px;
    cursor: pointer;

    &:hover {
      color: $walmart-blue;
    }
  }

  .item-stock {
    font-size: 13px;
    margin-bottom: 8px;

    .in-stock {
      color: #2a8703;
    }

    .low-stock {
      color: #e31837;
    }

    .out-stock {
      color: $walmart-gray-400;
    }
  }

  .item-delivery {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: $walmart-gray-400;
    margin-bottom: 12px;
  }

  .item-actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

.item-price {
  font-size: 20px;
  font-weight: 700;
  color: $walmart-gray-600;
  white-space: nowrap;

  .dollar {
    font-size: 12px;
    vertical-align: top;
    position: relative;
    top: 2px;
  }
}

// Order Summary
.order-summary {
  position: sticky;
  top: 100px;
  align-self: start;
}

.summary-card {
  background: $walmart-white;
  border-radius: 12px;
  padding: 24px;

  .summary-header {
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid $walmart-gray-200;

    h3 {
      font-size: 20px;
      font-weight: 700;
      color: $walmart-gray-600;
    }
  }
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  color: $walmart-gray-500;

  .free {
    color: #2a8703;
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

.checkout-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 24px;
  margin-top: 20px;
  background: $walmart-blue;
  border-color: $walmart-blue;

  &:hover {
    background: $walmart-blue-dark;
    border-color: $walmart-blue-dark;
  }
}

.summary-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  font-size: 13px;
  color: $walmart-gray-400;
}

// Promo Section
.promo-section {
  background: $walmart-white;
  border-radius: 12px;
  padding: 20px;
  margin-top: 16px;

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: $walmart-gray-600;
    margin-bottom: 12px;
  }

  .promo-input {
    display: flex;
    gap: 8px;
  }
}

// Empty Cart
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  text-align: center;
  background: $walmart-white;
  border-radius: 12px;

  .empty-icon {
    margin-bottom: 24px;
  }

  h2 {
    font-size: 24px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: $walmart-gray-400;
    margin-bottom: 24px;
  }
}
</style>
