package com.school.academic.classes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<SchoolClass, UUID> {
    List<SchoolClass> findByAcademicYearId(UUID academicYearId);
    List<SchoolClass> findByClassTeacherId(UUID teacherId);
}
