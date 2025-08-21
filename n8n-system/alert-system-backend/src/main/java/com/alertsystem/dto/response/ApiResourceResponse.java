package com.alertsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResourceResponse {
    
    private Long id;
    private String apiName;
    private String apiPath;
    private String httpMethod;
    private String apiDescription;
    private String moduleName;
    private Boolean isEnabled;
    private Boolean isPublic;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
