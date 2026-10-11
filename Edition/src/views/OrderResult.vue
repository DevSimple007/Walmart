<template>
  <div class="order-result-page">
    <div class="order-result-container">
      <div class="result-card">
        <div class="result-icon">
          <el-icon size="40"><Check /></el-icon>
        </div>
        <h1>{{ $t('orderFlow.resultTitle') }}</h1>
        <p class="result-subtitle">{{ $t('orderFlow.resultSubtitle') }}</p>

        <div class="result-info">
          <div class="info-row">
            <span class="info-label">{{ $t('orderFlow.orderNo') }}</span>
            <span class="info-value">
              <span class="mono">{{ orderNo }}</span>
              <el-button text type="primary" @click="copyOrderNo">{{ $t('orderFlow.copyNo') }}</el-button>
            </span>
          </div>
          <div class="info-row">
            <span class="info-label">{{ $t('orderFlow.deliveryWay') }}</span>
            <span class="info-value">{{ $t('orderFlow.fastDelivery') }}</span>
          </div>
        </div>

        <div class="result-tip">
          <el-icon size="20" color="#0071dc"><CircleCheck /></el-icon>
          <div class="tip-text">
            <strong>{{ $t('orderFlow.safeDelivery') }}</strong>
            <p>{{ $t('orderFlow.safeDeliveryDesc') }}</p>
          </div>
        </div>

        <div class="result-actions">
          <el-button type="primary" size="large" @click="router.push('/order/detail/' + orderNo)">
            {{ $t('orderFlow.viewDetail') }}
          </el-button>
          <el-button size="large" @click="router.push('/')">
            {{ $t('orderFlow.backHome') }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { Check, CircleCheck } from '@element-plus/icons-vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const orderNo = route.params.orderNo

function copyOrderNo() {
  const text = String(orderNo ?? '')
  if (navigator.clipboard?.writeText) {
    navigator.clipboard
      .writeText(text)
      .then(() => ElMessage.success(t('orderFlow.copied')))
      .catch(() => ElMessage.error(t('orderFlow.copyFailed')))
  } else {
    ElMessage.info(text)
  }
}
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-blue-dark: #004f9a;
$walmart-green: #2a8703;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;

.order-result-page {
  background: $walmart-gray-100;
  min-height: 100vh;
  padding: 40px 0;
}

.order-result-container {
  max-width: 640px;
  margin: 0 auto;
  padding: 0 24px;
}

.result-card {
  background: $walmart-white;
  border-radius: 12px;
  padding: 40px 32px;
  text-align: center;

  h1 {
    font-size: 24px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 8px;
  }
}

.result-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background: $walmart-green;
  color: $walmart-white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-subtitle {
  font-size: 14px;
  color: $walmart-gray-400;
  margin-bottom: 32px;
}

.result-info {
  text-align: left;
  border: 1px solid $walmart-gray-200;
  border-radius: 12px;
  padding: 8px 20px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid $walmart-gray-100;
  font-size: 14px;

  &:last-child {
    border-bottom: none;
  }

  .info-label {
    color: $walmart-gray-400;
    flex-shrink: 0;
  }

  .info-value {
    color: $walmart-gray-600;
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }

  .mono {
    font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  }
}

.result-tip {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  text-align: left;
  background: $walmart-gray-100;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;

  .tip-text {
    strong {
      display: block;
      font-size: 14px;
      color: $walmart-gray-600;
      margin-bottom: 4px;
    }

    p {
      font-size: 13px;
      color: $walmart-gray-400;
      line-height: 1.5;
    }
  }
}

.result-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .el-button {
    width: 100%;
    height: 48px;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 600;
    margin-left: 0;
  }
}
</style>
