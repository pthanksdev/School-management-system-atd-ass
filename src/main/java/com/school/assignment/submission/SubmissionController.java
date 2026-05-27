package com.school.assignment.submission;

import com.school.assignment.dto.GradeSubmissionRequest;
import com.school.assignment.dto.SubmissionDTO;
import com.school.assignment.dto.SubmitAssignmentRequest;
import com.school.common.response.ApiResponse;
import com.school.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Students submit assignments; teachers grade them")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/assignments/{assignmentId}")
    @Operation(summary = "Submit an assignment", description = "Student only. One submission per assignment. " +
               "Late submissions only accepted if the assignment allows it.")
    public ResponseEntity<ApiResponse<SubmissionDTO>> submit(
            @PathVariable UUID assignmentId,
            @RequestBody SubmitAssignmentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assignment submitted successfully",
                        submissionService.submit(assignmentId, request, currentUser)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a submission by ID")
    public ResponseEntity<ApiResponse<SubmissionDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getById(id)));
    }

    @GetMapping("/assignments/{assignmentId}")
    @Operation(summary = "Get all submissions for an assignment", description = "Teacher/Admin only.")
    public ResponseEntity<ApiResponse<List<SubmissionDTO>>> getByAssignment(@PathVariable UUID assignmentId) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getByAssignment(assignmentId)));
    }

    @GetMapping("/students/{studentId}")
    @Operation(summary = "Get all submissions by a student")
    public ResponseEntity<ApiResponse<List<SubmissionDTO>>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getByStudent(studentId)));
    }

    @PostMapping("/{id}/grade")
    @Operation(summary = "Grade a submission", description = "Teacher/Admin only. Notifies student and parent on grading.")
    public ResponseEntity<ApiResponse<SubmissionDTO>> grade(
            @PathVariable UUID id,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Submission graded",
                submissionService.grade(id, request, currentUser)));
    }
}
