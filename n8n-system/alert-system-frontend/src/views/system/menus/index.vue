<template>
  <div class="menus-management">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h2>選單管理</h2>
      <div class="header-actions">
        <el-button @click="expandAll">展開全部</el-button>
        <el-button @click="collapseAll">收起全部</el-button>
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          新增選單
        </el-button>
      </div>
    </div>

    <!-- 搜索區域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="選單名稱">
          <el-input v-model="searchForm.name" placeholder="請輸入選單名稱" clearable />
        </el-form-item>
        <el-form-item label="選單類型">
          <el-select v-model="searchForm.type" placeholder="請選擇類型" clearable>
            <el-option label="目錄" value="DIRECTORY" />
            <el-option label="選單" value="MENU" />
            <el-option label="按鈕" value="BUTTON" />
          </el-select>
        </el-form-item>
        <el-form-item label="狀態">
          <el-select v-model="searchForm.status" placeholder="請選擇狀態" clearable>
            <el-option label="啟用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchMenus">查詢</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 選單樹形表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="menuTree"
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="defaultExpandAll"
        style="width: 100%"
      >
        <el-table-column label="選單名稱" width="300">
          <template #default="{ row }">
            <span class="menu-name">
              <el-icon v-if="row.type === 'DIRECTORY'" style="margin-right: 5px;">
                <Folder />
              </el-icon>
              <el-icon v-else-if="row.type === 'MENU'" style="margin-right: 5px;">
                <Document />
              </el-icon>
              <el-icon v-else style="margin-right: 5px;">
                <Operation />
              </el-icon>
              {{ row.name }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="類型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路徑" width="200" />
        <el-table-column prop="permission" label="權限標識" width="150" />
        <el-table-column label="圖標" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ row.status === 'ACTIVE' ? '啟用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="role in (row.roles || []).slice(0, 2)"
              :key="role.id"
              size="small"
              style="margin-right: 5px;"
            >
              {{ role.name }}
            </el-tag>
            <el-tag v-if="(row.roles || []).length > 2" size="small" type="info">
              +{{ (row.roles || []).length - 2 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="創建時間" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="addChild(row)">
              添加子項
            </el-button>
            <el-button type="info" size="small" @click="editMenu(row)">
              編輯
            </el-button>
            <el-button type="warning" size="small" @click="assignRoles(row)">
              分配角色
            </el-button>
            <el-button
              :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
              size="small"
              @click="toggleMenuStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '禁用' : '啟用' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteMenu(row)">
              刪除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/編輯選單對話框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingMenu ? '編輯選單' : (parentMenu ? '新增子選單' : '新增選單')"
      width="800px"
    >
      <el-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="menuRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="選單名稱" prop="name">
              <el-input v-model="menuForm.name" placeholder="請輸入選單名稱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="選單類型" prop="type">
              <el-select v-model="menuForm.type" placeholder="請選擇類型">
                <el-option label="目錄" value="DIRECTORY" />
                <el-option label="選單" value="MENU" />
                <el-option label="按鈕" value="BUTTON" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="父級選單">
              <el-tree-select
                v-model="menuForm.parentId"
                :data="parentMenuOptions"
                :props="treeSelectProps"
                placeholder="請選擇父級選單（不選則為頂級選單）"
                clearable
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路徑" prop="path">
              <el-input v-model="menuForm.path" placeholder="路由路徑或外鏈地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="權限標識" prop="permission">
              <el-input v-model="menuForm.permission" placeholder="如：user:list,user:create" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="圖標">
              <el-input v-model="menuForm.icon" placeholder="圖標名稱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="menuForm.sortOrder" :min="0" :max="9999" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="狀態">
              <el-radio-group v-model="menuForm.status">
                <el-radio label="ACTIVE">啟用</el-radio>
                <el-radio label="INACTIVE">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="描述">
          <el-input
            v-model="menuForm.description"
            type="textarea"
            :rows="3"
            placeholder="選單描述（可選）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveMenu" :loading="saving">
          {{ editingMenu ? '更新' : '創建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配角色對話框 -->
    <el-dialog v-model="showRoleDialog" title="分配角色" width="600px">
      <div class="role-assignment">
        <div class="menu-info">
          <h4>選單：{{ currentMenu?.name }}</h4>
          <p>類型：{{ getTypeLabel(currentMenu?.type) }}</p>
          <p v-if="currentMenu?.permission">權限：{{ currentMenu.permission }}</p>
        </div>
        
        <div class="role-selection">
          <h4>選擇角色：</h4>
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox
              v-for="role in roles"
              :key="role.id"
              :label="role.id"
            >
              {{ role.name }} - {{ role.description }}
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" @click="saveMenuRoles" :loading="saving">
          保存分配
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, Document, Operation } from '@element-plus/icons-vue'
import { menuAPI, roleAPI } from '@/api/system'
import { formatDate } from '@/utils/date'

// 響應式數據
const loading = ref(false)
const saving = ref(false)
const menuTree = ref([])
const roles = ref([])
const showCreateDialog = ref(false)
const showRoleDialog = ref(false)
const editingMenu = ref(null)
const parentMenu = ref(null)
const currentMenu = ref(null)
const selectedRoles = ref([])
const defaultExpandAll = ref(false)

// 搜索表單
const searchForm = reactive({
  name: '',
  type: '',
  status: ''
})

// 選單表單
const menuForm = reactive({
  name: '',
  type: 'MENU',
  parentId: null,
  path: '',
  permission: '',
  icon: '',
  sortOrder: 0,
  status: 'ACTIVE',
  description: ''
})

// 表單驗證規則
const menuRules = {
  name: [
    { required: true, message: '請輸入選單名稱', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '請選擇選單類型', trigger: 'change' }
  ]
}

// 樹形選擇器props
const treeSelectProps = {
  children: 'children',
  label: 'name',
  value: 'id',
  disabled: (data) => data.type === 'BUTTON'
}

// 表單引用
const menuFormRef = ref()

// 父級選單選項（過濾掉按鈕類型）
const parentMenuOptions = computed(() => {
  return filterMenuOptions(menuTree.value)
})

// 過濾選單選項（移除按鈕類型）
const filterMenuOptions = (menus) => {
  return menus
    .filter(menu => menu.type !== 'BUTTON')
    .map(menu => ({
      ...menu,
      children: menu.children ? filterMenuOptions(menu.children) : undefined
    }))
}

// 獲取類型標籤類型
const getTypeTagType = (type) => {
  const typeMap = {
    'DIRECTORY': 'warning',
    'MENU': 'primary',
    'BUTTON': 'info'
  }
  return typeMap[type] || 'info'
}

// 獲取類型標籤文本
const getTypeLabel = (type) => {
  const typeMap = {
    'DIRECTORY': '目錄',
    'MENU': '選單',
    'BUTTON': '按鈕'
  }
  return typeMap[type] || type
}

// 加載選單樹
const loadMenus = async () => {
  try {
    loading.value = true
    const response = await menuAPI.getMenuTree()
    if (response.data.code === 200) {
      menuTree.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('加載選單樹失敗：' + error.message)
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

// 搜索選單
const searchMenus = () => {
  // 實現搜索邏輯
  loadMenus()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    name: '',
    type: '',
    status: ''
  })
  searchMenus()
}

// 展開全部
const expandAll = () => {
  defaultExpandAll.value = true
  // 重新加載數據以觸發展開
  loadMenus()
}

// 收起全部
const collapseAll = () => {
  defaultExpandAll.value = false
  // 重新加載數據以觸發收起
  loadMenus()
}

// 添加子項
const addChild = (menu) => {
  parentMenu.value = menu
  resetMenuForm()
  menuForm.parentId = menu.id
  showCreateDialog.value = true
}

// 編輯選單
const editMenu = (menu) => {
  editingMenu.value = menu
  Object.assign(menuForm, {
    name: menu.name,
    type: menu.type,
    parentId: menu.parentId,
    path: menu.path,
    permission: menu.permission,
    icon: menu.icon,
    sortOrder: menu.sortOrder,
    status: menu.status,
    description: menu.description
  })
  showCreateDialog.value = true
}

// 保存選單
const saveMenu = async () => {
  try {
    await menuFormRef.value.validate()
    saving.value = true
    
    let response
    if (editingMenu.value) {
      response = await menuAPI.updateMenu(editingMenu.value.id, menuForm)
    } else {
      response = await menuAPI.createMenu(menuForm)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(editingMenu.value ? '更新成功' : '創建成功')
      showCreateDialog.value = false
      resetMenuForm()
      loadMenus()
    }
  } catch (error) {
    ElMessage.error('保存失敗：' + error.message)
  } finally {
    saving.value = false
  }
}

// 重置選單表單
const resetMenuForm = () => {
  editingMenu.value = null
  parentMenu.value = null
  Object.assign(menuForm, {
    name: '',
    type: 'MENU',
    parentId: null,
    path: '',
    permission: '',
    icon: '',
    sortOrder: 0,
    status: 'ACTIVE',
    description: ''
  })
}

// 分配角色
const assignRoles = async (menu) => {
  try {
    currentMenu.value = menu
    
    // 加載選單的角色分配
    const response = await menuAPI.getMenuRoles(menu.id)
    if (response.data.code === 200) {
      selectedRoles.value = response.data.data.map(role => role.id)
    }
    
    showRoleDialog.value = true
  } catch (error) {
    ElMessage.error('加載選單角色失敗：' + error.message)
  }
}

// 保存選單角色分配
const saveMenuRoles = async () => {
  try {
    saving.value = true
    const response = await menuAPI.updateMenuRoles(currentMenu.value.id, selectedRoles.value)
    
    if (response.data.code === 200) {
      ElMessage.success('角色分配成功')
      showRoleDialog.value = false
      loadMenus()
    }
  } catch (error) {
    ElMessage.error('角色分配失敗：' + error.message)
  } finally {
    saving.value = false
  }
}

// 切換選單狀態
const toggleMenuStatus = async (menu) => {
  try {
    const newStatus = menu.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    const action = newStatus === 'ACTIVE' ? '啟用' : '禁用'
    
    await ElMessageBox.confirm(
      `確定要${action}選單 "${menu.name}" 嗎？`,
      '確認操作',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await menuAPI.updateMenu(menu.id, { ...menu, status: newStatus })
    if (response.data.code === 200) {
      ElMessage.success(`${action}成功`)
      loadMenus()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失敗：' + error.message)
    }
  }
}

// 刪除選單
const deleteMenu = async (menu) => {
  try {
    await ElMessageBox.confirm(
      `確定要刪除選單 "${menu.name}" 嗎？此操作將同時刪除所有子選單！`,
      '確認刪除',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await menuAPI.deleteMenu(menu.id)
    if (response.data.code === 200) {
      ElMessage.success('刪除成功')
      loadMenus()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('刪除失敗：' + error.message)
    }
  }
}

// 組件掛載
onMounted(() => {
  loadMenus()
  loadRoles()
})
</script>

<style scoped>
.menus-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
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

.menu-name {
  display: flex;
  align-items: center;
}

.role-assignment {
  max-height: 500px;
}

.menu-info {
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.menu-info h4 {
  margin: 0 0 10px 0;
  color: #409eff;
}

.menu-info p {
  margin: 5px 0;
  color: #666;
}

.role-selection h4 {
  margin: 0 0 15px 0;
  color: #409eff;
}
</style>
