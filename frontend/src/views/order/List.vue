<template>
  <div class="order-management">
    <div class="page-card wm-card">
      <div class="page-card-header">
        <h2 class="page-title">订单管理</h2>
      </div>

      <!-- 搜索表单 -->
      <div class="search-section">
        <el-form :inline="!isMobile" :model="searchForm" class="search-form" :label-position="isMobile ? 'top' : 'right'">
          <el-form-item label="订单号">
            <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="请选择订单状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="待支付" value="0" />
              <el-option label="已支付" value="1" />
              <el-option label="待发货" value="2" />
              <el-option label="已发货" value="3" />
              <el-option label="已完成" value="4" />
              <el-option label="已取消" value="5" />
            </el-select>
          </el-form-item>
          <el-form-item class="search-actions">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 桌面/平板端表格 -->
      <div class="table-scroll-wrapper" v-if="!isMobile">
        <el-table :data="orderList" stripe border v-loading="loading" class="wm-table">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="orderNo" label="订单号" min-width="160" />
          <el-table-column prop="userId" label="用户ID" width="100" />
          <el-table-column prop="totalAmount" label="总金额" width="120">
            <template #default="scope">
              <span class="amount">¥{{ scope.row.totalAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="订单状态" width="110">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" effect="light" round>
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="updateTime" label="更新时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="handleDetail(scope.row.id)">详情</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片列表 -->
      <div class="mobile-card-list" v-else>
        <div
          v-for="order in orderList"
          :key="order.id"
          class="order-card"
          @click="handleDetail(order.id)"
        >
          <div class="order-card-header">
            <span class="order-no">{{ order.orderNo }}</span>
            <el-tag :type="getStatusType(order.status)" effect="light" size="small" round>
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
          <div class="order-card-body">
            <div class="order-info-row">
              <span class="label">用户ID</span>
              <span>{{ order.userId }}</span>
            </div>
            <div class="order-info-row">
              <span class="label">总金额</span>
              <span class="amount">¥{{ order.totalAmount }}</span>
            </div>
            <div class="order-info-row">
              <span class="label">创建时间</span>
              <span>{{ order.createTime }}</span>
            </div>
          </div>
          <div class="order-card-footer">
            <el-button size="small" text type="primary" @click.stop="handleDetail(order.id)">查看详情</el-button>
            <el-button size="small" text type="danger" @click.stop="handleDelete(order.id)">删除</el-button>
          </div>
        </div>
        <div v-if="orderList.length === 0" class="empty-state">暂无订单数据</div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :layout="isMobile ? 'prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
          :total="pagination.total"
          :small="isMobile"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const orderList = ref<any[]>([])
const loading = ref(false)
const isMobile = ref(false)

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  orderNo: '',
  status: ''
})

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
}

const getOrderList = async () => {
  loading.value = true
  try {
    const res = await axios.get('/order/list/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
        status: searchForm.status || undefined,
        orderNo: searchForm.orderNo || undefined
      }
    })
    orderList.value = res.data.page?.records || res.data.records || []
    pagination.total = res.data.page?.total || res.data.total || 0
  } catch (error) {
    ElMessage.error('获取订单列表失败')
    console.error('获取订单列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  getOrderList()
}

const resetForm = () => {
  searchForm.orderNo = ''
  searchForm.status = ''
  pagination.currentPage = 1
  getOrderList()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getOrderList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getOrderList()
}

const handleDetail = (id: string) => {
  router.push(`/order/detail?id=${id}`)
}

const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/order/list/delete/${id}`)
    ElMessage.success('删除成功')
    getOrderList()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败', error)
  }
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '待支付', 1: '已支付', 2: '待发货', 3: '已发货', 4: '已完成', 5: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'info', 4: 'success', 5: 'danger' }
  return map[status] || 'default'
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  getOrderList()
})
</script>

<style scoped>
.order-management {
  padding: 0;
}

.page-card {
  padding: 0;
}

.page-card-header {
  padding: var(--wm-space-lg);
  border-bottom: 1px solid var(--wm-border-light);
}

.page-title {
  font-size: var(--wm-text-lg);
  font-weight: 600;
  color: var(--wm-text-primary);
  margin: 0;
}

.search-section {
  padding: var(--wm-space-lg);
  border-bottom: 1px solid var(--wm-border-light);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: var(--wm-space-sm);
}

.search-actions {
  margin-bottom: 0;
}

.amount {
  font-weight: 600;
  color: var(--wm-primary);
}

.table-scroll-wrapper {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: var(--wm-space-md) var(--wm-space-lg);
  border-top: 1px solid var(--wm-border-light);
}

/* ============================
   移动端卡片列表
   ============================ */
.mobile-card-list {
  padding: var(--wm-space-sm);
  display: flex;
  flex-direction: column;
  gap: var(--wm-space-sm);
}

.order-card {
  background: var(--wm-bg-card);
  border: 1px solid var(--wm-border);
  border-radius: var(--wm-radius-md);
  padding: var(--wm-space-md);
  transition: all var(--wm-transition-fast);
  cursor: pointer;
}

.order-card:active {
  background: var(--wm-primary-light);
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--wm-space-sm);
}

.order-no {
  font-weight: 600;
  color: var(--wm-text-primary);
  font-size: var(--wm-text-base);
}

.order-card-body {
  display: flex;
  flex-direction: column;
  gap: var(--wm-space-xs);
}

.order-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--wm-text-sm);
}

.order-info-row .label {
  color: var(--wm-text-secondary);
}

.order-card-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--wm-space-sm);
  margin-top: var(--wm-space-sm);
  padding-top: var(--wm-space-sm);
  border-top: 1px solid var(--wm-border-light);
}

.empty-state {
  text-align: center;
  padding: var(--wm-space-xl);
  color: var(--wm-text-muted);
  font-size: var(--wm-text-sm);
}

/* ============================
   平板端
   ============================ */
@media screen and (max-width: 1024px) {
  .page-card-header,
  .search-section {
    padding: var(--wm-space-md);
  }

  .pagination-wrapper {
    padding: var(--wm-space-md);
  }
}

/* ============================
   移动端
   ============================ */
@media screen and (max-width: 768px) {
  .page-card-header {
    padding: var(--wm-space-md);
  }

  .page-title {
    font-size: var(--wm-text-base);
  }

  .search-section {
    padding: var(--wm-space-md);
  }

  .search-form :deep(.el-form-item) {
    margin-bottom: var(--wm-space-sm);
    width: 100%;
  }

  .search-form :deep(.el-form-item .el-form-item__content) {
    width: 100%;
  }

  .search-form :deep(.el-input),
  .search-form :deep(.el-select) {
    width: 100%;
  }

  .search-actions :deep(.el-form-item__content) {
    display: flex;
    gap: var(--wm-space-sm);
  }

  .search-actions :deep(.el-button) {
    flex: 1;
  }

  .pagination-wrapper {
    justify-content: center;
  }
}
</style>
