package com.mygitgor.auth_service.dto.massaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class NotificationMessage {
    private String id;
    private String type;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;

    public NotificationMessage(String type) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
}
