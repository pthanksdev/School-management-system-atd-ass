package com.school.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Student submission payload — provide at least one of fileUrl or textContent")
public class SubmitAssignmentRequest {

    @Schema(description = "S3 key or URL of the submitted file", example = "submissions/uuid.pdf")
    private String fileUrl;

    @Schema(description = "Inline text answer", example = "x = 3 and x = -2 because...")
    private String textContent;
}
