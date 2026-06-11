<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <div class="card-header">个人中心</div>
      </template>
      <el-form :model="profileForm" label-width="100px" class="profile-form">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="profileForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存资料</el-button>
        </el-form-item>
      </el-form>
      <el-divider />
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="changePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useUserStore } from '../store/user'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const profileForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const loadProfile = async () => {
  const res = await axios.get('/system/user/profile')
  const info = res.data
  profileForm.username = info.username || ''
  profileForm.nickname = info.nickname || ''
  profileForm.email = info.email || ''
  profileForm.phone = info.phone || ''
  userStore.setUserInfo(info)
}

const saveProfile = async () => {
  await axios.put('/system/user/profile', {
    nickname: profileForm.nickname,
    email: profileForm.email,
    phone: profileForm.phone
  })
  ElMessage.success('资料已保存')
  await loadProfile()
}

const changePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  await axios.put('/system/user/password', passwordForm)
  ElMessage.success('密码修改成功')
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-page {
  padding: 20px;
}

.card-header {
  font-weight: 600;
}
</style>
