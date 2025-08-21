package com.alertsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApiResourceUpdateRequest {
    
    @NotBlank(message = "API名稱不能為空")
    @Size(max = 100, message = "API名稱長度不能超過100字符")
    private String apiName;
    
    @NotBlank(message = "API路徑不能為空")
    @Size(max = 500, message = "API路徑長度不能超過500字符")
    private String apiPath;
    
    @NotBlank(message = "HTTP方法不能為空")
    @Size(max = 10, message = "HTTP方法長度不能超過10字符")
    private String httpMethod;
    
    @Size(max = 500, message = "API描述長度不能超過500字符")
    private String apiDescription;
    
    @Size(max = 50, message = "模組名稱長度不能超過50字符")
    private String moduleName;
    
    @NotNull(message = "是否啟用不能為空")
    private Boolean isEnabled;
    
    @NotNull(message = "是否公開不能為空")
    private Boolean isPublic;
    
    private Integer sortOrder;
}
