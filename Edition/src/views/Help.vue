<template>
  <div class="help-page">
    <div class="help-container">
      <h1>{{ $t('help.title') }}</h1>
      <p class="help-subtitle">{{ $t('help.subtitle') }}</p>

      <div class="help-search">
        <el-input v-model="searchQuery" :placeholder="$t('help.searchPlaceholder')" size="large" prefix-icon="Search" />
      </div>

      <div class="quick-links">
        <h2>{{ $t('help.popularTopics') }}</h2>
        <div class="links-grid">
          <div class="link-card" v-for="topic in popularTopics" :key="topic.id">
            <el-icon size="24"><component :is="topic.icon" /></el-icon>
            <h3>{{ $t('help.t_' + topic.id) }}</h3>
            <p>{{ $t('help.td_' + topic.id) }}</p>
          </div>
        </div>
      </div>

      <div class="help-categories">
        <h2>{{ $t('help.browseCategory') }}</h2>
        <div class="category-list">
          <div class="category-item" v-for="cat in categories" :key="cat.id">
            <div class="category-header">
              <el-icon size="20"><component :is="cat.icon" /></el-icon>
              <h3>{{ $t('help.c_' + cat.id) }}</h3>
              <el-icon class="expand-icon"><ArrowDown /></el-icon>
            </div>
            <div class="category-links">
              <a v-for="(link, li) in cat.links" :key="li" href="#">{{ $t('help.cl_' + ((cat.id - 1) * 5 + li + 1)) }}</a>
            </div>
          </div>
        </div>
      </div>

      <div class="contact-section">
        <h2>{{ $t('help.stillNeed') }}</h2>
        <div class="contact-options">
          <div class="contact-card">
            <el-icon size="32"><ChatDotRound /></el-icon>
            <h3>{{ $t('help.chat') }}</h3>
            <p>{{ $t('help.chatDesc') }}</p>
            <el-button type="primary">{{ $t('help.startChat') }}</el-button>
          </div>
          <div class="contact-card">
            <el-icon size="32"><Phone /></el-icon>
            <h3>{{ $t('help.call') }}</h3>
            <p>1-800-WALMART (1-800-925-6278)</p>
            <el-button>{{ $t('help.callNow') }}</el-button>
          </div>
          <div class="contact-card">
            <el-icon size="32"><Message /></el-icon>
            <h3>{{ $t('help.email') }}</h3>
            <p>{{ $t('help.emailDesc') }}</p>
            <el-button>{{ $t('help.sendEmail') }}</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ArrowDown, ChatDotRound, Phone, Message, Search, ShoppingCart, User, Van, CreditCard, Location, RefreshRight, Document } from '@element-plus/icons-vue'

const searchQuery = ref('')

const popularTopics = [
  { id: 1, icon: 'Van' },
  { id: 2, icon: 'RefreshRight' },
  { id: 3, icon: 'Location' },
  { id: 4, icon: 'CreditCard' }
]

const categories = [
  { id: 1, icon: 'ShoppingCart', links: [1, 2, 3, 4, 5] },
  { id: 2, icon: 'RefreshRight', links: [1, 2, 3, 4, 5] },
  { id: 3, icon: 'User', links: [1, 2, 3, 4, 5] },
  { id: 4, icon: 'CreditCard', links: [1, 2, 3, 4, 5] },
  { id: 5, icon: 'Document', links: [1, 2, 3, 4, 5] },
  { id: 6, icon: 'Star', links: [1, 2, 3, 4, 5] }
]
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.help-page { background: $walmart-gray-100; min-height: 100vh; padding: 40px 0; }
.help-container { max-width: 900px; margin: 0 auto; padding: 0 24px; }
h1 { font-size: 32px; font-weight: 700; color: $walmart-gray-600; text-align: center; margin-bottom: 8px; }
.help-subtitle { text-align: center; color: $walmart-gray-400; font-size: 16px; margin-bottom: 32px; }
.help-search { max-width: 600px; margin: 0 auto 40px; }
.quick-links { margin-bottom: 40px;
  h2 { font-size: 20px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 20px; }
}
.links-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.link-card { background: $walmart-white; border-radius: 12px; padding: 20px; cursor: pointer;
  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
  .el-icon { color: $walmart-blue; margin-bottom: 12px; }
  h3 { font-size: 14px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 4px; }
  p { font-size: 12px; color: $walmart-gray-400; }
}
.help-categories { margin-bottom: 40px;
  h2 { font-size: 20px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 20px; }
}
.category-list { display: flex; flex-direction: column; gap: 12px; }
.category-item { background: $walmart-white; border-radius: 12px; overflow: hidden; }
.category-header { display: flex; align-items: center; gap: 12px; padding: 16px 20px; cursor: pointer;
  &:hover { background: $walmart-gray-100; }
  .el-icon:first-child { color: $walmart-blue; }
  h3 { flex: 1; font-size: 16px; font-weight: 600; color: $walmart-gray-600; }
}
.category-links { display: flex; flex-wrap: wrap; gap: 12px; padding: 0 20px 16px 52px;
  a { color: $walmart-blue; text-decoration: none; font-size: 14px; &:hover { text-decoration: underline; } }
}
.contact-section { h2 { font-size: 20px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 20px; text-align: center; } }
.contact-options { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.contact-card { background: $walmart-white; border-radius: 12px; padding: 24px; text-align: center;
  .el-icon { color: $walmart-blue; margin-bottom: 12px; }
  h3 { font-size: 16px; font-weight: 600; color: $walmart-gray-600; margin-bottom: 8px; }
  p { font-size: 13px; color: $walmart-gray-400; margin-bottom: 16px; }
}
</style>
