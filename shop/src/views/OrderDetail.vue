<template>
  <!-- OrderDetail — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <template v-if="order">
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
          <img alt="Profile" class="w-8 h-8 rounded-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAbWRWRjaR21qK1jU0kCwRb_bAPV-hTUNuaZhWlx7qH_Va4V32egzB9EA-dyJjfPe9hnkJfPAuD8FSIvw03zd17tPdYx3K8XtSc9b3_YTnlSDYSwkgXgjlVRRjOPE3rL-UQNuJ74U9yW3VtEtg7VpupRBb7r9Y5r5wmAz-R2XiUVAipjvedop15RZTvklVj-vtaXe9VOD3ODfIai_ITvZaWs2hlEdS_VxLLC-YWL49tz4gKH6_IB6yQ" />
        </div>
      </div>
    </header>

    <main class="flex-1 flex flex-col relative w-full pt-14 pb-safe bg-background max-w-[540px] mx-auto min-h-screen">
      <div class="flex flex-col w-full relative pb-28">
        <!-- 状态横幅区 (仪式感深蓝渐变 + 履约手绘图标) -->
        <section class="bg-gradient-to-br from-primary via-primary-container to-[#004b98] text-on-primary px-margin pt-4 pb-12 relative overflow-hidden rounded-b-3xl shadow-sm">
          <!-- 背景流光装饰 -->
          <div class="absolute -right-8 -top-8 w-44 h-44 rounded-full bg-white/5 pointer-events-none blur-xl"></div>
          <div class="absolute left-1/3 -bottom-10 w-36 h-36 rounded-full bg-secondary-container/10 pointer-events-none blur-lg"></div>
          <div class="relative z-10 flex items-center justify-between gap-space-md">
            <div class="flex-1 min-w-0">
              <div class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full bg-white/15 backdrop-blur-md mb-2">
                <span class="w-1.5 h-1.5 rounded-full bg-secondary-fixed animate-ping"></span>
                <span class="font-tab-label text-tab-label text-secondary-fixed">极速仓履约中</span>
              </div>
              <h2 class="font-headline-lg text-headline-lg font-bold tracking-tight text-white mb-1.5 flex items-center gap-1.5">
                {{ statusMap[order.status] || '未知状态' }}
              </h2>
              <p class="font-body-sm text-body-sm text-primary-fixed/90 leading-relaxed pr-2">
                您的商品正由沃尔玛专属拣货员精选打包中
              </p>
            </div>
            <!-- 配套履约手绘/冷链运输插画 -->
            <div class="shrink-0 w-24 h-24 relative flex items-center justify-center">
              <div class="w-20 h-20 rounded-2xl bg-white/10 backdrop-blur-md flex items-center justify-center shadow-inner relative group">
                <span class="material-symbols-outlined text-[42px] text-secondary-fixed animate-pulse" style="font-variation-settings: 'FILL' 1;">local_shipping</span>
                <div class="absolute -top-1 -right-1 w-6 h-6 rounded-full bg-secondary-container text-on-secondary-container flex items-center justify-center shadow-sm">
                  <span class="material-symbols-outlined text-[14px]">inventory_2</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 浮动包裹主体内容 (重叠上方横幅) -->
        <div class="px-margin -mt-6 flex flex-col gap-3 relative z-20">
          <!-- 履约配送信息卡片 -->
          <section class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm">
            <div class="flex items-start gap-space-sm pb-space-sm">
              <div class="w-8 h-8 rounded-full bg-primary-fixed flex items-center justify-center shrink-0 mt-0.5 text-primary">
                <span class="material-symbols-outlined text-[20px]">electric_moped</span>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="font-headline-sm text-headline-sm font-bold text-on-surface">沃尔玛极速达</span>
                    <span class="px-1.5 py-0.2 rounded bg-primary-fixed text-primary font-badge-label text-badge-label">准时达</span>
                  </div>
                  <span class="font-body-sm text-body-sm font-bold text-primary">预计明日送达</span>
                </div>
                <p class="font-body-md text-body-md text-primary font-semibold mt-1">
                  明天 10:00 - 11:00
                </p>
              </div>
            </div>
            <div class="h-px bg-surface-container my-1"></div>
            <div class="flex items-start gap-space-sm pt-space-sm">
              <div class="w-8 h-8 rounded-full bg-surface-container flex items-center justify-center shrink-0 mt-0.5 text-outline">
                <span class="material-symbols-outlined text-[18px]">location_on</span>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-space-sm">
                  <span class="font-headline-sm text-headline-sm font-semibold text-on-surface">{{ order.name || '张三' }}</span>
                  <span class="font-body-sm text-body-sm text-on-surface-variant font-medium">{{ order.phone || '138****0000' }}</span>
                </div>
                <p class="font-body-sm text-body-sm text-on-surface-variant mt-1 leading-snug">
                  {{ order.address || '广东省深圳市南山区高新南一道沃尔玛总部大厦 6楼' }}
                </p>
              </div>
            </div>
          </section>

          <!-- 商品清单完整明细卡片 -->
          <section class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm">
            <div class="flex items-center justify-between pb-space-sm">
              <div class="flex items-center gap-1.5">
                <span class="w-1 h-3.5 bg-primary rounded-full"></span>
                <h3 class="font-headline-sm text-headline-sm font-bold text-on-surface">购买商品清单</h3>
                <span class="font-body-sm text-body-sm text-on-surface-variant">共 {{ (order.items || []).length }} 件</span>
              </div>
              <span class="px-2 py-0.5 rounded-full bg-surface-container text-on-surface-variant font-tab-label text-tab-label">温控保鲜配送</span>
            </div>
            <div class="flex flex-col gap-3.5 pt-1">
              <article v-for="item in (order.items || [])" :key="item.id" class="flex items-center gap-3">
                <div class="w-18 h-18 w-[72px] h-[72px] rounded-lg bg-surface-container-low shrink-0 overflow-hidden relative">
                  <img class="w-full h-full object-cover" data-alt="A premium clear clamshell box filled with fresh Japanese shine muscat green grapes, plump, glowing softly under bright studio retail lights, water droplets on glossy skin." :src="item.imageUrl || '/placeholder.svg'" />
                  <span class="absolute top-0 left-0 font-badge-label text-badge-label px-1 rounded-br" :class="item.tag === '沃集鲜' ? 'bg-secondary text-on-secondary' : 'bg-tertiary text-on-tertiary'">鲜甜优选</span>
                </div>
                <div class="flex-1 min-w-0 flex flex-col justify-between h-[72px] py-0.5">
                  <div>
                    <h4 class="font-body-md text-body-md font-semibold text-on-surface truncate">{{ item.productName }}</h4>
                    <p v-if="item.spec" class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">规格：{{ item.spec }}</p>
                  </div>
                  <div class="flex items-baseline justify-between mt-auto">
                    <span class="font-body-sm text-body-sm text-on-surface-variant">¥{{ formatPrice(item.price) }} <span class="text-xs">x{{ item.quantity }}</span></span>
                    <span class="font-headline-sm text-headline-sm font-bold text-on-surface">¥{{ formatPrice((item.price || 0) * (item.quantity || 0)) }}</span>
                  </div>
                </div>
              </article>
            </div>
          </section>

          <!-- 订单信息与费用结算卡片 -->
          <section class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-3">
            <!-- 费用汇总部分 -->
            <div class="flex items-center justify-between pb-1">
              <h3 class="font-headline-sm text-headline-sm font-bold text-on-surface">费用明细</h3>
              <span class="font-body-sm text-body-sm text-on-surface-variant">微信免密支付</span>
            </div>
            <div class="flex flex-col gap-2 text-body-md">
              <div class="flex items-center justify-between text-on-surface-variant">
                <span class="font-body-sm text-body-sm">商品总金额</span>
                <span class="font-body-sm text-body-sm font-semibold text-on-surface">¥{{ formatPrice(order.totalAmount) }}</span>
              </div>
              <div class="flex items-center justify-between text-on-surface-variant">
                <span class="font-body-sm text-body-sm">运费</span>
                <span class="font-body-sm text-body-sm font-semibold text-on-surface">¥5.00</span>
              </div>
              <div class="flex items-center justify-between text-tertiary">
                <span class="font-body-sm text-body-sm flex items-center gap-1">
                  <span class="material-symbols-outlined text-[16px]">local_activity</span>
                  运费券/新客优惠
                </span>
                <span class="font-body-sm text-body-sm font-bold">-¥5.00</span>
              </div>
              <div v-if="order.couponDiscount && Number(order.couponDiscount) > 0" class="flex items-center justify-between text-tertiary">
                <span class="font-body-sm text-body-sm flex items-center gap-1">
                  <span class="material-symbols-outlined text-[16px]">confirmation_number</span>
                  优惠券立减
                </span>
                <span class="font-body-sm text-body-sm font-bold">-¥{{ formatPrice(order.couponDiscount) }}</span>
              </div>
              <div class="h-px bg-surface-container my-1"></div>
              <div class="flex items-baseline justify-between pt-0.5">
                <span class="font-headline-sm text-headline-sm font-bold text-on-surface">实付款</span>
                <div class="flex items-baseline text-tertiary">
                  <span class="font-price-currency text-price-currency font-bold mr-0.5">¥</span>
                  <span class="font-display-hero text-[26px] leading-none font-extrabold text-tertiary">{{ formatPrice(order.actualAmount || order.totalAmount) }}</span>
                </div>
              </div>
            </div>
            <!-- 订单信息辅助明细 -->
            <div class="bg-surface-container-low rounded-lg p-3 flex flex-col gap-2 mt-1">
              <div class="flex items-center justify-between">
                <span class="font-body-sm text-body-sm text-on-surface-variant">订单编号</span>
                <div class="flex items-center gap-1.5">
                  <span class="font-body-sm text-body-sm font-mono text-on-surface" id="orderNoText">{{ order.orderNo }}</span>
                  <button class="w-6 h-6 rounded flex items-center justify-center bg-surface-container text-primary active:bg-primary-fixed transition-colors" title="复制订单号" type="button">
                    <span class="material-symbols-outlined text-[14px]">content_copy</span>
                  </button>
                </div>
              </div>
              <div class="flex items-center justify-between">
                <span class="font-body-sm text-body-sm text-on-surface-variant">下单时间</span>
                <span class="font-body-sm text-body-sm text-on-surface">{{ order.createTime }}</span>
              </div>
              <div class="flex items-center justify-between">
                <span class="font-body-sm text-body-sm text-on-surface-variant">支付方式</span>
                <span class="font-body-sm text-body-sm text-on-surface">微信免密支付</span>
              </div>
              <div v-if="order.remark" class="flex items-start justify-between gap-3 pt-1 border-t-0">
                <span class="font-body-sm text-body-sm text-on-surface-variant shrink-0">订单备注</span>
                <span class="font-body-sm text-body-sm text-on-surface text-right font-medium">{{ order.remark }}</span>
              </div>
            </div>
          </section>

          <!-- 暖心履约承诺 -->
          <div class="flex items-center justify-center gap-4 py-2 text-on-surface-variant/70 font-body-sm text-[11px]">
            <div class="flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px] text-primary">verified</span>
              <span class="">正品溯源保障</span>
            </div>
            <div class="flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px] text-primary">ac_unit</span>
              <span class="">全程冷链配送</span>
            </div>
            <div class="flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px] text-primary">sentiment_very_satisfied</span>
              <span class="">售后无忧退款</span>
            </div>
          </div>
        </div>

        <!-- 底部固定操作栏 (按订单状态分流原有业务函数) -->
        <aside class="fixed bottom-0 left-0 right-0 w-full z-40 bg-surface-container-lowest shadow-[0_-2px_10px_rgba(0,0,0,0.06)] pb-safe">
          <div class="max-w-[540px] mx-auto h-16 px-margin flex items-center justify-between gap-space-sm">
            <!-- 左侧：联系客服 -->
            <a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary active:scale-95 transition-all px-2" href="tel:4008888888">
              <span class="material-symbols-outlined text-[22px]">support_agent</span>
              <span class="font-tab-label text-tab-label mt-0.5">联系客服</span>
            </a>
            <!-- 右侧按钮群 -->
            <div class="flex items-center gap-2.5 flex-1 justify-end">
              <template v-if="order.status === 0">
                <button class="h-10 px-4 rounded-full bg-surface-container text-on-surface font-body-md text-body-md font-semibold hover:bg-surface-variant active:scale-95 transition-all" type="button" @click="handleCancel">
                  取消订单
                </button>
                <button class="h-10 px-6 rounded-full bg-primary text-on-primary font-body-md text-body-md font-bold shadow-sm shadow-primary/30 hover:bg-primary-container active:scale-95 transition-all flex items-center gap-1" type="button" @click="handlePay">
                  <span class="material-symbols-outlined text-[18px]">payments</span>
                  <span class="">去付款</span>
                </button>
              </template>
              <template v-else-if="order.status === 3">
                <button class="h-10 px-6 rounded-full bg-primary text-on-primary font-body-md text-body-md font-bold shadow-sm shadow-primary/30 hover:bg-primary-container active:scale-95 transition-all flex items-center gap-1" type="button" @click="handleConfirm">
                  <span class="material-symbols-outlined text-[18px]">check_circle</span>
                  <span class="">确认收货</span>
                </button>
              </template>
              <template v-else>
                <button class="h-10 px-6 rounded-full bg-primary text-on-primary font-body-md text-body-md font-bold shadow-sm shadow-primary/30 hover:bg-primary-container active:scale-95 transition-all flex items-center gap-1" type="button" @click="$router.push('/')">
                  <span class="material-symbols-outlined text-[18px]">replay</span>
                  <span class="">再来一单</span>
                </button>
              </template>
            </div>
          </div>
        </aside>
        <!-- 交互反馈 Toast 容器 -->
        <div class="fixed top-20 left-1/2 -translate-x-1/2 z-50 bg-inverse-surface/90 text-inverse-on-surface px-4 py-2 rounded-full font-body-sm text-body-sm shadow-lg pointer-events-none opacity-0 transition-opacity duration-300 flex items-center gap-1.5" id="toastMessage">
          <span class="material-symbols-outlined text-[16px] text-secondary-fixed" id="toastIcon">check_circle</span>
          <span id="toastContent" class="">复制成功</span>
        </div>
      </div>
    </main>
  </template>

  <!-- 极简加载态 (设计稿配色) -->
  <div v-else class="min-h-screen flex flex-col items-center justify-center bg-background gap-3">
    <span class="material-symbols-outlined text-[32px] text-primary animate-pulse">progress_activity</span>
    <span class="font-body-sm text-body-sm text-on-surface-variant">加载中…</span>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCustomerOrderDetail, payOrder, cancelOrder, confirmOrder } from '@/api'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const order = ref(null)

const statusMap = { 0: '待付款', 1: '待发货', 2: '待发货', 3: '待收货', 4: '已完成', 5: '已取消' }

function formatPrice(price) { return Number(price || 0).toFixed(2) }

async function loadOrder() {
  try {
    const res = await getCustomerOrderDetail(route.params.orderNo)
    order.value = res.data
  } catch (e) {
    showToast('订单不存在')
    router.back()
  }
}

async function handlePay() {
  try { await payOrder(order.value.orderNo); showToast('付款成功'); loadOrder() }
  catch (e) { showToast(e.message || '付款失败') }
}

async function handleCancel() {
  try { await cancelOrder(order.value.orderNo); showToast('已取消'); loadOrder() }
  catch (e) { showToast(e.message || '取消失败') }
}

async function handleConfirm() {
  try { await confirmOrder(order.value.orderNo); showToast('已确认收货'); loadOrder() }
  catch (e) { showToast(e.message || '确认失败') }
}

onMounted(loadOrder)
</script>
