package com.company.erp.notification.entity;

public enum NotificationType {
    // Quotation notifications
    QUOTATION_SUBMITTED("Quotation Submitted", "A new quotation has been submitted for approval"),
    QUOTATION_APPROVED("Quotation Approved", "Your quotation has been approved"),
    QUOTATION_REJECTED("Quotation Rejected", "Your quotation has been rejected"),
    QUOTATION_CHANGES_REQUESTED("Changes Requested", "Changes have been requested for your quotation"),

    // Budget notifications
    BUDGET_WARNING("Budget Warning", "Project budget utilization warning"),
    BUDGET_CRITICAL("Budget Critical", "Project approaching budget limit"),
    BUDGET_EXCEEDED("Budget Exceeded", "Project budget has been exceeded"),
    BUDGET_ALLOCATED("Budget Allocated", "New budget has been allocated to project"),

    // Payment notifications
    PAYMENT_PROCESSED("Payment Processed", "Payment has been processed"),
    PAYMENT_COMPLETED("Payment Completed", "Payment has been completed"),
    PAYMENT_FAILED("Payment Failed", "Payment processing failed"),
    BANK_FILE_GENERATED("Bank File Generated", "Bank payment file has been generated"),

    // System notifications
    USER_CREATED("User Created", "New user account has been created"),
    PASSWORD_RESET("Password Reset", "Password reset request"),
    LOGIN_ATTEMPT("Login Attempt", "Suspicious login attempt detected"),
    SYSTEM_MAINTENANCE("System Maintenance", "System maintenance notification"),

    // Project notifications
    PROJECT_ASSIGNED("Project Assigned", "You have been assigned to a new project"),
    PROJECT_COMPLETED("Project Completed", "Project has been completed"),
    PROJECT_OVERDUE("Project Overdue", "Project is overdue"),

    // Approval notifications
    APPROVAL_REQUIRED("Approval Required", "Your approval is required"),
    APPROVAL_OVERDUE("Approval Overdue", "Approval is overdue"),
    BULK_APPROVAL_COMPLETED("Bulk Approval Completed", "Bulk approval has been completed"),

    // Summary notifications
    DAILY_SUMMARY("Daily Summary", "Daily activity summary"),
    WEEKLY_SUMMARY("Weekly Summary", "Weekly activity summary"),
    MONTHLY_SUMMARY("Monthly Summary", "Monthly activity summary");

    private final String displayName;
    private final String description;

    NotificationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}