<template>
  <!-- Category — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 px-margin flex items-center justify-between gap-space-md">
      <div class="flex items-center gap-space-sm shrink-0">
        <img alt="Walmart Logo with Favicon Spark" class="h-8 w-auto object-contain" src="https://lh3.googleusercontent.com/aida/AEtjO1WxbFcbyvQxjOWkbhkpIy_RXXWkvvU4r8XwbBzzs9Nco_MOan5kyMhQR-HNMCR0mRp1uF8RgvdxFCmQDKNrWLsQIREwC9bCIhANclidwFzjHDJdKYngvbC9SVhJUEFCNQKdAdgsR_kuBjHhBzu_6r_lIOSDxLPhDvxFeI1qc18Fk2Q5QU-DNPWkKJpBlkAaHNmUuGr1XuZnVwAPynaif0RJ1AMEB8zun78Qcq8acrYVeyXlIND6DoxdvQ" />
        <span class="font-headline-sm text-headline-sm text-on-surface tracking-tight font-bold">Category</span>
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
        <div class="flex items-center gap-1 bg-surface-container-low px-2 py-1 rounded-full text-on-surface-variant">
          <button aria-label="更多操作" class="flex items-center justify-center p-0.5 hover:text-primary transition-colors">
            <span class="material-symbols-outlined text-[18px]">more_horiz</span>
          </button>
          <div class="w-[1px] h-3 bg-outline-variant">
          </div>
          <button aria-label="小程序码" class="flex items-center justify-center p-0.5 hover:text-primary transition-colors">
            <span class="material-symbols-outlined text-[16px]">radio_button_checked</span>
          </button>
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
          <button class="absolute right-0 top-0 bottom-0 bg-gradient-to-l from-surface-container-lowest via-surface-container-lowest to-transparent pl-4 pr-margin flex flex-col items-center justify-center text-on-surface z-10">
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
          <!-- Subcategory Pills -->
          <div class="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-0.5">
            <button class="shrink-0 px-2.5 py-1 rounded-full font-body-sm text-body-sm bg-primary-fixed text-primary font-bold shadow-xs">
              葡提/奇异果
            </button>
            <button class="shrink-0 px-2.5 py-1 rounded-full font-body-sm text-body-sm bg-surface-container-low text-on-surface-variant hover:bg-surface-container transition-colors">
              葡提
            </button>
            <button class="shrink-0 px-2.5 py-1 rounded-full font-body-sm text-body-sm bg-surface-container-low text-on-surface-variant hover:bg-surface-container transition-colors">
              奇异果/猕猴桃
            </button>
            <button class="shrink-0 px-2.5 py-1 rounded-full font-body-sm text-body-sm bg-surface-container-low text-on-surface-variant hover:bg-surface-container transition-colors">
              姑娘果
            </button>
          </div>
          <!-- Sort & Filter Bar -->
          <div class="flex items-center justify-between text-body-sm font-body-sm text-on-surface-variant px-1 py-1">
            <button class="font-headline-sm text-headline-sm text-primary font-bold flex items-center">
              综合
            </button>
            <button class="hover:text-on-surface transition-colors">销量</button>
            <button class="flex items-center gap-0.5 hover:text-on-surface transition-colors">
              <span class="">价格</span>
              <span class="material-symbols-outlined text-[13px] leading-none">swap_vert</span>
            </button>
            <button class="flex items-center gap-0.5 hover:text-on-surface transition-colors">
              <span class="">筛选</span>
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
      <div class="hidden fixed inset-0 z-50 bg-on-background/40 backdrop-blur-xs flex flex-col justify-start" id="fullCategoryModal">
        <div class="bg-surface-container-lowest w-full max-w-[540px] mx-auto rounded-b-2xl shadow-xl flex flex-col max-h-[85vh] overflow-hidden pt-safe animate-in fade-in duration-200">
          <!-- Search within modal header -->
          <div class="px-margin py-3 flex items-center justify-between">
            <div class="font-headline-lg text-headline-lg text-on-surface font-bold">全部分类</div>
            <button class="flex items-center gap-1 font-body-sm text-body-sm text-on-surface-variant hover:text-primary transition-colors py-1 px-2">
              <span class="">收起</span>
              <span class="material-symbols-outlined text-[18px]">keyboard_arrow_up</span>
            </button>
          </div>
          <!-- Categories 5-column grid -->
          <div class="overflow-y-auto px-margin py-2 grid grid-cols-5 gap-y-4 gap-x-2 text-center pb-6">
            <!-- 1 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-primary-fixed p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Fresh sliced grapefruit icon on soft mint background." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCmyNvzlnnRSyxBjLi-7KNrK6tRDSlFJWBr7f78ahPFZv9vHrSvRWAEHTVj1bh0Rh7uqc6RRxbq1A_VS33exMPdlV47n6hwfDwgTrUNwWuzUxdIB-M8XgTqxfpMXKRegw_vaIzyRpzH3o1wG07hcZpePVdmLREXuE5jCC_7Qy34yGeib7R_714wp6PScJpfYMiRUTdz49zrXftEYSgnzKCVZfT8xGDHrrDIRKkMR-GkBEhttkHZhLyo">
              </div>
              <span class="mt-1 px-1.5 py-0.5 rounded-full font-badge-label text-badge-label bg-primary-container text-on-primary font-bold truncate w-full">水果鲜花</span>
            </div>
            <!-- 2 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Vegetables and tofu grocery icons on light background." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDXa89raiDcRmPcfQrQXYKchCt6aQYRdpiCgOn6G9N11WqdSRNjRnb3dwcF7SxZwr_hKpJW6Y1KnLVQ_ftWtKAaXhxYRc3jjPfI1Gza1e6Z7fC83Vo-F4fnhcIUqcOnvPuPD3k8BTG2VV_m_b_XRD1Wm_qkPiToG05Bi0p9FW5syEpjGkHePn6B7T0vs9B3m41sUI20833UuAGxCOwbUqp_54BLZRfo8wJWb5BYYCIm4M9jUNHesKHu">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">蔬菜豆制品</span>
            </div>
            <!-- 3 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Fresh meat and eggs icon." src="https://lh3.googleusercontent.com/aida-public/AB6AXuABoy9pAfRBamv1rrLcCRD1Mnbl9ykvlR_kf1BnT3xFkqWhUYSzd42csEHOuMv2FIxDAJPwX84ZJmFbxAWJaO7yYU52Jh1L-kvc6_MqrA94FtqzddWVVFOh5xV366fRdj2DwBarCPmlUdbxqDDjdk_kz-yb7pKJt06DYjwbsJWg3AqsuLBtYLGfanv_5jTg2Vbsc8vZF9qspMss4lZBLF41H8j9WmbgBjxW5b6LIH-nJ4EvzpEqyyLu">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">肉禽蛋品</span>
            </div>
            <!-- 4 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Seafood fish and shrimps fresh food packshot." src="https://lh3.googleusercontent.com/aida-public/AB6AXuAmgS3dNW01gYCP4RC2WQETD872tcxY37YKNRXAYpYiIqzZ3BSRUbtMv7h0UerwcAhRtINRpWayfnYvGlbDt8Rh1WGQZgfZYGrVRfWjM7LqyJaUpw9VeSsgRWHvi3X-AtHzjEJwTAQRAWoclgOTZi-UN2dSLf_k64gwud20FFaZ4a4kT_RgEefkoaUv_gaCmZLusd2OIYzzvNuxCwYmY62EJJcfHzIZYnDr2iyxgWTaWy4bJQYu0pCg">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">海鲜水产</span>
            </div>
            <!-- 5 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Roasted whole deli chicken prepared meal." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDiE1ItxPM0AOIbVefzHc51csYCzOPrrZTmJJAZVOCEGbfLuVGdXtyEACcCYrY1GPPCZIyi1kPawnN5-29rgEdhgOTW5GoqkyYctlRIi3TwNphjo6eZwf2qVl80SxA-VAOoATroHDx1JaputRNcip7KSF6X9E9iD_hmT_4rL6cY8G-vPdnBNs44ANt82Lv2hh44DmQcClsP7qpfntPg6ZG0j2hRnFFsa9iVeG1gO7_sjeT1SmVGqjwA">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">餐饮熟食</span>
            </div>
            <!-- Row 2 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Steamed buns and bakery food icon." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCxnXe6yyvGMgEADDo2jp6kLPndY9xnwj5k5uQOnjO30tsvQkgYxZ4po524IeZobZ11a8znCuYvG8WqwpY3cu2c7C4O1vfe8dLbLVFF8WTYCjRzsIt6u-d2gdrM8T6MBCEFSt0k2ARDBF1VTmkMqic1p0IgYPPQMBLd8FBB058d86fFsG-vSjBTFfHuG0p2B9HY0q8eqOpXb3odZN3FJht3uIXas-UWiDlUeKfChO9Q4sic3P0M8oDl">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">快手菜面点</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Fresh milk bottle and croissant baked goods." src="https://lh3.googleusercontent.com/aida-public/AB6AXuA6cgRui6Z2ndCQIiC-BvAu2BfIpEtLEbyi9746aJRA4GDYmKsnlqluOYGd-4LOK7qNa2UWjHrKBY8dVEfnJjGx8j4hFurmPgVvoE5ZcbCEJfib4nolDM-jiwg7SmWCVLYHwtfzIAXFiHy1pIbP_FQhnQgmJmHIPzXl3vSXGUVDODpvSrgJsHL4FMYvFrtdQd7AoYodXsO5fkql5eQMWYndJ60eK62Qjmaq76AZ7ii9Xgbmna66iUID">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">乳品烘焙</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Crispy snack chips and nuts packshot." src="https://lh3.googleusercontent.com/aida-public/AB6AXuC1IsFr73eQzcj62cDdmwh1fb8Y1_umu63z00lpkJgqXorneVNrhbRowO2HPT_Yj8ZVg1xjth6hWaA62gVBUVJZS_vNrFdwfCcDRXEEcTo8tS0SYDG5sJa_4gY9tbnTnbGtuL_G2zlAbwS4jn35qGXUXC3pdYP-cd8FEkgW17yUyvPXV6GgzZz6oKZKTRD8SwK2deFe9RYulM6zZiwLAh3YVDaFgWxR7_JBTQoQTdLHii9RF5Ve3M_d">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">零食坚果</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Cooking oil and organic seasoning bottles." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCL9lRHMhSL-ZGEhtLaMl7kqWhWM_jEKRo0eS3-VczbVLE1V0DPLmf2v0GLO4jC9ey_bBkRTqJSlgTxfFPeV7HDYUr4-Vp7M1PomEcXa953R9bTBs0VzV7G3Q2PkaXqpPvjpxpSlC763iGRlPb6Hh5aQo1Ca1dFY2Rr_j4euSGP9UU73VQQ6emfSnjnHQsKW9y1jricDvrDYmWLP05tXR2FWSzY2SSMhpQYYCl5Zzr9QvX14Uh-lMmd">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">粮油调味</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Craft beer bottles and sparkling juice beverage." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDFtnY8oecmjY1LNUYr4crWSSdWNFJr1wE6VlnbE5XtMupX5rHYl7J5ahoLgvdQbswH8iXtF8eHAcgC8ioCKQ5QQXOl5LFd5wlqd-GHuq7rWCZvTw3ykRtEBrYcz8NFy5jULsxdDgqX4tDg6lYULU0OC23FpryVO_mQc5xGsk0q5IP92cfYIgKUBZkaq6evqu_FX1m0ZY6R5RQBaZI0cQNBthMO9_C60V65ZgyRStfcWTHAko6n591b">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">酒水饮料</span>
            </div>
            <!-- Row 3 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Special Walmart curated value fresh box." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCg4RjjbM7GCVOzQzRsFeVAytLocKiSJTJXMdnvzKYDV7hFJfxrkUhhn9P9LGpDJIIClIZ6zOvuR3unrBpuUbQvmth1WP_dZAGgzDanJUyd8E_PttokTxPDRrCD2diA50KaD78ABn7x2X8wqol_eROk5rCk1f0HdUHHwobrTb4-YBKE5si28TMzvtR6hltNpPIk7NJ_Icm9ebDDXmmol6wEvgbOLSnouPxBvixWQ6CQezMfbz0TpxVw">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">沃集鲜专区</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Steaming copper hotpot with fresh ingredients." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCuTy5aJfBBtxoIuaE2Nt5TatQwxQJbjcB0VIE85fx6PnTZV3KrMqQGMRWnPbfj7r43i_Pu6PapgEctTkriCbXPeso_4FwrFgk87UwFKMBB_4b_SNaFu0WgIv05ztqUKmgKt39kMiSUAnSwetpXnt_uX0fnT2cwz7M5TpIwgD5DRBOl1kS7wH3BbE3oL_NJXYmc0pw4zLt3vEGZIqWPAKudp-4KcwgPZS2SR3ejJ-wkTgQYG7CjCWkE">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">火锅季</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Global imported luxury serum cosmetic bottle." src="https://lh3.googleusercontent.com/aida-public/AB6AXuAvdg1Hld3ukdPgWTLIaxQpEiEByC4sLi3kFNRU4E_BT93qst7_EJ9EEI3oLYrjSKhrUCahN_f8FIfKe3TeQgC3P6QFFvfgWXaSK8EYC05qORRXKqlq9NlcB-b9bezpuiX6PMddBLLHwwqXFa13V32RLqmhpdK_mQxaVD487EQwTwlRy8cRgeKBO77zyvbwMs766ElGT2Fe9-lCxnFxxNg6np2Id2m-1x8V3G6E_JC08vGKtkzo-zDa">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">全球好物</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Organic shampoo dispenser and beauty care products." src="https://lh3.googleusercontent.com/aida-public/AB6AXuA-otJKKkGa3mTYmO0XvqTd2rCwkln6E2MLk0stnf4TVKANqgHMfljxvgJh_ycblldWd1AtXUEtKEUdKEW7hwVWWEDp9ck2OZoiemQ3j6ssbJF49vRS3RHyOLUyuX02zI4OMHatnKr-hDvcxtjiJGkbylzgGAYckZMQ8RwY89WhZ1yLIb7yDNU3xqHqtzTPOf_NSh1fpCLh78RiyDYkT-WovKfY3wFdKB8UoVGnUMvl-2gTQjT2QaXj">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">个护美妆</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Soft tissue paper box and home cleaning supplies." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCiCu7kbloFxKh_eLh-qPNr8c40yFJUrUZm_DExsZMOYM6mv0z5lfYKirklxWdFUyX-QD2Irf939nAdyQyBcMapdrGKUVuW8zxWWYUflDaUqD1rtZqX6YXxAqHrvbmZFs6lEgm6-pERj2bOTs7CcJepCu0wLXDakUqGpBtEwK6d1v2EDJ2orXjaj2Sejj5ihEaZxQ5q4VKtI1iPHqDZl-e48V1m-Scga-CEoLMpafEETXCEz7H70hBC">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">纸品家清</span>
            </div>
            <!-- Row 4 -->
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Baby formula tin and pet care products." src="https://lh3.googleusercontent.com/aida-public/AB6AXuBGOKxUox4yOw372lMlRSCl6zBsxSDnk-XxY1bmOxLyFhNda8ysNBMm0hAXzpye1ldOzrJa_uO8K2sD-goeqHOChx_9F_Hl2ui37nBXCLhrjUf_wVrygpxPPg5oUuVZbnyCiIb6B8aQXhHR15blxluWojUKLL3TknEzDZ0EDTE6vfTlfM5o0w0UG0wqQs0oolm9vFdHfcWo1LbW6rKqujK-vjonuuQO5QJGSHv_M6ZD_Ijmolp_rRUu">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">母婴宠物</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Modern thermos bottle and home kitchen daily goods." src="https://lh3.googleusercontent.com/aida-public/AB6AXuB_J9sBI5mWFwVSfsRxLmDStloXNN67E-kG2Hizs57y_KqEbNkEftwDepKO9-i1YSnbNwSq1INBRHblK_atl3pird5VaN3WwgUX46j97DIrBMvLeeog0buC9CMTULR7J9NBWK0JMG-iyIqvx00y2bTui-OQZwUlvrb50UTXV0A3PmHdjh1gvGfWyVoncBfwF3GS-uKdDwerbIRmO-8CfsoO4GCtzdF8Tcmaj5HZxof-naip_RCHIWR7">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">家居百货</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Cozy down jacket apparel and cotton bedding." src="https://lh3.googleusercontent.com/aida-public/AB6AXuAsPefsODSfNNDgztHM2Qjl56jeb4QgIqsIGog2LeZhIOEMeVmf-BZR-aZVDMfgChw-7rPbW1b2jOsmV2p-dcEiInEs9xM9THwRdE4fuzEnWsrxOY9wwBUyJqxptHL4G64UwH-vNhpYkmh63DD1kveIZWE2okv5CRpusN0EDo8D6-aesU1jn83JWdS-Wu1aEcSbHuh97sx3PwLEjUFMS11FQTgI-KXhsr0MvQcdrasCScGpErxFU09d">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">床品服饰</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Electric handheld lint remover home electronics." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCtUWPkMTf6laqcwO7uR9RvWp7GuYrXHoaK82cuIh16iwUSL85a0FsKDLmp9eILKxo-VAMJk586lTZV97T4FDTGgtaeouZGCqSskET5Rn8ciUe6Yp9OcVzOLYlg0Y0E19q0EdzUjeFnAg4LHaIz0f7_BN7YyHhGj-FjXLkhYGXcuC0qQJ6kxdA7mPZG0kJmAfbwBHTeR-3G8UIF64V72B7Xrd77wH6ndN5buB5pHAbkD46ufrujVCX4">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">家电数码</span>
            </div>
            <div class="flex flex-col items-center">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-container p-0.5">
                <img class="w-full h-full object-cover rounded-full" data-alt="Healthcare multivitamin capsule bottle on green tone." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDXw9VqsHX--OeQuAnpG1bri_1k9qdVKZvMibO3wfue4RtNKgVg12e_XLV2xvFXnNZfBrT9chsJBoSBskPijZbYfCVZ11ZR59vTZrKdj42YceCOQjzUut6GF5sz2WfmBjGtzq_Ts9QH3MF_DleoBawI9YUcYH4vou0mR-mgmPcZrcdRIgaJu4db076G8q4NtnB3fBxzY6D9uOT_KLkuXfRKaqlqkm5TSSD2MnrMX9yR5_KDc4P6503b">
              </div>
              <span class="mt-1 font-body-sm text-body-sm text-on-surface-variant truncate w-full">营养保健</span>
            </div>
          </div>
        </div>
        <!-- Dismiss Backdrop Area -->
        <div class="flex-1 w-full">
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchProducts, getCategoryList } from '@/api'

const route = useRoute()
const allCategories = ref([])
const selectedParentId = ref(0)
const selectedChildId = ref(0)
const products = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)

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
    // 如果 URL 带 categoryId，自动选中
    if (route.query.categoryId) {
      const catId = Number(route.query.categoryId)
      const cat = allCategories.value.find(c => c.id === catId)
      if (cat) {
        if (cat.parentId === 0) {
          selectedParentId.value = catId
        } else {
          selectedParentId.value = cat.parentId
          selectedChildId.value = catId
        }
      }
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

// 监听路由参数变化（从首页再次点击分类时触发）
watch(() => route.query.categoryId, (newId) => {
  if (newId) {
    const catId = Number(newId)
    const cat = allCategories.value.find(c => c.id === catId)
    if (cat) {
      if (cat.parentId === 0) {
        selectedParentId.value = catId
        selectedChildId.value = 0
      } else {
        selectedParentId.value = cat.parentId
        selectedChildId.value = catId
      }
      loadProducts(true)
    }
  }
})

onMounted(async () => {
  await loadCategories()
  loadProducts(true)
})
</script>
