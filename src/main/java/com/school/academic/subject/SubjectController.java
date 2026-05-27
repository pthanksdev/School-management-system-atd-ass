package com.school.academic.subject;

import com.school.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
@Tag(name = "Subjects", description = "Subject catalogue management")
public class SubjectController {

    private final SubjectService service;

    @GetMapping
    @Operation(summary = "List all subjects")
    public ResponseEntity<ApiResponse<List<Subject>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID")
    public ResponseEntity<ApiResponse<Subject>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a subject", description = "Admin only. Subject code must be unique.")
    public ResponseEntity<ApiResponse<Subject>> create(@RequestBody Subject subject) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created", service.create(subject)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a subject")
    public ResponseEntity<ApiResponse<Subject>> update(@PathVariable UUID id, @RequestBody Subject subject) {
        return ResponseEntity.ok(ApiResponse.success("Subject updated", service.update(id, subject)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subject")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted"));
    }
}
