package com.alertsystem.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RoleApiPermissionRequest {
    
    @NotEmpty(message = "API資源ID列表不能為空")
    private List<Long> apiResourceIds;
}
