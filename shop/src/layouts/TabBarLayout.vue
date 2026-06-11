<template>
  <div class="tab-bar-layout">
    <div class="page-content">
      <router-view v-slot="{ Component, route: r }">
        <keep-alive :include="aliveNames">
          <component :is="Component" :key="r.path" />
        </keep-alive>
      </router-view>
    </div>
    <van-tabbar v-model="activeTab" @change="onTabChange">
      <van-tabbar-item>
        <template #icon="{ active }">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" :stroke="active ? '#0071DC' : '#86868B'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
        </template>
        首页
      </van-tabbar-item>
      <van-tabbar-item>
        <template #icon="{ active }">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" :stroke="active ? '#0071DC' : '#86868B'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="7" height="7" rx="1.5"/>
            <rect x="14" y="3" width="7" height="7" rx="1.5"/>
            <rect x="3" y="14" width="7" height="7" rx="1.5"/>
            <rect x="14" y="14" width="7" height="7" rx="1.5"/>
          </svg>
        </template>
        分类
      </van-tabbar-item>
      <van-tabbar-item>
        <template #icon="{ active }">
          <div class="tab-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" :stroke="active ? '#0071DC' : '#86868B'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="9" cy="21" r="1"/>
              <circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            <span v-if="cartCount > 0" class="tab-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
          </div>
        </template>
        购物车
      </van-tabbar-item>
      <van-tabbar-item>
        <template #icon="{ active }">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" :stroke="active ? '#0071DC' : '#86868B'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
        </template>
        我的
      </van-tabbar-item>
    </van-tabbar>
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

<style lang="scss" scoped>
.tab-bar-layout {
  min-height: 100vh;
  background: var(--shop-bg-secondary);
}

.page-content {
  padding-bottom: 50px;
}

.tab-icon-wrap {
  position: relative;
}

.tab-badge {
  position: absolute;
  top: -6px;
  right: -10px;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  font-size: 10px;
  font-weight: 600;
  color: #fff;
  background: var(--shop-accent);
  border-radius: 8px;
  padding: 0 4px;
}
</style>
