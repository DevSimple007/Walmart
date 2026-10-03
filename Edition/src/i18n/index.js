import { createI18n } from 'vue-i18n'
import zh from './zh'
import en from './en'

const LANG_KEY = 'walmart_lang'
// 默认中文，已保存过偏好的用户读取本地存储
const saved = typeof localStorage !== 'undefined' ? localStorage.getItem(LANG_KEY) : null
const locale = saved === 'en' ? 'en' : 'zh'

const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale,
  fallbackLocale: 'zh',
  messages: {
    zh,
    en
  }
})

export function setLocale(lang) {
  i18n.global.locale.value = lang
  try { localStorage.setItem(LANG_KEY, lang) } catch {}
  document.documentElement.setAttribute('lang', lang === 'zh' ? 'zh-CN' : 'en')
}

export function toggleLocale() {
  setLocale(i18n.global.locale.value === 'zh' ? 'en' : 'zh')
}

setLocale(locale)

export default i18n
