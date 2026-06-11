<template>
  <div class="address-page">
    <van-nav-bar title="收货地址" left-arrow @click-left="$router.back()" />

    <!-- Address list -->
    <div v-if="addresses.length && !showForm" class="address-list">
      <div
        class="address-card"
        v-for="addr in addresses"
        :key="addr.id"
      >
        <div class="address-info">
          <div class="address-row">
            <span class="addr-name">{{ addr.name }}</span>
            <span class="addr-phone">{{ addr.phone }}</span>
            <span v-if="addr.isDefault" class="addr-default-tag">默认</span>
          </div>
          <div class="addr-detail">{{ formatAddress(addr) }}</div>
        </div>
        <div class="address-actions">
          <button class="action-btn" @click="handleSetDefault(addr)" v-if="!addr.isDefault">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
            设为默认
          </button>
          <button class="action-btn" @click="showEditForm(addr)">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
            编辑
          </button>
          <button class="action-btn delete-btn" @click="handleDelete(addr)">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
            </svg>
            删除
          </button>
        </div>
      </div>
    </div>

    <div v-if="!addresses.length && !showForm" class="empty-state">
      <div class="empty-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
          <circle cx="12" cy="10" r="3"/>
        </svg>
      </div>
      <p>暂无收货地址</p>
    </div>

    <!-- Add/Edit address form -->
    <div v-if="showForm" class="address-form">
      <div class="form-title">{{ editingId ? '编辑地址' : '新增地址' }}</div>
      <div class="form-fields">
        <van-field v-model="form.name" label="收货人" placeholder="请输入收货人姓名" :border="false" />
        <van-field v-model="form.phone" label="手机号" placeholder="请输入手机号" type="tel" maxlength="11" :border="false" />
        <van-field v-model="form.province" label="省份" placeholder="请输入省份" :border="false" />
        <van-field v-model="form.city" label="城市" placeholder="请输入城市" :border="false" />
        <van-field v-model="form.district" label="区/县" placeholder="请输入区/县" :border="false" />
        <van-field v-model="form.address" label="详细地址" placeholder="请输入详细地址" :border="false" />
        <div class="field-row default-row">
          <span class="field-label">设为默认</span>
          <van-switch v-model="form.isDefault" size="20px" />
        </div>
      </div>
      <div class="form-actions">
        <van-button class="btn-cancel" @click="cancelForm">取消</van-button>
        <van-button class="btn-save" type="primary" :loading="saving" @click="saveAddress">保存</van-button>
      </div>
    </div>

    <!-- Bottom add button -->
    <div class="bottom-bar safe-bottom" v-if="!showForm">
      <van-button class="add-btn" type="primary" block @click="showAddForm">新增地址</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api'

const addresses = ref([])
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)

const defaultForm = () => ({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  isDefault: false
})

const form = ref(defaultForm())

async function loadAddresses() {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch {
    showToast('加载地址失败')
  }
}

function formatAddress(addr) {
  return [addr.province, addr.city, addr.district, addr.address].filter(Boolean).join(' ')
}

function showAddForm() {
  editingId.value = null
  form.value = defaultForm()
  showForm.value = true
}

function showEditForm(addr) {
  editingId.value = addr.id
  form.value = {
    name: addr.name,
    phone: addr.phone,
    province: addr.province || '',
    city: addr.city || '',
    district: addr.district || '',
    address: addr.address || '',
    isDefault: !!addr.isDefault
  }
  showForm.value = true
}

function cancelForm() {
  showForm.value = false
  editingId.value = null
}

async function saveAddress() {
  if (!form.value.name.trim()) { showToast('请输入收货人姓名'); return }
  if (!form.value.phone.trim() || form.value.phone.length !== 11) { showToast('请输入正确的手机号'); return }
  if (!form.value.address.trim()) { showToast('请输入详细地址'); return }

  saving.value = true
  try {
    if (editingId.value) {
      await updateAddress({ id: editingId.value, ...form.value })
      showToast('已更新')
    } else {
      await addAddress(form.value)
      showToast('已添加')
    }
    showForm.value = false
    editingId.value = null
    await loadAddresses()
  } catch {
    showToast('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleSetDefault(addr) {
  try {
    await setDefaultAddress(addr.id)
    showToast('已设为默认')
    await loadAddresses()
  } catch {
    showToast('设置失败')
  }
}

async function handleDelete(addr) {
  try {
    await showConfirmDialog({ title: '提示', message: '确定要删除这个地址吗？' })
    await deleteAddress(addr.id)
    showToast('已删除')
    await loadAddresses()
  } catch {
    // user cancelled or delete failed
  }
}

onMounted(loadAddresses)
</script>

<style lang="scss" scoped>
.address-page {
  background: var(--shop-bg-secondary);
  min-height: 100vh;
  padding-bottom: 80px;
}

.address-list {
  padding: var(--shop-gap) var(--shop-page-pad) 0;
}

.address-card {
  background: var(--shop-card-bg);
  border-radius: var(--shop-card-radius);
  padding: 16px;
  margin-bottom: 10px;
}

.address-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.addr-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--shop-text-primary);
}

.addr-phone {
  font-size: 14px;
  color: var(--shop-text-secondary);
}

.addr-default-tag {
  font-size: 10px;
  color: var(--shop-accent);
  background: var(--shop-accent-light);
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 600;
}

.addr-detail {
  font-size: 14px;
  color: var(--shop-text-secondary);
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 0.5px solid var(--shop-separator);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  font-size: 12px;
  color: var(--shop-text-secondary);
  cursor: pointer;
  font-family: var(--shop-font);
  padding: 4px 0;

  &:active {
    color: var(--shop-accent);
  }

  &.delete-btn:active {
    color: var(--shop-danger);
  }
}

.address-form {
  margin: var(--shop-gap) var(--shop-page-pad) 0;
  background: var(--shop-card-bg);
  border-radius: var(--shop-card-radius);
  padding: 16px;
}

.form-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--shop-text-primary);
  margin-bottom: 16px;
}

.form-fields {
  display: flex;
  flex-direction: column;
}

.field-row {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 0.5px solid var(--shop-separator);

  &:last-child {
    border-bottom: none;
  }
}

.field-label {
  font-size: 14px;
  color: var(--shop-text-secondary);
  width: 72px;
  flex-shrink: 0;
}

.default-row {
  justify-content: space-between;
  padding: 12px 16px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.btn-cancel {
  flex: 1;
  height: 44px;
  border-radius: 10px;
  border: 1.5px solid var(--shop-separator);
  background: transparent;
  color: var(--shop-text-primary);
  font-size: 15px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;

  &:active {
    background: var(--shop-bg-secondary);
  }
}

.btn-save {
  flex: 1;
  height: 44px;
  border-radius: 10px;
  border: none;
  background: var(--shop-accent);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;

  &:active {
    background: var(--shop-accent-hover);
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px var(--shop-page-pad);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 0.5px solid var(--shop-separator);
}

.add-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  border: none;
  background: var(--shop-accent);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  font-family: var(--shop-font);
  cursor: pointer;

  &:active {
    background: var(--shop-accent-hover);
  }
}
</style>
