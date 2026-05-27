package com.school.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    Optional<Teacher> findByUserId(UUID userId);
    Optional<Teacher> findByEmployeeNumber(String employeeNumber);
    List<Teacher> findByDepartmentId(UUID departmentId);
    boolean existsByEmployeeNumber(String employeeNumber);
}
