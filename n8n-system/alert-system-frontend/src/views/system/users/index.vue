<template>
<template>
  <div class="page-container">
    <!-- 頁面標題 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">帳號管理</h1>
        <p class="page-description">管理系統用戶帳號、角色權限和用戶狀態</p>
      </div>
      <el-button type="primary" @click="showCreateDialog = true" class="create-btn">
        <el-icon><Plus /></el-icon>
        新增用戶
      </el-button>
    </div>

    <!-- 搜索區域 -->
    <div class="pro-panel mb-xl">
      <div class="pro-panel-header">
        <h3 class="pro-panel-title">篩選條件</h3>
      </div>
      <div class="pro-panel-body">
        <el-form :model="searchForm" class="search-form">
          <div class="form-grid">
            <el-form-item label="用戶名" class="form-item">
              <el-input 
                v-model="searchForm.username" 
                placeholder="請輸入用戶名" 
                clearable 
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item label="郵箱" class="form-item">
              <el-input 
                v-model="searchForm.email" 
                placeholder="請輸入郵箱" 
                clearable 
                prefix-icon="Message"
              />
            </el-form-item>
            <el-form-item label="角色" class="form-item">
              <el-select v-model="searchForm.roleId" placeholder="請選擇角色" clearable>
                <el-option
                  v-for="role in roles"
                  :key="role.id"
                  :label="role.name"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="狀態" class="form-item">
              <el-select v-model="searchForm.status" placeholder="請選擇狀態" clearable>
                <el-option label="啟用" value="ACTIVE" />
                <el-option label="禁用" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </div>
          <div class="search-actions mt-xl">
            <el-button type="primary" @click="searchUsers" class="search-btn">
              <el-icon><Search /></el-icon>
              查詢
            </el-button>
            <el-button @click="resetSearch" class="reset-btn">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 用戶列表 -->
    <div class="pro-panel">
      <div class="pro-panel-header">
        <h3 class="pro-panel-title">用戶列表</h3>
      </div>
      <div class="pro-panel-body">
        <el-table
          v-loading="loading"
          :data="users"
          class="data-table"
        >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用戶名" width="120" />
        <el-table-column prop="email" label="郵箱" width="200" />
        <el-table-column prop="fullName" label="姓名" width="120" />
        <el-table-column label="角色" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roles"
              :key="role.id"
              :type="getRoleTagType(role.name)"
              size="small"
              style="margin-right: 5px;"
            >
              {{ role.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="狀態" width="100">
          <template #default="{ row }">
            <div class="status-indicator" :class="row.status.toLowerCase()">
              <span class="status-dot" :class="row.status.toLowerCase()"></span>
              {{ row.status === 'ACTIVE' ? '啟用' : '禁用' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="創建時間" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="editUser(row)" class="action-btn">
                <el-icon><Edit /></el-icon>
                編輯
              </el-button>
              <el-button type="info" size="small" @click="assignRoles(row)" class="action-btn">
                <el-icon><UserFilled /></el-icon>
                分配角色
              </el-button>
              <el-button
                :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
                size="small"
                @click="toggleUserStatus(row)"
                class="action-btn"
              >
                <el-icon><Switch /></el-icon>
                {{ row.status === 'ACTIVE' ? '禁用' : '啟用' }}
              </el-button>
              <el-button type="danger" size="small" @click="deleteUser(row)" class="action-btn">
                <el-icon><Delete /></el-icon>
                刪除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分頁 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadUsers"
          @current-change="loadUsers"
          class="pro-pagination"
        />
      </div>
    </div>

    <!-- 新增/編輯用戶對話框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingUser ? '編輯用戶' : '新增用戶'"
      width="600px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用戶名" prop="username">
          <el-input v-model="userForm.username" :disabled="!!editingUser" />
        </el-form-item>
        <el-form-item label="郵箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="userForm.fullName" />
        </el-form-item>
        <el-form-item v-if="!editingUser" label="密碼" prop="password">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="狀態" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio label="ACTIVE">啟用</el-radio>
            <el-radio label="INACTIVE">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="saving">
          {{ editingUser ? '更新' : '創建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配角色對話框 -->
    <el-dialog v-model="showRoleDialog" title="分配角色" width="500px">
      <el-form label-width="100px">
        <el-form-item label="用戶">
          <span>{{ currentUser?.username }} ({{ currentUser?.email }})</span>
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox
              v-for="role in roles"
              :key="role.id"
              :label="role.id"
            >
              {{ role.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUserRoles" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Switch, UserFilled, User, Message } from '@element-plus/icons-vue'
import { userAPI, roleAPI } from '@/api/system'
import { formatDate } from '@/utils/date'

// 響應式數據
const loading = ref(false)
const saving = ref(false)
const users = ref([])
const roles = ref([])
const showCreateDialog = ref(false)
const showRoleDialog = ref(false)
const editingUser = ref(null)
const currentUser = ref(null)
const selectedRoles = ref([])

// 搜索表單
const searchForm = reactive({
  username: '',
  email: '',
  roleId: '',
  status: ''
})

// 分頁數據
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 用戶表單
const userForm = reactive({
  username: '',
  email: '',
  fullName: '',
  password: '',
  status: 'ACTIVE'
})

// 表單驗證規則
const userRules = {
  username: [
    { required: true, message: '請輸入用戶名', trigger: 'blur' },
    { min: 3, max: 20, message: '用戶名長度在 3 到 20 個字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '請輸入郵箱', trigger: 'blur' },
    { type: 'email', message: '請輸入正確的郵箱地址', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '請輸入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '請輸入密碼', trigger: 'blur' },
    { min: 6, message: '密碼長度不能少於 6 個字符', trigger: 'blur' }
  ]
}

// 表單引用
const userFormRef = ref()

// 獲取角色標籤類型
const getRoleTagType = (roleName) => {
  const typeMap = {
    'ADMIN': 'danger',
    'USER': 'primary',
    'MANAGER': 'warning'
  }
  return typeMap[roleName] || 'info'
}

// 加載用戶列表
const loadUsers = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      ...searchForm
    }
    
    const response = await userAPI.getUsers(params)
    if (response.data.code === 200) {
      users.value = response.data.data.content || response.data.data
      pagination.total = response.data.data.totalElements || response.data.data.length
    }
  } catch (error) {
    ElMessage.error('加載用戶列表失敗：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加載角色列表
const loadRoles = async () => {
  try {
    const response = await roleAPI.getRoles()
    if (response.data.code === 200) {
      roles.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('加載角色列表失敗：' + error.message)
  }
}

// 搜索用戶
const searchUsers = () => {
  pagination.page = 1
  loadUsers()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    username: '',
    email: '',
    roleId: '',
    status: ''
  })
  searchUsers()
}

// 編輯用戶
const editUser = (user) => {
  editingUser.value = user
  Object.assign(userForm, {
    username: user.username,
    email: user.email,
    fullName: user.fullName,
    status: user.status
  })
  showCreateDialog.value = true
}

// 保存用戶
const saveUser = async () => {
  try {
    await userFormRef.value.validate()
    saving.value = true
    
    let response
    if (editingUser.value) {
      response = await userAPI.updateUser(editingUser.value.id, userForm)
    } else {
      response = await userAPI.createUser(userForm)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(editingUser.value ? '更新成功' : '創建成功')
      showCreateDialog.value = false
      resetUserForm()
      loadUsers()
    }
  } catch (error) {
    ElMessage.error('保存失敗：' + error.message)
  } finally {
    saving.value = false
  }
}

// 重置用戶表單
const resetUserForm = () => {
  editingUser.value = null
  Object.assign(userForm, {
    username: '',
    email: '',
    fullName: '',
    password: '',
    status: 'ACTIVE'
  })
}

// 分配角色
const assignRoles = (user) => {
  currentUser.value = user
  selectedRoles.value = user.roles.map(role => role.id)
  showRoleDialog.value = true
}

// 保存用戶角色
const saveUserRoles = async () => {
  try {
    saving.value = true
    const response = await userAPI.updateUserRoles(currentUser.value.id, selectedRoles.value)
    
    if (response.data.code === 200) {
      ElMessage.success('角色分配成功')
      showRoleDialog.value = false
      loadUsers()
    }
  } catch (error) {
    ElMessage.error('角色分配失敗：' + error.message)
  } finally {
    saving.value = false
  }
}

// 切換用戶狀態
const toggleUserStatus = async (user) => {
  try {
    const newStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    const action = newStatus === 'ACTIVE' ? '啟用' : '禁用'
    
    await ElMessageBox.confirm(
      `確定要${action}用戶 "${user.username}" 嗎？`,
      '確認操作',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await userAPI.updateUserStatus(user.id, newStatus)
    if (response.data.code === 200) {
      ElMessage.success(`${action}成功`)
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失敗：' + error.message)
    }
  }
}

// 刪除用戶
const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `確定要刪除用戶 "${user.username}" 嗎？此操作不可逆！`,
      '確認刪除',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await userAPI.deleteUser(user.id)
    if (response.data.code === 200) {
      ElMessage.success('刪除成功')
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('刪除失敗：' + error.message)
    }
  }
}

// 組件掛載
onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<style scoped>
/* 專業用戶管理頁面樣式 */

.create-btn {
  height: 44px;
  padding: 0 var(--space-xl);
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.search-form {
  margin: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-lg);
}

.form-item {
  margin-bottom: 0;
}

.form-item :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: var(--text-sm);
  margin-bottom: var(--space-sm);
}

.search-actions {
  display: flex;
  gap: var(--space-md);
  justify-content: flex-end;
  padding-top: var(--space-lg);
  border-top: 1px solid var(--border-color);
}

.search-btn, .reset-btn {
  height: 40px;
  padding: 0 var(--space-xl);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.data-table {
  width: 100%;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.data-table :deep(.el-table__header) {
  background-color: var(--bg-secondary);
}

.data-table :deep(.el-table__header th) {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
  font-weight: 600;
  font-size: var(--text-sm);
  border-bottom: 1px solid var(--border-color);
  padding: var(--space-lg);
}

.data-table :deep(.el-table__body td) {
  padding: var(--space-lg);
  border-bottom: 1px solid var(--border-light);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.data-table :deep(.el-table__body tr:hover td) {
  background-color: var(--bg-secondary);
}

.data-table :deep(.el-table__body tr:last-child td) {
  border-bottom: none;
}

.status-indicator.active {
  background-color: rgba(5, 150, 105, 0.1);
  color: var(--success-color);
  border: 1px solid rgba(5, 150, 105, 0.2);
}

.status-indicator.inactive {
  background-color: rgba(220, 38, 38, 0.1);
  color: var(--danger-color);
  border: 1px solid rgba(220, 38, 38, 0.2);
}

.status-dot.active { 
  background-color: var(--success-color); 
}

.status-dot.inactive { 
  background-color: var(--danger-color); 
}

.action-buttons {
  display: flex;
  gap: var(--space-sm);
  flex-wrap: wrap;
}

.action-btn {
  height: 32px;
  padding: 0 var(--space-md);
  font-size: var(--text-xs);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  border-radius: var(--radius-md);
  transition: all 0.2s var(--ease-out);
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.pagination-wrapper {
  margin-top: var(--space-xl);
  padding-top: var(--space-xl);
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: center;
}

.pro-pagination :deep(.el-pagination__total),
.pro-pagination :deep(.el-pagination__sizes),
.pro-pagination :deep(.el-pagination__jump) {
  color: var(--text-secondary);
  font-weight: 500;
}

.pro-pagination :deep(.el-pager li) {
  color: var(--text-secondary);
  font-weight: 500;
  border: 1px solid transparent;
  transition: all 0.2s var(--ease-out);
}

.pro-pagination :deep(.el-pager li:hover) {
  background-color: var(--bg-secondary);
  border-color: var(--border-color);
}

.pro-pagination :deep(.el-pager li.is-active) {
  background-color: var(--accent-color);
  color: var(--text-inverse);
  border-color: var(--accent-color);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: var(--space-lg);
    align-items: flex-start;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
    gap: var(--space-md);
  }
  
  .search-actions {
    justify-content: stretch;
  }
  
  .search-btn, .reset-btn {
    flex: 1;
    justify-content: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .data-table :deep(.el-table__body td),
  .data-table :deep(.el-table__header th) {
    padding: var(--space-md);
  }
}

@media (max-width: 480px) {
  .action-buttons {
    flex-direction: column;
    width: 100%;
  }
  
  .action-btn {
    width: 100%;
    justify-content: center;
  }
  
  .pro-pagination {
    overflow-x: auto;
  }
}
</style>
