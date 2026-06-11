<template>
  <div class="inventory-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
          <el-button type="primary" @click="handleAdd">添加库存</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品ID">
          <el-input v-model="searchForm.productId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="inventoryList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="productId" label="商品ID" width="100" />
        <el-table-column prop="quantity" label="库存数量" width="120" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200">
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

    <!-- 添加/编辑库存对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="商品ID" prop="productId">
          <el-input-number v-model="form.productId" :min="1" />
        </el-form-item>
        <el-form-item label="库存数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="0" />
        </el-form-item>
        <el-form-item label="仓库ID" prop="warehouseId">
          <el-input-number v-model="form.warehouseId" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const inventoryList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  productId: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加库存')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  productId: 0,
  warehouseId: 0,
  quantity: 0
})

const rules = reactive<FormRules>({
  productId: [
    { required: true, message: '请输入商品ID', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ],
  warehouseId: [
    { required: true, message: '请输入仓库ID', trigger: 'blur' }
  ]
})

// 获取库存列表
const getInventoryList = async () => {
  try {
    const res = await axios.get('/inventory/list/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
        productId: searchForm.productId || undefined
      }
    })
    inventoryList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取库存列表失败')
    console.error('获取库存列表失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getInventoryList()
}

// 重置
const resetForm = () => {
  searchForm.productId = ''
  pagination.currentPage = 1
  getInventoryList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getInventoryList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getInventoryList()
}

// 添加库存
const handleAdd = () => {
  dialogTitle.value = '添加库存'
  form.id = ''
  form.productId = 0
  form.warehouseId = 0
  form.quantity = 0
  dialogVisible.value = true
}

// 编辑库存
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑库存'
  form.id = row.id
  form.productId = row.productId
  form.warehouseId = row.warehouseId
  form.quantity = row.quantity
  dialogVisible.value = true
}

// 删除库存
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/inventory/list/delete/${id}`)
    ElMessage.success('删除成功')
    getInventoryList()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          // 编辑
          await axios.put('/inventory/list/update', form)
        } else {
          // 添加
          await axios.post('/inventory/list/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getInventoryList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getInventoryList()
})
</script>

<style scoped>
.inventory-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
