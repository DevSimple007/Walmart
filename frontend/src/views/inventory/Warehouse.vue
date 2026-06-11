<template>
  <div class="warehouse-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>仓库管理</span>
          <el-button type="primary" @click="handleAdd">添加仓库</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="仓库名称">
          <el-input v-model="searchForm.warehouseName" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="warehouseList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="warehouseName" label="仓库名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="contact" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
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

    <!-- 添加/编辑仓库对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
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

const warehouseList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  warehouseName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加仓库')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  warehouseName: '',
  address: '',
  contact: '',
  phone: ''
})

const rules = reactive<FormRules>({
  warehouseName: [
    { required: true, message: '请输入仓库名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ]
})

// 获取仓库列表
const getWarehouseList = async () => {
  try {
    const res = await axios.get('/inventory/warehouse/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize
      }
    })
    warehouseList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取仓库列表失败')
    console.error('获取仓库列表失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getWarehouseList()
}

// 重置
const resetForm = () => {
  searchForm.warehouseName = ''
  pagination.currentPage = 1
  getWarehouseList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getWarehouseList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getWarehouseList()
}

// 添加仓库
const handleAdd = () => {
  dialogTitle.value = '添加仓库'
  form.id = ''
  form.warehouseName = ''
  form.address = ''
  form.contact = ''
  form.phone = ''
  dialogVisible.value = true
}

// 编辑仓库
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑仓库'
  form.id = row.id
  form.warehouseName = row.warehouseName
  form.address = row.address
  form.contact = row.contact
  form.phone = row.phone
  dialogVisible.value = true
}

// 删除仓库
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/inventory/warehouse/delete/${id}`)
    ElMessage.success('删除成功')
    getWarehouseList()
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
          await axios.put('/inventory/warehouse/update', form)
        } else {
          // 添加
          await axios.post('/inventory/warehouse/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getWarehouseList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取仓库列表
onMounted(() => {
  getWarehouseList()
})
</script>

<style scoped>
.warehouse-management {
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