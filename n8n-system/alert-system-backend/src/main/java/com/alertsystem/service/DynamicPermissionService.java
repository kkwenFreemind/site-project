package com.alertsystem.service;

import com.alertsystem.entity.ApiResource;
import com.alertsystem.repository.ApiResourceRepository;
import com.alertsystem.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicPermissionService {
    
    private final ApiResourceRepository apiResourceRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    /**
     * 檢查當前用戶是否有權限訪問指定的API
     */
    public boolean hasPermission(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        // 移除 context path（如果存在）
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        
        log.debug("檢查權限: {} {}", method, requestPath);
        
        // 1. 檢查是否為公開API
        if (isPublicApi(requestPath, method)) {
            log.debug("公開API，允許訪問: {} {}", method, requestPath);
            return true;
        }
        
        // 2. 獲取當前用戶
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("用戶未認證，拒絕訪問: {} {}", method, requestPath);
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            log.debug("無效的用戶主體，拒絕訪問: {} {}", method, requestPath);
            return false;
        }
        
        UserPrincipal userDetails = (UserPrincipal) principal;
        Long userId = userDetails.getId();
        
        // 3. 檢查用戶權限
        boolean hasPermission = checkUserPermission(userId, requestPath, method);
        
        if (hasPermission) {
            log.debug("用戶 {} 有權限訪問: {} {}", userId, method, requestPath);
        } else {
            log.warn("用戶 {} 無權限訪問: {} {}", userId, method, requestPath);
        }
        
        return hasPermission;
    }
    
    /**
     * 檢查是否為公開API
     */
    @Cacheable(value = "publicApis", key = "#requestPath + '_' + #method")
    public boolean isPublicApi(String requestPath, String method) {
        List<ApiResource> publicApis = apiResourceRepository.findByIsPublicTrueAndIsEnabledTrue();
        
        return publicApis.stream()
                .anyMatch(api -> method.equalsIgnoreCase(api.getHttpMethod()) && 
                               matchPath(api.getApiPath(), requestPath));
    }
    
    /**
     * 檢查用戶權限
     */
    @Cacheable(value = "userPermissions", key = "#userId + '_' + #requestPath + '_' + #method")
    public boolean checkUserPermission(Long userId, String requestPath, String method) {
        List<ApiResource> userApis = apiResourceRepository.findApiResourcesByUserId(userId);
        
        return userApis.stream()
                .anyMatch(api -> method.equalsIgnoreCase(api.getHttpMethod()) && 
                               matchPath(api.getApiPath(), requestPath));
    }
    
    /**
     * 路徑匹配（支持通配符）
     */
    private boolean matchPath(String pattern, String path) {
        // 處理通配符路徑，例如 /api/users/* 匹配 /api/users/123
        if (pattern.contains("*")) {
            return pathMatcher.match(pattern, path);
        }
        // 精確匹配
        return pattern.equals(path);
    }
    
    /**
     * 獲取用戶的所有API權限
     */
    @Cacheable(value = "userApiResources", key = "#userId")
    public List<ApiResource> getUserApiResources(Long userId) {
        return apiResourceRepository.findApiResourcesByUserId(userId);
    }
    
    /**
     * 獲取角色的所有API權限
     */
    @Cacheable(value = "roleApiResources", key = "#roleId")
    public List<ApiResource> getRoleApiResources(Long roleId) {
        return apiResourceRepository.findApiResourcesByRoleId(roleId);
    }
    
    /**
     * 清除用戶權限緩存
     */
    public void clearUserPermissionCache(Long userId) {
        // 實現緩存清除邏輯
        log.info("清除用戶 {} 的權限緩存", userId);
    }
    
    /**
     * 清除角色權限緩存
     */
    public void clearRolePermissionCache(Long roleId) {
        // 實現緩存清除邏輯
        log.info("清除角色 {} 的權限緩存", roleId);
    }
}
