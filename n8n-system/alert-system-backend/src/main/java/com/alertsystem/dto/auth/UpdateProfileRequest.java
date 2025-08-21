package com.alertsystem.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Email(message = "郵箱格式不正確")
    @Size(max = 100, message = "郵箱長度不能超過100個字符")
    private String email;

    @Size(max = 100, message = "姓名長度不能超過100個字符")
    private String fullName;
}
