package com.school.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Payload to create a new assignment")
public class CreateAssignmentRequest {

    @NotBlank
    @Schema(description = "Assignment title", example = "Chapter 5 Exercise — Quadratic Equations")
    private String title;

    @Schema(description = "Full assignment instructions", example = "Solve all questions on pages 112-115.")
    private String description;

    @NotNull
    @Schema(description = "UUID of the ClassSubject this assignment belongs to")
    private UUID classSubjectId;

    @NotNull
    @Schema(description = "Submission deadline", example = "2024-12-01T23:59:00")
    private LocalDateTime dueDate;

    @Min(1)
    @Schema(description = "Maximum obtainable score", example = "100")
    private int maxScore;

    @Schema(description = "Whether submissions past the due date are accepted", example = "false")
    private boolean allowLateSubmission;

    @Schema(description = "S3 key or URL of an attachment (e.g. question paper PDF)")
    private String attachmentUrl;
}
