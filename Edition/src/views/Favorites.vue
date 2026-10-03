<template>
  <div class="favorites-page">
    <div class="favorites-container">
      <h1 class="page-title">{{ $t('favorites.title') }}</h1>

      <div class="products-grid" v-if="favorites.length">
        <div v-for="product in favorites" :key="product.id" class="product-card" @click="$router.push(`/ip/${product.productId}`)">
          <div class="product-image">
            <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
            <button class="remove-btn" @click.stop="removeFavorite(product.productId)">
              <el-icon><Close /></el-icon>
            </button>
          </div>
          <div class="product-info">
            <div class="product-pricing">
              <span class="now-price"><span class="dollar">$</span>{{ formatPrice(product.price) }}</span>
            </div>
            <p class="product-name">{{ product.productName }}</p>
            <el-button type="primary" size="small" @click.stop="addToCart(product)">{{ $t('favorites.addToCart') }}</el-button>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <el-icon size="64" color="#d1d1d1"><Star /></el-icon>
        <h2>{{ $t('favorites.noSaved') }}</h2>
        <p>{{ $t('favorites.itemsAppear') }}</p>
        <el-button type="primary" @click="$router.push('/')">{{ $t('favorites.startShopping') }}</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { getFavorites, removeFavorite as removeFav } from '@/api'
import { ElMessage } from 'element-plus'
import { Star, Close } from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const cartStore = useCartStore()
const favorites = ref([])

function formatPrice(price) { return Number(price).toFixed(2) }

async function loadFavorites() {
  try { const res = await getFavorites(); favorites.value = res.data || [] } catch {}
}

async function removeFavorite(id) {
  try { await removeFav(id); ElMessage.success(t('favorites.removed')); loadFavorites() } catch {}
}

async function addToCart(product) {
  // 收藏记录中没有 id 字段，需用 productId 标识商品
  try { await cartStore.addItem({ ...product, id: product.productId }); ElMessage.success(t('common.addedToCart')) } catch {}
}

onMounted(loadFavorites)
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.favorites-page { background: $walmart-gray-100; min-height: 100vh; padding: 24px 0; }
.favorites-container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.page-title { font-size: 28px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 24px; }
.products-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-card { background: $walmart-white; border-radius: 8px; overflow: hidden; cursor: pointer;
  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
  .product-image { position: relative; height: 200px; background: $walmart-gray-100; display: flex; align-items: center; justify-content: center;
    img { max-width: 100%; max-height: 100%; object-fit: contain; padding: 16px; }
    .remove-btn { position: absolute; top: 8px; right: 8px; width: 28px; height: 28px; border-radius: 50%; background: #fff; border: none; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
  }
  .product-info { padding: 12px;
    .product-pricing .now-price { font-size: 18px; font-weight: 700; color: $walmart-gray-600; .dollar { font-size: 12px; } }
    .product-name { font-size: 14px; color: $walmart-gray-600; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin: 8px 0; }
  }
}
.empty-state { text-align: center; padding: 80px; background: $walmart-white; border-radius: 12px;
  h2 { font-size: 20px; margin: 16px 0 8px; color: $walmart-gray-600; }
  p { color: #767676; margin-bottom: 24px; }
}
</style>
