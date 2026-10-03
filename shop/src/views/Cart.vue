<template>
  <!-- Cart — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 px-margin flex items-center justify-between gap-space-md">
      <div class="flex items-center gap-space-sm shrink-0">
        <img alt="Walmart Logo with Favicon Spark" class="h-8 w-auto object-contain" src="https://lh3.googleusercontent.com/aida/AEtjO1WxbFcbyvQxjOWkbhkpIy_RXXWkvvU4r8XwbBzzs9Nco_MOan5kyMhQR-HNMCR0mRp1uF8RgvdxFCmQDKNrWLsQIREwC9bCIhANclidwFzjHDJdKYngvbC9SVhJUEFCNQKdAdgsR_kuBjHhBzu_6r_lIOSDxLPhDvxFeI1qc18Fk2Q5QU-DNPWkKJpBlkAaHNmUuGr1XuZnVwAPynaif0RJ1AMEB8zun78Qcq8acrYVeyXlIND6DoxdvQ" />
        <span class="font-headline-sm text-headline-sm text-on-surface tracking-tight font-bold">Cart</span>
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
        <button class="flex items-center gap-space-xs text-left group min-w-0">
          <span class="material-symbols-outlined text-[18px] text-primary shrink-0">local_shipping</span>
          <span class="font-body-sm text-body-sm text-on-surface-variant shrink-0">送至：</span>
          <span class="font-headline-sm text-headline-sm text-on-surface truncate max-w-[190px]">南云商住楼2号楼</span>
          <span class="material-symbols-outlined text-[18px] text-on-surface-variant shrink-0">arrow_drop_down</span>
        </button>
        <button class="font-body-md text-body-md text-on-surface-variant hover:text-primary transition-colors py-1 px-2 shrink-0" id="cart-manage-btn">
          管理
        </button>
      </div>

      <!-- 满减凑单提示横幅 -->
      <div class="px-margin mb-space-sm">
        <div class="bg-secondary-fixed/50 rounded-xl px-space-md py-space-sm flex items-center justify-between gap-space-xs">
          <div class="flex items-center gap-space-xs min-w-0">
            <span class="material-symbols-outlined text-[18px] text-secondary shrink-0" style="font-variation-settings: 'FILL' 1;">redeem</span>
            <div class="font-body-sm text-body-sm text-on-secondary-fixed-variant truncate">
              再买 <span class="font-bold text-tertiary">¥10.10</span> 即可享<span class="bg-tertiary/10 text-tertiary px-1 rounded mx-0.5">满89减10</span>
            </div>
          </div>
          <button class="font-body-sm text-body-sm text-secondary font-bold shrink-0 flex items-center">
            去凑单
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

          <!-- 商品行 (v-for 遍历购物车数据) -->
          <div
            v-for="item in cartStore.items"
            :key="item.productId"
            class="flex items-start gap-space-sm pt-space-xs"
          >
            <label class="item-checkbox-wrapper pt-8 cursor-pointer shrink-0" @click.prevent="cartStore.toggleCheck(item.productId)">
              <input :checked="item.checked" class="item-check sr-only" type="checkbox" />
              <div :class="item.checked ? 'w-5 h-5 rounded-full bg-primary-container text-on-primary flex items-center justify-center transition-colors' : 'w-5 h-5 rounded-full border-2 border-outline-variant flex items-center justify-center transition-colors'">
                <span v-if="item.checked" class="material-symbols-outlined text-[15px]">check</span>
              </div>
            </label>
            <div class="w-[88px] h-[88px] rounded-lg bg-surface-container overflow-hidden shrink-0" @click="$router.push('/product/' + item.productId)">
              <img class="w-full h-full object-cover" data-alt="Cluster of premium luminous green Shine Muscat grapes with fresh water droplets on vine, crisp studio lighting, macro commercial photography" :src="item.imageUrl || '/placeholder.svg'" />
            </div>
            <div class="flex-1 min-w-0 flex flex-col justify-between h-[88px]">
              <div>
                <h3 class="font-body-md text-body-md text-on-surface font-semibold line-clamp-1">{{ item.productName }}</h3>
                <p v-if="item.description || item.spec || item.categoryName" class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">{{ item.description || item.spec || item.categoryName }}</p>
              </div>
              <div class="flex items-end justify-between">
                <div class="text-tertiary">
                  <span class="font-price-currency text-price-currency">¥</span>
                  <span class="font-price-lg text-price-lg">{{ formatPrice(item.price) }}</span>
                </div>
                <!-- 步进器 -->
                <div class="flex items-center bg-surface-container rounded-full px-1 py-0.5">
                  <button class="step-down w-6 h-6 rounded-full flex items-center justify-center text-on-surface hover:bg-surface-container-high transition-colors" @click="item.quantity > 1 && handleQuantityChange(item.productId, item.quantity - 1)">
                    <span class="material-symbols-outlined text-[14px]">remove</span>
                  </button>
                  <span class="item-count font-body-sm text-body-sm font-bold w-6 text-center text-on-surface">{{ item.quantity }}</span>
                  <button class="step-up w-6 h-6 rounded-full flex items-center justify-center text-on-surface hover:bg-surface-container-high transition-colors" @click="handleQuantityChange(item.productId, item.quantity + 1)">
                    <span class="material-symbols-outlined text-[14px]">add</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
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

      <!-- 「猜你喜欢」专区头部 (营销位，保留设计稿静态内容) -->
      <div class="flex items-center justify-center gap-space-xs mb-space-md">
        <span class="text-secondary-container text-base">✻</span>
        <h2 class="font-headline-sm text-headline-sm text-on-surface font-bold">猜你喜欢</h2>
        <span class="text-secondary-container text-base">✻</span>
      </div>

      <!-- 双列推荐商品瀑布流/网格 (营销位，保留设计稿静态内容) -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-28">
        <!-- 推荐卡片 1: 七度空间 -->
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_2px_8px_rgba(0,30,80,0.04)] overflow-hidden flex flex-col justify-between group">
          <div>
            <div class="w-full aspect-square bg-surface-container-low overflow-hidden relative">
              <img class="w-full h-full object-cover" data-alt="Blue hygiene cotton pads commercial product pack with night care floral graphics, vibrant retail banner text 420mm ultra soft pure cotton, clean white backdrop" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAe5ivXsS_cOZAeRUl7G6Qi78-4mqFxPRq21dHibF9fnaX7ZYyT9t7od7y5kDGW5AN3h9G6sk2KTCL9qEpRhw2Sgzp8WGeMLNzvwFnhKqCYvA06sHfdbaer9TwGJkO0uMd2c9UAUUvXF6GjxXYVKTXegHz133W1YCpucteSvs0u0nIK_U0rmCW1bFV2Pr3sC4YzV562_miZU9H1KhH-Kc_8xoVh5iJ6-yN3pfqXKQtuyXgHvLki8f6E" />
              <div class="absolute top-2 left-2 bg-tertiary text-on-tertiary font-badge-label text-badge-label px-1.5 py-0.5 rounded shadow-sm">
                热卖爆款
              </div>
            </div>
            <div class="p-space-sm">
              <h4 class="font-body-md text-body-md text-on-surface font-bold line-clamp-2 leading-tight">
                七度空间 超薄100%纯棉夜用安心420mm 8片...
              </h4>
              <p class="font-body-sm text-body-sm text-on-surface-variant mt-1 truncate">整晚自在清新</p>
            </div>
          </div>
          <div class="px-space-sm pb-space-sm flex items-center justify-between mt-space-xs">
            <div class="flex items-baseline text-tertiary">
              <span class="font-price-currency text-price-currency">¥</span>
              <span class="font-price-lg text-price-lg">9.99</span>
              <span class="font-body-sm text-body-sm text-on-surface-variant font-normal ml-0.5">/包</span>
            </div>
            <button aria-label="加入购物车" class="add-recom-btn w-8 h-8 rounded-full bg-primary-container text-on-primary flex items-center justify-center hover:opacity-90 active:scale-95 transition-transform shadow-sm">
              <span class="material-symbols-outlined text-[18px]">add</span>
            </button>
          </div>
        </div>
        <!-- 推荐卡片 2: 威猛先生 -->
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_2px_8px_rgba(0,30,80,0.04)] overflow-hidden flex flex-col justify-between group">
          <div>
            <div class="w-full aspect-square bg-surface-container-low overflow-hidden relative">
              <img class="w-full h-full object-cover" data-alt="Two orange trigger spray bottles of Mr Muscle heavy duty kitchen degreaser with fresh yellow lemon splash, dynamic cleaning product packshot, clean modern kitchen counter" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDQlv12DLpttk60_1HDqBwcK-BGGY28fM5wMOIwRwrUDGtwhreV0Izgl32U-Zt0fbHcrbF_guJZ8RxdAtcRXip3L2J10m9gSf55ZwxXp6jUHx1MqvWQnK5Ry5V7r9c--lFWeG8hi8WKNJGugee7OmH2dEECqMzhRoXM1whfhg1WMgxZkO51N9nbW_kuUNe_Opefb2n8hUh3NxH4tYnpyOG36HKV8eNndKhuScRWH0Tb0Ahxt6caSs1F" />
              <div class="absolute top-2 left-2 bg-secondary-container text-on-secondary-container font-badge-label text-badge-label px-1.5 py-0.5 rounded shadow-sm">
                榜首力荐
              </div>
            </div>
            <div class="p-space-sm">
              <h4 class="font-body-md text-body-md text-on-surface font-bold line-clamp-2 leading-tight">
                威猛先生 厨房重油污净 (清爽柠檬) 500g*2瓶
              </h4>
              <p class="font-body-sm text-body-sm text-on-surface-variant mt-1 truncate">除菌率99.9%，更强清洁力</p>
            </div>
          </div>
          <div>
            <div class="px-space-sm pb-space-xs">
              <div class="inline-flex items-center gap-1 bg-secondary-fixed/50 px-1.5 py-0.5 rounded text-[10px] text-on-secondary-fixed-variant">
                <span class="font-bold text-secondary">榜</span>
                <span class="">厨房清洁热销榜 TOP 10</span>
              </div>
            </div>
            <div class="px-space-sm pb-space-sm flex items-center justify-between">
              <div class="flex items-baseline text-tertiary">
                <span class="font-price-currency text-price-currency">¥</span>
                <span class="font-price-lg text-price-lg">22.9</span>
                <span class="font-body-sm text-body-sm text-on-surface-variant font-normal ml-0.5">/组</span>
              </div>
              <button aria-label="加入购物车" class="add-recom-btn w-8 h-8 rounded-full bg-primary-container text-on-primary flex items-center justify-center hover:opacity-90 active:scale-95 transition-transform shadow-sm">
                <span class="material-symbols-outlined text-[18px]">add</span>
              </button>
            </div>
          </div>
        </div>
        <!-- 推荐卡片 3: 维达超韧抽纸 -->
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_2px_8px_rgba(0,30,80,0.04)] overflow-hidden flex flex-col justify-between group">
          <div>
            <div class="w-full aspect-square bg-surface-container-low overflow-hidden relative">
              <img class="w-full h-full object-cover" data-alt="Blue plastic multi-pack bundle of Vinda ultra-strong facial tissues, silky smooth texture, soft commercial studio light, e-commerce catalog image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDgq1xCGiXBnYQ6I513e7YNzg6SZEObr6iUmayftd6IOnks5rckXSrLlXVhG1QiUV04H-8DN4tKvLLFBhZwR6cDdS3cH9-Sblfd15WBMLqPycghW3uapwk4h0Id7_JqdzY0hcb0fH19Teo1GIqwvZjJM84WWUc79zqgZw6kQT6qvXoeSg8_17ZqGu5QpLME-_E9yX9gsOeLbEE_LqxZcro5x5uRVfqFoz9tjDxvbhrvmyGQFtpP-Jh4" />
            </div>
            <div class="p-space-sm">
              <h4 class="font-body-md text-body-md text-on-surface font-bold line-clamp-2 leading-tight">
                维达超韧抽纸 3层120抽*24包 整箱装
              </h4>
              <p class="font-body-sm text-body-sm text-on-surface-variant mt-1 truncate">湿水不易破 亲肤无刺激</p>
            </div>
          </div>
          <div class="px-space-sm pb-space-sm flex items-center justify-between mt-space-xs">
            <div class="flex items-baseline text-tertiary">
              <span class="font-price-currency text-price-currency">¥</span>
              <span class="font-price-lg text-price-lg">45.9</span>
              <span class="font-body-sm text-body-sm text-on-surface-variant font-normal ml-0.5">/箱</span>
            </div>
            <button aria-label="加入购物车" class="add-recom-btn w-8 h-8 rounded-full bg-primary-container text-on-primary flex items-center justify-center hover:opacity-90 active:scale-95 transition-transform shadow-sm">
              <span class="material-symbols-outlined text-[18px]">add</span>
            </button>
          </div>
        </div>
        <!-- 推荐卡片 4: 泰国金枕头榴莲肉 -->
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_2px_8px_rgba(0,30,80,0.04)] overflow-hidden flex flex-col justify-between group">
          <div>
            <div class="w-full aspect-square bg-surface-container-low overflow-hidden relative">
              <img class="w-full h-full object-cover" data-alt="Golden rich Thai Monthong durian pulp pieces on eco-friendly paper tray, frosty cool mist, luscious fresh fruit market styling" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBPrPTZhWQHqQrq4vC3JqaHS2fqztypFNI_z_RZke0Akwz0lmYQJ56Apo0zez9mhXdXcs6kDIaE91zCAuFqDLW3BnENQOhBWo7MNaWjoBO-atEeeUKhBRxBZtcuqyoc8DkPrnv0RJsua_2rcJnepDs918JWRiBc585gk9M6t0pkcKcPmMIE7gLxCPjr-HZoq0KTENNivSchQU0GU12n1Me-LdDpN0cPpV_zyxSnh5fXdOlwXwKOxz9Y" />
              <div class="absolute top-2 left-2 bg-tertiary text-on-tertiary font-badge-label text-badge-label px-1.5 py-0.5 rounded shadow-sm">
                限时秒杀
              </div>
            </div>
            <div class="p-space-sm">
              <h4 class="font-body-md text-body-md text-on-surface font-bold line-clamp-2 leading-tight">
                泰国进口金枕头冷冻榴莲肉 250g*2盒
              </h4>
              <p class="font-body-sm text-body-sm text-on-surface-variant mt-1 truncate">肉质软糯 浓郁爆甜</p>
            </div>
          </div>
          <div class="px-space-sm pb-space-sm flex items-center justify-between mt-space-xs">
            <div class="flex items-baseline text-tertiary">
              <span class="font-price-currency text-price-currency">¥</span>
              <span class="font-price-lg text-price-lg">39.9</span>
              <span class="font-body-sm text-body-sm text-on-surface-variant font-normal ml-0.5">/组</span>
            </div>
            <button aria-label="加入购物车" class="add-recom-btn w-8 h-8 rounded-full bg-primary-container text-on-primary flex items-center justify-center hover:opacity-90 active:scale-95 transition-transform shadow-sm">
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
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { showConfirmDialog, showToast } from 'vant'
import { getToken } from '@/utils/auth'

const router = useRouter()
const cartStore = useCartStore()

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

function handleToggleAll() {
  const allChecked = cartStore.items.every(i => i.checked)
  cartStore.items.forEach(item => { item.checked = !allChecked })
}

function handleQuantityChange(productId, val) {
  cartStore.updateQuantity(productId, val)
}

async function handleDelete(productId) {
  try {
    await showConfirmDialog({ title: '提示', message: '确定要删除这个商品吗？' })
    await cartStore.removeItem(productId)
    showToast('已删除')
  } catch {}
}

function goCheckout() {
  cartStore.clearBuyNowItem()
  router.push('/order/confirm')
}

onMounted(() => {
  if (getToken()) cartStore.loadCart()
})
</script>
