package com.alertsystem.service;

import com.alertsystem.dto.menu.*;
import com.alertsystem.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuService {
    
    /**
     * 創建選單
     */
    MenuDTO createMenu(MenuCreateRequest request);
    
    /**
     * 更新選單
     */
    MenuDTO updateMenu(MenuUpdateRequest request);
    
    /**
     * 刪除選單
     */
    void deleteMenu(Long id);
    
    /**
     * 根據ID查詢選單
     */
    MenuDTO getMenuById(Long id);
    
    /**
     * 查詢所有選單（樹狀結構）
     */
    List<MenuDTO> getAllMenuTree();
    
    /**
     * 分頁查詢選單列表
     */
    Page<MenuDTO> getMenuList(Pageable pageable);
    
    /**
     * 根據用戶ID查詢用戶選單樹
     */
    List<UserMenuTree> getUserMenuTree(Long userId);
    
    /**
     * 根據用戶ID查詢用戶權限列表
     */
    List<String> getUserPermissions(Long userId);
    
    /**
     * 根據角色ID查詢角色選單
     */
    List<MenuDTO> getMenusByRoleId(Long roleId);
    
    /**
     * 為角色分配選單權限
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);
    
    /**
     * 根據父級ID查詢子選單
     */
    List<MenuDTO> getChildMenus(Long parentId);
    
    /**
     * 根據選單類型查詢選單
     */
    List<MenuDTO> getMenusByType(Integer type);
    
    /**
     * 檢查選單是否存在子選單
     */
    boolean hasChildren(Long menuId);
    
    /**
     * 根據權限標識查詢選單
     */
    MenuDTO getMenuByPermission(String permission);
    
    /**
     * 根據路由名稱查詢選單
     */
    MenuDTO getMenuByRouteName(String routeName);
    
    /**
     * 構建選單樹狀結構
     */
    List<MenuDTO> buildMenuTree(List<Menu> menus);
    
    /**
     * 將實體轉換為DTO
     */
    MenuDTO convertToDTO(Menu menu);
    
    /**
     * 將DTO轉換為實體
     */
    Menu convertToEntity(MenuCreateRequest request);
    
    /**
     * 更新實體字段
     */
    void updateEntity(Menu menu, MenuUpdateRequest request);
}
