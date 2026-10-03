<template>
  <div class="walmart-plus-page">
      <!-- Hero -->
      <div class="plus-hero">
        <div class="hero-content">
          <div class="plus-badge">{{ $t('plus.badge') }}</div>
          <h1>{{ $t('plus.heroTitle') }}</h1>
          <p>{{ $t('plus.heroSub') }}</p>
          <div class="hero-actions">
            <el-button type="primary" size="large">{{ $t('plus.startTrial') }}</el-button>
            <span class="trial-note">{{ $t('plus.trialNote') }}</span>
          </div>
        </div>
      </div>

      <div class="plus-container">
        <!-- Benefits -->
        <div class="benefits-section">
          <h2>{{ $t('plus.benefitsTitle') }}</h2>
          <div class="benefits-grid">
            <div class="benefit-card" v-for="benefit in benefits" :key="benefit.key">
              <div class="benefit-icon" :style="{ background: benefit.color }">
                <el-icon size="24"><component :is="benefit.icon" /></el-icon>
              </div>
              <h3>{{ $t('plus.' + benefit.key) }}</h3>
              <p>{{ $t('plus.' + benefit.descKey) }}</p>
            </div>
          </div>
        </div>

        <!-- How it works -->
        <div class="how-section">
          <h2>{{ $t('plus.howTitle') }}</h2>
          <div class="steps">
            <div class="step" v-for="(step, i) in steps" :key="i">
              <div class="step-number">{{ i + 1 }}</div>
              <h3>{{ $t('plus.step_' + (i + 1)) }}</h3>
              <p>{{ $t('plus.stepd_' + (i + 1)) }}</p>
            </div>
          </div>
        </div>

        <!-- Pricing -->
        <div class="pricing-section">
          <h2>{{ $t('plus.pricingTitle') }}</h2>
          <div class="pricing-cards">
            <div class="pricing-card">
              <h3>{{ $t('plus.monthly') }}</h3>
              <div class="price">$12.95</div>
              <span class="period">{{ $t('plus.perMonth') }}</span>
              <el-button type="primary" size="large">{{ $t('plus.startFreeTrial') }}</el-button>
            </div>
            <div class="pricing-card popular">
              <div class="popular-badge">{{ $t('plus.bestValue') }}</div>
              <h3>{{ $t('plus.annual') }}</h3>
              <div class="price">$98</div>
              <span class="period">{{ $t('plus.perYear') }}</span>
              <p class="savings">{{ $t('plus.savingsPerYear') }}</p>
              <el-button type="primary" size="large">{{ $t('plus.startFreeTrial') }}</el-button>
            </div>
          </div>
        </div>

        <!-- FAQ -->
        <div class="faq-section">
          <h2>{{ $t('plus.faqTitle') }}</h2>
          <div class="faq-list">
            <div class="faq-item" v-for="(faq, i) in faqs" :key="i">
              <h4>{{ $t('plus.q_' + (i + 1)) }}</h4>
              <p>{{ $t('plus.a_' + (i + 1)) }}</p>
            </div>
          </div>
        </div>
      </div>
  </div>
</template>

<script setup>
import { Van, Location, CreditCard, Clock, Ticket, CircleCheck } from '@element-plus/icons-vue'

const benefits = [
  { key: 'b_1', descKey: 'bd_1', icon: 'Van', color: '#e3f2fd' },
  { key: 'b_2', descKey: 'bd_2', icon: 'Location', color: '#e8f5e9' },
  { key: 'b_3', descKey: 'bd_3', icon: 'Ticket', color: '#fff3e0' },
  { key: 'b_4', descKey: 'bd_4', icon: 'Clock', color: '#fce4ec' },
  { key: 'b_5', descKey: 'bd_5', icon: 'CreditCard', color: '#f3e5f5' },
  { key: 'b_6', descKey: 'bd_6', icon: 'CircleCheck', color: '#e0f7fa' }
]

const steps = [1, 2, 3]

const faqs = [1, 2, 3, 4]
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-blue-dark: #004f9a; $walmart-yellow: #ffc220;
$walmart-gray-100: #f5f5f5; $walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.walmart-plus-page { background: $walmart-gray-100; }
.plus-hero { background: linear-gradient(135deg, $walmart-blue 0%, #004c99 50%, $walmart-blue-dark 100%); padding: 80px 0; color: $walmart-white; text-align: center;
  .hero-content { max-width: 700px; margin: 0 auto; padding: 0 24px;
    .plus-badge { display: inline-block; padding: 8px 20px; background: $walmart-yellow; color: $walmart-gray-600; font-size: 18px; font-weight: 800; border-radius: 24px; margin-bottom: 24px; }
    h1 { font-size: 42px; font-weight: 800; margin-bottom: 12px; }
    p { font-size: 18px; opacity: 0.9; margin-bottom: 24px; }
    .trial-note { display: block; margin-top: 12px; font-size: 13px; opacity: 0.8; }
  }
}
.plus-container { max-width: 1000px; margin: 0 auto; padding: 48px 24px; }
.benefits-section, .how-section, .pricing-section, .faq-section { margin-bottom: 48px;
  h2 { font-size: 24px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 24px; text-align: center; }
}
.benefits-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.benefit-card { background: $walmart-white; border-radius: 12px; padding: 24px; text-align: center;
  .benefit-icon { width: 56px; height: 56px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; color: $walmart-blue; }
  h3 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 6px; }
  p { font-size: 13px; color: $walmart-gray-400; }
}
.steps { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; text-align: center; }
.step { .step-number { width: 48px; height: 48px; border-radius: 50%; background: $walmart-blue; color: $walmart-white; font-size: 20px; font-weight: 700; display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; }
  h3 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 6px; }
  p { font-size: 14px; color: $walmart-gray-400; }
}
.pricing-cards { display: grid; grid-template-columns: repeat(2, 1fr); gap: 24px; max-width: 600px; margin: 0 auto; }
.pricing-card { background: $walmart-white; border-radius: 16px; padding: 32px; text-align: center; border: 2px solid $walmart-gray-200;
  &.popular { border-color: $walmart-blue; position: relative;
    .popular-badge { position: absolute; top: -12px; left: 50%; transform: translateX(-50%); background: $walmart-blue; color: $walmart-white; padding: 4px 16px; border-radius: 12px; font-size: 12px; font-weight: 600; }
  }
  h3 { font-size: 18px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 8px; }
  .price { font-size: 36px; font-weight: 800; color: $walmart-gray-600; }
  .period { font-size: 14px; color: $walmart-gray-400; }
  .savings { color: #2a8703; font-weight: 600; margin: 8px 0 16px; }
}
.faq-list { display: flex; flex-direction: column; gap: 16px; }
.faq-item { background: $walmart-white; border-radius: 12px; padding: 20px;
  h4 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 8px; }
  p { font-size: 14px; color: $walmart-gray-400; line-height: 1.6; }
}
</style>
