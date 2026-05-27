package com.school.attendance;

import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.dto.AttendanceSummaryDTO;
import com.school.attendance.dto.MarkAttendanceRequest;
import com.school.common.enums.AttendanceStatus;
import com.school.common.response.ApiResponse;
import com.school.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Mark, update and report on student attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @Operation(summary = "Mark attendance for a session",
               description = "Bulk-mark attendance for an entire class on a given date. Teacher/Admin only. " +
                             "Automatically notifies parents when a student is marked ABSENT.")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Attendance marked successfully",
                attendanceService.markAttendance(request, currentUser)));
    }

    @GetMapping("/class/{classSubjectId}")
    @Operation(summary = "Get attendance for a class on a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getByClass(
            @PathVariable UUID classSubjectId,
            @Parameter(description = "Date in YYYY-MM-DD format")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getByClassSubject(classSubjectId, date)));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get full attendance history for a student")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getByStudent(studentId)));
    }

    @GetMapping("/student/{studentId}/summary")
    @Operation(summary = "Get attendance summary for a student",
               description = "Returns total days, present/absent/late/excused counts and attendance percentage.")
    public ResponseEntity<ApiResponse<AttendanceSummaryDTO>> getSummary(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getSummary(studentId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an attendance record", description = "Correct a previously marked status. Teacher/Admin only.")
    public ResponseEntity<ApiResponse<AttendanceDTO>> updateRecord(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        AttendanceStatus status = AttendanceStatus.valueOf(body.get("status"));
        String note = body.get("note");
        return ResponseEntity.ok(ApiResponse.success("Attendance updated", attendanceService.update(id, status, note)));
    }

    @GetMapping("/report")
    @Operation(summary = "Attendance report for a class over a date range")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getReport(
            @RequestParam UUID classId,
            @Parameter(description = "Start date YYYY-MM-DD")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End date YYYY-MM-DD")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getReport(classId, from, to)));
    }
}
