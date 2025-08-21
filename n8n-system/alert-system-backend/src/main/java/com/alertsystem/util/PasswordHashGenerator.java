package com.alertsystem.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("Generating password hashes...");
        System.out.println("admin123 hash: " + encoder.encode("admin123"));
        System.out.println("test123 hash: " + encoder.encode("test123"));
        
        // Verify the hash works
        String adminHash = encoder.encode("admin123");
        boolean matches = encoder.matches("admin123", adminHash);
        System.out.println("Verification test - admin123 matches: " + matches);
    }
}
