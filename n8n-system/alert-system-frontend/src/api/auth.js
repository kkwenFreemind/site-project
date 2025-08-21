import api from '@/utils/request'

export const authAPI = {
  // 用戶登入
  login(credentials) {
    return api.post('/auth/login', credentials)
  },

  // 用戶註冊
  register(userData) {
    return api.post('/auth/register', userData)
  },

  // 用戶登出
  logout() {
    return api.post('/auth/logout')
  },

  // 獲取用戶資料
  getProfile() {
    return api.get('/auth/profile')
  },

  // 更新用戶資料
  updateProfile(userData) {
    return api.put('/auth/profile', userData)
  },

  // 刷新Token
  refreshToken(refreshToken) {
    return api.post('/auth/refresh', { refreshToken })
  }
}

export const adminAPI = {
  // 獲取所有用戶
  getUsers(params = {}) {
    return api.get('/admin/users', { params })
  },

  // 獲取用戶詳情
  getUserById(id) {
    return api.get(`/admin/users/${id}`)
  },

  // 創建用戶
  createUser(userData) {
    return api.post('/admin/users', userData)
  },

  // 更新用戶
  updateUser(id, userData) {
    return api.put(`/admin/users/${id}`, userData)
  },

  // 刪除用戶
  deleteUser(id) {
    return api.delete(`/admin/users/${id}`)
  },

  // 獲取所有角色
  getRoles() {
    return api.get('/admin/roles')
  },

  // 更新用戶角色
  updateUserRoles(userId, roleIds) {
    return api.put(`/admin/users/${userId}/roles`, { roleIds })
  },

  // 獲取登入記錄
  getLoginLogs(params = {}) {
    return api.get('/admin/login-logs', { params })
  }
}
