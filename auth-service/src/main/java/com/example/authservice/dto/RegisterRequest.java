package com.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Хэрэглэгчийн нэр оруулна уу")
    private String sisiId;
    
    @NotBlank(message = "Нууц үг оруулна уу")
    private String password;
    
    private List<String> roles;
} 