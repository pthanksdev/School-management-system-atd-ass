package com.school.attendance;

import com.school.academic.classes.ClassSubject;
import com.school.attendance.dto.AttendanceSummaryDTO;
import com.school.attendance.dto.MarkAttendanceRequest;
import com.school.common.enums.AttendanceStatus;
import com.school.notification.NotificationService;
import com.school.student.Student;
import com.school.student.StudentRepository;
import com.school.academic.classes.ClassService;
import com.school.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock StudentRepository studentRepository;
    @Mock ClassService classService;
    @Mock NotificationService notificationService;

    @InjectMocks AttendanceService attendanceService;

    private Student student;
    private ClassSubject classSubject;
    private User teacher;
    private UUID studentId;
    private UUID csId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        csId      = UUID.randomUUID();

        teacher = User.builder().id(UUID.randomUUID()).firstName("Jane").lastName("Doe").build();

        com.school.academic.subject.Subject subj = new com.school.academic.subject.Subject();
        subj.setName("Mathematics");

        com.school.academic.classes.SchoolClass sc = new com.school.academic.classes.SchoolClass();
        sc.setId(UUID.randomUUID());
        sc.setName("Form 3A");

        classSubject = ClassSubject.builder()
                .id(csId)
                .subject(subj)
                .schoolClass(sc)
                .build();

        com.school.user.User studentUser = User.builder()
                .id(UUID.randomUUID()).firstName("John").lastName("Student").build();

        student = Student.builder()
                .id(studentId)
                .user(studentUser)
                .admissionNumber("ADM001")
                .schoolClass(sc)
                .build();
    }

    @Test
    void markAttendance_shouldSaveRecordAndReturnDTO() {
        when(classService.getClassSubjectById(csId)).thenReturn(classSubject);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(attendanceRepository.findByStudentIdAndClassSubjectIdAndDate(any(), any(), any()))
                .thenReturn(Optional.empty());

        AttendanceRecord saved = AttendanceRecord.builder()
                .id(UUID.randomUUID())
                .student(student)
                .classSubject(classSubject)
                .date(LocalDate.now())
                .status(AttendanceStatus.PRESENT)
                .markedBy(teacher)
                .build();
        when(attendanceRepository.save(any())).thenReturn(saved);

        MarkAttendanceRequest.StudentAttendanceEntry entry = new MarkAttendanceRequest.StudentAttendanceEntry();
        entry.setStudentId(studentId);
        entry.setStatus(AttendanceStatus.PRESENT);

        MarkAttendanceRequest req = new MarkAttendanceRequest();
        req.setClassSubjectId(csId);
        req.setDate(LocalDate.now());
        req.setRecords(List.of(entry));

        var result = attendanceService.markAttendance(req, teacher);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        verify(attendanceRepository, times(1)).save(any());
        verify(notificationService, never()).sendAttendanceAlert(any(), any(), any());
    }

    @Test
    void markAttendance_whenAbsent_shouldNotifyParent() {
        com.school.user.User parent = User.builder().id(UUID.randomUUID()).firstName("Parent").lastName("P").build();
        student.setParent(parent);

        when(classService.getClassSubjectById(csId)).thenReturn(classSubject);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(attendanceRepository.findByStudentIdAndClassSubjectIdAndDate(any(), any(), any()))
                .thenReturn(Optional.empty());

        AttendanceRecord saved = AttendanceRecord.builder()
                .id(UUID.randomUUID()).student(student).classSubject(classSubject)
                .date(LocalDate.now()).status(AttendanceStatus.ABSENT).markedBy(teacher).build();
        when(attendanceRepository.save(any())).thenReturn(saved);

        MarkAttendanceRequest.StudentAttendanceEntry entry = new MarkAttendanceRequest.StudentAttendanceEntry();
        entry.setStudentId(studentId);
        entry.setStatus(AttendanceStatus.ABSENT);

        MarkAttendanceRequest req = new MarkAttendanceRequest();
        req.setClassSubjectId(csId);
        req.setDate(LocalDate.now());
        req.setRecords(List.of(entry));

        attendanceService.markAttendance(req, teacher);

        verify(notificationService, times(1)).sendAttendanceAlert(eq(student), any(), eq(classSubject));
    }

    @Test
    void getSummary_shouldCalculatePercentageCorrectly() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(attendanceRepository.countByStudentId(studentId)).thenReturn(10L);
        when(attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT)).thenReturn(8L);
        when(attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT)).thenReturn(1L);
        when(attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.LATE)).thenReturn(1L);
        when(attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.EXCUSED)).thenReturn(0L);

        AttendanceSummaryDTO summary = attendanceService.getSummary(studentId);

        assertThat(summary.getAttendancePercentage()).isEqualTo(90.0);
        assertThat(summary.getPresent()).isEqualTo(8);
        assertThat(summary.getAbsent()).isEqualTo(1);
    }
}
