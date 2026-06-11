<template>
  <div class="brand-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>品牌管理</span>
          <el-button type="primary" @click="handleAdd">添加品牌</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="品牌名称">
          <el-input v-model="searchForm.brandName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="brandList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="brandName" label="品牌名称" />
        <el-table-column prop="logo" label="品牌Logo" width="120">
          <template #default="scope">
            <el-image v-if="scope.row.logo" :src="scope.row.logo" fit="cover" style="width: 40px; height: 40px" />
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="品牌描述" />
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

    <!-- 添加/编辑品牌对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="form.brandName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="品牌Logo">
          <el-upload
            class="upload-demo"
            action="/api/upload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :file-list="fileList"
            list-type="picture"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png文件，且不超过500kb
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="品牌描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入品牌描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
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

const brandList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  brandName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加品牌')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  brandName: '',
  logo: '',
  description: '',
  status: 1
})

const rules = reactive<FormRules>({
  brandName: [
    { required: true, message: '请输入品牌名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

const fileList = ref<any[]>([])

// 获取品牌列表
const getBrandList = async () => {
  try {
    const res = await axios.get('/product/brand/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize
      }
    })
    brandList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取品牌列表失败')
    console.error('获取品牌列表失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getBrandList()
}

// 重置
const resetForm = () => {
  searchForm.brandName = ''
  pagination.currentPage = 1
  getBrandList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getBrandList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getBrandList()
}

// 添加品牌
const handleAdd = () => {
  dialogTitle.value = '添加品牌'
  form.id = ''
  form.brandName = ''
  form.logo = ''
  form.description = ''
  form.status = 1
  fileList.value = []
  dialogVisible.value = true
}

// 编辑品牌
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑品牌'
  form.id = row.id
  form.brandName = row.brandName
  form.logo = row.logo
  form.description = row.description
  form.status = row.status
  fileList.value = []
  if (row.logo) {
    fileList.value.push({ url: row.logo })
  }
  dialogVisible.value = true
}

// 删除品牌
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/product/brand/delete/${id}`)
    ElMessage.success('删除成功')
    getBrandList()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败', error)
  }
}

// 图片上传成功
const handleUploadSuccess = (_response: any, file: any) => {
  ElMessage.success('上传成功')
  form.logo = file.url
  fileList.value.push(file)
}

// 图片上传失败
const handleUploadError = (error: any) => {
  ElMessage.error('上传失败')
  console.error('上传失败', error)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          // 编辑
          await axios.put('/product/brand/update', form)
        } else {
          // 添加
          await axios.post('/product/brand/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getBrandList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取品牌列表
onMounted(() => {
  getBrandList()
})
</script>

<style scoped>
.brand-management {
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

.upload-demo {
  margin-top: 10px;
}
</style>