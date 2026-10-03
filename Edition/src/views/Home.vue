<template>
  <div class="home-page">
    <!-- Hero Carousel -->
    <section class="hero-section">
      <div class="hero-container">
        <el-carousel :autoplay="true" :interval="5000" height="400px" indicator-position="none">
          <el-carousel-item v-for="banner in banners" :key="banner.id">
            <div class="hero-banner" :style="{ background: banner.bg }">
              <div class="hero-content">
                <span class="hero-tag" v-if="banner.key">{{ $t('home.hero' + banner.key + 'Tag') }}</span>
                <h1 class="hero-title">{{ $t('home.hero' + banner.key + 'Title') }}</h1>
                <p class="hero-subtitle">{{ $t('home.hero' + banner.key + 'Sub') }}</p>
                <button class="walmart-btn walmart-btn-yellow" @click="$router.push(banner.link)">
                  {{ $t('home.shopNow') }}
                </button>
              </div>
              <div class="hero-image" v-if="banner.image">
                <img :src="banner.image" :alt="banner.title" />
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>

        <!-- Carousel Controls -->
        <div class="carousel-controls">
          <button class="carousel-btn" @click="prevBanner">
            <el-icon size="20"><ArrowLeft /></el-icon>
          </button>
          <button class="carousel-btn" @click="nextBanner">
            <el-icon size="20"><ArrowRight /></el-icon>
          </button>
        </div>
      </div>
    </section>

    <!-- Deals Section -->
    <section class="deals-section">
      <div class="section-container">
        <div class="section-header">
          <div>
            <h2>{{ $t('home.shopAllDeals') }}</h2>
            <p class="section-subtitle">{{ $t('home.upTo50') }}</p>
          </div>
          <router-link to="/shop/deals" class="view-all-link">{{ $t('common.viewAll') }}</router-link>
        </div>

        <div class="deals-carousel" ref="dealsCarousel">
          <div class="deals-track">
            <div
              v-for="product in dealsProducts"
              :key="product.id"
              class="product-card"
              @click="$router.push(`/ip/${product.id}`)"
            >
              <div class="product-image">
                <span class="deal-badge">{{ $t('common.deal') }}</span>
                <button class="favorite-btn" @click.stop="toggleFavorite(product)">
                  <el-icon size="20"><Star /></el-icon>
                </button>
                <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
              </div>
              <div class="product-info">
                <div class="product-pricing">
                  <span class="now-price">
                    <span class="dollar">$</span>{{ formatPrice(product.price) }}
                  </span>
                  <span class="was-price" v-if="product.originalPrice">${{ formatPrice(product.originalPrice) }}</span>
                </div>
                <p class="product-name">{{ product.productName }}</p>
                <div class="product-actions">
                  <button v-if="product.stock > 5" class="add-btn" @click.stop="addToCart(product)">
                    <el-icon size="14"><Plus /></el-icon> {{ $t('common.add') }}
                  </button>
                  <button v-else class="options-btn">{{ $t('common.options') }}</button>
                </div>
              </div>
            </div>
          </div>
          <button class="carousel-arrow carousel-prev" @click="scrollDeals(-1)">
            <el-icon size="20"><ArrowLeft /></el-icon>
          </button>
          <button class="carousel-arrow carousel-next" @click="scrollDeals(1)">
            <el-icon size="20"><ArrowRight /></el-icon>
          </button>
        </div>
      </div>
    </section>

    <!-- Category Deals Grid -->
    <section class="category-deals-section">
      <div class="section-container">
        <div class="category-grid">
          <router-link
            v-for="cat in categoryDeals"
            :key="cat.id"
            :to="cat.link"
            class="category-deal-card"
          >
            <img :src="cat.image" :alt="$t('home.catDeals' + cat.id)" />
            <div class="category-deal-info">
              <p>{{ $t('home.catDeals' + cat.id) }}</p>
              <span class="shop-deals-link">{{ $t('home.shopDeals') }}</span>
            </div>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Shop by Category -->
    <section class="shop-by-category">
      <div class="section-container">
        <div class="section-header">
          <h2>{{ $t('home.shopAllDealsCategory') }}</h2>
          <router-link to="/shop/deals" class="view-all-link">{{ $t('common.viewAll') }}</router-link>
        </div>

        <div class="category-tabs">
          <router-link
            v-for="tab in categoryTabs"
            :key="tab.id"
            :to="tab.link"
            class="category-tab"
          >
            {{ $t('home.tabs.' + tab.id) }}
          </router-link>
        </div>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="featured-section">
      <div class="section-container">
        <div class="section-header">
          <h2>{{ $t('home.popularPicks') }}</h2>
          <router-link to="/search" class="view-all-link">{{ $t('common.viewAll') }}</router-link>
        </div>

        <div class="products-grid">
          <div
            v-for="product in featuredProducts"
            :key="product.id"
            class="product-card"
            @click="$router.push(`/ip/${product.id}`)"
          >
            <div class="product-image">
              <button class="favorite-btn" @click.stop="toggleFavorite(product)">
                <el-icon size="20"><Star /></el-icon>
              </button>
              <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
            </div>
            <div class="product-info">
              <div class="product-pricing">
                <span class="now-price">
                  <span class="dollar">$</span>{{ formatPrice(product.price) }}
                </span>
              </div>
              <p class="product-name">{{ product.productName }}</p>
              <div class="product-actions">
                <button v-if="product.stock > 5" class="add-btn" @click.stop="addToCart(product)">
                  <el-icon size="14"><Plus /></el-icon> {{ $t('common.add') }}
                </button>
                <button v-else class="options-btn">{{ $t('common.options') }}</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { searchProducts, getCategoryList } from '@/api'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, ShoppingCart, Star, Plus } from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const cartStore = useCartStore()

const banners = [
  {
    id: 1,
    key: 1,
    bg: 'linear-gradient(135deg, #0071dc 0%, #004f9a 100%)',
    link: '/shop/deals',
    image: null
  },
  {
    id: 2,
    key: 2,
    bg: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)',
    link: '/shop/deals/under-25',
    image: null
  },
  {
    id: 3,
    key: 3,
    bg: 'linear-gradient(135deg, #2a8703 0%, #1a5c02 100%)',
    link: '/shop/deals/college',
    image: null
  },
  {
    id: 4,
    key: 4,
    bg: 'linear-gradient(135deg, #ffc220 0%, #e6ac00 100%)',
    link: '/account',
    image: null
  }
]

const categoryDeals = [
  { id: 1, image: '/category-snacks.jpg', link: '/shop/deals/food/snacks-and-sweet-treats' },
  { id: 2, image: '/category-beauty.jpg', link: '/shop/deals/beauty-and-personal-care' },
  { id: 3, image: '/category-fragrance.jpg', link: '/shop/deals/beauty-and-personal-care/fragrance' },
  { id: 4, image: '/category-fashion.jpg', link: '/shop/deals/clothing-and-accessories' },
  { id: 5, image: '/category-vacuums.jpg', link: '/shop/deals/all-home/floor-care' }
]

const categoryTabs = [
  { id: 'deals', link: '/shop/deals' },
  { id: 'top100', link: '/shop/deals/top100' },
  { id: 'beauty', link: '/shop/deals/beauty-and-personal-care' },
  { id: 'household', link: '/shop/deals/household-essentials' },
  { id: 'tech', link: '/shop/deals/electronics' },
  { id: 'toys', link: '/shop/deals/toys' },
  { id: 'baby', link: '/shop/deals/baby' },
  { id: 'home', link: '/shop/deals/all-home' },
  { id: 'fashion', link: '/shop/deals/clothing-and-accessories' },
  { id: 'patio', link: '/shop/deals/patio-and-garden' },
  { id: 'food', link: '/shop/deals/food' },
  { id: 'school', link: '/shop/deals/school-office-art' }
]

const dealsProducts = ref([])
const featuredProducts = ref([])

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

async function toggleFavorite(product) {
  if (!getToken()) {
    router.push('/login')
    return
  }
  try {
    const { addFavorite, removeFavorite, checkFavorite } = await import('@/api')
    const res = await checkFavorite(product.id)
    if (res.data) {
      await removeFavorite(product.id)
      ElMessage.success(t('common.removedFromFavorites'))
    } else {
      await addFavorite(product.id)
      ElMessage.success(t('common.addedToFavorites'))
    }
  } catch (e) {
    ElMessage.error(t('common.failedToUpdateFavorites'))
  }
}

function scrollDeals(direction) {
  const container = document.querySelector('.deals-carousel')
  if (container) {
    container.scrollBy({ left: direction * 300, behavior: 'smooth' })
  }
}

function prevBanner() {
  // Handled by el-carousel
}

function nextBanner() {
  // Handled by el-carousel
}

onMounted(async () => {
  try {
    const [dealsRes, featuredRes] = await Promise.all([
      searchProducts({ pageNum: 1, pageSize: 12 }).catch(() => ({ data: { records: [] } })),
      searchProducts({ pageNum: 1, pageSize: 8 }).catch(() => ({ data: { records: [] } }))
    ])
    dealsProducts.value = dealsRes.data?.records || []
    featuredProducts.value = featuredRes.data?.records || []
  } catch (e) {
    console.error('Failed to load products', e)
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

.home-page {
  background: $walmart-gray-100;
}

.section-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
}

// Hero Section
.hero-section {
  padding: 20px 0;
}

.hero-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
}

.hero-banner {
  height: 400px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 48px 60px;
  color: $walmart-white;
  overflow: hidden;
}

.hero-content {
  max-width: 500px;
  z-index: 1;

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

  .hero-title {
    font-size: 40px;
    font-weight: 800;
    margin-bottom: 12px;
    line-height: 1.1;
  }

  .hero-subtitle {
    font-size: 18px;
    opacity: 0.9;
    margin-bottom: 24px;
  }
}

.carousel-controls {
  position: absolute;
  bottom: 20px;
  right: 24px;
  display: flex;
  gap: 8px;

  .carousel-btn {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: $walmart-white;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $walmart-gray-600;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);

    &:hover {
      background: $walmart-gray-100;
    }
  }
}

// Deals Section
.deals-section {
  padding: 20px 0;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;

  h2 {
    font-size: 24px;
    font-weight: 700;
    color: $walmart-gray-600;
  }

  .section-subtitle {
    font-size: 16px;
    color: $walmart-gray-400;
    margin-top: 4px;
  }

  .view-all-link {
    color: $walmart-blue;
    font-size: 14px;
    font-weight: 600;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.deals-carousel {
  position: relative;
  overflow-x: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.deals-track {
  display: flex;
  gap: 16px;
  padding: 4px 0;
}

.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: $walmart-white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 10;

  &:hover {
    background: $walmart-gray-100;
  }

  &.carousel-prev {
    left: 12px;
  }

  &.carousel-next {
    right: 12px;
  }
}

// Product Card
.product-card {
  min-width: 220px;
  max-width: 220px;
  background: $walmart-white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
  flex-shrink: 0;

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

    .favorite-btn {
      position: absolute;
      top: 8px;
      right: 8px;
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.9);
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $walmart-gray-400;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);

      &:hover {
        color: $walmart-blue;
        background: $walmart-white;
      }
    }
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

    .product-actions {
      .add-btn {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 8px 16px;
        background: $walmart-blue;
        color: $walmart-white;
        border: none;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 600;
        cursor: pointer;

        &:hover {
          background: $walmart-blue-dark;
        }
      }

      .options-btn {
        display: inline-flex;
        align-items: center;
        padding: 8px 16px;
        background: $walmart-white;
        color: $walmart-gray-600;
        border: 1px solid $walmart-gray-300;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 600;
        cursor: pointer;

        &:hover {
          border-color: $walmart-blue;
          color: $walmart-blue;
        }
      }
    }
  }
}

// Category Deals
.category-deals-section {
  padding: 20px 0;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.category-deal-card {
  background: $walmart-white;
  border-radius: 8px;
  overflow: hidden;
  text-decoration: none;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  img {
    width: 100%;
    height: 120px;
    object-fit: cover;
  }

  .category-deal-info {
    padding: 12px;

    p {
      font-size: 14px;
      color: $walmart-gray-600;
      margin-bottom: 8px;
    }

    .shop-deals-link {
      font-size: 14px;
      color: $walmart-blue;
      font-weight: 600;
    }
  }
}

// Shop by Category
.shop-by-category {
  padding: 20px 0;
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-tab {
  padding: 10px 16px;
  background: $walmart-white;
  border-radius: 20px;
  text-decoration: none;
  color: $walmart-gray-600;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;

  &:hover {
    background: $walmart-blue;
    color: $walmart-white;
  }
}

// Featured Section
.featured-section {
  padding: 20px 0 40px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
</style>
