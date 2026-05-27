package com.school.assignment;

import com.school.assignment.dto.AssignmentDTO;
import com.school.assignment.dto.CreateAssignmentRequest;
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
@RequestMapping("/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignments", description = "Create and manage assignments through their DRAFT → PUBLISHED → CLOSED lifecycle")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping
    @Operation(summary = "List all assignments")
    public ResponseEntity<ApiResponse<List<AssignmentDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<ApiResponse<AssignmentDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new assignment", description = "Creates in DRAFT status. Teacher/Admin only.")
    public ResponseEntity<ApiResponse<AssignmentDTO>> create(
            @Valid @RequestBody CreateAssignmentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assignment created", assignmentService.create(request, currentUser)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an assignment", description = "Only editable while in DRAFT status.")
    public ResponseEntity<ApiResponse<AssignmentDTO>> update(
            @PathVariable UUID id,
            @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Assignment updated", assignmentService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an assignment", description = "Only DRAFT assignments can be deleted.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        assignmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Assignment deleted"));
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish an assignment", description = "Moves DRAFT → PUBLISHED and notifies students.")
    public ResponseEntity<ApiResponse<AssignmentDTO>> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Assignment published", assignmentService.publish(id)));
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close an assignment", description = "Moves PUBLISHED → CLOSED. No more submissions accepted.")
    public ResponseEntity<ApiResponse<AssignmentDTO>> close(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Assignment closed", assignmentService.close(id)));
    }

    @GetMapping("/class/{classSubjectId}")
    @Operation(summary = "Get all assignments for a class-subject")
    public ResponseEntity<ApiResponse<List<AssignmentDTO>>> getByClass(@PathVariable UUID classSubjectId) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getByClassSubject(classSubjectId)));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all published assignments visible to a student")
    public ResponseEntity<ApiResponse<List<AssignmentDTO>>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getByStudent(studentId)));
    }
}
