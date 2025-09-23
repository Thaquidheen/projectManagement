package com.company.erp.notification.entity;

public enum NotificationChannel {
    EMAIL("Email", "email", true),
    SMS("SMS", "sms", true),
    IN_APP("In-App", "in_app", true),
    PUSH("Push Notification", "push", false),
    MULTI("Multiple Channels", "multi", true);

    private final String displayName;
    private final String code;
    private final boolean enabled;

    NotificationChannel(String displayName, String code, boolean enabled) {
        this.displayName = displayName;
        this.code = code;
        this.enabled = enabled;
    }

    public String getDisplayName() { return displayName; }
    public String getCode() { return code; }
    public boolean isEnabled() { return enabled; }
}