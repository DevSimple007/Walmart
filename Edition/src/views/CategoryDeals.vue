<template>
  <div class="category-deals-page">
    <div class="page-container">
      <h1>{{ categoryName }}{{ $t('categoryDeals.suffix') }}</h1>
      <div class="products-grid" v-if="products.length">
        <div v-for="product in products" :key="product.id" class="product-card" @click="$router.push(`/ip/${product.id}`)">
          <div class="product-image">
            <span class="deal-badge">Deal</span>
            <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
          </div>
          <div class="product-info">
            <div class="product-pricing">
              <span class="now-price"><span class="dollar">$</span>{{ formatPrice(product.price) }}</span>
              <span class="was-price" v-if="product.originalPrice">${{ formatPrice(product.originalPrice) }}</span>
            </div>
            <p class="product-name">{{ product.productName }}</p>
          </div>
        </div>
      </div>
      <div class="empty-state" v-else-if="!loading">
        <p>No deals available in this category</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { searchProducts } from '@/api'

const route = useRoute()
const products = ref([])
const loading = ref(true)

const categoryName = computed(() => {
  const cat = route.params.category
  return cat ? cat.replace(/-/g, ' ').replace(/\b\w/g, c => c.toUpperCase()) : 'Deals'
})

function formatPrice(price) { return Number(price).toFixed(2) }

onMounted(async () => {
  try {
    const res = await searchProducts({ pageNum: 1, pageSize: 20 })
    products.value = res.data?.records || []
  } finally { loading.value = false }
})
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-yellow: #ffc220;
$walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.category-deals-page { background: $walmart-gray-100; min-height: 100vh; padding: 24px 0; }
.page-container { max-width: 1440px; margin: 0 auto; padding: 0 24px; }
h1 { font-size: 28px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 24px; }
.products-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-card { background: $walmart-white; border-radius: 8px; overflow: hidden; cursor: pointer;
  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
  .product-image { position: relative; height: 200px; background: $walmart-gray-100; display: flex; align-items: center; justify-content: center;
    img { max-width: 100%; max-height: 100%; object-fit: contain; padding: 16px; }
    .deal-badge { position: absolute; top: 8px; left: 8px; padding: 4px 8px; background: $walmart-yellow; color: $walmart-gray-600; font-size: 12px; font-weight: 700; border-radius: 4px; }
  }
  .product-info { padding: 12px;
    .product-pricing { margin-bottom: 8px;
      .now-price { font-size: 18px; font-weight: 700; color: $walmart-gray-600; .dollar { font-size: 12px; } }
      .was-price { font-size: 14px; color: $walmart-gray-400; text-decoration: line-through; margin-left: 8px; }
    }
    .product-name { font-size: 14px; color: $walmart-gray-600; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
  }
}
.empty-state { text-align: center; padding: 80px; background: $walmart-white; border-radius: 12px;
  p { color: $walmart-gray-400; }
}
</style>
