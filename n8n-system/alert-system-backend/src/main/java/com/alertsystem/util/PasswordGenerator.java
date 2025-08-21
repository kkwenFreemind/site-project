package com.alertsystem.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        System.out.println("Admin password hash: " + encoder.encode(adminPassword));
        System.out.println("User password hash: " + encoder.encode(userPassword));
        
        // 驗證
        String adminHash = encoder.encode(adminPassword);
        System.out.println("Admin password matches: " + encoder.matches(adminPassword, adminHash));
    }
}
