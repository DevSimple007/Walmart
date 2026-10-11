<template>
  <div class="addresses-page">
    <div class="addresses-container">
      <div class="page-header">
        <h1>{{ $t('addresses.title') }}</h1>
        <el-button type="primary" @click="showDialog()">{{ $t('addresses.addNew') }}</el-button>
      </div>

      <div class="addresses-list" v-if="addresses.length">
        <div v-for="addr in addresses" :key="addr.id" class="address-card" :class="{ default: addr.isDefault }">
          <div class="address-content">
            <div class="address-info">
              <span class="address-name">{{ addr.name }} <el-tag v-if="addr.isDefault" size="small" type="success">{{ $t('addresses.defaultTag') }}</el-tag></span>
              <span class="address-detail">{{ fullAddress(addr) }}</span>
              <span class="address-phone">{{ addr.phone }}</span>
            </div>
            <div class="address-actions">
              <el-button text @click="showDialog(addr)">{{ $t('addresses.edit') }}</el-button>
              <el-button text type="danger" @click="handleDelete(addr)">{{ $t('addresses.delete') }}</el-button>
              <el-button v-if="!addr.isDefault" text @click="setDefault(addr)">{{ $t('addresses.setAsDefault') }}</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <p>{{ $t('addresses.noSaved') }}</p>
        <el-button type="primary" @click="showDialog()">{{ $t('addresses.addFirst') }}</el-button>
      </div>

      <el-dialog v-model="dialogVisible" :title="editingAddress ? $t('addresses.dialogEdit') : $t('addresses.dialogAdd')" width="500">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item :label="$t('addresses.nameLabel')" prop="name"><el-input v-model="form.name" /></el-form-item>
          <el-form-item :label="$t('addresses.phoneLabel')" prop="phone"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item :label="$t('addresses.addressLabel')" prop="detailAddress"><el-input v-model="form.detailAddress" type="textarea" :rows="2" /></el-form-item>
          <el-form-item :label="$t('addresses.setDefaultLabel')"><el-switch v-model="form.isDefault" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="handleSave">{{ $t('common.save') }}</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const { t } = useI18n()

const addresses = ref([])
const dialogVisible = ref(false)
const editingAddress = ref(null)
const formRef = ref()

const form = reactive({ name: '', phone: '', detailAddress: '', isDefault: false })

const rules = {
  name: [{ required: true, message: () => t('addresses.pleaseEnterName') }],
  phone: [{ required: true, message: () => t('addresses.pleaseEnterPhone') }],
  detailAddress: [{ required: true, message: () => t('addresses.pleaseEnterAddress') }]
}

async function loadAddresses() {
  try { const res = await getAddressList(); addresses.value = res.data || [] } catch {}
}

// 后端 ShippingAddress 由 province/city/district/address 组成完整地址
function fullAddress(addr) {
  return [addr.province, addr.city, addr.district, addr.address].filter(Boolean).join(' ') || ''
}

function showDialog(addr = null) {
  editingAddress.value = addr
  if (addr) {
    Object.assign(form, {
      name: addr.name,
      phone: addr.phone,
      detailAddress: addr.address || fullAddress(addr),
      isDefault: !!addr.isDefault
    })
  } else {
    Object.assign(form, { name: '', phone: '', detailAddress: '', isDefault: false })
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
    // 转换为后端 ShippingAddress 结构：address 字符串、isDefault 为 0/1
    const payload = {
      name: form.name,
      phone: form.phone,
      address: form.detailAddress,
      isDefault: form.isDefault ? 1 : 0
    }
    if (editingAddress.value) {
      await updateAddress({ id: editingAddress.value.id, ...payload })
    } else {
      await addAddress(payload)
    }
    ElMessage.success(t('addresses.saved'))
    dialogVisible.value = false
    loadAddresses()
  } catch {}
}

async function handleDelete(addr) {
  try {
    await ElMessageBox.confirm(t('addresses.deleteConfirm'), t('addresses.deleteTitle'))
    await deleteAddress(addr.id)
    ElMessage.success(t('addresses.deleted'))
    loadAddresses()
  } catch {}
}

async function setDefault(addr) {
  try { await setDefaultAddress(addr.id); ElMessage.success(t('addresses.defaultUpdated')); loadAddresses() } catch {}
}

onMounted(loadAddresses)
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-gray-200: #e6e6e6;
$walmart-gray-400: #767676; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.addresses-page { background: $walmart-gray-100; min-height: 100vh; padding: 24px 0; }
.addresses-container { max-width: 800px; margin: 0 auto; padding: 0 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;
  h1 { font-size: 28px; font-weight: 700; color: $walmart-gray-600; }
}
.address-card { background: $walmart-white; border-radius: 12px; margin-bottom: 12px; border: 2px solid transparent;
  &.default { border-color: $walmart-blue; }
  .address-content { display: flex; justify-content: space-between; padding: 20px;
    .address-info { display: flex; flex-direction: column; gap: 4px;
      .address-name { font-weight: 600; color: $walmart-gray-600; display: flex; align-items: center; gap: 8px; }
      .address-detail, .address-phone { font-size: 14px; color: $walmart-gray-400; }
    }
  }
}
.empty-state { text-align: center; padding: 60px; background: $walmart-white; border-radius: 12px;
  p { color: $walmart-gray-400; margin-bottom: 16px; }
}
</style>
