import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import router from '../router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 模拟后端响应数据
const mockData = {
  // 登录响应
  login: {
    code: 200,
    message: '登录成功',
    data: {
      token: 'mock-token-123456',
      refreshToken: 'mock-refresh-token-123456',
      userInfo: {
        id: 1,
        username: 'admin',
        nickname: '超级管理员',
        email: 'admin@walmart.com',
        phone: '13800138000',
        status: 1
      }
    }
  },
  // 仪表盘数据
  dashboard: {
    code: 200,
    message: '获取成功',
    data: {
      todaySales: 12345.67,
      todayOrders: 123,
      inventoryWarning: 5,
      totalProducts: 1000,
      salesTrend: [
        { date: '2026-03-29', sales: 10000 },
        { date: '2026-03-30', sales: 12000 },
        { date: '2026-03-31', sales: 9000 },
        { date: '2026-04-01', sales: 15000 },
        { date: '2026-04-02', sales: 13000 },
        { date: '2026-04-03', sales: 11000 },
        { date: '2026-04-04', sales: 14000 }
      ],
      orderStatus: [
        { name: '待支付', value: 20 },
        { name: '已支付', value: 30 },
        { name: '待发货', value: 15 },
        { name: '已发货', value: 25 },
        { name: '已完成', value: 40 },
        { name: '已取消', value: 10 }
      ]
    }
  },
  // 会员等级列表
  memberLevelList: {
    code: 200,
    message: '获取成功',
    data: {
      records: [
        { id: 1, levelName: '普通会员', minPoints: 0, discount: 1.00 },
        { id: 2, levelName: '银卡会员', minPoints: 1000, discount: 0.95 },
        { id: 3, levelName: '金卡会员', minPoints: 5000, discount: 0.90 },
        { id: 4, levelName: '钻石会员', minPoints: 10000, discount: 0.85 }
      ]
    }
  },
  // 会员列表
  memberList: {
    code: 200,
    message: '获取成功',
    data: {
      records: [
        { id: 1, memberName: '张三', phone: '13900139001', email: 'zhangsan@example.com', address: '北京市朝阳区', levelId: 1, levelName: '普通会员', points: 500, balance: 1000.00, createTime: '2026-04-01 10:00:00' },
        { id: 2, memberName: '李四', phone: '13900139002', email: 'lisi@example.com', address: '上海市浦东新区', levelId: 2, levelName: '银卡会员', points: 2000, balance: 5000.00, createTime: '2026-04-02 14:00:00' },
        { id: 3, memberName: '王五', phone: '13900139003', email: 'wangwu@example.com', address: '广州市天河区', levelId: 3, levelName: '金卡会员', points: 6000, balance: 10000.00, createTime: '2026-04-03 16:00:00' },
        { id: 4, memberName: '赵六', phone: '13900139004', email: 'zhaoliu@example.com', address: '成都市武侯区', levelId: 4, levelName: '钻石会员', points: 12000, balance: 20000.00, createTime: '2026-04-04 09:00:00' },
        { id: 5, memberName: '钱七', phone: '13900139005', email: 'qianqi@example.com', address: '深圳市南山区', levelId: 2, levelName: '银卡会员', points: 3000, balance: 8000.00, createTime: '2026-04-05 11:00:00' }
      ],
      total: 5
    }
  }
}

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    if (res?.data?.page && !res?.data?.records) {
      res.data = res.data.page
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    const url = error.config?.url

    // 401 未授权：token过期或无效，清除登录状态并跳转登录页
    if (status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(error)
    }

    // 后端不可达时的模拟数据降级
    if (!status) {
      // 模拟登录响应
      if (url === '/auth/login' && error.config.method === 'post') {
        const userStore = useUserStore()
        userStore.setToken(mockData.login.data.token)
        userStore.setUserInfo(mockData.login.data.userInfo)
        ElMessage.success('登录成功')
        return Promise.resolve(mockData.login)
      }

      // 模拟仪表盘数据响应
      if (url === '/report/dashboard' && error.config.method === 'get') {
        return Promise.resolve(mockData.dashboard)
      }

      // 模拟会员等级列表响应
      if (url === '/member/level/list' && error.config.method === 'get') {
        return Promise.resolve(mockData.memberLevelList)
      }

      // 模拟会员列表响应
      if (url === '/member/list/list' && error.config.method === 'get') {
        return Promise.resolve(mockData.memberList)
      }

      // 模拟会员添加/编辑/删除响应
      if ((url === '/member/list/add' && error.config.method === 'post') ||
          (url === '/member/list/update' && error.config.method === 'put') ||
          (url?.startsWith('/member/list/delete/') && error.config.method === 'delete')) {
        ElMessage.success('操作成功')
        return Promise.resolve({ code: 200, message: '操作成功' })
      }

      ElMessage.error('后端服务不可用，请检查后端是否启动')
      return Promise.reject(error)
    }

    ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service