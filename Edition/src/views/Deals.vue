<template>
  <div class="deals-page">
    <div class="deals-container">
      <!-- Hero Banner -->
      <div class="deals-hero">
        <div class="hero-content">
          <span class="hero-tag">{{ $t('deals.heroTag') }}</span>
          <h1>{{ $t('deals.heroTitle') }}</h1>
          <p>{{ $t('deals.heroSub') }}</p>
          <button class="walmart-btn walmart-btn-yellow">{{ $t('deals.shopNow') }}</button>
        </div>
      </div>

      <!-- Deals Categories -->
      <div class="deals-categories">
        <h2>{{ $t('deals.byCategory') }}</h2>
        <div class="category-grid">
          <router-link
            v-for="cat in categories"
            :key="cat.id"
            :to="cat.link"
            class="category-card"
          >
            <div class="card-image" :style="{ background: cat.bg }">
              <span class="card-icon">{{ cat.icon }}</span>
            </div>
            <div class="card-info">
              <span class="card-name">{{ $t('deals.cat_' + cat.id) }}</span>
              <span class="card-deal">{{ $t('deals.deal_' + cat.id) }}</span>
            </div>
          </router-link>
        </div>
      </div>

      <!-- Featured Deals -->
      <div class="featured-deals">
        <div class="section-header">
          <h2>{{ $t('deals.todaysBest') }}</h2>
          <router-link to="/search" class="view-all">{{ $t('deals.viewAll') }}</router-link>
        </div>

        <div class="deals-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="$router.push(`/ip/${product.id}`)"
          >
            <div class="product-image">
              <span class="deal-badge">{{ $t('common.deal') }}</span>
              <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
              <button class="add-to-cart-btn" @click.stop="addToCart(product)">
                <el-icon size="18"><ShoppingCart /></el-icon>
              </button>
            </div>
            <div class="product-info">
              <div class="product-pricing">
                <span class="now-price">
                  <span class="dollar">$</span>{{ formatPrice(product.price) }}
                </span>
                <span class="was-price" v-if="product.originalPrice">
                  ${{ formatPrice(product.originalPrice) }}
                </span>
              </div>
              <p class="product-name">{{ product.productName }}</p>
              <div class="product-savings" v-if="product.originalPrice">
                <span>{{ $t('deals.youSave', { v: (product.originalPrice - product.price).toFixed(2) }) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- More Sections -->
      <div class="more-sections">
        <div class="section-card">
          <h3>{{ $t('deals.top100Title') }}</h3>
          <p>{{ $t('deals.top100Text') }}</p>
          <router-link to="/search" class="section-link">{{ $t('deals.shopNow') }}</router-link>
        </div>
        <div class="section-card">
          <h3>{{ $t('deals.flashTitle') }}</h3>
          <p>{{ $t('deals.flashText') }}</p>
          <router-link to="/search" class="section-link">{{ $t('deals.shopNow') }}</router-link>
        </div>
        <div class="section-card">
          <h3>{{ $t('deals.clearanceTitle') }}</h3>
          <p>{{ $t('deals.clearanceText') }}</p>
          <router-link to="/search" class="section-link">{{ $t('deals.shopNow') }}</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { searchProducts } from '@/api'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const cartStore = useCartStore()

const categories = [
  { id: 1, icon: '🏷️', bg: '#0071dc', link: '/shop/deals' },
  { id: 2, icon: '🔥', bg: '#e31837', link: '/shop/deals/top100' },
  { id: 3, icon: '💄', bg: '#ff69b4', link: '/shop/deals/beauty-and-personal-care' },
  { id: 4, icon: '🏠', bg: '#2a8703', link: '/shop/deals/household-essentials' },
  { id: 5, icon: '💻', bg: '#6b5ce7', link: '/shop/deals/electronics' },
  { id: 6, icon: '🧸', bg: '#ffc220', link: '/shop/deals/toys' },
  { id: 7, icon: '👶', bg: '#ff9a9e', link: '/shop/deals/baby' },
  { id: 8, icon: '🛋️', bg: '#a8edea', link: '/shop/deals/all-home' },
  { id: 9, icon: '👗', bg: '#fed6e3', link: '/shop/deals/clothing-and-accessories' },
  { id: 10, icon: '🌿', bg: '#d4fc79', link: '/shop/deals/patio-and-garden' },
  { id: 11, icon: '🍕', bg: '#ffecd2', link: '/shop/deals/food' },
  { id: 12, icon: '📚', bg: '#a18cd1', link: '/shop/deals/school-office-art' }
]

const products = ref([])

function formatPrice(price) {
  return Number(price).toFixed(2)
}

async function addToCart(product) {
  if (!getToken()) {
    router.push('/login')
    return
  }
  try {
    await cartStore.addItem(product)
    ElMessage.success(t('common.addedToCart'))
  } catch (e) {
    ElMessage.error(t('common.failedToAddCart'))
  }
}

onMounted(async () => {
  try {
    const res = await searchProducts({ pageNum: 1, pageSize: 12 })
    products.value = res.data?.records || []
  } catch (e) {
    console.error('Failed to load deals', e)
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

.deals-page {
  background: $walmart-gray-100;
  min-height: 100vh;
}

.deals-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 20px 24px;
}

// Hero
.deals-hero {
  background: linear-gradient(135deg, $walmart-blue 0%, $walmart-blue-dark 100%);
  border-radius: 12px;
  padding: 48px;
  color: $walmart-white;
  margin-bottom: 32px;

  .hero-content {
    max-width: 500px;

    .hero-tag {
      display: inline-block;
      padding: 6px 12px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 4px;
      font-size: 12px;
      font-weight: 700;
      margin-bottom: 16px;
      letter-spacing: 1px;
    }

    h1 {
      font-size: 36px;
      font-weight: 800;
      margin-bottom: 8px;
    }

    p {
      font-size: 18px;
      opacity: 0.9;
      margin-bottom: 24px;
    }
  }
}

// Categories
.deals-categories {
  margin-bottom: 40px;

  h2 {
    font-size: 22px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 20px;
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.category-card {
  background: $walmart-white;
  border-radius: 12px;
  overflow: hidden;
  text-decoration: none;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  .card-image {
    height: 100px;
    display: flex;
    align-items: center;
    justify-content: center;

    .card-icon {
      font-size: 40px;
    }
  }

  .card-info {
    padding: 12px;
    text-align: center;

    .card-name {
      display: block;
      font-size: 14px;
      font-weight: 600;
      color: $walmart-gray-600;
      margin-bottom: 4px;
    }

    .card-deal {
      font-size: 12px;
      color: $walmart-gray-400;
    }
  }
}

// Featured Deals
.featured-deals {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  h2 {
    font-size: 22px;
    font-weight: 700;
    color: $walmart-gray-600;
  }

  .view-all {
    color: $walmart-blue;
    font-size: 14px;
    font-weight: 600;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.deals-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.product-card {
  background: $walmart-white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .product-image {
    position: relative;
    height: 200px;
    background: $walmart-gray-100;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
      padding: 16px;
    }

    .deal-badge {
      position: absolute;
      top: 8px;
      left: 8px;
      padding: 4px 8px;
      background: $walmart-yellow;
      color: $walmart-gray-600;
      font-size: 12px;
      font-weight: 700;
      border-radius: 4px;
    }

    .add-to-cart-btn {
      position: absolute;
      bottom: 8px;
      right: 8px;
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: $walmart-blue;
      color: $walmart-white;
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.2s;

      &:hover {
        background: $walmart-blue-dark;
      }
    }
  }

  &:hover .add-to-cart-btn {
    opacity: 1;
  }

  .product-info {
    padding: 12px;

    .product-pricing {
      margin-bottom: 8px;

      .now-price {
        font-size: 18px;
        font-weight: 700;
        color: $walmart-gray-600;

        .dollar {
          font-size: 12px;
          vertical-align: top;
          position: relative;
          top: 2px;
        }
      }

      .was-price {
        font-size: 14px;
        color: $walmart-gray-400;
        text-decoration: line-through;
        margin-left: 8px;
      }
    }

    .product-name {
      font-size: 14px;
      color: $walmart-gray-600;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      margin-bottom: 8px;
    }

    .product-savings {
      font-size: 12px;
      color: $walmart-blue;
      font-weight: 600;
    }
  }
}

// More Sections
.more-sections {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.section-card {
  background: $walmart-white;
  border-radius: 12px;
  padding: 24px;
  text-align: center;

  h3 {
    font-size: 18px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: $walmart-gray-400;
    margin-bottom: 16px;
  }

  .section-link {
    display: inline-block;
    padding: 10px 24px;
    background: $walmart-blue;
    color: $walmart-white;
    text-decoration: none;
    border-radius: 24px;
    font-weight: 600;
    transition: background 0.2s;

    &:hover {
      background: $walmart-blue-dark;
    }
  }
}
</style>
