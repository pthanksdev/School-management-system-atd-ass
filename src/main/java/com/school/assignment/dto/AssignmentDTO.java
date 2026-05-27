package com.school.assignment.dto;

import com.school.common.enums.AssignmentStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentDTO {
    private UUID id;
    private String title;
    private String description;
    private UUID classSubjectId;
    private String subjectName;
    private String className;
    private UUID createdById;
    private String createdByName;
    private LocalDateTime dueDate;
    private int maxScore;
    private boolean allowLateSubmission;
    private String attachmentUrl;
    private AssignmentStatus status;
    private LocalDateTime createdAt;
    private long submissionCount;
}
