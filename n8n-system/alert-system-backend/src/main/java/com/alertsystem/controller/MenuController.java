package com.alertsystem.controller;

import com.alertsystem.dto.ApiResponse;
import com.alertsystem.dto.menu.*;
import com.alertsystem.security.UserPrincipal;
import com.alertsystem.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "選單管理", description = "選單管理相關API")
@Slf4j
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "創建選單", description = "創建新的選單項目")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public ResponseEntity<ApiResponse<MenuDTO>> createMenu(@Valid @RequestBody MenuCreateRequest request) {
        log.info("Creating menu: {}", request.getName());
        MenuDTO menu = menuService.createMenu(request);
        return ResponseEntity.ok(ApiResponse.success("選單創建成功", menu));
    }

    @Operation(summary = "更新選單", description = "更新選單信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public ResponseEntity<ApiResponse<MenuDTO>> updateMenu(
            @Parameter(description = "選單ID") @PathVariable Long id,
            @Valid @RequestBody MenuUpdateRequest request) {
        log.info("Updating menu: {}", id);
        request.setId(id);
        MenuDTO menu = menuService.updateMenu(request);
        return ResponseEntity.ok(ApiResponse.success("選單更新成功", menu));
    }

    @Operation(summary = "刪除選單", description = "刪除指定選單")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public ResponseEntity<ApiResponse<String>> deleteMenu(
            @Parameter(description = "選單ID") @PathVariable Long id) {
        log.info("Deleting menu: {}", id);
        menuService.deleteMenu(id);
        return ResponseEntity.ok(ApiResponse.success("選單刪除成功", ""));
    }

    @Operation(summary = "查詢選單詳情", description = "根據ID查詢選單詳細信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public ResponseEntity<ApiResponse<MenuDTO>> getMenuById(
            @Parameter(description = "選單ID") @PathVariable Long id) {
        MenuDTO menu = menuService.getMenuById(id);
        return ResponseEntity.ok(ApiResponse.success(menu));
    }

    @Operation(summary = "查詢選單樹", description = "查詢所有選單的樹狀結構")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenuTree() {
        List<MenuDTO> menuTree = menuService.getAllMenuTree();
        return ResponseEntity.ok(ApiResponse.success(menuTree));
    }

    @Operation(summary = "分頁查詢選單", description = "分頁查詢選單列表")
    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:view')")
    public ResponseEntity<ApiResponse<Page<MenuDTO>>> getMenuList(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MenuDTO> menuPage = menuService.getMenuList(pageable);
        return ResponseEntity.ok(ApiResponse.success(menuPage));
    }

    @Operation(summary = "查詢用戶選單", description = "查詢當前用戶可訪問的選單樹")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<UserMenuTree>>> getUserMenus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Long userId = userPrincipal.getId();
            
            List<UserMenuTree> userMenus = menuService.getUserMenuTree(userId);
            return ResponseEntity.ok(ApiResponse.success(userMenus));
        }
        
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }

    @Operation(summary = "查詢用戶權限", description = "查詢當前用戶的所有權限")
    @GetMapping("/user/permissions")
    public ResponseEntity<ApiResponse<List<String>>> getUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Long userId = userPrincipal.getId();
            
            List<String> permissions = menuService.getUserPermissions(userId);
            return ResponseEntity.ok(ApiResponse.success(permissions));
        }
        
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }

    @Operation(summary = "查詢角色選單", description = "查詢指定角色的選單權限")
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('system:role:view')")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getRoleMenus(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<MenuDTO> menus = menuService.getMenusByRoleId(roleId);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    @Operation(summary = "為角色分配選單", description = "為指定角色分配選單權限")
    @PostMapping("/role/{roleId}/assign")
    @PreAuthorize("hasAuthority('system:role:assign')")
    public ResponseEntity<ApiResponse<String>> assignMenusToRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        log.info("Assigning menus to role: {}, menus: {}", roleId, menuIds);
        menuService.assignMenusToRole(roleId, menuIds);
        return ResponseEntity.ok(ApiResponse.success("選單權限分配成功", ""));
    }

    @Operation(summary = "查詢子選單", description = "查詢指定父選單的子選單")
    @GetMapping("/children/{parentId}")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getChildMenus(
            @Parameter(description = "父選單ID") @PathVariable Long parentId) {
        List<MenuDTO> children = menuService.getChildMenus(parentId);
        return ResponseEntity.ok(ApiResponse.success(children));
    }

    @Operation(summary = "按類型查詢選單", description = "根據選單類型查詢選單列表")
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenusByType(
            @Parameter(description = "選單類型 0:目錄 1:選單 2:按鈕") @PathVariable Integer type) {
        List<MenuDTO> menus = menuService.getMenusByType(type);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }
}
