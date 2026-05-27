package com.school.attendance.dto;

import com.school.common.enums.AttendanceStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String admissionNumber;
    private UUID classSubjectId;
    private String subjectName;
    private String className;
    private LocalDate date;
    private AttendanceStatus status;
    private String note;
    private String markedByName;
    private LocalDateTime markedAt;
}
