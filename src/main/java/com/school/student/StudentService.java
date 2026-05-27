package com.school.student;

import com.school.academic.classes.ClassService;
import com.school.common.enums.Role;
import com.school.common.exception.BadRequestException;
import com.school.common.exception.ResourceNotFoundException;
import com.school.student.dto.CreateStudentRequest;
import com.school.student.dto.StudentDTO;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassService classService;
    private final PasswordEncoder passwordEncoder;

    public List<StudentDTO> getAll() {
        return studentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public StudentDTO getById(UUID id) {
        return studentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    public Student getEntityById(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    public Student getEntityByUserId(UUID userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + userId));
    }

    @Transactional
    public StudentDTO create(CreateStudentRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already in use: " + req.getEmail());
        if (studentRepository.existsByAdmissionNumber(req.getAdmissionNumber()))
            throw new BadRequestException("Admission number already exists: " + req.getAdmissionNumber());

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .phone(req.getPhone())
                .role(Role.STUDENT)
                .active(true)
                .build();
        userRepository.save(user);

        Student student = Student.builder()
                .user(user)
                .admissionNumber(req.getAdmissionNumber())
                .schoolClass(req.getClassId() != null ? classService.getById(req.getClassId()) : null)
                .parent(req.getParentId() != null ? userRepository.findById(req.getParentId()).orElse(null) : null)
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .address(req.getAddress())
                .enrolledAt(req.getEnrolledAt())
                .build();

        return toDTO(studentRepository.save(student));
    }

    @Transactional
    public StudentDTO update(UUID id, CreateStudentRequest req) {
        Student student = getEntityById(id);
        if (req.getClassId()  != null) student.setSchoolClass(classService.getById(req.getClassId()));
        if (req.getParentId() != null) student.setParent(userRepository.findById(req.getParentId()).orElse(null));
        if (req.getDateOfBirth() != null) student.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender()      != null) student.setGender(req.getGender());
        if (req.getAddress()     != null) student.setAddress(req.getAddress());
        return toDTO(studentRepository.save(student));
    }

    @Transactional
    public void delete(UUID id) {
        if (!studentRepository.existsById(id))
            throw new ResourceNotFoundException("Student", "id", id);
        studentRepository.deleteById(id);
    }

    public List<StudentDTO> getByParent(UUID parentId) {
        return studentRepository.findByParentId(parentId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public StudentDTO toDTO(Student s) {
        return StudentDTO.builder()
                .id(s.getId())
                .userId(s.getUser().getId())
                .firstName(s.getUser().getFirstName())
                .lastName(s.getUser().getLastName())
                .email(s.getUser().getEmail())
                .admissionNumber(s.getAdmissionNumber())
                .classId(s.getSchoolClass()  != null ? s.getSchoolClass().getId()   : null)
                .className(s.getSchoolClass() != null ? s.getSchoolClass().getName() : null)
                .parentId(s.getParent()   != null ? s.getParent().getId()        : null)
                .parentName(s.getParent() != null ? s.getParent().getFullName()  : null)
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .address(s.getAddress())
                .enrolledAt(s.getEnrolledAt())
                .build();
    }
}
