package com.company.erp.notification.entity;

import com.company.erp.common.entity.AuditableEntity;
import com.company.erp.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "notifications")
public class Notification extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationChannel channel;

    @ElementCollection
    @CollectionTable(name = "notification_template_data",
            joinColumns = @JoinColumn(name = "notification_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, Object> templateData;

    @Column(name = "is_read")
    private Boolean read = false;

    @Column(name = "read_date")
    private LocalDateTime readDate;

    @Column(name = "is_sent")
    private Boolean sent = false;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "error_message")
    private String errorMessage;

    // Constructors
    public Notification() {}

    public Notification(User user, NotificationType type, String title, String message) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }

    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }

    public Map<String, Object> getTemplateData() { return templateData; }
    public void setTemplateData(Map<String, Object> templateData) { this.templateData = templateData; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }

    public LocalDateTime getReadDate() { return readDate; }
    public void setReadDate(LocalDateTime readDate) { this.readDate = readDate; }

    public Boolean getSent() { return sent; }
    public void setSent(Boolean sent) { this.sent = sent; }

    public LocalDateTime getSentDate() { return sentDate; }
    public void setSentDate(LocalDateTime sentDate) { this.sentDate = sentDate; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}