<template>
  <div class="category-icon-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>首页分类图标管理</span>
          <el-button type="primary" @click="handleAdd">添加图标</el-button>
        </div>
      </template>

      <el-table :data="iconList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图标" width="80">
          <template #default="scope">
            <el-image
              v-if="scope.row.iconUrl"
              :src="scope.row.iconUrl"
              style="width: 50px; height: 50px; border-radius: 8px"
              fit="cover"
            />
            <div v-else class="no-icon" :style="{ background: scope.row.color + '20', color: scope.row.color }">
              {{ scope.row.name?.charAt(0) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="显示名称" />
        <el-table-column prop="linkCategoryId" label="关联分类ID" width="100" />
        <el-table-column prop="color" label="品牌色" width="100">
          <template #default="scope">
            <div style="display:flex;align-items:center;gap:8px;">
              <div :style="{ width: '20px', height: '20px', borderRadius: '4px', background: scope.row.color }"></div>
              <span>{{ scope.row.color }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="handleUpload(scope.row)">上传图标</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="显示名称" prop="name">
          <el-input v-model="form.name" placeholder="首页显示的分类名称" />
        </el-form-item>
        <el-form-item label="关联分类ID" prop="linkCategoryId">
          <el-input-number v-model="form.linkCategoryId" :min="0" placeholder="点击后跳转到的商品分类ID" />
          <div style="font-size:12px;color:#999;margin-top:4px;">点击图标后跳转到的商品分类ID，0表示不跳转</div>
        </el-form-item>
        <el-form-item label="品牌色" prop="color">
          <el-color-picker v-model="form.color" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="8" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="显示" :value="1" />
            <el-option label="隐藏" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标图片">
          <div style="display:flex;align-items:center;gap:12px;">
            <el-image
              v-if="form.iconUrl"
              :src="form.iconUrl"
              style="width: 64px; height: 64px; border-radius: 8px"
              fit="cover"
            />
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="onUploadSuccess"
              accept="image/*"
            >
              <el-button size="small">上传图片</el-button>
            </el-upload>
          </div>
          <div style="font-size:12px;color:#999;margin-top:4px;">建议尺寸 100x100，支持 jpg/png</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'

const iconList = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('编辑图标')
const formRef = ref()

const form = reactive({
  id: null as number | null,
  name: '',
  iconUrl: '',
  linkCategoryId: 0,
  color: '#0071DC',
  sort: 0,
  status: 1
})

const uploadUrl = '/api/upload'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const loadList = async () => {
  try {
    const res = await axios.get('/homeCategory/list')
    iconList.value = res.data?.data || res.data || []
  } catch {
    ElMessage.error('获取列表失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加首页图标'
  form.id = null
  form.name = ''
  form.iconUrl = ''
  form.linkCategoryId = 0
  form.color = '#0071DC'
  form.sort = iconList.value.length + 1
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑首页图标'
  form.id = row.id
  form.name = row.name
  form.iconUrl = row.iconUrl || ''
  form.linkCategoryId = row.linkCategoryId || 0
  form.color = row.color || '#0071DC'
  form.sort = row.sort || 0
  form.status = row.status ?? 1
  dialogVisible.value = true
}

const handleUpload = (row: any) => {
  form.id = row.id
  form.name = row.name
  form.iconUrl = row.iconUrl || ''
  form.linkCategoryId = row.linkCategoryId || 0
  form.color = row.color || '#0071DC'
  form.sort = row.sort || 0
  form.status = row.status ?? 1
  dialogTitle.value = '上传图标 - ' + row.name
  dialogVisible.value = true
}

const onUploadSuccess = (response: any) => {
  if (response.code === 200) {
    form.iconUrl = response.data?.url || ''
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleSubmit = async () => {
  try {
    if (form.id) {
      await axios.put('/homeCategory/update', form)
    } else {
      await axios.post('/homeCategory/add', form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadList()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id: number) => {
  try {
    await axios.delete(`/homeCategory/delete/${id}`)
    ElMessage.success('删除成功')
    loadList()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.category-icon-management {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.no-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
}
</style>
