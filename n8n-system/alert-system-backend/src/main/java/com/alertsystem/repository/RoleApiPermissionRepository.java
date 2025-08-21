package com.alertsystem.repository;

import com.alertsystem.entity.RoleApiPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoleApiPermissionRepository extends JpaRepository<RoleApiPermission, Long> {
    
    /**
     * 根據角色ID查找權限
     */
    List<RoleApiPermission> findByRoleId(Long roleId);
    
    /**
     * 根據API資源ID查找權限
     */
    List<RoleApiPermission> findByApiResourceId(Long apiResourceId);
    
    /**
     * 根據角色ID和API資源ID查找權限
     */
    RoleApiPermission findByRoleIdAndApiResourceId(Long roleId, Long apiResourceId);
    
    /**
     * 刪除角色的所有API權限
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RoleApiPermission rap WHERE rap.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 刪除指定API資源的所有權限
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RoleApiPermission rap WHERE rap.apiResourceId = :apiResourceId")
    void deleteByApiResourceId(@Param("apiResourceId") Long apiResourceId);
    
    /**
     * 批量插入角色API權限
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO role_api_permissions (role_id, api_resource_id, created_by, created_at)
        VALUES (:roleId, :apiResourceId, :createdBy, CURRENT_TIMESTAMP)
    """, nativeQuery = true)
    void insertRoleApiPermission(@Param("roleId") Long roleId, 
                                @Param("apiResourceId") Long apiResourceId, 
                                @Param("createdBy") Long createdBy);
}
