<template>
  <div class="page-wrapper">
    <div class="page-header-card">
      <div class="page-title-group">
        <div class="page-title-icon" style="background: linear-gradient(135deg, #3b82f6, #6366f1)">
          <el-icon :size="20"><Goods /></el-icon>
        </div>
        <div class="page-title-text">
          <h2>商品管理</h2>
          <span>浏览和管理所有商品信息</span>
        </div>
      </div>
      <div class="page-actions">
        <el-button v-if="isAdmin" type="primary" @click="openDialog(null)">
          <el-icon><Plus /></el-icon>新增商品
        </el-button>
        <el-button v-if="isAdmin" class="batch-btn" @click="openBatchDialog">
          <el-icon><Upload /></el-icon>批量录入
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-table ref="tableRef" :data="productList" border stripe v-loading="loading" style="width: 100%" row-key="pId">
        <el-table-column label="" width="50" align="center">
          <template #default>
            <span class="drag-handle"><el-icon size="16"><Grid /></el-icon></span>
          </template>
        </el-table-column>
        <el-table-column prop="pId" label="商品编号" width="110">
          <template #default="{ row }">
            <div class="cell-id">{{ row.pId }}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" min-width="180">
          <template #default="{ row }">
            <div class="cell-product">
              <div class="product-icon">
                <el-icon><Goods /></el-icon>
              </div>
              <span>{{ row.pName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="pPrice" label="单价" width="100">
          <template #default="{ row }">
            <span class="cell-price">¥{{ row.pPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sId" label="供应商编号" width="120">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ row.sId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pIntro" label="简介" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-intro">{{ row.pIntro || '暂无简介' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="pRemark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column v-if="isAdmin" label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button type="primary" link size="small" @click="openDialog(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row.pId)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="550px" :close-on-click-modal="false" top="8vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品编号" prop="pId">
              <el-input v-model="form.pId" :disabled="isEdit" placeholder="请输入编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价" prop="pPrice">
              <el-input-number v-model="form.pPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品名称" prop="pName">
          <el-input v-model="form.pName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="供应商编号" prop="sId">
          <el-input v-model="form.sId" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="简介" prop="pIntro">
          <el-input v-model="form.pIntro" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
        <el-form-item label="备注" prop="pRemark">
          <el-input v-model="form.pRemark" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchVisible" title="批量录入商品" width="700px" :close-on-click-modal="false" top="8vh">
      <el-alert title="请粘贴 JSON 数组格式的商品数据" type="info" :closable="false" show-icon style="margin-bottom: 16px" />
      <el-input v-model="batchJson" type="textarea" rows="10" placeholder='[{"pId":"P004","pName":"...","pPrice":3.0,"sId":"S001","pIntro":"...","pRemark":""}]' />
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="handleBatchSubmit">批量录入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { getProducts, saveProductBatch, updateProduct, deleteProduct } from '../../api/product'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import Sortable from 'sortablejs'

const store = useUserStore()
const isAdmin = computed(() => store.isAdmin)

const productList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const batchVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const batchLoading = ref(false)
const batchJson = ref('')
const formRef = ref(null)
const tableRef = ref(null)

const form = ref({
  pId: '', pName: '', pPrice: 0, sId: '', pIntro: '', pRemark: ''
})

const rules = {
  pId: [{ required: true, message: '请输入商品编号', trigger: 'blur' }],
  pName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  pPrice: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '单价必须大于 0', trigger: 'blur' }
  ],
  sId: [{ required: true, message: '请输入供应商编号', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getProducts()
    productList.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    form.value = { ...row }
  } else {
    isEdit.value = false
    form.value = { pId: '', pName: '', pPrice: 0, sId: '', pIntro: '', pRemark: '' }
  }
  dialogVisible.value = true
}

function openBatchDialog() {
  batchJson.value = ''
  batchVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateProduct(form.value)
      ElMessage.success('修改成功')
    } else {
      await saveProductBatch([form.value])
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

async function handleBatchSubmit() {
  let data
  try {
    data = JSON.parse(batchJson.value)
    if (!Array.isArray(data)) throw new Error('必须是数组')
  } catch {
    ElMessage.error('JSON 格式错误，请检查')
    return
  }
  batchLoading.value = true
  try {
    await saveProductBatch(data)
    ElMessage.success(`成功录入 ${data.length} 条`)
    batchVisible.value = false
    await fetchData()
  } finally {
    batchLoading.value = false
  }
}

function handleDelete(pId) {
  ElMessageBox.confirm('确认删除该商品？', '警告', { type: 'warning' }).then(async () => {
    await deleteProduct(pId)
    ElMessage.success('删除成功')
    await fetchData()
  }).catch(() => {})
}

onMounted(async () => {
  const saved = loadOrder('product_order')
  await fetchData()
  if (saved && saved.length === productList.value.length) {
    const map = {}
    productList.value.forEach(p => { map[p.pId] = p })
    productList.value = saved.map(id => map[id]).filter(Boolean)
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
      const item = productList.value.splice(oldIndex, 1)[0]
      productList.value.splice(newIndex, 0, item)
      saveOrder('product_order', productList.value.map(p => p.pId))
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
.cell-id {
  font-family: 'Inter', monospace;
  font-weight: 500;
  font-size: 13px;
  color: var(--text-primary);
}
.cell-product {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}
.product-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3b82f6;
  flex-shrink: 0;
}
.cell-price {
  font-weight: 600;
  color: #059669;
  font-size: 14px;
}
.cell-intro {
  color: var(--text-secondary);
  font-size: 13px;
}
.batch-btn {
  border-radius: 8px;
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
