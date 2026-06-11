import request from '@/utils/request'

// ===== 顾客认证 =====
export function customerLogin(data) {
  return request.post('/customer/auth/login', data)
}

export function customerRegister(data) {
  return request.post('/customer/auth/register', data)
}

export function getCustomerInfo() {
  return request.get('/customer/auth/info')
}

export function getMemberLevels() {
  return request.get('/customer/auth/levels')
}

// ===== 商品 =====
export function searchProducts(params) {
  return request.get('/customer/product/list', { params })
}

export function getProductDetail(id) {
  return request.get(`/customer/product/${id}`)
}

export function getCategoryList() {
  return request.get('/customer/product/category/list')
}

// ===== 购物车 =====
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

export function clearCartApi() {
  return request.delete('/customer/cart/clear')
}

// ===== 订单 =====
export function createCustomerOrder(data) {
  return request.post('/customer/order/create', data)
}

export function getCustomerOrders(params) {
  return request.get('/customer/order/list', { params })
}

export function getCustomerOrderDetail(orderNo) {
  return request.get(`/customer/order/detail/${orderNo}`)
}

export function payOrder(orderNo) {
  return request.put(`/customer/order/pay/${orderNo}`)
}

export function cancelOrder(orderNo) {
  return request.put(`/customer/order/cancel/${orderNo}`)
}

export function confirmOrder(orderNo) {
  return request.put(`/customer/order/confirm/${orderNo}`)
}

// ===== 优惠券 =====
export function getCustomerCoupons() {
  return request.get('/customer/coupon/list')
}

export function claimCoupon(couponId) {
  return request.post(`/customer/coupon/claim/${couponId}`)
}

// ===== 收藏 =====
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

// ===== 收货地址 =====
export function getAddressList() {
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

// ===== 首页分类图标 =====
export function getHomeCategories() {
  return request.get('/homeCategory/customer/list')
}

// ===== 热门/最新商品 =====
export function getHotProducts() {
  return request.get('/customer/product/hot')
}

export function getLatestProducts() {
  return request.get('/customer/product/latest')
}
