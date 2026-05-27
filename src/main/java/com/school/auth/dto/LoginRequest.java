package com.school.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login credentials")
public class LoginRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "User email address", example = "admin@school.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "Admin@12345")
    private String password;
}
