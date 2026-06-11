<template>
  <div class="detail-page">
    <van-nav-bar title="商品详情" left-arrow @click-left="$router.back()" />

    <div v-if="product" class="detail-content">
      <!-- Product image -->
      <div class="detail-img">
        <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.productName" class="detail-img-content" />
        <svg v-else width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="#D1D1D6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
          <circle cx="8.5" cy="8.5" r="1.5"/>
          <polyline points="21 15 16 10 5 21"/>
        </svg>
      </div>

      <!-- Price section -->
      <div class="price-section">
        <div class="detail-price">
          <span class="unit">¥</span><span class="price-num">{{ formatPrice(product.price) }}</span>
        </div>
        <div class="stock-info" :class="{ low: product.stock < 10 }">
          {{ product.stock > 0 ? (product.stock < 10 ? '仅剩' + product.stock + '件' : '库存充足') : '暂时缺货' }}
        </div>
      </div>

      <!-- Product info -->
      <div class="info-section">
        <div class="detail-name">{{ product.productName }}</div>
        <div class="detail-cat" v-if="product.categoryName">
          <span class="cat-tag">{{ product.categoryName }}</span>
        </div>
      </div>

      <!-- Specs -->
      <div class="spec-section">
        <div class="spec-title">商品参数</div>
        <div class="spec-list">
          <div class="spec-item">
            <span class="spec-label">商品编号</span>
            <span class="spec-value">{{ product.id }}</span>
          </div>
          <div class="spec-item" v-if="product.categoryName">
            <span class="spec-label">商品分类</span>
            <span class="spec-value">{{ product.categoryName }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">库存数量</span>
            <span class="spec-value">{{ product.stock }}件</span>
          </div>
        </div>
      </div>

      <!-- Description -->
      <div class="desc-section">
        <div class="desc-title">商品介绍</div>
        <div class="desc-content">{{ product.description || '该商品暂无详细介绍，如有疑问请联系客服。' }}</div>
      </div>

      <!-- Bottom actions -->
      <div class="detail-footer safe-bottom">
        <div class="footer-icons">
          <button class="footer-icon" @click="$router.push('/')">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#767676" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            <span>首页</span>
          </button>
          <button class="footer-icon" @click="$router.push('/cart')">
            <div class="icon-badge-wrap">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#767676" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="9" cy="21" r="1"/>
                <circle cx="20" cy="21" r="1"/>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
              </svg>
              <span v-if="cartStore.totalCount > 0" class="icon-badge">{{ cartStore.totalCount }}</span>
            </div>
            <span>购物车</span>
          </button>
        </div>
        <button class="btn-cart" @click="addToCart" :disabled="product.stock === 0">加入购物车</button>
        <button class="btn-buy" @click="buyNow" :disabled="product.stock === 0">立即购买</button>
      </div>
    </div>

    <van-loading v-else class="page-loading" size="24px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api'
import { useCartStore } from '@/stores/cart'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const product = ref(null)

function formatPrice(price) { return Number(price).toFixed(2) }

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
  } catch (e) {
    showToast('商品不存在')
    router.back()
  }
})

async function addToCart() {
  try {
    await cartStore.addItem(product.value)
    showToast('已加入购物车')
  } catch (e) {
    showToast(e.message || '添加失败')
  }
}

function buyNow() {
  cartStore.setBuyNowItem(product.value)
  router.push('/order/confirm')
}
</script>

<style lang="scss" scoped>
.detail-page {
  background: var(--shop-bg-secondary);
  min-height: 100vh;
  padding-bottom: 80px;
}

.detail-img {
  height: 375px;
  background: var(--shop-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  .detail-img-content {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.price-section {
  background: var(--shop-bg);
  padding: 14px var(--shop-page-pad);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-price {
  color: var(--shop-accent);

  .unit {
    font-size: 16px;
    font-weight: 700;
  }

  .price-num {
    font-size: 32px;
    font-weight: 800;
    letter-spacing: -1px;
  }
}

.stock-info {
  font-size: 12px;
  color: var(--shop-text-secondary);

  &.low {
    color: var(--shop-accent);
  }
}

.info-section {
  background: var(--shop-bg);
  padding: 14px var(--shop-page-pad);
  margin-top: 8px;

  .detail-name {
    font-size: 17px;
    font-weight: 600;
    line-height: 1.5;
    color: var(--shop-text-primary);
  }

  .detail-cat {
    margin-top: 10px;
  }

  .cat-tag {
    display: inline-block;
    font-size: 12px;
    color: var(--shop-accent);
    background: var(--shop-accent-light);
    padding: 3px 10px;
    border-radius: 6px;
    font-weight: 600;
  }
}

.desc-section,
.spec-section {
  background: var(--shop-bg);
  padding: 14px var(--shop-page-pad);
  margin-top: 8px;
}

.desc-title,
.spec-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--shop-text-primary);
  margin-bottom: 10px;
}

.desc-content {
  font-size: 14px;
  color: var(--shop-text-secondary);
  line-height: 1.7;
}

.spec-list .spec-item {
  display: flex;
  justify-content: space-between;
  padding: 11px 0;
  border-bottom: 0.5px solid var(--shop-separator);
  font-size: 13px;

  &:last-child {
    border-bottom: none;
  }

  .spec-label {
    color: var(--shop-text-secondary);
  }

  .spec-value {
    color: var(--shop-text-primary);
  }
}

.detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 0;
  padding: 10px var(--shop-page-pad);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 0.5px solid var(--shop-separator);
}

.footer-icons {
  display: flex;
  gap: 4px;
  margin-right: 8px;
}

.footer-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  font-size: 10px;
  color: var(--shop-text-secondary);
  width: 48px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  position: relative;
}

.icon-badge-wrap {
  position: relative;
}

.icon-badge {
  position: absolute;
  top: -6px;
  right: -8px;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  font-size: 10px;
  font-weight: 600;
  color: #fff;
  background: var(--shop-accent);
  border-radius: 8px;
  padding: 0 4px;
}

.btn-cart {
  flex: 1;
  height: 42px;
  border-radius: 10px 0 0 10px;
  background: #191919;
  color: #fff;
  border: none;
  font-weight: 700;
  font-size: 14px;
  font-family: var(--shop-font);
  cursor: pointer;

  &:active {
    opacity: 0.8;
  }

  &:disabled {
    opacity: 0.4;
  }
}

.btn-buy {
  flex: 1;
  height: 42px;
  border-radius: 0 10px 10px 0;
  background: var(--shop-accent);
  color: #fff;
  border: none;
  font-weight: 700;
  font-size: 14px;
  font-family: var(--shop-font);
  cursor: pointer;

  &:active {
    background: var(--shop-accent-hover);
  }

  &:disabled {
    opacity: 0.4;
  }
}

.page-loading {
  text-align: center;
  padding: 60px;
}
</style>
