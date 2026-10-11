<template>
  <!-- Search — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 pl-margin pr-[96px] flex items-center justify-between gap-space-sm">
      <div class="flex items-center gap-space-sm">
        <button aria-label="Back" class="w-11 h-11 flex items-center justify-center text-on-surface hover:text-primary transition-colors -ml-space-xs" @click="$router.back()">
          <span class="material-symbols-outlined text-[24px]">arrow_back_ios_new</span>
        </button>
        <img alt="Walmart Logo" class="h-8 w-auto max-w-[45%] object-contain shrink-0" :src="'/api/uploads/商品图片/screen.png'" />
        
      </div>
      <div class="flex items-center gap-space-sm">
        <button aria-label="More actions" class="w-11 h-11 flex items-center justify-center text-on-surface-variant hover:text-primary transition-colors">
          <span class="material-symbols-outlined text-[22px]">more_vert</span>
        </button>
        <img alt="Profile" class="w-8 h-8 rounded-full object-cover" src="/avatar-default.svg" />
      </div>
    </div>
  </header>

  <main class="flex-1 flex flex-col relative w-full pt-14 pb-safe bg-background max-w-[540px] mx-auto min-h-screen">
    <div class="flex flex-col w-full pb-10">
      <!-- Search Header Strip -->
      <div class="sticky top-0 z-40 bg-surface-container-lowest shadow-sm px-margin py-space-sm flex items-center gap-space-sm">
        <div class="flex-1 flex items-center bg-surface-container-low rounded-full px-space-md h-10 gap-space-xs shadow-inner">
          <span class="material-symbols-outlined text-[20px] text-on-surface-variant shrink-0">search</span>
          <input ref="inputRef" v-model="keyword" class="bg-transparent border-none text-body-md font-body-md text-on-surface w-full focus:outline-none placeholder:text-outline" id="search-input" placeholder="搜索商品、品牌或品类" type="text" @keyup.enter="doSearch" />
          <button v-if="keyword" aria-label="清空输入" class="w-6 h-6 flex items-center justify-center rounded-full bg-surface-container-highest text-on-surface-variant hover:text-on-surface" id="clear-search-btn" @click="keyword = ''">
            <span class="material-symbols-outlined text-[16px]">close</span>
          </button>
        </div>
        <button class="text-primary-container font-headline-sm text-headline-sm px-space-xs py-1 active:opacity-75 transition-opacity" id="search-action-btn" @click="doSearch">
          搜索
        </button>
      </div>

      <!-- View Mode Switch: Search Hints & History State (Toggleable) -->
      <div class="flex flex-col gap-space-lg px-margin pt-space-md" id="search-history-container" v-if="!searched && (history.length || hotProducts.length || latestProducts.length)">
        <!-- Recent Searches -->
        <div class="flex flex-col gap-space-sm" v-if="!searched && history.length">
          <div class="flex items-center justify-between">
            <span class="font-headline-sm text-headline-sm text-on-surface flex items-center gap-space-xs">
              <span class="material-symbols-outlined text-[18px] text-outline">history</span>
              搜索历史
            </span>
            <button aria-label="清空历史" class="flex items-center gap-0.5 text-outline hover:text-error text-body-sm font-body-sm" id="clear-history-btn" @click="clearHistory">
              <span class="material-symbols-outlined text-[18px]">delete</span>
            </button>
          </div>
          <div class="flex flex-wrap gap-space-xs" id="history-tags-wrapper">
            <button v-for="h in history" :key="h" class="history-chip bg-surface-container text-on-surface-variant hover:bg-surface-container-high font-body-sm text-body-sm px-space-md py-1 rounded-full active:scale-95 transition-all" @click="searchByTag(h)">{{ h }}</button>
          </div>
        </div>

        <!-- Hot Searches & New Arrivals -->
        <div class="grid grid-cols-1 gap-space-md">
          <!-- Hot Search Flame Section -->
          <div class="bg-surface-container-lowest p-space-md rounded-xl shadow-sm flex flex-col gap-space-sm" v-if="!searched && hotProducts.length">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-space-xs">
                <span class="w-6 h-6 rounded-full bg-secondary-fixed flex items-center justify-center text-on-secondary-fixed">
                  <span class="material-symbols-outlined text-[16px] text-tertiary">local_fire_department</span>
                </span>
                <span class="font-headline-sm text-headline-sm text-on-surface">热门搜索</span>
              </div>
              <span class="font-badge-label text-badge-label text-tertiary-container bg-tertiary-fixed px-space-xs py-0.5 rounded">实时榜单</span>
            </div>
            <div class="flex flex-wrap gap-space-xs">
              <span v-for="(p, i) in hotProducts" :key="p.id" class="inline-flex items-center gap-1 bg-surface-container-low text-on-surface px-space-md py-1 rounded-full font-body-sm text-body-sm active:bg-surface-container-high cursor-pointer" @click="searchByTag(p.productName)">
                <span v-if="i < 3" class="font-badge-label text-badge-label font-bold" :class="i === 2 ? 'text-secondary' : 'text-tertiary'">{{ i + 1 }}</span> {{ p.productName }}
              </span>
            </div>
          </div>
          <!-- New Releases -->
          <div class="bg-surface-container-lowest p-space-md rounded-xl shadow-sm flex flex-col gap-space-sm" v-if="!searched && latestProducts.length">
            <div class="flex items-center gap-space-xs">
              <span class="w-6 h-6 rounded-full bg-primary-fixed flex items-center justify-center text-primary">
                <span class="material-symbols-outlined text-[16px]">new_releases</span>
              </span>
              <span class="font-headline-sm text-headline-sm text-on-surface">最新上架</span>
            </div>
            <div class="flex flex-wrap gap-space-xs">
              <span v-for="p in latestProducts" :key="p.id" class="bg-primary-fixed/40 text-on-primary-fixed-variant px-space-md py-1 rounded-full font-body-sm text-body-sm cursor-pointer hover:bg-primary-fixed/70" @click="searchByTag(p.productName)">
                {{ p.productName }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Search Results Module -->
      <div class="flex flex-col mt-space-sm" id="search-results-section" v-if="searched">
        <!-- Result Header & Sort Bar -->
        <div class="sticky top-14 z-30 bg-surface-container-lowest shadow-sm flex flex-col">
          <div class="px-margin pt-space-xs pb-1 flex items-center justify-between bg-surface-container-low/60">
            <span class="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px] text-primary">info</span>
              为您找到 <span class="font-bold text-primary font-headline-sm text-headline-sm">{{ results.length }}</span> 件<span v-if="keyword">“{{ keyword }}”</span>相关商品
            </span>
            <span class="font-badge-label text-badge-label text-on-secondary-container bg-secondary-fixed px-1.5 py-0.5 rounded">自营极速达</span>
          </div>
          <!-- Sorting Tabs Strip -->
          <div class="flex items-center justify-between px-margin py-2 text-body-md font-body-md">
            <button class="relative py-1 font-bold text-primary font-headline-sm text-headline-sm" @click="sortBy = 'default'; doSort()">
              综合
              <span class="absolute bottom-0 left-1/2 -translate-x-1/2 w-5 h-0.5 bg-primary rounded-full"></span>
            </button>
            <button class="text-on-surface-variant flex items-center gap-0.5 hover:text-on-surface" @click="sortBy = sortBy === 'price_asc' ? 'price_desc' : 'price_asc'; doSort()">
              价格
              <span class="material-symbols-outlined text-[16px] text-outline">swap_vert</span>
            </button>
            <button class="text-on-surface-variant hover:text-on-surface">
              销量
            </button>
            <button class="text-on-surface-variant hover:text-on-surface" @click="sortBy = 'newest'; doSort()">
              最新上架
            </button>
          </div>
        </div>

        <!-- Product SKU List -->
        <div class="flex flex-col gap-space-xs p-margin" v-if="results.length" @scroll="onListScroll">
          <!-- Item: 沿用设计稿重复商品横卡结构 -->
          <div
            v-for="(product, i) in results"
            :key="product.id"
            class="bg-surface-container-lowest rounded-xl p-space-md flex gap-space-md shadow-sm relative overflow-hidden active:bg-surface-container-low transition-colors"
            @click="$router.push('/product/' + product.id)"
          >
            <!-- Visual Thumbnail -->
            <div class="relative w-24 h-24 shrink-0 rounded-lg overflow-hidden bg-surface-container">
              <img class="w-full h-full object-cover" data-alt="Deluxe packaging box of premium Mengniu Deluxe pure milk bottles arranged neatly with rich creamy splashing milk in clean white commercial lighting, high retail standard product photography." :src="product.imageUrl || '/placeholder.svg'" />
              <div v-if="product.categoryName" class="absolute top-0 left-0 font-badge-label text-badge-label px-1.5 py-0.5 rounded-br-lg" :class="i % 3 === 2 ? 'bg-secondary-container text-on-secondary-container' : (i % 3 === 1 ? 'bg-primary text-on-primary' : 'bg-tertiary-container text-on-tertiary-container')">
                {{ product.categoryName }}
              </div>
            </div>
            <!-- Info Stack -->
            <div class="flex-1 flex flex-col justify-between min-w-0">
              <div class="flex flex-col gap-1">
                <div class="flex items-center gap-1">
                  <span class="font-badge-label text-badge-label px-1 py-0.2 rounded shrink-0" :class="i % 2 ? 'bg-secondary-fixed text-on-secondary-fixed-variant' : 'bg-tertiary-fixed text-on-tertiary-fixed'">
                    {{ i % 2 ? '满减可用' : '大牌低价' }}
                  </span>
                  <h2 class="font-headline-sm text-headline-sm text-on-surface line-clamp-1 truncate">
                    {{ product.productName }}
                  </h2>
                </div>
                <p v-if="product.description" class="font-body-sm text-body-sm text-on-surface-variant">{{ product.description }}</p>
              </div>
              <div class="flex items-end justify-between mt-space-xs">
                <div class="flex flex-col">
                  <span class="font-body-sm text-body-sm text-outline">销量 2.3万+</span>
                  <div class="flex items-baseline gap-1">
                    <span class="text-tertiary font-price-currency text-price-currency">¥</span>
                    <span class="text-tertiary font-price-lg text-price-lg">{{ formatPrice(product.price) }}</span>
                    <span v-if="product.originalPrice" class="text-outline line-through font-body-sm text-body-sm">¥{{ formatPrice(product.originalPrice) }}</span>
                  </div>
                </div>
                <!-- Cart Circular Action -->
                <button aria-label="加入购物车" class="w-8 h-8 rounded-full bg-primary-container text-on-primary flex items-center justify-center shadow-md active:scale-90 transition-transform">
                  <span class="material-symbols-outlined text-[20px]">add</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty Result -->
        <div class="flex flex-col items-center justify-center py-16 px-margin gap-space-sm" v-if="searched && !loading && !results.length">
          <span class="material-symbols-outlined text-[48px] text-outline">search_off</span>
          <p class="font-body-md text-body-md text-on-surface-variant">没有找到相关商品</p>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { searchProducts, getHotProducts, getLatestProducts } from '@/api'

const route = useRoute()

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
  // 支持通过 URL 参数直接搜索（购物车「看相似」等入口跳转）
  const q = route.query.keyword
  if (q && typeof q === 'string') {
    keyword.value = q
    doSearch()
  }
})

function searchByTag(name) {
  keyword.value = name
  doSearch()
}

function onListScroll(e) {
  const el = e.target
  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 80) loadMore()
}
</script>
