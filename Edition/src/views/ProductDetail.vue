<template>
  <div class="product-detail-page">
    <div class="detail-container" v-if="product">
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <router-link to="/">Home</router-link>
        <span class="separator">/</span>
        <router-link v-if="product.categoryName" :to="`/category/${product.categoryId}`">
          {{ product.categoryName }}
        </router-link>
        <span class="separator">/</span>
        <span class="current">{{ product.productName }}</span>
      </div>

      <!-- Main Content -->
      <div class="detail-main">
        <!-- Product Image -->
        <div class="product-gallery">
          <div class="main-image">
            <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
            <span class="deal-tag" v-if="isDeal">Deal</span>
          </div>
        </div>

        <!-- Product Info -->
        <div class="product-info">
          <h1 class="product-title">{{ product.productName }}</h1>

          <div class="product-brand" v-if="product.brandName">
            <span>{{ $t('product.brand') }}</span>
            <a href="#">{{ product.brandName }}</a>
          </div>

          <div class="product-rating">
            <div class="stars">
              <el-icon v-for="i in 5" :key="i" :class="{ filled: i <= product.rating }">
                <StarFilled />
              </el-icon>
            </div>
            <span class="rating-count">{{ $t('product.reviews', { n: product.reviewCount || 0 }) }}</span>
          </div>

          <!-- Price -->
          <div class="price-section">
            <div class="current-price">
              <span class="dollar">$</span>
              <span class="amount">{{ formatPrice(product.price) }}</span>
            </div>
            <div class="original-price" v-if="product.originalPrice">
              <span>Was ${{ formatPrice(product.originalPrice) }}</span>
            </div>
            <div class="savings" v-if="savings > 0">
              <span>You save: ${{ savings.toFixed(2) }} ({{ savingsPercent }}%)</span>
            </div>
          </div>

          <!-- Stock -->
          <div class="stock-status" :class="{ low: product.stock < 10, out: product.stock === 0 }">
            <el-icon v-if="product.stock > 0"><CircleCheck /></el-icon>
            <el-icon v-else><CircleClose /></el-icon>
            <span v-if="product.stock > 10">{{ $t('common.inStock') }}</span>
            <span v-else-if="product.stock > 0">{{ $t('common.onlyLeft', { n: product.stock }) }} - {{ $t('common.orderSoon') }}</span>
            <span v-else>{{ $t('common.outOfStock') }}</span>
          </div>

          <!-- Quantity -->
          <div class="quantity-section" v-if="product.stock > 0">
            <label>{{ $t('product.qty') }}</label>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="Math.min(product.stock, 10)"
              size="large"
            />
          </div>

          <!-- Delivery Options -->
          <div class="delivery-options">
            <div class="delivery-option">
              <el-icon size="20"><Van /></el-icon>
              <div class="delivery-info">
                <span class="delivery-label">{{ $t('product.freeDelivery') }}</span>
                <span class="delivery-detail">{{ $t('product.tomorrow') }}</span>
              </div>
            </div>
            <div class="delivery-option">
              <el-icon size="20"><Location /></el-icon>
              <div class="delivery-info">
                <span class="delivery-label">{{ $t('product.freePickup') }}</span>
                <span class="delivery-detail">{{ $t('product.todayAtSupercenter') }}</span>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              class="add-to-cart-btn"
              @click="addToCart"
              :disabled="product.stock === 0"
            >
              <el-icon><ShoppingCart /></el-icon>
              {{ $t('product.addToCart') }}
            </el-button>
            <el-button
              size="large"
              class="buy-now-btn"
              @click="buyNow"
              :disabled="product.stock === 0"
            >
              {{ $t('product.buyNow') }}
            </el-button>
          </div>

          <!-- Favorite -->
          <div class="favorite-btn" @click="toggleFavorite">
            <el-icon size="20">
              <StarFilled v-if="isFavorite" />
              <Star v-else />
            </el-icon>
            <span>{{ isFavorite ? $t('product.saved') : $t('product.saveForLater') }}</span>
          </div>
        </div>
      </div>

      <!-- Product Details Section -->
      <div class="detail-sections">
        <!-- Highlights -->
        <div class="detail-card">
          <h3>{{ $t('product.highlights') }}</h3>
          <ul class="highlights-list">
            <li v-if="product.categoryName">{{ $t('product.categoryLabel', { name: product.categoryName }) }}</li>
            <li>{{ $t('product.productId', { id: product.id }) }}</li>
            <li>{{ $t('product.stockUnits', { n: product.stock }) }}</li>
          </ul>
        </div>

        <!-- Description -->
        <div class="detail-card">
          <h3>{{ $t('product.description') }}</h3>
          <p class="description-text">
            {{ product.description || $t('product.noDescription') }}
          </p>
        </div>

        <!-- Specifications -->
        <div class="detail-card">
          <h3>{{ $t('product.specifications') }}</h3>
          <table class="spec-table">
            <tr>
              <td class="spec-label">{{ $t('product.sku') }}</td>
              <td class="spec-value">{{ product.id }}</td>
            </tr>
            <tr v-if="product.categoryName">
              <td class="spec-label">{{ $t('product.category') }}</td>
              <td class="spec-value">{{ product.categoryName }}</td>
            </tr>
            <tr>
              <td class="spec-label">{{ $t('product.stockQuantity') }}</td>
              <td class="spec-value">{{ product.stock }}</td>
            </tr>
          </table>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div class="loading-state" v-else>
      <div class="spinner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { getProductDetail, checkFavorite, addFavorite, removeFavorite } from '@/api'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import {
  ShoppingCart, Star, StarFilled, CircleCheck, CircleClose,
  Van, Location
} from '@element-plus/icons-vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const product = ref(null)
const quantity = ref(1)
const isFavorite = ref(false)

const isDeal = computed(() => {
  return product.value?.originalPrice && product.value.originalPrice > product.value.price
})

const savings = computed(() => {
  if (!product.value?.originalPrice) return 0
  return product.value.originalPrice - product.value.price
})

const savingsPercent = computed(() => {
  if (!product.value?.originalPrice) return 0
  return Math.round((savings.value / product.value.originalPrice) * 100)
})

function formatPrice(price) {
  return Number(price).toFixed(2)
}

async function addToCart() {
  if (!getToken()) {
    router.push('/login')
    return
  }
  try {
    await cartStore.addItem(product.value, quantity.value)
    ElMessage.success(t('common.addedToCart'))
  } catch (e) {
    ElMessage.error(t('common.failedToAddCart'))
  }
}

function buyNow() {
  if (!getToken()) {
    router.push('/login')
    return
  }
  cartStore.addItem(product.value, quantity.value).then(() => {
    router.push('/cart')
  })
}

async function toggleFavorite() {
  if (!getToken()) {
    router.push('/login')
    return
  }
  try {
    if (isFavorite.value) {
      await removeFavorite(product.value.id)
      isFavorite.value = false
      ElMessage.success(t('common.removedFromFavorites'))
    } else {
      await addFavorite(product.value.id)
      isFavorite.value = true
      ElMessage.success(t('common.addedToFavorites'))
    }
  } catch (e) {
    ElMessage.error(t('common.failedToUpdateFavorites'))
  }
}

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data

    if (getToken()) {
      const favRes = await checkFavorite(route.params.id)
      isFavorite.value = favRes.data
    }
  } catch (e) {
    ElMessage.error(t('common.productNotFound'))
    router.push('/')
  }
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

.product-detail-page {
  background: $walmart-white;
  min-height: 100vh;
}

.detail-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 20px 24px;
}

// Breadcrumb
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: $walmart-gray-400;
  margin-bottom: 20px;

  a {
    color: $walmart-blue;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .separator {
    color: $walmart-gray-300;
  }

  .current {
    color: $walmart-gray-500;
  }
}

// Main Content
.detail-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

// Product Gallery
.product-gallery {
  position: sticky;
  top: 100px;
  align-self: start;
}

.main-image {
  position: relative;
  background: $walmart-gray-100;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 1;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    padding: 24px;
  }

  .deal-tag {
    position: absolute;
    top: 16px;
    left: 16px;
    padding: 6px 12px;
    background: $walmart-yellow;
    color: $walmart-gray-600;
    font-size: 14px;
    font-weight: 700;
    border-radius: 4px;
  }
}

// Product Info
.product-info {
  .product-title {
    font-size: 24px;
    font-weight: 600;
    color: $walmart-gray-600;
    line-height: 1.3;
    margin-bottom: 12px;
  }

  .product-brand {
    font-size: 14px;
    color: $walmart-gray-400;
    margin-bottom: 12px;

    a {
      color: $walmart-blue;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .product-rating {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 20px;

    .stars {
      display: flex;
      gap: 2px;
      color: $walmart-yellow;

      .filled {
        color: $walmart-yellow;
      }
    }

    .rating-count {
      font-size: 14px;
      color: $walmart-blue;
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

// Price Section
.price-section {
  margin-bottom: 20px;

  .current-price {
    display: flex;
    align-items: flex-start;
    margin-bottom: 4px;

    .dollar {
      font-size: 16px;
      font-weight: 700;
      color: $walmart-gray-600;
      margin-top: 4px;
    }

    .amount {
      font-size: 36px;
      font-weight: 700;
      color: $walmart-gray-600;
      line-height: 1;
    }
  }

  .original-price {
    font-size: 14px;
    color: $walmart-gray-400;
    text-decoration: line-through;
    margin-bottom: 4px;
  }

  .savings {
    font-size: 14px;
    color: $walmart-blue;
    font-weight: 600;
  }
}

// Stock Status
.stock-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  margin-bottom: 20px;

  &.low {
    color: #e31837;
  }

  &.out {
    color: $walmart-gray-400;
  }
}

// Quantity
.quantity-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;

  label {
    font-size: 14px;
    font-weight: 600;
    color: $walmart-gray-600;
  }
}

// Delivery Options
.delivery-options {
  margin-bottom: 24px;
}

.delivery-option {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid $walmart-gray-200;

  &:last-child {
    border-bottom: none;
  }

  .delivery-info {
    display: flex;
    flex-direction: column;

    .delivery-label {
      font-size: 14px;
      font-weight: 600;
      color: $walmart-gray-600;
    }

    .delivery-detail {
      font-size: 13px;
      color: $walmart-gray-400;
    }
  }
}

// Action Buttons
.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;

  .add-to-cart-btn {
    flex: 1;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 24px;
    background: $walmart-blue;
    border-color: $walmart-blue;

    &:hover {
      background: $walmart-blue-dark;
      border-color: $walmart-blue-dark;
    }
  }

  .buy-now-btn {
    flex: 1;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 24px;
    background: $walmart-yellow;
    border-color: $walmart-yellow;
    color: $walmart-gray-600;

    &:hover {
      background: darken($walmart-yellow, 5%);
      border-color: darken($walmart-yellow, 5%);
    }
  }
}

// Favorite Button
.favorite-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  color: $walmart-blue;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: $walmart-gray-100;
  }
}

// Detail Sections
.detail-sections {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.detail-card {
  background: $walmart-white;
  border: 1px solid $walmart-gray-200;
  border-radius: 12px;
  padding: 24px;

  h3 {
    font-size: 18px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid $walmart-gray-200;
  }
}

.highlights-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    padding: 8px 0;
    font-size: 14px;
    color: $walmart-gray-500;
    border-bottom: 1px solid $walmart-gray-100;

    &:last-child {
      border-bottom: none;
    }

    &::before {
      content: '•';
      color: $walmart-blue;
      margin-right: 8px;
    }
  }
}

.description-text {
  font-size: 14px;
  color: $walmart-gray-500;
  line-height: 1.6;
}

.spec-table {
  width: 100%;
  border-collapse: collapse;

  tr {
    border-bottom: 1px solid $walmart-gray-100;

    &:last-child {
      border-bottom: none;
    }
  }

  td {
    padding: 12px 0;
    font-size: 14px;
  }

  .spec-label {
    color: $walmart-gray-400;
    width: 40%;
  }

  .spec-value {
    color: $walmart-gray-600;
    font-weight: 500;
  }
}

// Loading
.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;

  .spinner {
    width: 40px;
    height: 40px;
    border: 3px solid $walmart-gray-200;
    border-top-color: $walmart-blue;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
