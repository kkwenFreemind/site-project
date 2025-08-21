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
public class MenuCreateRequest {
    
    @Builder.Default
    private Long parentId = 0L;
    
    @NotBlank(message = "選單名稱不能為空")
    private String name;
    
    @NotNull(message = "選單類型不能為空")
    private Integer type; // 0:目錄 1:選單 2:按鈕
    
    private String routeName;
    private String routePath;
    private String component;
    private String permission;
    
    @Builder.Default
    private Integer alwaysShow = 0;
    
    @Builder.Default
    private Integer keepAlive = 0;
    
    @Builder.Default
    private Integer visible = 1;
    
    @Builder.Default
    private Integer sortOrder = 0;
    
    private String icon;
    private String redirect;
    private String params;
}
