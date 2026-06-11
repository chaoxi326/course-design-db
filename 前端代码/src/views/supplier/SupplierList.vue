<template>
  <div class="page-wrapper">
    <div class="page-header-card">
      <div class="page-title-group">
        <div class="page-title-icon" style="background: linear-gradient(135deg, #f59e0b, #f97316)">
          <el-icon :size="20"><Box /></el-icon>
        </div>
        <div class="page-title-text">
          <h2>供应商管理</h2>
          <span>管理供应商信息和联系方式</span>
        </div>
      </div>
      <div class="page-actions">
        <el-button v-if="isAdmin" type="primary" @click="openDialog(null)">
          <el-icon><Plus /></el-icon>新增供应商
        </el-button>
        <el-button v-if="isAdmin" class="batch-btn" @click="openBatchDialog">
          <el-icon><Upload /></el-icon>批量录入
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-table :data="supplierList" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="sId" label="编号" width="100">
          <template #default="{ row }">
            <div class="cell-id">{{ row.sId }}</div>
          </template>
        </el-table-column>
        <el-table-column label="名称" min-width="160">
          <template #default="{ row }">
            <div class="cell-supplier">
              <div class="supplier-avatar">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div>
                <div class="supplier-name">{{ row.sName }}</div>
                <div class="supplier-short" v-if="row.sShortName">{{ row.sShortName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sPhone" label="公司电话" width="130" />
        <el-table-column label="联系人" width="160">
          <template #default="{ row }">
            <div class="cell-contact">
              <el-icon><User /></el-icon>
              <span>{{ row.sContactPerson || '未设置' }}</span>
              <span class="contact-phone" v-if="row.sContactPhone">{{ row.sContactPhone }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sAddress" label="地址" min-width="160" show-overflow-tooltip />
        <el-table-column prop="sRemark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button type="primary" link size="small" @click="openDialog(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row.sId)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应商' : '新增供应商'" width="620px" :close-on-click-modal="false" top="5vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="供应商编号" prop="sId">
              <el-input v-model="form.sId" :disabled="isEdit" placeholder="请输入编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商简称" prop="sShortName">
              <el-input v-model="form.sShortName" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="供应商名称" prop="sName">
          <el-input v-model="form.sName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="地址" prop="sAddress">
          <el-input v-model="form.sAddress" placeholder="请输入地址" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="公司电话" prop="sPhone">
              <el-input v-model="form.sPhone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="sEmail">
              <el-input v-model="form.sEmail" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系人" prop="sContactPerson">
              <el-input v-model="form.sContactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人电话" prop="sContactPhone">
              <el-input v-model="form.sContactPhone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="sRemark">
          <el-input v-model="form.sRemark" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchVisible" title="批量录入供应商" width="700px" :close-on-click-modal="false" top="8vh">
      <el-alert title="请粘贴 JSON 数组格式的供应商数据" type="info" :closable="false" show-icon style="margin-bottom: 16px" />
      <el-input v-model="batchJson" type="textarea" rows="10" placeholder='[{"sId":"S003","sName":"...","sShortName":"...","sAddress":"...","sPhone":"...","sEmail":"...","sContactPerson":"...","sContactPhone":"...","sRemark":""}]' />
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="handleBatchSubmit">批量录入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getSuppliers, saveSupplierBatch, updateSupplier, deleteSupplier } from '../../api/supplier'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useUserStore()
const isAdmin = computed(() => store.isAdmin)

const supplierList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const batchVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const batchLoading = ref(false)
const batchJson = ref('')
const formRef = ref(null)

const form = ref({
  sId: '', sName: '', sShortName: '', sAddress: '', sPhone: '',
  sEmail: '', sContactPerson: '', sContactPhone: '', sRemark: ''
})

const rules = {
  sId: [{ required: true, message: '请输入供应商编号', trigger: 'blur' }],
  sName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getSuppliers()
    supplierList.value = res.data || []
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
    form.value = { sId: '', sName: '', sShortName: '', sAddress: '', sPhone: '', sEmail: '', sContactPerson: '', sContactPhone: '', sRemark: '' }
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
      await updateSupplier(form.value)
      ElMessage.success('修改成功')
    } else {
      await saveSupplierBatch([form.value])
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
    await saveSupplierBatch(data)
    ElMessage.success(`成功录入 ${data.length} 条`)
    batchVisible.value = false
    await fetchData()
  } finally {
    batchLoading.value = false
  }
}

function handleDelete(sId) {
  ElMessageBox.confirm('确认删除该供应商？若有商品关联则无法删除。', '警告', { type: 'warning' }).then(async () => {
    await deleteSupplier(sId)
    ElMessage.success('删除成功')
    await fetchData()
  }).catch(() => {})
}

onMounted(fetchData)
</script>

<style scoped>
.cell-id {
  font-family: 'Inter', monospace;
  font-weight: 500;
  font-size: 13px;
  color: var(--text-primary);
}
.cell-supplier {
  display: flex;
  align-items: center;
  gap: 10px;
}
.supplier-avatar {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #fffbeb, #fef3c7);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f59e0b;
  flex-shrink: 0;
}
.supplier-name {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-primary);
}
.supplier-short {
  font-size: 12px;
  color: var(--text-muted);
}
.cell-contact {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}
.contact-phone {
  color: var(--text-muted);
  font-size: 12px;
  margin-left: 2px;
}
.batch-btn {
  border-radius: 8px;
}
</style>
