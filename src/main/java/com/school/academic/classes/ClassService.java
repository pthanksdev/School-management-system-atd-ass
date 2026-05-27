package com.school.academic.classes;

import com.school.common.exception.ResourceNotFoundException;
import com.school.student.Student;
import com.school.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassSubjectRepository classSubjectRepository;
    private final StudentRepository studentRepository;

    public List<SchoolClass> getAll() {
        return classRepository.findAll();
    }

    public SchoolClass getById(UUID id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));
    }

    @Transactional
    public SchoolClass create(SchoolClass schoolClass) {
        return classRepository.save(schoolClass);
    }

    @Transactional
    public SchoolClass update(UUID id, SchoolClass updated) {
        SchoolClass existing = getById(id);
        existing.setName(updated.getName());
        existing.setGradeLevel(updated.getGradeLevel());
        existing.setClassTeacher(updated.getClassTeacher());
        return classRepository.save(existing);
    }

    public List<Student> getStudents(UUID classId) {
        getById(classId); // ensure class exists
        return studentRepository.findBySchoolClassId(classId);
    }

    public List<ClassSubject> getSubjects(UUID classId) {
        getById(classId);
        return classSubjectRepository.findBySchoolClassId(classId);
    }

    @Transactional
    public ClassSubject assignSubject(UUID classId, ClassSubject classSubject) {
        SchoolClass schoolClass = getById(classId);
        classSubject.setSchoolClass(schoolClass);
        return classSubjectRepository.save(classSubject);
    }

    public ClassSubject getClassSubjectById(UUID id) {
        return classSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassSubject", "id", id));
    }

    public List<ClassSubject> getClassSubjectsByTeacher(UUID teacherId) {
        return classSubjectRepository.findByTeacherId(teacherId);
    }
}
