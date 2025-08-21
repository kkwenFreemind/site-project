<template>
  <div class="layout">
    <el-container class="layout-container">
      <!-- 側邊欄 -->
      <el-aside :width="collapsed ? '56px' : '220px'" class="layout-aside">
        <div class="logo">
          <el-icon class="logo-icon"><Bell /></el-icon>
          <span v-if="!collapsed" class="logo-text">告警系統</span>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="layout-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :collapse="collapsed"
          router
        >
          <template v-for="item in menuItems" :key="item.path">
            <!-- 有子菜單的項目 -->
            <el-sub-menu v-if="item.children && item.children.length > 0 && !item.hidden" :index="item.path">
              <template #title>
                <el-icon><component :is="item.meta.icon" /></el-icon>
                <span>{{ item.meta.title }}</span>
              </template>
              
              <template v-for="child in item.children" :key="child.path">
                <el-menu-item v-if="!child.meta?.hidden" :index="child.path">
                  <el-icon><component :is="child.meta.icon" /></el-icon>
                  <template #title>{{ child.meta.title }}</template>
                </el-menu-item>
              </template>
            </el-sub-menu>
            
            <!-- 普通菜單項 -->
            <el-menu-item v-else-if="!item.hidden" :index="item.path">
              <el-icon><component :is="item.meta.icon" /></el-icon>
              <template #title>{{ item.meta.title }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-container class="layout-main">
        <!-- 頂部導航 -->
        <el-header class="layout-header">
          <div class="header-left">
            <el-button
              type="text"
              @click="toggleSidebar"
              class="collapse-btn"
            >
              <el-icon><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
            </el-button>
            
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首頁</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <el-dropdown trigger="click" @command="handleUserMenuCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="userAvatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ authStore.user?.fullName || authStore.user?.username }}</span>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    個人資料
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    登出
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主要內容區 -->
        <el-main class="layout-content">
          <div class="content-wrapper">
            <router-view v-slot="{ Component }">
              <transition name="fade-transform" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Bell,
  Fold,
  Expand,
  User,
  ArrowDown,
  SwitchButton,
  Odometer,
  UserFilled,
  Document,
  Setting,
  Avatar,
  Menu,
  List
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const collapsed = ref(false)

// 計算當前頁面標題
const currentPageTitle = computed(() => {
  return route.meta?.title || '控制台'
})

// 用戶頭像
const userAvatar = computed(() => {
  return authStore.user?.avatar || ''
})

// 菜單項
const menuItems = computed(() => {
  const allMenus = [
    {
      path: '/dashboard',
      meta: { title: '控制台', icon: 'Odometer' }
    },
    {
      path: '/profile',
      meta: { title: '個人資料', icon: 'User' }
    },
    // 系統管理（只有管理員可見）
    {
      path: '/system',
      meta: { title: '系統管理', icon: 'Setting' },
      hidden: !authStore.isAdmin,
      children: [
        {
          path: '/system/users',
          meta: { title: '帳號管理', icon: 'UserFilled' }
        },
        {
          path: '/system/roles',
          meta: { title: '角色管理', icon: 'Avatar' }
        },
        {
          path: '/system/menus',
          meta: { title: '選單管理', icon: 'Menu' }
        }
      ]
    },
    // 日誌管理（只有管理員可見）
    {
      path: '/logs',
      meta: { title: '日誌管理', icon: 'Document' },
      hidden: !authStore.isAdmin,
      children: [
        {
          path: '/logs/login-logs',
          meta: { title: '登入記錄', icon: 'List' }
        }
      ]
    }
  ]
  
  return allMenus.filter(item => !item.hidden)
})

// 切換側邊欄
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}

// 處理用戶菜單命令
const handleUserMenuCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '確定要登出嗎？',
          '提示',
          {
            confirmButtonText: '確定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        await authStore.logout()
        ElMessage.success('已成功登出')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('登出失敗:', error)
        }
      }
      break
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
  width: 100vw;
  background: var(--bg-secondary);
}

.layout-container {
  height: 100%;
}

.layout-aside {
  background: linear-gradient(180deg, #1e293b 0%, #334155 100%);
  border-right: 1px solid rgba(75, 85, 99, 0.3);
  box-shadow: 4px 0 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s var(--ease-out);
  overflow: hidden;
  z-index: 100;
}

.logo {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: var(--text-inverse);
  border-bottom: 1px solid rgba(75, 85, 99, 0.3);
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.logo::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.08) 0%, 
    rgba(255, 255, 255, 0.02) 100%);
  pointer-events: none;
}

.logo-text {
  font-size: var(--text-lg);
  font-weight: 700;
  margin-left: 8px;
  letter-spacing: -0.025em;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 32px;
  color: var(--text-inverse);
  position: relative;
  z-index: 1;
}

.layout-menu {
  border: none;
  background: transparent;
  height: calc(100vh - 72px);
  overflow-y: auto;
  padding: 8px 0;
}

/* 菜單項目樣式重設 */
.layout-menu :deep(.el-menu-item) {
  margin: 2px 8px;
  padding: 0 12px;
  border-radius: var(--radius-md);
  background: transparent;
  border: 1px solid transparent;
  color: rgba(203, 213, 225, 0.8);
  font-weight: 500;
  font-size: var(--text-sm);
  transition: all 0.2s var(--ease-out);
  height: 40px;
  line-height: 38px;
  display: flex;
  align-items: center;
}

.layout-menu :deep(.el-menu-item span) {
  margin-left: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.layout-menu :deep(.el-menu-item:hover) {
  background: rgba(59, 130, 246, 0.1);
  color: #e2e8f0;
  border-color: rgba(59, 130, 246, 0.3);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
  transform: translateX(4px);
}

.layout-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(59, 130, 246, 0.1) 100%);
  color: #ffffff;
  border-color: rgba(59, 130, 246, 0.4);
  font-weight: 600;
  box-shadow: 
    0 4px 12px rgba(59, 130, 246, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  position: relative;
}

.layout-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 24px;
  background: linear-gradient(180deg, #3b82f6 0%, #60a5fa 100%);
  border-radius: 0 2px 2px 0;
}

.layout-menu :deep(.el-sub-menu__title) {
  margin: 2px 8px;
  padding: 0 12px;
  border-radius: var(--radius-md);
  background: transparent;
  border: 1px solid transparent;
  color: rgba(203, 213, 225, 0.9);
  font-weight: 600;
  font-size: var(--text-sm);
  height: 40px;
  line-height: 38px;
  transition: all 0.2s var(--ease-out);
}

.layout-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(59, 130, 246, 0.08);
  color: #f1f5f9;
  border-color: rgba(59, 130, 246, 0.2);
}

.layout-menu :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  background: rgba(59, 130, 246, 0.15);
  color: #ffffff;
  border-color: rgba(59, 130, 246, 0.3);
  font-weight: 700;
}

.layout-menu :deep(.el-sub-menu .el-menu-item) {
  margin-left: 24px;
  margin-right: 8px;
  padding: 0 12px;
  font-size: var(--text-sm);
  font-weight: 400;
  color: rgba(203, 213, 225, 0.7);
}

.layout-menu :deep(.el-sub-menu .el-menu-item:hover) {
  color: #e2e8f0;
  background: rgba(59, 130, 246, 0.08);
}

.layout-menu :deep(.el-sub-menu .el-menu-item.is-active) {
  color: #ffffff;
  background: rgba(59, 130, 246, 0.15);
}

/* 菜單圖標樣式 */
.layout-menu :deep(.el-icon) {
  margin-right: 8px;
  font-size: 16px;
  transition: all 0.2s var(--ease-out);
  flex-shrink: 0;
  width: 16px;
}

.layout-menu :deep(.el-menu-item:hover .el-icon),
.layout-menu :deep(.el-sub-menu__title:hover .el-icon) {
  color: #60a5fa;
  transform: scale(1.1);
}

.layout-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #ffffff;
  filter: drop-shadow(0 0 4px rgba(59, 130, 246, 0.4));
}

/* 收起狀態優化 */
.layout-menu.el-menu--collapse :deep(.el-menu-item),
.layout-menu.el-menu--collapse :deep(.el-sub-menu__title) {
  margin: 2px 4px;
  padding: 0 8px;
  justify-content: center;
  width: calc(100% - 8px);
}

.layout-menu.el-menu--collapse :deep(.el-menu-item .el-icon),
.layout-menu.el-menu--collapse :deep(.el-sub-menu__title .el-icon) {
  margin-right: 0;
}

.layout-menu.el-menu--collapse :deep(.el-menu-item:hover),
.layout-menu.el-menu--collapse :deep(.el-sub-menu__title:hover) {
  transform: scale(1.05);
}

.layout-header {
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-color);
  padding: 0 var(--space-xl);
  display: flex;
  align-items: center;
  justify-content: space-between;
  backdrop-filter: blur(8px);
  box-shadow: var(--shadow-sm);
  height: 64px;
  z-index: 50;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  margin-right: var(--space-xl);
  color: var(--text-secondary);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s var(--ease-out);
  font-size: 18px;
}

.collapse-btn:hover {
  background: var(--bg-tertiary);
  color: var(--accent-color);
  border-color: var(--accent-color);
  box-shadow: var(--shadow-sm);
}

.collapse-btn:active {
  transform: scale(0.95);
}

/* 面包屑導航樣式 */
.header-left :deep(.el-breadcrumb) {
  font-weight: 500;
}

.header-left :deep(.el-breadcrumb__inner) {
  color: var(--text-secondary);
  font-weight: 500;
}

.header-left :deep(.el-breadcrumb__inner.is-link) {
  color: var(--accent-color);
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  transition: all 0.2s var(--ease-out);
  border: 1px solid transparent;
}

.user-info:hover {
  background-color: var(--bg-secondary);
  border-color: var(--border-color);
}

.username {
  margin: 0 var(--space-sm);
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-primary);
}

.dropdown-icon {
  font-size: 12px;
  color: var(--text-tertiary);
  transition: transform 0.2s var(--ease-out);
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

.layout-content {
  background-color: var(--bg-secondary);
  padding: var(--space-xl);
  min-height: calc(100vh - 64px);
  overflow-y: auto;
}

.content-wrapper {
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

/* 路由過渡動畫 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.25s var(--ease-out);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(16px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-16px);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .layout-header {
    padding: 0 var(--space-lg);
  }
  
  .collapse-btn {
    margin-right: var(--space-lg);
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
  
  .username {
    display: none;
  }
  
  .layout-content {
    padding: var(--space-lg);
  }
}

@media (max-width: 480px) {
  .layout-header {
    padding: 0 var(--space-md);
  }
  
  .collapse-btn {
    margin-right: var(--space-md);
  }
  
  .layout-content {
    padding: var(--space-md);
  }
}

/* 深色模式適配 */
@media (prefers-color-scheme: dark) {
  .logo {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  }
}
</style>
