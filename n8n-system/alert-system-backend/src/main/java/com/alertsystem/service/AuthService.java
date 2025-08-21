package com.alertsystem.service;

import com.alertsystem.dto.auth.*;
import com.alertsystem.entity.User;

public interface AuthService {
    
    LoginResponse login(LoginRequest request, String ipAddress, String userAgent);
    
    LoginResponse register(RegisterRequest request);
    
    User getCurrentUser();
    
    User updateProfile(UpdateProfileRequest request);
    
    void logout(String token);
    
    LoginResponse refreshToken(String refreshToken);
}
