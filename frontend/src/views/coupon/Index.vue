<template>
  <div class="coupon-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
          <div>
            <el-button type="success" @click="handleDistributeAll">发放给所有用户</el-button>
            <el-button type="warning" @click="handleDistribute">发放给指定用户</el-button>
            <el-button type="primary" @click="handleAdd">添加优惠券</el-button>
          </div>
        </div>
      </template>

      <el-table :data="couponList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="couponName" label="优惠券名称" />
        <el-table-column prop="couponType" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="couponTypeTag(scope.row.couponType)">
              {{ couponTypeLabel(scope.row.couponType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="couponValue" label="优惠值" width="100" />
        <el-table-column prop="minAmount" label="最低消费" width="100" />
        <el-table-column prop="totalCount" label="发放数量" width="100" />
        <el-table-column prop="usedCount" label="已领取" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 添加/编辑优惠券对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="优惠券名称" prop="couponName">
          <el-input v-model="form.couponName" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="类型" prop="couponType">
          <el-select v-model="form.couponType" placeholder="请选择类型">
            <el-option label="满减" :value="1" />
            <el-option label="折扣" :value="2" />
            <el-option label="立减" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="优惠值" prop="couponValue">
          <el-input-number v-model="form.couponValue" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="最低消费" prop="minAmount">
          <el-input-number v-model="form.minAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="发放数量" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="1" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 发放给指定用户对话框 -->
    <el-dialog v-model="distributeVisible" title="发放优惠券给指定用户" width="500px">
      <el-form :model="distributeForm" :rules="distributeRules" ref="distributeFormRef" label-width="100px">
        <el-form-item label="选择用户" prop="memberId">
          <el-select v-model="distributeForm.memberId" filterable remote :remote-method="searchMember" :loading="memberLoading" placeholder="输入手机号搜索用户">
            <el-option v-for="m in memberList" :key="m.id" :label="`${m.memberName} (${m.phone})`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择优惠券" prop="couponId">
          <el-select v-model="distributeForm.couponId" placeholder="请选择优惠券">
            <el-option v-for="c in allCouponList" :key="c.id" :label="c.couponName" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="distributeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDistributeSubmit">发放</el-button>
      </template>
    </el-dialog>

    <!-- 发放给所有用户对话框 -->
    <el-dialog v-model="distributeAllVisible" title="发放优惠券给所有用户" width="400px">
      <el-form label-width="80px">
        <el-form-item label="选择优惠券">
          <el-select v-model="distributeAllCouponId" placeholder="请选择优惠券">
            <el-option v-for="c in allCouponList" :key="c.id" :label="c.couponName" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <p style="color: #E6A23C; font-size: 13px;">提示：将为所有注册用户发放此优惠券</p>
      <template #footer>
        <el-button @click="distributeAllVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDistributeAllSubmit">确认发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const couponList = ref<any[]>([])
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('添加优惠券')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  couponName: '',
  couponType: 1 as number,
  couponValue: 0,
  minAmount: 0,
  startTime: '',
  endTime: '',
  totalCount: 1,
  status: 1
})

const rules = reactive<FormRules>({
  couponName: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  couponType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  couponValue: [{ required: true, message: '请输入优惠值', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入发放数量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 发放给指定用户
const distributeVisible = ref(false)
const distributeFormRef = ref<FormInstance>()
const memberLoading = ref(false)
const memberList = ref<any[]>([])
const allCouponList = ref<any[]>([])
const distributeForm = reactive({ memberId: '', couponId: '' })
const distributeRules = reactive<FormRules>({
  memberId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  couponId: [{ required: true, message: '请选择优惠券', trigger: 'change' }]
})

// 发放给所有用户
const distributeAllVisible = ref(false)
const distributeAllCouponId = ref('')

const couponTypeLabel = (type: number) => {
  const map: Record<number, string> = { 1: '满减', 2: '折扣', 3: '立减' }
  return map[type] || '未知'
}

const couponTypeTag = (type: number) => {
  const map: Record<number, string> = { 1: 'warning', 2: 'success', 3: 'danger' }
  return map[type] || ''
}

const getCouponList = async () => {
  try {
    const res = await axios.get('/coupon/list', {
      params: { pageNum: pagination.currentPage, pageSize: pagination.pageSize }
    })
    couponList.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取优惠券列表失败')
  }
}

const getAllCoupons = async () => {
  try {
    const res = await axios.get('/coupon/list', { params: { pageNum: 1, pageSize: 100 } })
    allCouponList.value = res.data.records || []
  } catch {}
}

const searchMember = async (query: string) => {
  if (!query) return
  memberLoading.value = true
  try {
    const res = await axios.get('/member/list/list', { params: { pageNum: 1, pageSize: 20 } })
    memberList.value = (res.data.records || []).filter((m: any) => m.phone?.includes(query) || m.memberName?.includes(query))
  } catch {}
  memberLoading.value = false
}

const handleSizeChange = (size: number) => { pagination.pageSize = size; getCouponList() }
const handleCurrentChange = (current: number) => { pagination.currentPage = current; getCouponList() }

const handleAdd = () => {
  dialogTitle.value = '添加优惠券'
  form.id = ''
  form.couponName = ''
  form.couponType = 1
  form.couponValue = 0
  form.minAmount = 0
  form.startTime = ''
  form.endTime = ''
  form.totalCount = 1
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑优惠券'
  form.id = row.id
  form.couponName = row.couponName
  form.couponType = row.couponType
  form.couponValue = row.couponValue
  form.minAmount = row.minAmount
  form.startTime = row.startTime
  form.endTime = row.endTime
  form.totalCount = row.totalCount
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/coupon/delete/${id}`)
    ElMessage.success('删除成功')
    getCouponList()
  } catch {
    ElMessage.error('删除失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await axios.put('/coupon/update', form)
        } else {
          await axios.post('/coupon/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getCouponList()
      } catch {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDistribute = () => {
  distributeForm.memberId = ''
  distributeForm.couponId = ''
  distributeVisible.value = true
  getAllCoupons()
}

const handleDistributeSubmit = async () => {
  if (!distributeFormRef.value) return
  await distributeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await axios.post('/coupon/distribute', distributeForm)
        ElMessage.success('发放成功')
        distributeVisible.value = false
      } catch {
        ElMessage.error('发放失败')
      }
    }
  })
}

const handleDistributeAll = () => {
  distributeAllCouponId.value = ''
  distributeAllVisible.value = true
  getAllCoupons()
}

const handleDistributeAllSubmit = async () => {
  if (!distributeAllCouponId.value) {
    ElMessage.warning('请选择优惠券')
    return
  }
  try {
    const res = await axios.post('/coupon/distributeAll', { couponId: distributeAllCouponId.value })
    ElMessage.success(`成功发放给 ${res.data?.count || 0} 个用户`)
    distributeAllVisible.value = false
  } catch {
    ElMessage.error('发放失败')
  }
}

onMounted(() => {
  getCouponList()
})
</script>

<style scoped>
.coupon-management {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
