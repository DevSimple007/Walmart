<template>
  <div class="wm-table-container">
    <!-- 表格工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <slot name="toolbar-left"></slot>
      </div>
      <div class="toolbar-right">
        <el-button
          type="primary"
          size="small"
          @click="handleExport"
          class="wm-btn wm-btn-primary"
          v-if="showExport"
        >
          <el-icon><Download /></el-icon>
          <span class="btn-text">导出Excel</span>
        </el-button>
        <slot name="toolbar-right"></slot>
      </div>
    </div>

    <!-- 表格 (桌面/平板) -->
    <div class="table-scroll-wrapper" ref="tableWrapperRef">
      <el-table
        :data="tableData"
        :height="tableHeight"
        :stripe="stripe"
        :border="border"
        :default-sort="defaultSort"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        class="wm-table"
        v-loading="loading"
        element-loading-text="加载中..."
      >
        <slot></slot>
        <el-table-column
          label="操作"
          :width="actionColWidth"
          fixed="right"
          v-if="showAction"
        >
          <template #default="scope">
            <slot name="action" :row="scope.row"></slot>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="table-pagination" v-if="showPagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="pageSizes"
        :layout="paginationLayout"
        :total="pagination.total"
        :small="isMobile"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps, defineEmits, ref, onMounted, onUnmounted } from 'vue'
import { Download } from '@element-plus/icons-vue'

const isMobile = ref(false)
const tableWrapperRef = ref<HTMLElement | null>(null)

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

defineProps({
  tableData: { type: Array, default: () => [] },
  height: { type: [String, Number], default: 'auto' },
  stripe: { type: Boolean, default: true },
  border: { type: Boolean, default: true },
  defaultSort: { type: Object, default: () => ({}) },
  showToolbar: { type: Boolean, default: true },
  showExport: { type: Boolean, default: true },
  showAction: { type: Boolean, default: true },
  showPagination: { type: Boolean, default: true },
  pagination: { type: Object, default: () => ({ currentPage: 1, pageSize: 10, total: 0 }) },
  pageSizes: { type: Array, default: () => [10, 20, 50, 100] },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['size-change', 'current-change', 'export'])

const headerCellStyle = computed(() => ({
  backgroundColor: 'var(--wm-bg-page)',
  fontWeight: '600',
  color: 'var(--wm-text-primary)',
  borderBottom: '1px solid var(--wm-border)'
}))

const cellStyle = computed(() => ({
  borderBottom: '1px solid var(--wm-border-light)'
}))

const tableHeight = computed(() => {
  if (isMobile.value) return 'auto'
  return 'height'
})

const actionColWidth = computed(() => {
  return isMobile.value ? 120 : 200
})

const paginationLayout = computed(() => {
  if (isMobile.value) return 'prev, pager, next'
  return 'total, sizes, prev, pager, next, jumper'
})

const handleSizeChange = (size: number) => emit('size-change', size)
const handleCurrentChange = (current: number) => emit('current-change', current)
const handleExport = () => emit('export')
</script>

<style scoped>
.wm-table-container {
  width: 100%;
  margin-bottom: var(--wm-space-md);
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--wm-space-md);
  padding: var(--wm-space-sm) var(--wm-space-md);
  background: var(--wm-bg-card);
  border-radius: var(--wm-radius-sm);
  box-shadow: var(--wm-shadow-sm);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: var(--wm-space-sm);
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: var(--wm-space-sm);
}

.table-scroll-wrapper {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  border-radius: var(--wm-radius-sm);
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: var(--wm-space-md);
  padding: var(--wm-space-sm) var(--wm-space-md);
  background: var(--wm-bg-card);
  border-radius: var(--wm-radius-sm);
  box-shadow: var(--wm-shadow-sm);
}

/* ============================
   平板端
   ============================ */
@media screen and (max-width: 1024px) {
  .table-toolbar {
    padding: var(--wm-space-sm) var(--wm-space-sm);
  }

  .table-pagination {
    padding: var(--wm-space-sm);
  }
}

/* ============================
   移动端
   ============================ */
@media screen and (max-width: 768px) {
  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: var(--wm-space-sm);
  }

  .toolbar-left {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-right {
    justify-content: flex-end;
  }

  .btn-text {
    display: none;
  }

  .table-pagination {
    justify-content: center;
    padding: var(--wm-space-sm);
  }
}
</style>
