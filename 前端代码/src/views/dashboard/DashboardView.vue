<template>
  <div class="dashboard-page">
    <!-- Welcome strip -->
    <div class="welcome-strip">
      <div class="welcome-text">
        <h2>欢迎回来，{{ username }}</h2>
        <p>{{ welcomeMessage }}</p>
      </div>
      <div class="welcome-date">{{ currentDate }}</div>
    </div>

    <!-- Module cards -->
    <el-row :gutter="20" class="card-row">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="card in cards" :key="card.title">
        <div class="module-card" :style="{ '--card-gradient': card.gradient }" @click="card.path ? router.push(card.path) : null">
          <div class="module-card-bg"></div>
          <div class="module-card-content">
            <div class="module-card-icon">
              <el-icon :size="28">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="module-card-info">
              <span class="module-card-title">{{ card.title }}</span>
              <span class="module-card-desc">{{ card.desc }}</span>
            </div>
          </div>
          <div class="module-card-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Inventory section -->
    <div class="section-header">
      <div class="section-title-group">
        <div class="section-icon">
          <el-icon :size="20"><Coin /></el-icon>
        </div>
        <div>
          <h3>商品库存情况</h3>
          <p>当前共有 {{ inventoryList.length }} 种商品，库存合计 {{ totalInventory }} 件</p>
        </div>
      </div>
      <div class="section-stats">
        <div class="stat-item">
          <span class="stat-value">{{ inventoryList.length }}</span>
          <span class="stat-label">商品种类</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ totalInventory }}</span>
          <span class="stat-label">总库存量</span>
        </div>
      </div>
    </div>

    <div class="inventory-card">
      <el-table :data="inventoryList" v-loading="loading" style="width: 100%" :show-header="true" stripe>
        <el-table-column prop="pId" label="商品编号" width="120">
          <template #default="{ row }">
            <div class="cell-id">{{ row.pId }}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" min-width="180">
          <template #default="{ row }">
            <div class="cell-product">
              <div class="product-avatar">
                <el-icon><Goods /></el-icon>
              </div>
              <span>{{ productMap[row.pId] || row.pId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalQty" label="库存数量" width="130" sortable>
          <template #default="{ row }">
            <div class="cell-qty">
              <span class="qty-value">{{ row.totalQty }}</span>
              <span class="qty-unit">件</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存状态" width="160">
          <template #default="{ row }">
            <div class="stock-status">
              <div class="stock-bar-track">
                <div
                  class="stock-bar-fill"
                  :style="{
                    width: getStockPercent(row.totalQty) + '%',
                    background: getStockColor(row.totalQty)
                  }"
                ></div>
              </div>
              <el-tag
                :type="getStockTagType(row.totalQty)"
                size="small"
                effect="plain"
                round
              >
                {{ getStockLabel(row.totalQty) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { getProducts } from '../../api/product'
import { getDetails } from '../../api/purchase'

const router = useRouter()
const store = useUserStore()

const loading = ref(false)
const inventoryList = ref([])
const productMap = ref({})

const username = computed(() => store.username)

const welcomeMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息'
  if (hour < 9) return '早上好，开启新的一天'
  if (hour < 12) return '上午好，工作顺利'
  if (hour < 14) return '中午好，记得吃午餐'
  if (hour < 18) return '下午好，保持干劲'
  return '晚上好，辛苦了'
})

const currentDate = computed(() => {
  const d = new Date()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekdays[d.getDay()]}`
})

const totalInventory = computed(() => {
  return inventoryList.value.reduce((sum, item) => sum + item.totalQty, 0)
})

const cards = computed(() => {
  const list = [
    {
      title: '商品管理',
      desc: '浏览和管理商品信息',
      icon: 'Goods',
      gradient: 'linear-gradient(135deg, #3b82f6, #6366f1)',
      path: '/product'
    },
    {
      title: '采购管理',
      desc: '管理采购订单和明细',
      icon: 'DocumentCopy',
      gradient: 'linear-gradient(135deg, #10b981, #06b6d4)',
      path: '/purchase'
    }
  ]
  if (store.isAdmin) {
    list.unshift(
      {
        title: '员工管理',
        desc: '管理员工账号和信息',
        icon: 'UserFilled',
        gradient: 'linear-gradient(135deg, #8b5cf6, #ec4899)',
        path: '/employee'
      },
      {
        title: '供应商管理',
        desc: '管理供应商信息',
        icon: 'Box',
        gradient: 'linear-gradient(135deg, #f59e0b, #f97316)',
        path: '/supplier'
      }
    )
  } else {
    list.unshift({
      title: '个人信息',
      desc: '查看我的员工信息',
      icon: 'UserFilled',
      gradient: 'linear-gradient(135deg, #8b5cf6, #ec4899)',
      path: '/employee/info'
    })
  }
  return list
})

function getStockPercent(qty) {
  const max = Math.max(...inventoryList.value.map(i => i.totalQty), 1)
  return Math.min((qty / max) * 100, 100)
}

function getStockColor(qty) {
  if (qty >= 100) return 'linear-gradient(90deg, #10b981, #34d399)'
  if (qty >= 50) return 'linear-gradient(90deg, #3b82f6, #60a5fa)'
  if (qty >= 20) return 'linear-gradient(90deg, #f59e0b, #fbbf24)'
  return 'linear-gradient(90deg, #ef4444, #f87171)'
}

function getStockTagType(qty) {
  if (qty >= 100) return 'success'
  if (qty >= 50) return 'primary'
  if (qty >= 20) return 'warning'
  return 'danger'
}

function getStockLabel(qty) {
  if (qty >= 100) return '充足'
  if (qty >= 50) return '正常'
  if (qty >= 20) return '较少'
  return '紧缺'
}

async function loadInventory() {
  loading.value = true
  try {
    const [prodRes, detailRes] = await Promise.all([getProducts(), getDetails()])
    const products = prodRes.data || []
    const details = detailRes.data || []

    const map = {}
    products.forEach(p => { map[p.pId] = p.pName })
    productMap.value = map

    const qtyMap = {}
    details.forEach(d => {
      qtyMap[d.pId] = (qtyMap[d.pId] || 0) + (d.dQuantity || 0)
    })

    inventoryList.value = Object.entries(qtyMap)
      .map(([pId, totalQty]) => ({ pId, totalQty }))
      .sort((a, b) => b.totalQty - a.totalQty)
  } finally {
    loading.value = false
  }
}

onMounted(loadInventory)
</script>

<style scoped>
.dashboard-page {
  padding: 28px 28px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

/* Welcome strip */
.welcome-strip {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 28px;
  gap: 16px;
}

.welcome-text h2 {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 4px;
}

.welcome-text p {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
}

.welcome-date {
  font-size: 13px;
  color: var(--text-muted);
  white-space: nowrap;
  padding: 6px 14px;
  background: white;
  border-radius: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0,0,0,0.04);
}

/* Module cards */
.card-row {
  margin-bottom: 32px;
}

.module-card {
  position: relative;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.25s ease;
  margin-bottom: 20px;
  box-shadow: 0 4px 14px rgba(0,0,0,0.08);
}

.module-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 12px 28px rgba(0,0,0,0.15);
}

.module-card:active {
  transform: translateY(-2px) scale(1.01);
}

.module-card-bg {
  position: absolute;
  inset: 0;
  background: var(--card-gradient);
  opacity: 0.92;
}

.module-card-content {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  z-index: 1;
}

.module-card-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  backdrop-filter: blur(4px);
  flex-shrink: 0;
}

.module-card-info {
  display: flex;
  flex-direction: column;
  color: white;
}

.module-card-title {
  font-size: 17px;
  font-weight: 600;
  line-height: 1.3;
}

.module-card-desc {
  font-size: 12px;
  opacity: 0.75;
  margin-top: 2px;
}

.module-card-arrow {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.5);
  transition: transform 0.2s ease;
  z-index: 1;
}

.module-card:hover .module-card-arrow {
  transform: translateY(-50%) translateX(4px);
  color: rgba(255, 255, 255, 0.8);
}

/* Section header */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 16px;
  flex-wrap: wrap;
}

.section-title-group {
  display: flex;
  align-items: center;
  gap: 14px;
}

.section-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #10b981;
  flex-shrink: 0;
}

.section-title-group h3 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.section-title-group p {
  font-size: 13px;
  color: var(--text-muted);
  margin: 2px 0 0;
}

.section-stats {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 8px 20px;
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0,0,0,0.04);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 11px;
  color: var(--text-muted);
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: #e2e8f0;
}

/* Inventory card */
.inventory-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0, 0, 0, 0.06);
  background: white;
}

.cell-id {
  font-family: 'Inter', monospace;
  font-weight: 500;
  color: var(--text-primary);
  font-size: 13px;
}

.cell-product {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-avatar {
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

.cell-qty {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.qty-value {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.qty-unit {
  font-size: 12px;
  color: var(--text-muted);
}

.stock-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stock-bar-track {
  flex: 1;
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
  min-width: 60px;
}

.stock-bar-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.stock-bar-fill.green { background: linear-gradient(90deg, #10b981, #34d399); }
.stock-bar-fill.blue { background: linear-gradient(90deg, #3b82f6, #60a5fa); }
.stock-bar-fill.yellow { background: linear-gradient(90deg, #f59e0b, #fbbf24); }
.stock-bar-fill.red { background: linear-gradient(90deg, #ef4444, #f87171); }

:deep(.el-table) {
  border: none !important;
  box-shadow: none !important;
}

:deep(.el-table th.el-table__cell) {
  background-color: #f8fafc !important;
  color: var(--text-secondary) !important;
  font-weight: 600 !important;
  font-size: 12px !important;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

:deep(.el-table__body tr:hover td) {
  background-color: #f8fafc !important;
}
</style>
