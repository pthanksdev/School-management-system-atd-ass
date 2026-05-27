package com.school.academic.department;

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
@RequestMapping("/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Academic department management")
public class DepartmentController {

    private final DepartmentService service;

    @GetMapping
    @Operation(summary = "List all departments")
    public ResponseEntity<ApiResponse<List<Department>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<Department>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a department", description = "Admin only.")
    public ResponseEntity<ApiResponse<Department>> create(@RequestBody Department dept) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created", service.create(dept)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    public ResponseEntity<ApiResponse<Department>> update(@PathVariable UUID id, @RequestBody Department dept) {
        return ResponseEntity.ok(ApiResponse.success("Department updated", service.update(id, dept)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted"));
    }
}
