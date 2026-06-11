<template>
  <div class="wm-error-container" v-if="visible">
    <div class="wm-error">
      <div class="error-icon">
        <el-icon><Warning /></el-icon>
      </div>
      <div class="error-content">
        <h3 class="error-title">{{ title }}</h3>
        <p class="error-message">{{ message }}</p>
        <div class="error-actions">
          <el-button type="primary" @click="handleClose" class="wm-btn wm-btn-primary">
            确定
          </el-button>
          <el-button @click="handleRetry" v-if="showRetry" class="wm-btn">
            重试
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { Warning } from '@element-plus/icons-vue'

defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '错误'
  },
  message: {
    type: String,
    default: '操作失败，请稍后重试'
  },
  showRetry: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'retry'])

const handleClose = () => {
  emit('close')
}

const handleRetry = () => {
  emit('retry')
}
</script>

<style scoped>
.wm-error-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  transition: all 0.3s ease;
}

.wm-error {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  padding: 24px;
  max-width: 400px;
  width: 90%;
  display: flex;
  gap: 20px;
  transition: all 0.3s ease;
}

.error-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #fef0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  color: #f56c6c;
  flex-shrink: 0;
}

.error-content {
  flex: 1;
}

.error-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.error-message {
  font-size: 14px;
  color: #606266;
  margin: 0 0 20px 0;
  line-height: 1.5;
}

.error-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .wm-error {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .error-actions {
    width: 100%;
    flex-direction: column;
  }
  
  .error-actions .el-button {
    width: 100%;
  }
}
</style>