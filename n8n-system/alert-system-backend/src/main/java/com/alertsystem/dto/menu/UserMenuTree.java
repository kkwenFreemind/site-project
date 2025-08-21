package com.alertsystem.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用戶選單樹結構 DTO
 * 用於前端路由和選單渲染
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenuTree {
    
    private Long id;
    private String name;
    private String path; // 路由路徑
    private String component;
    private String redirect;
    private Boolean alwaysShow;
    
    // 路由元信息
    private MenuMeta meta;
    
    // 子選單
    private List<UserMenuTree> children;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuMeta {
        private String title; // 選單標題
        private String icon; // 圖標
        private Boolean hidden; // 是否隱藏
        private Boolean keepAlive; // 是否緩存
        private String[] roles; // 所需角色
        private String permission; // 權限標識
        private Boolean affix; // 是否固定在標籤頁
        private Boolean breadcrumb; // 是否顯示在面包屑
        private String activeMenu; // 高亮的選單路徑
    }
}
