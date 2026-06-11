<template>
  <div class="settings-page">
    <el-card>
      <template #header>
        <div class="card-header">设置</div>
      </template>
      <el-form label-width="140px">
        <el-form-item label="系统主题">
          <el-select v-model="theme" style="width: 220px">
            <el-option label="默认蓝色" value="default" />
            <el-option label="浅色商务" value="light" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const userStore = useUserStore()
const theme = ref('default')

const loadSettings = async () => {
  const res = await axios.get('/system/user/settings')
  theme.value = res.data.theme || 'default'
}

const saveSettings = async () => {
  await axios.put('/system/user/settings', { theme: theme.value })
  ElMessage.success('设置已保存')
  if (userStore.userInfo) {
    userStore.setUserInfo({ ...userStore.userInfo, theme: theme.value })
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.settings-page {
  padding: 20px;
}

.card-header {
  font-weight: 600;
}
</style>
