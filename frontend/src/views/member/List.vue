<template>
  <div class="member-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员管理</span>
          <el-button type="primary" @click="handleAdd">添加会员</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="会员名称">
          <el-input v-model="searchForm.memberName" placeholder="请输入会员名称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="memberList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="memberName" label="会员名称" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="levelName" label="会员等级" width="100" />
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="balance" label="余额" width="100">
          <template #default="scope">
            ¥{{ scope.row.balance }}
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

    <!-- 添加/编辑会员对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="会员名称" prop="memberName">
          <el-input v-model="form.memberName" placeholder="请输入会员名称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="会员等级" prop="levelId">
          <el-select v-model="form.levelId" placeholder="请选择会员等级">
            <el-option
              v-for="level in levelOptions"
              :key="level.id"
              :label="level.levelName"
              :value="level.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="积分" prop="points">
          <el-input-number v-model="form.points" :min="0" />
        </el-form-item>
        <el-form-item label="余额" prop="balance">
          <el-input v-model="form.balance" type="number" placeholder="请输入余额" />
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

const memberList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  memberName: '',
  phone: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加会员')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  memberName: '',
  phone: '',
  email: '',
  address: '',
  levelId: '',
  levelName: '',
  points: 0,
  balance: 0
})

const rules = reactive<FormRules>({
  memberName: [
    { required: true, message: '请输入会员名称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' }
  ],
  levelId: [
    { required: true, message: '请选择会员等级', trigger: 'change' }
  ],
  points: [
    { required: true, message: '请输入积分', trigger: 'blur' }
  ],
  balance: [
    { required: true, message: '请输入余额', trigger: 'blur' }
  ]
})

const levelOptions = ref<any[]>([])

// 获取会员列表
const getMemberList = async () => {
  try {
    const res = await axios.get('/member/list/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
        memberName: searchForm.memberName || undefined,
        phone: searchForm.phone || undefined
      }
    })
    memberList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取会员列表失败')
    console.error('获取会员列表失败', error)
  }
}

// 获取会员等级选项
const getLevelOptions = async () => {
  try {
    const res = await axios.get('/member/level/list')
    levelOptions.value = res.data.records
  } catch (error) {
    console.error('获取会员等级选项失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getMemberList()
}

// 重置
const resetForm = () => {
  searchForm.memberName = ''
  searchForm.phone = ''
  pagination.currentPage = 1
  getMemberList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getMemberList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getMemberList()
}

// 添加会员
const handleAdd = () => {
  dialogTitle.value = '添加会员'
  form.id = ''
  form.memberName = ''
  form.phone = ''
  form.email = ''
  form.address = ''
  form.levelId = ''
  form.levelName = ''
  form.points = 0
  form.balance = 0
  dialogVisible.value = true
}

// 编辑会员
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑会员'
  form.id = row.id
  form.memberName = row.memberName
  form.phone = row.phone
  form.email = row.email
  form.address = row.address
  form.levelId = row.levelId
  form.levelName = row.levelName
  form.points = row.points
  form.balance = row.balance
  dialogVisible.value = true
}

// 删除会员
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/member/list/delete/${id}`)
    ElMessage.success('删除成功')
    getMemberList()
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
          await axios.put('/member/list/update', form)
        } else {
          // 添加
          await axios.post('/member/list/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getMemberList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getMemberList()
  getLevelOptions()
})
</script>

<style scoped>
.member-management {
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
