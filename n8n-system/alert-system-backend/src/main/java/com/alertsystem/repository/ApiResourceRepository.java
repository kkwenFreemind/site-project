package com.alertsystem.repository;

import com.alertsystem.entity.ApiResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiResourceRepository extends JpaRepository<ApiResource, Long> {
    
    /**
     * 根據路徑和HTTP方法查找API資源
     */
    Optional<ApiResource> findByApiPathAndHttpMethod(String apiPath, String httpMethod);
    
    /**
     * 查找啟用的API資源
     */
    List<ApiResource> findByIsEnabledTrueOrderBySortOrder();
    
    /**
     * 根據模組名稱查找API資源
     */
    List<ApiResource> findByModuleNameAndIsEnabledTrueOrderBySortOrder(String moduleName);
    
    /**
     * 查找公開的API資源
     */
    List<ApiResource> findByIsPublicTrueAndIsEnabledTrue();
    
    /**
     * 根據用戶角色查找有權限的API資源
     */
    @Query("""
        SELECT DISTINCT ar FROM ApiResource ar
        JOIN RoleApiPermission rap ON ar.id = rap.apiResourceId
        JOIN User u ON u.id = :userId
        JOIN u.roles r ON r.id = rap.roleId
        WHERE ar.isEnabled = true
        ORDER BY ar.sortOrder
    """)
    List<ApiResource> findApiResourcesByUserId(@Param("userId") Long userId);
    
    /**
     * 根據角色ID查找API資源
     */
    @Query("""
        SELECT ar FROM ApiResource ar
        JOIN RoleApiPermission rap ON ar.id = rap.apiResourceId
        WHERE rap.roleId = :roleId 
        AND ar.isEnabled = true
        ORDER BY ar.sortOrder
    """)
    List<ApiResource> findApiResourcesByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 檢查用戶是否有權限訪問指定API
     */
    @Query("""
        SELECT COUNT(ar) > 0 FROM ApiResource ar
        JOIN RoleApiPermission rap ON ar.id = rap.apiResourceId
        JOIN User u ON u.id = :userId
        JOIN u.roles r ON r.id = rap.roleId
        WHERE (ar.apiPath = :apiPath OR ar.apiPath = :wildcardPath)
        AND ar.httpMethod = :httpMethod
        AND ar.isEnabled = true
    """)
    boolean hasPermission(@Param("userId") Long userId, 
                         @Param("apiPath") String apiPath,
                         @Param("wildcardPath") String wildcardPath,
                         @Param("httpMethod") String httpMethod);
    
    /**
     * 分頁查詢API資源
     */
    @Query("""
        SELECT ar FROM ApiResource ar
        WHERE (:moduleName IS NULL OR ar.moduleName = :moduleName)
        AND (:isEnabled IS NULL OR ar.isEnabled = :isEnabled)
        AND (:keyword IS NULL OR ar.apiName LIKE %:keyword% OR ar.apiPath LIKE %:keyword%)
        ORDER BY ar.sortOrder, ar.id
    """)
    Page<ApiResource> findApiResourcesWithConditions(
            @Param("moduleName") String moduleName,
            @Param("isEnabled") Boolean isEnabled,
            @Param("keyword") String keyword,
            Pageable pageable);
}
