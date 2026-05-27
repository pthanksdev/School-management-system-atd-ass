package com.school.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Fields to update on a user — all fields are optional")
public class UpdateUserRequest {

    @Size(min = 2, max = 50)
    @Schema(example = "Jane")
    private String firstName;

    @Size(min = 2, max = 50)
    @Schema(example = "Smith")
    private String lastName;

    @Email
    @Schema(example = "jane.smith@school.com")
    private String email;

    @Schema(example = "+2348099999999")
    private String phone;

    @Schema(description = "Set to false to deactivate the account")
    private Boolean active;

    @Schema(description = "Firebase Cloud Messaging token for push notifications")
    private String fcmToken;
}
