<template>
  <div class="profile">
    <div class="profile-header">
      <h1>個人資料</h1>
      <p>管理您的帳戶信息和設置</p>
    </div>

    <el-row :gutter="20">
      <!-- 用戶信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-avatar">
            <el-avatar :size="80" :src="userAvatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <div class="avatar-info">
              <h3>{{ authStore.user?.fullName || authStore.user?.username }}</h3>
              <p class="user-email">{{ authStore.user?.email }}</p>
              <div class="user-roles">
                <el-tag 
                  v-for="role in authStore.userRoles" 
                  :key="role"
                  :type="role === 'ADMIN' ? 'danger' : 'primary'"
                  size="small"
                  class="role-tag"
                >
                  {{ role }}
                </el-tag>
              </div>
            </div>
          </div>
          
          <el-divider />
          
          <div class="profile-stats">
            <div class="stat-item">
              <span class="stat-label">帳戶狀態</span>
              <el-tag type="success" size="small">活躍</el-tag>
            </div>
            <div class="stat-item">
              <span class="stat-label">註冊時間</span>
              <span class="stat-value">{{ formatDate(authStore.user?.createdAt) }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">最後更新</span>
              <span class="stat-value">{{ formatDate(authStore.user?.updatedAt) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 編輯表單 -->
      <el-col :span="16">
        <el-card class="profile-form-card" header="編輯個人資料">
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
            label-position="left"
          >
            <el-form-item label="用戶名" prop="username">
              <el-input
                v-model="profileForm.username"
                disabled
                placeholder="用戶名不可修改"
              />
            </el-form-item>

            <el-form-item label="郵箱" prop="email">
              <el-input
                v-model="profileForm.email"
                placeholder="請輸入郵箱"
              />
            </el-form-item>

            <el-form-item label="姓名" prop="fullName">
              <el-input
                v-model="profileForm.fullName"
                placeholder="請輸入姓名"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="updating"
                @click="handleUpdateProfile"
              >
                {{ updating ? '更新中...' : '更新資料' }}
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 密碼修改 -->
        <el-card class="profile-form-card" header="修改密碼" style="margin-top: 20px;">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            label-position="left"
          >
            <el-form-item label="當前密碼" prop="currentPassword">
              <el-input
                v-model="passwordForm.currentPassword"
                type="password"
                placeholder="請輸入當前密碼"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密碼" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="請輸入新密碼"
                show-password
              />
            </el-form-item>

            <el-form-item label="確認密碼" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="請確認新密碼"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="changingPassword"
                @click="handleChangePassword"
              >
                {{ changingPassword ? '修改中...' : '修改密碼' }}
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const authStore = useAuthStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const updating = ref(false)
const changingPassword = ref(false)

// 用戶頭像
const userAvatar = computed(() => {
  return authStore.user?.avatar || ''
})

// 個人資料表單
const profileForm = reactive({
  username: '',
  email: '',
  fullName: ''
})

// 密碼修改表單
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 個人資料驗證規則
const profileRules = {
  email: [
    { required: true, message: '請輸入郵箱', trigger: 'blur' },
    { type: 'email', message: '請輸入正確的郵箱格式', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '請輸入姓名', trigger: 'blur' },
    { min: 2, max: 100, message: '姓名長度在 2 到 100 個字符', trigger: 'blur' }
  ]
}

// 密碼驗證規則
const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('請輸入新密碼'))
  } else if (value.length < 6) {
    callback(new Error('密碼長度不能少於6個字符'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('請確認新密碼'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('兩次輸入密碼不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '請輸入當前密碼', trigger: 'blur' }
  ],
  newPassword: [
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 初始化表單數據
const initForm = () => {
  if (authStore.user) {
    profileForm.username = authStore.user.username
    profileForm.email = authStore.user.email
    profileForm.fullName = authStore.user.fullName
  }
}

// 重置個人資料表單
const resetForm = () => {
  initForm()
}

// 重置密碼表單
const resetPasswordForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 更新個人資料
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  
  try {
    const valid = await profileFormRef.value.validate()
    if (!valid) return

    updating.value = true
    
    const result = await authStore.updateProfile({
      email: profileForm.email,
      fullName: profileForm.fullName
    })

    if (result.success) {
      ElMessage.success('個人資料更新成功')
    } else {
      ElMessage.error(result.message || '更新失敗')
    }
  } catch (error) {
    console.error('更新個人資料失敗:', error)
    ElMessage.error('更新失敗，請檢查網路連接')
  } finally {
    updating.value = false
  }
}

// 修改密碼
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    const valid = await passwordFormRef.value.validate()
    if (!valid) return

    changingPassword.value = true
    
    // 這裡應該調用修改密碼的API
    // const result = await authAPI.changePassword({
    //   currentPassword: passwordForm.currentPassword,
    //   newPassword: passwordForm.newPassword
    // })

    // 模擬API調用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('密碼修改成功')
    resetPasswordForm()
  } catch (error) {
    console.error('修改密碼失敗:', error)
    ElMessage.error('修改失敗，請檢查網路連接')
  } finally {
    changingPassword.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '未知'
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.profile {
  padding: 0;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.profile-header p {
  color: #909399;
  font-size: 14px;
}

.profile-card {
  border-radius: 12px;
  height: fit-content;
}

.profile-avatar {
  text-align: center;
  padding: 20px 0;
}

.avatar-info {
  margin-top: 16px;
}

.avatar-info h3 {
  color: #303133;
  margin-bottom: 4px;
  font-size: 18px;
}

.user-email {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.user-roles {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.role-tag {
  margin: 0;
}

.profile-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.stat-value {
  color: #303133;
  font-size: 14px;
}

.profile-form-card {
  border-radius: 12px;
}

/* 響應式設計 */
@media (max-width: 1200px) {
  .el-col {
    margin-bottom: 20px;
  }
}

@media (max-width: 768px) {
  .profile-form-card .el-form {
    label-width: 80px !important;
  }
}
</style>
