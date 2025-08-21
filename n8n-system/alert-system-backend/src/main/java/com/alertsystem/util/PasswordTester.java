package com.alertsystem.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTester {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 數據庫中的哈希值
        String adminHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        String testuserHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye7xDJw58pME9JKwwB8q7VYfX9Zt.B1ee";
        
        // 測試密碼
        String adminPassword = "admin123";
        String testuserPassword = "test123";
        
        System.out.println("=== 密碼驗證測試 ===");
        
        // 檢查哈希格式
        System.out.println("Admin hash length: " + adminHash.length());
        System.out.println("Admin hash starts with $2a$10$: " + adminHash.startsWith("$2a$10$"));
        System.out.println("Testuser hash length: " + testuserHash.length());
        System.out.println("Testuser hash starts with $2a$10$: " + testuserHash.startsWith("$2a$10$"));
        
        // 測試密碼匹配
        System.out.println("\n=== 密碼匹配測試 ===");
        try {
            boolean adminMatch = encoder.matches(adminPassword, adminHash);
            System.out.println("Admin password matches: " + adminMatch);
            
            boolean testuserMatch = encoder.matches(testuserPassword, testuserHash);
            System.out.println("Testuser password matches: " + testuserMatch);
            
            // 生成新的哈希來比較
            System.out.println("\n=== 新生成的哈希 ===");
            String newAdminHash = encoder.encode(adminPassword);
            String newTestuserHash = encoder.encode(testuserPassword);
            
            System.out.println("New admin hash: " + newAdminHash);
            System.out.println("New testuser hash: " + newTestuserHash);
            
            // 測試新哈希
            System.out.println("New admin hash matches: " + encoder.matches(adminPassword, newAdminHash));
            System.out.println("New testuser hash matches: " + encoder.matches(testuserPassword, newTestuserHash));
            
        } catch (Exception e) {
            System.err.println("Error during password testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
