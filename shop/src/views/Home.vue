<template>
  <!-- Home — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
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
      <!-- 顶部定位及即时达配送提示区 (基于实景设计) -->
      <div class="px-margin pt-space-xs pb-space-sm flex items-center justify-between">
        <div class="flex items-center gap-space-xs overflow-hidden" @click="$router.push('/address')">
          <span class="material-symbols-outlined text-[18px] text-primary" style="font-variation-settings: 'FILL' 1;">location_on</span>
          <span class="font-headline-sm text-headline-sm text-on-surface truncate font-bold">{{ homeAddress || '请选择收货地址' }}</span>
          <span class="material-symbols-outlined text-[16px] text-on-surface-variant">keyboard_arrow_down</span>
          <span class="ml-space-xs bg-primary-fixed text-primary px-space-xs py-0.5 rounded-full font-badge-label text-badge-label flex items-center gap-0.5 shrink-0">
            <span class="material-symbols-outlined text-[11px]" style="font-variation-settings: 'FILL' 1;">bolt</span>
            最快30分钟达
          </span>
        </div>
        <div class="flex items-center gap-space-xs shrink-0">
          <button class="w-8 h-8 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors" @click="showToast('功能开发中')">
            <span class="material-symbols-outlined text-[20px]">qr_code_scanner</span>
          </button>
          <button class="w-8 h-8 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors relative" @click="showToast('功能开发中')">
            <span class="material-symbols-outlined text-[20px]">notifications</span>
            <span class="w-2 h-2 rounded-full bg-tertiary absolute top-1.5 right-1.5 ring-2 ring-surface"></span>
          </button>
        </div>
      </div>

      <!-- 搜索栏模块 -->
      <div class="px-margin mb-space-sm">
        <div class="flex items-center bg-surface-container-low rounded-full px-space-sm py-1 shadow-sm">
          <span class="material-symbols-outlined text-outline text-[20px] ml-space-xs">search</span>
          <input
            class="bg-transparent border-none text-on-surface placeholder:text-outline text-body-md font-body-md px-space-xs flex-1 outline-none min-w-0"
            placeholder="川香双椒麻辣味薯丁"
            readonly
            type="text"
            @click="$router.push('/search')"
          />
          <button class="bg-primary-container text-on-primary font-headline-sm text-[13px] px-space-md py-1 rounded-full shadow hover:bg-primary transition-colors shrink-0" @click="$router.push('/search')">
            搜索
          </button>
        </div>
      </div>

      <!-- 金刚区 (两排分类导航 10宫格，数据来自首页分类接口) -->
      <div class="px-margin py-space-xs grid grid-cols-5 gap-y-3 gap-x-1 text-center mb-space-sm">
        <a
          v-for="(cat, i) in homeGridCategories"
          :key="cat.id || cat.categoryName || cat.name || i"
          class="flex flex-col items-center group relative"
          href="javascript:;"
          @click="goCategory(cat)"
        >
          <div :class="['relative w-12 h-12 rounded-full flex items-center justify-center shadow-sm overflow-hidden mb-1', CAT_GRADIENTS[i % CAT_GRADIENTS.length]]">
            <img class="w-full h-full object-cover" :alt="cat.categoryName || cat.name" :src="cat.iconUrl || CAT_FALLBACK_IMAGES[i % CAT_FALLBACK_IMAGES.length]" />
          </div>
          <span
            v-if="cat.promoTag || PROMO_TAGS[i]"
            class="absolute -top-1.5 left-1/2 -translate-x-1/2 bg-tertiary text-on-tertiary font-badge-label text-badge-label px-1 rounded-full scale-90 whitespace-nowrap shadow-sm"
          >{{ cat.promoTag || PROMO_TAGS[i] }}</span>
          <span class="font-body-sm text-body-sm text-on-surface font-medium truncate w-full">{{ cat.categoryName || cat.name }}</span>
        </a>
      </div>

      <!-- 次级专区标签导航条 (水平滑动，跳转对应真实分类) -->
      <div class="px-margin mb-space-sm overflow-x-auto flex items-center gap-space-md scrollbar-none py-1">
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer" @click="goTag(15)">
          <span class="material-symbols-outlined text-[16px] text-primary">storefront</span>
          <span class="font-body-sm text-body-sm text-on-surface font-bold">沃集鲜专区</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer" @click="goTag(42)">
          <span class="material-symbols-outlined text-[16px] text-tertiary">local_fire_department</span>
          <span class="font-body-sm text-body-sm text-on-surface">火锅季</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer" @click="goTag(25)">
          <span class="material-symbols-outlined text-[16px] text-secondary">public</span>
          <span class="font-body-sm text-body-sm text-on-surface">全球好物</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer" @click="goTag(23)">
          <span class="material-symbols-outlined text-[16px] text-primary">child_care</span>
          <span class="font-body-sm text-body-sm text-on-surface">母婴宠物</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer" @click="goTag(21)">
          <span class="material-symbols-outlined text-[16px] text-on-surface-variant">clean_hands</span>
          <span class="font-body-sm text-body-sm text-on-surface">个护美妆</span>
        </div>
      </div>

      <!-- 假期快乐不重样 - 核心运营 Banner 组合（真实商品，点击进详情） -->
      <div class="px-margin mb-space-md" v-if="holidayProducts.length">
        <div class="bg-gradient-to-r from-sky-400 via-sky-300 to-amber-200 rounded-xl p-space-sm shadow-sm relative overflow-hidden">
          <div class="flex items-center justify-between mb-space-xs">
            <h2 class="font-display-hero text-[22px] tracking-tight font-extrabold text-primary-fixed-dim drop-shadow-sm flex items-center gap-1">
              假期快乐不重样
              <span class="material-symbols-outlined text-[20px] text-secondary-container">flight_takeoff</span>
            </h2>
            <span class="font-badge-label text-badge-label bg-surface-container-lowest/80 text-primary px-2 py-0.5 rounded-full font-bold">每日爆款直降</span>
          </div>
          <div class="grid grid-cols-3 gap-2">
            <div
              v-for="p in holidayProducts"
              :key="'holiday-' + p.id"
              class="bg-surface-container-lowest rounded-lg p-1.5 flex flex-col items-center shadow-sm"
              @click="$router.push('/product/' + p.id)"
            >
              <div class="w-full aspect-square rounded-md overflow-hidden bg-surface-container-low mb-1">
                <img class="w-full h-full object-cover" :alt="p.productName" :src="p.imageUrl || PLACEHOLDER_IMG" />
              </div>
              <span class="font-headline-sm text-[11px] text-on-surface truncate w-full text-center">{{ p.productName }}</span>
              <span class="font-price-currency text-[11px] text-tertiary font-bold mt-0.5">¥{{ formatPrice(p.price) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 双专区运营楼层：质选生鲜 & 宝藏新品（真实商品） -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col">
          <div class="flex items-center justify-between mb-0.5">
            <span class="font-headline-sm text-headline-sm text-on-surface font-extrabold">质选生鲜</span>
            <span class="bg-emerald-600 text-on-primary font-badge-label text-[9px] px-1.5 py-0.5 rounded-full">时令&健康</span>
          </div>
          <p class="font-body-sm text-[11px] text-outline truncate mb-2">从源头把控每一份食材</p>
          <div class="grid grid-cols-2 gap-1.5 mt-auto">
            <div
              v-for="p in freshProducts"
              :key="'fresh-' + p.id"
              class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center"
              @click="$router.push('/product/' + p.id)"
            >
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" :alt="p.productName" :src="p.imageUrl || PLACEHOLDER_IMG" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">{{ p.productName }}</span>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥{{ formatPrice(p.price) }}</span>
            </div>
          </div>
        </div>
        <div class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col">
          <div class="flex items-center justify-between mb-0.5">
            <span class="font-headline-sm text-headline-sm text-on-surface font-extrabold">宝藏新品</span>
            <span class="bg-tertiary text-on-tertiary font-badge-label text-[9px] px-1.5 py-0.5 rounded-full">又上新啦!</span>
          </div>
          <p class="font-body-sm text-[11px] text-outline truncate mb-2">发现新奇 精选推荐</p>
          <div class="grid grid-cols-2 gap-1.5 mt-auto">
            <div
              v-for="p in newProducts"
              :key="'new-' + p.id"
              class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center"
              @click="$router.push('/product/' + p.id)"
            >
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" :alt="p.productName" :src="p.imageUrl || PLACEHOLDER_IMG" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">{{ p.productName }}</span>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥{{ formatPrice(p.price) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 大牌低价 & 热门榜单 楼层（真实商品） -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col">
          <div class="flex items-center justify-between mb-0.5">
            <span class="font-headline-sm text-headline-sm text-on-surface font-extrabold">大牌低价</span>
            <span class="bg-purple-600 text-on-primary font-badge-label text-[9px] px-1.5 py-0.5 rounded-full">好物不贵</span>
          </div>
          <p class="font-body-sm text-[11px] text-outline truncate mb-2">生活好选择 闭眼入好价</p>
          <div class="grid grid-cols-2 gap-1.5 mt-auto">
            <div
              v-for="p in discountProducts"
              :key="'disc-' + p.id"
              class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center"
              @click="$router.push('/product/' + p.id)"
            >
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" :alt="p.productName" :src="p.imageUrl || PLACEHOLDER_IMG" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">{{ p.productName }}</span>
              <div class="flex items-baseline gap-1">
                <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥{{ formatPrice(p.price) }}</span>
                <span class="text-[10px] text-outline line-through">¥{{ formatPrice(p.originalPrice) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col">
          <div class="flex items-center justify-between mb-0.5">
            <span class="font-headline-sm text-headline-sm text-on-surface font-extrabold">热门榜单</span>
            <span class="bg-secondary-container text-on-secondary-container font-badge-label text-[9px] px-1.5 py-0.5 rounded-full">真实排名</span>
          </div>
          <p class="font-body-sm text-[11px] text-outline truncate mb-2">真实成交 好评人气飙升</p>
          <div class="grid grid-cols-2 gap-1.5 mt-auto">
            <div
              v-for="(p, i) in bestSellerProducts"
              :key="'best-' + p.id"
              class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center relative"
              @click="$router.push('/product/' + p.id)"
            >
              <span
                class="absolute top-1 left-1 font-badge-label text-[9px] w-4 h-4 rounded-full flex items-center justify-center font-bold"
                :class="i === 0 ? 'bg-amber-500 text-on-primary' : 'bg-slate-400 text-on-primary'"
              >{{ i + 1 }}</span>
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" :alt="p.productName" :src="p.imageUrl || PLACEHOLDER_IMG" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">{{ p.productName }}</span>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥{{ formatPrice(p.price) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 猜你喜欢 / 热卖单品 标签切换条 -->
      <div class="px-margin mb-space-sm flex items-center justify-between overflow-x-auto scrollbar-none gap-space-md">
        <div class="flex flex-col items-center cursor-pointer shrink-0" :class="activeTab === 0 ? '' : 'opacity-70 hover:opacity-100 transition-opacity'" @click="switchTab(0)">
          <span class="font-headline-sm text-[15px] text-primary-container font-extrabold">热卖单品</span>
          <span class="bg-primary-container text-on-primary font-badge-label text-[9px] px-1.5 py-0.2 rounded-full mt-0.5">WOW人气</span>
        </div>
        <div class="flex flex-col items-center cursor-pointer shrink-0" :class="activeTab === 1 ? '' : 'opacity-70 hover:opacity-100 transition-opacity'" @click="switchTab(1)">
          <span class="font-headline-sm text-[14px] text-on-surface font-bold">不时不食</span>
          <span class="text-outline font-body-sm text-[10px]">当季优选</span>
        </div>
        <div class="flex flex-col items-center cursor-pointer shrink-0" :class="activeTab === 2 ? '' : 'opacity-70 hover:opacity-100 transition-opacity'" @click="switchTab(2)">
          <span class="font-headline-sm text-[14px] text-on-surface font-bold">健康零食</span>
          <span class="text-outline font-body-sm text-[10px]">休闲零食</span>
        </div>
        <div class="flex flex-col items-center cursor-pointer shrink-0" :class="activeTab === 3 ? '' : 'opacity-70 hover:opacity-100 transition-opacity'" @click="switchTab(3)">
          <span class="font-headline-sm text-[14px] text-on-surface font-bold">秋日饮品</span>
          <span class="text-outline font-body-sm text-[10px]">酒水饮料</span>
        </div>
      </div>

      <!-- 双列瀑布流热卖卡片区：商品数据来自对应标签的真实商品列表 -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-space-lg">
        <div
          v-for="product in tabProducts"
          :key="product.id"
          class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col justify-between"
          @click="$router.push(`/product/${product.id}`)"
        >
          <div>
            <div class="w-full aspect-square rounded-lg overflow-hidden bg-surface-container-low mb-2 relative">
              <img class="w-full h-full object-cover" :alt="product.productName" :src="product.imageUrl || PLACEHOLDER_IMG" />
              <span
                v-if="product.categoryName"
                class="absolute top-1 left-1 bg-surface-container-lowest/90 text-primary font-badge-label text-[10px] px-1 py-0.5 rounded font-bold"
              >{{ product.categoryName }}</span>
            </div>
            <h3 class="font-headline-sm text-[13px] text-on-surface line-clamp-2 leading-tight mb-1 font-bold">
              {{ product.productName }}
            </h3>
            <p v-if="product.description" class="font-body-sm text-[11px] text-outline truncate mb-1">{{ product.description }}</p>
            <span v-if="product.stock != null" class="font-body-sm text-[11px] text-outline-variant block mb-1">库存 {{ product.stock }} 件</span>
            <div class="inline-flex items-center gap-1 bg-amber-50 text-amber-800 px-1.5 py-0.5 rounded text-[10px] font-bold mb-2">
              <span class="material-symbols-outlined text-[12px] text-amber-600" style="font-variation-settings: 'FILL' 1;">{{ product.stock > 0 ? 'star' : 'info' }}</span>
              {{ product.stock > 0 ? '现货速发' : '暂时缺货' }}
            </div>
          </div>
          <div class="flex items-center justify-between pt-1">
            <div class="flex items-baseline gap-0.5">
              <span class="font-price-currency text-price-currency text-tertiary">¥</span>
              <span class="font-price-lg text-[18px] text-tertiary font-extrabold leading-none">{{ formatPrice(product.price) }}</span>
              <span class="font-body-sm text-[10px] text-outline">/件</span>
            </div>
            <button
              class="w-7 h-7 rounded-full bg-primary-container text-on-primary flex items-center justify-center shadow-md active:scale-90 transition-transform"
              :disabled="product.stock === 0"
              @click.stop="handleAddToCart(product)"
            >
              <span class="material-symbols-outlined text-[18px]">add</span>
            </button>
          </div>
        </div>
        <div v-if="tabLoading" class="col-span-2 text-center py-6 text-outline font-body-sm text-body-sm">加载中…</div>
        <div v-else-if="!tabProducts.length" class="col-span-2 text-center py-6 text-outline font-body-sm text-body-sm">暂无商品</div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  searchProducts,
  getCategoryList,
  getHomeCategories,
  getLatestProducts,
  getDiscountProducts,
  getBestSellers,
  getHotProducts,
  getAddressList
} from '@/api'
import { getToken } from '@/utils/auth'
import { useCartStore } from '@/stores/cart'
import { showToast } from 'vant'

const router = useRouter()
const cartStore = useCartStore()
const categories = ref([])
const homeCategories = ref([])
const loading = ref(true)
const activeTab = ref(0)

// 商品缺图时的占位图（本地静态资源，避免外链不可达）
const PLACEHOLDER_IMG = '/placeholder.svg'

// ===== 收货地址（动态读取默认地址，点击可跳地址管理） =====
const homeAddress = ref('')

function addressLabel(addr) {
  if (!addr) return ''
  return [addr.province, addr.city, addr.district, addr.address].filter(Boolean).join('') || addr.name || ''
}

async function loadAddress() {
  if (!getToken()) return
  try {
    const res = await getAddressList()
    const list = res.data || []
    const def = list.find(a => a.isDefault === 1 || a.isDefault === true) || list[0]
    homeAddress.value = def ? addressLabel(def) : ''
  } catch (e) {
    console.error('加载收货地址失败', e)
  }
}

// ===== 各运营楼层真实商品 =====
const holidayProducts = ref([])    // 假期快乐不重样
const freshProducts = ref([])      // 质选生鲜（分类16 水果生鲜）
const newProducts = ref([])        // 宝藏新品
const discountProducts = ref([])   // 大牌低价（国际品牌 + 折扣）
const bestSellerProducts = ref([]) // 热门榜单（真实成交量）
const tabProducts = ref([])        // 当前标签下的商品瀑布流
const tabLoading = ref(false)

// 底部标签：热卖单品 / 不时不食（水果生鲜）/ 健康零食（休闲零食）/ 秋日饮品（酒水饮料）
const TAB_CONFIG = [
  { type: 'hot' },
  { type: 'category', categoryId: 16 },
  { type: 'category', categoryId: 18 },
  { type: 'category', categoryId: 20 }
]
const tabCache = {}

function formatPrice(price) {
  return Number(price || 0).toFixed(2)
}

async function switchTab(idx) {
  activeTab.value = idx
  await loadTabProducts(idx)
}

async function loadTabProducts(idx) {
  if (tabCache[idx]) {
    tabProducts.value = tabCache[idx]
    return
  }
  tabLoading.value = true
  try {
    const cfg = TAB_CONFIG[idx] || TAB_CONFIG[0]
    let list = []
    if (cfg.type === 'hot') {
      const res = await getBestSellers({ limit: 20 })
      list = res.data || []
    } else {
      const res = await searchProducts({ categoryId: cfg.categoryId, pageNum: 1, pageSize: 20 })
      list = res.data?.records || []
    }
    tabCache[idx] = list
    if (activeTab.value === idx) tabProducts.value = list
  } catch (e) {
    console.error('加载标签商品失败', e)
  } finally {
    tabLoading.value = false
  }
}

// 次级专区标签：跳转到对应真实分类
function goTag(categoryId) {
  router.push({ path: '/category', query: { categoryId } })
}

// 金刚区 10 宫格的渐变底衬（取自设计稿，按序号循环使用）
const CAT_GRADIENTS = [
  'bg-gradient-to-b from-orange-100 to-amber-50',
  'bg-gradient-to-b from-emerald-100 to-teal-50',
  'bg-gradient-to-b from-red-100 to-rose-50',
  'bg-gradient-to-b from-sky-100 to-blue-50',
  'bg-gradient-to-b from-amber-100 to-orange-50',
  'bg-gradient-to-b from-yellow-100 to-amber-50',
  'bg-gradient-to-b from-blue-100 to-indigo-50',
  'bg-gradient-to-b from-orange-100 to-yellow-50',
  'bg-gradient-to-b from-amber-100 to-amber-50',
  'bg-gradient-to-b from-cyan-100 to-sky-50'
]

// 金刚区缺图时的设计稿底图（按序号循环使用）
const CAT_FALLBACK_IMAGES = [
  'https://lh3.googleusercontent.com/aida-public/AB6AXuDXlnB1TbpSUfbn5ivRu31twf0VB7epiG_dvcDcbPBms9H4AAmCSVZdWT5LrbdVMUgKcM1DFhwXaZivKTegoTz4GZXS38FLeE9Z1i5GuJaMV5Yhtj3pGdeyScXEAkbEySCbs7GGYbrWpn2Xs3mtwnSxiO_n6aGWHz-eTRgGaBGy_eDAKAjNuxzWZcxyVTa-Ul6w32vTauA9ZiXmfP9gx-ApZhGgTnLIxWrQS4qmwHAEK9J795MTb7PD',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuDLnZkC_gFuKy0mGsM1ORqQIBCsNMnr-vb2DIRSJnlXJ-0wdZDqLSSH5eS30qpteG0_RFb6ezt1gvtcBGq0ayrMiaDFrLYzFlB6-aI4XkiTMSto8yzBG3-omxOOl1JU32G9Llf8RoQ9-b8TZIHX_zjby5yM3kn8OPXGEwl8beu0Ol6v-Mb3Eh88MHIWju9TgYnkTG_FZj6SjPmdssCV5tlGuKtUvvoisv1KS6_REGxKbxDQkgHgOgN_',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuAG7smnXCtnw2y_oY4p3v95FsVp6AJReGFpDMkR6n6PpZQ1xkWeylB2-1rJeQK1H2B2Leutush5AW5OH4M-0HcuyMmHSCgS9d3uRS4W3fNZULv72xy0JULLrtLLg_TL7B89jbcwANA1V4JRDWakUsVG9OGQuBDgSmbmNVROfXDydvANeSoTAkncNCxBKx2tCSuj5FSijoIG1CDu05xMUsyKleP7u7EOwOeNimpJ9ZJ52XgwVypGadXn',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuBuZoT_FDF2a39TOj9_Pkk089mHLEMbUV0yDdzjvDOIn1Y84cNE5hgbz-9gXW33WSjYphUSskmjH-Be1P-nhxw8gqA_V5evgSTGEYKaA-I16jPO2sFDQq5F5DE9SwqsR0mcMvzppT88NHnGX2AUngxox1TTGcKbIOEIKlbqdzWn8o7mhGIKE3lLvqxoFdZXO5QWuGOSSUAV4XK9-itHK8z33CYL-V27eDU7diOWJC9dJCmr-9AohKZv',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuB7wyr8yVU-xUhCuDi9EJouvMRwRbX1tSkyJYMMMKEsfmHYfKhzzlNJzBXZQt0wRP9_UWCSfO5yVek-gNmxWzvmsJ7c_5w4URfIheZnvGJD6AQKgL91M6jcmULa0fg5PbJQZ_DOUC9EI00MIKah7gSeG4oxegB2FA8CNw_XDoKf9T2Zkp0fHfImX2L5q765YHhItzQX-lOeZiEqhK4g8SCFXP6g13rbFrH1NjHc3M6NBkBsPLc83-k1',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuDAocRfJzzb9VH4Og206enjoJ0NOhtXvXrK538beTpy5ERoz_WJFFFO22GtrNujocVu8PL1Qc_ZsZsBfNKds5-E-CFU1XgwvvQt9Zhk_ntAEmR-GEaQydRdYngk8hCx8SMiUFmpwPBqpWQuv1Z3tZVCOEWj8RFRJ-fvFnxTw3HnihACXPJ7-AGIKDhASwH0nY1V0U0o2yhEDyztpxa7eKv6T5gr2Wd0YdvC45qxjK9AkyEIHM17o9KR',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuAEW9rOzS9mr_S2HZokd7HJPFzfOFZpjY2O5sYYK0iEJJsl_6F62cDqpONn_YunfxgLlaky4tsXt45443HHaZfY6av6268UvqckmgrHdMsalXx_DHk4tWPtwnSSoW_B8TlWIFnvrQmY5Uw3ql7moWi5UxkkY59SYQwTjq4kXD6OwJFJmFFpXykV5bAGH-FKT4yW0TmwJeAXS_jKHErkcxiHutEAxU8yehPCZyOoJivJ_to5dnRRdef7',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuA3YP2Ryqac7MwKHHjaU3uKweV-Zh8Q0uDe6jxjX3UOylujnBSvEAcsk-NTBZdDuxmeNCKjMB4UMidW7T61IkORIlzAFkQxZ6xTazcYEPGg2JF-s1Qx-3HFG1XXmFGPDeOeq0N_rWFPHZnxbBEMTM7uISHh1qvaEgHgtDGo3nRf7_VI_f5J96Ax-1mmOCvFeUgC9h9FeWo8i-o42GRAHlVSVK80JpeKNSUvqMxVPvG8OAv3rV5CTC4s',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuA-QL1JVkbPhZdkK0nmhthHe0JGf5hlBY9cUUSbvHnOYEiIEe9ZEere9P3ACNGni6WcN3XTxigklVpocYf3UvlfTfEz39M3qTO11xNCgn1IfBLMXw1kl_mf1z5UnKb6C2nWtjHlcXIF6P53Yp-vucKIQ4qHAwah0A-PUjHzXKB-xsX-abOxlXyIYda8P3zPe3bLW8J0AuA8G0OVGEdO794lGrzrmYDRfKNRLmYpIccPnM9SIYAh94SD',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuADpShPOU2fYXqxVi2-3G9daOUiyYC2T2Xabs0zTx8UhYikAiPXRDWe9g_EpuvIUr364MmtsEeS_f_2Ynzxl2mSoVXR_edf-SI8MN1uxj3pwmT3iXlfzAiaW9xOYOMnWUv8xFw3i9DApMuXGcHqsA3O9LzxiA3dYN_JgWldB_eW-MUX8fnky1tobaB7K2p49gWH8_SiHHKuEJTwosv1pFlEUIU9zAyFgGGAV-GCEG4pzePb5Br48Hmg'
]

// 金刚区角标（设计稿静态促销文案，如接口返回 promoTag 则以接口为准）
const PROMO_TAGS = ['云南冰糖橙', '', '', '', '苹果鸡肉肠', '南瓜小蛋糕', '', '', '', '薄荷清凉水']

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
  { name: '潮玩萌宠', color: '#FF69B4' }
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
    return categories.value.slice(0, 8).map(c => ({
      id: c.id,
      categoryName: c.categoryName,
      iconUrl: c.iconUrl,
      color: c.color
    }))
  }
  return defaultCategories.slice(0, 8)
})

// 金刚区 10 宫格（优先用首页分类配置；不足 10 个时用真实一级分类补齐，保证每个都能跳转）
const homeGridCategories = computed(() => {
  const list = displayCategories.value.slice(0, 10)
  if (list.length >= 10) return list
  const usedIds = new Set(list.map(c => c.id).filter(Boolean))
  const fill = categories.value
    .filter(c => !usedIds.has(c.id))
    .slice(0, 10 - list.length)
    .map(c => ({ id: c.id, categoryName: c.categoryName, iconUrl: c.iconUrl }))
  return [...list, ...fill]
})

function goCategory(cat) {
  if (cat.id) {
    router.push({ path: '/category', query: { categoryId: cat.id, name: cat.categoryName || cat.name } })
  } else {
    router.push({ path: '/category', query: { name: cat.name } })
  }
}

async function handleAddToCart(product) {
  try {
    await cartStore.addItem(product)
    showToast('已加入购物车')
  } catch (e) {
    showToast(e.message || '添加失败')
  }
}

onMounted(async () => {
  if (!getToken()) { loading.value = false; return }
  loadAddress()
  try {
    const [catRes, homeCatRes, holidayRes, freshRes, newRes, discountRes, bestRes] = await Promise.all([
      getCategoryList().catch(() => ({ data: [] })),
      getHomeCategories().catch(() => ({ data: [] })),
      getHotProducts().catch(() => ({ data: [] })),
      searchProducts({ categoryId: 16, pageNum: 1, pageSize: 4 }).catch(() => ({ data: { records: [] } })),
      getLatestProducts().catch(() => ({ data: [] })),
      getDiscountProducts({ limit: 2 }).catch(() => ({ data: [] })),
      getBestSellers({ limit: 2 }).catch(() => ({ data: [] }))
    ])
    categories.value = catRes.data || []
    homeCategories.value = homeCatRes.data || []
    holidayProducts.value = (holidayRes.data || []).slice(0, 3)
    freshProducts.value = (freshRes.data?.records || []).slice(0, 2)
    newProducts.value = (newRes.data || []).slice(0, 2)
    discountProducts.value = (discountRes.data || []).slice(0, 2)
    bestSellerProducts.value = (bestRes.data || []).slice(0, 2)
    await loadTabProducts(0)
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
})
</script>
