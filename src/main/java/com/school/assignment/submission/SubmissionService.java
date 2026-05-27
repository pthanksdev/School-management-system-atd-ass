package com.school.assignment.submission;

import com.school.assignment.Assignment;
import com.school.assignment.AssignmentService;
import com.school.assignment.dto.GradeSubmissionRequest;
import com.school.assignment.dto.SubmissionDTO;
import com.school.assignment.dto.SubmitAssignmentRequest;
import com.school.common.enums.AssignmentStatus;
import com.school.common.enums.SubmissionStatus;
import com.school.common.exception.BadRequestException;
import com.school.common.exception.ResourceNotFoundException;
import com.school.notification.NotificationService;
import com.school.student.Student;
import com.school.student.StudentService;
import com.school.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentService assignmentService;
    private final StudentService studentService;
    private final NotificationService notificationService;

    @Transactional
    public SubmissionDTO submit(UUID assignmentId, SubmitAssignmentRequest request, User currentUser) {
        Assignment assignment = assignmentService.getEntityById(assignmentId);

        if (assignment.getStatus() != AssignmentStatus.PUBLISHED)
            throw new BadRequestException("Assignment is not open for submission");

        Student student = studentService.getEntityByUserId(currentUser.getId());

        submissionRepository.findByAssignmentIdAndStudentId(assignmentId, student.getId())
                .ifPresent(s -> { throw new BadRequestException("You have already submitted this assignment"); });

        boolean isLate = LocalDateTime.now().isAfter(assignment.getDueDate());
        if (isLate && !assignment.isAllowLateSubmission())
            throw new BadRequestException("Late submissions are not allowed for this assignment");

        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .fileUrl(request.getFileUrl())
                .textContent(request.getTextContent())
                .submittedAt(LocalDateTime.now())
                .late(isLate)
                .status(SubmissionStatus.SUBMITTED)
                .build();

        return toDTO(submissionRepository.save(submission));
    }

    public SubmissionDTO getById(UUID id) {
        return submissionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
    }

    public List<SubmissionDTO> getByAssignment(UUID assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<SubmissionDTO> getByStudent(UUID studentId) {
        return submissionRepository.findByStudentId(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public SubmissionDTO grade(UUID submissionId, GradeSubmissionRequest request, User gradedBy) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        if (submission.getStatus() == SubmissionStatus.PENDING)
            throw new BadRequestException("Cannot grade a submission that hasn't been submitted");

        int maxScore = submission.getAssignment().getMaxScore();
        if (request.getScore() > maxScore)
            throw new BadRequestException("Score cannot exceed max score of " + maxScore);

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setGradedBy(gradedBy);
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.GRADED);

        Submission saved = submissionRepository.save(submission);
        notificationService.sendGradeNotification(saved);
        return toDTO(saved);
    }

    public SubmissionDTO toDTO(Submission s) {
        return SubmissionDTO.builder()
                .id(s.getId())
                .assignmentId(s.getAssignment().getId())
                .assignmentTitle(s.getAssignment().getTitle())
                .studentId(s.getStudent().getId())
                .studentName(s.getStudent().getUser().getFullName())
                .admissionNumber(s.getStudent().getAdmissionNumber())
                .fileUrl(s.getFileUrl())
                .textContent(s.getTextContent())
                .submittedAt(s.getSubmittedAt())
                .late(s.isLate())
                .score(s.getScore())
                .feedback(s.getFeedback())
                .gradedByName(s.getGradedBy() != null ? s.getGradedBy().getFullName() : null)
                .gradedAt(s.getGradedAt())
                .status(s.getStatus())
                .build();
    }
}
