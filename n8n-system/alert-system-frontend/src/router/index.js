import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, hideNav: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, hideNav: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { 
          title: '控制台',
          icon: 'Odometer'
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { 
          title: '個人資料',
          icon: 'User'
        }
      },
      // 系統管理
      {
        path: 'system',
        name: 'System',
        meta: { 
          title: '系統管理',
          icon: 'Setting',
          requiresAdmin: true
        },
        children: [
          {
            path: 'users',
            name: 'SystemUsers',
            component: () => import('@/views/system/users/index.vue'),
            meta: { 
              title: '帳號管理',
              icon: 'UserFilled',
              requiresAdmin: true
            }
          },
          {
            path: 'roles',
            name: 'SystemRoles',
            component: () => import('@/views/system/roles/index.vue'),
            meta: { 
              title: '角色管理',
              icon: 'Avatar',
              requiresAdmin: true
            }
          },
          {
            path: 'menus',
            name: 'SystemMenus',
            component: () => import('@/views/system/menus/index.vue'),
            meta: { 
              title: '選單管理',
              icon: 'Menu',
              requiresAdmin: true
            }
          }
        ]
      },
      // 日誌管理
      {
        path: 'logs',
        name: 'Logs',
        meta: { 
          title: '日誌管理',
          icon: 'Document',
          requiresAdmin: true
        },
        children: [
          {
            path: 'login-logs',
            name: 'LoginLogs',
            component: () => import('@/views/admin/LoginLogs.vue'),
            meta: { 
              title: '登入記錄',
              icon: 'List',
              requiresAdmin: true
            }
          }
        ]
      },
      // 保留舊的路由以兼容
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/admin/Users.vue'),
        meta: { 
          title: '用戶管理',
          icon: 'UserFilled',
          requiresAdmin: true,
          hidden: true // 隱藏在菜單中
        }
      },
      {
        path: 'login-logs',
        name: 'LoginLogsOld',
        component: () => import('@/views/admin/LoginLogs.vue'),
        meta: { 
          title: '登入記錄',
          icon: 'Document',
          requiresAdmin: true,
          hidden: true // 隱藏在菜單中
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守衛
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  console.log(`Route Guard: Navigating from ${from.path} to ${to.path}`)
  console.log('Route Guard: Auth token exists:', !!authStore.token)
  console.log('Route Guard: User exists:', !!authStore.user)
  console.log('Route Guard: Route meta:', to.meta)
  
  // 檢查是否需要認證
  if (to.meta.requiresAuth) {
    console.log('Route Guard: Route requires auth')
    // 如果沒有token，跳轉到登入頁
    if (!authStore.token) {
      console.log('Route Guard: No token, redirecting to login')
      next('/login')
      return
    }
    
    // 如果沒有用戶信息，嘗試獲取
    if (!authStore.user) {
      console.log('Route Guard: No user data, checking auth')
      const isValid = await authStore.checkAuth()
      console.log('Route Guard: Auth check result:', isValid)
      if (!isValid) {
        console.log('Route Guard: Auth check failed, redirecting to login')
        next('/login')
        return
      }
    }
    
    // 檢查管理員權限
    if (to.meta.requiresAdmin && !authStore.isAdmin) {
      console.log('Route Guard: Admin required but user is not admin, redirecting to dashboard')
      next('/dashboard')
      return
    }
  }
  
  // 如果已登入且訪問登入/註冊頁，跳轉到控制台
  if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
    console.log('Route Guard: Already authenticated, redirecting to dashboard')
    next('/dashboard')
    return
  }
  
  console.log('Route Guard: Allowing navigation')
  next()
})

export default router
