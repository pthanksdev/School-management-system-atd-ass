package com.school.teacher;

import com.school.academic.classes.ClassService;
import com.school.academic.classes.ClassSubject;
import com.school.academic.department.Department;
import com.school.academic.department.DepartmentRepository;
import com.school.common.enums.Role;
import com.school.common.exception.BadRequestException;
import com.school.common.exception.ResourceNotFoundException;
import com.school.teacher.dto.CreateTeacherRequest;
import com.school.teacher.dto.TeacherDTO;
import com.school.user.User;
import com.school.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ClassService classService;
    private final PasswordEncoder passwordEncoder;

    public List<TeacherDTO> getAll() {
        return teacherRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TeacherDTO getById(UUID id) {
        return teacherRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
    }

    public Teacher getEntityById(UUID id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
    }

    @Transactional
    public TeacherDTO create(CreateTeacherRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already in use: " + req.getEmail());
        if (teacherRepository.existsByEmployeeNumber(req.getEmployeeNumber()))
            throw new BadRequestException("Employee number already exists: " + req.getEmployeeNumber());

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .phone(req.getPhone())
                .role(Role.TEACHER)
                .active(true)
                .build();
        userRepository.save(user);

        Department dept = req.getDepartmentId() != null
                ? departmentRepository.findById(req.getDepartmentId()).orElse(null) : null;

        Teacher teacher = Teacher.builder()
                .user(user)
                .employeeNumber(req.getEmployeeNumber())
                .department(dept)
                .specialization(req.getSpecialization())
                .joinedAt(req.getJoinedAt())
                .build();

        return toDTO(teacherRepository.save(teacher));
    }

    @Transactional
    public TeacherDTO update(UUID id, CreateTeacherRequest req) {
        Teacher teacher = getEntityById(id);
        if (req.getDepartmentId() != null)
            teacher.setDepartment(departmentRepository.findById(req.getDepartmentId()).orElse(null));
        if (req.getSpecialization() != null) teacher.setSpecialization(req.getSpecialization());
        if (req.getJoinedAt() != null) teacher.setJoinedAt(req.getJoinedAt());
        return toDTO(teacherRepository.save(teacher));
    }

    public List<ClassSubject> getClasses(UUID teacherId) {
        getEntityById(teacherId);
        return classService.getClassSubjectsByTeacher(teacherId);
    }

    public TeacherDTO toDTO(Teacher t) {
        return TeacherDTO.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .firstName(t.getUser().getFirstName())
                .lastName(t.getUser().getLastName())
                .email(t.getUser().getEmail())
                .phone(t.getUser().getPhone())
                .employeeNumber(t.getEmployeeNumber())
                .departmentId(t.getDepartment() != null ? t.getDepartment().getId() : null)
                .departmentName(t.getDepartment() != null ? t.getDepartment().getName() : null)
                .specialization(t.getSpecialization())
                .joinedAt(t.getJoinedAt())
                .build();
    }
}
