package com.mygitgor.auth_service.dto.massaging;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmailNotificationMessage extends NotificationMessage {
    private String to;
    private String subject;
    private String text;
    private String templateName;
    private Map<String, Object> templateVariables;
    private List<String> cc;
    private List<String> bcc;

    public EmailNotificationMessage() {
        super("EMAIL_NOTIFICATION");
    }

    public EmailNotificationMessage(String to, String subject, String text) {
        super("EMAIL_NOTIFICATION");
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.templateVariables = new HashMap<>();
        this.cc = new ArrayList<>();
        this.bcc = new ArrayList<>();
    }
}