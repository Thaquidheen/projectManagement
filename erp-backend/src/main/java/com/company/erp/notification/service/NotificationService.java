// Enhanced NotificationService.java
package com.company.erp.notification.service;

import com.company.erp.common.service.AuditService;
import com.company.erp.notification.entity.Notification;
import com.company.erp.notification.entity.NotificationPreference;
import com.company.erp.notification.entity.NotificationTemplate;
import com.company.erp.notification.entity.NotificationType;
import com.company.erp.notification.entity.NotificationChannel;
import com.company.erp.notification.entity.NotificationPriority;
import com.company.erp.notification.repository.NotificationRepository;
import com.company.erp.notification.repository.NotificationPreferenceRepository;
import com.company.erp.notification.repository.NotificationTemplateRepository;
import com.company.erp.user.entity.User;
import com.company.erp.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationTemplateRepository templateRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final UserService userService;
    private final AuditService auditService;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository,
                               NotificationTemplateRepository templateRepository,
                               EmailService emailService,
                               SmsService smsService,
                               UserService userService,
                               AuditService auditService) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.templateRepository = templateRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.userService = userService;
        this.auditService = auditService;
    }

    @Async
    public CompletableFuture<Void> sendNotification(Long userId, NotificationType type,
                                                    Map<String, Object> templateData,
                                                    NotificationPriority priority) {
        try {
            User user = userService.getUserById(userId);
            NotificationPreference preference = getOrCreateUserPreference(user);

            // Check if user wants to receive this type of notification
            if (!shouldSendNotification(preference, type, priority)) {
                logger.debug("Notification blocked by user preference: {} - {}", userId, type);
                return CompletableFuture.completedFuture(null);
            }

            // Get template for notification type
            NotificationTemplate template = templateRepository.findByType(type)
                    .orElseThrow(() -> new IllegalArgumentException("Template not found for type: " + type));

            // Create notification record
            Notification notification = createNotification(user, type, template, templateData, priority);
            notification = notificationRepository.save(notification);

            // Send through preferred channels
            sendThroughChannels(user, preference, template, templateData, priority);

            // Mark as sent
            notification.setSent(true);
            notification.setSentDate(LocalDateTime.now());
            notificationRepository.save(notification);

            // Audit log
            auditService.logAction(userId, "NOTIFICATION_SENT", "NOTIFICATION",
                    notification.getId(), "Notification sent: " + type, null, null);

        } catch (Exception e) {
            logger.error("Failed to send notification to user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Notification sending failed", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    private void sendThroughChannels(User user, NotificationPreference preference,
                                     NotificationTemplate template, Map<String, Object> templateData,
                                     NotificationPriority priority) {

        Set<NotificationChannel> channels = getChannelsForPriority(preference, priority);

        for (NotificationChannel channel : channels) {
            switch (channel) {
                case EMAIL:
                    if (user.getEmail() != null && preference.getEmailEnabled()) {
                        sendEmailNotification(user, template, templateData);
                    }
                    break;
                case SMS:
                    if (user.getPhoneNumber() != null && preference.getSmsEnabled()) {
                        sendSmsNotification(user, template, templateData);
                    }
                    break;
                case IN_APP:
                    // Create in-app notification
                    createInAppNotification(user, template, templateData);
                    break;
                case PUSH:
                    // Future: Browser push notifications
                    logger.debug("Push notifications not yet implemented");
                    break;
            }
        }
    }

    private Set<NotificationChannel> getChannelsForPriority(NotificationPreference preference,
                                                            NotificationPriority priority) {
        switch (priority) {
            case CRITICAL:
                return Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                        NotificationChannel.IN_APP, NotificationChannel.PUSH);
            case HIGH:
                return Set.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP);
            case MEDIUM:
                return Set.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP);
            case LOW:
            default:
                return Set.of(NotificationChannel.IN_APP);
        }
    }

    private void sendEmailNotification(User user, NotificationTemplate template,
                                       Map<String, Object> templateData) {
        try {
            String subject = processTemplate(template.getEmailSubject(), templateData);
            String body = processTemplate(template.getEmailBody(), templateData);

            emailService.sendEmail(user.getEmail(), subject, body, template.getIsHtml());
        } catch (Exception e) {
            logger.error("Failed to send email notification to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private void sendSmsNotification(User user, NotificationTemplate template,
                                     Map<String, Object> templateData) {
        try {
            String message = processTemplate(template.getSmsTemplate(), templateData);

            // SMS messages should be concise
            if (message.length() > 160) {
                message = message.substring(0, 157) + "...";
            }

            smsService.sendSms(user.getPhoneNumber(), message);
        } catch (Exception e) {
            logger.error("Failed to send SMS notification to {}: {}", user.getPhoneNumber(), e.getMessage());
        }
    }

    private void createInAppNotification(User user, NotificationTemplate template,
                                         Map<String, Object> templateData) {
        try {
            String title = processTemplate(template.getTitle(), templateData);
            String message = processTemplate(template.getInAppTemplate(), templateData);

            Notification inAppNotification = new Notification();
            inAppNotification.setUser(user);
            inAppNotification.setType(template.getType());
            inAppNotification.setTitle(title);
            inAppNotification.setMessage(message);
            inAppNotification.setChannel(NotificationChannel.IN_APP);
            inAppNotification.setRead(false);
            inAppNotification.setSent(true);
            inAppNotification.setSentDate(LocalDateTime.now());

            notificationRepository.save(inAppNotification);
        } catch (Exception e) {
            logger.error("Failed to create in-app notification for user {}: {}", user.getId(), e.getMessage());
        }
    }

    private String processTemplate(String template, Map<String, Object> data) {
        if (template == null) return "";

        String processed = template;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            processed = processed.replace(placeholder, value);
        }
        return processed;
    }

    private boolean shouldSendNotification(NotificationPreference preference,
                                           NotificationType type, NotificationPriority priority) {

        // Always send critical notifications
        if (priority == NotificationPriority.CRITICAL) {
            return true;
        }

        // Check do not disturb hours
        if (preference.getDoNotDisturbEnabled() && isInDoNotDisturbHours(preference)) {
            return false;
        }

        // Check if type is enabled
        return preference.getEnabledTypes().contains(type);
    }

    private boolean isInDoNotDisturbHours(NotificationPreference preference) {
        LocalTime now = LocalTime.now();
        LocalTime start = preference.getDoNotDisturbStart();
        LocalTime end = preference.getDoNotDisturbEnd();

        if (start == null || end == null) {
            return false;
        }

        if (start.isBefore(end)) {
            return now.isAfter(start) && now.isBefore(end);
        } else {
            // Overnight period (e.g., 22:00 to 06:00)
            return now.isAfter(start) || now.isBefore(end);
        }
    }

    private NotificationPreference getOrCreateUserPreference(User user) {
        return preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> createDefaultPreference(user));
    }

    private NotificationPreference createDefaultPreference(User user) {
        NotificationPreference preference = new NotificationPreference();
        preference.setUser(user);
        preference.setEmailEnabled(true);
        preference.setSmsEnabled(true);
        preference.setInAppEnabled(true);
        preference.setPushEnabled(false);
        preference.setDoNotDisturbEnabled(false);
        preference.setLanguage("en");

        // Enable all notification types by default
        preference.setEnabledTypes(Set.of(NotificationType.values()));

        return preferenceRepository.save(preference);
    }

    private Notification createNotification(User user, NotificationType type,
                                            NotificationTemplate template, Map<String, Object> templateData,
                                            NotificationPriority priority) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(processTemplate(template.getTitle(), templateData));
        notification.setMessage(processTemplate(template.getInAppTemplate(), templateData));
        notification.setPriority(priority);
        notification.setChannel(NotificationChannel.MULTI); // Multiple channels
        notification.setTemplateData(templateData);
        notification.setRead(false);
        notification.setSent(false);

        return notification;
    }

    // Batch notification methods for efficiency
    @Scheduled(fixedRate = 60000) // Every minute
    public void processPendingNotifications() {
        List<Notification> pending = notificationRepository.findBySentFalseAndScheduledTimeBefore(LocalDateTime.now());

        for (Notification notification : pending) {
            try {
                sendNotification(notification.getUser().getId(), notification.getType(),
                        notification.getTemplateData(), notification.getPriority());
            } catch (Exception e) {
                logger.error("Failed to process pending notification {}: {}", notification.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * MON-FRI") // 9 AM on weekdays
    public void sendDailySummaryNotifications() {
        List<User> users = userService.getAllActiveUsers();

        for (User user : users) {
            NotificationPreference preference = getOrCreateUserPreference(user);
            if (preference.getDailySummaryEnabled()) {
                sendDailySummary(user);
            }
        }
    }

    private void sendDailySummary(User user) {
        // Get user's pending items
        Map<String, Object> summaryData = Map.of(
                "userName", user.getFullName(),
                "pendingApprovals", getPendingApprovalsCount(user),
                "budgetAlerts", getBudgetAlertsCount(user),
                "recentActivity", getRecentActivityCount(user)
        );

        sendNotification(user.getId(), NotificationType.DAILY_SUMMARY, summaryData, NotificationPriority.LOW);
    }

    private int getPendingApprovalsCount(User user) {
        // Implementation would query approval service
        return 0; // Placeholder
    }

    private int getBudgetAlertsCount(User user) {
        // Implementation would query budget service
        return 0; // Placeholder
    }

    private int getRecentActivityCount(User user) {
        // Implementation would query recent activities
        return 0; // Placeholder
    }

    // Public API methods
    public void sendQuotationSubmittedNotification(Long managerId, String projectName,
                                                   String quotationId, String submittedBy) {
        Map<String, Object> data = Map.of(
                "projectName", projectName,
                "quotationId", quotationId,
                "submittedBy", submittedBy,
                "actionUrl", "/quotations/" + quotationId
        );

        sendNotification(managerId, NotificationType.QUOTATION_SUBMITTED, data, NotificationPriority.MEDIUM);
    }

    public void sendQuotationApprovedNotification(Long projectManagerId, String projectName,
                                                  String quotationId, String approvedBy) {
        Map<String, Object> data = Map.of(
                "projectName", projectName,
                "quotationId", quotationId,
                "approvedBy", approvedBy,
                "actionUrl", "/quotations/" + quotationId
        );

        sendNotification(projectManagerId, NotificationType.QUOTATION_APPROVED, data, NotificationPriority.MEDIUM);
    }

    public void sendBudgetExceededNotification(Long managerId, String projectName,
                                               String utilizationPercentage) {
        Map<String, Object> data = Map.of(
                "projectName", projectName,
                "utilizationPercentage", utilizationPercentage,
                "actionUrl", "/projects/" + projectName + "/budget"
        );

        sendNotification(managerId, NotificationType.BUDGET_EXCEEDED, data, NotificationPriority.CRITICAL);
    }

    public void sendPaymentCompletedNotification(Long projectManagerId, String projectName,
                                                 String amount, String paymentDate) {
        Map<String, Object> data = Map.of(
                "projectName", projectName,
                "amount", amount,
                "paymentDate", paymentDate
        );

        sendNotification(projectManagerId, NotificationType.PAYMENT_COMPLETED, data, NotificationPriority.LOW);
    }

    // Convenience method for external services
    public void sendEmailNotification(String to, String subject, String body) {
        emailService.sendEmail(to, subject, body, false);
    }

    public void sendSmsNotification(String phoneNumber, String message) {
        smsService.sendSms(phoneNumber, message);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId, int limit) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId,
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setRead(true);
        notification.setReadDate(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalse(userId);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadDate(LocalDateTime.now());
        }

        notificationRepository.saveAll(unreadNotifications);
    }
}