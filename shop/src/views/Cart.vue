<template>
  <div class="cart-page">
    <div class="cart-header">
      <h1 class="cart-title">购物车</h1>
      <span class="cart-count" v-if="cartStore.items.length">{{ cartStore.totalCount }} 件商品</span>
    </div>

    <div v-if="cartStore.items.length" class="cart-content">
      <van-checkbox-group v-model="checkedIds">
        <div class="cart-item" v-for="item in cartStore.items" :key="item.productId">
          <van-checkbox :name="item.productId" icon-size="22" checked-color="#0071DC" />
          <div class="item-img" @click="$router.push(`/product/${item.productId}`)">
            <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName" />
            <svg v-else width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#D1D1D6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <polyline points="21 15 16 10 5 21"/>
            </svg>
          </div>
          <div class="item-info">
            <div class="item-name text-ellipsis-2">{{ item.productName }}</div>
            <div class="item-bottom">
              <span class="item-price"><span class="unit">¥</span>{{ formatPrice(item.price) }}</span>
              <van-stepper
                :model-value="item.quantity"
                :min="1"
                :max="item.stock || 99"
                @change="(val) => handleQuantityChange(item.productId, val)"
                theme="round"
                button-size="26px"
                input-width="36px"
              />
            </div>
          </div>
          <button class="item-delete" @click="handleDelete(item.productId)">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
            </svg>
          </button>
        </div>
      </van-checkbox-group>

      <div class="cart-footer safe-bottom">
        <van-checkbox :model-value="isAllChecked" @click.stop="handleToggleAll" icon-size="22" checked-color="#0071DC">
          <span class="select-all-text">全选</span>
        </van-checkbox>
        <div class="cart-total">
          <span class="total-label">合计</span>
          <span class="total-price"><span class="unit">¥</span>{{ cartStore.checkedTotal.toFixed(2) }}</span>
        </div>
        <button
          class="checkout-btn"
          :disabled="!cartStore.checkedItems.length"
          @click="goCheckout"
        >结算</button>
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
      <p>购物车是空的</p>
      <button class="shop-btn-plain" @click="$router.push('/')">去逛逛</button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { showConfirmDialog, showToast } from 'vant'
import { getToken } from '@/utils/auth'

const router = useRouter()
const cartStore = useCartStore()

const checkedIds = computed({
  get: () => cartStore.items.filter(i => i.checked).map(i => i.productId),
  set: (val) => {
    cartStore.items.forEach(item => {
      item.checked = val.includes(item.productId)
    })
  }
})

const isAllChecked = computed(() => cartStore.items.length > 0 && cartStore.items.every(i => i.checked))

function formatPrice(price) { return Number(price || 0).toFixed(2) }

function handleToggleAll() {
  const allChecked = cartStore.items.every(i => i.checked)
  cartStore.items.forEach(item => { item.checked = !allChecked })
}

function handleQuantityChange(productId, val) {
  cartStore.updateQuantity(productId, val)
}

async function handleDelete(productId) {
  try {
    await showConfirmDialog({ title: '提示', message: '确定要删除这个商品吗？' })
    await cartStore.removeItem(productId)
    showToast('已删除')
  } catch {}
}

function goCheckout() {
  cartStore.clearBuyNowItem()
  router.push('/order/confirm')
}

onMounted(() => {
  if (getToken()) cartStore.loadCart()
})
</script>

<style lang="scss" scoped>
.cart-page {
  background: var(--shop-bg-secondary);
  min-height: 100vh;
}

.cart-header {
  padding: 16px var(--shop-page-pad) 12px;
  background: var(--shop-bg);

  .cart-title {
    font-size: 26px;
    font-weight: 800;
    color: var(--shop-text-primary);
  }

  .cart-count {
    font-size: 13px;
    color: var(--shop-text-secondary);
    margin-top: 2px;
  }
}

.cart-content {
  padding: var(--shop-gap) var(--shop-page-pad) 80px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--shop-card-bg);
  border-radius: var(--shop-card-radius);
  padding: 12px;
  margin-bottom: 8px;

  &:active {
    opacity: 0.95;
  }
}

.item-img {
  width: 84px;
  height: 84px;
  background: var(--shop-bg-secondary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: 10px;
  color: var(--shop-text-primary);
}

.item-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.item-price {
  color: var(--shop-accent);
  font-weight: 700;
  font-size: 16px;

  .unit {
    font-size: 12px;
  }
}

.item-delete {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;

  &:active {
    background: var(--shop-bg-secondary);
  }
}

.cart-footer {
  position: fixed;
  bottom: 50px;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  padding: 10px var(--shop-page-pad);
  border-top: 0.5px solid var(--shop-separator);
}

.select-all-text {
  font-size: 14px;
  color: var(--shop-text-secondary);
}

.cart-total {
  flex: 1;
  text-align: right;
  padding-right: 14px;

  .total-label {
    font-size: 13px;
    color: var(--shop-text-secondary);
    margin-right: 4px;
  }
}

.total-price {
  color: var(--shop-accent);
  font-size: 20px;
  font-weight: 800;

  .unit {
    font-size: 13px;
  }
}

.checkout-btn {
  height: 42px;
  padding: 0 28px;
  border: none;
  border-radius: 10px;
  background: var(--shop-accent);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  font-family: var(--shop-font);
  cursor: pointer;

  &:active {
    background: var(--shop-accent-hover);
  }

  &:disabled {
    opacity: 0.4;
  }
}

.shop-btn-plain {
  height: 44px;
  padding: 0 28px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 10px;
  background: transparent;
  color: var(--shop-text-primary);
  font-size: 15px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;
}
</style>
