/** @type {import('tailwindcss').Config} */
// 主题严格取自 Stitch 设计稿 (everyday_value_h5_commerce/DESIGN.md + code.html 内联 tailwind.config)
// 颜色 / 字号 / 间距 / 圆角均为设计稿原值，禁止改动
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  corePlugins: {
    // 关闭 preflight，避免与项目原有 global.scss 及 Vant 组件样式冲突
    preflight: false
  },
  theme: {
    extend: {
      colors: {
        'secondary-fixed': '#ffdf9f',
        'on-tertiary-fixed-variant': '#930007',
        'inverse-primary': '#a9c7ff',
        'on-primary': '#ffffff',
        'secondary-container': '#fcc01d',
        error: '#ba1a1a',
        'on-tertiary-fixed': '#410001',
        'surface-container-highest': '#dee3eb',
        primary: '#0059af',
        'on-primary-fixed': '#001b3d',
        'inverse-surface': '#2c3137',
        'on-tertiary-container': '#fff8f7',
        'surface-tint': '#005db7',
        surface: '#f7f9ff',
        'on-secondary-container': '#6c5000',
        'inverse-on-surface': '#edf1f9',
        'on-surface-variant': '#414753',
        background: '#f7f9ff',
        'tertiary-fixed': '#ffdad5',
        'on-secondary': '#ffffff',
        'primary-fixed': '#d6e3ff',
        'tertiary-fixed-dim': '#ffb4aa',
        'surface-container': '#eaeef6',
        'on-error': '#ffffff',
        'primary-container': '#0071dc',
        'secondary-fixed-dim': '#f9bd19',
        outline: '#717784',
        tertiary: '#b8000c',
        'surface-container-low': '#f0f4fc',
        'on-background': '#171c22',
        'on-tertiary': '#ffffff',
        secondary: '#795900',
        'tertiary-container': '#e70213',
        'surface-variant': '#dee3eb',
        'surface-dim': '#d6dae2',
        'on-secondary-fixed': '#261a00',
        'on-primary-fixed-variant': '#00468c',
        'outline-variant': '#c1c6d5',
        'on-error-container': '#93000a',
        'on-surface': '#171c22',
        'error-container': '#ffdad6',
        'primary-fixed-dim': '#a9c7ff',
        'on-secondary-fixed-variant': '#5b4300',
        'surface-container-high': '#e4e8f0',
        'on-primary-container': '#f9f9ff',
        'surface-bright': '#f7f9ff',
        'surface-container-lowest': '#ffffff'
      },
      borderRadius: {
        DEFAULT: '0.25rem',
        lg: '0.5rem',
        xl: '0.75rem',
        full: '9999px'
      },
      spacing: {
        'space-sm': '0.5rem',
        'space-xs': '0.25rem',
        'space-lg': '1rem',
        'space-md': '0.75rem',
        margin: '0.75rem',
        'space-xl': '1.5rem',
        'gutter-md': '0.75rem',
        'margin-sm': '0.5rem',
        gutter: '0.5rem'
      },
      fontFamily: {
        'display-hero': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'headline-sm': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'body-sm': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'headline-lg': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'price-currency': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'price-lg': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'badge-label': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'body-md': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'tab-label': ['Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif']
      },
      fontSize: {
        'display-hero': ['28px', { lineHeight: '34px', letterSpacing: '-0.02em', fontWeight: '800' }],
        'headline-sm': ['16px', { lineHeight: '22px', fontWeight: '700' }],
        'body-sm': ['12px', { lineHeight: '16px', fontWeight: '400' }],
        'headline-lg': ['20px', { lineHeight: '26px', fontWeight: '700' }],
        'price-currency': ['12px', { lineHeight: '14px', fontWeight: '700' }],
        'price-lg': ['20px', { lineHeight: '24px', fontWeight: '800' }],
        'badge-label': ['10px', { lineHeight: '12px', fontWeight: '700' }],
        'body-md': ['14px', { lineHeight: '20px', fontWeight: '500' }],
        'tab-label': ['11px', { lineHeight: '14px', fontWeight: '600' }]
      }
    }
  },
  plugins: [
    function ({ addUtilities }) {
      addUtilities({
        // 安全区（设计稿使用 pb-safe / pt-safe，CDN 版未定义，这里补全）
        '.pb-safe': { paddingBottom: 'env(safe-area-inset-bottom, 0px)' },
        '.pt-safe': { paddingTop: 'env(safe-area-inset-top, 0px)' },
        // 隐藏滚动条
        '.scrollbar-none': {
          '-ms-overflow-style': 'none',
          'scrollbar-width': 'none',
          '&::-webkit-scrollbar': { display: 'none' }
        },
        '.no-scrollbar': {
          '-ms-overflow-style': 'none',
          'scrollbar-width': 'none',
          '&::-webkit-scrollbar': { display: 'none' }
        }
      })
    }
  ]
}
