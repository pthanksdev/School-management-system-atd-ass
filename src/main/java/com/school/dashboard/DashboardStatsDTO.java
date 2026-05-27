package com.school.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin dashboard overview statistics")
public class DashboardStatsDTO {
    @Schema(description = "Total enrolled students")
    private long totalStudents;
    @Schema(description = "Total onboarded teachers")
    private long totalTeachers;
    @Schema(description = "Total classes")
    private long totalClasses;
    @Schema(description = "Total subjects in catalogue")
    private long totalSubjects;
    @Schema(description = "Students marked PRESENT today")
    private long todayPresent;
    @Schema(description = "Students marked ABSENT today")
    private long todayAbsent;
    @Schema(description = "Today's overall attendance rate (%)", example = "91.3")
    private double todayAttendanceRate;
    @Schema(description = "Currently published (open) assignments")
    private long totalPublishedAssignments;
    @Schema(description = "Submissions awaiting grading")
    private long pendingSubmissions;
    @Schema(description = "Submissions already graded")
    private long gradedSubmissions;
}
