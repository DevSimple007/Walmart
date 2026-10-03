<template>
  <div class="account-page">
    <div class="account-container">
      <h1 class="page-title">{{ $t('account.title') }}</h1>

      <div class="account-content">
        <!-- Sidebar -->
        <div class="account-sidebar">
          <div class="user-info">
            <div class="avatar">
              <el-icon size="32"><User /></el-icon>
            </div>
            <div class="user-details">
              <span class="user-name">{{ userStore.userInfo?.memberName || $t('account.guest') }}</span>
              <span class="user-email">{{ userStore.userInfo?.phone || '' }}</span>
            </div>
          </div>
          <nav class="sidebar-nav">
            <router-link to="/account" class="nav-item active">
              <el-icon><User /></el-icon>
              <span>{{ $t('account.title') }}</span>
            </router-link>
            <router-link to="/orders" class="nav-item">
              <el-icon><List /></el-icon>
              <span>{{ $t('account.purchaseHistory') }}</span>
            </router-link>
            <router-link to="/account/addresses" class="nav-item">
              <el-icon><Location /></el-icon>
              <span>{{ $t('account.addresses') }}</span>
            </router-link>
            <router-link to="/account/favorites" class="nav-item">
              <el-icon><Star /></el-icon>
              <span>{{ $t('account.savedItems') }}</span>
            </router-link>
            <router-link to="/account/coupons" class="nav-item">
              <el-icon><Ticket /></el-icon>
              <span>{{ $t('account.coupons') }}</span>
            </router-link>
            <button class="nav-item logout" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>{{ $t('account.signOut') }}</span>
            </button>
          </nav>
        </div>

        <!-- Main Content -->
        <div class="account-main">
          <!-- Quick Actions -->
          <div class="quick-actions">
            <h2>{{ $t('account.quickActions') }}</h2>
            <div class="actions-grid">
              <router-link to="/orders" class="action-card">
                <el-icon size="24"><List /></el-icon>
                <span>{{ $t('account.purchaseHistory') }}</span>
              </router-link>
              <router-link to="/account/addresses" class="action-card">
                <el-icon size="24"><Location /></el-icon>
                <span>{{ $t('account.addresses') }}</span>
              </router-link>
              <router-link to="/account/favorites" class="action-card">
                <el-icon size="24"><Heart /></el-icon>
                <span>{{ $t('account.savedItems') }}</span>
              </router-link>
              <router-link to="/account/coupons" class="action-card">
                <el-icon size="24"><Ticket /></el-icon>
                <span>{{ $t('account.coupons') }}</span>
              </router-link>
            </div>
          </div>

          <!-- Recent Orders -->
          <div class="recent-orders">
            <div class="section-header">
              <h2>{{ $t('account.recentOrders') }}</h2>
              <router-link to="/orders" class="view-all">{{ $t('account.viewAll') }}</router-link>
            </div>
            <div class="orders-list" v-if="recentOrders.length">
              <div v-for="order in recentOrders" :key="order.orderNo" class="order-item">
                <div class="order-info">
                  <span class="order-number">{{ $t('orders.orderNo', { no: order.orderNo }) }}</span>
                  <span class="order-date">{{ formatDate(order.createTime) }}</span>
                </div>
                <div class="order-status" :class="statusClass(order.status)">
                  {{ $t('orders.status.' + order.status) }}
                </div>
                <div class="order-total">${{ order.totalAmount?.toFixed(2) }}</div>
                <router-link :to="`/orders?orderNo=${order.orderNo}`" class="order-link">
                  {{ $t('account.viewDetails') }}
                </router-link>
              </div>
            </div>
            <div class="empty-state" v-else>
              <p>{{ $t('account.noRecentOrders') }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { getOrders } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, List, Location, Star, Ticket, SwitchButton } from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const recentOrders = ref([])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

// 复用现有样式类（PENDING/PAID/SHIPPED/DELIVERED/CANCELLED）
function statusClass(status) {
  const map = { 0: 'PENDING', 1: 'PAID', 2: 'PAID', 3: 'SHIPPED', 4: 'DELIVERED', 5: 'CANCELLED' }
  return map[status] ?? status
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm(
      t('account.signOutConfirm'),
      t('account.signOutTitle'),
      {
        confirmButtonText: t('account.signOut'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    userStore.logout()
    ElMessage.success(t('account.signedOut'))
    router.push('/')
  } catch {
    // Cancelled
  }
}

onMounted(async () => {
  try {
    const res = await getOrders({ pageNum: 1, pageSize: 3 })
    recentOrders.value = res.data?.records || []
  } catch (e) {
    console.error('Failed to load orders', e)
  }
})
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-blue-dark: #004f9a;
$walmart-yellow: #ffc220;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-300: #d1d1d1;
$walmart-gray-400: #767676;
$walmart-gray-500: #46474a;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;

.account-page {
  background: $walmart-gray-100;
  min-height: 100vh;
  padding: 24px 0;
}

.account-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: $walmart-gray-600;
  margin-bottom: 24px;
}

.account-content {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
}

// Sidebar
.account-sidebar {
  background: $walmart-white;
  border-radius: 12px;
  overflow: hidden;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-bottom: 1px solid $walmart-gray-200;

  .avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: $walmart-blue;
    color: $walmart-white;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .user-details {
    display: flex;
    flex-direction: column;

    .user-name {
      font-size: 16px;
      font-weight: 600;
      color: $walmart-gray-600;
    }

    .user-email {
      font-size: 13px;
      color: $walmart-gray-400;
    }
  }
}

.sidebar-nav {
  padding: 8px;

  .nav-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    color: $walmart-gray-500;
    text-decoration: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s;
    border: none;
    background: none;
    width: 100%;
    cursor: pointer;
    text-align: left;

    &:hover {
      background: $walmart-gray-100;
      color: $walmart-gray-600;
    }

    &.active {
      background: rgba($walmart-blue, 0.1);
      color: $walmart-blue;
    }

    &.logout {
      margin-top: 8px;
      border-top: 1px solid $walmart-gray-200;

      &:hover {
        color: #e31837;
      }
    }
  }
}

// Main Content
.account-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

// Quick Actions
.quick-actions {
  background: $walmart-white;
  border-radius: 12px;
  padding: 24px;

  h2 {
    font-size: 18px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 16px;
  }
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  background: $walmart-gray-100;
  border-radius: 12px;
  text-decoration: none;
  color: $walmart-gray-600;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s;

  &:hover {
    background: $walmart-blue;
    color: $walmart-white;
  }
}

// Recent Orders
.recent-orders {
  background: $walmart-white;
  border-radius: 12px;
  padding: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  h2 {
    font-size: 18px;
    font-weight: 700;
    color: $walmart-gray-600;
  }

  .view-all {
    color: $walmart-blue;
    font-size: 14px;
    font-weight: 600;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.orders-list {
  display: flex;
  flex-direction: column;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid $walmart-gray-100;

  &:last-child {
    border-bottom: none;
  }

  .order-info {
    flex: 1;
    display: flex;
    flex-direction: column;

    .order-number {
      font-size: 14px;
      font-weight: 600;
      color: $walmart-gray-600;
    }

    .order-date {
      font-size: 13px;
      color: $walmart-gray-400;
    }
  }

  .order-status {
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;

    &.PENDING {
      background: #fff3cd;
      color: #856404;
    }

    &.PAID {
      background: #d4edda;
      color: #155724;
    }

    &.SHIPPED {
      background: #cce5ff;
      color: #004085;
    }

    &.DELIVERED {
      background: #d4edda;
      color: #155724;
    }

    &.CANCELLED {
      background: #f8d7da;
      color: #721c24;
    }
  }

  .order-total {
    font-size: 14px;
    font-weight: 600;
    color: $walmart-gray-600;
  }

  .order-link {
    color: $walmart-blue;
    font-size: 13px;
    text-decoration: none;
    font-weight: 600;

    &:hover {
      text-decoration: underline;
    }
  }
}

.empty-state {
  text-align: center;
  padding: 40px;

  p {
    color: $walmart-gray-400;
    font-size: 14px;
  }
}
</style>
