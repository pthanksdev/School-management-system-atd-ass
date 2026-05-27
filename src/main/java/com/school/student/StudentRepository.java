package com.school.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByUserId(UUID userId);
    Optional<Student> findByAdmissionNumber(String admissionNumber);
    List<Student> findBySchoolClassId(UUID classId);
    List<Student> findByParentId(UUID parentId);
    boolean existsByAdmissionNumber(String admissionNumber);
}
