package com.alertsystem.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用戶名不能為空")
    @Size(min = 3, max = 50, message = "用戶名長度必須在3-50個字符之間")
    private String username;

    @NotBlank(message = "郵箱不能為空")
    @Email(message = "郵箱格式不正確")
    @Size(max = 100, message = "郵箱長度不能超過100個字符")
    private String email;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 100, message = "密碼長度必須在6-100個字符之間")
    private String password;

    @Size(max = 100, message = "姓名長度不能超過100個字符")
    private String fullName;
}
