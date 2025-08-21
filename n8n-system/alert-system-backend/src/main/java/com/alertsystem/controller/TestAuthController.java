package com.alertsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alertsystem.service.SimpleAuthService;

@RestController
@RequestMapping("/api/test")
public class TestAuthController {
    
    @Autowired
    private SimpleAuthService simpleAuthService;
    
    @PostMapping("/validate")
    public String testValidation(@RequestParam String username, @RequestParam String password) {
        try {
            boolean isValid = simpleAuthService.validateLogin(username, password);
            return "驗證結果: " + (isValid ? "成功" : "失敗");
        } catch (Exception e) {
            return "錯誤: " + e.getMessage();
        }
    }
    
    @PostMapping("/generate-hash")
    public String generateHash(@RequestParam String password) {
        try {
            String hash = simpleAuthService.generateHash(password);
            return "生成的哈希: " + hash;
        } catch (Exception e) {
            return "錯誤: " + e.getMessage();
        }
    }
}
