<template>
  <!-- ProductDetail — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 pl-margin pr-[96px] flex items-center justify-between gap-space-sm">
      <div class="flex items-center gap-space-sm">
        <button aria-label="Back" class="w-11 h-11 flex items-center justify-center text-on-surface hover:text-primary transition-colors -ml-space-xs" @click="$router.back()">
          <span class="material-symbols-outlined text-[24px]">arrow_back_ios_new</span>
        </button>
        <img alt="Walmart Logo with Favicon Spark" class="h-8 w-auto max-w-[45%] object-contain shrink-0" :src="'/api/uploads/商品图片/screen.png'" />
        
      </div>
      <div class="flex items-center gap-space-sm">
        <button aria-label="More actions" class="w-11 h-11 flex items-center justify-center text-on-surface-variant hover:text-primary transition-colors">
          <span class="material-symbols-outlined text-[22px]">more_vert</span>
        </button>
        <img alt="Profile" class="w-8 h-8 rounded-full object-cover" src="/avatar-default.svg" />
      </div>
    </div>
  </header>

  <main v-if="product" class="flex-1 flex flex-col relative w-full pt-14 pb-safe bg-background max-w-[540px] mx-auto min-h-screen">
    <div class="flex flex-col w-full pb-24">
      <!-- Top Transparent/Blur Action Overlay Bar (Floating in Content Area) -->
      <div class="relative w-full">
        <!-- Carousel Gallery Container -->
        <div class="relative w-full aspect-square bg-surface-container overflow-hidden">
          <div class="flex w-full h-full" id="pdp-carousel">
            <div class="w-full h-full flex-shrink-0 relative">
              <img class="w-full h-full object-cover" :src="product.imageUrl || '/placeholder.svg'" :alt="product.productName" />
            </div>
          </div>
          <!-- Quick Action Badges Over Hero -->
          <div class="absolute top-space-md left-space-md right-space-md flex items-center justify-between pointer-events-none z-10">
            <button aria-label="返回上一页" class="w-9 h-9 rounded-full bg-inverse-surface/40 backdrop-blur-md text-on-primary flex items-center justify-center pointer-events-auto active:scale-95 transition-transform" @click="$router.back()">
              <span class="material-symbols-outlined text-[20px]">arrow_back_ios_new</span>
            </button>
            <div class="flex items-center gap-space-xs pointer-events-auto">
              <button aria-label="收藏商品" class="w-9 h-9 rounded-full bg-inverse-surface/40 backdrop-blur-md flex items-center justify-center active:scale-95 transition-transform" id="favBtn" @click="toggleFavorite">
                <span class="material-symbols-outlined text-[20px]" :class="isFav ? 'text-tertiary-container' : 'text-on-primary'" :style="isFav ? `font-variation-settings: 'FILL' 1` : ''">{{ isFav ? 'favorite' : 'favorite_border' }}</span>
              </button>
              <button aria-label="分享商品" class="w-9 h-9 rounded-full bg-inverse-surface/40 backdrop-blur-md text-on-primary flex items-center justify-center active:scale-95 transition-transform" id="shareBtn" @click="showToast('分享功能正在完善中')">
                <span class="material-symbols-outlined text-[20px]">share</span>
              </button>
              <button aria-label="更多操作" class="w-9 h-9 rounded-full bg-inverse-surface/40 backdrop-blur-md text-on-primary flex items-center justify-center active:scale-95 transition-transform">
                <span class="material-symbols-outlined text-[20px]">more_horiz</span>
              </button>
            </div>
          </div>
          <!-- Fresh Harvest Stamp & Badge（按商品动态） -->
          <div class="absolute bottom-space-md left-space-md flex items-center gap-space-xs">
            <span class="bg-secondary-container text-on-secondary-container px-space-sm py-0.5 rounded-full font-badge-label text-badge-label flex items-center gap-1 shadow-sm">
              <span class="material-symbols-outlined text-[13px]" style="font-variation-settings: 'FILL' 1;">{{ heroBadge.icon }}</span>
              {{ heroBadge.text }}
            </span>
          </div>
          <!-- Carousel Pagination Indicator -->
          <div class="absolute bottom-space-md right-space-md bg-inverse-surface/60 backdrop-blur-md text-on-primary px-space-sm py-0.5 rounded-full font-badge-label text-badge-label tracking-wider">
            <span id="carousel-curr" class="">1</span>/1
          </div>
        </div>
      </div>
      <!-- Main Details Section -->
      <div class="px-margin flex flex-col gap-space-md -mt-space-sm relative z-10">
        <!-- Card 1: Core Price & Title Info -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-sm">
          <!-- Price & Promotion Row -->
          <div class="flex items-baseline justify-between">
            <div class="flex items-baseline gap-space-xs">
              <span class="text-tertiary-container font-price-currency text-price-currency">¥</span>
              <span class="text-tertiary-container font-display-hero text-display-hero tracking-tight">{{ formatPrice(product.price) }}</span>
              <span class="text-on-surface-variant font-body-sm text-body-sm">{{ priceUnit }}</span>
              <span v-if="hasDiscount" class="text-outline font-body-sm text-body-sm line-through ml-space-xs">¥{{ formatPrice(product.originalPrice) }}</span>
            </div>
            <!-- Urgency Stock Callout -->
            <div class="flex items-center gap-1 bg-secondary-fixed text-on-secondary-fixed px-space-xs py-0.5 rounded">
              <span class="material-symbols-outlined text-[14px]">local_fire_department</span>
              <span class="font-tab-label text-tab-label">{{ product.stock > 0 ? '仅剩 ' + product.stock + ' 件' : '暂时缺货' }}</span>
            </div>
          </div>
          <!-- Badges and Benefit Tags（按商品动态） -->
          <div class="flex flex-wrap items-center gap-space-xs">
            <span v-if="hasDiscount" class="bg-tertiary-container text-on-tertiary-container px-space-xs py-0.5 rounded font-badge-label text-badge-label">
              限时降价
            </span>
            <span v-if="product.isInternational" class="bg-tertiary-fixed text-on-tertiary-fixed-variant px-space-xs py-0.5 rounded font-badge-label text-badge-label">
              国际品牌
            </span>
            <span class="bg-primary-fixed text-on-primary-fixed-variant px-space-xs py-0.5 rounded font-badge-label text-badge-label">
              {{ product.categoryName || '沃尔玛精选' }}
            </span>
          </div>
          <!-- Product Title -->
          <h1 class="font-headline-lg text-headline-lg text-on-surface line-clamp-2 mt-0.5 leading-snug">
            {{ product.productName }}
          </h1>
          <!-- Key Selling Points Row（按分类动态生成） -->
          <div class="bg-surface-container-low rounded-lg p-space-sm flex items-center justify-between text-on-surface-variant font-body-sm text-body-sm">
            <template v-for="(pt, i) in sellingPoints" :key="'pt-' + i">
              <span v-if="i > 0" class="text-outline-variant">•</span>
              <div class="flex items-center gap-1">
                <span class="material-symbols-outlined text-primary text-[15px]">{{ pt.icon }}</span>
                <span>{{ pt.text }}</span>
              </div>
            </template>
          </div>
        </div>
        <!-- Card 2: Fulfillment & Guarantees -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-md">
          <!-- Delivery Info -->
          <div class="flex items-start gap-space-sm">
            <span class="material-symbols-outlined text-primary text-[20px] mt-0.5">local_shipping</span>
            <div class="flex-1 flex flex-col">
              <div class="flex items-center justify-between">
                <span class="font-headline-sm text-headline-sm text-on-surface">发货配送</span>
                <span class="text-primary font-badge-label text-badge-label bg-primary-fixed/50 px-space-xs py-0.5 rounded">极速履约</span>
              </div>
              <p class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">
                {{ deliveryText }}
              </p>
            </div>
          </div>
          <!-- Divider by spacing & tonal shift -->
          <div class="h-px bg-surface-container-high w-full">
          </div>
          <!-- Service Guarantees -->
          <div class="flex items-start gap-space-sm">
            <span class="material-symbols-outlined text-secondary-container text-[20px] mt-0.5">shield</span>
            <div class="flex-1 flex flex-col">
              <span class="font-headline-sm text-headline-sm text-on-surface">品质安心买</span>
              <p class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">
                {{ qualityText }}
              </p>
            </div>
          </div>
        </div>
        <!-- Card 3: Specifications Table -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-sm">
          <div class="flex items-center justify-between pb-space-xs">
            <h2 class="font-headline-sm text-headline-sm text-on-surface">商品参数与规格</h2>
            <span class="font-body-sm text-body-sm text-outline">品质认证标识</span>
          </div>
          <div class="grid grid-cols-2 gap-x-space-md gap-y-space-sm font-body-sm text-body-sm">
            <div class="flex flex-col bg-surface-container-low p-space-sm rounded-lg">
              <span class="text-on-surface-variant text-[11px]">商品编号</span>
              <span class="text-on-surface font-semibold truncate">{{ product.id }}</span>
            </div>
            <div class="flex flex-col bg-surface-container-low p-space-sm rounded-lg">
              <span class="text-on-surface-variant text-[11px]">商品分类</span>
              <span class="text-on-surface font-semibold truncate">{{ product.categoryName || '—' }}</span>
            </div>
            <div class="flex flex-col bg-surface-container-low p-space-sm rounded-lg">
              <span class="text-on-surface-variant text-[11px]">商品规格</span>
              <span class="text-on-surface font-semibold truncate">{{ specText }}</span>
            </div>
            <div class="flex flex-col bg-surface-container-low p-space-sm rounded-lg">
              <span class="text-on-surface-variant text-[11px]">库存状态</span>
              <span class="text-on-surface font-semibold truncate">{{ product.stock > 0 ? product.stock + ' 件' : '暂时缺货' }}</span>
            </div>
            <div class="flex flex-col bg-surface-container-low p-space-sm rounded-lg">
              <span class="text-on-surface-variant text-[11px]">上架时间</span>
              <span class="text-on-surface font-semibold truncate">{{ createdDate }}</span>
            </div>
            <div class="flex flex-col bg-surface-container-low p-space-sm rounded-lg">
              <span class="text-on-surface-variant text-[11px]">商品价格</span>
              <span class="text-on-surface font-semibold truncate">¥{{ formatPrice(product.price) }}<template v-if="hasDiscount"> (原价 ¥{{ formatPrice(product.originalPrice) }})</template></span>
            </div>
          </div>
        </div>
        <!-- Card 4: Customer Reviews（按商品动态生成） -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-sm">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-space-xs">
              <h2 class="font-headline-sm text-headline-sm text-on-surface">用户评价</h2>
              <span class="font-tab-label text-tab-label text-tertiary-container bg-tertiary-fixed px-space-xs rounded">{{ reviewRate }}% 好评率</span>
            </div>
            <button class="flex items-center text-outline hover:text-primary font-body-sm text-body-sm">
              <span>共 {{ reviewCount }} 条</span>
              <span class="material-symbols-outlined text-[16px]">chevron_right</span>
            </button>
          </div>
          <!-- Quick Tag Chips（按商品生成） -->
          <div class="flex flex-wrap gap-space-xs pt-1">
            <span v-for="tag in reviewTags" :key="tag.text" class="bg-surface-container text-on-surface font-badge-label text-badge-label px-space-sm py-1 rounded-full">{{ tag.text }} ({{ tag.count }})</span>
          </div>
          <!-- Reviewer quotes（3 条，内容随商品变化） -->
          <div v-for="(rv, ri) in reviews" :key="'rv-' + ri" class="bg-surface-container-low p-space-sm rounded-lg mt-space-xs flex flex-col gap-1">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-1.5">
                <span class="w-5 h-5 rounded-full bg-primary text-on-primary font-tab-label text-[10px] flex items-center justify-center font-bold">{{ rv.name.charAt(0) }}</span>
                <span class="font-body-sm text-body-sm text-on-surface font-medium">{{ rv.name }}</span>
                <span class="font-body-sm text-[11px] text-outline">{{ rv.date }}</span>
              </div>
              <div class="flex text-secondary-container">
                <span v-for="si in 5" :key="si" class="material-symbols-outlined text-[14px]" style="font-variation-settings: 'FILL' 1;">star</span>
              </div>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-2">{{ rv.text }}</p>
          </div>
        </div>
        <!-- Card 5: Visual Narrative & Detail Section -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-md mb-space-md">
          <div class="flex items-center justify-center gap-space-sm py-space-xs">
            <div class="h-px bg-outline-variant w-12">
            </div>
            <span class="font-headline-sm text-headline-sm text-on-surface">图文详情</span>
            <div class="h-px bg-outline-variant w-12">
            </div>
          </div>
          <div class="flex flex-col gap-space-sm">
            <div class="overflow-hidden rounded-lg">
              <img class="w-full aspect-[4/3] object-cover" :src="product.imageUrl || '/placeholder.svg'" :alt="product.productName" />
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">
              {{ detailText }}
            </p>
            <div class="bg-surface-container-low rounded-lg p-space-sm flex flex-col gap-2">
              <div v-for="(pt, i) in sellingPoints" :key="'detail-pt-' + i" class="flex items-center gap-1.5 font-body-sm text-body-sm text-on-surface-variant">
                <span class="material-symbols-outlined text-primary text-[16px]">{{ pt.icon }}</span>
                <span>{{ pt.text }}</span>
              </div>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant leading-relaxed">{{ detailServiceText }}</p>
          </div>
        </div>
        <!-- Persistent Bottom Action Bar (PRD 3.4 Floating Dock) -->
        <div class="fixed bottom-0 left-0 right-0 z-40 bg-surface-container-lowest shadow-[0_-4px_16px_rgba(0,0,0,0.06)] pb-safe">
          <div class="max-w-[540px] mx-auto h-16 px-margin flex items-center justify-between gap-space-sm">
            <!-- Utility Icons (Left) -->
            <div class="flex items-center gap-space-xs">
              <!-- Home Icon -->
              <button aria-label="返回首页" class="flex flex-col items-center justify-center w-10 text-on-surface-variant hover:text-primary active:scale-95 transition-transform" @click="$router.push('/')">
                <span class="material-symbols-outlined text-[22px]">storefront</span>
                <span class="font-tab-label text-tab-label">首页</span>
              </button>
              <!-- Customer Service -->
              <button aria-label="联系客服" class="flex flex-col items-center justify-center w-10 text-on-surface-variant hover:text-primary active:scale-95 transition-transform">
                <span class="material-symbols-outlined text-[22px]">headset_mic</span>
                <span class="font-tab-label text-tab-label">客服</span>
              </button>
              <!-- Cart with Quantity Badge -->
              <button aria-label="购物车" class="flex flex-col items-center justify-center w-10 text-on-surface-variant hover:text-primary active:scale-95 transition-transform relative" id="cartBtn" @click="$router.push('/cart')">
                <span class="material-symbols-outlined text-[22px]">shopping_cart</span>
                <span class="font-tab-label text-tab-label">购物车</span>
                <span v-if="cartStore.totalCount > 0" class="absolute -top-0.5 right-1 w-4 h-4 bg-tertiary-container text-on-tertiary-container font-badge-label text-[10px] rounded-full flex items-center justify-center scale-100 transition-transform" id="cartBadge">
                  {{ cartStore.totalCount > 99 ? '99+' : cartStore.totalCount }}
                </span>
              </button>
            </div>
            <!-- Action Buttons (Right) -->
            <div class="flex items-center gap-space-xs flex-1 ml-space-xs">
              <!-- Add To Cart -->
              <button class="flex-1 h-11 rounded-full bg-surface-container-highest text-on-surface font-headline-sm text-[14px] flex items-center justify-center active:scale-[0.98] transition-all duration-150" id="addToCartBtn" @click="addToCart" :disabled="product.stock === 0">
                加入购物车
              </button>
              <!-- Instant Buy -->
              <button class="flex-1 h-11 rounded-full bg-primary-container text-on-primary font-headline-sm text-[14px] shadow-sm flex items-center justify-center active:scale-[0.98] transition-all duration-150" id="buyNowBtn" @click="buyNow" :disabled="product.stock === 0">
                立即购买
              </button>
            </div>
          </div>
        </div>
        <!-- Micro-interaction Toast for Cart Delight -->
        <div class="fixed top-20 left-1/2 -translate-x-1/2 z-50 bg-inverse-surface/90 text-inverse-on-surface font-body-sm text-body-sm px-space-md py-space-xs rounded-full shadow-lg pointer-events-none transition-all duration-300 opacity-0 translate-y-2 flex items-center gap-1.5" id="toastNotification">
          <span class="material-symbols-outlined text-[16px] text-secondary-fixed">check_circle</span>
          <span id="toastMessage" class="">已成功加入购物车</span>
        </div>
      </div>
    </div>
  </main>

  <div v-else class="flex-1 flex flex-col items-center justify-center bg-background max-w-[540px] mx-auto min-h-screen gap-space-sm">
    <span class="material-symbols-outlined text-primary text-[32px] animate-spin">progress_activity</span>
    <span class="font-body-sm text-body-sm text-on-surface-variant">加载中…</span>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail, checkFavorite, addFavorite, removeFavorite } from '@/api'
import { getToken } from '@/utils/auth'
import { useCartStore } from '@/stores/cart'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const product = ref(null)
const isFav = ref(false)

function formatPrice(price) { return Number(price).toFixed(2) }

// ===== 按商品动态计算的展示数据 =====
const pid = computed(() => Number(product.value?.id || 0))
const hasDiscount = computed(() => {
  const p = product.value || {}
  return p.originalPrice != null && Number(p.originalPrice) > Number(p.price)
})
const priceUnit = computed(() => {
  const n = product.value?.productName || ''
  if (/盒/.test(n)) return '/盒'
  if (/袋/.test(n)) return '/袋'
  if (/瓶/.test(n)) return '/瓶'
  if (/箱/.test(n)) return '/箱'
  if (/(斤|公斤|kg|KG|毫升|ml|mL|克)/.test(n)) return '/份'
  return '/件'
})
const specText = computed(() => {
  const n = product.value?.productName || ''
  const m = n.match(/(\d+(?:\.\d+)?\s*(?:g|kg|KG|ml|mL|L|斤|两|只|个|袋|盒|包|片|枚|条|支|罐|瓶|卷|抽|颗|粒|英寸|寸|cm))/)
  return m ? m[1] : '标准装'
})
const createdDate = computed(() => {
  const t = product.value?.createTime || ''
  return t ? String(t).replace('T', ' ').slice(0, 10) : '—'
})
const isFresh = computed(() => /生鲜|水果|蔬菜|粽/.test(product.value?.categoryName || ''))
const isAppliance = computed(() => /家电|数码|电脑|手机|电视|空调|冰箱|洗衣机|风扇/.test(product.value?.categoryName || ''))

const sellingPoints = computed(() => {
  const p = product.value || {}
  if (isFresh.value) return [
    { icon: 'verified', text: '源头直采' },
    { icon: 'ac_unit', text: '产地冷链直达' },
    { icon: 'thumb_up', text: '坏果包赔' }
  ]
  if (p.isInternational) return [
    { icon: 'public', text: '国际品牌' },
    { icon: 'verified', text: '正品保真' },
    { icon: 'workspace_premium', text: '品牌直采' }
  ]
  if (isAppliance.value) return [
    { icon: 'verified', text: '官方正品' },
    { icon: 'build', text: '全国联保' },
    { icon: 'local_shipping', text: '送货入户' }
  ]
  return [
    { icon: 'verified', text: '正品保真' },
    { icon: 'local_shipping', text: '极速发货' },
    { icon: 'autorenew', text: '7天无理由' }
  ]
})

const heroBadge = computed(() => {
  if (isFresh.value) return { icon: 'eco', text: '今日采摘 · 鲜冷达' }
  if (product.value?.isInternational) return { icon: 'public', text: '国际品牌 · 正品直采' }
  return { icon: 'verified', text: '沃尔玛自营 · 正品保障' }
})

const deliveryText = computed(() => {
  if (isFresh.value) return '送货上门（极速达最快 30 分钟内送达）或支持到就近沃尔玛门店免费自提，生鲜商品全程冷链配送。'
  if (isAppliance.value) return '大件商品支持送货入户并预约配送时间，也可到就近沃尔玛门店自提，配送全程可追踪。'
  return '送货上门（极速达最快 30 分钟内送达）或支持到就近沃尔玛门店免费自提。'
})

const qualityText = computed(() => {
  if (isFresh.value) return '沃尔玛正品保真 · 坏果烂果当场赔付包退 · 7天无理由退货无忧'
  if (isAppliance.value) return '沃尔玛官方正品 · 全国联保 · 支持上门安装维修 · 7天无理由退货无忧'
  if (product.value?.isInternational) return '国际品牌正品直采 · 支持防伪查验 · 7天无理由退货无忧'
  return '沃尔玛正品保真 · 假一赔十 · 7天无理由退货无忧'
})

const detailText = computed(() => {
  const p = product.value || {}
  if (p.description) return p.description
  return `精选${p.categoryName || '品质好物'} · ${p.productName}。沃尔玛为您严选每一件商品，从源头把控品质，让每一次购物都安心。`
})

const detailServiceText = computed(() => {
  if (isFresh.value) return '全程冷链配送，收货后请及时冷藏保存；如遇坏果、少发等问题，可在订单页一键售后，沃尔玛为您快速理赔。'
  if (isAppliance.value) return '大件商品支持送货入户并预约安装服务；如遇质量问题，凭订单在订单页发起售后，全国联保更省心。'
  if (product.value?.isInternational) return '国际品牌正品直采，支持防伪查询；如遇质量问题，支持7天无理由退换，售后无忧。'
  return '沃尔玛官方直营，正品保障；如遇质量问题，支持7天无理由退换，下单后由就近仓库发货，全程可追踪。'
})

// ===== 用户评价（按商品 id 稳定生成：每个商品展示不同评价） =====
const reviews = computed(() => {
  const p = product.value
  if (!p) return []
  const id = Number(p.id || 1)
  const names = ['李**', '王**', '张**', '刘**', '陈**', '赵**', '周**', '吴**']
  const texts = isFresh.value ? [
    `${p.productName}收到了，非常新鲜，包装也很仔细，家里人都说好吃，下次还会回购！`,
    `一直在这家买，${p.productName}品相一流，分量足，冷链配送很给力。`,
    `第二次回购${p.productName}了，果肉饱满，新鲜度没得说，五星好评。`
  ] : [
    `${p.productName}质量不错，和描述一致，发货快，包装也挺好。`,
    `买了好几次了，${p.productName}性价比很高，推荐入手。`,
    `${p.productName}收到了，正品无疑，客服服务态度好，很满意！`
  ]
  return [0, 1, 2].map(i => ({
    name: names[(id * 3 + i * 7) % names.length],
    date: `2026-0${((id + i) % 9) + 1}-${10 + ((id * (i + 3)) % 18)}`,
    text: texts[(id + i) % texts.length]
  }))
})

const reviewRate = computed(() => (95 + ((pid.value * 7) % 50) / 10).toFixed(1))
const reviewCount = computed(() => 1000 + (pid.value * 137) % 9000)
const reviewTags = computed(() => {
  const id = pid.value
  const pool = isFresh.value
    ? ['新鲜水灵', '分量充足', '包装完好', '物流很快', '甜度刚好', '值得回购']
    : ['包装完好', '物流很快', '性价比高', '正品保障', '分量充足', '客服耐心']
  return [0, 1, 2].map(i => ({
    text: pool[(id + i * 2) % pool.length],
    count: 100 + (id * (i + 11)) % 3000
  }))
})

// ===== 收藏 =====
async function loadFavState() {
  if (!getToken() || !product.value) return
  try {
    const res = await checkFavorite(product.value.id)
    isFav.value = !!res.data
  } catch (e) { console.error('查询收藏状态失败', e) }
}

async function toggleFavorite() {
  if (!getToken()) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!product.value) return
  try {
    if (isFav.value) {
      await removeFavorite(product.value.id)
      isFav.value = false
      showToast('已取消收藏')
    } else {
      await addFavorite(product.value.id)
      isFav.value = true
      showToast('已加入收藏')
    }
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    loadFavState()
  } catch (e) {
    showToast('商品不存在')
    router.back()
  }
})

async function addToCart() {
  try {
    await cartStore.addItem(product.value)
    showToast('已加入购物车')
  } catch (e) {
    showToast(e.message || '添加失败')
  }
}

function buyNow() {
  cartStore.setBuyNowItem(product.value)
  router.push('/order/confirm')
}
</script>
