import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../store/user'

// 所有路由
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      // 系统管理
      {
        path: 'system/user',
        name: 'User',
        component: () => import('../views/system/User.vue'),
        meta: { title: '用户管理', perm: 'sys:user:list' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('../views/system/Role.vue'),
        meta: { title: '角色管理', perm: 'sys:role:list' }
      },
      {
        path: 'system/menu',
        name: 'Menu',
        component: () => import('../views/system/Menu.vue'),
        meta: { title: '菜单管理', perm: 'sys:menu:list' }
      },
      // 商品管理
      {
        path: 'product/category',
        name: 'Category',
        component: () => import('../views/product/Category.vue'),
        meta: { title: '商品分类', perm: 'product:category:list' }
      },
      {
        path: 'product/list',
        name: 'ProductList',
        component: () => import('../views/product/List.vue'),
        meta: { title: '商品列表', perm: 'product:list:list' }
      },
      {
        path: 'product/brand',
        name: 'Brand',
        component: () => import('../views/product/Brand.vue'),
        meta: { title: '品牌管理', perm: 'product:brand:list' }
      },
      {
        path: 'product/category-icon',
        name: 'CategoryIcon',
        component: () => import('../views/product/CategoryIcon.vue'),
        meta: { title: '分类图标管理', perm: 'product:category:list' }
      },
      // 库存管理
      {
        path: 'inventory/list',
        name: 'InventoryList',
        component: () => import('../views/inventory/List.vue'),
        meta: { title: '库存查询', perm: 'inventory:list:list' }
      },
      {
        path: 'inventory/warehouse',
        name: 'Warehouse',
        component: () => import('../views/inventory/Warehouse.vue'),
        meta: { title: '仓库管理', perm: 'inventory:warehouse:list' }
      },
      // 会员管理
      {
        path: 'member/list',
        name: 'MemberList',
        component: () => import('../views/member/List.vue'),
        meta: { title: '会员管理', perm: 'member:list:list' }
      },
      {
        path: 'member/level',
        name: 'MemberLevel',
        component: () => import('../views/member/Level.vue'),
        meta: { title: '会员等级管理', perm: 'member:level:list' }
      },
      {
        path: 'coupon',
        name: 'Coupon',
        component: () => import('../views/coupon/Index.vue'),
        meta: { title: '优惠券管理', perm: 'coupon:list' }
      },
      // 订单管理
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('../views/order/List.vue'),
        meta: { title: '订单列表', perm: 'order:list:list' }
      },
      {
        path: 'order/detail',
        name: 'OrderDetail',
        component: () => import('../views/order/Detail.vue'),
        meta: { title: '订单详情', perm: 'order:detail:list' }
      },
      // 销售报表
      {
        path: 'report/index',
        name: 'ReportIndex',
        component: () => import('../views/report/Index.vue'),
        meta: { title: '销售报表', perm: 'report:index:list' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/Settings.vue'),
        meta: { title: '设置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.path === '/login') {
    next()
  } else {
    if (userStore.token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router