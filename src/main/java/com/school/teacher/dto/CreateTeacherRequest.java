package com.school.teacher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Payload to onboard a new teacher")
public class CreateTeacherRequest {

    @NotBlank
    @Schema(description = "Teacher login email", example = "mrs.okeke@school.com")
    private String email;

    @NotBlank @Size(min = 8)
    @Schema(description = "Initial password (min 8 chars)", example = "Teacher@123")
    private String password;

    @NotBlank
    @Schema(example = "Ngozi")
    private String firstName;

    @NotBlank
    @Schema(example = "Okeke")
    private String lastName;

    @Schema(example = "+2348022222222")
    private String phone;

    @NotBlank
    @Schema(description = "Unique employee/staff number", example = "EMP/2024/042")
    private String employeeNumber;

    @Schema(description = "UUID of the department")
    private UUID departmentId;

    @Schema(description = "Subject specialization", example = "Mathematics")
    private String specialization;

    @Schema(description = "Date joined the school", example = "2024-01-15")
    private LocalDate joinedAt;
}
