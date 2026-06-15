<template>
  <el-container style="height: 100vh">
    <el-aside :width="isCollapse ? '68px' : '240px'" class="aside">
      <div class="logo-area">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
        </div>
        <span v-show="!isCollapse" class="logo-text">进销存管理</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="transparent"
        text-color="var(--sidebar-text)"
        active-text-color="var(--sidebar-active-text)"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-menu-item v-if="isAdmin" index="/employee">
          <el-icon><UserFilled /></el-icon>
          <span>员工管理</span>
        </el-menu-item>

        <el-menu-item v-if="!isAdmin" index="/employee/info">
          <el-icon><UserFilled /></el-icon>
          <span>个人信息</span>
        </el-menu-item>

        <el-menu-item v-if="isAdmin" index="/supplier">
          <el-icon><Box /></el-icon>
          <span>供应商管理</span>
        </el-menu-item>

        <el-menu-item index="/product">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>

        <el-menu-item index="/purchase">
          <el-icon><DocumentCopy /></el-icon>
          <span>采购管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer" v-show="!isCollapse">
        <div class="sidebar-footer-info">
          <el-tag :type="isAdmin ? 'danger' : 'info'" size="small" effect="dark" round>
            {{ isAdmin ? '管理员' : '员工' }}
          </el-tag>
        </div>
      </div>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" :size="18">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-tag :type="isAdmin ? 'danger' : 'info'" size="small" round>
            {{ isAdmin ? '管理员' : '普通员工' }}
          </el-tag>
          <el-dropdown trigger="click" @command="handleDropdown">
            <span class="user-info">
              <el-avatar :size="32" :style="{ background: isAdmin ? 'linear-gradient(135deg, #6366f1, #8b5cf6)' : 'linear-gradient(135deg, #3b82f6, #6366f1)' }">
                {{ username?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

const isCollapse = ref(false)
const isAdmin = computed(() => store.isAdmin)
const username = computed(() => store.username)
const activeMenu = computed(() => route.path)

function handleLogout() {
  ElMessageBox.confirm('确认退出登录？', '提示').then(() => {
    store.logout()
    router.push('/login')
  }).catch(() => {})
}

function handleDropdown(command) {
  if (command === 'logout') handleLogout()
}
</script>

<style scoped>
.aside {
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 10;
}

.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 18px;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.logo-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: var(--accent-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  letter-spacing: 0.3px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  padding: 12px 8px;
  background: transparent !important;
}

.sidebar-menu .el-menu-item {
  border-radius: 8px;
  margin-bottom: 2px;
  height: 44px;
  line-height: 44px;
  padding: 0 12px !important;
  transition: all 0.15s ease;
}

.sidebar-menu .el-menu-item:hover {
  background: var(--sidebar-hover-bg) !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: var(--sidebar-active-bg) !important;
  color: var(--sidebar-active-text) !important;
}

.sidebar-menu .el-menu-item .el-icon {
  font-size: 18px;
  margin-right: 10px;
}

.sidebar-menu .el-menu-item span {
  font-size: 14px;
  font-weight: 500;
}

.sidebar-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.sidebar-footer-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Header */
.header {
  height: 64px !important;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--header-bg);
  border-bottom: 1px solid var(--header-border);
  padding: 0 24px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  position: sticky;
  top: 0;
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 18px;
  cursor: pointer;
  color: var(--text-secondary);
  padding: 6px;
  border-radius: 8px;
  transition: all 0.15s ease;
}

.collapse-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--text-primary);
}

:deep(.el-breadcrumb__inner) {
  font-weight: 500 !important;
  color: var(--text-secondary) !important;
}

:deep(.el-breadcrumb__inner.is-link) {
  color: var(--text-muted) !important;
}

:deep(.el-breadcrumb__inner.is-link:hover) {
  color: #6366f1 !important;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 2px 8px 2px 2px;
  border-radius: 20px;
  transition: background 0.15s ease;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.04);
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main {
  background: var(--main-bg);
  overflow-y: auto;
  padding: 0;
}
</style>
