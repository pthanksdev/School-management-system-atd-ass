package com.school.teacher;

import com.school.academic.classes.ClassSubject;
import com.school.common.response.ApiResponse;
import com.school.teacher.dto.CreateTeacherRequest;
import com.school.teacher.dto.TeacherDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Teacher onboarding and class assignment")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @Operation(summary = "List all teachers")
    public ResponseEntity<ApiResponse<List<TeacherDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by ID")
    public ResponseEntity<ApiResponse<TeacherDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Onboard a new teacher", description = "Creates a User account (TEACHER role) and a Teacher profile. Admin only.")
    public ResponseEntity<ApiResponse<TeacherDTO>> create(@Valid @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Teacher created successfully", teacherService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update teacher profile")
    public ResponseEntity<ApiResponse<TeacherDTO>> update(
            @PathVariable UUID id,
            @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Teacher updated", teacherService.update(id, request)));
    }

    @GetMapping("/{id}/classes")
    @Operation(summary = "Get all class-subjects assigned to a teacher")
    public ResponseEntity<ApiResponse<List<ClassSubject>>> getClasses(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getClasses(id)));
    }
}
