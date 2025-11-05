package com.mygitgor.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationMessage {
    private String email;
    private String subject;
    private String templateName;
    private Map<String, Object> templateData;
    private LocalDateTime timestamp;
}
