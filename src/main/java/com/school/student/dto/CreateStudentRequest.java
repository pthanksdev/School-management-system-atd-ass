package com.school.student.dto;

import com.school.common.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Payload to enroll a new student")
public class CreateStudentRequest {

    @NotBlank
    @Schema(description = "Student email (used to log in)", example = "john.student@school.com")
    private String email;

    @NotBlank @Size(min = 8)
    @Schema(description = "Initial password (min 8 chars)", example = "Student@123")
    private String password;

    @NotBlank
    @Schema(example = "John")
    private String firstName;

    @NotBlank
    @Schema(example = "Musa")
    private String lastName;

    @Schema(example = "+2348011111111")
    private String phone;

    @NotBlank
    @Schema(description = "Unique admission number", example = "ADM/2024/001")
    private String admissionNumber;

    @Schema(description = "UUID of the class to enroll the student in")
    private UUID classId;

    @Schema(description = "UUID of the parent (User with PARENT role)")
    private UUID parentId;

    @Schema(description = "Date of birth", example = "2008-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender", example = "MALE", allowableValues = {"MALE","FEMALE"})
    private Gender gender;

    @Schema(description = "Home address", example = "12 Adeola Street, Lagos")
    private String address;

    @Schema(description = "Date of enrollment", example = "2024-09-01")
    private LocalDate enrolledAt;
}
