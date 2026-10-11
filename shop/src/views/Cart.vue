<template>
  <!-- Cart — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
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
      <!-- 顶部定位及编辑栏 -->
      <div class="px-margin pt-space-xs pb-space-sm flex items-center justify-between">
        <button class="flex items-center gap-space-xs text-left group min-w-0" @click="$router.push('/address')">
          <span class="material-symbols-outlined text-[18px] text-primary shrink-0">local_shipping</span>
          <span class="font-body-sm text-body-sm text-on-surface-variant shrink-0">送至：</span>
          <span class="font-headline-sm text-headline-sm text-on-surface truncate max-w-[190px]">{{ cartAddress || '请选择收货地址' }}</span>
          <span class="material-symbols-outlined text-[18px] text-on-surface-variant shrink-0">arrow_drop_down</span>
        </button>
      </div>

      <!-- 优惠券动态提示横幅（读取用户真实优惠券数据） -->
      <div v-if="couponBanner" class="px-margin mb-space-sm">
        <div class="bg-secondary-fixed/50 rounded-xl px-space-md py-space-sm flex items-center justify-between gap-space-xs">
          <div class="flex items-center gap-space-xs min-w-0">
            <span class="material-symbols-outlined text-[18px] text-secondary shrink-0" style="font-variation-settings: 'FILL' 1;">redeem</span>
            <div class="font-body-sm text-body-sm text-on-secondary-fixed-variant truncate">
              <template v-if="couponBanner.gap > 0">再买 <span class="font-bold text-tertiary">¥{{ couponBanner.gap.toFixed(2) }}</span> 即可享<span class="bg-tertiary/10 text-tertiary px-1 rounded mx-0.5">{{ couponBanner.label }}</span></template>
              <template v-else>本单已可享<span class="bg-tertiary/10 text-tertiary px-1 rounded mx-0.5">{{ couponBanner.label }}</span></template>
            </div>
          </div>
          <button class="font-body-sm text-body-sm text-secondary font-bold shrink-0 flex items-center" @click="couponBanner.gap > 0 ? $router.push('/') : $router.push('/order/confirm')">
            {{ couponBanner.gap > 0 ? '去凑单' : '去结算' }}
            <span class="material-symbols-outlined text-[14px]">chevron_right</span>
          </button>
        </div>
      </div>

      <!-- 购物车商品卡片列表 (接口数据绑定) -->
      <div v-if="cartStore.items.length" class="px-margin mb-space-lg">
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_2px_8px_rgba(0,30,80,0.04)] p-space-md flex flex-col gap-space-md">
          <!-- 门店标题与组全选 -->
          <div class="flex items-center justify-between pb-space-xs">
            <label class="flex items-center gap-space-sm cursor-pointer select-none" @click.prevent="handleToggleAll">
              <input :checked="isAllChecked" class="group-select sr-only" type="checkbox" />
              <div :class="isAllChecked ? 'w-5 h-5 rounded-full bg-primary-container text-on-primary flex items-center justify-center transition-colors' : 'w-5 h-5 rounded-full border-2 border-outline-variant flex items-center justify-center transition-colors'">
                <span v-if="isAllChecked" class="material-symbols-outlined text-[15px]">check</span>
              </div>
              <div class="flex items-center gap-1.5">
                <span class="material-symbols-outlined text-[18px] text-primary" style="font-variation-settings: 'FILL' 1;">storefront</span>
                <span class="font-headline-sm text-headline-sm text-on-surface">沃尔玛自营生鲜超市</span>
              </div>
            </label>
            <span class="font-badge-label text-badge-label bg-primary/10 text-primary px-1.5 py-0.5 rounded-full">准时达</span>
          </div>

          <!-- 商品行 (v-for + 右滑操作：移入收藏 / 看相似 / 删除) -->
          <van-swipe-cell v-for="item in cartStore.items" :key="item.productId">
            <div class="flex items-start gap-space-sm pt-space-xs bg-surface-container-lowest">
            <label class="item-checkbox-wrapper pt-8 cursor-pointer shrink-0" @click.prevent="cartStore.toggleCheck(item.productId)">
              <input :checked="item.checked" class="item-check sr-only" type="checkbox" />
              <div :class="item.checked ? 'w-5 h-5 rounded-full bg-primary-container text-on-primary flex items-center justify-center transition-colors' : 'w-5 h-5 rounded-full border-2 border-outline-variant flex items-center justify-center transition-colors'">
                <span v-if="item.checked" class="material-symbols-outlined text-[15px]">check</span>
              </div>
            </label>
            <div class="w-[88px] h-[88px] rounded-lg bg-surface-container overflow-hidden shrink-0" @click="handleItemNav(item)">
              <img class="w-full h-full object-cover" :alt="item.productName" :src="item.imageUrl || '/placeholder.svg'" />
            </div>
            <div class="flex-1 min-w-0 flex flex-col justify-between h-[88px]" @click="handleItemNav(item)">
              <div>
                <h3 class="font-body-md text-body-md text-on-surface font-semibold line-clamp-1">{{ item.productName }}</h3>
                <p v-if="item.description || item.spec || item.categoryName" class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">{{ item.description || item.spec || item.categoryName }}</p>
              </div>
              <div class="flex items-end justify-between">
                <div class="text-tertiary">
                  <span class="font-price-currency text-price-currency">¥</span>
                  <span class="font-price-lg text-price-lg">{{ formatPrice(item.price) }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <!-- 步进器：数量为 1 时点减号会二次确认后删除 -->
                  <div class="flex items-center bg-surface-container rounded-full px-1 py-0.5">
                    <button class="step-down w-6 h-6 rounded-full flex items-center justify-center transition-colors" :class="item.quantity <= 1 ? 'text-outline-variant' : 'text-on-surface hover:bg-surface-container-high'" @click.stop="handleDecrease(item)">
                      <span class="material-symbols-outlined text-[14px]">remove</span>
                    </button>
                    <span class="item-count font-body-sm text-body-sm font-bold w-6 text-center text-on-surface">{{ item.quantity }}</span>
                    <button class="step-up w-6 h-6 rounded-full flex items-center justify-center text-on-surface hover:bg-surface-container-high transition-colors" @click.stop="handleQuantityChange(item.productId, item.quantity + 1)">
                      <span class="material-symbols-outlined text-[14px]">add</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
            </div>
            <!-- 右滑操作（移入收藏 / 看相似 / 删除） -->
            <template #right>
              <div class="flex h-full">
                <button class="h-full min-w-[64px] bg-surface-container-high text-on-surface-variant font-body-sm text-[12px] flex items-center justify-center" @click="handleMoveToFav(item)">移入收藏</button>
                <button class="h-full min-w-[64px] bg-secondary-fixed text-on-secondary-fixed-variant font-body-sm text-[12px] flex items-center justify-center" @click="handleViewSimilar(item)">看相似</button>
                <button class="h-full min-w-[64px] bg-[#FF3B30] text-on-primary font-body-sm text-[12px] flex items-center justify-center" @click="handleDelete(item.productId)">删除</button>
              </div>
            </template>
          </van-swipe-cell>
        </div>
      </div>

      <!-- 空购物车态 (接口无数据时展示) -->
      <div v-else class="px-margin mb-space-lg">
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_2px_8px_rgba(0,30,80,0.04)] py-space-xl px-space-md flex flex-col items-center justify-center gap-space-sm">
          <span class="material-symbols-outlined text-[48px] text-outline-variant">remove_shopping_cart</span>
          <p class="font-body-md text-body-md text-on-surface-variant">购物车还是空的</p>
          <button class="h-11 px-6 rounded-full bg-primary-container hover:bg-primary text-on-primary font-headline-sm text-headline-sm flex items-center justify-center shadow-md active:scale-95 transition-all" @click="$router.push('/')">去逛逛</button>
        </div>
      </div>

      <!-- 「猜你喜欢」专区头部 -->
      <div class="flex items-center justify-center gap-space-xs mb-space-md">
        <span class="text-secondary-container text-base">✻</span>
        <h2 class="font-headline-sm text-headline-sm text-on-surface font-bold">猜你喜欢</h2>
        <span class="text-secondary-container text-base">✻</span>
      </div>

      <!-- 双列推荐商品网格（真实商品，整卡进详情，+ 加购） -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-28">
        <div
          v-for="rec in recommendProducts"
          :key="'rec-' + rec.id"
          class="bg-surface-container-lowest rounded-xl shadow-[0_2px_8px_rgba(0,30,80,0.04)] overflow-hidden flex flex-col justify-between group"
          @click="$router.push('/product/' + rec.id)"
        >
          <div>
            <div class="w-full aspect-square bg-surface-container-low overflow-hidden relative">
              <img class="w-full h-full object-cover" :alt="rec.productName" :src="rec.imageUrl || '/placeholder.svg'" />
              <div v-if="rec.categoryName" class="absolute top-2 left-2 bg-tertiary text-on-tertiary font-badge-label text-badge-label px-1.5 py-0.5 rounded shadow-sm">
                {{ rec.categoryName }}
              </div>
            </div>
            <div class="p-space-sm">
              <h4 class="font-body-md text-body-md text-on-surface font-bold line-clamp-2 leading-tight">
                {{ rec.productName }}
              </h4>
              <p v-if="rec.description" class="font-body-sm text-body-sm text-on-surface-variant mt-1 truncate">{{ rec.description }}</p>
            </div>
          </div>
          <div class="px-space-sm pb-space-sm flex items-center justify-between mt-space-xs">
            <div class="flex items-baseline text-tertiary">
              <span class="font-price-currency text-price-currency">¥</span>
              <span class="font-price-lg text-price-lg">{{ formatPrice(rec.price) }}</span>
            </div>
            <button aria-label="加入购物车" class="add-recom-btn w-8 h-8 rounded-full bg-primary-container text-on-primary flex items-center justify-center hover:opacity-90 active:scale-95 transition-transform shadow-sm" @click.stop="handleRecommendAdd(rec)">
              <span class="material-symbols-outlined text-[18px]">add</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 吸底结算操作栏 -->
      <div v-if="cartStore.items.length" class="fixed bottom-14 left-0 right-0 z-40 max-w-[540px] mx-auto bg-surface-container-lowest/95 backdrop-blur-md shadow-[0_-4px_12px_rgba(0,0,0,0.06)] px-margin py-2.5 flex items-center justify-between">
        <!-- 左侧：全选 -->
        <label class="flex items-center gap-space-xs cursor-pointer select-none" @click.prevent="handleToggleAll">
          <input :checked="isAllChecked" class="sr-only" id="bottom-select-all" type="checkbox" />
          <div :class="isAllChecked ? 'w-5 h-5 rounded-full bg-primary-container text-on-primary flex items-center justify-center transition-colors' : 'w-5 h-5 rounded-full border-2 border-outline-variant flex items-center justify-center transition-colors'" id="bottom-checkbox-visual">
            <span v-if="isAllChecked" class="material-symbols-outlined text-[15px]">check</span>
          </div>
          <span class="font-body-md text-body-md text-on-surface font-medium">全选</span>
          <span class="font-body-sm text-body-sm text-on-surface-variant" id="total-count-label">(共{{ cartStore.totalCount }}件)</span>
        </label>
        <!-- 中间与右侧：结算信息与大按钮 -->
        <div class="flex items-center gap-space-md">
          <div class="flex flex-col items-end">
            <div class="flex items-baseline">
              <span class="font-body-sm text-body-sm text-on-surface font-bold mr-1">合计:</span>
              <span class="font-price-currency text-price-currency text-tertiary font-bold">¥</span>
              <span class="font-price-lg text-price-lg text-tertiary font-bold ml-0.5" id="cart-total-price">{{ cartStore.checkedTotal.toFixed(2) }}</span>
            </div>
            <div class="font-body-sm text-body-sm text-on-surface-variant text-[11px] leading-tight">
              已减 ¥10.00 免运费
            </div>
          </div>
          <button class="h-11 px-6 rounded-full bg-primary-container hover:bg-primary text-on-primary font-headline-sm text-headline-sm flex items-center justify-center shadow-md active:scale-95 transition-all" id="checkout-btn" :disabled="!cartStore.checkedItems.length" @click="goCheckout">去结算 ({{ cartStore.totalCount }})</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { showConfirmDialog, showToast } from 'vant'
import { getToken } from '@/utils/auth'
import { getHotProducts, getBestSellers, searchProducts, getAddressList, getCustomerCoupons, addFavorite } from '@/api'

const router = useRouter()
const cartStore = useCartStore()

// 「猜你喜欢」推荐位真实商品
const recommendProducts = ref([])
// 收货地址（动态读取默认地址）
const cartAddress = ref('')
// 用户优惠券（用于满减提示横幅）
const couponList = ref([])

// 购物车全部商品金额
const allItemsTotal = computed(() => cartStore.items.reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0))

// 可用优惠券（未使用且在有效期内）
const validCoupons = computed(() => {
  const now = new Date()
  return couponList.value.filter(c => {
    if (c.status !== 0) return false
    if (c.startTime && new Date(c.startTime) > now) return false
    if (c.endTime && new Date(c.endTime) < now) return false
    return true
  })
})

// 横幅数据：优先展示已可用的最大面额券，否则展示还差最少的券
const couponBanner = computed(() => {
  const list = validCoupons.value
  if (!list.length) return null
  const total = allItemsTotal.value
  // 1) 购物车非空时，优先找已满足门槛的券（面额最大）
  const usable = total > 0 ? list.filter(c => total >= Number(c.minAmount || 0)) : []
  let target
  if (usable.length) {
    target = usable.reduce((a, b) => (Number(b.couponValue || 0) > Number(a.couponValue || 0) ? b : a))
  } else {
    // 2) 找还差最少的券；优先有门槛的券，避免空车时展示无门槛券
    const withMin = list.filter(c => Number(c.minAmount || 0) > 0)
    const pool = withMin.length ? withMin : list
    target = pool.reduce((a, b) => {
      const ga = Number(a.minAmount || 0) - total
      const gb = Number(b.minAmount || 0) - total
      return gb < ga ? b : a
    })
  }
  const gap = Math.max(0, Number(target.minAmount || 0) - total)
  const value = Number(target.couponValue || 0)
  const min = Number(target.minAmount || 0)
  let label
  if (target.couponType === 2) {
    label = min > 0 ? `满${min}可用` : '无门槛折扣'
  } else {
    label = min > 0 ? `满${min}减${value.toFixed(0)}` : `立减${value.toFixed(0)}元`
  }
  return { gap, label }
})

const checkedIds = computed({
  get: () => cartStore.items.filter(i => i.checked).map(i => i.productId),
  set: (val) => {
    cartStore.items.forEach(item => {
      item.checked = val.includes(item.productId)
    })
  }
})

const isAllChecked = computed(() => cartStore.items.length > 0 && cartStore.items.every(i => i.checked))

function formatPrice(price) { return Number(price || 0).toFixed(2) }

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
    cartAddress.value = def ? addressLabel(def) : ''
  } catch (e) {
    console.error('加载收货地址失败', e)
  }
}

function handleToggleAll() {
  const allChecked = cartStore.items.every(i => i.checked)
  cartStore.items.forEach(item => { item.checked = !allChecked })
}

function handleQuantityChange(productId, val) {
  cartStore.updateQuantity(productId, val)
}

// 减号：数量 > 1 时减一；数量为 1 时二次确认后删除该商品
function handleDecrease(item) {
  if (item.quantity <= 1) {
    handleDelete(item.productId)
    return
  }
  cartStore.updateQuantity(item.productId, item.quantity - 1)
}

// 商品行点击：跳详情
function handleItemNav(item) {
  router.push('/product/' + item.productId)
}

async function handleDelete(productId) {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要删除这个商品吗？',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      confirmButtonColor: '#FF3B30',
      teleport: 'body',
      zIndex: 9999,
      overlayStyle: { zIndex: 9998 },
    })
    await cartStore.removeItem(productId)
    showToast('已删除')
  } catch {}
}

// 右滑：移入收藏（收藏成功后从购物车移除）
async function handleMoveToFav(item) {
  try {
    await addFavorite(item.productId)
  } catch (e) {
    // 已收藏过的情况不阻断移入流程
    if (!String(e?.message || '').includes('已收藏')) {
      showToast(e?.message || '收藏失败')
      return
    }
  }
  try {
    await cartStore.removeItem(item.productId)
    showToast('已移入收藏')
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

// 右滑：看相似（跳搜索页按分类/名称搜索同类商品）
function handleViewSimilar(item) {
  const kw = item.categoryName || (item.productName || '').split(/[\s\-（(]/)[0].slice(0, 4)
  router.push({ path: '/search', query: { keyword: kw } })
}

async function handleRecommendAdd(product) {
  try {
    await cartStore.addItem(product)
    showToast('已加入购物车')
  } catch (e) {
    showToast(e.message || '添加失败')
  }
}

function goCheckout() {
  cartStore.clearBuyNowItem()
  router.push('/order/confirm')
}

onMounted(async () => {
  if (getToken()) cartStore.loadCart()
  loadAddress()
  // 加载用户优惠券（动态横幅数据）
  if (getToken()) {
    try {
      const res = await getCustomerCoupons()
      couponList.value = res.data || []
    } catch (e) {
      console.error('加载优惠券失败', e)
    }
  }
  // 加载「猜你喜欢」推荐位真实商品（热销优先，兜底最新商品）
  try {
    const res = await getBestSellers({ limit: 4 }).catch(() => getHotProducts())
    let list = res?.data
    if (!Array.isArray(list)) list = list?.records || []
    if (!list.length) {
      const fallback = await searchProducts({ pageNum: 1, pageSize: 4 }).catch(() => ({ data: { records: [] } }))
      list = fallback.data?.records || []
    }
    recommendProducts.value = list.slice(0, 4)
  } catch (e) {
    console.error('加载推荐位失败', e)
  }
})
</script>
