<template>
  <div class="layout-container">
    <!-- 移动端遮罩 -->
    <div
      v-if="isMobile && !isCollapse"
      class="sidebar-overlay"
      @click="closeMobileSidebar"
    ></div>

    <!-- 侧边栏 -->
    <aside
      class="sidebar"
      :class="{
        collapsed: isCollapse,
        'mobile-open': isMobile && !isCollapse,
        'mobile-hidden': isMobile && isCollapse
      }"
    >
      <div class="logo-container">
        <img src="https://www.walmart.com/favicon.ico" alt="Walmart" class="logo" />
        <span v-if="!isCollapse" class="logo-text">沃尔玛OMS</span>
        <!-- 移动端关闭按钮 -->
        <button v-if="isMobile" class="sidebar-close" @click="closeMobileSidebar">
          <el-icon><Close /></el-icon>
        </button>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapse"
        background-color="#0071dc"
        text-color="#ffffff"
        active-text-color="#ffffff"
        unique-opened
        router
        @select="handleMenuSelect"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title><span>首页</span></template>
        </el-menu-item>
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/menu">菜单管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/product">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/product/category">商品分类</el-menu-item>
          <el-menu-item index="/product/list">商品列表</el-menu-item>
          <el-menu-item index="/product/brand">品牌管理</el-menu-item>
          <el-menu-item index="/product/category-icon">分类图标管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/inventory">
          <template #title>
            <el-icon><Collection /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/inventory/list">库存查询</el-menu-item>
          <el-menu-item index="/inventory/warehouse">仓库管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/member">
          <template #title>
            <el-icon><User /></el-icon>
            <span>会员管理</span>
          </template>
          <el-menu-item index="/member/list">会员管理</el-menu-item>
          <el-menu-item index="/member/level">会员等级</el-menu-item>
          <el-menu-item index="/coupon">优惠券管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/order">
          <template #title>
            <el-icon><Tickets /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order/list">订单列表</el-menu-item>
          <el-menu-item index="/order/detail">订单详情</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/report">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>销售报表</span>
          </template>
          <el-menu-item index="/report/index">销售报表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-wrapper" :class="{ 'sidebar-collapsed': isCollapse && !isMobile }">
      <!-- 顶部栏 - 玻璃拟态 -->
      <header class="header">
        <div class="header-left">
          <!-- 桌面端折叠按钮 -->
          <button class="menu-toggle" @click="toggleSidebar" aria-label="切换菜单">
            <el-icon v-if="!isCollapse || isMobile"><Menu /></el-icon>
            <el-icon v-else><Expand /></el-icon>
          </button>
        </div>
        <div class="header-center" v-if="!isMobile">
          <el-input
            v-model="searchQuery"
            placeholder="搜索订单、商品、会员..."
            prefix-icon="Search"
            class="search-input"
            clearable
          />
        </div>
        <div class="header-right">
          <!-- 移动端搜索按钮 -->
          <button v-if="isMobile" class="header-icon-btn" aria-label="搜索">
            <el-icon><Search /></el-icon>
          </button>
          <!-- 通知 -->
          <el-dropdown trigger="click" class="header-dropdown">
            <button class="header-icon-btn notification-btn" aria-label="通知">
              <el-icon><Bell /></el-icon>
              <span class="notification-dot"></span>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>通知1</el-dropdown-item>
                <el-dropdown-item>通知2</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <!-- 用户菜单 -->
          <el-dropdown class="header-dropdown" @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userAvatar" class="user-avatar" />
              <span class="user-name" v-if="!isMobile">{{ userStore.userInfo?.nickname || '管理员' }}</span>
              <el-icon v-if="!isMobile"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import {
  HomeFilled,
  Setting,
  Goods,
  Collection,
  User,
  Tickets,
  DataAnalysis,
  Menu,
  Expand,
  ArrowDown,
  Bell,
  Search,
  Close
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)
const searchQuery = ref('')
const isMobile = ref(false)
const userAvatar = ref('')

const activeMenu = computed(() => route.path)

// 响应式检测
const checkMobile = () => {
  isMobile.value = window.innerWidth < 1024
  if (isMobile.value) {
    isCollapse.value = true
  }
}

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const closeMobileSidebar = () => {
  if (isMobile.value) {
    isCollapse.value = true
  }
}

// 移动端点击菜单项后自动关闭侧边栏
const handleMenuSelect = () => {
  if (isMobile.value) {
    isCollapse.value = true
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const handleUserCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/profile')
    return
  }
  if (command === 'settings') {
    router.push('/settings')
    return
  }
  if (command === 'logout') {
    handleLogout()
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

/* ============================
   侧边栏
   ============================ */
.sidebar {
  width: var(--wm-sidebar-width);
  background: var(--wm-bg-sidebar);
  color: var(--wm-text-inverse);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease, transform 0.3s ease;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
  z-index: var(--wm-z-overlay);
}

.sidebar.collapsed {
  width: var(--wm-sidebar-collapsed);
}

.logo-container {
  display: flex;
  align-items: center;
  padding: var(--wm-space-md) var(--wm-space-lg);
  height: var(--wm-header-height);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  gap: var(--wm-space-sm);
}

.logo {
  width: 32px;
  height: 32px;
  border-radius: var(--wm-radius-sm);
}

.logo-text {
  font-size: var(--wm-text-base);
  font-weight: 700;
  color: var(--wm-text-inverse);
  white-space: nowrap;
}

.sidebar-close {
  margin-left: auto;
  background: none;
  border: none;
  color: var(--wm-text-inverse);
  cursor: pointer;
  padding: var(--wm-space-sm);
  border-radius: var(--wm-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background var(--wm-transition-fast);
  min-width: 44px;
  min-height: 44px;
}

.sidebar-close:hover {
  background: rgba(255, 255, 255, 0.15);
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: var(--wm-sidebar-width);
}

/* ============================
   移动端侧边栏覆盖层
   ============================ */
.sidebar-overlay {
  position: fixed;
  inset: 0;
  background: var(--wm-bg-overlay);
  z-index: calc(var(--wm-z-overlay) - 1);
  animation: fadeIn 0.25s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 移动端侧边栏从左侧滑出 */
@media screen and (max-width: 1024px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    width: 280px;
  }

  .sidebar.mobile-open {
    transform: translateX(0);
    animation: slideIn 0.3s ease;
  }

  .sidebar.mobile-hidden {
    transform: translateX(-100%);
  }

  .sidebar.collapsed {
    width: 280px;
    transform: translateX(-100%);
  }

  @keyframes slideIn {
    from { transform: translateX(-100%); }
    to { transform: translateX(0); }
  }
}

/* ============================
   主内容区
   ============================ */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: margin-left 0.3s ease;
}

/* ============================
   顶部栏 - 玻璃拟态
   ============================ */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(--wm-space-lg);
  height: var(--wm-header-height);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  flex-shrink: 0;
  z-index: var(--wm-z-sticky);
}

.header-left {
  display: flex;
  align-items: center;
}

.menu-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--wm-primary);
  padding: var(--wm-space-sm);
  border-radius: var(--wm-radius-sm);
  transition: all var(--wm-transition-fast);
  min-width: 44px;
  min-height: 44px;
  font-size: 20px;
}

.menu-toggle:hover {
  background: var(--wm-primary-light);
  color: var(--wm-primary-hover);
}

.header-center {
  flex: 1;
  max-width: 420px;
  margin: 0 var(--wm-space-lg);
}

.search-input {
  border-radius: var(--wm-radius-full);
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--wm-radius-full);
  background: var(--wm-bg-page);
  box-shadow: 0 0 0 1px var(--wm-border);
  transition: all var(--wm-transition-fast);
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--wm-primary);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--wm-primary-glow), 0 0 0 1px var(--wm-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--wm-space-sm);
}

.header-icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--wm-text-secondary);
  padding: var(--wm-space-sm);
  border-radius: var(--wm-radius-sm);
  transition: all var(--wm-transition-fast);
  min-width: 44px;
  min-height: 44px;
  font-size: 20px;
  position: relative;
}

.header-icon-btn:hover {
  background: var(--wm-primary-light);
  color: var(--wm-primary);
}

.notification-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 8px;
  height: 8px;
  background: var(--wm-danger);
  border-radius: 50%;
  border: 2px solid white;
}

.header-dropdown {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--wm-space-sm);
  cursor: pointer;
  padding: var(--wm-space-xs) var(--wm-space-md);
  border-radius: var(--wm-radius-full);
  transition: all var(--wm-transition-fast);
}

.user-info:hover {
  background: var(--wm-primary-light);
}

.user-avatar {
  border: 2px solid var(--wm-primary);
}

.user-name {
  font-size: var(--wm-text-sm);
  font-weight: 500;
  color: var(--wm-text-primary);
  white-space: nowrap;
}

/* ============================
   内容区
   ============================ */
.main-content {
  flex: 1;
  padding: var(--wm-space-lg);
  background: var(--wm-bg-page);
  overflow-y: auto;
  overflow-x: hidden;
}

/* ============================
   响应式：平板端
   ============================ */
@media screen and (max-width: 1024px) {
  .header {
    padding: 0 var(--wm-space-md);
  }

  .main-content {
    padding: var(--wm-space-md);
  }
}

/* ============================
   响应式：移动端
   ============================ */
@media screen and (max-width: 768px) {
  .header {
    padding: 0 var(--wm-space-md);
    height: 56px;
  }

  .logo-container {
    height: 56px;
  }

  .main-content {
    padding: var(--wm-space-sm);
  }

  .user-info {
    padding: var(--wm-space-xs);
  }
}

/* ============================
   过渡动画
   ============================ */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
