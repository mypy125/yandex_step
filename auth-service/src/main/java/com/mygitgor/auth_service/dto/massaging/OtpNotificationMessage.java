package com.mygitgor.auth_service.dto.massaging;

import com.mygitgor.auth_service.dto.USER_ROLE;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OtpNotificationMessage extends NotificationMessage {
    private String to;
    private String otp;
    private String subject;
    private String text;
    private String templateType;
    private USER_ROLE userRole;
    private String purpose;

    public OtpNotificationMessage() {
        super("OTP_NOTIFICATION");
    }

    public OtpNotificationMessage(String to, String otp, String subject, String text,
                                  String templateType, USER_ROLE userRole, String purpose) {
        super("OTP_NOTIFICATION");
        this.to = to;
        this.otp = otp;
        this.subject = subject;
        this.text = text;
        this.templateType = templateType;
        this.userRole = userRole;
        this.purpose = purpose;
    }
}
