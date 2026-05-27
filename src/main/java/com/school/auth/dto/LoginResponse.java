package com.school.auth.dto;

import com.school.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT tokens and user profile returned on login or register")
public class LoginResponse {

    @Schema(description = "Short-lived JWT access token (24h)")
    private String token;

    @Schema(description = "Long-lived refresh token (7 days)")
    private String refreshToken;

    @Schema(description = "Authenticated user profile")
    private UserDTO user;
}
