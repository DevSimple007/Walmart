<template>
  <div class="profile-page">
    <!-- User header -->
    <div class="user-header">
      <div v-if="userStore.userInfo" class="user-info">
        <div class="avatar">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
        </div>
        <div class="user-detail">
          <div class="username">{{ userStore.userInfo.memberName || '用户' }}</div>
          <div class="user-meta">
            <span v-if="userStore.userInfo.levelId" class="level-badge">Lv.{{ userStore.userInfo.levelId }}</span>
            <span>{{ userStore.userInfo.points || 0 }} 积分</span>
          </div>
        </div>
      </div>
      <div v-else class="user-info" @click="$router.push('/login')">
        <div class="avatar">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
        </div>
        <div class="user-detail">
          <div class="username">登录 / 注册</div>
          <div class="user-meta">点击登录享受更多优惠</div>
        </div>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.6)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </div>
    </div>

    <!-- Asset card -->
    <div class="asset-card" v-if="userStore.userInfo">
      <div class="asset-item">
        <div class="asset-num">{{ userStore.userInfo.balance || 0 }}</div>
        <div class="asset-label">余额</div>
      </div>
      <div class="asset-divider"></div>
      <div class="asset-item">
        <div class="asset-num">{{ userStore.userInfo.points || 0 }}</div>
        <div class="asset-label">积分</div>
      </div>
      <div class="asset-divider"></div>
      <div class="asset-item">
        <div class="asset-num">{{ userStore.userInfo.levelId || 1 }}</div>
        <div class="asset-label">等级</div>
      </div>
    </div>

    <!-- 会员等级 -->
    <div class="section-block" v-if="userStore.userInfo">
      <div class="section-block-header">
        <span class="section-block-title">会员等级</span>
      </div>
      <div class="level-info">
        <div class="level-current">
          <div class="level-badge" :style="{ background: levelColor }">
            {{ levelName }}
          </div>
          <div class="level-detail">
            <div class="level-points">{{ userStore.userInfo.points || 0 }} 积分</div>
            <div class="level-next" v-if="nextLevel && (userStore.userInfo.points || 0) < nextLevel.minPoints">{{ nextLevel.levelName }} 还需 {{ nextLevel.minPoints - (userStore.userInfo.points || 0) }} 积分</div>
            <div class="level-next" v-else-if="nextLevel">已达{{ nextLevel.levelName }}门槛</div>
            <div class="level-next" v-else>已是最高等级</div>
          </div>
        </div>
        <div class="level-progress" v-if="nextLevel">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
          </div>
          <div class="progress-text">{{ userStore.userInfo.points || 0 }} / {{ nextLevel.minPoints }}</div>
        </div>
      </div>
    </div>

    <!-- Orders -->
    <div class="section-block">
      <div class="section-block-header">
        <span class="section-block-title">我的订单</span>
        <span class="section-block-link" @click="$router.push('/order')">全部订单</span>
      </div>
      <div class="order-grid">
        <div class="order-item" v-for="item in orderItems" :key="item.label" @click="goOrder(item.status)">
          <div class="order-icon" :style="{ background: item.bg }">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" :stroke="item.color" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <component :is="item.iconSvg" />
            </svg>
          </div>
          <span class="order-label">{{ item.label }}</span>
        </div>
      </div>
    </div>

    <!-- Menu list -->
    <div class="section-block menu-list">
      <div
        class="menu-item"
        v-for="item in menuItems"
        :key="item.label"
        @click="item.action ? item.action() : goPage(item.path)"
      >
        <div class="menu-icon-wrap">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0071DC" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <component :is="item.iconSvg" />
          </svg>
        </div>
        <span class="menu-label">{{ item.label }}</span>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ABABAB" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </div>
    </div>

    <!-- Logout -->
    <div v-if="userStore.userInfo" class="logout-section">
      <button class="logout-btn" @click="handleLogout">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
          <polyline points="16 17 21 12 16 7"/>
          <line x1="21" y1="12" x2="9" y2="12"/>
        </svg>
        <span>退出登录</span>
      </button>
    </div>
  </div>
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

<style lang="scss" scoped>
.user-header {
  background: linear-gradient(135deg, #1D1D1F 0%, #3A3A3C 100%);
  padding: 40px 16px 28px;
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.username {
  font-size: 20px;
  font-weight: 700;
}

.user-meta {
  font-size: 13px;
  opacity: 0.8;
  margin-top: 4px;
  display: flex;
  gap: 10px;
}

.level-badge {
  background: rgba(255, 255, 255, 0.25);
  padding: 1px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.asset-card {
  display: flex;
  align-items: center;
  background: var(--shop-card-bg);
  margin: -16px 16px 0;
  border-radius: var(--shop-card-radius);
  padding: 20px 0;
  position: relative;
  z-index: 1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.asset-item {
  flex: 1;
  text-align: center;
}

.asset-num {
  font-size: 20px;
  font-weight: 700;
  color: var(--shop-text-primary);
}

.asset-label {
  font-size: 12px;
  color: var(--shop-text-secondary);
  margin-top: 4px;
}

.asset-divider {
  width: 1px;
  height: 32px;
  background: var(--shop-separator);
}

.level-info {
  .level-current {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
  }

  .level-badge {
    padding: 4px 12px;
    border-radius: 12px;
    color: #fff;
    font-size: 13px;
    font-weight: 600;
    white-space: nowrap;
  }

  .level-detail {
    flex: 1;
  }

  .level-points {
    font-size: 16px;
    font-weight: 700;
    color: var(--shop-text-primary);
  }

  .level-next {
    font-size: 12px;
    color: var(--shop-text-secondary);
    margin-top: 2px;
  }

  .progress-bar {
    height: 6px;
    background: var(--shop-bg-secondary);
    border-radius: 3px;
    overflow: hidden;
  }

  .progress-fill {
    height: 100%;
    background: var(--shop-accent);
    border-radius: 3px;
    transition: width 0.3s ease;
  }

  .progress-text {
    font-size: 11px;
    color: var(--shop-text-tertiary);
    text-align: right;
    margin-top: 4px;
  }
}

.section-block {
  background: var(--shop-card-bg);
  margin: 12px 16px 0;
  border-radius: var(--shop-card-radius);
  padding: 16px;
}

.section-block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-block-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--shop-text-primary);
}

.section-block-link {
  font-size: 13px;
  color: var(--shop-accent);
  cursor: pointer;
  font-weight: 500;
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  text-align: center;
}

.order-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 0;

  &:active {
    opacity: 0.7;
  }
}

.order-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.order-label {
  font-size: 12px;
  color: var(--shop-text-secondary);
  font-weight: 500;
}

.menu-list {
  padding: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 0.5px solid var(--shop-separator);

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: var(--shop-bg-secondary);
  }
}

.menu-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--shop-accent-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.menu-label {
  flex: 1;
  font-size: 14px;
  color: var(--shop-text-primary);
}

.logout-section {
  padding: 24px 16px 40px;
}

.logout-btn {
  width: 100%;
  height: 50px;
  border: 1.5px solid #FFD4D4;
  border-radius: 14px;
  background: linear-gradient(180deg, #FFF5F5 0%, #FFF 100%);
  color: #FF3B30;
  font-size: 15px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s ease;

  &:active {
    background: #FFECEC;
    transform: scale(0.98);
  }

  svg {
    flex-shrink: 0;
  }
}
</style>
