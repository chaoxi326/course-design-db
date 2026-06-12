<template>
  <div class="page-wrapper">
    <div class="back-bar">
      <el-button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <!-- 采购单信息卡片 -->
    <div class="detail-card">
      <div class="detail-card-header">
        <div class="detail-card-icon">
          <el-icon :size="22"><DocumentCopy /></el-icon>
        </div>
        <div>
          <h3>采购单信息</h3>
          <span class="detail-subtitle">单号 {{ order?.oId }}</span>
        </div>
        <div class="detail-header-actions">
          <el-tag type="success" effect="dark" round size="small" v-if="order">已完成</el-tag>
        </div>
      </div>
      <el-descriptions :column="3" border v-if="order" class="detail-descriptions">
        <el-descriptions-item label="采购单号" content-class="desc-value">{{ order.oId }}</el-descriptions-item>
        <el-descriptions-item label="经办员工" content-class="desc-value">{{ order.eId }}</el-descriptions-item>
        <el-descriptions-item label="总数量" content-class="desc-value">{{ order.oTotalQuantity }} 件</el-descriptions-item>
        <el-descriptions-item label="总金额" content-class="desc-value highlight">
          ¥{{ order.oTotalPrice?.toFixed(2) }}
        </el-descriptions-item>
        <el-descriptions-item label="采购时间" content-class="desc-value">{{ order.oTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" content-class="desc-value">{{ order.oRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 采购明细卡片 -->
    <div class="detail-card">
      <div class="detail-card-header">
        <div class="detail-card-icon" style="background: linear-gradient(135deg, #dbeafe, #bfdbfe); color: #3b82f6">
          <el-icon :size="22"><List /></el-icon>
        </div>
        <div>
          <h3>采购明细</h3>
          <span class="detail-subtitle">共 {{ detailList.length }} 条记录</span>
        </div>
        <div class="detail-header-actions">
          <el-button v-if="isAdmin" type="primary" plain size="small" @click="openEditDetail">
            <el-icon><Edit /></el-icon> 编辑明细
          </el-button>
        </div>
      </div>

      <el-table :data="detailList" border v-loading="loadingDetail" style="width: 100%">
        <el-table-column prop="dId" label="明细编号" width="140">
          <template #default="{ row }">
            <div class="cell-id">{{ row.dId }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="pId" label="商品编号" width="110">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ row.pId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" min-width="140">
          <template #default="{ row }">
            <span>{{ productMap[row.pId] || row.pId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="90" align="center">
          <template #default="{ row }">
            <span class="cell-qty">{{ row.dQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="110">
          <template #default="{ row }">
            <span class="cell-price">¥{{ row.dPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="商品总价" width="130">
          <template #default="{ row }">
            <span class="cell-subtotal">¥{{ (row.dQuantity * row.dPrice).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="dRemark" label="备注" min-width="160" />
      </el-table>
    </div>

    <!-- 编辑明细弹窗 -->
    <el-dialog v-model="editDetailVisible" title="编辑采购明细" width="900px" :close-on-click-modal="false" top="8vh">
      <el-table :data="editDetailList" border stripe style="width: 100%">
        <el-table-column label="明细编号" width="130">
          <template #default="{ row }">
            <span class="cell-id">{{ row.dId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="商品" min-width="220">
          <template #default="{ row }">
            <el-select v-model="row.pId" filterable placeholder="选择商品" style="width: 100%" @change="(val) => onEditProductChange(row, val)">
              <el-option v-for="p in productList" :key="p.pId" :label="p.pName + ' (' + p.pId + ')'" :value="p.pId" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.dQuantity" :min="1" size="small" style="width: 100%" controls-position="right" @change="recalcEditDetail(row)" />
          </template>
        </el-table-column>
        <el-table-column label="单价" width="130">
          <template #default="{ row }">
            <span class="cell-price">¥{{ row.dPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="小计" min-width="100">
          <template #default="{ row }">
            <span class="cell-subtotal">¥{{ ((row.dQuantity || 0) * (row.dPrice || 0)).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="editDetailVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleEditDetail">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrders, getDetails, updateDetail, refreshOrderTotals } from '../../api/purchase'
import { getProducts } from '../../api/product'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const store = useUserStore()
const isAdmin = computed(() => store.isAdmin)

const oId = route.params.oId
const order = ref(null)
const detailList = ref([])
const productList = ref([])
const productMap = ref({})
const loadingDetail = ref(false)
const editDetailVisible = ref(false)
const editDetailList = ref([])
const submitting = ref(false)

async function fetchData() {
  loadingDetail.value = true
  try {
    const [orderRes, detailRes, prodRes] = await Promise.all([getOrders(), getDetails(), getProducts()])
    const products = prodRes.data || []
    productList.value = products
    const map = {}
    products.forEach(p => { map[p.pId] = p.pName })
    productMap.value = map
    order.value = (orderRes.data || []).find(o => o.oId === oId)
    detailList.value = (detailRes.data || []).filter(d => d.oId === oId)
  } finally {
    loadingDetail.value = false
  }
}

function openEditDetail() {
  editDetailList.value = detailList.value.map(d => ({ ...d }))
  editDetailVisible.value = true
}

function onEditProductChange(row, pId) {
  const prod = productList.value.find(p => p.pId === pId)
  if (prod) {
    row.dPrice = prod.pPrice
    row.pName = prod.pName
  }
}

function recalcEditDetail(row) {
  // 数量变化时小计自动更新
}

async function handleEditDetail() {
  submitting.value = true
  const errors = []
  try {
    for (const detail of editDetailList.value) {
      detail.dTotalPrice = (detail.dQuantity || 0) * (detail.dPrice || 0)
      try {
        await updateDetail(detail)
      } catch {
        errors.push(detail.dId || detail.pId || '未知行')
      }
    }
    if (errors.length > 0) {
      ElMessage.warning(`部分明细更新失败: ${errors.join(', ')}`)
      try { await refreshOrderTotals(oId) } catch {}
      return
    }
    await refreshOrderTotals(oId)
    ElMessage.success('明细更新成功')
    editDetailVisible.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.back-bar {
  margin-bottom: 20px;
}
.back-btn {
  border-radius: 8px;
}

.detail-card {
  background: white;
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  overflow: hidden;
}

.detail-card-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.detail-card-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #10b981;
  flex-shrink: 0;
}

.detail-card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.detail-subtitle {
  font-size: 13px;
  color: var(--text-muted);
}

.detail-header-actions {
  margin-left: auto;
}

.detail-descriptions {
  padding: 20px 24px;
}

:deep(.detail-descriptions .el-descriptions__table) {
  font-size: 14px;
}

:deep(.el-descriptions__cell) {
  padding: 12px 16px !important;
}

:deep(.el-descriptions__label) {
  font-weight: 600 !important;
  color: var(--text-secondary) !important;
  font-size: 13px !important;
}

.desc-value {
  font-weight: 500;
  color: var(--text-primary);
}

:deep(.highlight) {
  color: #059669 !important;
  font-weight: 600 !important;
  font-size: 15px !important;
}

.cell-id {
  font-family: 'Inter', monospace;
  font-weight: 500;
  font-size: 13px;
}
.cell-qty {
  font-weight: 600;
  font-size: 14px;
}
.cell-price {
  font-weight: 500;
  color: #059669;
}
.cell-subtotal {
  font-weight: 600;
  color: var(--text-primary);
}
</style>
