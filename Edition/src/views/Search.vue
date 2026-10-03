<template>
  <div class="search-page">
    <div class="search-container">
      <!-- Search Header -->
      <div class="search-header">
        <div class="search-input-wrapper">
          <el-icon class="search-icon"><Search /></el-icon>
          <input
            v-model="searchQuery"
            type="text"
            class="search-input"
            :placeholder="$t('search.placeholder')"
            @keyup.enter="performSearch"
            autofocus
          />
          <button class="search-btn" @click="performSearch">
            <el-icon size="20"><Search /></el-icon>
          </button>
        </div>
      </div>

      <!-- Search Filters -->
      <div class="search-filters" v-if="searchQuery">
        <span class="results-count">{{ $t('search.resultsFor', { n: totalResults, q: searchQuery }) }}</span>
        <div class="filter-buttons">
          <el-dropdown>
            <el-button>
              {{ $t('search.sortBy', { sort: $t('search.' + sortBy) }) }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="changeSort('bestMatch')">{{ $t('search.bestMatch') }}</el-dropdown-item>
                <el-dropdown-item @click="changeSort('priceLowToHigh')">{{ $t('search.priceLowToHigh') }}</el-dropdown-item>
                <el-dropdown-item @click="changeSort('priceHighToLow')">{{ $t('search.priceHighToLow') }}</el-dropdown-item>
                <el-dropdown-item @click="changeSort('rating')">{{ $t('search.rating') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button>
            <el-icon><Filter /></el-icon>
            {{ $t('search.filters') }}
          </el-button>
        </div>
      </div>

      <!-- Search Results -->
      <div class="search-results" v-if="products.length">
        <div class="results-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="$router.push(`/ip/${product.id}`)"
          >
            <div class="product-image">
              <span class="deal-badge" v-if="product.originalPrice">Deal</span>
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
              <div class="product-rating" v-if="product.rating">
                <el-icon v-for="i in 5" :key="i" :class="{ filled: i <= product.rating }">
                  <StarFilled />
                </el-icon>
                <span class="rating-count">({{ product.reviewCount || 0 }})</span>
              </div>
              <div class="product-delivery">
                <el-icon size="14"><Van /></el-icon>
                <span>{{ $t('search.freeDelivery') }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Load More -->
        <div class="load-more" v-if="hasMore">
          <el-button @click="loadMore" :loading="loading">
            {{ $t('search.showMore') }}
          </el-button>
        </div>
      </div>

      <!-- No Results -->
      <div class="no-results" v-else-if="!loading && searchQuery">
        <el-icon size="64" color="#d1d1d1"><Search /></el-icon>
        <h2>{{ $t('search.noResultsTitle', { q: searchQuery }) }}</h2>
        <p>{{ $t('search.checkSpelling') }}</p>
      </div>

      <!-- Initial State -->
      <div class="initial-state" v-else-if="!searchQuery">
        <h2>{{ $t('search.explorePopular') }}</h2>
        <div class="popular-categories">
          <router-link to="/shop/deals/electronics" class="category-link">
            <el-icon size="24"><Monitor /></el-icon>
            <span>{{ $t('search.catElectronics') }}</span>
          </router-link>
          <router-link to="/shop/deals/clothing-and-accessories" class="category-link">
            <el-icon size="24"><ShoppingBag /></el-icon>
            <span>{{ $t('search.catFashion') }}</span>
          </router-link>
          <router-link to="/shop/deals/home" class="category-link">
            <el-icon size="24"><House /></el-icon>
            <span>{{ $t('search.catHome') }}</span>
          </router-link>
          <router-link to="/shop/deals/food" class="category-link">
            <el-icon size="24"><ShoppingCart /></el-icon>
            <span>{{ $t('search.catGrocery') }}</span>
          </router-link>
          <router-link to="/shop/deals/toys" class="category-link">
            <el-icon size="24"><MagicStick /></el-icon>
            <span>{{ $t('search.catToys') }}</span>
          </router-link>
          <router-link to="/shop/deals/baby" class="category-link">
            <el-icon size="24"><Present /></el-icon>
            <span>{{ $t('search.catBaby') }}</span>
          </router-link>
        </div>
      </div>

      <!-- Loading -->
      <div class="loading-state" v-if="loading">
        <div class="spinner"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { searchProducts } from '@/api'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import {
  Search, ShoppingCart, ArrowDown, Filter, StarFilled, Van,
  Monitor, ShoppingBag, House, MagicStick, Present
} from '@element-plus/icons-vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const searchQuery = ref(route.query.q || '')
const products = ref([])
const totalResults = ref(0)
const loading = ref(false)
const pageNum = ref(1)
const hasMore = ref(false)
// 存储排序键，展示时再翻译，避免切换语言后残留旧语言文案
const sortBy = ref('bestMatch')

function formatPrice(price) {
  return Number(price).toFixed(2)
}

async function performSearch() {
  if (!searchQuery.value.trim()) return

  loading.value = true
  pageNum.value = 1
  try {
    const res = await searchProducts({
      pageNum: 1,
      pageSize: 20,
      keyword: searchQuery.value.trim()
    })
    products.value = res.data?.records || []
    // 后端自定义分页 SQL 的 total 可能返回 0，此时用当前记录数兜底展示
    const total = res.data?.total || 0
    totalResults.value = total || products.value.length
    hasMore.value = total > 0
      ? products.value.length < totalResults.value
      : products.value.length >= 20
  } catch (e) {
    console.error('Search failed', e)
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  loading.value = true
  try {
    pageNum.value++
    const res = await searchProducts({
      pageNum: pageNum.value,
      pageSize: 20,
      keyword: searchQuery.value.trim()
    })
    const newProducts = res.data?.records || []
    products.value.push(...newProducts)
    const total = res.data?.total || 0
    hasMore.value = total > 0
      ? products.value.length < total
      : newProducts.length >= 20
  } catch (e) {
    console.error('Load more failed', e)
  } finally {
    loading.value = false
  }
}

function changeSort(sort) {
  sortBy.value = sort
  performSearch()
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

watch(() => route.query.q, (newQ) => {
  if (newQ) {
    searchQuery.value = newQ
    performSearch()
  }
})

onMounted(() => {
  if (searchQuery.value) {
    performSearch()
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

.search-page {
  background: $walmart-gray-100;
  min-height: 100vh;
}

.search-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 20px 24px;
}

// Search Header
.search-header {
  margin-bottom: 20px;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background: $walmart-white;
  border-radius: 24px;
  padding: 4px 4px 4px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .search-icon {
    color: $walmart-gray-400;
    margin-right: 8px;
  }

  .search-input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 16px;
    color: $walmart-gray-600;
    background: transparent;

    &::placeholder {
      color: $walmart-gray-400;
    }
  }

  .search-btn {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: $walmart-yellow;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $walmart-gray-600;

    &:hover {
      background: darken($walmart-yellow, 5%);
    }
  }
}

// Search Filters
.search-filters {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  .results-count {
    font-size: 14px;
    color: $walmart-gray-400;
  }

  .filter-buttons {
    display: flex;
    gap: 8px;
  }
}

// Search Results
.results-grid {
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

    .product-rating {
      display: flex;
      align-items: center;
      gap: 2px;
      margin-bottom: 8px;
      color: $walmart-yellow;

      .rating-count {
        font-size: 12px;
        color: $walmart-gray-400;
        margin-left: 4px;
      }
    }

    .product-delivery {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: $walmart-gray-400;
    }
  }
}

.load-more {
  display: flex;
  justify-content: center;
  padding: 40px;
}

// No Results
.no-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 24px;
  text-align: center;

  h2 {
    font-size: 20px;
    font-weight: 600;
    color: $walmart-gray-600;
    margin: 16px 0 8px;
  }

  p {
    font-size: 14px;
    color: $walmart-gray-400;
  }
}

// Initial State
.initial-state {
  padding: 40px 0;

  h2 {
    font-size: 22px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 24px;
  }
}

.popular-categories {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.category-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: $walmart-white;
  border-radius: 12px;
  text-decoration: none;
  color: $walmart-gray-600;
  transition: all 0.2s;

  &:hover {
    background: $walmart-blue;
    color: $walmart-white;
    transform: translateY(-2px);
  }

  span {
    font-size: 14px;
    font-weight: 600;
  }
}

// Loading
.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;

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
