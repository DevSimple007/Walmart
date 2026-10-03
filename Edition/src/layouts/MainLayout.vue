<template>
  <div class="main-layout">
    <!-- Top Banner -->
    <div class="top-banner" v-if="showBanner">
      <div class="banner-content">
        <span>{{ $t('nav.banner') }}</span>
        <button class="banner-close" @click="showBanner = false">
          <el-icon><Close /></el-icon>
        </button>
      </div>
    </div>

    <!-- Header -->
    <header class="main-header">
      <div class="header-container">
        <!-- Logo -->
        <router-link to="/" class="header-logo">
          <img src="https://www.walmart.com/favicon.ico" alt="Walmart" class="logo-spark" />
          <span class="logo-text">Walmart</span>
        </router-link>

        <!-- Pickup/Delivery -->
        <div class="header-location">
          <div class="location-icon">
            <el-icon size="20"><Location /></el-icon>
          </div>
          <div class="location-info">
            <span class="location-label">{{ $t('nav.pickupQuestion') }}</span>
            <span class="location-address">{{ $t('nav.location') }}</span>
          </div>
        </div>

        <!-- Search -->
        <div class="header-search">
          <div class="search-wrapper">
            <el-icon class="search-icon"><Search /></el-icon>
            <input
              v-model="searchQuery"
              type="text"
              class="search-input"
              :placeholder="$t('nav.searchPlaceholder')"
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">
              <el-icon size="20"><Search /></el-icon>
            </button>
          </div>
        </div>

        <!-- Navigation Icons -->
        <div class="header-nav">
          <router-link to="/account" class="nav-item">
            <div class="nav-icon">
              <el-icon size="24"><User /></el-icon>
            </div>
            <span class="nav-label">{{ $t('nav.signIn') }}</span>
            <span class="nav-sublabel">{{ $t('nav.account') }}</span>
          </router-link>

          <router-link to="/orders" class="nav-item">
            <div class="nav-icon">
              <el-icon size="24"><List /></el-icon>
            </div>
            <span class="nav-label">{{ $t('nav.reorder') }}</span>
            <span class="nav-sublabel">{{ $t('nav.myItems') }}</span>
          </router-link>

          <router-link to="/cart" class="nav-item cart-item">
            <div class="nav-icon cart-icon">
              <el-icon size="24"><ShoppingCart /></el-icon>
              <span class="cart-badge" v-if="cartStore.totalCount > 0">
                {{ cartStore.totalCount }}
              </span>
            </div>
            <span class="nav-total">${{ cartStore.totalPrice.toFixed(2) }}</span>
          </router-link>

          <!-- 语言切换 -->
          <button class="nav-item lang-toggle" @click="onToggleLocale" :title="$t('lang.label')">
            <div class="nav-icon lang-badge">{{ currentLang === 'zh' ? 'EN' : '中' }}</div>
            <span class="nav-label">{{ currentLang === 'zh' ? $t('nav.switchToEn') : $t('nav.switchToZh') }}</span>
            <span class="nav-sublabel">{{ $t('lang.label') }}</span>
          </button>
        </div>
      </div>

      <!-- Secondary Nav -->
      <div class="secondary-nav">
        <div class="nav-container">
          <button class="dept-btn" @click="showDepartments = !showDepartments">
            <el-icon><Menu /></el-icon>
            <span>{{ $t('nav.departments') }}</span>
            <el-icon><ArrowDown /></el-icon>
          </button>
          <button class="dept-btn">
            <span>{{ $t('nav.services') }}</span>
            <el-icon><ArrowDown /></el-icon>
          </button>
          <div class="nav-links">
            <router-link to="/shop/deals" class="nav-link">{{ $t('nav.dealsMore') }}</router-link>
            <router-link to="/shop/deals/4th-of-july" class="nav-link">{{ $t('nav.fourthOfJuly') }}</router-link>
            <router-link to="/shop/deals/get-it-fast" class="nav-link">{{ $t('nav.getItFast') }}</router-link>
            <router-link to="/shop/deals/pharmacy" class="nav-link">{{ $t('nav.pharmacy') }}</router-link>
            <router-link to="/shop/deals/new-trending" class="nav-link">{{ $t('nav.newTrending') }}</router-link>
            <router-link to="/shop/deals/back-to-school" class="nav-link">{{ $t('nav.backToSchool') }}</router-link>
            <router-link to="/shop/deals/soccer" class="nav-link">{{ $t('nav.soccer') }}</router-link>
            <router-link to="/shop/deals/meals" class="nav-link">{{ $t('nav.meals') }}</router-link>
            <router-link to="/account/favorites" class="nav-link">{{ $t('nav.myItemsLink') }}</router-link>
            <router-link to="/shop/deals/only-at-walmart" class="nav-link">{{ $t('nav.onlyAtWalmart') }}</router-link>
            <a href="#" class="nav-link">{{ $t('nav.creditCard') }}</a>
            <router-link to="/shop/deals/walmart-plus" class="nav-link">{{ $t('nav.walmartPlus') }}</router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Departments Dropdown -->
    <transition name="dropdown">
      <div class="departments-dropdown" v-if="showDepartments" @click.self="showDepartments = false">
        <div class="departments-panel">
          <div class="dept-sidebar">
            <div
              v-for="dept in departments"
              :key="dept.id"
              class="dept-item"
              :class="{ active: selectedDept === dept.id }"
              @mouseenter="selectedDept = dept.id"
            >
              <el-icon size="20"><component :is="dept.icon" /></el-icon>
              <span>{{ $t('dept.' + dept.id) }}</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="dept-content" v-if="selectedDeptData">
            <h3>{{ $t('dept.' + selectedDeptData.id) }}</h3>
            <div class="dept-links">
              <router-link
                v-for="(link, li) in selectedDeptData.links"
                :key="link.name"
                :to="link.to"
                class="dept-link"
                @click="showDepartments = false"
              >
                {{ $t('dl.' + selectedDeptData.id + '_' + li) }}
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="main-footer">
      <!-- Feedback -->
      <div class="footer-feedback">
        <div class="footer-container">
          <span>{{ $t('footer.feedback') }}</span>
          <button class="feedback-btn">{{ $t('footer.giveFeedback') }}</button>
        </div>
      </div>

      <!-- Footer Links -->
      <div class="footer-links">
        <div class="footer-container">
          <div class="footer-links-list">
            <router-link to="/">{{ $t('footer.allDepartments') }}</router-link>
            <a href="https://www.walmart.com/store/directory" target="_blank">{{ $t('footer.storeDirectory') }}</a>
            <a href="https://careers.walmart.com/" target="_blank">{{ $t('footer.careers') }}</a>
            <a href="https://corporate.walmart.com/" target="_blank">{{ $t('footer.ourCompany') }}</a>
            <a href="https://marketplace.walmart.com/" target="_blank">{{ $t('footer.sellOnWalmart') }}</a>
            <router-link to="/help">{{ $t('footer.help') }}</router-link>
            <a href="https://corporate.walmart.com/recalls" target="_blank">{{ $t('footer.productRecalls') }}</a>
            <a href="#">{{ $t('footer.accessibility') }}</a>
            <a href="#">{{ $t('footer.taxExempt') }}</a>
            <a href="#">{{ $t('footer.getTheApp') }}</a>
            <a href="#">{{ $t('footer.safetyDataSheet') }}</a>
            <a href="#">{{ $t('footer.termsOfUse') }}</a>
            <a href="https://corporate.walmart.com/privacy-security/walmart-privacy-notice" target="_blank">{{ $t('footer.privacyNotice') }}</a>
            <a href="#">{{ $t('footer.californiaSupply') }}</a>
            <a href="#">{{ $t('footer.privacyChoices') }}</a>
            <a href="#">{{ $t('footer.noticeCollection') }}</a>
            <a href="#">{{ $t('footer.adChoices') }}</a>
            <a href="#">{{ $t('footer.healthPrivacy') }}</a>
            <a href="https://www.sparkdriverapp.com/" target="_blank">{{ $t('footer.sparkDriver') }}</a>
            <a href="#">{{ $t('footer.brandShop') }}</a>
            <a href="https://business.walmart.com/" target="_blank">{{ $t('footer.walmartBusiness') }}</a>
            <a href="#">{{ $t('footer.iywyk') }}</a>
            <a href="#">{{ $t('footer.deleteAccount') }}</a>
          </div>
        </div>
      </div>

      <!-- Copyright -->
      <div class="footer-bottom">
        <div class="footer-container">
          <p>{{ $t('footer.copyright') }}</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { getToken } from '@/utils/auth'
import { toggleLocale } from '@/i18n'
import {
  Search, User, ShoppingCart, Menu, ArrowDown, ArrowRight, Close,
  Location, List, Platform, Goods, Suitcase, Collection, Food,
  House, Connection, VideoCamera, Present, School, OfficeBuilding
} from '@element-plus/icons-vue'

const router = useRouter()
const cartStore = useCartStore()
const { locale: i18nLocale } = useI18n()
const currentLang = i18nLocale

function onToggleLocale() {
  toggleLocale()
}

const showBanner = ref(true)
const showDepartments = ref(false)
const searchQuery = ref('')
const selectedDept = ref('electronics')

const departments = ref([
  { id: 'all', name: 'All Departments', icon: 'Menu', links: [] },
  { id: 'summer-deals', name: 'Summer Deals & More', icon: 'Sunny', links: [
    { name: 'All Summer Deals', to: '/shop/deals' },
    { name: 'Outdoor Living', to: '/shop/deals/patio-and-garden' },
    { name: 'Pool & Water Toys', to: '/shop/deals/toys' },
    { name: 'Grilling', to: '/shop/deals/all-home' },
    { name: 'Summer Clothing', to: '/shop/deals/clothing-and-accessories' }
  ]},
  { id: '4th-july', name: '4th of July', icon: 'Flag', links: [
    { name: 'All 4th of July', to: '/shop/deals/4th-of-july' },
    { name: 'Patriotic Clothing', to: '/shop/deals/clothing-and-accessories' },
    { name: 'Fireworks & Sparklers', to: '/shop/deals' },
    { name: 'Party Supplies', to: '/shop/deals' },
    { name: 'Outdoor Furniture', to: '/shop/deals/all-home' }
  ]},
  { id: 'grocery', name: 'Grocery', icon: 'ShoppingCart', links: [
    { name: 'All Grocery', to: '/shop/deals/food' },
    { name: 'Fresh Food', to: '/shop/deals/food' },
    { name: 'Pantry', to: '/shop/deals/food' },
    { name: 'Snacks & Sweet Treats', to: '/shop/deals/food/snacks-and-sweet-treats' },
    { name: 'Beverages', to: '/shop/deals/food' },
    { name: 'Household Essentials', to: '/shop/deals/household-essentials' },
    { name: 'Dinner Tonight', to: '/shop/deals/meals' }
  ]},
  { id: 'clothing', name: 'Clothing, Shoes & Accessories', icon: 'ShoppingBag', links: [
    { name: 'All Clothing', to: '/shop/deals/clothing-and-accessories' },
    { name: "Men's Clothing", to: '/shop/deals/clothing-and-accessories' },
    { name: "Women's Clothing", to: '/shop/deals/clothing-and-accessories' },
    { name: "Kids' Clothing", to: '/shop/deals/clothing-and-accessories' },
    { name: 'Shoes', to: '/shop/deals/clothing-and-accessories' },
    { name: 'Accessories', to: '/shop/deals/clothing-and-accessories' },
    { name: 'Jewelry', to: '/shop/deals/clothing-and-accessories' },
    { name: 'Fashion Deals Under $15', to: '/shop/deals/clothing-and-accessories/deals-under-15' }
  ]},
  { id: 'baby', name: 'Baby & Kids', icon: 'Present', links: [
    { name: 'All Baby', to: '/shop/deals/baby' },
    { name: 'Diapering', to: '/shop/deals/baby' },
    { name: 'Feeding', to: '/shop/deals/baby' },
    { name: 'Nursery', to: '/shop/deals/baby' },
    { name: 'Baby Gear', to: '/shop/deals/baby' },
    { name: "Kids' Rooms", to: '/shop/deals/baby' }
  ]},
  { id: 'pharmacy', name: 'Pharmacy, Health & Wellness', icon: 'FirstAidKit', links: [
    { name: 'All Pharmacy', to: '/shop/deals/pharmacy' },
    { name: 'Prescriptions', to: '/shop/deals/pharmacy' },
    { name: 'Vitamins & Supplements', to: '/shop/deals/pharmacy' },
    { name: 'Personal Care', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Health & Wellness', to: '/shop/deals/pharmacy' }
  ]},
  { id: 'home', name: 'Home, Garden & Tools', icon: 'House', links: [
    { name: 'All Home', to: '/shop/deals/all-home' },
    { name: 'Furniture', to: '/shop/deals/all-home' },
    { name: 'Bedding', to: '/shop/deals/all-home' },
    { name: 'Kitchen', to: '/shop/deals/all-home' },
    { name: 'Home Décor', to: '/shop/deals/all-home' },
    { name: 'Appliances', to: '/shop/deals/all-home' },
    { name: 'Floor Care', to: '/shop/deals/all-home/floor-care' },
    { name: 'Tools', to: '/shop/deals/all-home' }
  ]},
  { id: 'electronics', name: 'Electronics', icon: 'Monitor', links: [
    { name: 'All Electronics', to: '/shop/deals/electronics' },
    { name: 'TVs & Video', to: '/shop/deals/electronics' },
    { name: 'Computers', to: '/shop/deals/electronics' },
    { name: 'Cell Phones', to: '/shop/deals/electronics' },
    { name: 'Cameras', to: '/shop/deals/electronics' },
    { name: 'Audio', to: '/shop/deals/electronics' },
    { name: 'Wearable Technology', to: '/shop/deals/electronics' },
    { name: 'Smart Home', to: '/shop/deals/electronics' }
  ]},
  { id: 'gaming', name: 'Gaming & Movies', icon: 'VideoCamera', links: [
    { name: 'All Gaming', to: '/shop/deals/electronics' },
    { name: 'Video Games', to: '/shop/deals/electronics' },
    { name: 'Gaming Consoles', to: '/shop/deals/electronics' },
    { name: 'Movies & TV Shows', to: '/shop/deals/electronics' },
    { name: 'Music', to: '/shop/deals/electronics' }
  ]},
  { id: 'auto', name: 'Auto & Tires', icon: 'Van', links: [
    { name: 'All Auto', to: '/shop/deals' },
    { name: 'Tires', to: '/shop/deals' },
    { name: 'Auto Parts', to: '/shop/deals' },
    { name: 'Motor Oil & Fluids', to: '/shop/deals' },
    { name: 'Car Electronics', to: '/shop/deals/electronics' }
  ]},
  { id: 'personal-care', name: 'Personal Care', icon: 'MagicStick', links: [
    { name: 'All Personal Care', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Hair Care', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Skin Care', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Oral Care', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Deodorant', to: '/shop/deals/beauty-and-personal-care' }
  ]},
  { id: 'beauty', name: 'Beauty', icon: 'MagicStick', links: [
    { name: 'All Beauty', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Makeup', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Fragrance', to: '/shop/deals/beauty-and-personal-care/fragrance' },
    { name: 'Beauty Tools', to: '/shop/deals/beauty-and-personal-care' },
    { name: 'Nail Care', to: '/shop/deals/beauty-and-personal-care' }
  ]},
  { id: 'toys', name: 'Toys & Outdoor Play', icon: 'Present', links: [
    { name: 'All Toys', to: '/shop/deals/toys' },
    { name: 'Action Figures', to: '/shop/deals/toys' },
    { name: 'Building Toys', to: '/shop/deals/toys' },
    { name: 'Dolls', to: '/shop/deals/toys' },
    { name: 'Games', to: '/shop/deals/toys' },
    { name: 'Outdoor Play', to: '/shop/deals/toys' }
  ]},
  { id: 'household', name: 'Household Essentials', icon: 'ShoppingCart', links: [
    { name: 'All Household', to: '/shop/deals/household-essentials' },
    { name: 'Cleaning Supplies', to: '/shop/deals/household-essentials' },
    { name: 'Paper Products', to: '/shop/deals/household-essentials' },
    { name: 'Laundry', to: '/shop/deals/household-essentials' },
    { name: 'Trash Bags', to: '/shop/deals/household-essentials' }
  ]},
  { id: 'pets', name: 'Pets', icon: 'Postcard', links: [
    { name: 'All Pets', to: '/shop/deals' },
    { name: 'Dog Food', to: '/shop/deals' },
    { name: 'Cat Food', to: '/shop/deals' },
    { name: 'Pet Supplies', to: '/shop/deals' },
    { name: 'Pet Toys', to: '/shop/deals' }
  ]},
  { id: 'school', name: 'School, Office & Art Supplies', icon: 'Document', links: [
    { name: 'All School & Office', to: '/shop/deals/school-office-art' },
    { name: 'Back to School', to: '/shop/deals/back-to-school' },
    { name: 'Office Supplies', to: '/shop/deals/school-office-art' },
    { name: 'Art Supplies', to: '/shop/deals/school-office-art' },
    { name: 'Paper & Notebooks', to: '/shop/deals/school-office-art' }
  ]},
  { id: 'sports', name: 'Sports & Outdoors', icon: 'Van', links: [
    { name: 'All Sports', to: '/shop/deals' },
    { name: 'Exercise & Fitness', to: '/shop/deals' },
    { name: 'Outdoor Recreation', to: '/shop/deals/patio-and-garden' },
    { name: 'Sports Equipment', to: '/shop/deals' },
    { name: 'Fan Shop', to: '/shop/deals' }
  ]},
  { id: 'collectibles', name: 'Collectibles, Books & Music', icon: 'Document', links: [
    { name: 'All Collectibles', to: '/shop/deals' },
    { name: 'Books', to: '/shop/deals' },
    { name: 'Movies & Music', to: '/shop/deals/electronics' },
    { name: 'Video Games', to: '/shop/deals/electronics' },
    { name: 'Collectible Toys', to: '/shop/deals/toys' }
  ]},
  { id: 'party', name: 'Party Supplies', icon: 'Present', links: [
    { name: 'All Party Supplies', to: '/shop/deals' },
    { name: 'Balloons', to: '/shop/deals' },
    { name: 'Tableware', to: '/shop/deals' },
    { name: 'Decorations', to: '/shop/deals' },
    { name: 'Gift Wrap', to: '/shop/deals' }
  ]}
])

const selectedDeptData = computed(() => {
  return departments.value.find(d => d.id === selectedDept.value)
})

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ path: '/search', query: { q: searchQuery.value.trim() } })
  }
}

onMounted(() => {
  if (getToken()) {
    cartStore.loadCart()
  }
})
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc;
$walmart-blue-dark: #004f9a;
$walmart-yellow: #ffc220;
$walmart-gray-100: #f5f5f5;
$walmart-gray-200: #e6e6e6;
$walmart-gray-300: #d1d1d1;
$walmart-gray-400: #767676;
$walmart-gray-500: #46474a;
$walmart-gray-600: #2e2f32;
$walmart-white: #ffffff;

.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

// Top Banner
.top-banner {
  background: $walmart-gray-100;
  padding: 8px 0;
  font-size: 12px;
  color: $walmart-gray-600;

  .banner-content {
    max-width: 1440px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
  }

  .banner-close {
    position: absolute;
    right: 24px;
    background: none;
    border: none;
    cursor: pointer;
    color: $walmart-gray-400;

    &:hover {
      color: $walmart-gray-600;
    }
  }
}

// Header
.main-header {
  background: $walmart-blue;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-logo {
  display: flex;
  align-items: center;
  gap: 4px;
  text-decoration: none;
  flex-shrink: 0;

  .logo-spark {
    width: 32px;
    height: 32px;
  }

  .logo-text {
    font-size: 24px;
    font-weight: 800;
    color: $walmart-yellow;
    letter-spacing: -0.5px;
  }
}

.header-location {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  cursor: pointer;
  flex-shrink: 0;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }

  .location-icon {
    color: $walmart-white;
  }

  .location-info {
    display: flex;
    flex-direction: column;

    .location-label {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.8);
    }

    .location-address {
      font-size: 14px;
      color: $walmart-white;
      font-weight: 600;
    }
  }
}

.header-search {
  flex: 1;
  max-width: 600px;

  .search-wrapper {
    display: flex;
    align-items: center;
    background: $walmart-white;
    border-radius: 20px;
    padding: 0 4px 0 16px;
    height: 44px;
  }

  .search-icon {
    color: $walmart-gray-400;
    margin-right: 8px;
  }

  .search-input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 14px;
    color: $walmart-gray-600;
    background: transparent;

    &::placeholder {
      color: $walmart-gray-400;
    }
  }

  .search-btn {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: $walmart-yellow;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $walmart-gray-600;

    &:hover {
      background: darken($walmart-yellow, 5%);
    }
  }
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px 12px;
    text-decoration: none;
    color: $walmart-white;
    border-radius: 8px;
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }

    .nav-icon {
      position: relative;
    }

    .nav-label {
      font-size: 12px;
      font-weight: 600;
    }

    .nav-sublabel {
      font-size: 10px;
      opacity: 0.8;
    }
  }

  .cart-item {
    position: relative;

    .cart-badge {
      position: absolute;
      top: -4px;
      right: -4px;
      min-width: 18px;
      height: 18px;
      background: $walmart-yellow;
      color: $walmart-gray-600;
      font-size: 11px;
      font-weight: 700;
      border-radius: 9px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 4px;
    }

    .nav-total {
      font-size: 14px;
      font-weight: 700;
    }
  }

  // 语言切换按钮（与导航项风格一致）
  .lang-toggle {
    background: none;
    border: none;
    cursor: pointer;
    font-family: inherit;

    .lang-badge {
      width: 26px;
      height: 26px;
      border-radius: 50%;
      background: $walmart-white;
      color: $walmart-blue;
      font-size: 11px;
      font-weight: 800;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 2px;
    }
  }
}

// Secondary Nav
.secondary-nav {
  background: $walmart-blue;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.dept-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  background: none;
  border: none;
  color: $walmart-white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 16px;

  .nav-link {
    padding: 12px 16px;
    color: $walmart-white;
    text-decoration: none;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    white-space: nowrap;
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
  }
}

// Departments Dropdown
.departments-dropdown {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1001;
  display: flex;
  justify-content: flex-start;
}

.departments-panel {
  display: flex;
  background: $walmart-white;
  margin-top: 100px;
  height: calc(100vh - 100px);
  width: 800px;
  box-shadow: 4px 0 12px rgba(0, 0, 0, 0.15);
}

.dept-sidebar {
  width: 280px;
  background: $walmart-gray-100;
  overflow-y: auto;
  border-right: 1px solid $walmart-gray-200;
}

.dept-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: background 0.2s;
  color: $walmart-gray-600;

  &:hover, &.active {
    background: $walmart-white;
    color: $walmart-blue;
  }

  .arrow {
    margin-left: auto;
  }
}

.dept-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: $walmart-gray-600;
    margin-bottom: 20px;
  }
}

.dept-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;

  .dept-link {
    padding: 8px 12px;
    color: $walmart-blue;
    text-decoration: none;
    font-size: 14px;
    border-radius: 8px;
    transition: background 0.2s;

    &:hover {
      background: $walmart-gray-100;
    }
  }
}

// Main Content
.main-content {
  flex: 1;
  background: $walmart-gray-100;
}

// Footer
.main-footer {
  background: $walmart-blue;
  color: $walmart-white;
  margin-top: auto;
}

.footer-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-feedback {
  background: rgba(255, 255, 255, 0.1);
  padding: 16px 0;

  .footer-container {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
  }

  span {
    font-size: 14px;
  }

  .feedback-btn {
    padding: 6px 16px;
    background: $walmart-white;
    color: $walmart-blue;
    border: none;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;

    &:hover {
      background: rgba(255, 255, 255, 0.9);
    }
  }
}

.footer-links {
  padding: 24px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.footer-links-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 24px;

  a {
    color: rgba(255, 255, 255, 0.8);
    text-decoration: none;
    font-size: 13px;

    &:hover {
      color: $walmart-white;
      text-decoration: underline;
    }
  }
}

.footer-bottom {
  padding: 20px 0;

  p {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    text-align: center;
  }
}

// Transitions
.dropdown-enter-active, .dropdown-leave-active {
  transition: opacity 0.3s;
}

.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
}
</style>
