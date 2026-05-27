package com.school.attendance.dto;

import com.school.common.enums.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Bulk attendance marking request for a class session")
public class MarkAttendanceRequest {

    @NotNull
    @Schema(description = "UUID of the ClassSubject (class + subject + teacher assignment)")
    private UUID classSubjectId;

    @NotNull
    @Schema(description = "Date of the session", example = "2024-11-20")
    private LocalDate date;

    @NotNull
    @Schema(description = "One entry per student in the class")
    private List<StudentAttendanceEntry> records;

    @Data
    @Schema(description = "Attendance entry for a single student")
    public static class StudentAttendanceEntry {

        @NotNull
        @Schema(description = "UUID of the student")
        private UUID studentId;

        @NotNull
        @Schema(description = "Attendance status", example = "PRESENT",
                allowableValues = {"PRESENT", "ABSENT", "LATE", "EXCUSED"})
        private AttendanceStatus status;

        @Schema(description = "Optional note (e.g. reason for absence)", example = "Medical appointment")
        private String note;
    }
}
