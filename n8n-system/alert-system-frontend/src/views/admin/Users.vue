<template>
  <div class="users-management">
    <div class="page-header">
      <h1>用戶管理</h1>
      <p>管理系統中的所有用戶帳戶</p>
    </div>

    <!-- 操作工具欄 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
          v-model="searchQuery"
          placeholder="搜尋用戶名或郵箱"
          @input="handleSearch"
          clearable
          style="width: 300px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="action-section">
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          新增用戶
        </el-button>
        <el-button @click="loadUsers">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 用戶列表 -->
    <el-card class="table-card">
      <el-table
        :data="filteredUsers"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="username" label="用戶名" width="150" />
        
        <el-table-column prop="email" label="郵箱" width="200" />
        
        <el-table-column prop="fullName" label="姓名" width="150" />
        
        <el-table-column label="角色" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roles"
              :key="role"
              :type="role === 'ADMIN' ? 'danger' : 'primary'"
              size="small"
              class="role-tag"
            >
              {{ role }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="狀態" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'ACTIVE' ? 'success' : 'danger'"
              size="small"
            >
              {{ row.status === 'ACTIVE' ? '活躍' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="創建時間" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="editUser(row)"
            >
              編輯
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="manageRoles(row)"
            >
              角色
            </el-button>
            <el-button
              v-if="row.username !== authStore.user?.username"
              type="danger"
              size="small"
              @click="deleteUser(row)"
            >
              刪除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增用戶對話框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="新增用戶"
      width="500px"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="80px"
      >
        <el-form-item label="用戶名" prop="username">
          <el-input v-model="createForm.username" placeholder="請輸入用戶名" />
        </el-form-item>
        
        <el-form-item label="郵箱" prop="email">
          <el-input v-model="createForm.email" placeholder="請輸入郵箱" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="createForm.fullName" placeholder="請輸入姓名" />
        </el-form-item>
        
        <el-form-item label="密碼" prop="password">
          <el-input
            v-model="createForm.password"
            type="password"
            placeholder="請輸入密碼"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="creating"
          @click="handleCreateUser"
        >
          確定
        </el-button>
      </template>
    </el-dialog>

    <!-- 編輯用戶對話框 -->
    <el-dialog
      v-model="showEditDialog"
      title="編輯用戶"
      width="500px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="用戶名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        
        <el-form-item label="郵箱" prop="email">
          <el-input v-model="editForm.email" placeholder="請輸入郵箱" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="editForm.fullName" placeholder="請輸入姓名" />
        </el-form-item>
        
        <el-form-item label="狀態">
          <el-select v-model="editForm.status" placeholder="請選擇狀態">
            <el-option label="活躍" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="updating"
          @click="handleUpdateUser"
        >
          確定
        </el-button>
      </template>
    </el-dialog>

    <!-- 角色管理對話框 -->
    <el-dialog
      v-model="showRolesDialog"
      title="管理用戶角色"
      width="400px"
    >
      <div v-if="selectedUser">
        <p class="user-info">
          <strong>用戶：</strong>{{ selectedUser.username }} ({{ selectedUser.fullName }})
        </p>
        
        <el-checkbox-group v-model="selectedRoles" class="roles-group">
          <el-checkbox
            v-for="role in availableRoles"
            :key="role.id"
            :label="role.name"
            :value="role.name"
          >
            {{ role.name }} - {{ role.description }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      
      <template #footer>
        <el-button @click="showRolesDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="updatingRoles"
          @click="handleUpdateRoles"
        >
          確定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { adminAPI } from '@/api/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Plus,
  Refresh
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const authStore = useAuthStore()

// 響應式數據
const users = ref([])
const availableRoles = ref([])
const searchQuery = ref('')
const loading = ref(false)
const creating = ref(false)
const updating = ref(false)
const updatingRoles = ref(false)

// 對話框顯示狀態
const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const showRolesDialog = ref(false)

// 選中的用戶和角色
const selectedUser = ref(null)
const selectedRoles = ref([])

// 表單數據
const createForm = reactive({
  username: '',
  email: '',
  fullName: '',
  password: ''
})

const editForm = reactive({
  id: null,
  username: '',
  email: '',
  fullName: '',
  status: 'ACTIVE'
})

// 表單引用
const createFormRef = ref()
const editFormRef = ref()

// 過濾用戶列表
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(user => 
    user.username.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query) ||
    user.fullName.toLowerCase().includes(query)
  )
})

// 表單驗證規則
const createRules = {
  username: [
    { required: true, message: '請輸入用戶名', trigger: 'blur' },
    { min: 3, max: 50, message: '用戶名長度在 3 到 50 個字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '請輸入郵箱', trigger: 'blur' },
    { type: 'email', message: '請輸入正確的郵箱格式', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '請輸入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '請輸入密碼', trigger: 'blur' },
    { min: 6, message: '密碼長度不能少於6個字符', trigger: 'blur' }
  ]
}

const editRules = {
  email: [
    { required: true, message: '請輸入郵箱', trigger: 'blur' },
    { type: 'email', message: '請輸入正確的郵箱格式', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '請輸入姓名', trigger: 'blur' }
  ]
}

// 載入用戶列表
const loadUsers = async () => {
  try {
    loading.value = true
    const response = await adminAPI.getUsers()
    
    if (response.code === 200) {
      users.value = response.data
    }
  } catch (error) {
    console.error('載入用戶列表失敗:', error)
    ElMessage.error('載入用戶列表失敗')
  } finally {
    loading.value = false
  }
}

// 載入角色列表
const loadRoles = async () => {
  try {
    const response = await adminAPI.getRoles()
    
    if (response.code === 200) {
      availableRoles.value = response.data
    }
  } catch (error) {
    console.error('載入角色列表失敗:', error)
    ElMessage.error('載入角色列表失敗')
  }
}

// 搜尋處理
const handleSearch = () => {
  // 在 computed 中已經處理過濾，這裡不需要額外操作
}

// 創建用戶
const handleCreateUser = async () => {
  if (!createFormRef.value) return
  
  try {
    const valid = await createFormRef.value.validate()
    if (!valid) return

    creating.value = true
    
    const response = await adminAPI.createUser(createForm)
    
    if (response.code === 200) {
      ElMessage.success('用戶創建成功')
      showCreateDialog.value = false
      resetCreateForm()
      loadUsers()
    }
  } catch (error) {
    console.error('創建用戶失敗:', error)
    ElMessage.error('創建用戶失敗')
  } finally {
    creating.value = false
  }
}

// 編輯用戶
const editUser = (user) => {
  editForm.id = user.id
  editForm.username = user.username
  editForm.email = user.email
  editForm.fullName = user.fullName
  editForm.status = user.status
  showEditDialog.value = true
}

// 更新用戶
const handleUpdateUser = async () => {
  if (!editFormRef.value) return
  
  try {
    const valid = await editFormRef.value.validate()
    if (!valid) return

    updating.value = true
    
    const response = await adminAPI.updateUser(editForm.id, {
      email: editForm.email,
      fullName: editForm.fullName,
      status: editForm.status
    })
    
    if (response.code === 200) {
      ElMessage.success('用戶更新成功')
      showEditDialog.value = false
      loadUsers()
    }
  } catch (error) {
    console.error('更新用戶失敗:', error)
    ElMessage.error('更新用戶失敗')
  } finally {
    updating.value = false
  }
}

// 管理角色
const manageRoles = (user) => {
  selectedUser.value = user
  selectedRoles.value = [...user.roles]
  showRolesDialog.value = true
}

// 更新用戶角色
const handleUpdateRoles = async () => {
  if (!selectedUser.value) return
  
  try {
    updatingRoles.value = true
    
    const roleIds = availableRoles.value
      .filter(role => selectedRoles.value.includes(role.name))
      .map(role => role.id)
    
    const response = await adminAPI.updateUserRoles(selectedUser.value.id, roleIds)
    
    if (response.code === 200) {
      ElMessage.success('角色更新成功')
      showRolesDialog.value = false
      loadUsers()
    }
  } catch (error) {
    console.error('更新角色失敗:', error)
    ElMessage.error('更新角色失敗')
  } finally {
    updatingRoles.value = false
  }
}

// 刪除用戶
const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `確定要刪除用戶 "${user.username}" 嗎？此操作不可恢復。`,
      '警告',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const response = await adminAPI.deleteUser(user.id)
    
    if (response.code === 200) {
      ElMessage.success('用戶刪除成功')
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('刪除用戶失敗:', error)
      ElMessage.error('刪除用戶失敗')
    }
  }
}

// 重置創建表單
const resetCreateForm = () => {
  createForm.username = ''
  createForm.email = ''
  createForm.fullName = ''
  createForm.password = ''
  if (createFormRef.value) {
    createFormRef.value.clearValidate()
  }
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<style scoped>
.users-management {
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

.action-section {
  display: flex;
  gap: 12px;
}

.table-card {
  border-radius: 8px;
}

.role-tag {
  margin-right: 4px;
}

.user-info {
  margin-bottom: 20px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.roles-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .search-section,
  .action-section {
    width: 100%;
  }
  
  .action-section {
    justify-content: center;
  }
}
</style>
