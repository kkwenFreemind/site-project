package com.alertsystem.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuUpdateRequest {
    
    @NotNull(message = "選單ID不能為空")
    private Long id;
    
    private Long parentId;
    
    @NotBlank(message = "選單名稱不能為空")
    private String name;
    
    @NotNull(message = "選單類型不能為空")
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
}
