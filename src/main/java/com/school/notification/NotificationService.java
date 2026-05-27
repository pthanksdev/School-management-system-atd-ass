package com.school.notification;

import com.school.academic.classes.ClassSubject;
import com.school.assignment.Assignment;
import com.school.assignment.submission.Submission;
import com.school.common.enums.NotificationType;
import com.school.student.Student;
import com.school.student.StudentRepository;
import com.school.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;

    // ─── In-App Notifications ────────────────────────────────────────────────

    public List<NotificationDTO> getForUser(UUID userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadForUser(UUID userId) {
        return notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Transactional
    public int markAllRead(UUID userId) {
        return notificationRepository.markAllReadByRecipient(userId);
    }

    @Transactional
    public void markRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    // ─── Domain-Level Senders ────────────────────────────────────────────────

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAttendanceAlert(Student student, LocalDate date, ClassSubject classSubject) {
        String dateStr  = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        String subject  = classSubject.getSubject().getName();
        String title    = "Attendance Alert";
        String body     = String.format("%s was marked ABSENT in %s on %s.",
                student.getUser().getFullName(), subject, dateStr);

        if (student.getParent() != null) {
            persist(student.getParent(), title, body, NotificationType.ATTENDANCE, student.getId());
            logPush(student.getParent(), title, body);
        }
        persist(student.getUser(), title, body, NotificationType.ATTENDANCE, null);
        logPush(student.getUser(), title, body);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAssignmentPublished(Assignment assignment) {
        String title = "New Assignment: " + assignment.getTitle();
        String body  = String.format("A new assignment has been posted in %s. Due: %s",
                assignment.getClassSubject().getSubject().getName(),
                assignment.getDueDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")));

        List<Student> students = studentRepository.findBySchoolClassId(
                assignment.getClassSubject().getSchoolClass().getId());

        for (Student s : students) {
            persist(s.getUser(), title, body, NotificationType.ASSIGNMENT, assignment.getId());
            logPush(s.getUser(), title, body);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendGradeNotification(Submission submission) {
        String title = "Assignment Graded";
        String body  = String.format("Your submission for '%s' has been graded. Score: %d/%d",
                submission.getAssignment().getTitle(),
                submission.getScore(),
                submission.getAssignment().getMaxScore());

        User student = submission.getStudent().getUser();
        persist(student, title, body, NotificationType.GRADE, submission.getId());
        logPush(student, title, body);

        if (submission.getStudent().getParent() != null) {
            persist(submission.getStudent().getParent(), title, body, NotificationType.GRADE, submission.getId());
            logPush(submission.getStudent().getParent(), title, body);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendGeneral(User recipient, String title, String body) {
        persist(recipient, title, body, NotificationType.GENERAL, null);
        logPush(recipient, title, body);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void persist(User recipient, String title, String body, NotificationType type, UUID referenceId) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .body(body)
                .type(type)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notification);
    }

    /**
     * Placeholder for push delivery (FCM, SMS, email, etc.).
     * Replace this method body with your preferred push provider.
     */
    private void logPush(User user, String title, String body) {
        if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
            log.info("[PUSH] To user {} (token={}): {} — {}",
                    user.getId(), user.getFcmToken(), title, body);
        }
    }

    public NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .body(n.getBody())
                .type(n.getType())
                .read(n.isRead())
                .referenceId(n.getReferenceId())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
