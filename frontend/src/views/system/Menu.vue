<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" @click="handleAdd">添加菜单</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="菜单名称">
          <el-input v-model="searchForm.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="menuList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="menuName" label="菜单名称" />
        <el-table-column prop="path" label="菜单路径" />
        <el-table-column prop="component" label="组件路径" />
        <el-table-column prop="perms" label="权限标识" />
        <el-table-column prop="type" label="菜单类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="菜单图标" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
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

    <!-- 添加/编辑菜单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="父菜单" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父菜单">
            <el-option label="根菜单" value="0" />
            <el-option
              v-for="menu in menuOptions"
              :key="menu.id"
              :label="menu.menuName"
              :value="menu.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入菜单路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="perms">
          <el-input v-model="form.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择菜单类型">
            <el-option label="目录" value="0" />
            <el-option label="菜单" value="1" />
            <el-option label="按钮" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入菜单图标" />
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

const menuList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  menuName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加菜单')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  parentId: 0,
  menuName: '',
  path: '',
  component: '',
  perms: '',
  type: 0,
  icon: '',
  sort: 0
})

const rules = reactive<FormRules>({
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
})

const menuOptions = ref<any[]>([])

// 获取菜单类型标签类型
const getTypeTagType = (type: number) => {
  switch (type) {
    case 0:
      return 'info'
    case 1:
      return 'success'
    case 2:
      return 'warning'
    default:
      return ''
  }
}

// 获取菜单类型文本
const getTypeText = (type: number) => {
  switch (type) {
    case 0:
      return '目录'
    case 1:
      return '菜单'
    case 2:
      return '按钮'
    default:
      return ''
  }
}

// 获取菜单列表
const getMenuList = async () => {
  try {
    const res = await axios.get('/system/menu/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize
      }
    })
    menuList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取菜单列表失败')
    console.error('获取菜单列表失败', error)
  }
}

// 获取菜单选项（用于父菜单选择）
const getMenuOptions = async () => {
  try {
    const res = await axios.get('/system/menu/list')
    menuOptions.value = res.data.records
  } catch (error) {
    console.error('获取菜单选项失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getMenuList()
}

// 重置
const resetForm = () => {
  searchForm.menuName = ''
  pagination.currentPage = 1
  getMenuList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getMenuList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getMenuList()
}

// 添加菜单
const handleAdd = () => {
  dialogTitle.value = '添加菜单'
  form.id = ''
  form.parentId = 0
  form.menuName = ''
  form.path = ''
  form.component = ''
  form.perms = ''
  form.type = 0
  form.icon = ''
  form.sort = 0
  dialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑菜单'
  form.id = row.id
  form.parentId = row.parentId
  form.menuName = row.menuName
  form.path = row.path
  form.component = row.component
  form.perms = row.perms
  form.type = row.type
  form.icon = row.icon
  form.sort = row.sort
  dialogVisible.value = true
}

// 删除菜单
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/system/menu/delete/${id}`)
    ElMessage.success('删除成功')
    getMenuList()
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
          await axios.put('/system/menu/update', form)
        } else {
          // 添加
          await axios.post('/system/menu/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getMenuList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 组件挂载时获取菜单列表和菜单选项
onMounted(() => {
  getMenuList()
  getMenuOptions()
})
</script>

<style scoped>
.menu-management {
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