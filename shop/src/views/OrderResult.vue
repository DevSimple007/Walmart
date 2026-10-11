<template>
  <!-- OrderResult — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
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
    <div class="flex flex-col w-full pb-10">
      <div class="flex flex-col items-center justify-center px-margin pt-6 pb-5 text-center relative overflow-hidden">
        <div class="relative flex items-center justify-center mb-3">
          <div class="absolute w-20 h-20 rounded-full bg-[#00A862]/15 blur-lg animate-pulse">
          </div>
          <div class="relative w-16 h-16 rounded-full bg-[#00A862] shadow-[0_4px_16px_rgba(0,168,98,0.35)] flex items-center justify-center text-on-primary">
            <span class="material-symbols-outlined text-[36px]" style="font-variation-settings: 'FILL' 1, 'wght' 700;">check</span>
          </div>
        </div>
        <h2 class="font-headline-lg text-headline-lg text-on-surface tracking-tight font-extrabold mb-1.5">
          支付及下单成功！
        </h2>
        <p class="font-body-md text-body-md text-on-surface-variant max-w-[300px] leading-relaxed">
          您的订单已提交仓库，拣货人员正在加急为您打包
        </p>
        <div class="mt-4 inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-secondary-fixed text-on-secondary-fixed font-badge-label text-badge-label font-bold">
          <span class="material-symbols-outlined text-[15px]" style="font-variation-settings: 'FILL' 1;">bolt</span>
          <span class="">极速拣货通道生效中</span>
        </div>
      </div>
      <div class="px-margin flex flex-col gap-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-lg shadow-[0_2px_12px_rgba(0,30,80,0.05)] flex flex-col gap-3.5">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-1.5">
              <span class="font-body-sm text-body-sm text-on-surface-variant">订单编号</span>
              <span class="font-body-md text-body-md font-semibold text-on-surface select-all tracking-tight" id="orderNoText">{{ orderNo }}</span>
            </div>
            <button class="flex items-center gap-1 px-2.5 py-1 rounded-full bg-surface-container hover:bg-surface-container-high transition-colors active:scale-95 text-primary text-body-sm font-semibold" id="copyBtn" @click="copyOrderNo">
              <span class="material-symbols-outlined text-[14px]">content_copy</span>
              <span id="copyLabel" class="">复制</span>
            </button>
          </div>
          <div class="h-[1px] w-full bg-surface-container">
          </div>
          <div class="flex items-baseline justify-between">
            <span class="font-body-sm text-body-sm text-on-surface-variant">支付金额</span>
            <div class="text-right">
              <div class="flex items-baseline justify-end text-tertiary">
                <span class="font-price-currency text-price-currency mr-0.5">¥</span>
                <span class="font-price-lg text-price-lg">80.69</span>
              </div>
              <span class="font-body-sm text-body-sm text-on-surface-variant block mt-0.5">微信免密支付已扣款</span>
            </div>
          </div>
          <div class="h-[1px] w-full bg-surface-container">
          </div>
          <div class="flex items-start justify-between gap-space-sm">
            <span class="font-body-sm text-body-sm text-on-surface-variant shrink-0 mt-0.5">配送方式</span>
            <div class="flex items-center gap-1 text-right">
              <span class="font-badge-label text-badge-label font-bold px-1.5 py-0.5 rounded bg-primary-fixed text-on-primary-fixed-variant">最快达</span>
              <span class="font-body-md text-body-md font-semibold text-on-surface">极速送货上门</span>
            </div>
          </div>
          <div class="flex items-start justify-between gap-space-sm">
            <span class="font-body-sm text-body-sm text-on-surface-variant shrink-0 mt-0.5">预计送达</span>
            <div class="text-right">
              <span class="font-body-md text-body-md font-bold text-primary">明天（10月02日）10:00 - 11:00</span>
            </div>
          </div>
          <div class="flex items-start justify-between gap-space-md">
            <span class="font-body-sm text-body-sm text-on-surface-variant shrink-0 mt-0.5">收货地址</span>
            <div class="text-right min-w-0">
              <p class="font-body-md text-body-md font-semibold text-on-surface leading-snug">
                广东省深圳市南山区高新南一道
              </p>
              <p class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">
                沃尔玛总部大厦 6楼
              </p>
            </div>
          </div>
        </div>
        <div class="bg-surface-container-low rounded-xl p-space-md flex items-start gap-space-md shadow-sm">
          <div class="w-9 h-9 rounded-full bg-primary-fixed flex items-center justify-center shrink-0 text-primary">
            <span class="material-symbols-outlined text-[20px]" style="font-variation-settings: 'FILL' 1;">verified_user</span>
          </div>
          <div class="flex flex-col justify-center min-w-0 flex-1">
            <div class="flex items-center gap-1.5">
              <span class="font-headline-sm text-headline-sm text-on-surface font-bold text-[14px]">冷链保鲜 · 无接触配送</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant mt-0.5 leading-tight">
              全程温控追溯，配送骑手规范佩戴口罩并定时整车消杀。
            </p>
          </div>
        </div>
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-[0_2px_8px_rgba(0,30,80,0.04)] flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-lg overflow-hidden bg-surface-container flex items-center justify-center shrink-0">
              <img class="w-full h-full object-cover" data-alt="Close up fresh premium organic milk box and ripe bananas packed neatly inside a crisp Walmart delivery paper grocery bag bathed in bright sunny kitchen morning light" src="/placeholder.svg" />
            </div>
            <div>
              <p class="font-body-md text-body-md font-bold text-on-surface">共 3 件品质生鲜商品</p>
              <p class="font-body-sm text-body-sm text-on-surface-variant">包含 鲜牛奶、进口香蕉 等</p>
            </div>
          </div>
          <span class="material-symbols-outlined text-outline-variant text-[20px]">chevron_right</span>
        </div>
        <div class="flex flex-col gap-space-sm pt-2">
          <a class="w-full h-12 rounded-full bg-primary-container hover:bg-primary transition-all active:scale-[0.98] shadow-md flex items-center justify-center gap-2 text-on-primary font-headline-sm text-headline-sm" href="javascript:;" @click="$router.push('/order/detail/' + orderNo)">
            <span class="">查看订单详情</span>
            <span class="material-symbols-outlined text-[18px]">arrow_forward</span>
          </a>
          <a class="w-full h-12 rounded-full bg-surface-container hover:bg-surface-container-high transition-all active:scale-[0.98] flex items-center justify-center text-on-surface font-headline-sm text-headline-sm" href="javascript:;" @click="$router.push('/')">
            <span class="">返回首页继续逛</span>
          </a>
        </div>
        <div class="flex items-center justify-center gap-6 pt-3 pb-2 text-on-surface-variant font-body-sm text-body-sm">
          <a class="flex items-center gap-1 hover:text-primary transition-colors" href="/support">
            <span class="material-symbols-outlined text-[16px]">headset_mic</span>
            <span class="">在线客服</span>
          </a>
          <span class="w-1 h-1 rounded-full bg-outline-variant">
          </span>
          <a class="flex items-center gap-1 hover:text-primary transition-colors" href="/faq">
            <span class="material-symbols-outlined text-[16px]">help_outline</span>
            <span class="">配送常见问题</span>
          </a>
        </div>
      </div>
      <div class="fixed bottom-12 left-1/2 -translate-x-1/2 bg-inverse-surface text-inverse-on-surface px-4 py-2 rounded-full font-body-sm text-body-sm shadow-xl flex items-center gap-1.5 opacity-0 pointer-events-none transition-opacity duration-200 z-50" id="toast">
        <span class="material-symbols-outlined text-[18px] text-secondary-fixed">check_circle</span>
        <span class="">订单编号已复制到剪贴板</span>
      </div>
    </div>
  </main>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { showToast } from 'vant'

const route = useRoute()
const orderNo = route.params.orderNo

function copyOrderNo() {
  const text = String(orderNo ?? '')
  if (navigator.clipboard?.writeText) {
    navigator.clipboard.writeText(text).then(() => showToast('订单编号已复制到剪贴板')).catch(() => showToast('复制失败'))
  } else {
    showToast('订单编号：' + text)
  }
}
</script>
