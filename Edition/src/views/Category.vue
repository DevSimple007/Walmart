<template>
  <div class="category-page">
    <div class="category-container">
      <div class="category-sidebar">
        <h3>{{ $t('category.departments') }}</h3>
        <nav class="dept-nav">
          <a v-for="dept in departments" :key="dept.id" class="dept-link" :class="{ active: selectedDept === dept.id }" @click="selectedDept = dept.id">
            {{ dept.name }}
          </a>
        </nav>
      </div>
      <div class="category-main">
        <div class="breadcrumb">
          <router-link to="/">Home</router-link> / <span>{{ selectedDeptName }}</span>
        </div>
        <h1>{{ selectedDeptName }}</h1>
        <div class="results-bar">
          <span>{{ $t('category.results', { n: products.length }) }}</span>
          <el-dropdown>
            <el-button>{{ $t('category.sortBy') }} <el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>Best match</el-dropdown-item>
                <el-dropdown-item>Price: Low to High</el-dropdown-item>
                <el-dropdown-item>Price: High to Low</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="products-grid">
          <div v-for="product in products" :key="product.id" class="product-card" @click="$router.push(`/ip/${product.id}`)">
            <div class="product-image">
              <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
              <button class="add-to-cart-btn" @click.stop="addToCart(product)"><el-icon><ShoppingCart /></el-icon></button>
            </div>
            <div class="product-info">
              <div class="product-pricing">
                <span class="now-price"><span class="dollar">$</span>{{ formatPrice(product.price) }}</span>
              </div>
              <p class="product-name">{{ product.productName }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { searchProducts, getCategoryList } from '@/api'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { ShoppingCart, ArrowDown } from '@element-plus/icons-vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const fallbackDepartments = [
  { id: 1, name: 'Electronics' }, { id: 2, name: 'Clothing' }, { id: 3, name: 'Home' },
  { id: 4, name: 'Garden' }, { id: 5, name: 'Baby' }, { id: 6, name: 'Toys' },
  { id: 7, name: 'Grocery' }, { id: 8, name: 'Pets' }
]

const departments = ref(fallbackDepartments)

const selectedDept = ref(route.params.id ? Number(route.params.id) : 1)
const products = ref([])

const selectedDeptName = computed(() => departments.value.find(d => d.id === selectedDept.value)?.name || '')

function formatPrice(price) { return Number(price).toFixed(2) }

async function addToCart(product) {
  if (!getToken()) { router.push('/login'); return }
  try { await cartStore.addItem(product); ElMessage.success(t('common.addedToCart')) } catch { ElMessage.error(t('category.failed')) }
}

// 从后端加载真实分类树，加载失败时保留兜底列表
async function loadDepartments() {
  try {
    const res = await getCategoryList()
    const tree = res.data || []
    if (tree.length) {
      departments.value = tree.map(cat => ({ id: cat.id, name: cat.categoryName }))
    }
  } catch {}
}

async function loadProducts() {
  try {
    const res = await searchProducts({ pageNum: 1, pageSize: 20, categoryId: selectedDept.value })
    products.value = res.data?.records || []
  } catch { }
}

watch(selectedDept, loadProducts)
watch(() => route.params.id, (id) => {
  if (id) selectedDept.value = Number(id)
})
onMounted(() => {
  loadDepartments()
  loadProducts()
})
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.category-page { background: $walmart-gray-100; min-height: 100vh; padding: 20px 0; }
.category-container { max-width: 1440px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 240px 1fr; gap: 24px; }
.category-sidebar { background: $walmart-white; border-radius: 12px; padding: 20px; height: fit-content; position: sticky; top: 120px;
  h3 { font-size: 18px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 16px; }
}
.dept-nav { display: flex; flex-direction: column; }
.dept-link { padding: 10px 12px; font-size: 14px; color: $walmart-gray-600; text-decoration: none; border-radius: 8px; cursor: pointer;
  &:hover { background: $walmart-gray-100; }
  &.active { background: rgba($walmart-blue, 0.1); color: $walmart-blue; font-weight: 600; }
}
.category-main { .breadcrumb { font-size: 13px; color: $walmart-gray-400; margin-bottom: 12px;
  a { color: $walmart-blue; text-decoration: none; &:hover { text-decoration: underline; } }
}
  h1 { font-size: 24px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 16px; }
}
.results-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; font-size: 14px; color: $walmart-gray-400; }
.products-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-card { background: $walmart-white; border-radius: 8px; overflow: hidden; cursor: pointer;
  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
  .product-image { position: relative; height: 200px; background: $walmart-gray-100; display: flex; align-items: center; justify-content: center;
    img { max-width: 100%; max-height: 100%; object-fit: contain; padding: 16px; }
    .add-to-cart-btn { position: absolute; bottom: 8px; right: 8px; width: 36px; height: 36px; border-radius: 50%; background: $walmart-blue; color: #fff; border: none; display: flex; align-items: center; justify-content: center; opacity: 0; }
  }
  &:hover .add-to-cart-btn { opacity: 1; }
  .product-info { padding: 12px;
    .product-pricing .now-price { font-size: 18px; font-weight: 700; color: $walmart-gray-600; .dollar { font-size: 12px; } }
    .product-name { font-size: 14px; color: $walmart-gray-600; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-top: 8px; }
  }
}
</style>
