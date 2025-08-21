import api from '@/utils/request'

// 用戶管理API
export const userAPI = {
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

  // 更新用戶角色
  updateUserRoles(userId, roleIds) {
    return api.put(`/admin/users/${userId}/roles`, { roleIds })
  },

  // 重置用戶密碼
  resetPassword(userId, newPassword) {
    return api.put(`/admin/users/${userId}/password`, { newPassword })
  },

  // 啟用/禁用用戶
  updateUserStatus(userId, status) {
    return api.put(`/admin/users/${userId}/status`, { status })
  }
}

// 角色管理API
export const roleAPI = {
  // 獲取所有角色
  getRoles(params = {}) {
    return api.get('/admin/roles', { params })
  },

  // 獲取角色詳情
  getRoleById(id) {
    return api.get(`/admin/roles/${id}`)
  },

  // 創建角色
  createRole(roleData) {
    return api.post('/admin/roles', roleData)
  },

  // 更新角色
  updateRole(id, roleData) {
    return api.put(`/admin/roles/${id}`, roleData)
  },

  // 刪除角色
  deleteRole(id) {
    return api.delete(`/admin/roles/${id}`)
  },

  // 獲取角色的選單權限
  getRoleMenus(roleId) {
    return api.get(`/admin/roles/${roleId}/menus`)
  },

  // 更新角色的選單權限
  updateRoleMenus(roleId, menuIds) {
    return api.put(`/admin/roles/${roleId}/menus`, { menuIds })
  }
}

// 選單管理API
export const menuAPI = {
  // 獲取所有選單
  getMenus(params = {}) {
    return api.get('/menus', { params })
  },

  // 獲取選單樹結構
  getMenuTree() {
    return api.get('/menus/tree')
  },

  // 獲取用戶選單樹
  getUserMenuTree() {
    return api.get('/menus/user/tree')
  },

  // 獲取選單詳情
  getMenuById(id) {
    return api.get(`/menus/${id}`)
  },

  // 創建選單
  createMenu(menuData) {
    return api.post('/menus', menuData)
  },

  // 更新選單
  updateMenu(id, menuData) {
    return api.put(`/menus/${id}`, menuData)
  },

  // 刪除選單
  deleteMenu(id) {
    return api.delete(`/menus/${id}`)
  },

  // 批量更新選單順序
  updateMenuOrder(menuList) {
    return api.put('/menus/order', { menuList })
  },

  // 獲取選單的角色分配
  getMenuRoles(menuId) {
    return api.get(`/menus/${menuId}/roles`)
  },

  // 更新選單的角色分配
  updateMenuRoles(menuId, roleIds) {
    return api.put(`/menus/${menuId}/roles`, { roleIds })
  }
}

// 通用系統API
export const systemAPI = {
  // 獲取系統統計信息
  getStats() {
    return api.get('/admin/stats')
  },

  // 獲取登入記錄
  getLoginLogs(params = {}) {
    return api.get('/admin/login-logs', { params })
  }
}
