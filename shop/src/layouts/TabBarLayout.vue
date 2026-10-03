<template>
  <div class="flex flex-col min-h-screen bg-background">
    <div class="flex-1">
      <router-view v-slot="{ Component, route: r }">
        <keep-alive :include="aliveNames">
          <component :is="Component" :key="r.path" />
        </keep-alive>
      </router-view>
    </div>

    <!-- 底部导航（外观严格取自 Stitch 设计稿，逻辑沿用原实现） -->
    <nav class="fixed bottom-0 w-full z-50 pb-safe bg-surface-container-lowest/90 backdrop-blur-xl shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
      <div class="flex justify-around items-center h-14 max-w-[540px] mx-auto px-margin">
        <a
          :aria-current="activeTab === 0 ? 'page' : undefined"
          :class="[
            'flex flex-col items-center justify-center min-w-[44px] min-h-[44px] transition-colors',
            activeTab === 0 ? 'text-primary-container' : 'text-on-surface-variant hover:text-primary-container'
          ]"
          href="javascript:;"
          @click="onTabChange(0)"
        >
          <span class="material-symbols-outlined text-[24px]">home</span>
          <span class="font-tab-label text-tab-label mt-0.5">首页</span>
        </a>

        <a
          :aria-current="activeTab === 1 ? 'page' : undefined"
          :class="[
            'flex flex-col items-center justify-center min-w-[44px] min-h-[44px] transition-colors',
            activeTab === 1 ? 'text-primary-container' : 'text-on-surface-variant hover:text-primary-container'
          ]"
          href="javascript:;"
          @click="onTabChange(1)"
        >
          <span class="material-symbols-outlined text-[24px]">grid_view</span>
          <span class="font-tab-label text-tab-label mt-0.5">分类</span>
        </a>

        <a
          :aria-current="activeTab === 2 ? 'page' : undefined"
          :class="[
            'flex flex-col items-center justify-center min-w-[44px] min-h-[44px] transition-colors relative',
            activeTab === 2 ? 'text-primary-container' : 'text-on-surface-variant hover:text-primary-container'
          ]"
          href="javascript:;"
          @click="onTabChange(2)"
        >
          <div class="relative flex items-center justify-center">
            <span class="material-symbols-outlined text-[24px]">shopping_cart</span>
            <span
              v-if="cartCount > 0"
              class="absolute -top-1 -right-2 bg-tertiary-container text-on-tertiary font-badge-label text-badge-label px-1.5 py-0.2 rounded-full min-w-[16px] h-4 flex items-center justify-center"
            >{{ cartCount > 99 ? '99+' : cartCount }}</span>
          </div>
          <span class="font-tab-label text-tab-label mt-0.5">购物车</span>
        </a>

        <a
          :aria-current="activeTab === 3 ? 'page' : undefined"
          :class="[
            'flex flex-col items-center justify-center min-w-[44px] min-h-[44px] transition-colors',
            activeTab === 3 ? 'text-primary-container' : 'text-on-surface-variant hover:text-primary-container'
          ]"
          href="javascript:;"
          @click="onTabChange(3)"
        >
          <span class="material-symbols-outlined text-[24px]">person</span>
          <span class="font-tab-label text-tab-label mt-0.5">我的</span>
        </a>
      </div>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const cartCount = computed(() => cartStore.totalCount)

const tabPaths = ['/', '/category', '/cart', '/profile']
const aliveNames = ['Home', 'Category', 'Cart', 'Profile']
const activeTab = ref(0)

// 路由变化时同步 tab 高亮
watch(() => route.path, (path) => {
  const idx = tabPaths.indexOf(path)
  if (idx !== -1) activeTab.value = idx
}, { immediate: true })

// 点击 tab 时跳转路由
function onTabChange(index) {
  const target = tabPaths[index]
  if (target && route.path !== target) {
    router.push(target)
  }
}
</script>
