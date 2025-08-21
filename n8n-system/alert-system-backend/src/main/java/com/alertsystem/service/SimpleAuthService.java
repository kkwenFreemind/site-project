package com.alertsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.alertsystem.entity.User;
import com.alertsystem.repository.UserRepository;

@Service
public class SimpleAuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public boolean validateLogin(String username, String plainPassword) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                System.out.println("用戶不存在: " + username);
                return false;
            }
            
            String storedHash = user.getPasswordHash();
            System.out.println("存儲的哈希: " + storedHash);
            System.out.println("哈希長度: " + storedHash.length());
            
            // 檢查是否是有效的 BCrypt 哈希
            if (!storedHash.startsWith("$2a$") && !storedHash.startsWith("$2b$") && !storedHash.startsWith("$2y$")) {
                System.out.println("無效的 BCrypt 格式");
                return false;
            }
            
            boolean matches = passwordEncoder.matches(plainPassword, storedHash);
            System.out.println("密碼匹配結果: " + matches);
            return matches;
            
        } catch (Exception e) {
            System.out.println("驗證過程出錯: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public String generateHash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
}
