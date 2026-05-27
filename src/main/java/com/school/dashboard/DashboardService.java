package com.school.dashboard;

import com.school.academic.classes.ClassRepository;
import com.school.academic.subject.SubjectRepository;
import com.school.assignment.AssignmentRepository;
import com.school.assignment.submission.SubmissionRepository;
import com.school.attendance.AttendanceRepository;
import com.school.common.enums.AssignmentStatus;
import com.school.common.enums.AttendanceStatus;
import com.school.common.enums.SubmissionStatus;
import com.school.student.StudentRepository;
import com.school.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository    studentRepository;
    private final TeacherRepository    teacherRepository;
    private final ClassRepository      classRepository;
    private final SubjectRepository    subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    public DashboardStatsDTO getAdminStats() {
        LocalDate today = LocalDate.now();

        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalClasses  = classRepository.count();
        long totalSubjects = subjectRepository.count();

        long todayPresent = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.PRESENT);
        long todayAbsent  = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.ABSENT);
        long todayTotal   = attendanceRepository.countByDate(today);
        double attendanceRate = todayTotal > 0
                ? Math.round(todayPresent * 1000.0 / todayTotal) / 10.0 : 0.0;

        long publishedAssignments = assignmentRepository.countByStatus(AssignmentStatus.PUBLISHED);
        long pendingSubs  = submissionRepository.countByStatus(SubmissionStatus.SUBMITTED);
        long gradedSubs   = submissionRepository.countByStatus(SubmissionStatus.GRADED);

        return DashboardStatsDTO.builder()
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalClasses(totalClasses)
                .totalSubjects(totalSubjects)
                .todayPresent(todayPresent)
                .todayAbsent(todayAbsent)
                .todayAttendanceRate(attendanceRate)
                .totalPublishedAssignments(publishedAssignments)
                .pendingSubmissions(pendingSubs)
                .gradedSubmissions(gradedSubs)
                .build();
    }
}
