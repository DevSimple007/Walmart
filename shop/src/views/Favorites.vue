<template>
  <div class="favorites-page">
    <van-nav-bar title="我的收藏" left-arrow @click-left="$router.back()" />

    <div v-if="favorites.length" class="favorites-list">
      <div
        class="product-card"
        v-for="item in favorites"
        :key="item.id"
        @click="$router.push(`/product/${item.productId}`)"
      >
        <div class="product-img">
          <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName" />
          <svg v-else width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#D1D1D6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            <circle cx="8.5" cy="8.5" r="1.5"/>
            <polyline points="21 15 16 10 5 21"/>
          </svg>
        </div>
        <div class="product-info">
          <div class="product-name text-ellipsis-2">{{ item.productName }}</div>
          <div class="product-price">
            <span class="unit">¥</span>{{ formatPrice(item.price) }}
          </div>
        </div>
        <button class="fav-btn" @click.stop="removeFav(item.productId)">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="#FF3B30" stroke="#FF3B30" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
        </svg>
      </div>
      <p>暂无收藏商品</p>
      <button class="shop-btn-plain" style="border-radius:12px;height:44px;padding:0 28px;border:1.5px solid rgba(0,0,0,0.06);background:transparent;font-weight:600;font-size:15px;cursor:pointer;font-family:var(--shop-font);" @click="$router.push('/')">去逛逛</button>
    </div>

    <van-loading v-if="loading" class="page-loading" size="24px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavorites, removeFavorite } from '@/api'
import { showConfirmDialog, showToast } from 'vant'

const favorites = ref([])
const loading = ref(true)

function formatPrice(price) { return Number(price || 0).toFixed(2) }

async function loadFavorites() {
  loading.value = true
  try {
    const res = await getFavorites()
    favorites.value = res.data?.records || res.data || []
  } catch (e) {
    console.error('加载收藏失败', e)
  } finally {
    loading.value = false
  }
}

async function removeFav(productId) {
  try {
    await showConfirmDialog({ title: '提示', message: '确定取消收藏吗？' })
    await removeFavorite(productId)
    favorites.value = favorites.value.filter(f => f.productId !== productId)
    showToast('已取消收藏')
  } catch {}
}

onMounted(loadFavorites)
</script>

<style lang="scss" scoped>
.favorites-page {
  background: var(--shop-bg-secondary);
  min-height: 100vh;
}

.favorites-list {
  padding: var(--shop-gap) var(--shop-page-pad);
}

.product-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--shop-card-bg);
  border-radius: var(--shop-card-radius);
  padding: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  position: relative;
}

.product-img {
  width: 80px;
  height: 80px;
  background: var(--shop-bg-secondary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  color: var(--shop-text-primary);
  margin-bottom: 8px;
  line-height: 1.4;
}

.product-price {
  color: var(--shop-danger);
  font-weight: 700;
  font-size: 16px;

  .unit {
    font-size: 12px;
  }
}

.fav-btn {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-loading {
  text-align: center;
  padding: 40px;
}

.shop-btn-plain {
  height: 44px;
  padding: 0 28px;
  border: 1.5px solid var(--shop-separator);
  border-radius: 10px;
  background: transparent;
  color: var(--shop-text-primary);
  font-size: 15px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;
}
</style>
