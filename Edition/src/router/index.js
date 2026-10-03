import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import i18n from '@/i18n'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { titleKey: 'home' }
      },
      {
        path: 'shop/deals',
        name: 'Deals',
        component: () => import('@/views/Deals.vue'),
        meta: { titleKey: 'deals' }
      },
      {
        path: 'shop/deals/:category',
        name: 'CategoryDeals',
        component: () => import('@/views/CategoryDeals.vue'),
        meta: { titleKey: 'categoryDeals' }
      },
      {
        path: 'category/:id',
        name: 'Category',
        component: () => import('@/views/Category.vue'),
        meta: { titleKey: 'category' }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/Search.vue'),
        meta: { titleKey: 'search' }
      },
      {
        path: 'ip/:id',
        name: 'ProductDetail',
        component: () => import('@/views/ProductDetail.vue'),
        meta: { titleKey: 'product' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue'),
        meta: { titleKey: 'cart', needLogin: true }
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: () => import('@/views/Checkout.vue'),
        meta: { titleKey: 'checkout', needLogin: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/Orders.vue'),
        meta: { titleKey: 'orders', needLogin: true }
      },
      {
        path: 'account',
        name: 'Account',
        component: () => import('@/views/Account.vue'),
        meta: { titleKey: 'account', needLogin: true }
      },
      {
        path: 'account/addresses',
        name: 'Addresses',
        component: () => import('@/views/Addresses.vue'),
        meta: { titleKey: 'addresses', needLogin: true }
      },
      {
        path: 'account/favorites',
        name: 'Favorites',
        component: () => import('@/views/Favorites.vue'),
        meta: { titleKey: 'favorites', needLogin: true }
      },
      {
        path: 'account/coupons',
        name: 'Coupons',
        component: () => import('@/views/Coupons.vue'),
        meta: { titleKey: 'coupons', needLogin: true }
      },
      {
        path: 'help',
        name: 'Help',
        component: () => import('@/views/Help.vue'),
        meta: { titleKey: 'help' }
      },
      {
        path: 'all-departments',
        name: 'AllDepartments',
        component: () => import('@/views/AllDepartments.vue'),
        meta: { titleKey: 'allDepartments' }
      },
      {
        path: 'store/directory',
        name: 'StoreDirectory',
        component: () => import('@/views/StoreDirectory.vue'),
        meta: { titleKey: 'storeDirectory' }
      },
      {
        path: 'cp/pharmacy',
        name: 'Pharmacy',
        component: () => import('@/views/Pharmacy.vue'),
        meta: { titleKey: 'pharmacy' }
      },
      {
        path: 'plus',
        name: 'WalmartPlus',
        component: () => import('@/views/WalmartPlus.vue'),
        meta: { titleKey: 'plus' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { titleKey: 'login', hideHeader: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { titleKey: 'register', hideHeader: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = i18n.global.t('titles.' + (to.meta.titleKey || 'home'))
  if (to.meta.needLogin && !getToken()) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
