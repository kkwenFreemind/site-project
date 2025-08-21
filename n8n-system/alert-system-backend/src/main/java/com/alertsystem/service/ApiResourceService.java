package com.alertsystem.service;

import com.alertsystem.dto.request.ApiResourceCreateRequest;
import com.alertsystem.dto.request.ApiResourceUpdateRequest;
import com.alertsystem.dto.request.RoleApiPermissionRequest;
import com.alertsystem.dto.response.ApiResourceResponse;
import com.alertsystem.entity.ApiResource;
import com.alertsystem.entity.RoleApiPermission;
import com.alertsystem.repository.ApiResourceRepository;
import com.alertsystem.repository.RoleApiPermissionRepository;
import com.alertsystem.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiResourceService {
    
    private final ApiResourceRepository apiResourceRepository;
    private final RoleApiPermissionRepository roleApiPermissionRepository;
    private final RoleRepository roleRepository;
    private final DynamicPermissionService permissionService;
    
    /**
     * 分頁查詢API資源
     */
    public Page<ApiResourceResponse> getApiResources(String moduleName, 
                                                    Boolean isEnabled, 
                                                    String keyword, 
                                                    int page, 
                                                    int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApiResource> apiResources = apiResourceRepository
                .findApiResourcesWithConditions(moduleName, isEnabled, keyword, pageable);
        
        return apiResources.map(this::convertToResponse);
    }
    
    /**
     * 根據ID獲取API資源
     */
    public ApiResourceResponse getApiResourceById(Long id) {
        ApiResource apiResource = apiResourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API資源不存在"));
        return convertToResponse(apiResource);
    }
    
    /**
     * 創建API資源
     */
    @Transactional
    @CacheEvict(value = {"publicApis", "userPermissions", "userApiResources", "roleApiResources"}, allEntries = true)
    public ApiResourceResponse createApiResource(ApiResourceCreateRequest request, Long userId) {
        // 檢查路徑和方法是否已存在
        if (apiResourceRepository.findByApiPathAndHttpMethod(request.getApiPath(), request.getHttpMethod()).isPresent()) {
            throw new RuntimeException("API路徑和方法組合已存在");
        }
        
        ApiResource apiResource = new ApiResource();
        apiResource.setApiName(request.getApiName());
        apiResource.setApiPath(request.getApiPath());
        apiResource.setHttpMethod(request.getHttpMethod().toUpperCase());
        apiResource.setApiDescription(request.getApiDescription());
        apiResource.setModuleName(request.getModuleName());
        apiResource.setIsEnabled(request.getIsEnabled());
        apiResource.setIsPublic(request.getIsPublic());
        apiResource.setSortOrder(request.getSortOrder());
        apiResource.setCreatedBy(userId);
        
        ApiResource saved = apiResourceRepository.save(apiResource);
        log.info("創建API資源: {}", saved.getApiName());
        
        return convertToResponse(saved);
    }
    
    /**
     * 更新API資源
     */
    @Transactional
    @CacheEvict(value = {"publicApis", "userPermissions", "userApiResources", "roleApiResources"}, allEntries = true)
    public ApiResourceResponse updateApiResource(Long id, ApiResourceUpdateRequest request, Long userId) {
        ApiResource apiResource = apiResourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API資源不存在"));
        
        // 檢查路徑和方法是否與其他記錄衝突
        apiResourceRepository.findByApiPathAndHttpMethod(request.getApiPath(), request.getHttpMethod())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException("API路徑和方法組合已存在");
                    }
                });
        
        apiResource.setApiName(request.getApiName());
        apiResource.setApiPath(request.getApiPath());
        apiResource.setHttpMethod(request.getHttpMethod().toUpperCase());
        apiResource.setApiDescription(request.getApiDescription());
        apiResource.setModuleName(request.getModuleName());
        apiResource.setIsEnabled(request.getIsEnabled());
        apiResource.setIsPublic(request.getIsPublic());
        apiResource.setSortOrder(request.getSortOrder());
        apiResource.setUpdatedBy(userId);
        
        ApiResource saved = apiResourceRepository.save(apiResource);
        log.info("更新API資源: {}", saved.getApiName());
        
        return convertToResponse(saved);
    }
    
    /**
     * 刪除API資源
     */
    @Transactional
    @CacheEvict(value = {"publicApis", "userPermissions", "userApiResources", "roleApiResources"}, allEntries = true)
    public void deleteApiResource(Long id) {
        ApiResource apiResource = apiResourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API資源不存在"));
        
        // 刪除相關權限
        roleApiPermissionRepository.deleteByApiResourceId(id);
        
        // 刪除API資源
        apiResourceRepository.deleteById(id);
        
        log.info("刪除API資源: {}", apiResource.getApiName());
    }
    
    /**
     * 獲取角色的API權限
     */
    @Cacheable(value = "roleApiPermissions", key = "#roleId")
    public List<ApiResourceResponse> getRoleApiPermissions(Long roleId) {
        // 檢查角色是否存在
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("角色不存在");
        }
        
        List<ApiResource> apiResources = apiResourceRepository.findApiResourcesByRoleId(roleId);
        return apiResources.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 為角色分配API權限
     */
    @Transactional
    @CacheEvict(value = {"userPermissions", "userApiResources", "roleApiResources", "roleApiPermissions"}, allEntries = true)
    public void assignApiPermissionsToRole(Long roleId, RoleApiPermissionRequest request, Long userId) {
        // 檢查角色是否存在
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("角色不存在");
        }
        
        // 刪除角色現有的API權限
        roleApiPermissionRepository.deleteByRoleId(roleId);
        
        // 添加新的API權限
        if (request.getApiResourceIds() != null && !request.getApiResourceIds().isEmpty()) {
            List<RoleApiPermission> permissions = request.getApiResourceIds().stream()
                    .map(apiResourceId -> {
                        RoleApiPermission permission = new RoleApiPermission();
                        permission.setRoleId(roleId);
                        permission.setApiResourceId(apiResourceId);
                        permission.setCreatedBy(userId);
                        return permission;
                    })
                    .collect(Collectors.toList());
            
            roleApiPermissionRepository.saveAll(permissions);
        }
        
        // 清除相關緩存
        permissionService.clearRolePermissionCache(roleId);
        
        log.info("為角色 {} 分配API權限，共 {} 個", roleId, 
                request.getApiResourceIds() != null ? request.getApiResourceIds().size() : 0);
    }
    
    /**
     * 獲取所有模組名稱
     */
    public List<String> getModuleNames() {
        return apiResourceRepository.findAll().stream()
                .map(ApiResource::getModuleName)
                .filter(moduleName -> moduleName != null && !moduleName.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * 轉換為響應DTO
     */
    private ApiResourceResponse convertToResponse(ApiResource apiResource) {
        return ApiResourceResponse.builder()
                .id(apiResource.getId())
                .apiName(apiResource.getApiName())
                .apiPath(apiResource.getApiPath())
                .httpMethod(apiResource.getHttpMethod())
                .apiDescription(apiResource.getApiDescription())
                .moduleName(apiResource.getModuleName())
                .isEnabled(apiResource.getIsEnabled())
                .isPublic(apiResource.getIsPublic())
                .sortOrder(apiResource.getSortOrder())
                .createdAt(apiResource.getCreatedAt())
                .updatedAt(apiResource.getUpdatedAt())
                .build();
    }
}
