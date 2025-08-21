import { defineStore } from 'pinia'
import { authAPI } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    user: null,
    loading: false,
  }),

  getters: {
    isAuthenticated: (state) => {
      return !!(state.token && state.user)
    },
    
    isAdmin: (state) => {
      // 處理兩種角色格式：字符串數組或對象數組
      if (Array.isArray(state.user?.roles)) {
        // 如果是對象數組（來自profile API）
        if (state.user.roles.length > 0 && typeof state.user.roles[0] === 'object') {
          return state.user.roles.some(role => role.name === 'ADMIN')
        }
        // 如果是字符串數組（來自login API）
        return state.user.roles.includes('ADMIN')
      }
      // 如果是Set或其他格式
      return Array.from(state.user?.roles || []).includes('ADMIN')
    },
    
    userRoles: (state) => {
      if (Array.isArray(state.user?.roles)) {
        // 如果是對象數組（來自profile API）
        if (state.user.roles.length > 0 && typeof state.user.roles[0] === 'object') {
          return state.user.roles.map(role => role.name)
        }
        // 如果是字符串數組（來自login API）
        return state.user.roles
      }
      // 如果是Set或其他格式
      return Array.from(state.user?.roles || [])
    },
  },

  actions: {
    // 設置認證信息
    setAuth(authData) {
      console.log('setAuth called with:', authData)
      this.token = authData.token
      this.refreshToken = authData.refreshToken
      this.user = authData.userInfo

      // 保存到localStorage
      localStorage.setItem('token', authData.token)
      console.log('Token saved to localStorage:', authData.token)
      if (authData.refreshToken) {
        localStorage.setItem('refreshToken', authData.refreshToken)
      }
    },

    // 清除認證信息
    clearAuth() {
      this.token = null
      this.refreshToken = null
      this.user = null

      // 清除localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
    },

    // 用戶登入
    async login(credentials) {
      try {
        this.loading = true
        const response = await authAPI.login(credentials)
        
        // 響應攔截器已經處理了 ApiResponse 格式，直接返回 data 部分
        // response 就是 { token, refreshToken, userInfo, ... }
        this.setAuth(response)
        return { success: true }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || '登入失敗' 
        }
      } finally {
        this.loading = false
      }
    },

    // 用戶註冊
    async register(userData) {
      try {
        this.loading = true
        const response = await authAPI.register(userData)
        
        // 響應攔截器已經處理了 ApiResponse 格式，直接返回 data 部分
        this.setAuth(response)
        return { success: true }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || '註冊失敗' 
        }
      } finally {
        this.loading = false
      }
    },

    // 用戶登出
    async logout() {
      try {
        if (this.token) {
          await authAPI.logout()
        }
      } catch (error) {
        console.error('登出API調用失敗:', error)
      } finally {
        this.clearAuth()
        // 動態導入 router 以避免循環依賴
        const { default: router } = await import('@/router')
        router.push('/login')
      }
    },

    // 獲取用戶資料
    async fetchProfile() {
      try {
        const response = await authAPI.getProfile()
        // 響應攔截器已經處理了 ApiResponse 格式，直接返回 data 部分
        this.user = response
        return true
      } catch (error) {
        console.error('獲取用戶資料失敗:', error)
        this.clearAuth()
        return false
      }
    },

    // 檢查認證狀態
    async checkAuth() {
      if (!this.token) {
        return false
      }

      try {
        const success = await this.fetchProfile()
        if (!success) {
          this.clearAuth()
        }
        return success
      } catch (error) {
        this.clearAuth()
        return false
      }
    },

    // 更新用戶資料
    async updateProfile(userData) {
      try {
        const response = await authAPI.updateProfile(userData)
        // 響應攔截器已經處理了 ApiResponse 格式，直接返回 data 部分
        this.user = { ...this.user, ...response }
        return { success: true }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || '更新失敗' 
        }
      }
    },
  },
})
