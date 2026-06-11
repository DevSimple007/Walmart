<template>
  <div class="dashboard-container">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <h1>欢迎回来，{{ userStore.userInfo?.nickname || '管理员' }}</h1>
      <p>沃尔玛OMS系统数据概览</p>
    </div>

    <!-- 数据卡片 -->
    <div class="stats-grid">
      <div class="stat-card wm-card" v-for="(item, idx) in statCards" :key="idx">
        <div class="stat-icon" :class="item.iconClass">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ item.label }}</h3>
          <p class="stat-value">{{ item.prefix }}{{ item.value }}</p>
          <p class="stat-change" :class="item.changeClass">{{ item.changeText }}</p>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 销售趋势图 -->
      <div class="chart-card wm-card chart-wide">
        <div class="chart-header">
          <h3>销售趋势</h3>
          <el-select v-model="chartType" size="small" class="chart-select">
            <el-option label="日" value="day" />
            <el-option label="周" value="week" />
            <el-option label="月" value="month" />
          </el-select>
        </div>
        <div class="chart-body">
          <div ref="salesChartRef" class="chart"></div>
        </div>
      </div>

      <!-- 订单状态分布 -->
      <div class="chart-card wm-card">
        <div class="chart-header">
          <h3>订单状态分布</h3>
        </div>
        <div class="chart-body">
          <div ref="orderStatusChartRef" class="chart"></div>
        </div>
      </div>

      <!-- 热门商品 -->
      <div class="chart-card wm-card chart-wide">
        <div class="chart-header">
          <h3>热门商品</h3>
        </div>
        <div class="chart-body">
          <div ref="hotProductsChartRef" class="chart"></div>
        </div>
      </div>

      <!-- 库存分类分布 -->
      <div class="chart-card wm-card">
        <div class="chart-header">
          <h3>库存分类分布</h3>
        </div>
        <div class="chart-body">
          <div ref="inventoryChartRef" class="chart"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, markRaw, computed } from 'vue'
import { useUserStore } from '../store/user'
import * as echarts from 'echarts'
import {
  Goods,
  Tickets,
  Collection,
  User
} from '@element-plus/icons-vue'
import axios from '../utils/axios'

const userStore = useUserStore()

// 统计数据
const stats = ref({
  salesToday: 0,
  salesChange: 0,
  ordersToday: 0,
  ordersChange: 0,
  inventoryAlert: 0,
  memberCount: 0
})

const chartType = ref('day')

// 统计卡片配置
const statCards = computed(() => [
  {
    label: '今日销售额',
    value: stats.value.salesToday,
    prefix: '¥',
    icon: markRaw(Goods),
    iconClass: 'sales-icon',
    changeClass: stats.value.salesChange > 0 ? 'positive' : stats.value.salesChange < 0 ? 'negative' : '',
    changeText: `${stats.value.salesChange > 0 ? '+' : ''}${stats.value.salesChange}% 较昨日`
  },
  {
    label: '今日订单',
    value: stats.value.ordersToday,
    prefix: '',
    icon: markRaw(Tickets),
    iconClass: 'orders-icon',
    changeClass: stats.value.ordersChange > 0 ? 'positive' : stats.value.ordersChange < 0 ? 'negative' : '',
    changeText: `${stats.value.ordersChange > 0 ? '+' : ''}${stats.value.ordersChange}% 较昨日`
  },
  {
    label: '库存预警',
    value: stats.value.inventoryAlert,
    prefix: '',
    icon: markRaw(Collection),
    iconClass: 'inventory-icon',
    changeClass: '',
    changeText: '低库存商品预警'
  },
  {
    label: '会员总数',
    value: stats.value.memberCount,
    prefix: '',
    icon: markRaw(User),
    iconClass: 'members-icon',
    changeClass: '',
    changeText: '已注册会员'
  }
])

// 图表实例
const salesChartRef = ref<HTMLElement | null>(null)
const orderStatusChartRef = ref<HTMLElement | null>(null)
const hotProductsChartRef = ref<HTMLElement | null>(null)
const inventoryChartRef = ref<HTMLElement | null>(null)

let salesChart: echarts.ECharts | null = null
let orderStatusChart: echarts.ECharts | null = null
let hotProductsChart: echarts.ECharts | null = null
let inventoryChart: echarts.ECharts | null = null

const initCharts = () => {
  if (salesChartRef.value) salesChart = echarts.init(salesChartRef.value)
  if (orderStatusChartRef.value) orderStatusChart = echarts.init(orderStatusChartRef.value)
  if (hotProductsChartRef.value) hotProductsChart = echarts.init(hotProductsChartRef.value)
  if (inventoryChartRef.value) inventoryChart = echarts.init(inventoryChartRef.value)
  loadDashboardData()
}

const loadDashboardData = async () => {
  try {
    const [statsRes, salesRes, topRes] = await Promise.all([
      axios.get('/dashboard/stats'),
      axios.get('/report/sales', { params: { type: chartType.value } }),
      axios.get('/report/top')
    ])

    const statsData = statsRes.data.data
    stats.value = {
      salesToday: statsData.salesToday || 0,
      salesChange: statsData.salesChange || 0,
      ordersToday: statsData.ordersToday || 0,
      ordersChange: statsData.ordersChange || 0,
      inventoryAlert: statsData.inventoryAlert || 0,
      memberCount: statsData.memberCount || 0
    }

    if (statsData.orderStatus) updateOrderStatusChart(statsData.orderStatus)
    updateSalesChart(salesRes.data.data || [])
    updateHotProductsChart(topRes.data.data || [])
    updateInventoryChart(statsData.categoryDistribution || [])
  } catch (error) {
    console.error('加载仪表盘数据失败', error)
  }
}

const chartColors = {
  primary: '#0071dc',
  primaryHover: '#0056b3',
  grid: '#f1f5f9',
  axis: '#cbd5e1',
  text: '#64748b',
  pieColors: ['#0071dc', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899']
}

const updateSalesChart = (data: any[]) => {
  if (!salesChart) return
  const xAxisData = data.map(item => item.date || item.week || item.month || '')
  const salesData = data.map(item => Number(item.sales) || 0)
  if (xAxisData.length === 0) { xAxisData.push('暂无数据'); salesData.push(0) }

  salesChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: { type: 'category', data: xAxisData, axisLine: { lineStyle: { color: chartColors.axis } }, axisLabel: { color: chartColors.text } },
    yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: chartColors.grid } }, axisLabel: { color: chartColors.text } },
    series: [{
      data: salesData, type: 'bar', name: '销售额', barWidth: '50%',
      itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: chartColors.primary }, { offset: 1, color: 'rgba(0,113,220,0.4)' }]), borderRadius: [6, 6, 0, 0] },
      emphasis: { itemStyle: { color: chartColors.primaryHover } }
    }]
  })
}

const updateOrderStatusChart = (statusData: any[]) => {
  if (!orderStatusChart) return
  const statusNameMap: Record<number, string> = { 0: '待支付', 1: '已支付', 2: '待发货', 3: '已发货', 4: '已完成', 5: '已取消' }
  const pieData = statusData.map(item => ({ value: Number(item.count) || 0, name: statusNameMap[Number(item.status)] || `状态${item.status}` }))
  if (pieData.length === 0) pieData.push({ value: 0, name: '暂无数据' })

  orderStatusChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left', textStyle: { color: chartColors.text } },
    color: chartColors.pieColors,
    series: [{
      name: '订单状态', type: 'pie', radius: ['35%', '65%'], center: ['50%', '55%'], data: pieData,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
      label: { show: true, formatter: '{b}\n{d}%', fontSize: 12, color: chartColors.text },
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.2)' } }
    }]
  })
}

const updateHotProductsChart = (topData: any[]) => {
  if (!hotProductsChart) return
  const productNames = topData.map(item => item.productName || `商品#${item.productId}`).reverse()
  const salesValues = topData.map(item => Number(item.sales) || 0).reverse()
  if (productNames.length === 0) { productNames.push('暂无数据'); salesValues.push(0) }

  hotProductsChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: chartColors.grid } }, axisLabel: { color: chartColors.text } },
    yAxis: { type: 'category', data: productNames, axisLine: { show: false }, axisLabel: { color: chartColors.text } },
    series: [{
      data: salesValues, type: 'bar', name: '销售额', barWidth: '50%',
      itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: 'rgba(0,113,220,0.4)' }, { offset: 1, color: chartColors.primary }]), borderRadius: [0, 6, 6, 0] },
      emphasis: { itemStyle: { color: chartColors.primaryHover } }
    }]
  })
}

const updateInventoryChart = (categoryData?: any[]) => {
  if (!inventoryChart) return
  const pieData = categoryData && categoryData.length > 0 ? categoryData : [{ value: 0, name: '暂无数据' }]

  inventoryChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { color: chartColors.text } },
    color: chartColors.pieColors,
    series: [{
      name: '商品分类', type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 3 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: pieData
    }]
  })
}

watch(chartType, async () => {
  try {
    const salesRes = await axios.get('/report/sales', { params: { type: chartType.value } })
    updateSalesChart(salesRes.data.data || [])
  } catch (error) {
    console.error('加载销售数据失败', error)
  }
})

const handleResize = () => {
  salesChart?.resize()
  orderStatusChart?.resize()
  hotProductsChart?.resize()
  inventoryChart?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  orderStatusChart?.dispose()
  hotProductsChart?.dispose()
  inventoryChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: var(--wm-space-lg);
}

.welcome-section {
  margin-bottom: var(--wm-space-xl);
}

.welcome-section h1 {
  font-size: var(--wm-text-2xl);
  font-weight: 700;
  color: var(--wm-text-primary);
  margin-bottom: var(--wm-space-xs);
}

.welcome-section p {
  font-size: var(--wm-text-base);
  color: var(--wm-text-secondary);
  margin: 0;
}

/* ============================
   统计卡片网格
   ============================ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--wm-space-lg);
  margin-bottom: var(--wm-space-xl);
}

.stat-card {
  display: flex;
  align-items: center;
  padding: var(--wm-space-lg);
  cursor: default;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--wm-radius-md);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: var(--wm-space-md);
  font-size: 22px;
  flex-shrink: 0;
}

.sales-icon { background: rgba(0, 113, 220, 0.1); color: #0071dc; }
.orders-icon { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
.inventory-icon { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
.members-icon { background: rgba(239, 68, 68, 0.1); color: #ef4444; }

.stat-content { flex: 1; min-width: 0; }

.stat-content h3 {
  font-size: var(--wm-text-xs);
  font-weight: 500;
  color: var(--wm-text-secondary);
  margin: 0 0 var(--wm-space-xs) 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: var(--wm-text-2xl);
  font-weight: 700;
  color: var(--wm-text-primary);
  margin: 0 0 var(--wm-space-xs) 0;
  line-height: var(--wm-leading-tight);
}

.stat-change {
  font-size: var(--wm-text-xs);
  margin: 0;
  color: var(--wm-text-muted);
}

.stat-change.positive { color: var(--wm-success); }
.stat-change.negative { color: var(--wm-danger); }

/* ============================
   图表网格
   ============================ */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--wm-space-lg);
}

.chart-card {
  padding: var(--wm-space-lg);
}

/* 宽图表占满一行 */
.chart-wide {
  grid-column: span 2;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--wm-space-md);
}

.chart-header h3 {
  font-size: var(--wm-text-base);
  font-weight: 600;
  color: var(--wm-text-primary);
  margin: 0;
}

.chart-select { width: 100px; }

.chart-body { height: 300px; }
.chart { width: 100%; height: 100%; }

/* ============================
   平板端 (768px ~ 1024px)
   ============================ */
@media screen and (max-width: 1024px) {
  .dashboard-container {
    padding: var(--wm-space-md);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--wm-space-md);
  }

  .stat-card {
    padding: var(--wm-space-md);
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }

  .stat-value {
    font-size: var(--wm-text-xl);
  }

  .charts-grid {
    gap: var(--wm-space-md);
  }

  .chart-card {
    padding: var(--wm-space-md);
  }

  .chart-wide {
    grid-column: span 2;
  }

  .chart-body {
    height: 260px;
  }
}

/* ============================
   移动端 (< 768px)
   ============================ */
@media screen and (max-width: 768px) {
  .dashboard-container {
    padding: var(--wm-space-sm);
  }

  .welcome-section {
    margin-bottom: var(--wm-space-lg);
  }

  .welcome-section h1 {
    font-size: var(--wm-text-xl);
  }

  .welcome-section p {
    font-size: var(--wm-text-sm);
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
    gap: var(--wm-space-sm);
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
    padding: var(--wm-space-md);
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: var(--wm-space-sm);
    width: 44px;
    height: 44px;
    font-size: 18px;
  }

  .stat-content h3 {
    font-size: 11px;
  }

  .stat-value {
    font-size: var(--wm-text-lg);
  }

  .charts-grid {
    grid-template-columns: 1fr;
    gap: var(--wm-space-md);
  }

  .chart-wide {
    grid-column: span 1;
  }

  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--wm-space-sm);
  }

  .chart-select {
    width: 100%;
  }

  .chart-body {
    height: 220px;
  }
}

/* 超小屏幕: 统计卡片单列 */
@media screen and (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
