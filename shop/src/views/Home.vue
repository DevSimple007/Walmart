<template>
  <!-- Home — 外观严格取自 Stitch 设计稿；数据/事件沿用原有接口逻辑 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 px-margin flex items-center justify-between gap-space-md">
      <div class="flex items-center gap-space-sm shrink-0">
        <img alt="Walmart Logo with Favicon Spark" class="h-8 w-auto object-contain" src="https://lh3.googleusercontent.com/aida/AEtjO1WxbFcbyvQxjOWkbhkpIy_RXXWkvvU4r8XwbBzzs9Nco_MOan5kyMhQR-HNMCR0mRp1uF8RgvdxFCmQDKNrWLsQIREwC9bCIhANclidwFzjHDJdKYngvbC9SVhJUEFCNQKdAdgsR_kuBjHhBzu_6r_lIOSDxLPhDvxFeI1qc18Fk2Q5QU-DNPWkKJpBlkAaHNmUuGr1XuZnVwAPynaif0RJ1AMEB8zun78Qcq8acrYVeyXlIND6DoxdvQ" />
        <span class="font-headline-sm text-headline-sm text-on-surface tracking-tight font-bold">Home</span>
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
        <div class="flex items-center gap-space-xs overflow-hidden">
          <span class="material-symbols-outlined text-[18px] text-primary" style="font-variation-settings: 'FILL' 1;">location_on</span>
          <span class="font-headline-sm text-headline-sm text-on-surface truncate font-bold">南云商住楼2号楼</span>
          <span class="material-symbols-outlined text-[16px] text-on-surface-variant">keyboard_arrow_down</span>
          <span class="ml-space-xs bg-primary-fixed text-primary px-space-xs py-0.5 rounded-full font-badge-label text-badge-label flex items-center gap-0.5 shrink-0">
            <span class="material-symbols-outlined text-[11px]" style="font-variation-settings: 'FILL' 1;">bolt</span>
            最快30分钟达
          </span>
        </div>
        <div class="flex items-center gap-space-xs shrink-0">
          <button class="w-8 h-8 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors">
            <span class="material-symbols-outlined text-[20px]">qr_code_scanner</span>
          </button>
          <button class="w-8 h-8 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors relative">
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
            class="bg-transparent text-on-surface placeholder:text-outline text-body-md font-body-md px-space-xs flex-1 outline-none min-w-0"
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
          :key="cat.id || cat.categoryName || i"
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

      <!-- 次级专区标签导航条 (水平滑动) -->
      <div class="px-margin mb-space-sm overflow-x-auto flex items-center gap-space-md scrollbar-none py-1">
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer">
          <span class="material-symbols-outlined text-[16px] text-primary">storefront</span>
          <span class="font-body-sm text-body-sm text-on-surface font-bold">沃集鲜专区</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer">
          <span class="material-symbols-outlined text-[16px] text-tertiary">local_fire_department</span>
          <span class="font-body-sm text-body-sm text-on-surface">火锅季</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer">
          <span class="material-symbols-outlined text-[16px] text-secondary">public</span>
          <span class="font-body-sm text-body-sm text-on-surface">全球好物</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer">
          <span class="material-symbols-outlined text-[16px] text-primary">child_care</span>
          <span class="font-body-sm text-body-sm text-on-surface">母婴宠物</span>
        </div>
        <div class="flex items-center gap-1.5 shrink-0 px-2.5 py-1 rounded-full bg-surface-container-low shadow-sm cursor-pointer">
          <span class="material-symbols-outlined text-[16px] text-on-surface-variant">clean_hands</span>
          <span class="font-body-sm text-body-sm text-on-surface">个护美妆</span>
        </div>
      </div>

      <!-- 假期快乐不重样 - 核心运营 Banner 组合（营销位，保留设计稿静态内容） -->
      <div class="px-margin mb-space-md">
        <div class="bg-gradient-to-r from-sky-400 via-sky-300 to-amber-200 rounded-xl p-space-sm shadow-sm relative overflow-hidden">
          <div class="flex items-center justify-between mb-space-xs">
            <h2 class="font-display-hero text-[22px] tracking-tight font-extrabold text-primary-fixed-dim drop-shadow-sm flex items-center gap-1">
              假期快乐不重样
              <span class="material-symbols-outlined text-[20px] text-secondary-container">flight_takeoff</span>
            </h2>
            <span class="font-badge-label text-badge-label bg-surface-container-lowest/80 text-primary px-2 py-0.5 rounded-full font-bold">每日爆款直降</span>
          </div>
          <div class="grid grid-cols-3 gap-2">
            <div class="bg-surface-container-lowest rounded-lg p-1.5 flex flex-col items-center shadow-sm">
              <div class="w-full aspect-square rounded-md overflow-hidden bg-surface-container-low mb-1">
                <img class="w-full h-full object-cover" data-alt="Fluffy round pumpkin cakes in a neat bakery display tray with golden crust" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDm5DJ6q8obag2OUoOYr6UBxv4KV5mFo8ImBNFHQ_HzAEJteNhrjIX0yEjHOsM7B03ev6ZHfo2DXHxzWppaNa-pK82S7y32W8tvhHhuIy9rSwhGCpJQWrAK57NTiitLjXQWWnReRCOo3X9qIZs01JOZsGT8hLicBszjfKC2Qsk2PEYQYiiPh_ANihlULU7qHIAgUkbEARBSZdAXucASCHYsPlb22QQz1coh7evp8a2fL61TUw4w8fZ6" />
              </div>
              <span class="font-headline-sm text-[11px] text-on-surface truncate w-full text-center">南瓜软蛋糕</span>
              <span class="font-price-currency text-[11px] text-tertiary font-bold mt-0.5">¥9.99</span>
            </div>
            <div class="bg-surface-container-lowest rounded-lg p-1.5 flex flex-col items-center shadow-sm">
              <div class="w-full aspect-square rounded-md overflow-hidden bg-surface-container-low mb-1">
                <img class="w-full h-full object-cover" data-alt="Sliced roasted savory pork jerkies ready to eat, rich glazed sauce" src="https://lh3.googleusercontent.com/aida-public/AB6AXuA__BJXsKitwT8yiHk5R6gC4Y5naaCaSS95o6OnbUKMePmPMNF37srPEtC2tfW2SsKX95XvXeIIf8kdi-A2ug_L4TMRcDvFs5O0LjHJ7BtmJQkgInwqTmsGCxWSAp1HRpLIbfazedJD7_-47SydfXsH9YbXIvHMLMMovlmMye312vTh_6FwYlRINcHcc3reVUqnmj4an4fWUTc74Dv3-kPYu94-Mce8FoI_JH2m9h7sFh_6ZiO5O6IP" />
              </div>
              <span class="font-headline-sm text-[11px] text-on-surface truncate w-full text-center">原切脆脆猪肉棒</span>
              <span class="font-price-currency text-[11px] text-tertiary font-bold mt-0.5">¥19.90</span>
            </div>
            <div class="bg-surface-container-lowest rounded-lg p-1.5 flex flex-col items-center shadow-sm">
              <div class="w-full aspect-square rounded-md overflow-hidden bg-surface-container-low mb-1">
                <img class="w-full h-full object-cover" data-alt="Bottled chilled herbal tea beverage with citrus slice and condensation drops" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCqkQ6OfTHSwcGJbKTyi4WZc81DELZTPEeiK0BMxTbj-7Mmyxtp3ryAxeulrcdvWtcJxkt3j7v7uKOqGggEalSDOarx3qrSTgDmI4JgvXaguGHtdme-FzmXv8PY8xGLILya5qbCFd-vjDvD1atHT-NXlm-XebjTrxnZji8nR3-2vg02qYSvPPoo2UmMafuTVpDd2uHyssxhmXvGLZtuBtWvMoegx9l-1vX-j4auTpbMBbazpfD84WjU" />
              </div>
              <span class="font-headline-sm text-[11px] text-on-surface truncate w-full text-center">老盐黄皮单丛茶</span>
              <span class="font-price-currency text-[11px] text-tertiary font-bold mt-0.5">¥6.80</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 双专区运营楼层：质选生鲜 & 宝藏新品（营销位，保留设计稿静态内容） -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col">
          <div class="flex items-center justify-between mb-0.5">
            <span class="font-headline-sm text-headline-sm text-on-surface font-extrabold">质选生鲜</span>
            <span class="bg-emerald-600 text-on-primary font-badge-label text-[9px] px-1.5 py-0.5 rounded-full">时令&健康</span>
          </div>
          <p class="font-body-sm text-[11px] text-outline truncate mb-2">从源头把控每一份食材</p>
          <div class="grid grid-cols-2 gap-1.5 mt-auto">
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center">
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Chilled premium vacuum packed cod fish fillet ready for pan fry" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCK2cnkk8dB5wgz0KqUn8Zx-qG639CNN5DcQZM9NiATzvgbggwO_huY_yp99Cz0RJQRUP1SpKCdWjtL8jOsHqa83FuweJ3lWny7rFRlpeKXJLvsP8l2MpG2b-BOzXIEKE59XjEJ3f97M531h2Fi3IdQPPR5D7Gsy8qPiFOTwYaKBggbQWqJXsS6RCxV1FRYMHa-O7cVqqIattkjC5_vZkFHW_U4EsREoQ_2ja5MfsrID5bGhQQugAc4" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">深海捕捞</span>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥29.99</span>
            </div>
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center">
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Fresh low-sugar coconut milk bowl dessert with sweet beans and purple taro" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBjPqj4lothnVpFvQG3KpQM_3S-a8TEA0OtFA6lQxEHizy01oTvUPERI7Di4kY57GVpYr-eXYh1qqbNgT1purdCUfw3jYqbI62NbZErsQaBG5HQsBzXMlk1-N54a_My6yighJg2YzprSuSxFo7lhpxQ-zPbm8evKtdUp812HnzrsdORFUtlrfgXUPT9JhLBirxZZ3N0VgoTC6m0RtWJC-VTnwoW-y9BMNGRi21Y47NfaVGgVyFzQKOi" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">进口椰浆</span>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥14.99</span>
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
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center">
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Rich spicy hotpot soup base broth pack with fresh red chili seasoning" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAMaIe8oAl2OGZWKKW2jutnnRAIe7DGCPJLNDDpNkDGpJ6lHYu80XDVYSPyhRALPbzqpI1cKBkmzQX4syTvtrw7ey5q1aXQgdLsrRKNrE2I9OKtrF69NxIA2MK0sBKgwuCinGSdseTcihqEX8yJvF-WukQQgBsOunwD2S4v6SsrC1aAIvPkVa7Kk23ZGMihrULb_XSHF510pXdjcB9y7LxXvtDgFF140nCVtdsZ7MiK_990hg_THnMB" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">汤底浓郁</span>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥12.99</span>
            </div>
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center">
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Herbal plum refreshing juice drinks in slim eco cans" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDPSK4vD1w2Q-LCu6h9OSLfHJflskPmLK5u53DjftIDwc3ZJPZf1CsF1W_uaSgAYmUVTeLV_zFuHHmu2eCv1yJIHrz0OqZQ7wtXoHqEm2wxlG2xbd6IZoV_Kiea8Vtq6Asjc7EEwNutBe3cQYLzGVjOcU2IHYzXh4G36IDlrb-70Qzl7WwoxLWZkjgeLfgraEsnLtucm5CdEK4p_6GbTY4iYtFBGjqJqsllVvph5DHLukk3AEbk_6K_" />
              </div>
              <span class="font-body-sm text-[10px] text-on-surface-variant truncate w-full text-center">慢熬8小时</span>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥18.99</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 大牌低价 & 热门榜单 楼层（营销位，保留设计稿静态内容） -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-space-md">
        <div class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col">
          <div class="flex items-center justify-between mb-0.5">
            <span class="font-headline-sm text-headline-sm text-on-surface font-extrabold">大牌低价</span>
            <span class="bg-purple-600 text-on-primary font-badge-label text-[9px] px-1.5 py-0.5 rounded-full">好物不贵</span>
          </div>
          <p class="font-body-sm text-[11px] text-outline truncate mb-2">生活好选择 闭眼入好价</p>
          <div class="grid grid-cols-2 gap-1.5 mt-auto">
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center">
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Crispy chocolate biscuit stick snacks in anime collaboration wrapper" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDqk3sVH_dsbwBp4HlLwBA-rqrZmL2FsOyridjULNLJWoo8QYCsnuuFDJferwZPhiQ38sr2UnGNGRvL1COIJGvBjXtRB6ZED03TuhnWjqCqpCWhzwOCqJLKRifKetLK5S-O7P0fqTTmCIlrzL91vZEH-30BfSRRwNnpsp774ZglqnRPYXlcLHviYZlna5BmbLEjezd_ugaeefzLD1--r6jyE7nZUNaIP70YBkpEHaGtkc3v3PK_mWm6" />
              </div>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥29.99</span>
            </div>
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center">
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Dermatology soothing thermal water spray bottle on pure white backdrop" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBn0dOEUVR6UpyTAWOc2hZn3xSMR5pz-Y3OVPEDzioSdl0NLcHLBpfFZz8MAbziG2BSkByz7ZcqH0ypjCnrf7t4qU3A8tpkJiDFUPMDAK_r0y9rJZMKlCt-Qn5MYL2MzSd7XEHoOY_Q_6wB4hpb98r24eakXpvn846ybdNB2fFfHd-QDME2UcIyhzadcGrkDWuYT5y6Fj667YB9h6j2xyhg4qiQhFPgDIlr8uc_dZBAS5AWG0kOp9_b" />
              </div>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥19.90</span>
            </div>
          </div>
        </div>
        <div class="bg-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col">
          <div class="flex items-center justify-between mb-0.5">
            <span class="font-headline-sm text-headline-sm text-on-surface font-extrabold">热门榜单</span>
            <span class="bg-secondary-container text-on-secondary-container font-badge-label text-[9px] px-1.5 py-0.5 rounded-full">真实排名</span>
          </div>
          <p class="font-body-sm text-[11px] text-outline truncate mb-2">应季水果 好评人气飙升</p>
          <div class="grid grid-cols-2 gap-1.5 mt-auto">
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center relative">
              <span class="absolute top-1 left-1 bg-amber-500 text-on-primary font-badge-label text-[9px] w-4 h-4 rounded-full flex items-center justify-center font-bold">1</span>
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Sweet juicy purple prunes neatly packaged in clear plastic box" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDI3YWEINvPQW-JADicIkg8YeCmk6nYWINc_BiGCmTgRR-emBY85vpo7sYmZrXBzY01524mQJ55E39oDY7N0Fmu3y4pHqY4q4Lanv55ULGHssINP9isZfXCWDUjmD2NBBFJ_kQ2IWXoXCmEsOaOZZbY3kXWHarlWbBucn_IwTdvYiYjM7hqxL8mIBttpK_DqbYsjDPEtdhomBIhSnD3KRnoMoI1eBcgHDSzX-l4sQrkzLfPwfBliCqd" />
              </div>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥13.99</span>
            </div>
            <div class="bg-surface-container-low rounded-lg p-1 flex flex-col items-center relative">
              <span class="absolute top-1 left-1 bg-slate-400 text-on-primary font-badge-label text-[9px] w-4 h-4 rounded-full flex items-center justify-center font-bold">2</span>
              <div class="w-full aspect-square rounded bg-surface overflow-hidden mb-1">
                <img class="w-full h-full object-cover" data-alt="Cluster of sweet dark purple grapes with natural frosty bloom" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDcX835mhXi29saj6IkxYOMYrMGMB99U_t6KtXNksHlZt6z0dASaVQ8D5f9AEf7_DM5aytLkQfotXccL5Yo37YJm6pnN5T7N_miRcrSpxcooI74iZaKUDZMjH8nB0JhqVUzL5jgQj2cBbVz8vF7e0SWzig1NrmeM2rV9uEk4Yx1qlRU1vAgqbRG3R_Ld2wqE8sSrV5EuDphETB6jL_J75zFPVq82bDvtplEIznG52_T4f4cWDxGff0L" />
              </div>
              <span class="font-price-lg text-[13px] text-tertiary font-extrabold">¥9.99</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 猜你喜欢 / 热卖单品 标签切换条 -->
      <div class="px-margin mb-space-sm flex items-center justify-between overflow-x-auto scrollbar-none gap-space-md">
        <div class="flex flex-col items-center cursor-pointer shrink-0">
          <span class="font-headline-sm text-[15px] text-primary-container font-extrabold">热卖单品</span>
          <span class="bg-primary-container text-on-primary font-badge-label text-[9px] px-1.5 py-0.2 rounded-full mt-0.5">WOW人气</span>
        </div>
        <div class="flex flex-col items-center cursor-pointer shrink-0 opacity-70 hover:opacity-100 transition-opacity">
          <span class="font-headline-sm text-[14px] text-on-surface font-bold">不时不食</span>
          <span class="text-outline font-body-sm text-[10px]">蜜薯9.99</span>
        </div>
        <div class="flex flex-col items-center cursor-pointer shrink-0 opacity-70 hover:opacity-100 transition-opacity">
          <span class="font-headline-sm text-[14px] text-on-surface font-bold">健康零食</span>
          <span class="text-outline font-body-sm text-[10px]">饼干13.99</span>
        </div>
        <div class="flex flex-col items-center cursor-pointer shrink-0 opacity-70 hover:opacity-100 transition-opacity">
          <span class="font-headline-sm text-[14px] text-on-surface font-bold">秋日饮品</span>
          <span class="text-outline font-body-sm text-[10px]">牛乳茶5.99</span>
        </div>
      </div>

      <!-- 双列瀑布流热卖卡片区：商品数据来自商品列表接口 -->
      <div class="px-margin grid grid-cols-2 gap-gutter mb-space-lg">
        <!-- 商品 1 (营销卡，保留设计稿静态内容) -->
        <div class="bg-gradient-to-b from-amber-100/70 to-surface-container-lowest rounded-xl p-space-sm shadow-sm flex flex-col justify-between">
          <div>
            <div class="flex items-center justify-between mb-1">
              <span class="font-headline-sm text-headline-sm text-amber-900 font-extrabold">品秋味 食秋果</span>
            </div>
            <p class="font-body-sm text-[11px] text-amber-800 font-bold mb-2">莱阳秋月梨 15.99元/约700g</p>
            <div class="w-full aspect-square rounded-lg overflow-hidden relative mb-2">
              <img class="w-full h-full object-cover" data-alt="Fresh sliced golden autumn pear with juicy transparent crystalline flesh on warm kitchen wooden board" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBqaksyEFDxppY3K-V7yWXmVqoS3hEFECHbNyKffO8r6oyXa3yW-HkUu98ON-NzXZ4yzvEumlybYCCnLJ8VYZFLTXhL01fzVxrv1W007YZCt4XiXu2wK-GNmYJuDq4z2_luH0NK81V2WE3p_5iG0jri3tiy_xMFx13VDzjUxZCKSCRwlx79tzQeW2oNDvVH7tLwGcLHhY0M2UPxSf9LtbVWprt2nXVJpe4Y9TWbuP77RP2Yx5LYNr2p" />
              <div class="absolute bottom-1 left-1 bg-surface-container-lowest/90 px-1.5 py-0.5 rounded text-[10px] text-amber-900 font-bold">
                梨子届中的“白富美”
              </div>
            </div>
          </div>
          <button class="w-full bg-secondary-container text-on-secondary-container font-headline-sm text-[13px] py-1.5 rounded-full font-bold shadow hover:brightness-105 transition-all flex items-center justify-center gap-1" @click="$router.push('/category')">
            立即抢购
            <span class="material-symbols-outlined text-[14px]">arrow_forward</span>
          </button>
        </div>

        <!-- 商品 2..N (接口数据绑定) -->
        <div
          v-for="product in products"
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
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { searchProducts, getCategoryList, getHomeCategories } from '@/api'
import { getToken } from '@/utils/auth'
import { useCartStore } from '@/stores/cart'
import { showToast } from 'vant'

const router = useRouter()
const cartStore = useCartStore()
const products = ref([])
const categories = ref([])
const homeCategories = ref([])
const loading = ref(true)

function formatPrice(price) {
  return Number(price).toFixed(2)
}

// 商品缺图时的占位图（本地静态资源，避免外链不可达）
const PLACEHOLDER_IMG = '/placeholder.svg'

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

const banners = [
  { id: 1, title: '新品上市', sub: '精选好物 限时优惠', tag: 'NEW', bg: 'linear-gradient(135deg, #0071DC 0%, #0056B3 100%)' },
  { id: 2, title: '限时特惠', sub: '每日精选 好价不停', tag: 'HOT', bg: 'linear-gradient(135deg, #1D1D1F 0%, #3A3A3C 100%)' },
  { id: 3, title: '生鲜直达', sub: '产地直采 新鲜到家', tag: '', bg: 'linear-gradient(135deg, #34C759 0%, #28A745 100%)' },
]

const iconConfig = [
  { color: '#FF6B35' }, { color: '#34C759' }, { color: '#FF9500' }, { color: '#FF3B30' },
  { color: '#8B572A' }, { color: '#5856D6' }, { color: '#FF2D55' }, { color: '#0071DC' },
  { color: '#AF52DE' }, { color: '#FF69B4' }, { color: '#FFD700' }, { color: '#00BCD4' },
  { color: '#795548' }, { color: '#607D8B' }, { color: '#E91E63' }, { color: '#FF9800' },
  { color: '#9C27B0' }, { color: '#009688' }, { color: '#795548' }, { color: '#455A64' },
  { color: '#F44336' },
]

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
  { name: '潮玩萌宠', color: '#FF69B4' },
  { name: '粽情端午', color: '#FFD700' },
  { name: '全球好物', color: '#00BCD4' },
  { name: '家居百货', color: '#795548' },
  { name: '床品家纺', color: '#607D8B' },
  { name: '服饰鞋帽', color: '#E91E63' },
  { name: '家电', color: '#FF9800' },
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
    return categories.value.slice(0, 8).map((c, i) => ({
      ...c,
      color: iconConfig[i % iconConfig.length].color
    }))
  }
  return defaultCategories.slice(0, 8)
})

// 金刚区 10 宫格（设计稿为 10 个位置）
const homeGridCategories = computed(() => {
  const list = displayCategories.value.slice(0, 10)
  if (list.length >= 10) return list
  const fill = defaultCategories.slice(list.length, 10).map(c => ({ ...c }))
  return [...list, ...fill]
})

function goCategory(cat) {
  if (cat.id) {
    router.push({ path: '/category', query: { categoryId: cat.id, name: cat.categoryName } })
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
  try {
    const [catRes, prodRes, homeCatRes] = await Promise.all([
      getCategoryList().catch(() => ({ data: [] })),
      searchProducts({ pageNum: 1, pageSize: 20 }).catch(() => ({ data: { records: [] } })),
      getHomeCategories().catch(() => ({ data: [] }))
    ])
    categories.value = catRes.data || []
    products.value = prodRes.data?.records || []
    homeCategories.value = homeCatRes.data || []
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
})
</script>
