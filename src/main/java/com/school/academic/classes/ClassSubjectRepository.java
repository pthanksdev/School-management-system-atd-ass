package com.school.academic.classes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClassSubjectRepository extends JpaRepository<ClassSubject, UUID> {
    List<ClassSubject> findBySchoolClassId(UUID classId);
    List<ClassSubject> findByTeacherId(UUID teacherId);
    List<ClassSubject> findBySubjectId(UUID subjectId);
}
