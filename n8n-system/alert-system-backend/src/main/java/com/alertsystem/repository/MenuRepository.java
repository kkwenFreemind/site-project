package com.alertsystem.repository;

import com.alertsystem.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * 根據父級ID查詢子選單
     */
    List<Menu> findByParentIdOrderBySortOrderAsc(Long parentId);

    /**
     * 查詢所有根選單（一級選單）
     */
    @Query("SELECT m FROM Menu m WHERE m.parentId = 0 OR m.parentId IS NULL ORDER BY m.sortOrder ASC")
    List<Menu> findRootMenus();

    /**
     * 根據類型查詢選單
     */
    List<Menu> findByTypeOrderBySortOrderAsc(Integer type);

    /**
     * 根據可見性查詢選單
     */
    List<Menu> findByVisibleOrderBySortOrderAsc(Integer visible);

    /**
     * 根據權限標識查詢選單
     */
    Menu findByPermission(String permission);

    /**
     * 根據路由名稱查詢選單
     */
    Menu findByRouteName(String routeName);

    /**
     * 根據用戶ID查詢用戶可訪問的選單
     */
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.users u " +
           "WHERE u.id = :userId " +
           "AND m.visible = 1 " +
           "ORDER BY m.sortOrder ASC")
    List<Menu> findMenusByUserId(@Param("userId") Long userId);

    /**
     * 根據角色ID查詢角色可訪問的選單
     */
    @Query("SELECT m FROM Menu m " +
           "JOIN m.roles r " +
           "WHERE r.id = :roleId " +
           "AND m.visible = 1 " +
           "ORDER BY m.sortOrder ASC")
    List<Menu> findMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * 根據用戶ID查詢用戶可訪問的選單樹
     */
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.users u " +
           "WHERE u.id = :userId " +
           "AND m.visible = 1 " +
           "AND m.type IN (0, 1) " + // 只查詢目錄和選單，不包括按鈕
           "ORDER BY m.parentId, m.sortOrder ASC")
    List<Menu> findMenuTreeByUserId(@Param("userId") Long userId);

    /**
     * 根據用戶ID查詢用戶可訪問的權限列表
     */
    @Query("SELECT DISTINCT m.permission FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.users u " +
           "WHERE u.id = :userId " +
           "AND m.permission IS NOT NULL " +
           "AND m.permission <> ''")
    List<String> findPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根據父級ID查詢是否存在子選單
     */
    boolean existsByParentId(Long parentId);

    /**
     * 根據路由路徑查詢選單
     */
    Menu findByRoutePath(String routePath);

    /**
     * 查詢所有可見的選單，按父級和排序排列
     */
    @Query("SELECT m FROM Menu m WHERE m.visible = 1 ORDER BY m.parentId, m.sortOrder ASC")
    List<Menu> findAllVisibleMenus();

    /**
     * 根據選單名稱模糊查詢
     */
    @Query("SELECT m FROM Menu m WHERE m.name LIKE %:name% ORDER BY m.sortOrder ASC")
    List<Menu> findByNameContaining(@Param("name") String name);
}
