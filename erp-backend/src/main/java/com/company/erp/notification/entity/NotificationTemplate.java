// NotificationTemplate.java
package com.company.erp.notification.entity;

import com.company.erp.common.entity.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "notification_templates")
public class NotificationTemplate extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = true)
    private NotificationType type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    @Column(name = "sms_template")
    private String smsTemplate;

    @Column(name = "in_app_template", columnDefinition = "TEXT")
    private String inAppTemplate;

    @Column(name = "push_template")
    private String pushTemplate;

    @Column(name = "is_html")
    private Boolean isHtml = false;

    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "language", length = 5)
    private String language = "en";

    // Constructors
    public NotificationTemplate() {}

    public NotificationTemplate(NotificationType type, String name, String title) {
        this.type = type;
        this.name = name;
        this.title = title;
    }

    // Getters and Setters
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEmailSubject() { return emailSubject; }
    public void setEmailSubject(String emailSubject) { this.emailSubject = emailSubject; }

    public String getEmailBody() { return emailBody; }
    public void setEmailBody(String emailBody) { this.emailBody = emailBody; }

    public String getSmsTemplate() { return smsTemplate; }
    public void setSmsTemplate(String smsTemplate) { this.smsTemplate = smsTemplate; }

    public String getInAppTemplate() { return inAppTemplate; }
    public void setInAppTemplate(String inAppTemplate) { this.inAppTemplate = inAppTemplate; }

    public String getPushTemplate() { return pushTemplate; }
    public void setPushTemplate(String pushTemplate) { this.pushTemplate = pushTemplate; }

    public Boolean getIsHtml() { return isHtml; }
    public void setIsHtml(Boolean isHtml) { this.isHtml = isHtml; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
