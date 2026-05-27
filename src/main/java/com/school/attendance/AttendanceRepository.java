package com.school.attendance;

import com.school.common.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {

    List<AttendanceRecord> findByStudentId(UUID studentId);

    List<AttendanceRecord> findByStudentIdAndDateBetween(UUID studentId, LocalDate from, LocalDate to);

    List<AttendanceRecord> findByClassSubjectIdAndDate(UUID classSubjectId, LocalDate date);

    List<AttendanceRecord> findByClassSubjectIdAndDateBetween(UUID classSubjectId, LocalDate from, LocalDate to);

    Optional<AttendanceRecord> findByStudentIdAndClassSubjectIdAndDate(
            UUID studentId, UUID classSubjectId, LocalDate date);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND a.status = :status")
    long countByStudentIdAndStatus(@Param("studentId") UUID studentId, @Param("status") AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId")
    long countByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.date = :date AND a.status = :status")
    long countByDateAndStatus(@Param("date") LocalDate date, @Param("status") AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.date = :date")
    long countByDate(@Param("date") LocalDate date);

    @Query("""
        SELECT a FROM AttendanceRecord a
        WHERE a.classSubject.schoolClass.id = :classId
        AND a.date BETWEEN :from AND :to
        ORDER BY a.date DESC
        """)
    List<AttendanceRecord> findByClassAndDateRange(
            @Param("classId") UUID classId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
