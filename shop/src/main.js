import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
// 引入 Vant 完整样式：函数式调用的组件（showConfirmDialog/showToast 等）不会自动注入样式，
// 缺少 .van-popup 基础样式会导致弹窗以 static 定位渲染在页面底部（与底部导航重合）
import 'vant/lib/index.css'
import './styles/global.scss'
import './styles/tailwind.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
