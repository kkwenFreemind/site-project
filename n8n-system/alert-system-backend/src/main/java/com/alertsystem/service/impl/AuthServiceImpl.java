package com.alertsystem.service.impl;

import com.alertsystem.dto.auth.*;
import com.alertsystem.entity.LoginLog;
import com.alertsystem.entity.Role;
import com.alertsystem.entity.User;
import com.alertsystem.repository.LoginLogRepository;
import com.alertsystem.repository.RoleRepository;
import com.alertsystem.repository.UserRepository;
import com.alertsystem.service.AuthService;
import com.alertsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;

    @Override
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        try {
            // 驗證用戶憑證
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // 獲取用戶信息
            User user = userRepository.findByUsernameWithRoles(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 檢查用戶狀態
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                throw new RuntimeException("用戶帳號已被停用");
            }

            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // 將 token 存入 Redis (用於登出時撤銷)
            redisTemplate.opsForValue().set(
                "token:" + token, 
                user.getUsername(), 
                24, 
                TimeUnit.HOURS
            );

            // 記錄登入日誌
            LoginLog loginLog = LoginLog.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(LoginLog.LoginStatus.SUCCESS)
                .build();
            loginLogRepository.save(loginLog);

            // 構建響應
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()))
                .build();

            return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24小時
                .userInfo(userInfo)
                .build();

        } catch (Exception e) {
            // 記錄失敗的登入嘗試
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user != null) {
                LoginLog loginLog = LoginLog.builder()
                    .user(user)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .status(LoginLog.LoginStatus.FAILED)
                    .build();
                loginLogRepository.save(loginLog);
            }
            
            log.error("登入失敗: {}", e.getMessage());
            throw new RuntimeException("用戶名或密碼錯誤");
        }
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用戶名已存在");
        }

        // 檢查郵箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("郵箱已存在");
        }

        // 獲取默認角色
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("默認角色不存在"));

        // 創建新用戶
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .status(User.UserStatus.ACTIVE)
            .roles(Set.of(userRole))
            .build();

        user = userRepository.save(user);

        // 生成 Token
        String token = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 將 token 存入 Redis
        redisTemplate.opsForValue().set(
            "token:" + token, 
            user.getUsername(), 
            24, 
            TimeUnit.HOURS
        );

        // 構建響應
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .status(user.getStatus().name())
            .roles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()))
            .build();

        return LoginResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(86400L)
            .userInfo(userInfo)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
    }

    @Override
    public User updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        if (StringUtils.hasText(request.getEmail()) && 
            !request.getEmail().equals(user.getEmail())) {
            // 檢查新郵箱是否已被使用
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("郵箱已存在");
            }
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }

        return userRepository.save(user);
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 從 Redis 中刪除 token
        redisTemplate.delete("token:" + token);
        
        // 清空 Security Context
        SecurityContextHolder.clearContext();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token 無效");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 生成新的 token
        String newToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 將新 token 存入 Redis
        redisTemplate.opsForValue().set(
            "token:" + newToken, 
            user.getUsername(), 
            24, 
            TimeUnit.HOURS
        );

        // 構建響應
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .status(user.getStatus().name())
            .roles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()))
            .build();

        return LoginResponse.builder()
            .token(newToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(86400L)
            .userInfo(userInfo)
            .build();
    }
}
