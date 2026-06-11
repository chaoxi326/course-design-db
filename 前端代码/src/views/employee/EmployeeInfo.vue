<template>
  <div class="page-wrapper">
    <div class="info-card">
      <div class="info-card-header">
        <div class="info-avatar">
          <span>{{ info.eName?.charAt(0) || '?' }}</span>
        </div>
        <div class="info-title-group">
          <h3>{{ info.eName }}</h3>
          <el-tag :type="info.eLevel === '管理员' ? 'danger' : 'info'" effect="dark" size="small" round>
            {{ info.eLevel }}
          </el-tag>
        </div>
      </div>

      <el-descriptions :column="2" border class="info-descriptions">
        <el-descriptions-item label="工号" content-class="desc-value">{{ info.eId }}</el-descriptions-item>
        <el-descriptions-item label="姓名" content-class="desc-value">{{ info.eName }}</el-descriptions-item>
        <el-descriptions-item label="级别" content-class="desc-value">
          <el-tag :type="info.eLevel === '管理员' ? 'danger' : 'info'" size="small" round>{{ info.eLevel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="电话" content-class="desc-value">{{ info.ePhone }}</el-descriptions-item>
        <el-descriptions-item label="工资" content-class="desc-value highlight">¥{{ info.eSalary?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="备注" content-class="desc-value">{{ info.eRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../../stores/user'

const store = useUserStore()
const info = store.user
</script>

<style scoped>
.page-wrapper {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.info-card {
  background: white;
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.info-card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 28px 28px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.info-avatar {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  background: linear-gradient(135deg, #8b5cf6, #a78bfa);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.25);
}

.info-title-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-title-group h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.info-descriptions {
  padding: 20px 28px 28px;
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
}
</style>
