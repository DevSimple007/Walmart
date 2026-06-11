<template>
  <div class="search-page">
    <!-- Search header -->
    <div class="search-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
      </button>
      <div class="search-input-wrap">
        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input
          ref="inputRef"
          v-model="keyword"
          class="search-field"
          placeholder="搜索商品"
          @keyup.enter="doSearch"
        />
        <button v-if="keyword" class="clear-btn" @click="keyword = ''">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
          </svg>
        </button>
      </div>
      <button class="search-btn" @click="doSearch">搜索</button>
    </div>

    <!-- Filter bar -->
    <div class="filter-bar" v-if="searched && results.length">
      <button class="filter-item" :class="{ active: sortBy === 'default' }" @click="sortBy = 'default'; doSort()">综合</button>
      <button class="filter-item" :class="{ active: sortBy === 'price_asc' }" @click="sortBy = 'price_asc'; doSort()">价格↑</button>
      <button class="filter-item" :class="{ active: sortBy === 'price_desc' }" @click="sortBy = 'price_desc'; doSort()">价格↓</button>
      <button class="filter-item" :class="{ active: sortBy === 'newest' }" @click="sortBy = 'newest'; doSort()">最新</button>
    </div>

    <!-- Results count -->
    <div class="results-info" v-if="searched && results.length">
      <span>{{ results.length }} 个结果</span>
      <span v-if="keyword">  "{{ keyword }}"</span>
    </div>

    <!-- Search history -->
    <div class="search-history" v-if="!searched && history.length">
      <div class="history-header">
        <span class="history-title">搜索历史</span>
        <button class="history-clear" @click="clearHistory">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="3 6 5 6 21 6"/>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
          </svg>
        </button>
      </div>
      <div class="history-tags">
        <span class="history-tag" v-for="h in history" :key="h" @click="keyword = h; doSearch()">{{ h }}</span>
      </div>
    </div>

    <!-- Hot products -->
    <div class="search-history" v-if="!searched && hotProducts.length">
      <div class="history-header">
        <span class="history-title">热门搜索</span>
      </div>
      <div class="history-tags">
        <span class="history-tag" v-for="p in hotProducts" :key="p.id" @click="searchByTag(p.productName)">{{ p.productName }}</span>
      </div>
    </div>

    <!-- Latest products -->
    <div class="search-history" v-if="!searched && latestProducts.length">
      <div class="history-header">
        <span class="history-title">最新上架</span>
      </div>
      <div class="history-tags">
        <span class="history-tag" v-for="p in latestProducts" :key="p.id" @click="searchByTag(p.productName)">{{ p.productName }}</span>
      </div>
    </div>

    <!-- Search results — horizontal list -->
    <div class="search-results" v-if="searched && results.length">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text=""
        @load="loadMore"
      >
        <div
          class="product-list-item"
          v-for="product in results"
          :key="product.id"
          @click="$router.push(`/product/${product.id}`)"
        >
          <div class="item-img">
            <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.productName" />
            <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#D1D1D6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <polyline points="21 15 16 10 5 21"/>
            </svg>
          </div>
          <div class="item-info">
            <div class="item-name text-ellipsis-2">{{ product.productName }}</div>
            <div class="item-price">
              <span class="unit">¥</span>{{ formatPrice(product.price) }}
            </div>
            <div class="item-meta" v-if="product.categoryName">{{ product.categoryName }}</div>
          </div>
        </div>
      </van-list>
    </div>

    <div class="empty-state" v-else-if="searched && !loading">
      <div class="empty-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
      </div>
      <p>没有找到"{{ keyword }}"相关商品</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { searchProducts, getHotProducts, getLatestProducts } from '@/api'

const keyword = ref('')
const results = ref([])
const searched = ref(false)
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const sortBy = ref('default')
const inputRef = ref(null)
const history = ref(JSON.parse(localStorage.getItem('search_history') || '[]'))
const hotProducts = ref([])
const latestProducts = ref([])

function formatPrice(price) { return Number(price).toFixed(2) }

function saveHistory(k) {
  if (!k.trim()) return
  history.value = [k, ...history.value.filter(h => h !== k)].slice(0, 10)
  localStorage.setItem('search_history', JSON.stringify(history.value))
}

function clearHistory() {
  history.value = []
  localStorage.removeItem('search_history')
}

function doSort() {
  if (sortBy.value === 'price_asc') {
    results.value.sort((a, b) => a.price - b.price)
  } else if (sortBy.value === 'price_desc') {
    results.value.sort((a, b) => b.price - a.price)
  } else if (sortBy.value === 'newest') {
    results.value.sort((a, b) => (b.id || 0) - (a.id || 0))
  }
  // 'default' — re-fetch to restore API order
}

async function doSearch() {
  if (!keyword.value.trim()) return
  searched.value = true
  saveHistory(keyword.value.trim())
  pageNum.value = 1
  finished.value = false
  results.value = []
  await loadMore()
}

async function loadMore() {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const res = await searchProducts({ keyword: keyword.value, pageNum: pageNum.value, pageSize: 20 })
    const list = res.data?.records || []
    results.value.push(...list)
    if (list.length < 20) finished.value = true
    pageNum.value++
    if (sortBy.value !== 'default') doSort()
  } catch (e) { results.value = [] } finally { loading.value = false }
}

onMounted(async () => {
  inputRef.value?.focus()
  try {
    const [hotRes, latestRes] = await Promise.all([
      getHotProducts().catch(() => ({ data: [] })),
      getLatestProducts().catch(() => ({ data: [] }))
    ])
    hotProducts.value = hotRes.data || []
    latestProducts.value = latestRes.data || []
  } catch (_) {}
})

function searchByTag(name) {
  keyword.value = name
  doSearch()
}
</script>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  background: var(--shop-bg-secondary);
}

.search-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px var(--shop-page-pad);
  background: var(--shop-bg);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  background: none;
  border: none;
  padding: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: var(--shop-text-primary);
}

.search-input-wrap {
  flex: 1;
  position: relative;

  .search-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    pointer-events: none;
  }

  .search-field {
    width: 100%;
    height: 38px;
    border-radius: 20px;
    border: none;
    padding: 0 36px 0 38px;
    background: var(--shop-bg-secondary);
    font-size: 14px;
    outline: none;
    color: var(--shop-text-primary);
    font-family: var(--shop-font);

    &::placeholder {
      color: var(--shop-text-tertiary);
    }
  }

  .clear-btn {
    position: absolute;
    right: 8px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    padding: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
  }
}

.search-btn {
  background: none;
  border: none;
  color: var(--shop-accent);
  font-size: 15px;
  font-weight: 600;
  font-family: var(--shop-font);
  padding: 6px 4px;
  cursor: pointer;
  white-space: nowrap;

  &:active {
    opacity: 0.6;
  }
}

.results-info {
  padding: 10px var(--shop-page-pad) 6px;
  font-size: 12px;
  color: var(--shop-text-tertiary);
}

.search-history {
  margin: var(--shop-gap) var(--shop-page-pad) 0;
  padding: 16px;
  background: var(--shop-card-bg);
  border-radius: var(--shop-card-radius);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.history-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--shop-text-primary);
}

.history-clear {
  background: none;
  border: none;
  padding: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  border-radius: 8px;

  &:active {
    background: var(--shop-bg-secondary);
  }
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-tag {
  display: inline-block;
  padding: 7px 14px;
  background: var(--shop-bg-secondary);
  border-radius: 20px;
  font-size: 13px;
  color: var(--shop-text-primary);
  cursor: pointer;

  &:active {
    background: var(--shop-bg-tertiary);
  }
}

.search-results {
  padding-top: 2px;
}
</style>
