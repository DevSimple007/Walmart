<template>
  <div class="pharmacy-page">
    <!-- Hero -->
    <div class="pharmacy-hero">
      <div class="hero-content">
        <h1>{{ $t('pharmacy.title') }}</h1>
        <p>{{ $t('pharmacy.subtitle') }}</p>
        <el-button type="primary" size="large">{{ $t('pharmacy.transferRx') }}</el-button>
      </div>
    </div>

    <div class="pharmacy-container">
      <!-- Quick Actions -->
      <div class="quick-actions">
        <div class="action-card" v-for="action in quickActions" :key="action.key">
          <el-icon size="32"><component :is="action.icon" /></el-icon>
          <h3>{{ $t('pharmacy.' + action.key) }}</h3>
          <p>{{ $t('pharmacy.' + action.descKey) }}</p>
        </div>
      </div>

      <!-- Services -->
      <div class="services-section">
        <h2>{{ $t('pharmacy.servicesTitle') }}</h2>
        <div class="services-grid">
          <div class="service-card" v-for="service in services" :key="service.key">
            <h3>{{ $t('pharmacy.' + service.key) }}</h3>
            <p>{{ $t('pharmacy.' + service.descKey) }}</p>
            <a href="#">{{ $t('pharmacy.learnMore') }}</a>
          </div>
        </div>
      </div>

      <!-- Health Resources -->
      <div class="health-section">
        <h2>{{ $t('pharmacy.healthTitle') }}</h2>
        <div class="health-grid">
          <div class="health-card" v-for="item in healthResources" :key="item.key">
            <h3>{{ $t('pharmacy.' + item.key) }}</h3>
            <p>{{ $t('pharmacy.' + item.descKey) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Location, FirstAidKit, Timer, Document } from '@element-plus/icons-vue'

const quickActions = [
  { key: 'qa_1', descKey: 'qd_1', icon: 'Document' },
  { key: 'qa_2', descKey: 'qd_2', icon: 'Timer' },
  { key: 'qa_3', descKey: 'qd_3', icon: 'Location' },
  { key: 'qa_4', descKey: 'qd_4', icon: 'FirstAidKit' }
]

const services = [
  { key: 's_1', descKey: 'sd_1' },
  { key: 's_2', descKey: 'sd_2' },
  { key: 's_3', descKey: 'sd_3' },
  { key: 's_4', descKey: 'sd_4' },
  { key: 's_5', descKey: 'sd_5' },
  { key: 's_6', descKey: 'sd_6' }
]

const healthResources = [
  { key: 'h_1', descKey: 'hd_1' },
  { key: 'h_2', descKey: 'hd_2' },
  { key: 'h_3', descKey: 'hd_3' },
  { key: 'h_4', descKey: 'hd_4' }
]
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-blue-dark: #004f9a; $walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6; $walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.pharmacy-page { background: $walmart-gray-100; }
.pharmacy-hero { background: linear-gradient(135deg, $walmart-blue 0%, $walmart-blue-dark 100%); padding: 60px 0; color: $walmart-white;
  .hero-content { max-width: 1440px; margin: 0 auto; padding: 0 24px;
    h1 { font-size: 36px; font-weight: 800; margin-bottom: 12px; }
    p { font-size: 18px; opacity: 0.9; margin-bottom: 24px; }
  }
}
.pharmacy-container { max-width: 1440px; margin: 0 auto; padding: 40px 24px; }
.quick-actions { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 40px; }
.action-card { background: $walmart-white; border-radius: 12px; padding: 24px; text-align: center; cursor: pointer;
  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
  .el-icon { color: $walmart-blue; margin-bottom: 12px; }
  h3 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 4px; }
  p { font-size: 13px; color: $walmart-gray-400; }
}
.services-section, .health-section { margin-bottom: 40px;
  h2 { font-size: 22px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 20px; }
}
.services-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.service-card { background: $walmart-white; border-radius: 12px; padding: 20px;
  h3 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 8px; }
  p { font-size: 14px; color: $walmart-gray-400; margin-bottom: 12px; }
  a { color: $walmart-blue; font-size: 14px; font-weight: 600; text-decoration: none; &:hover { text-decoration: underline; } }
}
.health-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.health-card { background: $walmart-white; border-radius: 12px; padding: 20px;
  h3 { font-size: 15px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 6px; }
  p { font-size: 13px; color: $walmart-gray-400; }
}
</style>
