<template>
  <div class="home-page">
    <!-- Search bar -->
    <div class="search-bar">
      <img src="https://www.walmart.com/favicon.ico" alt="Walmart" class="logo" style="width: 28px; height: 28px; border-radius: 4px;" />
      <div class="search-wrapper">
        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input class="search-input" placeholder="搜索商品" readonly @click="$router.push('/search')" />
      </div>
      <button class="search-action" aria-label="消息通知">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
        </svg>
      </button>
    </div>

    <!-- Banner -->
    <div class="banner-section">
      <van-swipe :autoplay="4000" lazy-render :indicator-color="'rgba(0,113,220,0.4)'">
        <van-swipe-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ background: banner.bg }">
            <div class="banner-content">
              <div class="banner-tag" v-if="banner.tag">{{ banner.tag }}</div>
              <div class="banner-title">{{ banner.title }}</div>
              <div class="banner-sub">{{ banner.sub }}</div>
            </div>
          </div>
        </van-swipe-item>
      </van-swipe>
    </div>

    <!-- Categories -->
    <div class="category-grid">
      <div
        class="category-item"
        v-for="cat in displayCategories"
        :key="cat.id || cat.name"
        @click="goCategory(cat)"
      >
        <div class="cat-icon">
          <img v-if="cat.iconUrl" :src="cat.iconUrl" class="cat-icon-img" :alt="cat.categoryName || cat.name" />
          <span v-else class="cat-icon-text" :style="{ color: cat.color || '#0071DC' }">{{ (cat.categoryName || cat.name).charAt(0) }}</span>
        </div>
        <span class="cat-name">{{ cat.categoryName || cat.name }}</span>
      </div>
    </div>

    <!-- Popular products -->
    <div class="section-title">
      <span class="title-text">热门推荐</span>
      <span class="see-all" @click="$router.push('/category')">查看全部</span>
    </div>

    <div class="product-grid" v-if="products.length">
      <div
        class="product-card"
        v-for="product in products"
        :key="product.id"
        @click="$router.push(`/product/${product.id}`)"
      >
        <div class="product-img">
          <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.productName" />
          <svg v-else width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#D1D1D6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            <circle cx="8.5" cy="8.5" r="1.5"/>
            <polyline points="21 15 16 10 5 21"/>
          </svg>
        </div>
        <div class="product-info">
          <div class="product-name text-ellipsis-2">{{ product.productName }}</div>
          <div class="product-price">
            <span class="unit">¥</span>{{ formatPrice(product.price) }}
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
          <line x1="3" y1="6" x2="21" y2="6"/>
          <path d="M16 10a4 4 0 0 1-8 0"/>
        </svg>
      </div>
      <p>暂无商品</p>
    </div>

    <van-loading v-if="loading" class="page-loading" size="24px" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { searchProducts, getCategoryList, getHomeCategories } from '@/api'
import { getToken } from '@/utils/auth'

const router = useRouter()
const products = ref([])
const categories = ref([])
const homeCategories = ref([])
const loading = ref(true)

const banners = [
  { id: 1, title: '新品上市', sub: '精选好物 限时优惠', tag: 'NEW', bg: 'linear-gradient(135deg, #0071DC 0%, #0056B3 100%)' },
  { id: 2, title: '限时特惠', sub: '每日精选 好价不停', tag: 'HOT', bg: 'linear-gradient(135deg, #1D1D1F 0%, #3A3A3C 100%)' },
  { id: 3, title: '生鲜直达', sub: '产地直采 新鲜到家', tag: '', bg: 'linear-gradient(135deg, #34C759 0%, #28A745 100%)' },
]

const iconConfig = [
  { color: '#FF6B35' }, { color: '#34C759' }, { color: '#FF9500' }, { color: '#FF3B30' },
  { color: '#8B572A' }, { color: '#5856D6' }, { color: '#FF2D55' }, { color: '#0071DC' },
  { color: '#AF52DE' }, { color: '#FF69B4' }, { color: '#FFD700' }, { color: '#00BCD4' },
  { color: '#795548' }, { color: '#607D8B' }, { color: '#E91E63' }, { color: '#FF9800' },
  { color: '#9C27B0' }, { color: '#009688' }, { color: '#795548' }, { color: '#455A64' },
  { color: '#F44336' },
]

const defaultCategories = [
  { name: '沃集鲜', color: '#FF6B35' },
  { name: '水果生鲜', color: '#34C759' },
  { name: '牛奶烘焙', color: '#FF9500' },
  { name: '休闲零食', color: '#FF3B30' },
  { name: '粮油调味', color: '#8B572A' },
  { name: '酒水饮料', color: '#5856D6' },
  { name: '个护美妆', color: '#FF2D55' },
  { name: '纸品家清', color: '#0071DC' },
  { name: '母婴玩具', color: '#AF52DE' },
  { name: '潮玩萌宠', color: '#FF69B4' },
  { name: '粽情端午', color: '#FFD700' },
  { name: '全球好物', color: '#00BCD4' },
  { name: '家居百货', color: '#795548' },
  { name: '床品家纺', color: '#607D8B' },
  { name: '服饰鞋帽', color: '#E91E63' },
  { name: '家电', color: '#FF9800' },
]

const displayCategories = computed(() => {
  if (homeCategories.value.length > 0) {
    return homeCategories.value.map(c => ({
      id: c.linkCategoryId,
      categoryName: c.name,
      iconUrl: c.iconUrl,
      color: c.color
    }))
  }
  if (categories.value.length > 0) {
    return categories.value.slice(0, 8).map((c, i) => ({
      ...c,
      color: iconConfig[i % iconConfig.length].color
    }))
  }
  return defaultCategories.slice(0, 8)
})

function formatPrice(price) {
  return Number(price).toFixed(2)
}

function goCategory(cat) {
  if (cat.id) {
    router.push({ path: '/category', query: { categoryId: cat.id, name: cat.categoryName } })
  } else {
    router.push({ path: '/category', query: { name: cat.name } })
  }
}

onMounted(async () => {
  if (!getToken()) { loading.value = false; return }
  try {
    const [catRes, prodRes, homeCatRes] = await Promise.all([
      getCategoryList().catch(() => ({ data: [] })),
      searchProducts({ pageNum: 1, pageSize: 20 }).catch(() => ({ data: { records: [] } })),
      getHomeCategories().catch(() => ({ data: [] }))
    ])
    categories.value = catRes.data || []
    products.value = prodRes.data?.records || []
    homeCategories.value = homeCatRes.data || []
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
}

.banner-item {
  height: 150px;
  display: flex;
  align-items: flex-end;
  padding: 24px;
  color: #fff;
}

.banner-content {
  .banner-tag {
    display: inline-block;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 1px;
    background: rgba(255, 255, 255, 0.25);
    padding: 3px 10px;
    border-radius: 6px;
    margin-bottom: 8px;
  }

  .banner-title {
    font-size: 24px;
    font-weight: 800;
    line-height: 1.2;
  }

  .banner-sub {
    font-size: 13px;
    opacity: 0.85;
    margin-top: 4px;
  }
}

.see-all {
  font-size: 13px;
  color: var(--shop-accent);
  font-weight: 600;
}

.page-loading {
  text-align: center;
  padding: 40px;
}
</style>
