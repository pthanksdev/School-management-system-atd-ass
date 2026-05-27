package com.school.assignment;

import com.school.academic.classes.ClassService;
import com.school.academic.classes.ClassSubject;
import com.school.assignment.dto.AssignmentDTO;
import com.school.assignment.dto.CreateAssignmentRequest;
import com.school.assignment.submission.SubmissionRepository;
import com.school.common.enums.AssignmentStatus;
import com.school.common.exception.BadRequestException;
import com.school.common.exception.ResourceNotFoundException;
import com.school.notification.NotificationService;
import com.school.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ClassService classService;
    private final NotificationService notificationService;

    public List<AssignmentDTO> getAll() {
        return assignmentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AssignmentDTO getById(UUID id) {
        return assignmentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));
    }

    public Assignment getEntityById(UUID id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));
    }

    @Transactional
    public AssignmentDTO create(CreateAssignmentRequest req, User teacher) {
        ClassSubject classSubject = classService.getClassSubjectById(req.getClassSubjectId());
        Assignment assignment = Assignment.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .classSubject(classSubject)
                .createdBy(teacher)
                .dueDate(req.getDueDate())
                .maxScore(req.getMaxScore())
                .allowLateSubmission(req.isAllowLateSubmission())
                .attachmentUrl(req.getAttachmentUrl())
                .status(AssignmentStatus.DRAFT)
                .build();
        return toDTO(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentDTO update(UUID id, CreateAssignmentRequest req) {
        Assignment assignment = getEntityById(id);
        if (assignment.getStatus() == AssignmentStatus.CLOSED)
            throw new BadRequestException("Cannot edit a closed assignment");
        if (req.getTitle() != null) assignment.setTitle(req.getTitle());
        if (req.getDescription() != null) assignment.setDescription(req.getDescription());
        if (req.getDueDate() != null) assignment.setDueDate(req.getDueDate());
        if (req.getMaxScore() > 0) assignment.setMaxScore(req.getMaxScore());
        assignment.setAllowLateSubmission(req.isAllowLateSubmission());
        if (req.getAttachmentUrl() != null) assignment.setAttachmentUrl(req.getAttachmentUrl());
        return toDTO(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentDTO publish(UUID id) {
        Assignment assignment = getEntityById(id);
        if (assignment.getStatus() != AssignmentStatus.DRAFT)
            throw new BadRequestException("Only DRAFT assignments can be published");
        assignment.setStatus(AssignmentStatus.PUBLISHED);
        Assignment saved = assignmentRepository.save(assignment);
        notificationService.sendAssignmentPublished(saved);
        return toDTO(saved);
    }

    @Transactional
    public AssignmentDTO close(UUID id) {
        Assignment assignment = getEntityById(id);
        if (assignment.getStatus() != AssignmentStatus.PUBLISHED)
            throw new BadRequestException("Only PUBLISHED assignments can be closed");
        assignment.setStatus(AssignmentStatus.CLOSED);
        return toDTO(assignmentRepository.save(assignment));
    }

    @Transactional
    public void delete(UUID id) {
        Assignment assignment = getEntityById(id);
        if (assignment.getStatus() == AssignmentStatus.PUBLISHED)
            throw new BadRequestException("Cannot delete a published assignment. Close it first.");
        assignmentRepository.deleteById(id);
    }

    public List<AssignmentDTO> getByClassSubject(UUID classSubjectId) {
        return assignmentRepository.findByClassSubjectId(classSubjectId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AssignmentDTO> getByStudent(UUID studentId) {
        return assignmentRepository.findByStudentId(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AssignmentDTO> getByTeacher(UUID teacherId) {
        return assignmentRepository.findByCreatedById(teacherId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AssignmentDTO toDTO(Assignment a) {
        long submissionCount = submissionRepository.countByAssignmentId(a.getId());
        return AssignmentDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .classSubjectId(a.getClassSubject().getId())
                .subjectName(a.getClassSubject().getSubject().getName())
                .className(a.getClassSubject().getSchoolClass().getName())
                .createdById(a.getCreatedBy().getId())
                .createdByName(a.getCreatedBy().getFullName())
                .dueDate(a.getDueDate())
                .maxScore(a.getMaxScore())
                .allowLateSubmission(a.isAllowLateSubmission())
                .attachmentUrl(a.getAttachmentUrl())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .submissionCount(submissionCount)
                .build();
    }
}
