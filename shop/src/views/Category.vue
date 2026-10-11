<template>
  <!-- Category — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 pl-margin pr-[96px] flex items-center justify-between gap-space-md">
      <div class="flex items-center gap-space-sm shrink-0">
        <img alt="Walmart Logo with Favicon Spark" class="h-8 w-auto max-w-[45%] object-contain shrink-0" :src="'/api/uploads/商品图片/screen.png'" />
        
      </div>
      <div class="flex items-center gap-space-sm shrink-0">
        <button aria-label="Search" class="w-11 h-11 flex items-center justify-center text-on-surface-variant hover:text-primary transition-colors" @click="$router.push('/search')">
          <span class="material-symbols-outlined text-[22px]">search</span>
        </button>
        <img alt="Profile" class="w-8 h-8 rounded-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAbWRWRjaR21qK1jU0kCwRb_bAPV-hTUNuaZhWlx7qH_Va4V32egzB9EA-dyJjfPe9hnkJfPAuD8FSIvw03zd17tPdYx3K8XtSc9b3_YTnlSDYSwkgXgjlVRRjOPE3rL-UQNuJ74U9yW3VtEtg7VpupRBb7r9Y5r5wmAz-R2XiUVAipjvedop15RZTvklVj-vtaXe9VOD3ODfIai_ITvZaWs2hlEdS_VxLLC-YWL49tz4gKH6_IB6yQ" @click="$router.push('/profile')" />
      </div>
    </div>
  </header>

  <main class="flex-1 flex flex-col relative w-full pt-14 pb-20 bg-background max-w-[540px] mx-auto min-h-screen">
    <div class="flex flex-col w-full">
      <!-- 1. Top Search Bar -->
      <div class="sticky top-0 z-40 bg-surface-container-lowest px-margin py-space-sm shadow-sm flex items-center gap-space-sm">
        <div class="flex-1 flex items-center bg-surface-container-low rounded-full px-space-md py-1.5 gap-space-xs text-on-surface-variant">
          <span class="material-symbols-outlined text-[18px]">search</span>
          <input class="bg-transparent border-none outline-none font-body-sm text-body-sm text-on-surface w-full placeholder:text-outline" placeholder="请输入搜索商品" readonly type="text" @click="$router.push('/search')" />
        </div>
      </div>
      <!-- 2. Primary Category Horizontal Bar -->
      <div class="relative bg-surface-container-lowest pt-2 pb-2.5">
        <div class="flex items-center">
          <div class="flex-1 overflow-x-auto no-scrollbar flex items-start gap-3.5 px-margin pr-16" id="topCategoryScroller">
            <!-- Item: 全部 -->
            <button class="flex flex-col items-center shrink-0 group focus:outline-none" @click="selectParent(0)">
              <div class="w-12 h-12 rounded-full overflow-hidden flex items-center justify-center relative p-0.5" :class="selectedParentId === 0 ? 'bg-primary-fixed shadow-sm' : 'bg-surface-container'">
                <img class="w-full h-full object-cover rounded-full" data-alt="All categories grid icon" :src="'/placeholder.svg'" :alt="'全部'" />
              </div>
              <span :class="selectedParentId === 0 ? 'mt-1.5 px-2 py-0.5 rounded-full font-badge-label text-badge-label bg-primary-container text-on-primary font-bold' : 'mt-1.5 font-body-sm text-body-sm text-on-surface-variant font-medium'">全部</span>
            </button>
            <!-- Item: 一级分类 -->
            <button
              v-for="cat in parentCategories"
              :key="cat.id"
              class="flex flex-col items-center shrink-0 group focus:outline-none"
              @click="selectParent(cat.id)"
            >
              <div class="w-12 h-12 rounded-full overflow-hidden flex items-center justify-center relative p-0.5" :class="selectedParentId === cat.id ? 'bg-primary-fixed shadow-sm' : 'bg-surface-container'">
                <img class="w-full h-full object-cover rounded-full" data-alt="Category icon" :src="cat.iconUrl || '/placeholder.svg'" :alt="cat.categoryName" />
              </div>
              <span :class="selectedParentId === cat.id ? 'mt-1.5 px-2 py-0.5 rounded-full font-badge-label text-badge-label bg-primary-container text-on-primary font-bold' : 'mt-1.5 font-body-sm text-body-sm text-on-surface-variant font-medium'">{{ cat.categoryName }}</span>
            </button>
          </div>
          <!-- Right "全部 ☰" Expand Button -->
          <button class="absolute right-0 top-0 bottom-0 bg-gradient-to-l from-surface-container-lowest via-surface-container-lowest to-transparent pl-4 pr-margin flex flex-col items-center justify-center text-on-surface z-10" @click="showAllCategories = true">
            <span class="material-symbols-outlined text-[20px] text-on-surface-variant">menu</span>
            <span class="font-badge-label text-badge-label text-on-surface-variant font-bold mt-0.5">全部</span>
          </button>
        </div>
      </div>
      <!-- 3. Dual Column Layout (Left Rail 88px + Right Content Area) -->
      <div class="flex w-full items-start">
        <!-- Left Navigation Rail (88px) -->
        <aside class="w-[88px] shrink-0 bg-surface-container-low flex flex-col min-h-[620px] select-none py-1">
          <!-- 全部 -->
          <button
            class="nav-sub-item w-full py-3.5 px-1 text-center relative flex items-center justify-center"
            :class="selectedChildId === 0 ? 'font-headline-sm text-headline-sm text-primary font-bold bg-surface-container-lowest' : 'font-body-sm text-body-sm text-on-surface-variant hover:bg-surface-container transition-colors'"
            @click="selectChild(0)"
          >
            <span v-if="selectedChildId === 0" class="absolute left-0 top-3 bottom-3 w-1 bg-primary rounded-r-full">
            </span>
            <span :class="selectedChildId === 0 ? 'font-body-md text-body-md' : ''">全部</span>
          </button>
          <!-- 二级分类 -->
          <button
            v-for="(child, ci) in currentChildren"
            :key="child.id"
            class="nav-sub-item w-full py-3.5 px-1 text-center relative flex items-center justify-center"
            :class="selectedChildId === child.id ? 'font-headline-sm text-headline-sm text-primary font-bold bg-surface-container-lowest' : 'font-body-sm text-body-sm text-on-surface-variant hover:bg-surface-container transition-colors'"
            @click="selectChild(child.id)"
          >
            <span v-if="selectedChildId === child.id" class="absolute left-0 top-3 bottom-3 w-1 bg-primary rounded-r-full">
            </span>
            <div class="flex items-center justify-center gap-0.5">
              <span v-if="selectedChildId === child.id" class="bg-tertiary-container text-on-tertiary font-badge-label text-badge-label px-1 py-0.2 rounded-sm scale-90">荐</span>
              <span v-if="ci === currentChildren.length - 1 && currentChildren.length > 1" class="bg-tertiary text-on-tertiary font-badge-label text-badge-label px-1 py-0.2 rounded-sm scale-90">热</span>
              <span :class="selectedChildId === child.id ? 'font-body-md text-body-md' : 'font-body-sm text-body-sm'">{{ child.categoryName }}</span>
            </div>
          </button>
        </aside>
        <!-- Right SKU Content Area -->
        <section class="flex-1 min-w-0 bg-surface-container-lowest px-space-sm pt-2 pb-6 flex flex-col gap-2.5" @scroll="onListScroll">
          <!-- Subcategory Pills（来自当前一级分类的真实子分类） -->
          <div v-if="currentChildren.length" class="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-0.5">
            <button
              v-for="child in currentChildren"
              :key="'pill-' + child.id"
              class="shrink-0 px-2.5 py-1 rounded-full font-body-sm text-body-sm transition-colors"
              :class="selectedChildId === child.id ? 'bg-primary-fixed text-primary font-bold shadow-xs' : 'bg-surface-container-low text-on-surface-variant hover:bg-surface-container'"
              @click="selectChild(child.id)"
            >
              {{ child.categoryName }}
            </button>
          </div>
          <!-- Sort & Filter Bar（排序/筛选接入真实接口参数） -->
          <div class="flex items-center justify-between text-body-sm font-body-sm px-1 py-1">
            <button
              class="transition-colors"
              :class="sortBy === 'default' ? 'font-headline-sm text-headline-sm text-primary font-bold' : 'text-on-surface-variant hover:text-on-surface'"
              @click="selectSort('default')"
            >
              综合
            </button>
            <button
              class="transition-colors"
              :class="sortBy === 'sales' ? 'font-headline-sm text-headline-sm text-primary font-bold' : 'text-on-surface-variant hover:text-on-surface'"
              @click="selectSort('sales')"
            >
              销量
            </button>
            <button
              class="flex items-center gap-0.5 transition-colors"
              :class="sortBy === 'price' ? 'font-headline-sm text-headline-sm text-primary font-bold' : 'text-on-surface-variant hover:text-on-surface'"
              @click="selectSort('price')"
            >
              <span>价格</span>
              <span class="material-symbols-outlined text-[13px] leading-none">{{ sortBy === 'price' ? (priceOrder === 'asc' ? 'arrow_upward' : 'arrow_downward') : 'swap_vert' }}</span>
            </button>
            <button
              class="flex items-center gap-0.5 transition-colors"
              :class="hasActiveFilter ? 'text-primary font-bold' : 'text-on-surface-variant hover:text-on-surface'"
              @click="openFilter"
            >
              <span>筛选</span>
              <span class="material-symbols-outlined text-[13px] leading-none">filter_alt</span>
            </button>
          </div>
          <!-- Category Operational Banner: 品秋味 食秋果 -->
          <div class="relative w-full rounded-xl overflow-hidden bg-secondary-fixed p-3 flex items-center justify-between shadow-sm">
            <div class="relative z-10 flex flex-col max-w-[58%]">
              <div class="flex items-center gap-1">
                <span class="font-headline-sm text-headline-sm text-on-secondary-fixed font-black tracking-tight">品秋味 食秋果</span>
              </div>
              <p class="font-body-sm text-body-sm text-on-secondary-fixed-variant mt-0.5 font-medium truncate">莱阳秋月梨 15.99元/约700g</p>
              <div class="mt-2 inline-flex items-center">
                <span class="bg-secondary text-on-secondary font-badge-label text-badge-label px-2.5 py-1 rounded-full flex items-center gap-0.5 font-bold shadow-xs">
                  立即抢购
                  <span class="material-symbols-outlined text-[11px] leading-none">chevron_right</span>
                </span>
              </div>
            </div>
            <div class="w-24 h-20 shrink-0 relative rounded-lg overflow-hidden">
              <img class="w-full h-full object-cover" data-alt="Fresh sliced golden juicy Qiuyue Asian pears in wooden bowl with autumn sunlight, studio macro food photograph with clean background." src="https://lh3.googleusercontent.com/aida-public/AB6AXuBboI6j4Z8AiDb_zVOhNccVE8gEhw5uh7gzvUcJP8Gg7KKf2qVJI-rnImAZ4USxdqpfTPUsqqdNJ0jxGGfx6nJAbdNoaGYWWzcCdlPlKUVio3J-4bit0OM9A-uxKp99CxgbJczqWLkMVt7fITFKmjdc-SfyHd7NUJUVKyx3pCjyddl9WwWuK_edDX4MZn7JZ-3-eR5OOVWtyEEwuwcH7oCrFfeXC3Hxu3lZd089ClBMkoZ4e6DwQRCJ" />
            </div>
          </div>
          <!-- SKU List (商品数据来自商品列表接口) -->
          <div
            v-for="product in products"
            :key="product.id"
            class="flex gap-2.5 py-2.5 items-center"
            @click="$router.push('/product/' + product.id)"
          >
            <div class="w-[96px] h-[96px] rounded-lg overflow-hidden shrink-0 bg-surface-container relative">
              <img class="w-full h-full object-cover" data-alt="Fresh grocery product packshot" :src="product.imageUrl || '/placeholder.svg'" :alt="product.productName" />
            </div>
            <div class="flex-1 min-w-0 flex flex-col justify-between self-stretch py-0.5">
              <div>
                <h2 class="font-headline-sm text-headline-sm text-on-surface font-bold line-clamp-2 leading-tight">
                  {{ product.productName }}
                </h2>
                <p v-if="product.description" class="font-body-sm text-body-sm text-on-surface-variant truncate mt-0.5">{{ product.description }}</p>
              </div>
              <div class="mt-1">
                <!-- Golden Authority Tag -->
                <div class="inline-flex items-center gap-1 bg-secondary-fixed/50 px-1.5 py-0.5 rounded-sm">
                  <span class="bg-secondary-container text-on-secondary-container font-badge-label text-badge-label px-1 py-0.2 rounded-xs font-bold">榜</span>
                  <span class="font-badge-label text-badge-label text-secondary font-bold">应季水果热卖榜 TOP 4</span>
                </div>
                <!-- Price & Circular Add Button -->
                <div class="flex items-center justify-between mt-4">
                  <div class="flex items-baseline text-tertiary-container font-price-lg text-price-lg font-extrabold">
                    <span class="font-price-currency text-price-currency">¥</span>
                    <span class="">{{ formatPrice(product.price) }}</span>
                    <span class="font-body-sm text-body-sm text-outline font-normal ml-0.5">/盒</span>
                  </div>
                  <button aria-label="加购" class="w-7 h-7 rounded-full bg-primary-container text-on-primary flex items-center justify-center shadow-sm active:scale-95 transition-transform" @click.stop>
                    <span class="material-symbols-outlined text-[18px]">add</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <!-- Loading State -->
          <div v-if="loading && !products.length" class="flex items-center justify-center gap-1.5 py-4 text-on-surface-variant">
            <span class="material-symbols-outlined text-[18px]">progress_activity</span>
            <span class="font-body-sm text-body-sm">加载中…</span>
          </div>
          <!-- Empty State -->
          <div v-if="!loading && !products.length" class="flex flex-col items-center justify-center py-16 text-center">
            <span class="material-symbols-outlined text-[44px] text-outline-variant">inventory_2</span>
            <p class="font-body-sm text-body-sm text-on-surface-variant mt-2">该分类暂无商品</p>
          </div>
        </section>
      </div>
      <!-- Full Categories Expandable Overlay Panel (Based on Image 5 reference) -->
      <div v-if="showAllCategories" class="fixed inset-0 z-50 bg-on-background/40 backdrop-blur-xs flex flex-col justify-start" id="fullCategoryModal" @click.self="showAllCategories = false">
        <div class="bg-surface-container-lowest w-full max-w-[540px] mx-auto rounded-b-2xl shadow-xl flex flex-col max-h-[85vh] overflow-hidden pt-safe animate-in fade-in duration-200">
          <!-- Search within modal header -->
          <div class="px-margin py-3 flex items-center justify-between">
            <div class="font-headline-lg text-headline-lg text-on-surface font-bold">全部分类</div>
            <button class="flex items-center gap-1 font-body-sm text-body-sm text-on-surface-variant hover:text-primary transition-colors py-1 px-2" @click="showAllCategories = false">
              <span class="">收起</span>
              <span class="material-symbols-outlined text-[18px]">keyboard_arrow_up</span>
            </button>
          </div>
          <!-- Categories 5-column grid -->
          <div class="overflow-y-auto px-margin py-2 grid grid-cols-5 gap-y-4 gap-x-2 text-center pb-6">
            <!-- 全部 -->
            <button class="flex flex-col items-center" @click="pickParentFromModal(0)">
              <div class="w-12 h-12 rounded-full overflow-hidden flex items-center justify-center p-0.5" :class="selectedParentId === 0 ? 'bg-primary-fixed shadow-sm' : 'bg-surface-container'">
                <img class="w-full h-full object-cover rounded-full" alt="全部" src="/placeholder.svg" />
              </div>
              <span class="mt-1 px-1.5 py-0.5 rounded-full font-badge-label text-badge-label truncate w-full" :class="selectedParentId === 0 ? 'bg-primary-container text-on-primary font-bold' : 'text-on-surface-variant'">全部</span>
            </button>
            <!-- 真实一级分类（来自分类接口） -->
            <button
              v-for="cat in parentCategories"
              :key="'modal-' + cat.id"
              class="flex flex-col items-center"
              @click="pickParentFromModal(cat.id)"
            >
              <div class="w-12 h-12 rounded-full overflow-hidden flex items-center justify-center p-0.5" :class="selectedParentId === cat.id ? 'bg-primary-fixed shadow-sm' : 'bg-surface-container'">
                <img class="w-full h-full object-cover rounded-full" :alt="cat.categoryName" :src="cat.iconUrl || '/placeholder.svg'" />
              </div>
              <span class="mt-1 px-1.5 py-0.5 rounded-full font-badge-label text-badge-label truncate w-full" :class="selectedParentId === cat.id ? 'bg-primary-container text-on-primary font-bold' : 'text-on-surface-variant'">{{ cat.categoryName }}</span>
            </button>
          </div>
        </div>
        <!-- Dismiss Backdrop Area -->
        <div class="flex-1 w-full" @click="showAllCategories = false">
        </div>
      </div>
      <!-- 筛选浮层（van-popup 统一弹层：teleport + 高 z-index，不与底部导航重合） -->
      <van-popup v-model:show="showFilter" position="bottom" round teleport="body" :z-index="3000" :overlay-style="{ zIndex: 2999 }">
        <div class="flex flex-col">
          <div class="px-margin py-3 flex items-center justify-between">
            <div class="font-headline-lg text-headline-lg text-on-surface font-bold">筛选</div>
            <button class="flex items-center gap-1 font-body-sm text-body-sm text-on-surface-variant hover:text-primary transition-colors py-1 px-2" @click="showFilter = false">
              <span>收起</span>
              <span class="material-symbols-outlined text-[18px]">keyboard_arrow_down</span>
            </button>
          </div>
          <div class="px-margin pb-4 flex flex-col gap-space-md">
            <!-- 价格区间 -->
            <div class="flex flex-col gap-2">
              <span class="font-body-sm text-body-sm text-on-surface font-bold">价格区间</span>
              <div class="flex items-center gap-space-sm">
                <div class="flex-1 flex items-center bg-surface-container-low rounded-lg px-space-sm py-2">
                  <span class="font-body-sm text-body-sm text-outline shrink-0 mr-1">¥</span>
                  <input v-model="filterDraft.minPrice" class="bg-transparent border-none outline-none font-body-sm text-body-sm text-on-surface w-full min-w-0 placeholder:text-outline" placeholder="最低价" type="number" min="0" />
                </div>
                <span class="text-outline shrink-0">—</span>
                <div class="flex-1 flex items-center bg-surface-container-low rounded-lg px-space-sm py-2">
                  <span class="font-body-sm text-body-sm text-outline shrink-0 mr-1">¥</span>
                  <input v-model="filterDraft.maxPrice" class="bg-transparent border-none outline-none font-body-sm text-body-sm text-on-surface w-full min-w-0 placeholder:text-outline" placeholder="最高价" type="number" min="0" />
                </div>
              </div>
            </div>
            <!-- 快捷筛选 -->
            <div class="flex flex-col gap-2">
              <span class="font-body-sm text-body-sm text-on-surface font-bold">快捷筛选</span>
              <div class="flex items-center gap-space-sm">
                <button
                  class="px-3 py-1.5 rounded-full font-body-sm text-body-sm transition-colors"
                  :class="filterDraft.discountOnly ? 'bg-primary-fixed text-primary font-bold' : 'bg-surface-container-low text-on-surface-variant'"
                  @click="filterDraft.discountOnly = !filterDraft.discountOnly"
                >只看折扣商品</button>
                <button
                  class="px-3 py-1.5 rounded-full font-body-sm text-body-sm transition-colors"
                  :class="filterDraft.internationalOnly ? 'bg-primary-fixed text-primary font-bold' : 'bg-surface-container-low text-on-surface-variant'"
                  @click="filterDraft.internationalOnly = !filterDraft.internationalOnly"
                >国际品牌</button>
              </div>
            </div>
            <!-- 操作按钮 -->
            <div class="flex items-center gap-space-sm pt-1 pb-6">
              <button class="flex-1 h-11 rounded-full bg-surface-container-low text-on-surface font-headline-sm text-headline-sm active:scale-95 transition-all" @click="resetFilterDraft">重置</button>
              <button class="flex-1 h-11 rounded-full bg-primary-container text-on-primary font-headline-sm text-headline-sm font-bold active:scale-95 transition-all" @click="applyFilter">确定</button>
            </div>
          </div>
        </div>
      </van-popup>
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { showToast } from 'vant'
import { searchProducts, getCategoryList } from '@/api'

const route = useRoute()
const allCategories = ref([])
const selectedParentId = ref(0)
const selectedChildId = ref(0)
const products = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)

// 排序：default-综合 / sales-销量 / price-价格（asc/desc）
const sortBy = ref('default')
const priceOrder = ref('asc')

// 筛选弹层（草稿态，点「确定」才生效）
const showFilter = ref(false)
const filters = ref({ minPrice: '', maxPrice: '', discountOnly: false, internationalOnly: false })
const filterDraft = ref({ minPrice: '', maxPrice: '', discountOnly: false, internationalOnly: false })
const hasActiveFilter = computed(() => {
  const f = filters.value
  return f.minPrice !== '' || f.maxPrice !== '' || f.discountOnly || f.internationalOnly
})

// 「全部」展开弹层
const showAllCategories = ref(false)

// 弹层里选一级分类后收起
function pickParentFromModal(id) {
  selectParent(id)
  showAllCategories.value = false
}

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
    // 如果 URL 带 categoryId，自动选中（支持二级分类）
    if (route.query.categoryId) {
      applyRouteCategory(Number(route.query.categoryId))
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

// 排序切换：综合 / 销量 / 价格（点击价格在升序降序间切换）
function selectSort(type) {
  if (type === 'price') {
    if (sortBy.value === 'price') {
      priceOrder.value = priceOrder.value === 'asc' ? 'desc' : 'asc'
    } else {
      sortBy.value = 'price'
      priceOrder.value = 'asc'
    }
  } else {
    if (sortBy.value === type) return
    sortBy.value = type
  }
  loadProducts(true)
}

// 筛选弹层操作
function openFilter() {
  filterDraft.value = { ...filters.value }
  showFilter.value = true
}

function resetFilterDraft() {
  filterDraft.value = { minPrice: '', maxPrice: '', discountOnly: false, internationalOnly: false }
}

function applyFilter() {
  const d = filterDraft.value
  if (d.minPrice !== '' && d.maxPrice !== '' && Number(d.minPrice) > Number(d.maxPrice)) {
    showToast('最低价不能大于最高价')
    return
  }
  filters.value = { ...d }
  showFilter.value = false
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
    // 排序参数
    if (sortBy.value === 'sales') {
      params.sort = 'sales'
    } else if (sortBy.value === 'price') {
      params.sort = priceOrder.value === 'asc' ? 'price_asc' : 'price_desc'
    }
    // 筛选参数
    const f = filters.value
    if (f.minPrice !== '' && f.minPrice != null) params.minPrice = f.minPrice
    if (f.maxPrice !== '' && f.maxPrice != null) params.maxPrice = f.maxPrice
    if (f.discountOnly) params.discountOnly = true
    if (f.internationalOnly) params.internationalOnly = true
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

// 右侧商品流触底加载（替代原 van-list @load）
function onListScroll(e) {
  const el = e.target
  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 80) loadMore()
}

// 在分类树中递归查找（支持二级分类 id —— 修复点击子分类时页面停留在上次分类的问题）
function findCategoryById(catId) {
  for (const parent of allCategories.value) {
    if (parent.id === catId) return { id: parent.id, parentId: 0 }
    if (parent.children) {
      const child = parent.children.find(c => c.id === catId)
      if (child) return { id: child.id, parentId: parent.id }
    }
  }
  return null
}

// 应用路由中的分类参数（一级或二级分类均可）
function applyRouteCategory(catId) {
  const cat = findCategoryById(catId)
  if (!cat) return
  if (cat.parentId === 0) {
    selectedParentId.value = cat.id
    selectedChildId.value = 0
  } else {
    selectedParentId.value = cat.parentId
    selectedChildId.value = cat.id
  }
  loadProducts(true)
}

// 监听路由参数变化（从首页再次点击分类时触发）
watch(() => route.query.categoryId, (newId) => {
  if (newId) applyRouteCategory(Number(newId))
})

onMounted(async () => {
  await loadCategories()
  loadProducts(true)
})
</script>
