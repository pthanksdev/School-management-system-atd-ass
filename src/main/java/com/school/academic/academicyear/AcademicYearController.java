package com.school.academic.academicyear;

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
@RequestMapping("/academic-years")
@RequiredArgsConstructor
@Tag(name = "Academic Years", description = "Manage academic years and set the current active year")
public class AcademicYearController {

    private final AcademicYearService service;

    @GetMapping
    @Operation(summary = "List all academic years")
    public ResponseEntity<ApiResponse<List<AcademicYear>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }

    @GetMapping("/current")
    @Operation(summary = "Get the current academic year")
    public ResponseEntity<ApiResponse<AcademicYear>> getCurrent() {
        return ResponseEntity.ok(ApiResponse.success(service.getCurrent()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get academic year by ID")
    public ResponseEntity<ApiResponse<AcademicYear>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create an academic year", description = "Admin only.")
    public ResponseEntity<ApiResponse<AcademicYear>> create(@RequestBody AcademicYear year) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Academic year created", service.create(year)));
    }

    @PatchMapping("/{id}/set-current")
    @Operation(summary = "Set the current academic year", description = "Clears the current flag from all others.")
    public ResponseEntity<ApiResponse<AcademicYear>> setCurrent(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Academic year set as current", service.setCurrent(id)));
    }
}
