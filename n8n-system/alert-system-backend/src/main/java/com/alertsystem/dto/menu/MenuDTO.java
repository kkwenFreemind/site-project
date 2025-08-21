package com.alertsystem.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    
    private Long id;
    private Long parentId;
    private String treePath;
    private String name;
    private Integer type;
    private String routeName;
    private String routePath;
    private String component;
    private String permission;
    private Integer alwaysShow;
    private Integer keepAlive;
    private Integer visible;
    private Integer sortOrder;
    private String icon;
    private String redirect;
    private String params;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 子選單列表
    private List<MenuDTO> children;
    
    // 選單類型描述
    private String typeDescription;
    
    // 父選單名稱
    private String parentName;
    
    // 是否有子選單
    private Boolean hasChildren;
}
