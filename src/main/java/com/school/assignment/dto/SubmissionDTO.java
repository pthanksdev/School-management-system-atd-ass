package com.school.assignment.dto;

import com.school.common.enums.SubmissionStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {
    private UUID id;
    private UUID assignmentId;
    private String assignmentTitle;
    private UUID studentId;
    private String studentName;
    private String admissionNumber;
    private String fileUrl;
    private String textContent;
    private LocalDateTime submittedAt;
    private boolean late;
    private Integer score;
    private String feedback;
    private String gradedByName;
    private LocalDateTime gradedAt;
    private SubmissionStatus status;
}
