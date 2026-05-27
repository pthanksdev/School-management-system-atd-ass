package com.school.assignment;

import com.school.common.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    List<Assignment> findByClassSubjectId(UUID classSubjectId);

    List<Assignment> findByCreatedById(UUID teacherId);

    List<Assignment> findByStatus(AssignmentStatus status);

    long countByStatus(AssignmentStatus status);

    @Query("""
        SELECT a FROM Assignment a
        WHERE a.classSubject.schoolClass.id = :classId
        AND a.status = 'PUBLISHED'
        ORDER BY a.dueDate ASC
        """)
    List<Assignment> findPublishedByClass(@Param("classId") UUID classId);

    @Query("""
        SELECT DISTINCT a FROM Assignment a
        JOIN a.classSubject cs
        JOIN cs.schoolClass sc
        WHERE sc.id = (SELECT s.schoolClass.id FROM Student s WHERE s.id = :studentId)
        AND a.status = 'PUBLISHED'
        ORDER BY a.dueDate ASC
        """)
    List<Assignment> findByStudentId(@Param("studentId") UUID studentId);
}
