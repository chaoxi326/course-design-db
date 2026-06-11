<template>
  <div class="page-wrapper">
    <div class="page-header-card">
      <div class="page-title-group">
        <div class="page-title-icon" style="background: linear-gradient(135deg, #8b5cf6, #ec4899)">
          <el-icon :size="20"><UserFilled /></el-icon>
        </div>
        <div class="page-title-text">
          <h2>员工管理</h2>
          <span>管理系统中的员工账号和信息</span>
        </div>
      </div>
      <div class="page-actions">
        <el-button v-if="isAdmin" type="primary" @click="openDialog(null)">
          <el-icon><Plus /></el-icon>新增员工
        </el-button>
        <el-button v-if="isAdmin" class="batch-btn" @click="openBatchDialog">
          <el-icon><Upload /></el-icon>批量录入
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-table :data="employeeList" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="eId" label="工号" width="100">
          <template #default="{ row }">
            <div class="cell-id">{{ row.eId }}</div>
          </template>
        </el-table-column>
        <el-table-column label="姓名" width="140">
          <template #default="{ row }">
            <div class="cell-employee">
              <el-avatar :size="28" style="background: linear-gradient(135deg, #8b5cf6, #a78bfa); font-size: 13px; font-weight: 600; flex-shrink: 0">
                {{ row.eName?.charAt(0) || '?' }}
              </el-avatar>
              <span>{{ row.eName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="eLevel" label="级别" width="110">
          <template #default="{ row }">
            <el-tag :type="row.eLevel === '管理员' ? 'danger' : 'info'" size="small" effect="light" round>
              {{ row.eLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ePhone" label="电话" width="140" />
        <el-table-column prop="eSalary" label="工资" width="110">
          <template #default="{ row }">
            <span class="cell-money">¥{{ row.eSalary?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="eRemark" label="备注" min-width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button type="primary" link size="small" @click="openDialog(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row.eId)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 单条新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '新增员工'" width="520px" :close-on-click-modal="false" top="8vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工号" prop="eId">
          <el-input v-model="form.eId" :disabled="isEdit" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="姓名" prop="eName">
          <el-input v-model="form.eName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="ePassword">
          <el-input v-model="form.ePassword" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="级别" prop="eLevel">
          <el-select v-model="form.eLevel" style="width: 100%">
            <el-option label="管理员" value="管理员" />
            <el-option label="普通员工" value="普通员工" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="电话" prop="ePhone">
              <el-input v-model="form.ePhone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工资" prop="eSalary">
              <el-input-number v-model="form.eSalary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="eRemark">
          <el-input v-model="form.eRemark" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量录入弹窗 -->
    <el-dialog v-model="batchVisible" title="批量录入员工" width="680px" :close-on-click-modal="false" top="8vh">
      <el-alert title="请粘贴 JSON 数组格式的员工数据" type="info" :closable="false" show-icon style="margin-bottom: 16px" />
      <el-input v-model="batchJson" type="textarea" rows="10" placeholder='[{"eId":"E004","eName":"张三","ePassword":"123456","eLevel":"普通员工","ePhone":"13800000000","eSalary":5000,"eRemark":""}]' />
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="handleBatchSubmit">批量录入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getEmployees, saveEmployeeBatch, updateEmployee, deleteEmployee } from '../../api/employee'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useUserStore()
const isAdmin = computed(() => store.isAdmin)

const employeeList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const batchVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const batchLoading = ref(false)
const batchJson = ref('')
const formRef = ref(null)

const form = ref({
  eId: '', eName: '', ePassword: '', eLevel: '普通员工', ePhone: '', eSalary: 0, eRemark: ''
})

const rules = {
  eId: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  eName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  ePassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  eLevel: [{ required: true, message: '请选择级别', trigger: 'change' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getEmployees()
    employeeList.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    form.value = { ...row, ePassword: '' }
  } else {
    isEdit.value = false
    form.value = { eId: '', eName: '', ePassword: '', eLevel: '普通员工', ePhone: '', eSalary: 0, eRemark: '' }
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
      await updateEmployee(form.value)
      ElMessage.success('修改成功')
    } else {
      await saveEmployeeBatch([form.value])
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
    await saveEmployeeBatch(data)
    ElMessage.success(`成功录入 ${data.length} 条`)
    batchVisible.value = false
    await fetchData()
  } finally {
    batchLoading.value = false
  }
}

function handleDelete(eId) {
  ElMessageBox.confirm('确认删除该员工？不可恢复。', '警告', { type: 'warning' }).then(async () => {
    await deleteEmployee(eId)
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
.cell-employee {
  display: flex;
  align-items: center;
  gap: 10px;
}
.cell-money {
  font-weight: 600;
  color: #059669;
  font-size: 14px;
}
.time-label {
  font-size: 12px;
  color: var(--text-muted);
}
.batch-btn {
  border-radius: 8px;
}
</style>
