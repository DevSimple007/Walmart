<template>
  <div class="store-directory-page">
    <div class="page-container">
      <h1>{{ $t('store.title') }}</h1>
      <p class="subtitle">{{ $t('store.subtitle') }}</p>

      <!-- Search -->
      <div class="store-search">
        <el-input v-model="searchQuery" :placeholder="$t('store.searchPlaceholder')" size="large" prefix-icon="Location">
          <template #append>
            <el-button type="primary" @click="searchStores">{{ $t('store.findStores') }}</el-button>
          </template>
        </el-input>
      </div>

      <!-- Store Type Filter -->
      <div class="store-filters">
        <el-radio-group v-model="storeType">
          <el-radio-button label="all">{{ $t('store.filterAll') }}</el-radio-button>
          <el-radio-button label="supercenter">{{ $t('store.supercenter') }}</el-radio-button>
          <el-radio-button label="neighborhood">{{ $t('store.neighborhood') }}</el-radio-button>
          <el-radio-button label="sams">{{ $t('store.sams') }}</el-radio-button>
        </el-radio-group>
      </div>

      <!-- Store List -->
      <div class="store-list">
        <div class="store-card" v-for="store in stores" :key="store.id">
          <div class="store-type" :class="store.type">{{ $t('store.' + store.type) }}</div>
          <h3>{{ $t('store.' + store.type) }}</h3>
          <p class="store-address">{{ store.address }}</p>
          <p class="store-phone">{{ store.phone }}</p>
          <div class="store-hours">
            <span class="label">{{ $t('store.hours') }}</span>
            <span>{{ $t('store.openHours', { time: store.hours }) }}</span>
          </div>
          <div class="store-services">
            <el-tag v-for="service in store.services" :key="service" size="small" type="info">{{ $t('store.' + service) }}</el-tag>
          </div>
          <div class="store-actions">
            <el-button text type="primary">{{ $t('store.directions') }}</el-button>
            <el-button text type="primary">{{ $t('store.storeDetails') }}</el-button>
          </div>
        </div>
      </div>

      <!-- Map Placeholder -->
      <div class="store-map">
        <div class="map-placeholder">
          <el-icon size="48" color="#d1d1d1"><Location /></el-icon>
          <p>{{ $t('store.mapPlaceholder') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Location } from '@element-plus/icons-vue'

const searchQuery = ref('')
const storeType = ref('all')

const stores = [
  { id: 1, type: 'supercenter', address: '900 Academy Drive, Sacramento, CA 95829', phone: '(916) 439-4620', hours: '6:00 AM - 11:00 PM', services: ['svcGrocery', 'svcPharmacy', 'svcAuto', 'svcVision', 'svcPhoto'] },
  { id: 2, type: 'supercenter', address: '8281 Florin Road, Sacramento, CA 95828', phone: '(916) 399-0200', hours: '6:00 AM - 11:00 PM', services: ['svcGrocery', 'svcPharmacy', 'svcAuto', 'svcGarden'] },
  { id: 3, type: 'neighborhood', address: '4350 Arden Way, Sacramento, CA 95823', phone: '(916) 480-5700', hours: '7:00 AM - 11:00 PM', services: ['svcGrocery', 'svcPharmacy'] },
  { id: 4, type: 'supercenter', address: '6655 Stockton Blvd, Sacramento, CA 95824', phone: '(916) 452-0611', hours: '6:00 AM - 11:00 PM', services: ['svcGrocery', 'svcPharmacy', 'svcAuto', 'svcTire'] }
]

function searchStores() {
  // Search functionality
}
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.store-directory-page { background: $walmart-gray-100; min-height: 100vh; padding: 24px 0; }
.page-container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
h1 { font-size: 28px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 8px; }
.subtitle { color: $walmart-gray-400; margin-bottom: 24px; }
.store-search { max-width: 600px; margin-bottom: 20px; }
.store-filters { margin-bottom: 24px; }
.store-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; margin-bottom: 24px; }
.store-card { background: $walmart-white; border-radius: 12px; padding: 20px;
  .store-type { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: 700; margin-bottom: 8px;
    &.supercenter { background: #e3f2fd; color: $walmart-blue; }
    &.neighborhood { background: #e8f5e9; color: #2a8703; }
    &.sams { background: #fff3e0; color: #e65100; }
  }
  h3 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 4px; }
  .store-address, .store-phone { font-size: 14px; color: $walmart-gray-400; margin-bottom: 4px; }
  .store-hours { font-size: 13px; color: $walmart-gray-600; margin: 8px 0;
    .label { font-weight: 600; margin-right: 4px; }
  }
  .store-services { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 12px; }
}
.store-map { background: $walmart-white; border-radius: 12px; padding: 60px; text-align: center;
  .map-placeholder { display: flex; flex-direction: column; align-items: center; gap: 12px;
    p { color: $walmart-gray-400; }
  }
}
</style>
