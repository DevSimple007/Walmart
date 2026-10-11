<template>
  <!-- OrderList — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
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

  <main class="flex-1 flex flex-col relative w-full pt-14 pb-safe bg-background max-w-[540px] mx-auto min-h-screen">
    <div class="flex flex-col w-full pb-12">
      <!-- Top App Navigation Bar -->
      <section class="sticky top-0 z-40 bg-surface-container-lowest shadow-sm flex flex-col w-full">
        <div class="h-12 px-margin flex items-center justify-between">
          <div class="flex items-center gap-space-xs">
            <button aria-label="返回" class="w-9 h-9 flex items-center justify-center rounded-full text-on-surface hover:bg-surface-container active:scale-95 transition-transform" @click="$router.back()">
              <span class="material-symbols-outlined text-[22px]">arrow_back_ios_new</span>
            </button>
            <span class="font-headline-sm text-headline-sm text-on-surface font-bold">我的订单</span>
          </div>
          <button aria-label="搜索订单" class="w-9 h-9 flex items-center justify-center rounded-full text-on-surface-variant hover:bg-surface-container active:scale-95 transition-transform">
            <span class="material-symbols-outlined text-[22px]">search</span>
          </button>
        </div>
        <!-- Status Tabs -->
        <div class="flex items-center justify-between px-margin overflow-x-auto no-scrollbar bg-surface-container-lowest">
          <!-- 全部 -->
          <button
            :class="activeTab === -1 ? 'flex flex-col items-center py-2.5 px-1 relative text-primary-container font-bold' : 'flex flex-col items-center py-2.5 px-1 relative transition-colors text-on-surface-variant hover:text-on-surface'"
            @click="activeTab = -1"
          >
            <span :class="activeTab === -1 ? 'font-tab-label text-tab-label whitespace-nowrap text-primary' : 'font-tab-label text-tab-label whitespace-nowrap'">全部</span>
            <span :class="activeTab === -1 ? 'w-6 h-0.5 rounded-full bg-primary mt-1' : 'w-4 h-0.5 rounded-full bg-transparent mt-1'"></span>
          </button>
          <!-- 待付款 -->
          <button
            :class="activeTab === 0 ? 'flex flex-col items-center py-2.5 px-1 relative text-primary-container font-bold' : 'flex flex-col items-center py-2.5 px-1 relative transition-colors text-on-surface-variant hover:text-on-surface'"
            @click="activeTab = 0"
          >
            <span :class="activeTab === 0 ? 'font-tab-label text-tab-label whitespace-nowrap flex items-center gap-0.5 text-primary' : 'font-tab-label text-tab-label whitespace-nowrap flex items-center gap-0.5'">
              待付款 <span v-if="false" class="text-[10px] px-1 rounded-full bg-error-container text-on-error-container font-bold leading-tight">1</span>
            </span>
            <span :class="activeTab === 0 ? 'w-6 h-0.5 rounded-full bg-primary mt-1' : 'w-4 h-0.5 rounded-full bg-transparent mt-1'"></span>
          </button>
          <!-- 待发货 -->
          <button
            :class="activeTab === 1 ? 'flex flex-col items-center py-2.5 px-1 relative text-primary-container font-bold' : 'flex flex-col items-center py-2.5 px-1 relative transition-colors text-on-surface-variant hover:text-on-surface'"
            @click="activeTab = 1"
          >
            <span :class="activeTab === 1 ? 'font-tab-label text-tab-label whitespace-nowrap flex items-center gap-0.5 text-primary' : 'font-tab-label text-tab-label whitespace-nowrap flex items-center gap-0.5'">
              待发货 <span v-if="false" class="text-[10px] px-1 rounded-full bg-primary-fixed text-on-primary-fixed-variant font-bold leading-tight">1</span>
            </span>
            <span :class="activeTab === 1 ? 'w-6 h-0.5 rounded-full bg-primary mt-1' : 'w-4 h-0.5 rounded-full bg-transparent mt-1'"></span>
          </button>
          <!-- 待收货 -->
          <button
            :class="activeTab === 3 ? 'flex flex-col items-center py-2.5 px-1 relative text-primary-container font-bold' : 'flex flex-col items-center py-2.5 px-1 relative transition-colors text-on-surface-variant hover:text-on-surface'"
            @click="activeTab = 3"
          >
            <span :class="activeTab === 3 ? 'font-tab-label text-tab-label whitespace-nowrap flex items-center gap-0.5 text-primary' : 'font-tab-label text-tab-label whitespace-nowrap flex items-center gap-0.5'">
              待收货 <span v-if="false" class="text-[10px] px-1 rounded-full bg-surface-container-high text-on-surface-variant font-bold leading-tight">2</span>
            </span>
            <span :class="activeTab === 3 ? 'w-6 h-0.5 rounded-full bg-primary mt-1' : 'w-4 h-0.5 rounded-full bg-transparent mt-1'"></span>
          </button>
          <!-- 已完成 -->
          <button
            :class="activeTab === 4 ? 'flex flex-col items-center py-2.5 px-1 relative text-primary-container font-bold' : 'flex flex-col items-center py-2.5 px-1 relative transition-colors text-on-surface-variant hover:text-on-surface'"
            @click="activeTab = 4"
          >
            <span :class="activeTab === 4 ? 'font-tab-label text-tab-label whitespace-nowrap text-primary' : 'font-tab-label text-tab-label whitespace-nowrap'">已完成</span>
            <span :class="activeTab === 4 ? 'w-6 h-0.5 rounded-full bg-primary mt-1' : 'w-4 h-0.5 rounded-full bg-transparent mt-1'"></span>
          </button>
        </div>
      </section>

      <!-- Notice Banner for Pending Shipments -->
      <div class="px-margin pt-space-md">
        <div class="flex items-center gap-space-xs bg-primary-fixed/40 px-3 py-2 rounded-xl text-on-primary-fixed">
          <span class="material-symbols-outlined text-[18px] text-primary" style="font-variation-settings: 'FILL' 1;">local_shipping</span>
          <span class="font-body-sm text-body-sm flex-1 truncate font-medium">沃尔玛极速鲜达：智能仓配团队正在高效作业</span>
          <span class="material-symbols-outlined text-[16px] text-on-surface-variant">chevron_right</span>
        </div>
      </div>

      <!-- Order Cards Feed -->
      <div class="flex flex-col gap-space-md px-margin pt-space-md">
        <template v-for="order in orders" :key="order.id">
          <!-- Card 1: 待付款 -->
          <article
            v-if="order.status === 0"
            class="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden flex flex-col"
            @click="$router.push('/order/detail/' + order.orderNo)"
          >
            <!-- Card Header -->
            <div class="flex items-center justify-between p-3.5 bg-surface-container-low/40">
              <div class="flex items-center gap-1.5 min-w-0">
                <span class="material-symbols-outlined text-[18px] text-primary" style="font-variation-settings: 'FILL' 1;">storefront</span>
                <span class="font-body-md text-body-md font-bold text-on-surface truncate">沃尔玛自营门店</span>
                <span class="font-body-sm text-body-sm text-outline truncate text-[11px]">{{ order.orderNo }}</span>
              </div>
              <div class="flex items-center gap-1 shrink-0 text-secondary">
                <span class="material-symbols-outlined text-[15px] animate-pulse">timer</span>
                <span class="font-tab-label text-tab-label font-bold">{{ statusMap[order.status] || '未知' }} ({{ order.createTime }})</span>
              </div>
            </div>
            <!-- Card Body: Thumbnails -->
            <div class="p-3.5 flex items-center justify-between gap-space-sm">
              <div class="flex items-center gap-space-sm overflow-hidden">
                <div
                  v-for="item in (order.items || [])"
                  :key="item.id || item.productId"
                  class="relative w-[70px] h-[70px] rounded-lg bg-surface-container-low shrink-0 overflow-hidden"
                >
                  <img class="w-full h-full object-cover" data-alt="Fresh Atlantic salmon fillet raw cut on clean ice display with fresh lemon slice garnish, bright grocery studio shot with cool clean lighting" :src="item.imageUrl || '/placeholder.svg'" />
                  <span class="absolute bottom-0 right-0 bg-inverse-surface/75 text-inverse-on-surface font-badge-label text-badge-label px-1 rounded-tl-md">x{{ item.quantity }}</span>
                </div>
                </div>
              <div class="flex flex-col items-end shrink-0 pl-space-xs text-right">
                <span class="font-body-sm text-body-sm text-on-surface-variant">共 {{ (order.items || []).length }} 件</span>
                <span class="material-symbols-outlined text-outline text-[18px] mt-1">navigate_next</span>
              </div>
            </div>
            <!-- Card Amount Summary -->
            <div class="px-3.5 pb-3 flex items-center justify-end gap-1.5">
              <span class="font-body-sm text-body-sm text-on-surface-variant">需付款</span>
              <div class="flex items-baseline text-tertiary">
                <span class="font-price-currency text-price-currency font-bold">¥</span>
                <span class="font-price-lg text-price-lg font-extrabold tracking-tight">{{ formatPrice(order.actualAmount || order.totalAmount) }}</span>
              </div>
              <span class="font-body-sm text-body-sm text-outline text-[11px]">(含运费 ¥0.00)</span>
            </div>
            <!-- Card Actions -->
            <div class="flex items-center justify-end gap-space-sm px-3.5 py-3 bg-surface-container-lowest">
              <button class="h-9 px-4 rounded-full bg-surface-container text-on-surface font-body-sm text-body-sm font-semibold active:scale-95 transition-transform">
                取消订单
              </button>
              <button class="h-9 px-5 rounded-full bg-secondary-container text-on-secondary-container font-body-sm text-body-sm font-bold shadow-sm active:scale-95 transition-transform flex items-center gap-1">
                <span class="">立即付款</span>
                <span class="material-symbols-outlined text-[16px]">chevron_right</span>
              </button>
            </div>
          </article>

          <!-- Card 2: 待发货 -->
          <article
            v-else-if="order.status === 1 || order.status === 2"
            class="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden flex flex-col"
            @click="$router.push('/order/detail/' + order.orderNo)"
          >
            <!-- Card Header -->
            <div class="flex items-center justify-between p-3.5 bg-surface-container-low/40">
              <div class="flex items-center gap-1.5 min-w-0">
                <span class="material-symbols-outlined text-[18px] text-primary" style="font-variation-settings: 'FILL' 1;">eco</span>
                <span class="font-body-md text-body-md font-bold text-on-surface truncate">沃尔玛自营生鲜超市</span>
                <span class="font-body-sm text-body-sm text-outline truncate text-[11px]">{{ order.orderNo }}</span>
              </div>
              <div class="flex items-center gap-1 shrink-0 text-primary">
                <span class="material-symbols-outlined text-[16px] animate-spin">sync</span>
                <span class="font-tab-label text-tab-label font-bold">{{ statusMap[order.status] || '未知' }}（仓库拣货中）</span>
              </div>
            </div>
            <!-- Card Detailed Item Rows -->
            <div class="p-3.5 flex flex-col gap-space-sm">
              <div
                v-for="item in (order.items || [])"
                :key="item.id || item.productId"
                class="flex items-center justify-between gap-space-sm"
              >
                <div class="flex items-center gap-space-sm min-w-0">
                  <div class="w-14 h-14 rounded-lg bg-surface-container-low shrink-0 overflow-hidden">
                    <img class="w-full h-full object-cover" data-alt="High-end Shine Muscat green grapes with radiant emerald translucent skin, placed on pristine clean studio surface with soft natural lighting" :src="item.imageUrl || '/placeholder.svg'" />
                  </div>
                  <div class="flex flex-col min-w-0">
                    <span class="font-body-md text-body-md font-medium text-on-surface truncate">{{ item.productName || item.name }}</span>
                    <span class="font-body-sm text-body-sm text-outline">数量: {{ item.quantity }}</span>
                  </div>
                </div>
                <span class="font-body-sm text-body-sm font-bold text-on-surface shrink-0">¥{{ formatPrice(item.price) }}</span>
              </div>
              </div>
            <!-- Card Amount Summary -->
            <div class="px-3.5 pb-3 flex items-center justify-between">
              <span class="font-body-sm text-body-sm text-on-surface-variant">共 {{ (order.items || []).length }} 种商品</span>
              <div class="flex items-baseline gap-1">
                <span class="font-body-sm text-body-sm text-on-surface-variant">实付款</span>
                <div class="flex items-baseline text-on-surface font-extrabold">
                  <span class="font-price-currency text-price-currency">¥</span>
                  <span class="font-price-lg text-price-lg">{{ formatPrice(order.actualAmount || order.totalAmount) }}</span>
                </div>
              </div>
            </div>
            <!-- Progress Track Accent -->
            <div class="mx-3.5 mb-3 p-2.5 rounded-lg bg-surface-container-low flex items-center gap-2">
              <div class="w-6 h-6 rounded-full bg-primary flex items-center justify-center text-on-primary shrink-0">
                <span class="material-symbols-outlined text-[14px]">inventory_2</span>
              </div>
              <div class="flex flex-col min-w-0 flex-1">
                <span class="font-tab-label text-tab-label text-on-surface font-bold truncate">智能分拣中</span>
                <span class="font-body-sm text-body-sm text-outline text-[11px] truncate">预计今日 18:30 前完成打包发出</span>
              </div>
            </div>
            <!-- Card Actions -->
            <div class="flex items-center justify-end gap-space-sm px-3.5 py-3 bg-surface-container-lowest">
              <button class="h-9 px-4 rounded-full bg-surface-container text-on-surface font-body-sm text-body-sm font-semibold active:scale-95 transition-transform">
                修改地址
              </button>
              <button class="h-9 px-4 rounded-full bg-primary-fixed text-on-primary-fixed-variant font-body-sm text-body-sm font-bold active:scale-95 transition-transform flex items-center gap-1" id="remindShipmentBtn">
                <span class="material-symbols-outlined text-[16px] text-primary">notifications_active</span>
                <span class="">提醒发货</span>
              </button>
            </div>
          </article>

          <!-- Card 3: 待收货/配送中 -->
          <article
            v-else
            class="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden flex flex-col"
            @click="$router.push('/order/detail/' + order.orderNo)"
          >
            <!-- Card Header -->
            <div class="flex items-center justify-between p-3.5 bg-surface-container-low/40">
              <div class="flex items-center gap-1.5 min-w-0">
                <span class="material-symbols-outlined text-[18px] text-primary" style="font-variation-settings: 'FILL' 1;">local_mall</span>
                <span class="font-body-md text-body-md font-bold text-on-surface truncate">沃尔玛闪电送</span>
                <span class="font-body-sm text-body-sm text-outline truncate text-[11px]">{{ order.orderNo }}</span>
              </div>
              <div class="flex items-center gap-1 shrink-0 text-secondary-fixed-dim">
                <span class="material-symbols-outlined text-[16px]">two_wheeler</span>
                <span class="font-tab-label text-tab-label font-bold text-on-secondary-container">{{ statusMap[order.status] || '未知' }}（骑手正在配送）</span>
              </div>
            </div>
            <!-- Courier Status Teaser -->
            <div class="mx-3.5 mt-3 p-3 rounded-xl bg-secondary-fixed/30 flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-secondary-container flex items-center justify-center text-on-secondary-container shrink-0">
                <span class="material-symbols-outlined text-[20px]">sports_motorsports</span>
              </div>
              <div class="flex flex-col min-w-0 flex-1">
                <div class="flex items-center justify-between">
                  <span class="font-body-md text-body-md font-bold text-on-secondary-fixed">达达快送 骑手李师傅</span>
                  <span class="font-badge-label text-badge-label text-primary font-bold">距离您 420m</span>
                </div>
                <span class="font-body-sm text-body-sm text-on-surface-variant truncate">已从前置仓出发，准时达保障中</span>
              </div>
            </div>
            <!-- Thumbnail Preview List -->
            <div class="p-3.5 flex items-center justify-between gap-space-sm">
              <div class="flex items-center gap-space-sm overflow-hidden">
                <div
                  v-for="item in (order.items || [])"
                  :key="item.id || item.productId"
                  class="relative w-[64px] h-[64px] rounded-lg bg-surface-container-low shrink-0 overflow-hidden"
                >
                  <img class="w-full h-full object-cover" data-alt="Fresh sourdough bakery bread loaf sliced on wooden rustic board with flour dust, natural warm morning bakery aesthetic" :src="item.imageUrl || '/placeholder.svg'" />
                </div>
                </div>
              <div class="flex flex-col items-end shrink-0 pl-space-xs text-right">
                <span class="font-body-sm text-body-sm text-on-surface-variant">共 {{ (order.items || []).length }} 件</span>
                <span class="material-symbols-outlined text-outline text-[18px] mt-1">navigate_next</span>
              </div>
            </div>
            <!-- Card Actions -->
            <div class="flex items-center justify-end gap-space-sm px-3.5 py-3 bg-surface-container-lowest">
              <button class="h-9 px-4 rounded-full bg-surface-container text-on-surface font-body-sm text-body-sm font-semibold active:scale-95 transition-transform flex items-center gap-1">
                <span class="material-symbols-outlined text-[16px] text-outline">location_on</span>
                <span class="">查看物流</span>
              </button>
              <button class="h-9 px-4 rounded-full bg-primary-container text-on-primary font-body-sm text-body-sm font-bold shadow-sm active:scale-95 transition-transform">
                确认收货
              </button>
            </div>
          </article>
        </template>

        <!-- 空态 -->
        <div
          v-if="!orders.length && !loading"
          class="bg-surface-container-lowest rounded-xl shadow-sm flex flex-col items-center justify-center py-12 gap-space-xs"
        >
          <span class="material-symbols-outlined text-[40px] text-outline">inbox</span>
          <span class="font-body-md text-body-md text-on-surface-variant">暂无订单</span>
        </div>
      </div>
      <!-- Micro Interaction Toast Script -->
      <div class="fixed bottom-16 left-1/2 -translate-x-1/2 bg-inverse-surface text-inverse-on-surface px-4 py-2 rounded-full font-body-sm text-body-sm shadow-xl flex items-center gap-2 pointer-events-none transition-all duration-300 opacity-0 translate-y-3 z-50" id="remindToast">
        <span class="material-symbols-outlined text-[18px] text-secondary-fixed">check_circle</span>
        <span class="">已成功提醒仓库优先拣货！</span>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getCustomerOrders } from '@/api'

const route = useRoute()
const orders = ref([])
const loading = ref(true)
const activeTab = ref(route.query.status ? Number(route.query.status) : -1)

const statusMap = {
  0: '待付款', 1: '待发货', 2: '待发货', 3: '待收货', 4: '已完成', 5: '已取消'
}

function formatPrice(v) { return Number(v || 0).toFixed(2) }

async function loadOrders() {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 20 }
    if (activeTab.value !== -1) params.status = activeTab.value
    const res = await getCustomerOrders(params)
    orders.value = res.data?.records || res.page?.list || []
  } catch (e) {
    orders.value = []
  } finally {
    loading.value = false
  }
}

watch(activeTab, loadOrders)
onMounted(loadOrders)
</script>
