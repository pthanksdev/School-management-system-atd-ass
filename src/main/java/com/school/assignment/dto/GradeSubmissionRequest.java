package com.school.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Grading payload — Teacher/Admin only")
public class GradeSubmissionRequest {

    @NotNull @Min(0)
    @Schema(description = "Score awarded (must be ≤ assignment maxScore)", example = "87")
    private Integer score;

    @Schema(description = "Written feedback for the student", example = "Good work! Review question 3.")
    private String feedback;
}
