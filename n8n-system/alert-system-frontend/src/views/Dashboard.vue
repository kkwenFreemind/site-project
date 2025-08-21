<template>
  <div class="page-container">
    <!-- 頁面標題 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">控制台</h1>
        <p class="page-description">歡迎回來，{{ authStore.user?.fullName || authStore.user?.username }}！</p>
      </div>
    </div>

    <!-- 統計卡片 -->
    <div class="stats-grid mb-2xl">
      <div class="stats-card">
        <div class="stats-content">
          <div class="stats-icon primary">
            <el-icon><User /></el-icon>
          </div>
          <div class="stats-data">
            <div class="stats-number">{{ dashboardData.totalUsers }}</div>
            <div class="stats-label">總用戶數</div>
          </div>
        </div>
        <div class="stats-trend positive">
          <span class="trend-text">+12%</span>
          <span class="trend-period">vs 上月</span>
        </div>
      </div>

      <div class="stats-card">
        <div class="stats-content">
          <div class="stats-icon success">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="stats-data">
            <div class="stats-number">{{ dashboardData.activeUsers }}</div>
            <div class="stats-label">活躍用戶</div>
          </div>
        </div>
        <div class="stats-trend positive">
          <span class="trend-text">+8%</span>
          <span class="trend-period">vs 上月</span>
        </div>
      </div>

      <div class="stats-card">
        <div class="stats-content">
          <div class="stats-icon warning">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stats-data">
            <div class="stats-number">{{ dashboardData.todayLogins }}</div>
            <div class="stats-label">今日登入</div>
          </div>
        </div>
        <div class="stats-trend positive">
          <span class="trend-text">+24%</span>
          <span class="trend-period">vs 昨日</span>
        </div>
      </div>

      <div class="stats-card">
        <div class="stats-content">
          <div class="stats-icon danger">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stats-data">
            <div class="stats-number">{{ dashboardData.failedLogins }}</div>
            <div class="stats-label">失敗登入</div>
          </div>
        </div>
        <div class="stats-trend negative">
          <span class="trend-text">-5%</span>
          <span class="trend-period">vs 昨日</span>
        </div>
      </div>
    </div>

    <!-- 主要內容區 -->
    <div class="dashboard-grid">
      <!-- 快速操作 -->
      <div class="pro-panel">
        <div class="pro-panel-header">
          <h3 class="pro-panel-title">快速操作</h3>
        </div>
        <div class="pro-panel-body">
          <div class="quick-actions">
            <button class="quick-action-btn" @click="$router.push('/profile')">
              <div class="action-icon primary">
                <el-icon><User /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">編輯個人資料</div>
                <div class="action-desc">管理您的個人信息和設置</div>
              </div>
              <div class="action-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </button>
            
            <button v-if="authStore.isAdmin" class="quick-action-btn" @click="$router.push('/system/users')">
              <div class="action-icon success">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">管理用戶</div>
                <div class="action-desc">查看和管理系統用戶帳號</div>
              </div>
              <div class="action-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </button>
            
            <button v-if="authStore.isAdmin" class="quick-action-btn" @click="$router.push('/logs/login-logs')">
              <div class="action-icon warning">
                <el-icon><Document /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">查看日誌</div>
                <div class="action-desc">檢視系統操作和登入記錄</div>
              </div>
              <div class="action-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </button>
            
            <button v-if="authStore.isAdmin" class="quick-action-btn" @click="$router.push('/system/roles')">
              <div class="action-icon info">
                <el-icon><Avatar /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">角色管理</div>
                <div class="action-desc">配置用戶角色和權限</div>
              </div>
              <div class="action-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </button>
          </div>
        </div>
      </div>

      <!-- 系統信息 -->
      <div class="pro-panel">
        <div class="pro-panel-header">
          <h3 class="pro-panel-title">系統信息</h3>
        </div>
        <div class="pro-panel-body">
          <div class="system-info">
            <div class="info-item">
              <div class="info-label">
                <el-icon><User /></el-icon>
                當前用戶
              </div>
              <div class="info-value">{{ authStore.user?.username }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">
                <el-icon><Avatar /></el-icon>
                用戶角色
              </div>
              <div class="info-value">
                <div class="role-tags">
                  <span 
                    v-for="role in authStore.userRoles" 
                    :key="role"
                    class="role-tag"
                    :class="role.toLowerCase()"
                  >
                    {{ role }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="info-item">
              <div class="info-label">
                <el-icon><SuccessFilled /></el-icon>
                帳戶狀態
              </div>
              <div class="info-value">
                <span class="status-indicator success">
                  <span class="status-dot success"></span>
                  活躍
                </span>
              </div>
            </div>
            
            <div class="info-item">
              <div class="info-label">
                <el-icon><Clock /></el-icon>
                最後登入
              </div>
              <div class="info-value">{{ formatDate(new Date()) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近活動 -->
    <div class="pro-panel mt-xl">
      <div class="pro-panel-header">
        <h3 class="pro-panel-title">最近活動</h3>
      </div>
      <div class="pro-panel-body">
        <div class="activity-table">
          <el-table :data="recentActivities" class="data-table">
            <el-table-column prop="time" label="時間" width="180">
              <template #default="{ row }">
                <div class="time-cell">
                  <el-icon class="time-icon"><Clock /></el-icon>
                  {{ row.time }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="action" label="操作">
              <template #default="{ row }">
                <div class="action-cell">
                  <el-icon class="action-icon"><Operation /></el-icon>
                  {{ row.action }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="ip" label="IP地址" width="150">
              <template #default="{ row }">
                <div class="ip-cell">
                  <el-icon class="ip-icon"><Monitor /></el-icon>
                  <code>{{ row.ip }}</code>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="狀態" width="120">
              <template #default="{ row }">
                <div class="status-indicator" :class="row.status.toLowerCase()">
                  <span class="status-dot" :class="row.status.toLowerCase()"></span>
                  {{ row.status === 'SUCCESS' ? '成功' : '失敗' }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import {
  User,
  UserFilled,
  Document,
  SuccessFilled,
  Warning,
  ArrowRight,
  Avatar,
  Clock,
  Operation,
  Monitor
} from '@element-plus/icons-vue'
import { formatDate } from '@/utils/index.js'

const authStore = useAuthStore()

// 儀表板數據
const dashboardData = ref({
  totalUsers: 0,
  activeUsers: 0,
  todayLogins: 0,
  failedLogins: 0
})

// 最近活動
const recentActivities = ref([])

// 載入儀表板數據
const loadDashboardData = async () => {
  try {
    // 這裡應該調用實際的API，目前使用模擬數據
    dashboardData.value = {
      totalUsers: 156,
      activeUsers: 89,
      todayLogins: 42,
      failedLogins: 3
    }

    // 模擬最近的登入記錄
    recentActivities.value = [
      {
        time: formatDate(new Date()),
        action: '用戶登入',
        ip: '127.0.0.1',
        status: 'SUCCESS'
      },
      {
        time: formatDate(new Date(Date.now() - 30 * 60000)),
        action: '用戶登入',
        ip: '192.168.1.100',
        status: 'SUCCESS'
      },
      {
        time: formatDate(new Date(Date.now() - 60 * 60000)),
        action: '登入失敗',
        ip: '10.0.0.1',
        status: 'FAILED'
      }
    ]
  } catch (error) {
    console.error('載入儀表板數據失敗:', error)
    ElMessage.error('載入數據失敗')
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
/* 專業 Dashboard 樣式 */

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-xl);
}

.stats-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  transition: all 0.25s var(--ease-out);
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--accent-color) 0%, var(--accent-light) 100%);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-medium);
}

.stats-content {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  margin-bottom: var(--space-md);
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--text-inverse);
  flex-shrink: 0;
}

.stats-icon.primary {
  background: linear-gradient(135deg, var(--accent-color) 0%, var(--accent-light) 100%);
}

.stats-icon.success {
  background: linear-gradient(135deg, var(--success-color) 0%, #10b981 100%);
}

.stats-icon.warning {
  background: linear-gradient(135deg, var(--warning-color) 0%, #f59e0b 100%);
}

.stats-icon.danger {
  background: linear-gradient(135deg, var(--danger-color) 0%, #ef4444 100%);
}

.stats-data {
  flex: 1;
}

.stats-number {
  font-size: var(--text-4xl);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--space-xs);
  letter-spacing: -0.025em;
}

.stats-label {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.025em;
}

.stats-trend {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-size: var(--text-sm);
  font-weight: 500;
}

.stats-trend.positive {
  color: var(--success-color);
}

.stats-trend.negative {
  color: var(--danger-color);
}

.trend-text {
  font-weight: 600;
}

.trend-period {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: var(--space-xl);
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.quick-action-btn {
  display: flex;
  align-items: center;
  width: 100%;
  padding: var(--space-lg);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s var(--ease-out);
  text-align: left;
  gap: var(--space-md);
}

.quick-action-btn:hover {
  background: var(--bg-elevated);
  border-color: var(--accent-color);
  transform: translateX(4px);
  box-shadow: var(--shadow-sm);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: var(--text-inverse);
  flex-shrink: 0;
}

.action-icon.primary {
  background: var(--accent-color);
}

.action-icon.success {
  background: var(--success-color);
}

.action-icon.warning {
  background: var(--warning-color);
}

.action-icon.info {
  background: var(--info-color);
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-xs);
}

.action-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.4;
}

.action-arrow {
  color: var(--text-tertiary);
  font-size: 16px;
  transition: all 0.2s var(--ease-out);
}

.quick-action-btn:hover .action-arrow {
  color: var(--accent-color);
  transform: translateX(4px);
}

.system-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-md) 0;
  border-bottom: 1px solid var(--border-light);
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
}

.info-label .el-icon {
  font-size: 16px;
  color: var(--text-tertiary);
}

.info-value {
  color: var(--text-primary);
  font-weight: 500;
  font-size: var(--text-sm);
}

.role-tags {
  display: flex;
  gap: var(--space-xs);
}

.role-tag {
  display: inline-flex;
  align-items: center;
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-base);
  font-size: var(--text-xs);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.025em;
}

.role-tag.admin {
  background-color: rgba(220, 38, 38, 0.1);
  color: var(--danger-color);
  border: 1px solid rgba(220, 38, 38, 0.2);
}

.role-tag.user {
  background-color: rgba(59, 130, 246, 0.1);
  color: var(--accent-color);
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.activity-table {
  overflow: hidden;
  border-radius: var(--radius-lg);
}

.time-cell, .action-cell, .ip-cell {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.time-icon, .action-icon, .ip-icon {
  font-size: 14px;
  color: var(--text-tertiary);
}

.ip-cell code {
  font-family: var(--font-family-mono);
  font-size: var(--text-xs);
  background: var(--bg-secondary);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
}

.status-indicator.success {
  background-color: rgba(5, 150, 105, 0.1);
  color: var(--success-color);
  border: 1px solid rgba(5, 150, 105, 0.2);
}

.status-indicator.failed {
  background-color: rgba(220, 38, 38, 0.1);
  color: var(--danger-color);
  border: 1px solid rgba(220, 38, 38, 0.2);
}

.status-dot.success {
  background-color: var(--success-color);
}

.status-dot.failed {
  background-color: var(--danger-color);
}

/* 響應式設計 */
@media (max-width: 1200px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: var(--space-xl);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--space-lg);
  }
  
  .stats-card {
    padding: var(--space-lg);
  }
  
  .stats-content {
    gap: var(--space-md);
  }
  
  .stats-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .stats-number {
    font-size: var(--text-3xl);
  }
  
  .action-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .quick-action-btn {
    padding: var(--space-md);
  }
  
  .action-title {
    font-size: var(--text-sm);
  }
  
  .action-desc {
    font-size: var(--text-xs);
  }
}

@media (max-width: 480px) {
  .stats-card {
    padding: var(--space-md);
  }
  
  .stats-content {
    flex-direction: column;
    text-align: center;
    gap: var(--space-sm);
  }
  
  .quick-action-btn {
    flex-direction: column;
    text-align: center;
    gap: var(--space-sm);
  }
  
  .action-arrow {
    display: none;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-sm);
  }
}
</style>
