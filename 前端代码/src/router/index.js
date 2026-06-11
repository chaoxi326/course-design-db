import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/DashboardView.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('../views/employee/EmployeeList.vue'),
        meta: { title: '员工管理', roles: ['管理员'] }
      },
      {
        path: 'employee/info',
        name: 'EmployeeInfo',
        component: () => import('../views/employee/EmployeeInfo.vue'),
        meta: { title: '个人信息', roles: ['普通员工'] }
      },
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('../views/supplier/SupplierList.vue'),
        meta: { title: '供应商管理', roles: ['管理员'] }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('../views/product/ProductList.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'purchase',
        name: 'Purchase',
        component: () => import('../views/purchase/PurchaseList.vue'),
        meta: { title: '采购管理' }
      },
      {
        path: 'purchase/detail/:oId',
        name: 'PurchaseDetail',
        component: () => import('../views/purchase/PurchaseDetail.vue'),
        meta: { title: '采购明细' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const store = useUserStore()
  if (to.meta.requiresAuth === false) {
    next()
    return
  }
  if (!store.isLoggedIn) {
    next('/login')
    return
  }
  if (to.meta.roles && !to.meta.roles.includes(store.user.eLevel)) {
    next('/dashboard')
    return
  }
  next()
})

export default router
