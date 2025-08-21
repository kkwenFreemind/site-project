package com.alertsystem.security;

import com.alertsystem.service.DynamicPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicPermissionFilter extends OncePerRequestFilter {
    
    private final DynamicPermissionService permissionService;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        // 移除 context path（如果存在）
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        
        log.debug("動態權限檢查: {} {}", method, requestPath);
        
        // 跳過不需要權限檢查的路徑
        if (shouldSkipPermissionCheck(requestPath)) {
            log.debug("跳過權限檢查: {} {}", method, requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 檢查是否為公開API
        if (permissionService.isPublicApi(requestPath, method)) {
            log.debug("公開API，允許訪問: {} {}", method, requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 檢查用戶是否已認證
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("用戶未認證，拒絕訪問: {} {}", method, requestPath);
            handleAccessDenied(response, requestPath, method);
            return;
        }
        
        // 檢查動態權限
        if (!permissionService.hasPermission(request)) {
            handleAccessDenied(response, requestPath, method);
            return;
        }
        
        log.debug("權限檢查通過: {} {}", method, requestPath);
        filterChain.doFilter(request, response);
    }
    
    /**
     * 判斷是否需要跳過權限檢查
     */
    private boolean shouldSkipPermissionCheck(String requestPath) {
        // 跳過靜態資源和健康檢查
        return requestPath.startsWith("/static/") ||
               requestPath.startsWith("/actuator/health") ||
               requestPath.startsWith("/swagger-ui/") ||
               requestPath.startsWith("/v3/api-docs/") ||
               requestPath.equals("/favicon.ico");
    }
    
    /**
     * 處理訪問被拒絕的情況
     */
    private void handleAccessDenied(HttpServletResponse response, 
                                   String requestPath, 
                                   String method) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("message", "權限不足");
        result.put("data", null);
        result.put("timestamp", System.currentTimeMillis());
        result.put("path", requestPath);
        result.put("method", method);
        
        String jsonResponse = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResponse);
        
        log.warn("用戶訪問被拒絕: {} {}", method, requestPath);
    }
}
