<template>
  <div class="category-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品分类管理</span>
          <el-button type="primary" @click="handleAdd">添加分类</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分类名称">
          <el-input v-model="searchForm.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="categoryList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="parentId" label="父分类ID" width="100" />
        <el-table-column prop="parentName" label="父分类名称" />
        <el-table-column prop="sort" label="排序" width="80" />
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

    <!-- 分类树展示 -->
    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>分类树</span>
        </div>
      </template>
      <el-tree
        :data="categoryTree"
        node-key="id"
        :props="treeProps"
        style="height: 400px; overflow-y: auto"
      />
    </el-card>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父分类">
            <el-option label="根分类" value="0" />
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
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

const categoryList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  categoryName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  parentId: 0,
  categoryName: '',
  sort: 0
})

const rules = reactive<FormRules>({
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
})

const categoryOptions = ref<any[]>([])
const categoryTree = ref<any[]>([])

const treeProps = {
  children: 'children',
  label: 'categoryName'
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    const res = await axios.get('/product/category/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
        categoryName: searchForm.categoryName || undefined
      }
    })
    categoryList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取分类列表失败')
    console.error('获取分类列表失败', error)
  }
}

// 获取分类选项（用于父分类选择）
const getCategoryOptions = async () => {
  try {
    const res = await axios.get('/product/category/list')
    categoryOptions.value = res.data.records
  } catch (error) {
    console.error('获取分类选项失败', error)
  }
}

// 获取分类树
const getCategoryTree = async () => {
  try {
    const res = await axios.get('/product/category/tree')
    categoryTree.value = res.data
  } catch (error) {
    ElMessage.error('获取分类树失败')
    console.error('获取分类树失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getCategoryList()
}

// 重置
const resetForm = () => {
  searchForm.categoryName = ''
  pagination.currentPage = 1
  getCategoryList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getCategoryList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getCategoryList()
}

// 添加分类
const handleAdd = () => {
  dialogTitle.value = '添加分类'
  form.id = ''
  form.parentId = 0
  form.categoryName = ''
  form.sort = 0
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑分类'
  form.id = row.id
  form.parentId = row.parentId
  form.categoryName = row.categoryName
  form.sort = row.sort
  dialogVisible.value = true
}

// 删除分类
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/product/category/delete/${id}`)
    ElMessage.success('删除成功')
    getCategoryList()
    getCategoryTree()
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
          await axios.put('/product/category/update', form)
        } else {
          // 添加
          await axios.post('/product/category/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getCategoryList()
        getCategoryTree()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getCategoryList()
  getCategoryOptions()
  getCategoryTree()
})
</script>

<style scoped>
.category-management {
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

.mt-4 {
  margin-top: 20px;
}
</style>