import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartList, addToCart as addToCartApi, updateCartItem, removeCartItem, clearCartApi } from '@/api'
import { getToken } from '@/utils/auth'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const buyNowItems = ref([])

  const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() => checkedItems.value.reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0))
  const checkedItems = computed(() => items.value.filter(item => item.checked))
  const orderItems = computed(() => buyNowItems.value.length ? [...buyNowItems.value] : checkedItems.value)
  const orderTotal = computed(() => orderItems.value.reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0))
  const checkedTotal = computed(() => checkedItems.value.reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0))

  async function loadCart() {
    if (!getToken()) return
    try {
      const res = await getCartList()
      items.value = (res.data || []).map(item => ({
        ...item,
        checked: true
      }))
    } catch (e) {
      console.error('加载购物车失败', e)
    }
  }

  async function addItem(product, quantity = 1) {
    await addToCartApi({ productId: product.id, quantity })
    await loadCart()
  }

  async function removeItem(productId) {
    await removeCartItem(productId)
    items.value = items.value.filter(i => i.productId !== productId)
  }

  async function updateQuantity(productId, quantity) {
    await updateCartItem({ productId, quantity })
    const item = items.value.find(i => i.productId === productId)
    if (item) item.quantity = quantity
  }

  function toggleCheck(productId) {
    const item = items.value.find(i => i.productId === productId)
    if (item) item.checked = !item.checked
  }

  function toggleCheckAll() {
    const allChecked = items.value.length > 0 && items.value.every(i => i.checked)
    items.value.forEach(i => i.checked = !allChecked)
  }

  async function clearCart() {
    await clearCartApi()
    items.value = []
  }

  async function removeChecked() {
    for (const item of checkedItems.value) {
      await removeCartItem(item.productId)
    }
    items.value = items.value.filter(i => !i.checked)
  }

  function setBuyNowItem(product, quantity = 1) {
    buyNowItems.value = [{
      productId: product.id,
      productName: product.productName,
      price: product.price,
      imageUrl: product.imageUrl,
      quantity
    }]
  }

  function clearBuyNowItem() {
    buyNowItems.value = []
  }

  return {
    items, buyNowItems, totalCount, totalPrice, checkedItems, checkedTotal,
    orderItems, orderTotal,
    loadCart, addItem, removeItem, updateQuantity,
    toggleCheck, toggleCheckAll, clearCart, removeChecked,
    setBuyNowItem, clearBuyNowItem
  }
})
