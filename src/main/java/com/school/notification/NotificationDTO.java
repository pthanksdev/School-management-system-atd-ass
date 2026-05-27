package com.school.notification;

import com.school.common.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private UUID id;
    private String title;
    private String body;
    private NotificationType type;
    private boolean read;
    private UUID referenceId;
    private LocalDateTime createdAt;
}
