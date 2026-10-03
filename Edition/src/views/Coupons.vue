<template>
  <div class="coupons-page">
    <div class="coupons-container">
      <h1 class="page-title">{{ $t('coupons.title') }}</h1>

      <div class="coupons-list" v-if="coupons.length">
        <div v-for="coupon in coupons" :key="coupon.id" class="coupon-card">
          <div class="coupon-left">
            <span class="coupon-amount">${{ coupon.couponValue }}</span>
            <span class="coupon-condition">{{ $t('coupons.min', { n: coupon.minAmount }) }}</span>
          </div>
          <div class="coupon-right">
            <h3>{{ coupon.couponName }}</h3>
            <p>{{ couponDesc(coupon) }}</p>
            <span class="coupon-expire">{{ $t('coupons.expires', { date: formatDate(coupon.endTime) }) }}</span>
          </div>
          <el-button type="primary" @click="claimCoupon(coupon)">{{ $t('coupons.claim') }}</el-button>
        </div>
      </div>

      <div class="empty-state" v-else>
        <el-icon size="64" color="#d1d1d1"><Ticket /></el-icon>
        <h2>{{ $t('coupons.noCoupons') }}</h2>
        <p>{{ $t('coupons.checkBack') }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getCoupons, claimCoupon as claimApi } from '@/api'
import { ElMessage } from 'element-plus'
import { Ticket } from '@element-plus/icons-vue'

const { t } = useI18n()
const coupons = ref([])

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

// 后端 MemberCoupon：couponType=2 为折扣券（couponValue 为折扣比例），否则为满减券
function couponDesc(coupon) {
  if (coupon.couponType === 2) {
    return t('coupons.percentOff', { n: Math.round(coupon.couponValue * 100) })
  }
  return t('coupons.amountOff', { value: coupon.couponValue, min: coupon.minAmount })
}

async function loadCoupons() {
  try { const res = await getCoupons(); coupons.value = res.data || [] } catch {}
}

async function claimCoupon(coupon) {
  // 领取接口需要的是优惠券 ID（couponId），而非会员优惠券记录 ID
  try { await claimApi(coupon.couponId); ElMessage.success(t('coupons.claimed')) } catch { ElMessage.error(t('coupons.failedClaim')) }
}

onMounted(loadCoupons)
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.coupons-page { background: $walmart-gray-100; min-height: 100vh; padding: 24px 0; }
.coupons-container { max-width: 800px; margin: 0 auto; padding: 0 24px; }
.page-title { font-size: 28px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 24px; }
.coupon-card { display: flex; align-items: center; background: $walmart-white; border-radius: 12px; margin-bottom: 12px; overflow: hidden;
  .coupon-left { padding: 24px; background: $walmart-blue; color: #fff; text-align: center; min-width: 120px;
    .coupon-amount { display: block; font-size: 28px; font-weight: 800; }
    .coupon-condition { font-size: 12px; opacity: 0.8; }
  }
  .coupon-right { flex: 1; padding: 16px 24px;
    h3 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 4px; }
    p { font-size: 13px; color: $walmart-gray-400; margin-bottom: 4px; }
    .coupon-expire { font-size: 12px; color: $walmart-gray-400; }
  }
}
.empty-state { text-align: center; padding: 80px; background: $walmart-white; border-radius: 12px;
  h2 { font-size: 20px; margin: 16px 0 8px; color: $walmart-gray-600; }
  p { color: $walmart-gray-400; }
}
</style>
