<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <div class="error-message">
        <h2>頁面不存在</h2>
        <p>抱歉，您訪問的頁面不存在或已被移除。</p>
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">
          <el-icon><House /></el-icon>
          回到首頁
        </el-button>
        <el-button @click="goBack">
          <el-icon><Back /></el-icon>
          返回上頁
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { House, Back } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.not-found-content {
  text-align: center;
  color: #fff;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.error-message h2 {
  font-size: 28px;
  margin-bottom: 12px;
  font-weight: 600;
}

.error-message p {
  font-size: 16px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .error-code {
    font-size: 80px;
  }
  
  .error-message h2 {
    font-size: 24px;
  }
  
  .error-message p {
    font-size: 14px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
