package com.school.teacher.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TeacherDTO {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String employeeNumber;
    private UUID departmentId;
    private String departmentName;
    private String specialization;
    private LocalDate joinedAt;
}
