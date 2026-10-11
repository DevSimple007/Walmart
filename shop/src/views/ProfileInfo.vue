<template>
  <!-- ProfileInfo — 个人信息（头像/昵称/性别/生日/邮箱可编辑），风格对齐既有子页面 -->
  <header class="fixed top-0 w-full z-50 bg-surface-container-lowest/80 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] pt-safe">
    <div class="h-14 pl-margin pr-[96px] flex items-center gap-space-sm">
      <button aria-label="Back" class="w-11 h-11 flex items-center justify-center text-on-surface hover:text-primary transition-colors -ml-space-xs" @click="$router.back()">
        <span class="material-symbols-outlined text-[24px]">arrow_back_ios_new</span>
      </button>
      <h1 class="font-headline-sm text-headline-sm text-on-surface tracking-tight font-bold">个人信息</h1>
    </div>
  </header>

  <main class="flex-1 flex flex-col relative w-full pt-14 bg-background max-w-[540px] mx-auto min-h-screen">
    <div class="px-margin pt-space-sm pb-28 flex flex-col gap-space-md">
      <!-- 头像（点击上传，即时保存） -->
      <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex items-center justify-between">
        <span class="font-body-md text-body-md text-on-surface">头像</span>
        <van-uploader :after-read="onAvatarRead" :max-count="1" :preview-image="false" accept="image/*">
          <div class="relative cursor-pointer active:opacity-70 transition-opacity">
            <img v-if="form.avatar" alt="用户头像" class="w-14 h-14 rounded-full object-cover" :src="form.avatar" />
            <div v-else class="w-14 h-14 rounded-full bg-surface-container flex items-center justify-center overflow-hidden">
              <span class="material-symbols-outlined text-outline text-[26px]">person</span>
            </div>
            <div class="absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-primary-container text-on-primary flex items-center justify-center shadow-sm">
              <span class="material-symbols-outlined text-[12px]">photo_camera</span>
            </div>
          </div>
        </van-uploader>
      </div>

      <!-- 基本信息 -->
      <div class="bg-surface-container-lowest rounded-xl p-space-md shadow-sm flex flex-col gap-space-md">
        <div class="flex items-center justify-between gap-space-md">
          <span class="font-body-md text-body-md text-on-surface shrink-0">昵称</span>
          <input v-model="form.memberName" class="flex-1 min-w-0 text-right bg-transparent border-none outline-none font-body-md text-body-md text-on-surface placeholder:text-outline-variant" maxlength="20" placeholder="请输入昵称" type="text" />
        </div>
        <div class="flex items-center justify-between gap-space-md">
          <span class="font-body-md text-body-md text-on-surface shrink-0">手机号</span>
          <span class="font-body-md text-body-md text-on-surface-variant">{{ maskedPhone }}</span>
        </div>
        <button class="flex items-center justify-between gap-space-md" @click="showGenderSheet = true">
          <span class="font-body-md text-body-md text-on-surface shrink-0">性别</span>
          <span class="flex items-center gap-1">
            <span class="font-body-md text-body-md" :class="form.gender ? 'text-on-surface' : 'text-outline'">{{ genderText }}</span>
            <span class="material-symbols-outlined text-outline text-[18px]">chevron_right</span>
          </span>
        </button>
        <button class="flex items-center justify-between gap-space-md" @click="openBirthdayPicker">
          <span class="font-body-md text-body-md text-on-surface shrink-0">生日</span>
          <span class="flex items-center gap-1">
            <span class="font-body-md text-body-md" :class="form.birthday ? 'text-on-surface' : 'text-outline'">{{ form.birthday || '请选择' }}</span>
            <span class="material-symbols-outlined text-outline text-[18px]">chevron_right</span>
          </span>
        </button>
        <div class="flex items-center justify-between gap-space-md">
          <span class="font-body-md text-body-md text-on-surface shrink-0">邮箱</span>
          <input v-model="form.email" class="flex-1 min-w-0 text-right bg-transparent border-none outline-none font-body-md text-body-md text-on-surface placeholder:text-outline-variant" maxlength="50" placeholder="选填" type="email" />
        </div>
      </div>

      <!-- 保存 -->
      <button class="h-12 rounded-full bg-primary-container text-on-primary font-headline-sm text-headline-sm font-bold shadow-md active:scale-[0.98] transition-all disabled:opacity-60" :disabled="saving" @click="saveProfile">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </div>

    <!-- 性别选择 -->
    <van-action-sheet v-model:show="showGenderSheet" :actions="genderActions" cancel-text="取消" teleport="body" :z-index="3000" @select="onGenderSelect" />
    <!-- 生日选择 -->
    <van-popup v-model:show="showBirthdayPicker" position="bottom" round teleport="body" :z-index="3000" :overlay-style="{ zIndex: 2999 }">
      <van-date-picker
        v-model="tempBirthday"
        title="选择生日"
        :min-date="minBirthdayDate"
        :max-date="maxBirthdayDate"
        @confirm="onBirthdayConfirm"
        @cancel="showBirthdayPicker = false"
      />
    </van-popup>
  </main>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast } from 'vant'
import { updateCustomerInfo, uploadFile } from '@/api'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({ memberName: '', email: '', gender: 0, birthday: '', avatar: '' })
const saving = ref(false)
const showGenderSheet = ref(false)
const showBirthdayPicker = ref(false)

const genderActions = [
  { name: '男', value: 1 },
  { name: '女', value: 2 },
  { name: '保密', value: 0 },
]
const genderText = computed(() => (form.gender === 1 ? '男' : form.gender === 2 ? '女' : '保密'))
const maskedPhone = computed(() => {
  const p = userStore.userInfo?.phone || ''
  return p ? p.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2') : ''
})

// 生日范围：1900-01-01 ~ 今天
const maxBirthdayDate = new Date()
const minBirthdayDate = new Date(1900, 0, 1)
const tempBirthday = ref([])

function openBirthdayPicker() {
  const base = form.birthday ? new Date(form.birthday) : new Date(2000, 0, 1)
  tempBirthday.value = [
    String(base.getFullYear()),
    String(base.getMonth() + 1).padStart(2, '0'),
    String(base.getDate()).padStart(2, '0'),
  ]
  showBirthdayPicker.value = true
}

function onBirthdayConfirm({ selectedValues }) {
  form.birthday = selectedValues.join('-')
  showBirthdayPicker.value = false
}

function onGenderSelect(action) {
  form.gender = action.value
  showGenderSheet.value = false
}

// 头像：选择后立即上传并保存
async function onAvatarRead(file) {
  if (!file || !file.file) return
  if (file.file.size > 5 * 1024 * 1024) {
    showToast('图片大小不能超过5MB')
    return
  }
  try {
    const fd = new FormData()
    fd.append('file', file.file)
    fd.append('category', '用户头像')
    const res = await uploadFile(fd)
    const url = res.data?.url
    if (!url) throw new Error('上传失败')
    form.avatar = url
    const saved = await updateCustomerInfo({ avatar: url })
    userStore.userInfo = saved.data
    localStorage.setItem('user_info', JSON.stringify(saved.data))
    showToast({ message: '头像已更新', icon: 'success' })
  } catch (e) {
    showToast(e.message || '头像上传失败')
  }
}

function applyUserInfo(info) {
  if (!info) return
  form.memberName = info.memberName || ''
  form.email = info.email || ''
  form.gender = info.gender ?? 0
  form.birthday = info.birthday || ''
  form.avatar = info.avatar || ''
}

async function saveProfile() {
  if (!form.memberName.trim()) {
    showToast('请输入昵称')
    return
  }
  if (form.email && !/^[\w.%+-]+@[\w.-]+\.[A-Za-z]{2,}$/.test(form.email)) {
    showToast('邮箱格式不正确')
    return
  }
  saving.value = true
  try {
    const res = await updateCustomerInfo({
      memberName: form.memberName.trim(),
      email: form.email.trim(),
      gender: form.gender,
      birthday: form.birthday || null,
      avatar: form.avatar || null,
    })
    userStore.userInfo = res.data
    localStorage.setItem('user_info', JSON.stringify(res.data))
    showToast({ message: '保存成功', icon: 'success' })
    router.back()
  } catch (e) {
    showToast(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  applyUserInfo(userStore.userInfo)
  if (userStore.token) {
    await userStore.fetchUserInfo()
    applyUserInfo(userStore.userInfo)
  }
})
</script>
