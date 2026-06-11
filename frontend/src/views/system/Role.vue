<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">添加角色</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="roleList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色代码" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handlePermission(scope.row)">权限配置</el-button>
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

    <!-- 添加/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色代码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色代码" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入角色描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限配置对话框 -->
    <el-dialog v-model="permissionDialogVisible" title="权限配置">
      <el-tree
        :data="menuTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedKeys"
        @check-change="handleCheckChange"
        style="height: 400px; overflow-y: auto"
      >
        <template #default="{ node }">
          <span>{{ node.label }}</span>
        </template>
      </el-tree>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePermission">保存</el-button>
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

const roleList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  roleName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')
const formRef = ref<FormInstance>()
const form = reactive({
  id: '',
  roleName: '',
  roleCode: '',
  description: ''
})

const rules = reactive<FormRules>({
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色代码', trigger: 'blur' }
  ]
})

// 权限配置相关
const permissionDialogVisible = ref(false)
const menuTree = ref<any[]>([])
const checkedKeys = ref<number[]>([])
const currentRoleId = ref<number>(0)

// 获取角色列表
const getRoleList = async () => {
  try {
    const res = await axios.get('/system/role/list', {
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize
      }
    })
    roleList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取角色列表失败')
    console.error('获取角色列表失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getRoleList()
}

// 重置
const resetForm = () => {
  searchForm.roleName = ''
  pagination.currentPage = 1
  getRoleList()
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getRoleList()
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
  getRoleList()
}

// 添加角色
const handleAdd = () => {
  dialogTitle.value = '添加角色'
  form.id = ''
  form.roleName = ''
  form.roleCode = ''
  form.description = ''
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑角色'
  form.id = row.id
  form.roleName = row.roleName
  form.roleCode = row.roleCode
  form.description = row.description
  dialogVisible.value = true
}

// 删除角色
const handleDelete = async (id: string) => {
  try {
    await axios.delete(`/system/role/delete/${id}`)
    ElMessage.success('删除成功')
    getRoleList()
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
          await axios.put('/system/role/update', form)
        } else {
          // 添加
          await axios.post('/system/role/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getRoleList()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error('操作失败', error)
      }
    }
  })
}

// 获取菜单树
const getMenuTree = async () => {
  try {
    const res = await axios.get('/system/menu/tree')
    menuTree.value = res.data
  } catch (error) {
    ElMessage.error('获取菜单树失败')
    console.error('获取菜单树失败', error)
  }
}

// 权限配置
const handlePermission = async (row: any) => {
  currentRoleId.value = row.id
  checkedKeys.value = []
  // 获取角色的权限
  try {
    const res = await axios.get(`/system/menu/list?roleId=${row.id}`)
    checkedKeys.value = res.data.map((item: any) => item.id)
  } catch (error) {
    console.error('获取角色权限失败', error)
  }
  // 获取菜单树
  await getMenuTree()
  permissionDialogVisible.value = true
}

// 处理勾选变化
const handleCheckChange = (_data: any, _checked: boolean, _indeterminate: boolean) => {
  // 可以在这里处理勾选逻辑
}

// 保存权限配置
const handleSavePermission = async () => {
  try {
    await axios.post('/system/role/saveRoleMenus', {
      roleId: currentRoleId.value,
      menuIds: checkedKeys.value
    })
    ElMessage.success('权限配置保存成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('权限配置保存失败')
    console.error('权限配置保存失败', error)
  }
}

// 组件挂载时获取角色列表
onMounted(() => {
  getRoleList()
})
</script>

<style scoped>
.role-management {
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