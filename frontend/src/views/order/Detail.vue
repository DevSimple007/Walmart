<template>
  <div class="order-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单详情</span>
          <el-button type="primary" @click="goBack">返回</el-button>
        </div>
      </template>

      <div v-if="loading" style="text-align: center; padding: 60px;">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        <p style="color: #999; margin-top: 10px;">加载中...</p>
      </div>

      <div v-else-if="orderDetail" class="detail-content">
        <div class="order-info">
          <h3>订单信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(orderDetail.status)">
                {{ getStatusText(orderDetail.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="用户ID">{{ orderDetail.userId }}</el-descriptions-item>
            <el-descriptions-item label="商品金额">¥{{ orderDetail.totalAmount }}</el-descriptions-item>
            <el-descriptions-item label="优惠券优惠" v-if="orderDetail.couponDiscount && Number(orderDetail.couponDiscount) > 0">
              <span style="color: #F56C6C;">-¥{{ orderDetail.couponDiscount }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="实付金额">
              <span style="color: #F56C6C; font-weight: bold;">¥{{ orderDetail.actualAmount || orderDetail.totalAmount }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="收货地址">{{ orderDetail.address || '无' }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ orderDetail.remark || '无' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ orderDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ orderDetail.updateTime }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="order-items">
          <h3>商品列表</h3>
          <el-table :data="orderItems" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="productName" label="商品名称">
              <template #default="scope">
                {{ scope.row.productName || '商品#' + scope.row.productId }}
              </template>
            </el-table-column>
            <el-table-column prop="productId" label="商品ID" width="100" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="price" label="单价" width="100">
              <template #default="scope">
                ¥{{ scope.row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="总价" width="100">
              <template #default="scope">
                ¥{{ scope.row.totalPrice }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="order-actions">
          <el-button type="primary" @click="handleStatusChange">修改状态</el-button>
          <el-button type="danger" @click="handleRefund">申请退款</el-button>
        </div>
      </div>

      <div v-else style="text-align: center; padding: 60px;">
        <el-icon :size="48" color="#ccc"><Document /></el-icon>
        <p style="color: #999; margin-top: 10px;">订单不存在或加载失败</p>
        <el-button type="primary" @click="goBack">返回订单列表</el-button>
      </div>
    </el-card>

    <!-- 状态修改对话框 -->
    <el-dialog v-model="statusDialogVisible" title="修改订单状态">
      <el-form :model="statusForm" :rules="statusRules" ref="statusFormRef" label-width="80px">
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择订单状态">
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="待发货" :value="2" />
            <el-option label="已发货" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatusChange">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { Loading, Document } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const orderDetail = ref<any>(null)
const orderItems = ref<any[]>([])
const loading = ref(true)

// 状态修改对话框
const statusDialogVisible = ref(false)
const statusFormRef = ref()
const statusForm = reactive({
  status: 0
})

const statusRules = reactive({
  status: [
    { required: true, message: '请选择订单状态', trigger: 'change' }
  ]
})

// 获取订单ID
const orderId = computed(() => route.query.id as string)

// 获取订单详情
const getOrderDetail = async () => {
  if (!orderId.value) {
    loading.value = false
    return
  }
  loading.value = true
  try {
    const res = await axios.get(`/order/list/get/${orderId.value}`)
    orderDetail.value = res.data.data
    // 获取订单商品列表
    getOrderItems()
  } catch (error) {
    orderDetail.value = null
    console.error('获取订单详情失败', error)
  } finally {
    loading.value = false
  }
}

// 获取订单商品列表
const getOrderItems = async () => {
  if (!orderId.value) return
  try {
    const res = await axios.get(`/order/list/item/list/${orderId.value}`)
    orderItems.value = (res.data.data || []).map((item: any) => ({
      ...item,
      totalPrice: (item.price || 0) * (item.quantity || 0)
    }))
  } catch (error) {
    orderItems.value = []
  }
}

// 修改订单状态
const handleStatusChange = () => {
  if (orderDetail.value) {
    statusForm.status = orderDetail.value.status
    statusDialogVisible.value = true
  }
}

// 提交状态修改
const submitStatusChange = async () => {
  try {
    await axios.put(`/order/list/updateStatus/${orderId.value}`, null, {
      params: { status: statusForm.status }
    })
    ElMessage.success('状态更新成功')
    statusDialogVisible.value = false
    getOrderDetail()
  } catch (error) {
    ElMessage.error('状态更新失败')
    console.error('状态更新失败', error)
  }
}

// 处理退款
const handleRefund = async () => {
  try {
    await axios.put(`/order/list/handleRefund/${orderId.value}`, null, {
      params: { orderId: orderId.value }
    })
    ElMessage.success('退款处理成功')
    getOrderDetail()
  } catch (error) {
    ElMessage.error('退款处理失败')
    console.error('退款处理失败', error)
  }
}

// 返回订单列表
const goBack = () => {
  router.push('/order/list')
}

// 获取订单状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '待发货',
    3: '已发货',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取订单状态标签类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'info',
    3: 'info',
    4: 'success',
    5: 'danger'
  }
  return typeMap[status] || 'default'
}

// 组件挂载时获取订单详情
onMounted(() => {
  getOrderDetail()
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-content {
  margin-top: 20px;
}

.order-info {
  margin-bottom: 30px;
}

.order-info h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.order-items {
  margin-bottom: 30px;
}

.order-items h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer {
  text-align: right;
}
</style>