package com.school.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Attendance statistics for a student across all sessions")
public class AttendanceSummaryDTO {
    @Schema(description = "Student UUID")
    private UUID studentId;
    @Schema(example = "John Musa")
    private String studentName;
    @Schema(example = "ADM/2024/001")
    private String admissionNumber;
    @Schema(description = "Total sessions recorded")
    private long totalDays;
    private long present;
    private long absent;
    private long late;
    private long excused;
    @Schema(description = "Percentage of sessions attended (present + late)", example = "87.5")
    private double attendancePercentage;
}
