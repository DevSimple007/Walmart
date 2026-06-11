<template>
  <div class="level-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员等级管理</span>
          <el-button type="primary" @click="handleAdd">添加会员等级</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="等级名称">
          <el-input v-model="searchForm.levelName" placeholder="请输入等级名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="levelList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="levelName" label="等级名称" />
        <el-table-column prop="minPoints" label="最小积分" width="100" />
        <el-table-column prop="maxPoints" label="最大积分" width="100" />
        <el-table-column prop="discount" label="折扣" width="80">
          <template #default="scope">
            {{ scope.row.discount }}折
          </template>
        </el-table-column>
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

    <!-- 添加/编辑会员等级对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="form.levelName" placeholder="请输入等级名称" />
        </el-form-item>
        <el-form-item label="最小积分" prop="minPoints">
          <el-input-number v-model="form.minPoints" :min="0" />
        </el-form-item>
        <el-form-item label="最大积分" prop="maxPoints">
          <el-input-number v-model="form.maxPoints" :min="0" />
        </el-form-item>
        <el-form-item label="折扣" prop="discount">
          <el-input v-model="form.discount" type="number" placeholder="请输入折扣" />
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

const levelList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  levelName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加会员等级')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  levelName: '',
  minPoints: 0,
  maxPoints: 0,
  discount: 1.00,
  status: 1
})

const rules = reactive<FormRules>({
  levelName: [
    { required: true, message: '请输入等级名称', trigger: 'blur' }
  ],
  minPoints: [
    { required: true, message: '请输入最小积分', trigger: 'blur' }
  ],
  maxPoints: [
    { required: true, message: '请输入最大积分', trigger: 'blur' }
  ],
  discount: [
    { required: true, message: '请输入折扣', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 获取会员等级列表
const getLevelList = async () => {
  try {
    const res = await axios.get('/member/level/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize
      }
    })
    levelList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取会员等级列表失败')
    console.error('获取会员等级列表失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getLevelList()
}

// 重置
const resetForm = () => {
  searchForm.levelName = ''
  pagination.currentPage = 1
  getLevelList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getLevelList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getLevelList()
}

// 添加会员等级
const handleAdd = () => {
  dialogTitle.value = '添加会员等级'
  form.id = ''
  form.levelName = ''
  form.minPoints = 0
  form.maxPoints = 0
  form.discount = 1.00
  form.status = 1
  dialogVisible.value = true
}

// 编辑会员等级
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑会员等级'
  form.id = row.id
  form.levelName = row.levelName
  form.minPoints = row.minPoints
  form.maxPoints = row.maxPoints
  form.discount = row.discount
  form.status = row.status
  dialogVisible.value = true
}

// 删除会员等级
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/member/level/delete/${id}`)
    ElMessage.success('删除成功')
    getLevelList()
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
        const payload = {
          id: form.id ? Number(form.id) : undefined,
          levelName: form.levelName,
          minPoints: Number(form.minPoints),
          discount: Number(form.discount),
          status: Number(form.status)
        }
        if (form.id) {
          await axios.put('/member/level/update', payload)
        } else {
          await axios.post('/member/level/add', payload)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getLevelList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取会员等级列表
onMounted(() => {
  getLevelList()
})
</script>

<style scoped>
.level-management {
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