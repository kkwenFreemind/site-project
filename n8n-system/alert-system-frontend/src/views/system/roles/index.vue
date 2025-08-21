<template>
  <div class="roles-management">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        新增角色
      </el-button>
    </div>

    <!-- 搜索區域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="角色名稱">
          <el-input v-model="searchForm.name" placeholder="請輸入角色名稱" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRoles">查詢</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 角色列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="roles"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名稱" width="150" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column label="用戶數量" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ row.userCount || 0 }} 人</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="選單權限" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="menu in (row.menus || []).slice(0, 3)"
              :key="menu.id"
              size="small"
              style="margin-right: 5px;"
            >
              {{ menu.name }}
            </el-tag>
            <el-tag v-if="(row.menus || []).length > 3" size="small" type="info">
              +{{ (row.menus || []).length - 3 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="創建時間" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editRole(row)">
              編輯
            </el-button>
            <el-button type="info" size="small" @click="assignMenus(row)">
              分配權限
            </el-button>
            <el-button type="warning" size="small" @click="viewUsers(row)">
              查看用戶
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteRole(row)"
              :disabled="['ADMIN', 'USER'].includes(row.name)"
            >
              刪除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分頁 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadRoles"
          @current-change="loadRoles"
        />
      </div>
    </div>

    <!-- 新增/編輯角色對話框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingRole ? '編輯角色' : '新增角色'"
      width="600px"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名稱" prop="name">
          <el-input v-model="roleForm.name" :disabled="!!editingRole && ['ADMIN', 'USER'].includes(editingRole.name)" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="請輸入角色描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRole" :loading="saving">
          {{ editingRole ? '更新' : '創建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配權限對話框 -->
    <el-dialog v-model="showMenuDialog" title="分配選單權限" width="800px">
      <div class="menu-assignment">
        <div class="role-info">
          <h4>角色：{{ currentRole?.name }}</h4>
          <p>{{ currentRole?.description }}</p>
        </div>
        
        <div class="menu-tree">
          <el-tree
            ref="menuTreeRef"
            :data="menuTree"
            :props="treeProps"
            show-checkbox
            node-key="id"
            :default-checked-keys="selectedMenus"
            :check-strictly="false"
          >
            <template #default="{ node, data }">
              <span class="menu-node">
                <el-icon v-if="data.type === 'DIRECTORY'">
                  <Folder />
                </el-icon>
                <el-icon v-else-if="data.type === 'MENU'">
                  <Document />
                </el-icon>
                <el-icon v-else>
                  <Operation />
                </el-icon>
                <span style="margin-left: 5px;">{{ data.name }}</span>
                <el-tag v-if="data.permission" size="small" type="info" style="margin-left: 10px;">
                  {{ data.permission }}
                </el-tag>
              </span>
            </template>
          </el-tree>
        </div>
      </div>
      <template #footer>
        <el-button @click="showMenuDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRoleMenus" :loading="saving">
          保存權限
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看用戶對話框 -->
    <el-dialog v-model="showUsersDialog" title="角色用戶列表" width="700px">
      <div class="role-users">
        <h4>角色：{{ currentRole?.name }}</h4>
        <el-table :data="roleUsers" stripe>
          <el-table-column prop="username" label="用戶名" width="120" />
          <el-table-column prop="email" label="郵箱" width="200" />
          <el-table-column prop="fullName" label="姓名" width="120" />
          <el-table-column label="狀態" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
                {{ row.status === 'ACTIVE' ? '啟用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="創建時間">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, Document, Operation } from '@element-plus/icons-vue'
import { roleAPI, menuAPI, userAPI } from '@/api/system'
import { formatDate } from '@/utils/date'

// 響應式數據
const loading = ref(false)
const saving = ref(false)
const roles = ref([])
const menuTree = ref([])
const roleUsers = ref([])
const showCreateDialog = ref(false)
const showMenuDialog = ref(false)
const showUsersDialog = ref(false)
const editingRole = ref(null)
const currentRole = ref(null)
const selectedMenus = ref([])

// 搜索表單
const searchForm = reactive({
  name: ''
})

// 分頁數據
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 角色表單
const roleForm = reactive({
  name: '',
  description: ''
})

// 表單驗證規則
const roleRules = {
  name: [
    { required: true, message: '請輸入角色名稱', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名稱長度在 2 到 20 個字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '請輸入角色描述', trigger: 'blur' }
  ]
}

// 樹形組件props
const treeProps = {
  children: 'children',
  label: 'name'
}

// 表單引用
const roleFormRef = ref()
const menuTreeRef = ref()

// 加載角色列表
const loadRoles = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      ...searchForm
    }
    
    const response = await roleAPI.getRoles(params)
    if (response.data.code === 200) {
      roles.value = response.data.data.content || response.data.data
      pagination.total = response.data.data.totalElements || response.data.data.length
    }
  } catch (error) {
    ElMessage.error('加載角色列表失敗：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加載選單樹
const loadMenuTree = async () => {
  try {
    const response = await menuAPI.getMenuTree()
    if (response.data.code === 200) {
      menuTree.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('加載選單樹失敗：' + error.message)
  }
}

// 搜索角色
const searchRoles = () => {
  pagination.page = 1
  loadRoles()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    name: ''
  })
  searchRoles()
}

// 編輯角色
const editRole = (role) => {
  editingRole.value = role
  Object.assign(roleForm, {
    name: role.name,
    description: role.description
  })
  showCreateDialog.value = true
}

// 保存角色
const saveRole = async () => {
  try {
    await roleFormRef.value.validate()
    saving.value = true
    
    let response
    if (editingRole.value) {
      response = await roleAPI.updateRole(editingRole.value.id, roleForm)
    } else {
      response = await roleAPI.createRole(roleForm)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(editingRole.value ? '更新成功' : '創建成功')
      showCreateDialog.value = false
      resetRoleForm()
      loadRoles()
    }
  } catch (error) {
    ElMessage.error('保存失敗：' + error.message)
  } finally {
    saving.value = false
  }
}

// 重置角色表單
const resetRoleForm = () => {
  editingRole.value = null
  Object.assign(roleForm, {
    name: '',
    description: ''
  })
}

// 分配選單權限
const assignMenus = async (role) => {
  try {
    currentRole.value = role
    
    // 加載角色的選單權限
    const response = await roleAPI.getRoleMenus(role.id)
    if (response.data.code === 200) {
      selectedMenus.value = response.data.data.map(menu => menu.id)
    }
    
    // 如果還沒有加載選單樹，則加載
    if (menuTree.value.length === 0) {
      await loadMenuTree()
    }
    
    showMenuDialog.value = true
    
    // 等待對話框渲染完成後設置選中狀態
    await nextTick()
    if (menuTreeRef.value) {
      menuTreeRef.value.setCheckedKeys(selectedMenus.value)
    }
  } catch (error) {
    ElMessage.error('加載角色權限失敗：' + error.message)
  }
}

// 保存角色選單權限
const saveRoleMenus = async () => {
  try {
    saving.value = true
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const allSelectedKeys = [...checkedKeys, ...halfCheckedKeys]
    
    const response = await roleAPI.updateRoleMenus(currentRole.value.id, allSelectedKeys)
    if (response.data.code === 200) {
      ElMessage.success('權限分配成功')
      showMenuDialog.value = false
      loadRoles()
    }
  } catch (error) {
    ElMessage.error('權限分配失敗：' + error.message)
  } finally {
    saving.value = false
  }
}

// 查看角色用戶
const viewUsers = async (role) => {
  try {
    currentRole.value = role
    
    // 獲取該角色的所有用戶
    const response = await userAPI.getUsers({ roleId: role.id })
    if (response.data.code === 200) {
      roleUsers.value = response.data.data.content || response.data.data
    }
    
    showUsersDialog.value = true
  } catch (error) {
    ElMessage.error('加載角色用戶失敗：' + error.message)
  }
}

// 刪除角色
const deleteRole = async (role) => {
  try {
    await ElMessageBox.confirm(
      `確定要刪除角色 "${role.name}" 嗎？此操作不可逆！`,
      '確認刪除',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await roleAPI.deleteRole(role.id)
    if (response.data.code === 200) {
      ElMessage.success('刪除成功')
      loadRoles()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('刪除失敗：' + error.message)
    }
  }
}

// 組件掛載
onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.roles-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-section {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.table-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pagination-section {
  margin-top: 20px;
  text-align: right;
}

.menu-assignment {
  max-height: 500px;
}

.role-info {
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.role-info h4 {
  margin: 0 0 10px 0;
  color: #409eff;
}

.role-info p {
  margin: 0;
  color: #666;
}

.menu-tree {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}

.menu-node {
  display: flex;
  align-items: center;
}

.role-users h4 {
  margin: 0 0 15px 0;
  color: #409eff;
}
</style>
