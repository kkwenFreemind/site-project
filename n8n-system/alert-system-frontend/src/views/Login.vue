<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-header">
        <h2>智能告警分發系統</h2>
        <p>請登入您的帳號</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="請輸入用戶名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="請輸入密碼"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">
            記住我
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="authStore.loading"
            @click="handleLogin"
            class="login-button"
          >
            {{ authStore.loading ? '登入中...' : '登入' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>還沒有帳號？</span>
        <el-link type="primary" @click="$router.push('/register')">
          立即註冊
        </el-link>
      </div>
    </div>

    <!-- 背景裝飾 -->
    <div class="login-bg">
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
const loginFormRef = ref()

// 登入表單數據
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表單驗證規則
const loginRules = {
  username: [
    { required: true, message: '請輸入用戶名', trigger: 'blur' },
    { min: 3, max: 50, message: '用戶名長度在 3 到 50 個字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '請輸入密碼', trigger: 'blur' },
    { min: 6, message: '密碼長度不能少於 6 個字符', trigger: 'blur' }
  ]
}

// 處理登入
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    console.log('Starting login process...')
    const result = await authStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    console.log('Login result:', result)
    console.log('Auth store token:', authStore.token)
    console.log('Auth store user:', authStore.user)

    if (result.success) {
      ElMessage.success('登入成功')
      
      console.log('Login successful, fetching profile...')
      // 確保用戶資料已載入
      await authStore.fetchProfile()
      
      console.log('Profile fetched, user data:', authStore.user)
      console.log('Is authenticated:', authStore.isAuthenticated)
      console.log('Navigating to dashboard...')
      
      // 使用replace而不是push來避免路由歷史問題
      await router.replace('/dashboard')
      
      console.log('Navigation completed')
    } else {
      ElMessage.error(result.message || '登入失敗')
    }
  } catch (error) {
    console.error('登入失敗:', error)
    ElMessage.error('登入失敗，請檢查網路連接')
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, 
    #f8fafc 0%, 
    #e2e8f0 25%, 
    #cbd5e1 50%, 
    #94a3b8 75%, 
    #64748b 100%);
  background-size: 400% 400%;
  animation: subtleGradient 20s ease infinite;
  overflow: hidden;
  padding: var(--space-xl);
}

@keyframes subtleGradient {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.login-form {
  width: 480px;
  max-width: 90vw;
  padding: var(--space-4xl) var(--space-3xl);
  background: var(--bg-elevated);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
  position: relative;
  z-index: 10;
  animation: slideInUp 0.6s var(--ease-out);
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(32px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: var(--space-4xl);
}

.login-header h2 {
  color: var(--text-primary);
  margin-bottom: var(--space-md);
  font-weight: 700;
  font-size: var(--text-3xl);
  letter-spacing: -0.025em;
}

.login-header p {
  color: var(--text-secondary);
  font-size: var(--text-lg);
  font-weight: 500;
}

.form-item {
  margin-bottom: var(--space-xl);
}

.form-item :deep(.el-input) {
  height: 56px;
}

.form-item :deep(.el-input__wrapper) {
  background: var(--bg-secondary);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-lg) var(--space-xl);
  font-size: var(--text-base);
  transition: all 0.25s var(--ease-out);
  box-shadow: var(--shadow-xs);
}

.form-item :deep(.el-input__wrapper:hover) {
  background: var(--bg-elevated);
  border-color: var(--border-medium);
  box-shadow: var(--shadow-sm);
}

.form-item :deep(.el-input__wrapper.is-focus) {
  background: var(--bg-elevated);
  border-color: var(--accent-color);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1), var(--shadow-sm);
}

.form-item :deep(.el-input__inner) {
  color: var(--text-primary);
  font-weight: 500;
  font-size: var(--text-base);
  height: 24px;
}

.form-item :deep(.el-input__inner::placeholder) {
  color: var(--text-tertiary);
  font-weight: 400;
}

.form-item :deep(.el-input__prefix-inner) {
  color: var(--text-tertiary);
  font-size: 18px;
}

.form-item :deep(.el-input__suffix-inner) {
  color: var(--text-tertiary);
}

.remember-me {
  display: flex;
  align-items: center;
  margin-bottom: var(--space-3xl);
}

.remember-me :deep(.el-checkbox) {
  color: var(--text-secondary);
  font-weight: 500;
}

.remember-me :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--accent-color);
  border-color: var(--accent-color);
}

.login-button {
  width: 100%;
  height: 56px;
  background: linear-gradient(135deg, var(--accent-color) 0%, var(--accent-dark) 100%);
  border: none;
  border-radius: var(--radius-lg);
  color: var(--text-inverse);
  font-size: var(--text-base);
  font-weight: 600;
  letter-spacing: 0.025em;
  transition: all 0.25s var(--ease-out);
  box-shadow: var(--shadow-base);
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.1) 0%, 
    rgba(255, 255, 255, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.25s var(--ease-out);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.login-button:hover::before {
  opacity: 1;
}

.login-button:active {
  transform: translateY(0);
  box-shadow: var(--shadow-base);
}

.login-button.is-loading {
  transform: none;
  box-shadow: var(--shadow-base);
}

.login-footer {
  text-align: center;
  margin-top: var(--space-3xl);
  color: var(--text-secondary);
  font-size: var(--text-base);
}

.login-footer :deep(.el-link) {
  color: var(--accent-color);
  font-weight: 600;
  text-decoration: none;
  margin-left: var(--space-sm);
  transition: all 0.2s var(--ease-out);
}

.login-footer :deep(.el-link:hover) {
  color: var(--accent-dark);
  text-decoration: underline;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  overflow: hidden;
}

.bg-shapes {
  position: relative;
  height: 100%;
  width: 100%;
}

.shape {
  position: absolute;
  border-radius: var(--radius-full);
  filter: blur(120px);
  opacity: 0.15;
  animation: gentleFloat 25s ease-in-out infinite;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(45deg, var(--accent-color), var(--accent-light));
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(45deg, var(--primary-color), var(--primary-light));
  top: 60%;
  right: 10%;
  animation-delay: -8s;
}

.shape-3 {
  width: 500px;
  height: 500px;
  background: linear-gradient(45deg, var(--success-color), #10b981);
  bottom: 10%;
  left: 50%;
  transform: translateX(-50%);
  animation-delay: -16s;
}

@keyframes gentleFloat {
  0%, 100% {
    transform: translateY(0px) translateX(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-20px) translateX(15px) rotate(60deg);
  }
  66% {
    transform: translateY(15px) translateX(-15px) rotate(120deg);
  }
}

/* 響應式設計 */
@media (max-width: 480px) {
  .login-container {
    padding: var(--space-lg);
  }
  
  .login-form {
    width: 100%;
    padding: var(--space-3xl) var(--space-xl);
    border-radius: var(--radius-xl);
  }
  
  .login-header h2 {
    font-size: var(--text-2xl);
  }
  
  .login-header p {
    font-size: var(--text-base);
  }
  
  .login-button {
    height: 52px;
    font-size: var(--text-sm);
  }
  
  .form-item :deep(.el-input) {
    height: 52px;
  }
  
  .form-item :deep(.el-input__wrapper) {
    padding: var(--space-md) var(--space-lg);
  }
}

/* 深色模式適配 */
@media (prefers-color-scheme: dark) {
  .login-container {
    background: linear-gradient(135deg, 
      var(--bg-primary) 0%, 
      var(--bg-secondary) 25%, 
      var(--bg-tertiary) 50%, 
      var(--primary-color) 75%, 
      var(--primary-dark) 100%);
  }
  
  .login-form {
    background: var(--bg-elevated);
    border-color: var(--border-color);
    box-shadow: var(--shadow-xl);
  }
  
  .shape {
    opacity: 0.08;
  }
}
</style>
