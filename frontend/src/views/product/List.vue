<template>
  <div class="product-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">添加商品</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类">
            <el-option label="全部" value="" />
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="国际品牌">
          <el-select v-model="searchForm.isInternational" placeholder="全部" style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="折扣价">
          <el-input v-model="searchForm.minOriginalPrice" type="number" placeholder="最低" style="width: 110px" />
          <span style="margin: 0 6px;">-</span>
          <el-input v-model="searchForm.maxOriginalPrice" type="number" placeholder="最高" style="width: 110px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="productList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image
              v-if="scope.row.imageUrl"
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
              preview-teleported
            />
            <span v-else style="color: #ccc;">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column label="折扣价" width="100">
          <template #default="scope">
            {{ scope.row.originalPrice ? '¥' + scope.row.originalPrice : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="国际品牌" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isInternational === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.isInternational === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '在售' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="handleStatus(scope.row.id, scope.row.status === 1 ? 0 : 1)"
            >
              {{ scope.row.status === 1 ? '下架' : '上架' }}
            </el-button>
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

    <!-- 添加/编辑商品对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model="form.price" type="number" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="折扣价" prop="originalPrice">
          <el-input v-model="form.originalPrice" type="number" placeholder="划线原价，须大于售价；留空表示无折扣" />
        </el-form-item>
        <el-form-item label="国际品牌" prop="isInternational">
          <el-checkbox v-model="form.isInternational" :true-value="1" :false-value="0">该商品为国际品牌</el-checkbox>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="在售" value="1" />
            <el-option label="下架" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="图片上传">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleUploadRemove"
            :file-list="fileList"
            list-type="picture-card"
            :limit="1"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png文件，且不超过5MB，最多1张
              </div>
            </template>
          </el-upload>
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
import { ref, reactive, computed, onMounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const productList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  productName: '',
  categoryId: '',
  isInternational: '' as number | '',
  minOriginalPrice: '',
  maxOriginalPrice: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  productName: '',
  categoryId: '',
  price: '',
  originalPrice: '',
  isInternational: 0,
  stock: 0,
  status: 1,
  description: '',
  imageUrl: ''
})

const rules = reactive<FormRules>({
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

const categoryOptions = ref<any[]>([])
const fileList = ref<any[]>([])

// 上传地址（带分类和商品名参数）
const uploadUrl = computed(() => {
  const params = new URLSearchParams()
  if (form.categoryId) {
    const cat = categoryOptions.value.find(c => c.id === form.categoryId)
    if (cat) params.append('category', cat.categoryName)
  }
  if (form.productName) params.append('productName', form.productName)
  const qs = params.toString()
  return '/api/upload' + (qs ? '?' + qs : '')
})

// 获取商品列表
const getProductList = async () => {
  try {
    const params: Record<string, any> = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    }
    if (searchForm.productName) params.productName = searchForm.productName
    if (searchForm.categoryId !== '') params.categoryId = searchForm.categoryId
    if (searchForm.isInternational !== '') params.isInternational = searchForm.isInternational
    if (searchForm.minOriginalPrice !== '') params.minOriginalPrice = searchForm.minOriginalPrice
    if (searchForm.maxOriginalPrice !== '') params.maxOriginalPrice = searchForm.maxOriginalPrice
    const res = await axios.get('/product/list/list', { params })
    productList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    console.error('获取商品列表失败', error)
  }
}

// 获取分类选项（后台列表接口分页，这里改用分类树并拍平，避免只拿到前 10 个）
const getCategoryOptions = async () => {
  try {
    const res = await axios.get('/product/category/tree')
    const flat: any[] = []
    const walk = (nodes: any[]) => {
      (nodes || []).forEach((node: any) => {
        flat.push({ id: node.id, categoryName: node.categoryName })
        if (node.children?.length) walk(node.children)
      })
    }
    walk(res.data || [])
    categoryOptions.value = flat
  } catch (error) {
    console.error('获取分类选项失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getProductList()
}

// 重置
const resetForm = () => {
  searchForm.productName = ''
  searchForm.categoryId = ''
  searchForm.isInternational = ''
  searchForm.minOriginalPrice = ''
  searchForm.maxOriginalPrice = ''
  pagination.currentPage = 1
  getProductList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getProductList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getProductList()
}

// 添加商品
const handleAdd = () => {
  dialogTitle.value = '添加商品'
  form.id = ''
  form.productName = ''
  form.categoryId = ''
  form.price = ''
  form.originalPrice = ''
  form.isInternational = 0
  form.stock = 0
  form.status = 1
  form.description = ''
  form.imageUrl = ''
  fileList.value = []
  dialogVisible.value = true
}

// 编辑商品
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑商品'
  form.id = row.id
  form.productName = row.productName
  form.categoryId = row.categoryId
  form.price = row.price
  form.originalPrice = row.originalPrice ?? ''
  form.isInternational = row.isInternational ?? 0
  form.stock = row.stock
  form.status = row.status
  form.description = row.description
  form.imageUrl = row.imageUrl || ''
  fileList.value = row.imageUrl ? [{ name: row.imageUrl.split('/').pop(), url: row.imageUrl }] : []
  dialogVisible.value = true
}

// 删除商品
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/product/list/delete/${id}`)
    ElMessage.success('删除成功')
    getProductList()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败', error)
  }
}

// 商品上下架
const handleStatus = async (id: string, status: number) => {
  try {
    await axios.put(`/product/list/updateStatus/${id}`, null, { params: { status } })
    ElMessage.success(status === 1 ? '上架成功' : '下架成功')
    getProductList()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败', error)
  }
}

// 图片上传成功
const handleUploadSuccess = (response: any) => {
  if (response.code === 200 || response.code === 0) {
    form.imageUrl = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 图片移除
const handleUploadRemove = () => {
  form.imageUrl = ''
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
          await axios.put('/product/list/update', form)
        } else {
          // 添加
          await axios.post('/product/list/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getProductList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getProductList()
  getCategoryOptions()
})
</script>

<style scoped>
.product-management {
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