package com.school.academic.classes;

import com.school.common.response.ApiResponse;
import com.school.student.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "School class management — assign teachers and subjects")
public class ClassController {

    private final ClassService classService;

    @GetMapping
    @Operation(summary = "List all classes")
    public ResponseEntity<ApiResponse<List<SchoolClass>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(classService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get class by ID")
    public ResponseEntity<ApiResponse<SchoolClass>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(classService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a class", description = "Admin only.")
    public ResponseEntity<ApiResponse<SchoolClass>> create(@RequestBody SchoolClass schoolClass) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Class created", classService.create(schoolClass)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a class")
    public ResponseEntity<ApiResponse<SchoolClass>> update(@PathVariable UUID id, @RequestBody SchoolClass schoolClass) {
        return ResponseEntity.ok(ApiResponse.success("Class updated", classService.update(id, schoolClass)));
    }

    @GetMapping("/{id}/students")
    @Operation(summary = "Get all students in a class")
    public ResponseEntity<ApiResponse<List<Student>>> getStudents(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(classService.getStudents(id)));
    }

    @GetMapping("/{id}/subjects")
    @Operation(summary = "Get all subject assignments for a class")
    public ResponseEntity<ApiResponse<List<ClassSubject>>> getSubjects(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(classService.getSubjects(id)));
    }

    @PostMapping("/{id}/subjects")
    @Operation(summary = "Assign a subject (with teacher) to a class", description = "Admin only.")
    public ResponseEntity<ApiResponse<ClassSubject>> assignSubject(
            @PathVariable UUID id,
            @RequestBody ClassSubject classSubject) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject assigned to class", classService.assignSubject(id, classSubject)));
    }
}
