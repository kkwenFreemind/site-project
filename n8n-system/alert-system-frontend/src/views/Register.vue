<template>
  <div class="register-container">
    <div class="register-form">
      <div class="register-header">
        <h2>創建新帳號</h2>
        <p>填寫以下信息完成註冊</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="請輸入用戶名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="請輸入郵箱"
            size="large"
            prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item prop="fullName">
          <el-input
            v-model="registerForm.fullName"
            placeholder="請輸入姓名"
            size="large"
            prefix-icon="UserFilled"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="請輸入密碼"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="請確認密碼"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="registerForm.agreed">
            我已閱讀並同意
            <el-link type="primary">《服務條款》</el-link>
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="authStore.loading"
            @click="handleRegister"
            class="register-button"
          >
            {{ authStore.loading ? '註冊中...' : '註冊' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有帳號？</span>
        <el-link type="primary" @click="$router.push('/login')">
          立即登入
        </el-link>
      </div>
    </div>

    <!-- 背景裝飾 -->
    <div class="register-bg">
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const registerFormRef = ref()

// 註冊表單數據
const registerForm = reactive({
  username: '',
  email: '',
  fullName: '',
  password: '',
  confirmPassword: '',
  agreed: false
})

// 自定義驗證規則
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('請輸入密碼'))
  } else if (value.length < 6) {
    callback(new Error('密碼長度不能少於6個字符'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('請再次輸入密碼'))
  } else if (value !== registerForm.password) {
    callback(new Error('兩次輸入密碼不一致'))
  } else {
    callback()
  }
}

const validateAgreed = (rule, value, callback) => {
  if (!value) {
    callback(new Error('請先同意服務條款'))
  } else {
    callback()
  }
}

// 表單驗證規則
const registerRules = {
  username: [
    { required: true, message: '請輸入用戶名', trigger: 'blur' },
    { min: 3, max: 50, message: '用戶名長度在 3 到 50 個字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用戶名只能包含字母、數字和下劃線', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '請輸入郵箱', trigger: 'blur' },
    { type: 'email', message: '請輸入正確的郵箱格式', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '請輸入姓名', trigger: 'blur' },
    { min: 2, max: 100, message: '姓名長度在 2 到 100 個字符', trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreed: [
    { validator: validateAgreed, trigger: 'change' }
  ]
}

// 處理註冊
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    const result = await authStore.register({
      username: registerForm.username,
      email: registerForm.email,
      fullName: registerForm.fullName,
      password: registerForm.password
    })

    if (result.success) {
      ElMessage.success('註冊成功')
      router.push('/')
    } else {
      ElMessage.error(result.message || '註冊失敗')
    }
  } catch (error) {
    console.error('註冊失敗:', error)
    ElMessage.error('註冊失敗，請檢查網路連接')
  }
}
</script>

<style scoped>
.register-container {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.register-form {
  width: 450px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  z-index: 10;
  max-height: 90vh;
  overflow-y: auto;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #303133;
  margin-bottom: 8px;
  font-weight: 600;
}

.register-header p {
  color: #909399;
  font-size: 14px;
}

.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #909399;
}

.register-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.bg-shapes {
  position: relative;
  height: 100%;
  width: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  top: 80%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 響應式設計 */
@media (max-width: 480px) {
  .register-form {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>
