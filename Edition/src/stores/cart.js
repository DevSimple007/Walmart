import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartList, addToCart as addToCartApi, updateCartItem, removeCartItem } from '@/api'
import { getToken } from '@/utils/auth'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const loading = ref(false)

  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  async function loadCart() {
    if (!getToken()) return
    loading.value = true
    try {
      const res = await getCartList()
      items.value = (res.data || []).map(item => ({
        ...item,
        checked: true
      }))
    } catch (e) {
      console.error('Failed to load cart', e)
    } finally {
      loading.value = false
    }
  }

  async function addItem(product, quantity = 1) {
    await addToCartApi({
      productId: product.id,
      quantity
    })
    await loadCart()
  }

  async function updateQuantity(productId, quantity) {
    await updateCartItem({ productId, quantity })
    await loadCart()
  }

  async function removeItem(productId) {
    await removeCartItem(productId)
    await loadCart()
  }

  function toggleCheck(productId) {
    const item = items.value.find(i => i.productId === productId)
    if (item) {
      item.checked = !item.checked
    }
  }

  function toggleAll() {
    const allChecked = items.value.every(i => i.checked)
    items.value.forEach(item => {
      item.checked = !allChecked
    })
  }

  const checkedItems = computed(() => {
    return items.value.filter(i => i.checked)
  })

  const checkedTotal = computed(() => {
    return checkedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  return {
    items,
    loading,
    totalCount,
    totalPrice,
    checkedItems,
    checkedTotal,
    loadCart,
    addItem,
    updateQuantity,
    removeItem,
    toggleCheck,
    toggleAll
  }
})
