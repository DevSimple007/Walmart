<template>
  <div class="wm-form-container">
    <el-form
      :model="form"
      :rules="rules"
      ref="formRef"
      :label-width="effectiveLabelWidth"
      :label-position="labelPosition"
      class="wm-form"
      :size="effectiveSize"
    >
      <slot></slot>

      <!-- 表单底部 -->
      <div class="form-footer" v-if="showFooter">
        <slot name="footer">
          <el-button @click="handleCancel" class="wm-btn" :size="effectiveSize">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            class="wm-btn wm-btn-primary"
            :loading="loading"
            :size="effectiveSize"
          >
            {{ submitText }}
          </el-button>
        </slot>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

const props = defineProps({
  form: { type: Object, required: true },
  rules: { type: Object as () => FormRules, default: () => ({}) },
  labelWidth: { type: [String, Number], default: '100px' },
  size: { type: String, default: 'default', validator: (v: string) => ['default', 'small', 'large'].includes(v) },
  showFooter: { type: Boolean, default: true },
  submitText: { type: String, default: '提交' },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['submit', 'cancel'])
const formRef = ref<FormInstance>()

// 移动端自动调整
const effectiveLabelWidth = computed(() => {
  return isMobile.value ? '80px' : props.labelWidth
})

const effectiveSize = computed(() => {
  return isMobile.value ? 'large' : props.size
})

const labelPosition = computed(() => {
  return isMobile.value ? 'top' : 'right'
})

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) emit('submit')
  })
}

const handleCancel = () => emit('cancel')

const resetFields = () => formRef.value?.resetFields()
const validate = async () => await formRef.value?.validate()

defineExpose({ resetFields, validate })
</script>

<style scoped>
.wm-form-container {
  width: 100%;
}

.wm-form {
  background: var(--wm-bg-card);
  border-radius: var(--wm-radius-md);
  padding: var(--wm-space-lg);
  box-shadow: var(--wm-shadow-md);
  border: 1px solid rgba(255, 255, 255, 0.6);
  transition: box-shadow var(--wm-transition-normal);
}

.wm-form:hover {
  box-shadow: var(--wm-shadow-lg);
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: var(--wm-space-sm);
  margin-top: var(--wm-space-xl);
  padding-top: var(--wm-space-lg);
  border-top: 1px solid var(--wm-border-light);
}

/* ============================
   平板端
   ============================ */
@media screen and (max-width: 1024px) {
  .wm-form {
    padding: var(--wm-space-md);
  }

  .form-footer {
    margin-top: var(--wm-space-lg);
    padding-top: var(--wm-space-md);
  }
}

/* ============================
   移动端
   ============================ */
@media screen and (max-width: 768px) {
  .wm-form {
    padding: var(--wm-space-md);
    border-radius: var(--wm-radius-sm);
  }

  .form-footer {
    flex-direction: column;
    align-items: stretch;
    gap: var(--wm-space-sm);
    margin-top: var(--wm-space-lg);
    padding-top: var(--wm-space-md);
  }

  .form-footer .el-button {
    width: 100%;
    min-height: 48px;
  }
}
</style>
