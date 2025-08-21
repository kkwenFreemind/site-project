<template>
  <div class="login-logs">
    <div class="page-header">
      <h1>登入記錄</h1>
      <p>查看系統的所有登入活動記錄</p>
    </div>

    <!-- 過濾工具欄 -->
    <div class="toolbar">
      <div class="filters">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="開始日期"
          end-placeholder="結束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
          style="width: 240px"
        />
        
        <el-select
          v-model="statusFilter"
          placeholder="選擇狀態"
          clearable
          @change="handleFilterChange"
          style="width: 140px"
        >
          <el-option label="全部" value="" />
          <el-option label="成功" value="SUCCESS" />
          <el-option label="失敗" value="FAILED" />
        </el-select>
        
        <el-input
          v-model="userFilter"
          placeholder="搜尋用戶名"
          clearable
          @input="handleFilterChange"
          style="width: 200px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="actions">
        <el-button @click="loadLogs">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="primary" @click="exportLogs">
          <el-icon><Download /></el-icon>
          導出
        </el-button>
      </div>
    </div>

    <!-- 統計卡片 -->
    <div class="stats-grid">
      <el-card class="stats-card" shadow="hover">
        <div class="stats-item">
          <div class="stats-icon success">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-number">{{ statistics.successCount }}</div>
            <div class="stats-label">成功登入</div>
          </div>
        </div>
      </el-card>

      <el-card class="stats-card" shadow="hover">
        <div class="stats-item">
          <div class="stats-icon danger">
            <el-icon><CircleCloseFilled /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-number">{{ statistics.failedCount }}</div>
            <div class="stats-label">失敗登入</div>
          </div>
        </div>
      </el-card>

      <el-card class="stats-card" shadow="hover">
        <div class="stats-item">
          <div class="stats-icon warning">
            <el-icon><User /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-number">{{ statistics.uniqueUsers }}</div>
            <div class="stats-label">活躍用戶</div>
          </div>
        </div>
      </el-card>

      <el-card class="stats-card" shadow="hover">
        <div class="stats-item">
          <div class="stats-icon info">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-number">{{ statistics.totalRecords }}</div>
            <div class="stats-label">總記錄數</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 登入記錄表格 -->
    <el-card class="table-card">
      <el-table
        :data="filteredLogs"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'loginTime', order: 'descending' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="username" label="用戶名" width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="24" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="loginTime" label="登入時間" width="180" sortable>
          <template #default="{ row }">
            {{ formatDate(row.loginTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="ipAddress" label="IP地址" width="140">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="getLocationInfo(row.ipAddress)" placement="top">
              <span class="ip-address">{{ row.ipAddress }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        
        <el-table-column prop="userAgent" label="瀏覽器" min-width="200">
          <template #default="{ row }">
            <div class="user-agent">
              <el-icon class="browser-icon">
                <component :is="getBrowserIcon(row.userAgent)" />
              </el-icon>
              <span class="browser-info">{{ formatUserAgent(row.userAgent) }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="狀態" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'SUCCESS' ? 'success' : 'danger'"
              size="small"
            >
              <el-icon>
                <SuccessFilled v-if="row.status === 'SUCCESS'" />
                <CircleCloseFilled v-else />
              </el-icon>
              {{ row.status === 'SUCCESS' ? '成功' : '失敗' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="showLogDetail(row)"
            >
              詳情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分頁 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalRecords"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 詳情對話框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="登入記錄詳情"
      width="600px"
    >
      <div v-if="selectedLog" class="log-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="記錄ID">
            {{ selectedLog.id }}
          </el-descriptions-item>
          
          <el-descriptions-item label="用戶名">
            {{ selectedLog.username }}
          </el-descriptions-item>
          
          <el-descriptions-item label="登入時間">
            {{ formatDate(selectedLog.loginTime) }}
          </el-descriptions-item>
          
          <el-descriptions-item label="狀態">
            <el-tag
              :type="selectedLog.status === 'SUCCESS' ? 'success' : 'danger'"
              size="small"
            >
              {{ selectedLog.status === 'SUCCESS' ? '成功' : '失敗' }}
            </el-tag>
          </el-descriptions-item>
          
          <el-descriptions-item label="IP地址" :span="2">
            {{ selectedLog.ipAddress }}
          </el-descriptions-item>
          
          <el-descriptions-item label="User Agent" :span="2">
            <div class="user-agent-detail">
              {{ selectedLog.userAgent }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { adminAPI } from '@/api/auth'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  User,
  Document,
  SuccessFilled,
  CircleCloseFilled,
  Monitor
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'

// 響應式數據
const logs = ref([])
const loading = ref(false)
const showDetailDialog = ref(false)
const selectedLog = ref(null)

// 過濾條件
const dateRange = ref([])
const statusFilter = ref('')
const userFilter = ref('')

// 分頁
const currentPage = ref(1)
const pageSize = ref(20)
const totalRecords = ref(0)

// 統計數據
const statistics = reactive({
  successCount: 0,
  failedCount: 0,
  uniqueUsers: 0,
  totalRecords: 0
})

// 過濾後的記錄
const filteredLogs = computed(() => {
  let filtered = [...logs.value]
  
  // 日期過濾
  if (dateRange.value && dateRange.value.length === 2) {
    const [startDate, endDate] = dateRange.value
    filtered = filtered.filter(log => {
      const logDate = dayjs(log.loginTime).format('YYYY-MM-DD')
      return logDate >= startDate && logDate <= endDate
    })
  }
  
  // 狀態過濾
  if (statusFilter.value) {
    filtered = filtered.filter(log => log.status === statusFilter.value)
  }
  
  // 用戶過濾
  if (userFilter.value) {
    const query = userFilter.value.toLowerCase()
    filtered = filtered.filter(log => 
      log.username.toLowerCase().includes(query)
    )
  }
  
  return filtered
})

// 載入登入記錄
const loadLogs = async () => {
  try {
    loading.value = true
    
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    const response = await adminAPI.getLoginLogs(params)
    
    if (response.code === 200) {
      logs.value = response.data.content || response.data
      totalRecords.value = response.data.totalElements || response.data.length
      updateStatistics()
    }
  } catch (error) {
    console.error('載入登入記錄失敗:', error)
    ElMessage.error('載入登入記錄失敗')
  } finally {
    loading.value = false
  }
}

// 更新統計數據
const updateStatistics = () => {
  const successLogs = logs.value.filter(log => log.status === 'SUCCESS')
  const failedLogs = logs.value.filter(log => log.status === 'FAILED')
  const uniqueUserSet = new Set(logs.value.map(log => log.username))
  
  statistics.successCount = successLogs.length
  statistics.failedCount = failedLogs.length
  statistics.uniqueUsers = uniqueUserSet.size
  statistics.totalRecords = logs.value.length
}

// 過濾條件變化處理
const handleFilterChange = () => {
  currentPage.value = 1
}

// 日期範圍變化處理
const handleDateChange = () => {
  handleFilterChange()
}

// 分頁大小變化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadLogs()
}

// 當前頁變化
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadLogs()
}

// 顯示記錄詳情
const showLogDetail = (log) => {
  selectedLog.value = log
  showDetailDialog.value = true
}

// 導出記錄
const exportLogs = () => {
  ElMessage.info('導出功能開發中...')
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化User Agent
const formatUserAgent = (userAgent) => {
  if (!userAgent) return '未知'
  
  // 簡化User Agent顯示
  if (userAgent.includes('Chrome')) return 'Chrome'
  if (userAgent.includes('Firefox')) return 'Firefox'
  if (userAgent.includes('Safari')) return 'Safari'
  if (userAgent.includes('Edge')) return 'Edge'
  
  return userAgent.substring(0, 30) + '...'
}

// 獲取瀏覽器圖標
const getBrowserIcon = (userAgent) => {
  if (!userAgent) return Monitor
  
  if (userAgent.includes('Chrome')) return Monitor
  if (userAgent.includes('Firefox')) return Monitor
  if (userAgent.includes('Safari')) return Monitor
  if (userAgent.includes('Edge')) return Monitor
  
  return Monitor
}

// 獲取位置信息（模擬）
const getLocationInfo = (ip) => {
  // 這裡可以集成IP地理位置API
  return `IP: ${ip}\n位置: 未知`
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.login-logs {
  padding: 0;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.page-header p {
  color: #909399;
  font-size: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filters {
  display: flex;
  gap: 12px;
  align-items: center;
}

.actions {
  display: flex;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
  transition: transform 0.3s;
}

.stats-card:hover {
  transform: translateY(-2px);
}

.stats-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stats-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
}

.stats-icon.success {
  background-color: #f0f9ff;
  color: #67c23a;
}

.stats-icon.danger {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stats-icon.warning {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.stats-icon.info {
  background-color: #f4f4f5;
  color: #909399;
}

.stats-content {
  flex: 1;
}

.stats-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stats-label {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}

.table-card {
  border-radius: 8px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  flex-shrink: 0;
}

.ip-address {
  font-family: monospace;
  color: #606266;
  cursor: pointer;
}

.ip-address:hover {
  color: #409eff;
}

.user-agent {
  display: flex;
  align-items: center;
  gap: 8px;
}

.browser-icon {
  color: #909399;
  flex-shrink: 0;
}

.browser-info {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.log-detail {
  padding: 10px 0;
}

.user-agent-detail {
  word-break: break-all;
  color: #606266;
  font-size: 13px;
  line-height: 1.4;
}

/* 響應式設計 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .actions {
    justify-content: center;
  }
}
</style>
