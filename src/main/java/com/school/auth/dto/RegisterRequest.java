package com.school.auth.dto;

import com.school.common.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "New user registration payload")
public class RegisterRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address", example = "jane.doe@school.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Password (min 8 characters)", example = "Secret@123")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Schema(description = "First name", example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Phone number", example = "+2348012345678")
    private String phone;

    @NotNull(message = "Role is required")
    @Schema(description = "User role", example = "TEACHER", allowableValues = {"ADMIN","TEACHER","STUDENT","PARENT"})
    private Role role;
}
