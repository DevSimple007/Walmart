<template>
  <!-- OrderConfirm — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 px-margin flex items-center justify-between gap-space-sm">
      <div class="flex items-center gap-space-sm">
        <button aria-label="Back" class="w-11 h-11 flex items-center justify-center text-on-surface hover:text-primary transition-colors -ml-space-xs" @click="$router.back()">
          <span class="material-symbols-outlined text-[24px]">arrow_back_ios_new</span>
        </button>
        <img alt="Walmart Logo with Favicon Spark" class="h-8 w-auto object-contain" src="https://lh3.googleusercontent.com/aida/AEtjO1WxbFcbyvQxjOWkbhkpIy_RXXWkvvU4r8XwbBzzs9Nco_MOan5kyMhQR-HNMCR0mRp1uF8RgvdxFCmQDKNrWLsQIREwC9bCIhANclidwFzjHDJdKYngvbC9SVhJUEFCNQKdAdgsR_kuBjHhBzu_6r_lIOSDxLPhDvxFeI1qc18Fk2Q5QU-DNPWkKJpBlkAaHNmUuGr1XuZnVwAPynaif0RJ1AMEB8zun78Qcq8acrYVeyXlIND6DoxdvQ" />
        <h1 class="font-headline-sm text-headline-sm text-on-surface tracking-tight font-bold truncate max-w-[160px]">Checkout Flow</h1>
      </div>
      <div class="flex items-center gap-space-sm">
        <button aria-label="More actions" class="w-11 h-11 flex items-center justify-center text-on-surface-variant hover:text-primary transition-colors">
          <span class="material-symbols-outlined text-[22px]">more_vert</span>
        </button>
        <img alt="Profile" class="w-8 h-8 rounded-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAbWRWRjaR21qK1jU0kCwRb_bAPV-hTUNuaZhWlx7qH_Va4V32egzB9EA-dyJjfPe9hnkJfPAuD8FSIvw03zd17tPdYx3K8XtSc9b3_YTnlSDYSwkgXgjlVRRjOPE3rL-UQNuJ74U9yW3VtEtg7VpupRBb7r9Y5r5wmAz-R2XiUVAipjvedop15RZTvklVj-vtaXe9VOD3ODfIai_ITvZaWs2hlEdS_VxLLC-YWL49tz4gKH6_IB6yQ" />
      </div>
    </div>
  </header>
  <main class="flex-1 flex flex-col relative w-full pt-14 pb-safe bg-background max-w-[540px] mx-auto min-h-screen">
    <div class="flex flex-col w-full pb-28" v-if="cartStore.orderItems.length">
      <!-- Top Address / Fulfillment Mode Switch -->
      <section class="p-margin flex flex-col gap-space-md">
        <!-- Fulfillment Toggle Pill -->
        <div class="bg-surface-container-high p-space-xs rounded-full grid grid-cols-2 gap-space-xs shadow-inner" id="fulfillmentToggle">
          <button class="relative flex items-center justify-center gap-space-xs py-2 px-space-md rounded-full transition-all duration-200" :class="deliveryType === 'delivery' ? 'bg-primary-container text-on-primary shadow-sm' : 'text-on-surface-variant hover:text-on-surface'" id="btnDelivery" @click="deliveryType = 'delivery'" type="button">
            <span class="material-symbols-outlined text-[18px]">local_shipping</span>
            <span class="font-headline-sm text-headline-sm">送货上门</span>
            <span class="font-badge-label text-badge-label bg-surface-container-lowest/20 px-1.5 py-0.5 rounded-full">配送费 ¥{{ deliveryFee }}</span>
            <span v-if="deliveryType === 'delivery'" class="material-symbols-outlined text-[14px] absolute right-2.5 top-1/2 -translate-y-1/2" style="font-variation-settings: 'FILL' 1;">check_circle</span>
          </button>
          <button class="relative flex items-center justify-center gap-space-xs py-2 px-space-md rounded-full transition-all duration-200" :class="deliveryType === 'pickup' ? 'bg-primary-container text-on-primary shadow-sm' : 'text-on-surface-variant hover:text-on-surface'" id="btnPickup" @click="deliveryType = 'pickup'" type="button">
            <span class="material-symbols-outlined text-[18px]">storefront</span>
            <span class="font-headline-sm text-headline-sm">到店自提</span>
            <span class="font-badge-label text-badge-label bg-surface-container-highest px-1.5 py-0.5 rounded-full text-secondary">免配送费</span>
          </button>
        </div>
        <!-- Delivery Address Card (Delivery Mode) -->
        <div class="bg-surface-container-lowest rounded-xl p-space-lg shadow-sm transition-all duration-300" id="addressSection" v-if="deliveryType === 'delivery'">
          <!-- 暂无地址 -->
          <div v-if="!addressList.length" class="flex items-start justify-between gap-space-md cursor-pointer active:opacity-70 transition-opacity">
            <div class="flex gap-space-sm items-start flex-1 min-w-0">
              <div class="w-8 h-8 rounded-full bg-primary-fixed flex items-center justify-center text-primary shrink-0 mt-0.5">
                <span class="material-symbols-outlined text-[20px]" style="font-variation-settings: 'FILL' 1;">location_on</span>
              </div>
              <div class="flex flex-col gap-1 min-w-0">
                <p class="font-body-md text-body-md text-on-surface leading-snug line-clamp-2">暂无收货地址</p>
              </div>
            </div>
          </div>
          <!-- 地址列表 -->
          <div
            v-for="addr in addressList"
            :key="addr.id"
            class="flex items-start justify-between gap-space-md cursor-pointer active:opacity-70 transition-opacity"
            :class="{ 'ring-2 ring-primary-fixed rounded-lg': selectedAddressId === addr.id }"
            @click="selectAddress(addr)"
          >
            <div class="flex gap-space-sm items-start flex-1 min-w-0">
              <div class="w-8 h-8 rounded-full bg-primary-fixed flex items-center justify-center text-primary shrink-0 mt-0.5">
                <span class="material-symbols-outlined text-[20px]" style="font-variation-settings: 'FILL' 1;">location_on</span>
              </div>
              <div class="flex flex-col gap-1 min-w-0">
                <div class="flex items-center gap-space-xs">
                  <span class="font-headline-sm text-headline-sm text-on-surface">{{ addr.name }}</span>
                  <span class="font-body-md text-body-md text-on-surface-variant">{{ addr.phone }}</span>
                  <span v-if="addr.isDefault === 1 || addr.isDefault === true" class="font-badge-label text-badge-label bg-primary-container text-on-primary px-1.5 py-0.5 rounded">默认</span>
                </div>
                <p class="font-body-md text-body-md text-on-surface leading-snug line-clamp-2">{{ formatAddress(addr) }}</p>
              </div>
            </div>
            <span class="material-symbols-outlined text-outline text-[20px] shrink-0 self-center">chevron_right</span>
          </div>
          <!-- Delivery Slot Selector -->
          <div class="mt-space-md pt-space-md flex items-center justify-between gap-space-sm bg-surface-container-low p-space-sm rounded-lg cursor-pointer" @click="showTimePicker = true">
            <div class="flex items-center gap-1.5 text-on-surface">
              <span class="material-symbols-outlined text-primary text-[18px]">schedule</span>
              <span class="font-body-md text-body-md font-semibold">送达时间</span>
            </div>
            <div class="flex items-center gap-1 text-primary">
              <span class="font-body-sm text-body-sm font-semibold">{{ deliveryTime || '请选择期望送达时间' }}</span>
              <span class="font-badge-label text-badge-label bg-secondary-fixed text-on-secondary-fixed px-1 rounded font-bold">极速履约</span>
              <span class="material-symbols-outlined text-[16px]">chevron_right</span>
            </div>
          </div>
        </div>
        <!-- Store Pickup Card -->
        <div class="bg-surface-container-lowest rounded-xl p-space-lg shadow-sm" id="pickupSection" v-if="deliveryType === 'pickup'">
          <div class="flex items-center justify-between gap-space-md">
            <div class="flex gap-space-sm items-start flex-1 min-w-0">
              <div class="w-8 h-8 rounded-full bg-secondary-fixed flex items-center justify-center text-on-secondary-fixed shrink-0">
                <span class="material-symbols-outlined text-[20px]">store</span>
              </div>
              <div class="flex flex-col gap-1">
                <div class="flex items-center gap-space-xs">
                  <span class="font-headline-sm text-headline-sm text-on-surface">沃尔玛深圳蛇口店</span>
                  <span class="font-badge-label text-badge-label bg-surface-container-highest text-on-surface-variant px-1.5 py-0.5 rounded">距您 1.2km</span>
                </div>
                <p class="font-body-sm text-body-sm text-on-surface-variant">自提无需配送地址，核销凭证将在支付后生成</p>
              </div>
            </div>
            <span class="material-symbols-outlined text-outline text-[20px]">chevron_right</span>
          </div>
        </div>
      </section>
      <!-- SKU Items Accordion Card -->
      <section class="px-margin mb-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-lg shadow-sm">
          <!-- Store Header -->
          <div class="flex items-center justify-between pb-space-sm mb-space-sm">
            <div class="flex items-center gap-1.5">
              <span class="material-symbols-outlined text-primary text-[20px]">storefront</span>
              <span class="font-headline-sm text-headline-sm text-on-surface">沃尔玛自营超市</span>
              <span class="font-badge-label text-badge-label bg-primary-fixed text-on-primary-fixed px-1.5 py-0.5 rounded font-bold">正品保真</span>
            </div>
            <span class="font-body-sm text-body-sm text-on-surface-variant">共 {{ cartStore.orderItems.length }} 件</span>
          </div>
          <!-- Item List -->
          <div class="flex flex-col gap-space-md">
            <div class="flex items-center gap-space-md" v-for="item in cartStore.orderItems" :key="item.productId">
              <div class="relative w-16 h-16 rounded-lg overflow-hidden shrink-0 bg-surface-container">
                <img class="w-full h-full object-cover" :data-alt="item.productName" :src="item.imageUrl || '/placeholder.svg'" />
              </div>
              <div class="flex-1 min-w-0 flex flex-col justify-between h-16">
                <div class="flex justify-between items-start gap-space-xs">
                  <h2 class="font-body-md text-body-md text-on-surface font-semibold truncate">{{ item.productName }}</h2>
                  <span class="font-price-lg text-price-lg text-on-surface shrink-0">¥{{ formatPrice(item.price) }}</span>
                </div>
                <div class="flex justify-between items-center text-on-surface-variant">
                  <span v-if="item.spec || item.subtitle" class="font-body-sm text-body-sm bg-surface-container-low px-1.5 py-0.5 rounded">{{ item.spec || item.subtitle }}</span>
                  <span class="font-body-sm text-body-sm font-medium">x {{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
      <!-- Promotions & Order Notes Card -->
      <section class="px-margin mb-space-md flex flex-col gap-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-lg shadow-sm flex flex-col gap-space-md">
          <!-- Coupon Selector -->
          <div class="flex items-center justify-between cursor-pointer active:opacity-75 transition-opacity" @click="showCouponPopup = true">
            <div class="flex items-center gap-1.5 text-on-surface">
              <span class="material-symbols-outlined text-tertiary-container text-[20px]">confirmation_number</span>
              <span class="font-headline-sm text-headline-sm">优惠券</span>
            </div>
            <div class="flex items-center gap-1">
              <span class="font-badge-label text-badge-label bg-secondary-fixed text-on-secondary-fixed-variant px-2 py-0.5 rounded-full font-bold flex items-center gap-0.5">
                <span class="material-symbols-outlined text-[14px]">local_activity</span>
                {{ selectedCoupon ? '已选 ' + selectedCoupon.couponName : (usableCoupons.length ? usableCoupons.length + ' 张可用' : '暂无可用优惠券') }}
              </span>
              <span class="material-symbols-outlined text-outline text-[18px]">chevron_right</span>
            </div>
          </div>
          <!-- Invoice / Tax Info Optional -->
          <div class="flex items-center justify-between cursor-pointer">
            <span class="font-body-md text-body-md text-on-surface">发票信息</span>
            <div class="flex items-center gap-1 text-on-surface-variant">
              <span class="font-body-sm text-body-sm">个人电子普通发票</span>
              <span class="material-symbols-outlined text-outline text-[18px]">chevron_right</span>
            </div>
          </div>
          <!-- Order Note Input -->
          <div class="flex items-center gap-space-sm pt-space-xs">
            <span class="font-body-md text-body-md text-on-surface whitespace-nowrap">订单备注</span>
            <input class="w-full bg-surface-container-low text-on-surface placeholder:text-outline-variant font-body-sm text-body-sm px-space-md py-2 rounded-lg focus:outline-none focus:bg-surface-container focus:text-on-surface transition-colors" maxlength="50" placeholder="选填，请和配送员协商一致（50字内）" type="text" v-model="remark" />
          </div>
        </div>
      </section>
      <!-- Pricing Breakdown Card -->
      <section class="px-margin mb-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-lg shadow-sm flex flex-col gap-space-sm">
          <h3 class="font-headline-sm text-headline-sm text-on-surface mb-1">金额明细</h3>
          <div class="flex items-center justify-between text-body-md font-body-md text-on-surface-variant">
            <span class="">商品总额</span>
            <span class="text-on-surface font-semibold">¥{{ cartStore.orderTotal.toFixed(2) }}</span>
          </div>
          <div class="flex items-center justify-between text-body-md font-body-md text-on-surface-variant" v-if="deliveryType === 'delivery'">
            <span class="">配送费</span>
            <span class="text-on-surface font-semibold" id="deliveryFeeVal">¥{{ deliveryFee.toFixed(2) }}</span>
          </div>
          <div class="flex items-center justify-between text-body-md font-body-md text-on-surface-variant" v-if="couponDiscount > 0">
            <span class="">优惠券抵扣</span>
            <span class="text-tertiary font-bold">-¥{{ couponDiscount.toFixed(2) }}</span>
          </div>
          <div class="flex items-center justify-between text-body-md font-body-md text-on-surface-variant" id="freightDiscountRow">
            <div class="flex items-center gap-1">
              <span class="">运费减免</span>
              <span class="font-badge-label text-badge-label bg-secondary-fixed text-on-secondary-fixed px-1 rounded">新客首单</span>
            </div>
            <span class="text-tertiary font-bold">-¥5.00</span>
          </div>
        </div>
      </section>
      <!-- Safe Delivery Promise Micro-banner -->
      <section class="px-margin mb-space-md flex items-center justify-center gap-space-md text-outline">
        <div class="flex items-center gap-1 text-on-surface-variant font-body-sm text-body-sm">
          <span class="material-symbols-outlined text-[16px] text-primary">verified_user</span>
          <span class="">正品自营保证</span>
        </div>
        <span class="text-surface-container-highest">•</span>
        <div class="flex items-center gap-1 text-on-surface-variant font-body-sm text-body-sm">
          <span class="material-symbols-outlined text-[16px] text-primary">update</span>
          <span class="">超时无忧赔付</span>
        </div>
        <span class="text-surface-container-highest">•</span>
        <div class="flex items-center gap-1 text-on-surface-variant font-body-sm text-body-sm">
          <span class="material-symbols-outlined text-[16px] text-primary">eco</span>
          <span class="">绿色环保配送</span>
        </div>
      </section>
      <!-- Sticky Bottom Checkout Bar (Level 3 Elevation) -->
      <aside class="fixed bottom-0 left-0 right-0 z-40 bg-surface-container-lowest shadow-[0_-4px_16px_rgba(0,0,0,0.06)] pb-safe">
        <div class="max-w-[540px] mx-auto px-margin h-16 flex items-center justify-between">
          <!-- Price Summary -->
          <div class="flex flex-col">
            <div class="flex items-baseline gap-1">
              <span class="font-body-md text-body-md text-on-surface font-semibold">实付款：</span>
              <span class="font-price-currency text-price-currency text-tertiary font-bold">¥</span>
              <span class="font-display-hero text-display-hero text-tertiary font-extrabold leading-none tracking-tight">{{ totalWithFee.toFixed(2) }}</span>
            </div>
            <div class="flex items-center gap-1 text-on-surface-variant">
              <span class="font-body-sm text-body-sm">已优惠 ¥15.00</span>
              <span class="material-symbols-outlined text-[14px]">info</span>
            </div>
          </div>
          <!-- Action Button -->
          <button class="h-11 px-7 rounded-full bg-primary-container text-on-primary font-headline-sm text-headline-sm font-bold shadow-md hover:opacity-95 active:scale-[0.98] transition-all flex items-center justify-center gap-1.5 cursor-pointer" id="btnSubmitOrder" @click="submitOrder" :disabled="submitting" type="button">
            <span id="btnText" class="">{{ submitting ? '提交中...' : '提交订单' }}</span>
            <span class="material-symbols-outlined animate-spin text-[18px]" :class="{ hidden: !submitting }" id="btnSpinner">progress_activity</span>
          </button>
        </div>
      </aside>
    </div>

    <!-- 空态 -->
    <div v-else class="flex-1 flex flex-col items-center justify-center px-margin gap-space-md pt-24 pb-28">
      <div class="w-16 h-16 rounded-full bg-surface-container flex items-center justify-center text-outline">
        <span class="material-symbols-outlined text-[32px]">shopping_cart</span>
      </div>
      <p class="font-body-md text-body-md text-on-surface-variant">购物车中暂无可结算商品</p>
      <button class="h-11 px-7 rounded-full bg-primary-container text-on-primary font-headline-sm text-headline-sm font-bold shadow-md hover:opacity-95 active:scale-[0.98] transition-all cursor-pointer" @click="$router.push('/')" type="button">去选购</button>
    </div>

    <!-- 送达时间选择浮层（保留原有逻辑） -->
    <van-popup v-model:show="showTimePicker" position="bottom" round teleport="body" :z-index="3000" :overlay-style="{ zIndex: 2999 }">
      <van-date-picker
        v-model="tempDate"
        title="选择日期"
        :min-date="minDate"
        @confirm="onDateConfirm"
        @cancel="showTimePicker = false"
      />
    </van-popup>

    <!-- 优惠券选择浮层（保留原有逻辑） -->
    <van-popup v-model:show="showCouponPopup" position="bottom" round teleport="body" :z-index="3000" :overlay-style="{ zIndex: 2999 }" style="max-height: 60vh;">
      <div class="p-space-lg flex flex-col">
        <div class="flex items-center justify-between mb-space-md">
          <span class="font-headline-sm text-headline-sm text-on-surface font-bold">选择优惠券</span>
          <span class="material-symbols-outlined text-outline cursor-pointer" @click="showCouponPopup = false">close</span>
        </div>
        <div class="flex flex-col gap-space-sm overflow-y-auto" style="max-height: 50vh;">
          <div class="px-space-md py-space-sm rounded-lg border text-center font-body-md text-body-md cursor-pointer" :class="!selectedCoupon ? 'border-primary-container text-primary bg-primary-fixed/40' : 'border-surface-container-highest text-on-surface-variant'" @click="selectCoupon(null)">不使用优惠券</div>
          <div
            v-for="c in usableCoupons"
            :key="c.id"
            class="flex items-center justify-between px-space-md py-space-sm rounded-lg border cursor-pointer"
            :class="selectedCoupon?.id === c.id ? 'border-primary-container bg-primary-fixed/40' : 'border-surface-container-highest'"
            @click="selectCoupon(c)"
          >
            <div class="flex flex-col">
              <span class="font-headline-sm text-headline-sm text-on-surface font-bold">{{ c.couponName }}</span>
              <span class="font-body-sm text-body-sm text-on-surface-variant">{{ formatCouponDesc(c) }}</span>
            </div>
            <span class="material-symbols-outlined text-primary" v-if="selectedCoupon?.id === c.id">check_circle</span>
          </div>
          <div v-if="!usableCoupons.length" class="text-center py-space-lg font-body-md text-body-md text-outline">暂无可用优惠券</div>
        </div>
      </div>
    </van-popup>
  </main>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { createCustomerOrder, getAddressList, getCustomerCoupons } from '@/api'
import { showToast } from 'vant'

const router = useRouter()
const cartStore = useCartStore()
const remark = ref('')
const submitting = ref(false)
const deliveryType = ref('pickup')
const address = ref('')
const phone = ref('')
const addressName = ref('')
const deliveryTime = ref('')
const addressList = ref([])
const selectedAddressId = ref(0)
const showTimePicker = ref(false)
const deliveryFee = 5

// 优惠券相关
const couponList = ref([])
const selectedCoupon = ref(null)
const showCouponPopup = ref(false)

const usableCoupons = computed(() => {
  const total = cartStore.orderTotal
  return couponList.value.filter(c => {
    if (c.status !== 0) return false
    if (c.minAmount && total < Number(c.minAmount)) return false
    if (c.startTime && new Date(c.startTime) > new Date()) return false
    if (c.endTime && new Date(c.endTime) < new Date()) return false
    return true
  })
})

const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  const total = cartStore.orderTotal
  const c = selectedCoupon.value
  if (c.couponType === 2) {
    // 折扣券: couponValue 是折扣比例(如0.15表示减15%)
    const discount = total * Number(c.couponValue)
    return Math.min(total, discount)
  } else {
    // 满减/立减券
    const minAmount = Number(c.minAmount || 0)
    if (minAmount > 0 && total < minAmount) return 0
    return Math.min(total, Number(c.couponValue || 0))
  }
})

const now = new Date()
const minDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1)
const tempDate = ref([
  String(tomorrow.getFullYear()),
  String(tomorrow.getMonth() + 1).padStart(2, '0'),
  String(tomorrow.getDate()).padStart(2, '0')
])

const totalWithFee = computed(() => {
  const fee = deliveryType.value === 'delivery' ? deliveryFee : 0
  return Math.max(0, cartStore.orderTotal + fee - couponDiscount.value)
})

function formatPrice(price) { return Number(price || 0).toFixed(2) }

function formatCouponValue(val) {
  return Number(val || 0).toFixed(0)
}

function formatCouponDesc(c) {
  const min = Number(c.minAmount || 0)
  if (c.couponType === 2) return min > 0 ? `满${min}可用` : '无门槛'
  return min > 0 ? `满${min}减${formatCouponValue(c.couponValue)}` : `立减${formatCouponValue(c.couponValue)}元`
}

function selectCoupon(coupon) {
  selectedCoupon.value = coupon
  showCouponPopup.value = false
}

function formatAddress(addr) {
  return `${addr.province || ''}${addr.city || ''}${addr.district || ''}${addr.address || ''}`
}

function selectAddress(addr) {
  selectedAddressId.value = addr.id
  addressName.value = addr.name
  phone.value = addr.phone
  address.value = formatAddress(addr)
}

async function loadAddresses() {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
    // 自动选中默认地址
    const defaultAddr = addressList.value.find(a => a.isDefault === 1)
    if (defaultAddr) selectAddress(defaultAddr)
  } catch {}
}

function onDateConfirm({ selectedValues }) {
  deliveryTime.value = `${selectedValues[0]}-${selectedValues[1]}-${selectedValues[2]}`
  showTimePicker.value = false
}

async function submitOrder() {
  if (deliveryType.value === 'delivery') {
    if (!address.value.trim()) { showToast('请选择或输入收货地址'); return }
    if (!phone.value.trim()) { showToast('请输入联系电话'); return }
    if (!deliveryTime.value) { showToast('请选择送达时间'); return }
  }
  submitting.value = true
  try {
    const order = {
      remark: remark.value,
      deliveryType: deliveryType.value,
      address: deliveryType.value === 'delivery' ? address.value : '',
      phone: deliveryType.value === 'delivery' ? phone.value : '',
      deliveryTime: deliveryType.value === 'delivery' ? deliveryTime.value : '',
      couponId: selectedCoupon.value ? selectedCoupon.value.couponId : null,
      couponMemberId: selectedCoupon.value ? selectedCoupon.value.id : null,
      items: cartStore.orderItems.map(item => ({
        productId: item.productId, quantity: item.quantity
      }))
    }
    const res = await createCustomerOrder(order)
    if (cartStore.buyNowItems.length) {
      cartStore.clearBuyNowItem()
    } else {
      cartStore.removeChecked()
    }
    showToast('下单成功')
    router.replace(`/order/result/${res.data?.orderNo || Date.now()}`)
  } catch (e) {
    showToast(e.message || '下单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loadAddresses()
  // 加载可用优惠券
  try {
    const res = await getCustomerCoupons()
    couponList.value = res.data || []
  } catch {}
  if (cartStore.buyNowItems.length === 0 && cartStore.checkedItems.length === 0) {
    showToast('请先选择商品')
    router.replace('/cart')
  }
})
</script>
