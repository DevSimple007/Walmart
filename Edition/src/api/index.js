import request from '@/utils/request'

// ===== Auth =====
export function login(data) {
  return request.post('/customer/auth/login', data)
}

export function register(data) {
  return request.post('/customer/auth/register', data)
}

export function getUserInfo() {
  return request.get('/customer/auth/info')
}

// ===== Products =====
export function searchProducts(params) {
  return request.get('/customer/product/list', { params })
}

export function getProductDetail(id) {
  return request.get(`/customer/product/${id}`)
}

export function getCategoryList() {
  return request.get('/customer/product/category/list')
}

export function getHotProducts() {
  return request.get('/customer/product/hot')
}

export function getLatestProducts() {
  return request.get('/customer/product/latest')
}

// ===== Home Categories =====
export function getHomeCategories() {
  return request.get('/homeCategory/customer/list')
}

// ===== Cart =====
export function getCartList() {
  return request.get('/customer/cart/list')
}

export function addToCart(data) {
  return request.post('/customer/cart/add', data)
}

export function updateCartItem(data) {
  return request.put('/customer/cart/update', data)
}

export function removeCartItem(productId) {
  return request.delete(`/customer/cart/remove/${productId}`)
}

export function clearCart() {
  return request.delete('/customer/cart/clear')
}

// ===== Orders =====
export function createOrder(data) {
  return request.post('/customer/order/create', data)
}

export function getOrders(params) {
  return request.get('/customer/order/list', { params })
}

export function getOrderDetail(orderNo) {
  return request.get(`/customer/order/detail/${orderNo}`)
}

export function payOrder(orderNo) {
  return request.put(`/customer/order/pay/${orderNo}`)
}

export function cancelOrder(orderNo) {
  return request.put(`/customer/order/cancel/${orderNo}`)
}

export function confirmReceive(orderNo) {
  return request.put(`/customer/order/confirm/${orderNo}`)
}

// ===== Favorites =====
export function getFavorites() {
  return request.get('/customer/favorite/list')
}

export function addFavorite(productId) {
  return request.post(`/customer/favorite/add/${productId}`)
}

export function removeFavorite(productId) {
  return request.delete(`/customer/favorite/remove/${productId}`)
}

export function checkFavorite(productId) {
  return request.get(`/customer/favorite/check/${productId}`)
}

// ===== Addresses =====
export function getAddresses() {
  return request.get('/customer/address/list')
}

export function addAddress(data) {
  return request.post('/customer/address/add', data)
}

export function updateAddress(data) {
  return request.put('/customer/address/update', data)
}

export function deleteAddress(id) {
  return request.delete(`/customer/address/delete/${id}`)
}

export function setDefaultAddress(id) {
  return request.put(`/customer/address/setDefault/${id}`)
}

// ===== Coupons =====
export function getCoupons() {
  return request.get('/customer/coupon/list')
}

export function claimCoupon(couponId) {
  return request.post(`/customer/coupon/claim/${couponId}`)
}
