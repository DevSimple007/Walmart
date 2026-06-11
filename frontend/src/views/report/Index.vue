<template>
  <div class="report-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>销售报表</span>
        </div>
      </template>

      <div class="report-tabs">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="销售统计" name="sales">
            <div class="sales-tab">
              <div class="date-type">
                <el-radio-group v-model="dateType" @change="handleDateTypeChange">
                  <el-radio-button label="day">日</el-radio-button>
                  <el-radio-button label="week">周</el-radio-button>
                  <el-radio-button label="month">月</el-radio-button>
                </el-radio-group>
              </div>
              <div class="chart-container">
                <div id="salesChart" ref="salesChartRef" class="chart"></div>
                <div id="ordersChart" ref="ordersChartRef" class="chart"></div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="销量排行" name="top">
            <div class="top-tab">
              <div id="topChart" ref="topChartRef" class="chart"></div>
              <div class="top-table">
                <el-table :data="topData" style="width: 100%">
                  <el-table-column prop="rank" label="排名" width="80" />
                  <el-table-column prop="productName" label="商品名称" />
                  <el-table-column prop="sales" label="销售额" width="120">
                    <template #default="scope">
                      ¥{{ scope.row.sales }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="quantity" label="销量" width="100" />
                </el-table>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="利润分析" name="profit">
            <div class="profit-tab">
              <div id="profitChart" ref="profitChartRef" class="chart"></div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const activeTab = ref('sales')
const dateType = ref('day')

// 图表实例
const salesChartRef = ref<HTMLElement | null>(null)
const ordersChartRef = ref<HTMLElement | null>(null)
const topChartRef = ref<HTMLElement | null>(null)
const profitChartRef = ref<HTMLElement | null>(null)

let salesChart: echarts.ECharts | null = null
let ordersChart: echarts.ECharts | null = null
let topChart: echarts.ECharts | null = null
let profitChart: echarts.ECharts | null = null

// 数据
const salesData = ref<any[]>([])
const topData = ref<any[]>([])
const profitData = ref<any[]>([])

// 初始化图表
const initCharts = () => {
  // 销售图表
  if (salesChartRef.value) {
    salesChart = echarts.init(salesChartRef.value)
  }
  
  // 订单图表
  if (ordersChartRef.value) {
    ordersChart = echarts.init(ordersChartRef.value)
  }
  
  // 销量排行图表
  if (topChartRef.value) {
    topChart = echarts.init(topChartRef.value)
  }
  
  // 利润分析图表
  if (profitChartRef.value) {
    profitChart = echarts.init(profitChartRef.value)
  }
  
  // 加载数据
  loadSalesData()
  loadTopData()
  loadProfitData()
}

// 加载销售数据
const loadSalesData = async () => {
  try {
    const res = await axios.get('/report/sales', {
      params: {
        type: dateType.value
      }
    })
    salesData.value = res.data.data
    updateSalesChart()
  } catch (error) {
    ElMessage.error('获取销售数据失败')
    console.error('获取销售数据失败', error)
  }
}

// 加载销量排行数据
const loadTopData = async () => {
  try {
    const res = await axios.get('/report/top')
    topData.value = res.data.data
    updateTopChart()
  } catch (error) {
    ElMessage.error('获取销量排行数据失败')
    console.error('获取销量排行数据失败', error)
  }
}

// 加载利润分析数据
const loadProfitData = async () => {
  try {
    const res = await axios.get('/report/profit')
    profitData.value = res.data.data
    updateProfitChart()
  } catch (error) {
    ElMessage.error('获取利润分析数据失败')
    console.error('获取利润分析数据失败', error)
  }
}

// 更新销售图表
const updateSalesChart = () => {
  if (!salesChart || !ordersChart) return
  
  const xAxisData = salesData.value.map(item => {
    if (dateType.value === 'day') return item.date
    if (dateType.value === 'week') return item.week
    if (dateType.value === 'month') return item.month
    return ''
  })
  
  const salesValues = salesData.value.map(item => item.sales)
  const ordersValues = salesData.value.map(item => item.orders)
  
  // 销售额图表
  salesChart.setOption({
    title: {
      text: '销售额统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: salesValues,
        type: 'bar',
        name: '销售额',
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  })
  
  // 订单数图表
  ordersChart.setOption({
    title: {
      text: '订单数统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: ordersValues,
        type: 'line',
        name: '订单数',
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  })
}

// 更新销量排行图表
const updateTopChart = () => {
  if (!topChart) return
  
  const productNames = topData.value.map(item => item.productName)
  const salesValues = topData.value.map(item => item.sales)
  
  topChart.setOption({
    title: {
      text: '销量排行Top10',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: productNames.reverse()
    },
    series: [
      {
        data: salesValues.reverse(),
        type: 'bar',
        name: '销售额',
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  })
}

// 更新利润分析图表
const updateProfitChart = () => {
  if (!profitChart) return
  
  const monthData = profitData.value.map(item => item.month)
  const salesValues = profitData.value.map(item => item.sales)
  const costValues = profitData.value.map(item => item.cost)
  const profitValues = profitData.value.map(item => item.profit)
  
  profitChart.setOption({
    title: {
      text: '利润分析',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['销售额', '成本', '利润'],
      bottom: 0
    },
    xAxis: {
      type: 'category',
      data: monthData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: salesValues,
        type: 'bar',
        name: '销售额',
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        data: costValues,
        type: 'bar',
        name: '成本',
        itemStyle: {
          color: '#F56C6C'
        }
      },
      {
        data: profitValues,
        type: 'line',
        name: '利润',
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  })
}

// 日期类型变化
const handleDateTypeChange = () => {
  loadSalesData()
}

// 标签变化
const handleTabChange = (tab: string) => {
  if (tab === 'top' && topData.value.length === 0) {
    loadTopData()
  } else if (tab === 'profit' && profitData.value.length === 0) {
    loadProfitData()
  }
}

// 窗口大小变化
const handleResize = () => {
  salesChart?.resize()
  ordersChart?.resize()
  topChart?.resize()
  profitChart?.resize()
}

// 组件挂载
onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

// 组件卸载
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  ordersChart?.dispose()
  topChart?.dispose()
  profitChart?.dispose()
})
</script>

<style scoped>
.report-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-tabs {
  margin-top: 20px;
}

.sales-tab {
  padding: 20px;
}

.date-type {
  margin-bottom: 20px;
  text-align: center;
}

.chart-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  height: 400px;
}

.chart {
  width: 100%;
  height: 100%;
}

.top-tab {
  padding: 20px;
}

.top-table {
  margin-top: 20px;
}

.profit-tab {
  padding: 20px;
  height: 500px;
}

@media screen and (max-width: 768px) {
  .chart-container {
    grid-template-columns: 1fr;
    height: 800px;
  }
}
</style>