<template>
  <div class="category-page">
    <div class="search-bar">
      <span class="logo">分类</span>
      <div class="search-wrapper">
        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input class="search-input" placeholder="搜索商品" readonly @click="$router.push('/search')" />
      </div>
    </div>

    <!-- 横向导航：一级分类 -->
    <div class="horizontal-nav">
      <div class="h-nav-inner">
        <div
          class="h-nav-item"
          :class="{ active: selectedParentId === 0 }"
          @click="selectParent(0)"
        >全部</div>
        <div
          class="h-nav-item"
          :class="{ active: selectedParentId === cat.id }"
          v-for="cat in parentCategories"
          :key="cat.id"
          @click="selectParent(cat.id)"
        >{{ cat.categoryName }}</div>
      </div>
    </div>

    <div class="category-layout">
      <!-- 纵向导航：二级分类 -->
      <div class="side-bar">
        <div
          class="side-item"
          :class="{ active: selectedChildId === 0 }"
          @click="selectChild(0)"
        >全部</div>
        <div
          class="side-item"
          :class="{ active: selectedChildId === child.id }"
          v-for="child in currentChildren"
          :key="child.id"
          @click="selectChild(child.id)"
        >{{ child.categoryName }}</div>
      </div>

      <!-- 商品列表 -->
      <div class="product-list">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text=""
          @load="loadMore"
        >
          <div class="product-grid">
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
        </van-list>
        <div class="empty-state" v-if="!loading && !products.length">
          <div class="empty-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
              <line x1="3" y1="6" x2="21" y2="6"/>
              <path d="M16 10a4 4 0 0 1-8 0"/>
            </svg>
          </div>
          <p>该分类暂无商品</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchProducts, getCategoryList } from '@/api'

const route = useRoute()
const allCategories = ref([])
const selectedParentId = ref(0)
const selectedChildId = ref(0)
const products = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)

function formatPrice(price) { return Number(price).toFixed(2) }

// 一级分类（parentId=0）
const parentCategories = computed(() => {
  return allCategories.value.filter(c => c.parentId === 0)
})

// 当前选中一级分类的子分类
const currentChildren = computed(() => {
  if (selectedParentId.value === 0) return []
  const parent = allCategories.value.find(c => c.id === selectedParentId.value)
  return parent?.children || []
})

async function loadCategories() {
  try {
    const res = await getCategoryList()
    allCategories.value = res.data || []
    // 如果 URL 带 categoryId，自动选中
    if (route.query.categoryId) {
      const catId = Number(route.query.categoryId)
      const cat = allCategories.value.find(c => c.id === catId)
      if (cat) {
        if (cat.parentId === 0) {
          selectedParentId.value = catId
        } else {
          selectedParentId.value = cat.parentId
          selectedChildId.value = catId
        }
      }
    }
  } catch (e) { console.error(e) }
}

function selectParent(id) {
  selectedParentId.value = id
  selectedChildId.value = 0
  loadProducts(true)
}

function selectChild(id) {
  selectedChildId.value = id
  loadProducts(true)
}

let loadSeq = 0

async function loadProducts(reset = false) {
  if (reset) {
    finished.value = true
    products.value = []
    pageNum.value = 1
  }
  if (!reset && (loading.value || finished.value)) return
  const seq = ++loadSeq
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: 20 }
    // 优先用子分类 ID，其次用父分类 ID
    if (selectedChildId.value > 0) {
      params.categoryId = selectedChildId.value
    } else if (selectedParentId.value > 0) {
      params.categoryId = selectedParentId.value
    }
    const res = await searchProducts(params)
    if (seq !== loadSeq) return
    const list = res.data?.records || []
    if (reset) { products.value = list } else { products.value.push(...list) }
    finished.value = list.length < 20
    pageNum.value++
  } catch (e) { console.error(e) } finally {
    if (seq === loadSeq) loading.value = false
  }
}

function loadMore() { loadProducts(false) }

// 监听路由参数变化（从首页再次点击分类时触发）
watch(() => route.query.categoryId, (newId) => {
  if (newId) {
    const catId = Number(newId)
    const cat = allCategories.value.find(c => c.id === catId)
    if (cat) {
      if (cat.parentId === 0) {
        selectedParentId.value = catId
        selectedChildId.value = 0
      } else {
        selectedParentId.value = cat.parentId
        selectedChildId.value = catId
      }
      loadProducts(true)
    }
  }
})

onMounted(async () => {
  await loadCategories()
  loadProducts(true)
})
</script>

<style lang="scss" scoped>
.category-page {
  min-height: 100vh;
}

.horizontal-nav {
  background: #fff;
  border-bottom: 1px solid var(--shop-separator);
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar { display: none; }
}

.h-nav-inner {
  display: flex;
  padding: 0 12px;
  gap: 0;
}

.h-nav-item {
  padding: 10px 14px;
  font-size: 13px;
  color: var(--shop-text-secondary);
  white-space: nowrap;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
  cursor: pointer;
  font-weight: 500;

  &.active {
    color: var(--shop-accent);
    border-bottom-color: var(--shop-accent);
    font-weight: 700;
  }
}

.category-layout {
  display: flex;
  height: calc(100vh - 110px);
}

.side-bar {
  width: 88px;
  background: var(--shop-bg-secondary);
  overflow-y: auto;
  flex-shrink: 0;
  -webkit-overflow-scrolling: touch;
}

.side-item {
  padding: 14px 8px;
  text-align: center;
  font-size: 13px;
  color: var(--shop-text-secondary);
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
  font-weight: 500;

  &.active {
    color: var(--shop-accent);
    font-weight: 700;
    background: var(--shop-bg);

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 18px;
      background: var(--shop-accent);
      border-radius: 0 3px 3px 0;
    }
  }
}

.product-list {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  padding: var(--shop-gap);
  background: var(--shop-bg);
}
</style>
