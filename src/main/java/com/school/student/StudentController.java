package com.school.student;

import com.school.attendance.AttendanceService;
import com.school.attendance.dto.AttendanceDTO;
import com.school.common.response.ApiResponse;
import com.school.student.dto.CreateStudentRequest;
import com.school.student.dto.StudentDTO;
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
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student enrollment and profile management")
public class StudentController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;

    @GetMapping
    @Operation(summary = "List all students")
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Enroll a new student", description = "Creates a User account (STUDENT role) and a Student profile. Admin only.")
    public ResponseEntity<ApiResponse<StudentDTO>> create(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", studentService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student profile", description = "Update class, parent, address etc.")
    public ResponseEntity<ApiResponse<StudentDTO>> update(
            @PathVariable UUID id,
            @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated", studentService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted"));
    }

    @GetMapping("/{id}/attendance")
    @Operation(summary = "Get student attendance history")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendance(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getByStudent(id)));
    }
}
