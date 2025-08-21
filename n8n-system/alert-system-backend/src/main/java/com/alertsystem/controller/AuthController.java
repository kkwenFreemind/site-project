package com.alertsystem.controller;

import com.alertsystem.dto.ApiResponse;
import com.alertsystem.dto.auth.*;
import com.alertsystem.entity.User;
import com.alertsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "認證管理", description = "用戶認證相關API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用戶登入", description = "用戶登入獲取訪問令牌")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, 
                                          HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        LoginResponse response = authService.login(request, ipAddress, userAgent);
        return ApiResponse.success("登入成功", response);
    }

    @PostMapping("/register")
    @Operation(summary = "用戶註冊", description = "註冊新用戶帳號")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ApiResponse.success("註冊成功", response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用戶登出", description = "登出並撤銷訪問令牌")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        authService.logout(token);
        return ApiResponse.success("登出成功", null);
    }

    @GetMapping("/profile")
    @Operation(summary = "獲取用戶資料", description = "獲取當前用戶的詳細資料")
    public ApiResponse<UserProfileResponse> getProfile() {
        User user = authService.getCurrentUser();
        
        UserProfileResponse profile = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .toList())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
                
        return ApiResponse.success(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用戶資料", description = "更新當前用戶的資料")
    public ApiResponse<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User user = authService.updateProfile(request);
        return ApiResponse.success("資料更新成功", user);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用refresh token獲取新的訪問令牌")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return ApiResponse.success("令牌刷新成功", response);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    // Refresh Token Request DTO
    public static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
