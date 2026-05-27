package com.school.attendance;

import com.school.academic.classes.ClassService;
import com.school.academic.classes.ClassSubject;
import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.dto.AttendanceSummaryDTO;
import com.school.attendance.dto.MarkAttendanceRequest;
import com.school.common.enums.AttendanceStatus;
import com.school.common.exception.ResourceNotFoundException;
import com.school.notification.NotificationService;
import com.school.student.Student;
import com.school.student.StudentRepository;
import com.school.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final ClassService classService;
    private final NotificationService notificationService;

    @Transactional
    public List<AttendanceDTO> markAttendance(MarkAttendanceRequest request, User markedBy) {
        ClassSubject classSubject = classService.getClassSubjectById(request.getClassSubjectId());
        List<AttendanceRecord> saved = new ArrayList<>();

        for (MarkAttendanceRequest.StudentAttendanceEntry entry : request.getRecords()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", entry.getStudentId()));

            // Upsert: update if already exists for this date
            AttendanceRecord record = attendanceRepository
                    .findByStudentIdAndClassSubjectIdAndDate(
                            entry.getStudentId(), request.getClassSubjectId(), request.getDate())
                    .orElse(AttendanceRecord.builder()
                            .student(student)
                            .classSubject(classSubject)
                            .date(request.getDate())
                            .build());

            record.setStatus(entry.getStatus());
            record.setNote(entry.getNote());
            record.setMarkedBy(markedBy);
            saved.add(attendanceRepository.save(record));

            // Notify parent if absent
            if (entry.getStatus() == AttendanceStatus.ABSENT && student.getParent() != null) {
                notificationService.sendAttendanceAlert(student, request.getDate(), classSubject);
            }
        }

        return saved.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getByClassSubject(UUID classSubjectId, LocalDate date) {
        return attendanceRepository.findByClassSubjectIdAndDate(classSubjectId, date)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getByStudent(UUID studentId) {
        return attendanceRepository.findByStudentId(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AttendanceSummaryDTO getSummary(UUID studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        long total   = attendanceRepository.countByStudentId(studentId);
        long present = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        long absent  = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT);
        long late    = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.LATE);
        long excused = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.EXCUSED);

        double percentage = total > 0 ? Math.round((present + late) * 1000.0 / total) / 10.0 : 0.0;

        return AttendanceSummaryDTO.builder()
                .studentId(studentId)
                .studentName(student.getUser().getFullName())
                .admissionNumber(student.getAdmissionNumber())
                .totalDays(total)
                .present(present)
                .absent(absent)
                .late(late)
                .excused(excused)
                .attendancePercentage(percentage)
                .build();
    }

    @Transactional
    public AttendanceDTO update(UUID id, AttendanceStatus status, String note) {
        AttendanceRecord record = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceRecord", "id", id));
        record.setStatus(status);
        if (note != null) record.setNote(note);
        return toDTO(attendanceRepository.save(record));
    }

    public List<AttendanceDTO> getReport(UUID classId, LocalDate from, LocalDate to) {
        return attendanceRepository.findByClassAndDateRange(classId, from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AttendanceDTO toDTO(AttendanceRecord r) {
        return AttendanceDTO.builder()
                .id(r.getId())
                .studentId(r.getStudent().getId())
                .studentName(r.getStudent().getUser().getFullName())
                .admissionNumber(r.getStudent().getAdmissionNumber())
                .classSubjectId(r.getClassSubject().getId())
                .subjectName(r.getClassSubject().getSubject().getName())
                .className(r.getClassSubject().getSchoolClass().getName())
                .date(r.getDate())
                .status(r.getStatus())
                .note(r.getNote())
                .markedByName(r.getMarkedBy() != null ? r.getMarkedBy().getFullName() : null)
                .markedAt(r.getMarkedAt())
                .build();
    }
}
