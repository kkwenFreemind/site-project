package com.alertsystem.controller.system;

import com.alertsystem.dto.common.ApiResponse;
import com.alertsystem.dto.common.PageResponse;
import com.alertsystem.dto.request.ApiResourceCreateRequest;
import com.alertsystem.dto.request.ApiResourceUpdateRequest;
import com.alertsystem.dto.request.RoleApiPermissionRequest;
import com.alertsystem.dto.response.ApiResourceResponse;
import com.alertsystem.security.UserPrincipal;
import com.alertsystem.service.ApiResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "API資源管理", description = "API資源相關操作")
@RestController
@RequestMapping("/api/system/api-resources")
@RequiredArgsConstructor
public class ApiResourceController {
    
    private final ApiResourceService apiResourceService;
    
    @Operation(summary = "分頁查詢API資源")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<ApiResourceResponse>> getApiResources(
            @Parameter(description = "模組名稱") @RequestParam(required = false) String moduleName,
            @Parameter(description = "是否啟用") @RequestParam(required = false) Boolean isEnabled,
            @Parameter(description = "關鍵字搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<ApiResourceResponse> result = apiResourceService.getApiResources(
                moduleName, isEnabled, keyword, page, size);
        
        return ApiResponse.success(PageResponse.of(result));
    }
    
    @Operation(summary = "根據ID獲取API資源")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ApiResourceResponse> getApiResourceById(
            @Parameter(description = "API資源ID") @PathVariable Long id) {
        
        ApiResourceResponse result = apiResourceService.getApiResourceById(id);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "創建API資源")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ApiResourceResponse> createApiResource(
            @Valid @RequestBody ApiResourceCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        ApiResourceResponse result = apiResourceService.createApiResource(request, currentUser.getId());
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "更新API資源")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ApiResourceResponse> updateApiResource(
            @Parameter(description = "API資源ID") @PathVariable Long id,
            @Valid @RequestBody ApiResourceUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        ApiResourceResponse result = apiResourceService.updateApiResource(id, request, currentUser.getId());
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "刪除API資源")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteApiResource(
            @Parameter(description = "API資源ID") @PathVariable Long id) {
        
        apiResourceService.deleteApiResource(id);
        return ApiResponse.success(null);
    }
    
    @Operation(summary = "獲取所有模組名稱")
    @GetMapping("/modules")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<String>> getModuleNames() {
        List<String> result = apiResourceService.getModuleNames();
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "獲取角色的API權限")
    @GetMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ApiResourceResponse>> getRoleApiPermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        
        List<ApiResourceResponse> result = apiResourceService.getRoleApiPermissions(roleId);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "為角色分配API權限")
    @PutMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> assignApiPermissionsToRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Valid @RequestBody RoleApiPermissionRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        apiResourceService.assignApiPermissionsToRole(roleId, request, currentUser.getId());
        return ApiResponse.success(null);
    }
}
