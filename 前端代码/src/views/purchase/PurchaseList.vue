<template>
  <div class="page-wrapper">
    <div class="page-header-card">
      <div class="page-title-group">
        <div class="page-title-icon" style="background: linear-gradient(135deg, #10b981, #06b6d4)">
          <el-icon :size="20"><DocumentCopy /></el-icon>
        </div>
        <div class="page-title-text">
          <h2>采购管理</h2>
          <span>管理采购订单及明细</span>
        </div>
      </div>
      <div class="page-actions">
        <el-button v-if="isAdmin" type="primary" @click="openDialog">
          <el-icon><Plus /></el-icon>新增采购单
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-table
        ref="tableRef"
        :data="orderList"
        border
        stripe
        v-loading="loading"
        style="width: 100%"
        row-key="oId"
        @row-click="goToDetail"
        :row-class-name="() => 'clickable-row'"
      >
        <el-table-column label="" width="50" align="center">
          <template #default>
            <span class="drag-handle"><el-icon size="16"><Grid /></el-icon></span>
          </template>
        </el-table-column>
        <el-table-column prop="oId" label="采购单号" width="160">
          <template #default="{ row }">
            <div class="cell-order-id">{{ row.oId }}</div>
          </template>
        </el-table-column>
        <el-table-column label="经办人" width="120">
          <template #default="{ row }">
            <div class="cell-employee">{{ employeeMap[row.eId] || row.eId }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="eId" label="员工编号" width="100">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" round>{{ row.eId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="oTotalQuantity" label="总数量" width="90" align="center">
          <template #default="{ row }">
            <span class="cell-qty">{{ row.oTotalQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="oTotalPrice" label="总金额" width="130">
          <template #default="{ row }">
            <span class="cell-total">¥{{ row.oTotalPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="oTime" label="采购时间" width="180">
          <template #default="{ row }">
            <div class="cell-time">
              <el-icon><Calendar /></el-icon>
              <span>{{ row.oTime?.replace('T', ' ') }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="oRemark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column v-if="isAdmin" label="操作" width="180" fixed="right" @click.stop>
          <template #default="{ row }">
            <div class="action-cell">
              <el-button type="primary" link size="small" @click.stop="openEditOrder(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" link size="small" @click.stop="handleDelete(row.oId)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增采购单弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增采购单" width="1100px" :close-on-click-modal="false" top="2vh">
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="100px">
        <div class="dialog-section">
          <div class="dialog-section-title">
            <el-icon><InfoFilled /></el-icon> 采购主表
          </div>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="采购单号" prop="oId">
                <el-input v-model="orderForm.oId" placeholder="请输入单号" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="经办员工" prop="eId">
                <el-select v-model="orderForm.eId" filterable placeholder="请选择员工" style="width: 100%">
                  <el-option v-for="emp in employeeList" :key="emp.eId" :label="emp.eName + ' (' + emp.eId + ')'" :value="emp.eId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="采购时间" prop="oTime">
                <el-date-picker v-model="orderForm.oTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注">
            <el-input v-model="orderForm.oRemark" placeholder="可选" />
          </el-form-item>
        </div>

        <div class="dialog-section">
          <div class="dialog-section-title">
            <el-icon><List /></el-icon> 采购明细
          </div>
          <el-table :data="detailList" border style="width: 100%; margin-bottom: 12px" class="dialog-detail-table">
            <el-table-column label="明细编号" width="130">
              <template #default="{ row }">
                <el-input v-model="row.dId" size="small" placeholder="编号" />
              </template>
            </el-table-column>
            <el-table-column label="商品" min-width="220">
              <template #default="{ row }">
                <el-select v-model="row.pId" filterable placeholder="选择商品" style="width: 100%" @change="(val) => onProductChange(row, val)">
                  <el-option v-for="p in productList" :key="p.pId" :label="p.pName + ' (' + p.pId + ')'" :value="p.pId" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.dQuantity" :min="1" size="small" style="width: 100%" controls-position="right" @change="recalc" />
              </template>
            </el-table-column>
            <el-table-column label="单价" width="100">
              <template #default="{ row }">
                <span class="cell-price">¥{{ row.dPrice?.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="小计" width="100">
              <template #default="{ row }">
                <span class="cell-subtotal">¥{{ ((row.dQuantity || 0) * (row.dPrice || 0)).toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="100" align="center">
              <template #default="{ $index }">
                <span style="cursor:pointer;color:#ef4444;font-size:16px;display:inline-flex" @click="detailList.splice($index, 1); recalc()">
                  <el-icon><Close /></el-icon>
                </span>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" plain size="small" @click="addDetailRow">
            <el-icon><Plus /></el-icon> 添加明细行
          </el-button>
        </div>

        <div class="dialog-summary">
          <div class="summary-item">
            <span class="summary-label">总数量</span>
            <span class="summary-value">{{ orderForm.oTotalQuantity }}</span>
          </div>
          <div class="summary-divider"></div>
          <div class="summary-item">
            <span class="summary-label">总金额</span>
            <span class="summary-value summary-price">¥{{ orderForm.oTotalPrice?.toFixed(2) }}</span>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确认创建</el-button>
      </template>
    </el-dialog>

    <!-- 编辑主表弹窗 -->
    <el-dialog v-model="editOrderVisible" title="编辑采购单" width="560px" :close-on-click-modal="false" top="20vh">
      <el-form ref="editOrderFormRef" :model="editOrderForm" :rules="orderRules" label-width="100px">
        <el-form-item label="采购单号">
          <el-input v-model="editOrderForm.oId" disabled />
        </el-form-item>
        <el-form-item label="经办员工" prop="eId">
          <el-select v-model="editOrderForm.eId" filterable placeholder="请选择员工" style="width: 100%">
            <el-option v-for="emp in employeeList" :key="emp.eId" :label="emp.eName + ' (' + emp.eId + ')'" :value="emp.eId" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购时间" prop="oTime">
          <el-date-picker v-model="editOrderForm.oTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editOrderForm.oRemark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editOrderVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleEditOrder">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { getOrders, getDetails, savePurchase, updateOrder, deletePurchase } from '../../api/purchase'
import { getEmployees } from '../../api/employee'
import { getProducts } from '../../api/product'
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Sortable from 'sortablejs'

const store = useUserStore()
const router = useRouter()
const isAdmin = computed(() => store.isAdmin)

const orderList = ref([])
const employeeList = ref([])
const productList = ref([])
const employeeMap = ref({})
const loading = ref(false)
const dialogVisible = ref(false)
const editOrderVisible = ref(false)
const submitting = ref(false)
const orderFormRef = ref(null)
const editOrderFormRef = ref(null)
const tableRef = ref(null)

const orderForm = ref({ oId: '', eId: '', oTotalQuantity: 0, oTotalPrice: 0, oTime: '', oRemark: '' })
const editOrderForm = ref({ oId: '', eId: '', oTotalQuantity: 0, oTotalPrice: 0, oTime: '', oRemark: '' })
const detailList = ref([])

const orderRules = {
  oId: [{ required: true, message: '请输入采购单号', trigger: 'blur' }],
  eId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  oTime: [{ required: true, message: '请选择采购时间', trigger: 'change' }]
}

async function fetchOrders() {
  loading.value = true
  try {
    const [orderRes, empRes] = await Promise.all([getOrders(), getEmployees()])
    const emps = empRes.data || []
    employeeList.value = emps
    const map = {}
    emps.forEach(e => { map[e.eId] = e.eName })
    employeeMap.value = map
    orderList.value = orderRes.data || []
  } finally {
    loading.value = false
  }
}

async function loadOptions() {
  const [empRes, prodRes] = await Promise.all([getEmployees(), getProducts()])
  employeeList.value = empRes.data || []
  productList.value = prodRes.data || []
}

function openDialog() {
  orderForm.value = { oId: '', eId: '', oTotalQuantity: 0, oTotalPrice: 0, oTime: '', oRemark: '' }
  detailList.value = []
  loadOptions()
  dialogVisible.value = true
}

function addDetailRow() {
  detailList.value.push({ dId: '', oId: orderForm.value.oId, pId: '', dQuantity: 1, dPrice: 0, dRemark: '' })
}

function onProductChange(row, pId) {
  const prod = productList.value.find(p => p.pId === pId)
  if (prod) {
    row.dPrice = prod.pPrice
    row.pName = prod.pName
  }
  recalc()
}

function recalc() {
  const totalQty = detailList.value.reduce((sum, d) => sum + (d.dQuantity || 0), 0)
  const totalPrice = detailList.value.reduce((sum, d) => sum + (d.dQuantity || 0) * (d.dPrice || 0), 0)
  orderForm.value.oTotalQuantity = totalQty
  orderForm.value.oTotalPrice = totalPrice
}

async function handleSubmit() {
  const valid = await orderFormRef.value.validate().catch(() => false)
  if (!valid) return
  if (detailList.value.length === 0) {
    ElMessage.warning('请至少添加一条采购明细')
    return
  }
  submitting.value = true
  try {
    const payload = {
      orders: [{ ...orderForm.value }],
      details: detailList.value.map(d => ({
        ...d,
        oId: orderForm.value.oId,
        dTotalPrice: (d.dQuantity || 0) * (d.dPrice || 0)
      }))
    }
    await savePurchase(payload)
    ElMessage.success('采购单创建成功')
    dialogVisible.value = false
    await fetchOrders()
  } finally {
    submitting.value = false
  }
}

function openEditOrder(row) {
  editOrderForm.value = {
    oId: row.oId,
    eId: row.eId,
    oTotalQuantity: row.oTotalQuantity,
    oTotalPrice: row.oTotalPrice,
    oTime: row.oTime,
    oRemark: row.oRemark
  }
  loadOptions()
  editOrderVisible.value = true
}

async function handleEditOrder() {
  const valid = await editOrderFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await updateOrder(editOrderForm.value)
    ElMessage.success('修改成功')
    editOrderVisible.value = false
    await fetchOrders()
  } finally {
    submitting.value = false
  }
}

function handleDelete(oId) {
  ElMessageBox.confirm('将级联删除该采购单及其所有明细，确认？', '警告', { type: 'warning' }).then(async () => {
    await deletePurchase(oId)
    ElMessage.success('删除成功')
    await fetchOrders()
  }).catch(() => {})
}

function goToDetail(row) {
  router.push(`/purchase/detail/${row.oId}`)
}

onMounted(async () => {
  const saved = loadOrder('purchase_order')
  await fetchOrders()
  if (saved && saved.length === orderList.value.length) {
    const map = {}
    orderList.value.forEach(o => { map[o.oId] = o })
    orderList.value = saved.map(id => map[id]).filter(Boolean)
  }
  await nextTick()
  initSortable()
})

function initSortable() {
  const el = tableRef.value?.$el?.querySelector('.el-table__body-wrapper tbody')
  if (!el) return
  Sortable.create(el, {
    animation: 200,
    handle: '.drag-handle',
    ghostClass: 'sortable-ghost',
    onEnd(evt) {
      const { oldIndex, newIndex } = evt
      if (oldIndex === undefined || newIndex === undefined) return
      const item = orderList.value.splice(oldIndex, 1)[0]
      orderList.value.splice(newIndex, 0, item)
      saveOrder('purchase_order', orderList.value.map(o => o.oId))
    }
  })
}

function saveOrder(key, ids) {
  localStorage.setItem(key, JSON.stringify(ids))
}

function loadOrder(key) {
  try {
    const raw = localStorage.getItem(key)
    return raw ? JSON.parse(raw) : null
  } catch { return null }
}
</script>

<style scoped>
.cell-order-id {
  font-family: 'Inter', monospace;
  font-weight: 600;
  font-size: 13px;
  color: #6366f1;
}
.cell-employee {
  font-weight: 500;
  color: var(--text-primary);
}
.cell-qty {
  font-weight: 600;
  font-size: 15px;
  color: var(--text-primary);
}
.cell-total {
  font-weight: 600;
  color: #059669;
  font-size: 14px;
}
.cell-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}
.clickable-row {
  cursor: pointer;
}

/* Dialog sections */
.dialog-section {
  margin-bottom: 24px;
}
.dialog-section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

/* Summary */
.dialog-summary {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20px;
  padding: 12px 20px;
  background: #f8fafc;
  border-radius: 10px;
  margin-top: 16px;
}
.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.summary-label {
  font-size: 13px;
  color: var(--text-muted);
}
.summary-value {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}
.summary-price {
  color: #059669;
}
.summary-divider {
  width: 1px;
  height: 24px;
  background: #e2e8f0;
}
.cell-price {
  font-weight: 500;
  color: #059669;
}
.cell-subtotal {
  font-weight: 600;
  color: var(--text-primary);
}
.dialog-detail-table .cell {
  overflow: visible !important;
  text-overflow: clip !important;
}
.drag-handle {
  cursor: grab;
  color: #cbd5e1;
  font-size: 16px;
  display: inline-flex;
  transition: color 0.15s;
}
.drag-handle:hover {
  color: #6366f1;
}
.sortable-ghost {
  opacity: 0.3;
}
</style>
