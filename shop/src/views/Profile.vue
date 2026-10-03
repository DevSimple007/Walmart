<template>
  <!-- Profile — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 px-margin flex items-center justify-between gap-space-md">
      <div class="flex items-center gap-space-sm shrink-0">
        <img alt="Walmart Logo" class="h-8 w-auto object-contain" src="https://lh3.googleusercontent.com/aida/AEtjO1WxbFcbyvQxjOWkbhkpIy_RXXWkvvU4r8XwbBzzs9Nco_MOan5kyMhQR-HNMCR0mRp1uF8RgvdxFCmQDKNrWLsQIREwC9bCIhANclidwFzjHDJdKYngvbC9SVhJUEFCNQKdAdgsR_kuBjHhBzu_6r_lIOSDxLPhDvxFeI1qc18Fk2Q5QU-DNPWkKJpBlkAaHNmUuGr1XuZnVwAPynaif0RJ1AMEB8zun78Qcq8acrYVeyXlIND6DoxdvQ" />
        <span class="font-headline-sm text-headline-sm text-on-surface tracking-tight font-bold">Profile</span>
      </div>
      <div class="flex items-center gap-space-sm shrink-0">
        <button aria-label="Search" class="w-11 h-11 flex items-center justify-center text-on-surface-variant hover:text-primary transition-colors" @click="$router.push('/search')">
          <span class="material-symbols-outlined text-[22px]">search</span>
        </button>
        <img alt="Profile" class="w-8 h-8 rounded-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAbWRWRjaR21qK1jU0kCwRb_bAPV-hTUNuaZhWlx7qH_Va4V32egzB9EA-dyJjfPe9hnkJfPAuD8FSIvw03zd17tPdYx3K8XtSc9b3_YTnlSDYSwkgXgjlVRRjOPE3rL-UQNuJ74U9yW3VtEtg7VpupRBb7r9Y5r5wmAz-R2XiUVAipjvedop15RZTvklVj-vtaXe9VOD3ODfIai_ITvZaWs2hlEdS_VxLLC-YWL49tz4gKH6_IB6yQ" />
      </div>
    </div>
  </header>

  <main class="flex-1 flex flex-col relative w-full pt-14 pb-20 bg-background max-w-[540px] mx-auto min-h-screen">
    <div class="flex flex-col w-full">
      <div class="px-margin pt-space-sm pb-space-lg flex flex-col gap-space-md">
        <!-- 1. Top User Status Card (Logged in) -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-md">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-space-md" @click="!userStore.userInfo && $router.push('/login')">
              <div class="relative shrink-0">
                <img alt="用户头像" class="w-14 h-14 rounded-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAbWRWRjaR21qK1jU0kCwRb_bAPV-hTUNuaZhWlx7qH_Va4V32egzB9EA-dyJjfPe9hnkJfPAuD8FSIvw03zd17tPdYx3K8XtSc9b3_YTnlSDYSwkgXgjlVRRjOPE3rL-UQNuJ74U9yW3VtEtg7VpupRBb7r9Y5r5wmAz-R2XiUVAipjvedop15RZTvklVj-vtaXe9VOD3ODfIai_ITvZaWs2hlEdS_VxLLC-YWL49tz4gKH6_IB6yQ" />
                <div class="absolute -bottom-1 -right-1 bg-secondary-container text-on-secondary-container rounded-full p-0.5 flex items-center justify-center shadow-sm">
                  <span class="material-symbols-outlined text-[14px]">stars</span>
                </div>
              </div>
              <div class="flex flex-col min-w-0">
                <div class="flex items-center gap-space-xs">
                  <span class="font-headline-sm text-headline-sm text-on-surface tracking-tight">{{ userStore.userInfo?.memberName || userStore.userInfo?.phone || '' }}</span>
                  <span class="inline-flex items-center gap-0.5 bg-secondary-fixed text-on-secondary-fixed font-badge-label text-badge-label px-1.5 py-0.5 rounded-full">
                    <span class="material-symbols-outlined text-[11px]" style="font-variation-settings: 'FILL' 1;">workspace_premium</span>
                    <span class="">{{ levelName }}</span>
                  </span>
                </div>
                <div class="flex items-center gap-space-xs mt-1 text-on-surface-variant font-body-sm text-body-sm">
                  <span class="">积分余额 <strong class="text-primary font-headline-sm text-[13px]">{{ userStore.userInfo?.points || 0 }}</strong>
                  </span>
                  <span class="text-outline-variant">•</span>
                  <span class="">{{ nextLevel ? ('距' + nextLevel.levelName + ' 差 ' + Math.max(0, nextLevel.minPoints - (userStore.userInfo?.points || 0)) + ' 分') : '已是最高等级' }}</span>
                </div>
              </div>
            </div>
            <button aria-label="会员详情" class="w-8 h-8 rounded-full bg-surface-container-low flex items-center justify-center text-on-surface-variant hover:text-primary transition-colors">
              <span class="material-symbols-outlined text-[18px]">chevron_right</span>
            </button>
          </div>
          <!-- Growth Progress Bar -->
          <div class="flex flex-col gap-1.5 bg-surface-container-low rounded-lg p-space-sm">
            <div class="flex justify-between items-center text-on-surface-variant font-badge-label text-badge-label">
              <span class="flex items-center gap-1 font-bold text-secondary">
                <span class="material-symbols-outlined text-[12px]">verified</span>
                当前金卡会员 {{ progressPercent }}%
              </span>
              <span v-if="nextLevel" class="text-on-surface-variant">目标：{{ nextLevel?.levelName }} ({{ nextLevel?.minPoints }}分)</span>
            </div>
            <div class="w-full bg-surface-container-highest rounded-full h-1.5 overflow-hidden">
              <div class="bg-gradient-to-r from-secondary-container to-secondary-fixed-dim h-full rounded-full transition-all duration-500" :style="{ width: progressPercent + '%' }">
              </div>
            </div>
          </div>
        </div>
        <!-- 2. Money Saver Card Banner (Orange-Red Gradient Banner) -->
        <div class="relative overflow-hidden rounded-xl bg-gradient-to-br from-[#FF6B00] via-[#FA4B00] to-[#E60012] text-on-tertiary p-space-md shadow-md flex flex-col gap-space-sm">
          <!-- Ambient Sparkle Decor -->
          <div class="absolute -right-6 -bottom-6 w-24 h-24 bg-white/10 rounded-full blur-xl pointer-events-none">
          </div>
          <!-- Header Row -->
          <div class="flex items-center justify-between z-10">
            <div class="flex items-baseline gap-space-xs">
              <span class="font-headline-sm text-headline-sm tracking-tight text-white font-extrabold flex items-center gap-1">
                <span class="material-symbols-outlined text-[18px] text-secondary-fixed">savings</span>
                省钱卡
              </span>
              <span class="font-body-sm text-body-sm text-white/90">每月可享12张券</span>
            </div>
            <button class="bg-white text-[#E60012] font-tab-label text-tab-label font-bold px-3 py-1 rounded-full shadow-sm flex items-center gap-0.5 hover:bg-surface-container-lowest active:scale-95 transition-all">
              <span class="">立即开通</span>
              <span class="material-symbols-outlined text-[13px] font-bold">chevron_right</span>
            </button>
          </div>
          <!-- Main Benefits & Coupon Mockups -->
          <div class="grid grid-cols-12 gap-space-xs items-center mt-1 z-10">
            <!-- Benefit Bullets -->
            <div class="col-span-5 flex flex-col gap-1 text-[11px] leading-tight text-white/95">
              <div class="flex items-center gap-1">
                <span class="material-symbols-outlined text-[13px] text-white">check_circle</span>
                <span class="truncate">小程序/H5可用</span>
              </div>
              <div class="flex items-center gap-1">
                <span class="material-symbols-outlined text-[13px] text-white">check_circle</span>
                <span class="truncate">9.9购月卡一单回本</span>
              </div>
              <div class="flex items-center gap-1">
                <span class="material-symbols-outlined text-[13px] text-white">check_circle</span>
                <span class="truncate">可叠加促销商品券</span>
              </div>
            </div>
            <!-- 3 Coupon Tickets -->
            <div class="col-span-7 grid grid-cols-3 gap-1">
              <!-- Coupon 1 -->
              <div class="bg-surface-container-lowest text-on-surface rounded-md p-1 flex flex-col items-center justify-center shadow-sm relative overflow-hidden">
                <div class="text-[#E60012] font-display-hero text-[15px] leading-none font-black flex items-baseline">
                  <span class="font-price-currency text-[10px]">¥</span>7
                </div>
                <span class="text-[9px] text-on-surface-variant scale-90 -mt-0.5 whitespace-nowrap">满89元</span>
              </div>
              <!-- Coupon 2 -->
              <div class="bg-surface-container-lowest text-on-surface rounded-md p-1 flex flex-col items-center justify-center shadow-sm relative overflow-hidden">
                <div class="text-[#E60012] font-display-hero text-[15px] leading-none font-black flex items-baseline">
                  <span class="font-price-currency text-[10px]">¥</span>10
                </div>
                <span class="text-[9px] text-on-surface-variant scale-90 -mt-0.5 whitespace-nowrap">满119元</span>
              </div>
              <!-- Coupon 3 -->
              <div class="bg-surface-container-lowest text-on-surface rounded-md p-1 flex flex-col items-center justify-center shadow-sm relative overflow-hidden">
                <div class="text-[#E60012] font-display-hero text-[15px] leading-none font-black flex items-baseline">
                  <span class="font-price-currency text-[10px]">¥</span>10
                </div>
                <span class="text-[9px] text-on-surface-variant scale-90 -mt-0.5 whitespace-nowrap">满119元</span>
              </div>
            </div>
          </div>
        </div>
        <!-- 3. My Orders Module -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-md">
          <div class="flex items-center justify-between">
            <h2 class="font-headline-sm text-headline-sm text-on-surface">我的订单</h2>
            <a class="flex items-center text-on-surface-variant hover:text-primary text-body-sm font-body-sm transition-colors" href="javascript:;" @click="$router.push('/order')">
              <span class="">全部订单</span>
              <span class="material-symbols-outlined text-[16px]">chevron_right</span>
            </a>
          </div>
          <!-- 5 Order Status Icons -->
          <div class="grid grid-cols-5 gap-1 pt-1">
            <!-- 待付款 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="goOrder(0)">
              <div class="relative w-10 h-10 rounded-full bg-surface-container-low flex items-center justify-center text-on-surface group-hover:bg-primary-fixed group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">account_balance_wallet</span>
                <span class="absolute -top-1 -right-1 bg-tertiary-container text-on-tertiary font-badge-label text-badge-label px-1.5 py-0.2 rounded-full min-w-[16px] h-4 flex items-center justify-center">1</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">待付款</span>
            </a>
            <!-- 待配送 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="goOrder(1)">
              <div class="relative w-10 h-10 rounded-full bg-surface-container-low flex items-center justify-center text-on-surface group-hover:bg-primary-fixed group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">local_shipping</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">待配送</span>
            </a>
            <!-- 待收货 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="goOrder(3)">
              <div class="relative w-10 h-10 rounded-full bg-surface-container-low flex items-center justify-center text-on-surface group-hover:bg-primary-fixed group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">inventory_2</span>
                <span class="absolute -top-1 -right-1 bg-tertiary-container text-on-tertiary font-badge-label text-badge-label px-1.5 py-0.2 rounded-full min-w-[16px] h-4 flex items-center justify-center">2</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">待收货</span>
            </a>
            <!-- 待评价 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="goOrder(4)">
              <div class="relative w-10 h-10 rounded-full bg-surface-container-low flex items-center justify-center text-on-surface group-hover:bg-primary-fixed group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">chat_bubble_outline</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">待评价</span>
            </a>
            <!-- 退货/售后 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;">
              <div class="relative w-10 h-10 rounded-full bg-surface-container-low flex items-center justify-center text-on-surface group-hover:bg-primary-fixed group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">currency_yen</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">退货/售后</span>
            </a>
          </div>
        </div>
        <!-- 4. Core Promotional Activities (4-Grid) -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm">
          <div class="grid grid-cols-4 gap-2">
            <!-- 抽试吃 -->
            <a class="flex flex-col items-center justify-center gap-1.5 relative group" href="javascript:;">
              <div class="relative">
                <div class="w-11 h-11 rounded-full bg-[#FFF1F0] flex items-center justify-center text-tertiary group-active:scale-95 transition-transform">
                  <span class="material-symbols-outlined text-[24px]">restaurant</span>
                </div>
                <span class="absolute -top-1.5 -right-2 bg-tertiary-container text-on-tertiary font-badge-label text-[9px] px-1 py-0.2 rounded-full whitespace-nowrap shadow-xs">领奖品</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">抽试吃</span>
            </a>
            <!-- 邀请有礼 -->
            <a class="flex flex-col items-center justify-center gap-1.5 relative group" href="javascript:;">
              <div class="relative">
                <div class="w-11 h-11 rounded-full bg-[#FFF7E6] flex items-center justify-center text-secondary group-active:scale-95 transition-transform">
                  <span class="material-symbols-outlined text-[24px]">redeem</span>
                </div>
                <span class="absolute -top-1.5 -right-2 bg-secondary text-on-secondary font-badge-label text-[9px] px-1 py-0.2 rounded-full whitespace-nowrap shadow-xs">抽茅台</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">邀请有礼</span>
            </a>
            <!-- 推广赚钱 -->
            <a class="flex flex-col items-center justify-center gap-1.5 relative group" href="javascript:;">
              <div class="relative">
                <div class="w-11 h-11 rounded-full bg-[#FFF2E8] flex items-center justify-center text-[#FA541C] group-active:scale-95 transition-transform">
                  <span class="material-symbols-outlined text-[24px]">monetization_on</span>
                </div>
                <span class="absolute -top-1.5 -right-2 bg-[#FA541C] text-white font-badge-label text-[9px] px-1 py-0.2 rounded-full whitespace-nowrap shadow-xs">返现</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">推广赚钱</span>
            </a>
            <!-- 购物卡 -->
            <a class="flex flex-col items-center justify-center gap-1.5 relative group" href="javascript:;">
              <div class="w-11 h-11 rounded-full bg-[#E6F7FF] flex items-center justify-center text-primary-container group-active:scale-95 transition-transform">
                <span class="material-symbols-outlined text-[24px]">credit_card</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">购物卡</span>
            </a>
          </div>
        </div>
        <!-- 5. Assets & Services Matrix (2 Rows x 5 Items) -->
        <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-md">
          <div class="flex items-center justify-between">
            <h3 class="font-headline-sm text-headline-sm text-on-surface">我的资产与服务</h3>
          </div>
          <!-- Row 1 -->
          <div class="grid grid-cols-5 gap-1">
            <!-- 简单报 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;">
              <div class="relative w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">feed</span>
                <span class="absolute -top-1 -right-1 bg-tertiary text-on-tertiary font-badge-label text-[9px] px-1 rounded-full scale-90">HOT</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">简单报</span>
            </a>
            <!-- 联系客服 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">support_agent</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">联系客服</span>
            </a>
            <!-- 优惠券 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="goPage('/coupon')">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">confirmation_number</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">优惠券</span>
              <span class="text-[10px] text-tertiary -mt-1 font-bold">3张可用</span>
            </a>
            <!-- 我的收藏 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="goPage('/favorites')">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">favorite_border</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">我的收藏</span>
              <span class="text-[10px] text-on-surface-variant -mt-1">12件</span>
            </a>
            <!-- 收货地址 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="goPage('/address')">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">location_on</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">收货地址</span>
              <span class="text-[10px] text-on-surface-variant -mt-1">管理</span>
            </a>
          </div>
          <!-- Row 2 -->
          <div class="grid grid-cols-5 gap-1 pt-1">
            <!-- 企业采购 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">domain</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">企业采购</span>
            </a>
            <!-- 公众号 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">forum</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">公众号</span>
            </a>
            <!-- 扫码加车 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">qr_code_scanner</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">扫码加车</span>
            </a>
            <!-- 顾客调研 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;">
              <div class="relative w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">assignment</span>
                <span class="absolute -top-1 -right-1 bg-tertiary-container text-on-tertiary font-badge-label text-[9px] px-1 rounded-full scale-90">有奖</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">顾客调研</span>
            </a>
            <!-- 关于我们 -->
            <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="showToast('功能开发中')">
              <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-[22px]">info</span>
              </div>
              <span class="font-tab-label text-tab-label text-on-surface">关于我们</span>
            </a>
          </div>
        </div>
        <!-- 6. Bottom Rewarded Survey Banner -->
        <div class="bg-gradient-to-r from-primary-fixed to-surface-container-lowest rounded-xl p-space-md shadow-sm flex items-center justify-between gap-space-sm relative overflow-hidden">
          <!-- Playful Mascot / Dialogue Cloud -->
          <div class="flex items-center gap-space-sm shrink-0">
            <div class="relative w-12 h-12 flex items-center justify-center">
              <div class="w-10 h-10 rounded-full bg-secondary-container flex items-center justify-center text-on-secondary-container shadow-sm">
                <span class="material-symbols-outlined text-[24px]">sentiment_very_satisfied</span>
              </div>
              <div class="absolute -top-1 -right-1 bg-primary text-on-primary rounded-full w-4 h-4 flex items-center justify-center text-[10px] font-bold">!</div>
            </div>
            <div class="flex flex-col">
              <span class="font-tab-label text-[11px] text-primary font-bold bg-white/80 px-2 py-0.5 rounded-full inline-block w-fit">今天我们做得怎么样？</span>
              <span class="font-headline-sm text-[13px] text-on-surface font-extrabold mt-1">参与顾客满意度调研</span>
            </div>
          </div>
          <!-- Action Button Pill -->
          <a class="shrink-0 bg-primary-container text-on-primary font-tab-label text-tab-label font-bold px-3.5 py-2 rounded-full shadow-sm flex items-center gap-1 active:scale-95 transition-transform" href="javascript:;">
            <span class="">赢取优惠券</span>
            <span class="material-symbols-outlined text-[14px]">arrow_forward</span>
          </a>
        </div>
        <!-- 退出登录（复用「我的资产与服务」卡片 + 条目 class，无新增视觉风格） -->
        <div v-if="userStore.userInfo" class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-md">
          <a class="flex flex-col items-center justify-center gap-1 group" href="javascript:;" @click="handleLogout">
            <div class="w-10 h-10 rounded-xl bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:text-primary transition-colors">
              <span class="material-symbols-outlined text-[22px]">logout</span>
            </div>
            <span class="font-tab-label text-tab-label text-on-surface">退出登录</span>
          </a>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { onMounted, h, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showConfirmDialog, showToast } from 'vant'
import { getMemberLevels } from '@/api'

const router = useRouter()
const userStore = useUserStore()

// 会员等级数据（从后端动态获取）
const levelList = ref([])

const levelColors = {
  1: '#86868B',
  2: '#C0C0C0',
  3: '#FFD700',
  4: '#E5E4E2',
  5: '#B9F2FF',
}

const levelMap = computed(() => {
  const map = {}
  for (const item of levelList.value) {
    map[item.id] = {
      name: item.levelName,
      color: levelColors[item.id] || '#86868B',
      minPoints: item.minPoints,
    }
  }
  return map
})

const levelName = computed(() => levelMap.value[userStore.userInfo?.levelId]?.name || '普通会员')
const levelColor = computed(() => levelMap.value[userStore.userInfo?.levelId]?.color || '#86868B')
const nextLevel = computed(() => {
  const currentId = userStore.userInfo?.levelId || 1
  const currentLevel = levelMap.value[currentId]
  if (!currentLevel) return null
  // 按 minPoints 排序，找第一个比当前等级 minPoints 大的等级
  const sorted = [...levelList.value].sort((a, b) => a.minPoints - b.minPoints)
  const currentIdx = sorted.findIndex(l => l.id === currentId)
  if (currentIdx === -1 || currentIdx >= sorted.length - 1) return null
  const next = sorted[currentIdx + 1]
  return { levelName: next.levelName, minPoints: next.minPoints }
})
const progressPercent = computed(() => {
  if (!nextLevel.value) return 100
  const points = userStore.userInfo?.points || 0
  const currentMin = levelMap.value[userStore.userInfo?.levelId]?.minPoints || 0
  const nextMin = nextLevel.value.minPoints
  if (nextMin <= currentMin || points >= nextMin) return 100
  return Math.min(100, Math.round(((points - currentMin) / (nextMin - currentMin)) * 100))
})

const orderItems = [
  { label: '待付款', status: 0, color: '#FF9500', bg: '#FFF4E5', iconSvg: () => [h('path', { d: 'M12 1v22M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6' })] },
  { label: '待发货', status: 1, color: '#0071DC', bg: '#EBF5FF', iconSvg: () => [h('rect', { x: 1, y: 3, width: 15, height: 13 }), h('polygon', { points: '16 8 20 8 23 11 23 16 16 16 16 8' }), h('circle', { cx: 5.5, cy: 18.5, r: 2.5 }), h('circle', { cx: 18.5, cy: 18.5, r: 2.5 })] },
  { label: '待收货', status: 3, color: '#34C759', bg: '#E8F8ED', iconSvg: () => [h('path', { d: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z' }), h('polyline', { points: '14 2 14 8 20 8' }), h('line', { x1: 16, y1: 13, x2: 8, y2: 13 }), h('line', { x1: 16, y1: 17, x2: 8, y2: 17 })] },
  { label: '已完成', status: 4, color: '#86868B', bg: '#F5F5F7', iconSvg: () => [h('path', { d: 'M22 11.08V12a10 10 0 1 1-5.93-9.14' }), h('polyline', { points: '22 4 12 14.01 9 11.01' })] },
]

const menuItems = [
  { label: '收货地址', path: '/address', iconSvg: () => [h('path', { d: 'M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z' }), h('circle', { cx: 12, cy: 10, r: 3 })] },
  { label: '优惠券', path: '/coupon', iconSvg: () => [h('path', { d: 'M20 12V8H6a2 2 0 0 1-2-2c0-1.1.9-2 2-2h12v4' }), h('path', { d: 'M4 6v12c0 1.1.9 2 2 2h14v-4' }), h('path', { d: 'M18 12a2 2 0 0 0-2 2c0 1.1.9 2 2 2h4v-4h-4z' })] },
  { label: '我的收藏', path: '/favorites', iconSvg: () => [h('path', { d: 'M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z' })] },
  { label: '帮助中心', path: '', action: () => showToast('功能开发中'), iconSvg: () => [h('circle', { cx: 12, cy: 12, r: 10 }), h('path', { d: 'M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3' }), h('line', { x1: 12, y1: 17, x2: 12.01, y2: 17 })] },
  { label: '关于我们', path: '', action: () => showToast('功能开发中'), iconSvg: () => [h('circle', { cx: 12, cy: 12, r: 10 }), h('line', { x1: 12, y1: 16, x2: 12, y2: 12 }), h('line', { x1: 12, y1: 8, x2: 12.01, y2: 8 })] },
]

function goOrder(status) {
  if (!userStore.userInfo) { router.push('/login'); return }
  router.push({ path: '/order', query: { status } })
}

function goPage(path) {
  if (!path) return
  if (!userStore.userInfo) { router.push('/login'); return }
  router.push(path)
}

function handleLogout() {
  showConfirmDialog({
    title: '退出登录',
    message: '退出后需要重新登录才能使用完整功能',
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    confirmButtonColor: '#FF3B30',
    teleport: 'body',
    zIndex: 9999,
    overlayStyle: { zIndex: 9998 },
  })
    .then(() => {
      userStore.logout()
      showToast({ message: '已退出登录', icon: 'success' })
      router.replace('/login')
    })
    .catch(() => {})
}

onMounted(async () => {
  // 获取会员等级列表
  try {
    const res = await getMemberLevels()
    levelList.value = res.data.list || []
  } catch (e) {
    console.error('获取会员等级失败', e)
  }
  // 获取用户信息
  if (userStore.token) {
    userStore.fetchUserInfo()
  }
})
</script>
