package com.school.student.dto;

import com.school.common.enums.Gender;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentDTO {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String admissionNumber;
    private UUID classId;
    private String className;
    private UUID parentId;
    private String parentName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private LocalDate enrolledAt;
}
