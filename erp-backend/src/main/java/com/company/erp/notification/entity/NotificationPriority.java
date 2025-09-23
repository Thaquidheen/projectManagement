package com.company.erp.notification.entity;

public enum NotificationPriority {
    LOW(1, "Low Priority", "#28a745"),
    MEDIUM(2, "Medium Priority", "#ffc107"),
    HIGH(3, "High Priority", "#fd7e14"),
    CRITICAL(4, "Critical", "#dc3545");

    private final int level;
    private final String displayName;
    private final String colorCode;

    NotificationPriority(int level, String displayName, String colorCode) {
        this.level = level;
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public int getLevel() { return level; }
    public String getDisplayName() { return displayName; }
    public String getColorCode() { return colorCode; }

    public boolean isHigherThan(NotificationPriority other) {
        return this.level > other.level;
    }
}