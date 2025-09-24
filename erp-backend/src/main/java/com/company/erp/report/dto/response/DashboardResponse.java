package com.company.erp.report.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardResponse {

    // User Information
    private Long userId;
    private String userName;
    private String userRole;

    // Key Metrics
    private Long activeProjectsCount;
    private Long pendingApprovalsCount;
    private Long totalQuotationsCount;
    private Long myProjectsCount;
    private Long myQuotationsCount;
    private Long pendingTasksCount;

    // Financial Summary (for admin users)
    private BigDecimal totalBudgetAllocated;
    private BigDecimal totalSpentAmount;
    private BigDecimal budgetUtilizationPercentage;
    private BigDecimal myBudgetUtilization;

    // System Information
    private String systemStatus;
    private BigDecimal systemHealthScore;
    private Long totalUsers;
    private Long activeUsers;

    // Team Information (for managers)
    private Long myTeamSize;
    private List<Map<String, Object>> teamMembers;

    // Recent Activities
    private List<Map<String, Object>> recentActivities;
    private List<Map<String, Object>> upcomingTasks;
    private List<Map<String, Object>> notifications;

    // Quick Stats
    private Map<String, Object> quickStats;
    private List<Map<String, Object>> alerts;

    // Widget Data
    private Map<String, Object> widgets;

    // Metadata
    private LocalDateTime generatedAt;
    private String dashboardType; // ADMIN, PROJECT_MANAGER, USER

    // Constructors
    public DashboardResponse() {
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public Long getActiveProjectsCount() { return activeProjectsCount; }
    public void setActiveProjectsCount(Long activeProjectsCount) { this.activeProjectsCount = activeProjectsCount; }

    public Long getPendingApprovalsCount() { return pendingApprovalsCount; }
    public void setPendingApprovalsCount(Long pendingApprovalsCount) { this.pendingApprovalsCount = pendingApprovalsCount; }

    public Long getTotalQuotationsCount() { return totalQuotationsCount; }
    public void setTotalQuotationsCount(Long totalQuotationsCount) { this.totalQuotationsCount = totalQuotationsCount; }

    public Long getMyProjectsCount() { return myProjectsCount; }
    public void setMyProjectsCount(Long myProjectsCount) { this.myProjectsCount = myProjectsCount; }

    public Long getMyQuotationsCount() { return myQuotationsCount; }
    public void setMyQuotationsCount(Long myQuotationsCount) { this.myQuotationsCount = myQuotationsCount; }

    public Long getPendingTasksCount() { return pendingTasksCount; }
    public void setPendingTasksCount(Long pendingTasksCount) { this.pendingTasksCount = pendingTasksCount; }

    public BigDecimal getTotalBudgetAllocated() { return totalBudgetAllocated; }
    public void setTotalBudgetAllocated(BigDecimal totalBudgetAllocated) { this.totalBudgetAllocated = totalBudgetAllocated; }

    public BigDecimal getTotalSpentAmount() { return totalSpentAmount; }
    public void setTotalSpentAmount(BigDecimal totalSpentAmount) { this.totalSpentAmount = totalSpentAmount; }

    public BigDecimal getBudgetUtilizationPercentage() { return budgetUtilizationPercentage; }
    public void setBudgetUtilizationPercentage(BigDecimal budgetUtilizationPercentage) { this.budgetUtilizationPercentage = budgetUtilizationPercentage; }

    public BigDecimal getMyBudgetUtilization() { return myBudgetUtilization; }
    public void setMyBudgetUtilization(BigDecimal myBudgetUtilization) { this.myBudgetUtilization = myBudgetUtilization; }

    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }

    public BigDecimal getSystemHealthScore() { return systemHealthScore; }
    public void setSystemHealthScore(BigDecimal systemHealthScore) { this.systemHealthScore = systemHealthScore; }

    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }

    public Long getMyTeamSize() { return myTeamSize; }
    public void setMyTeamSize(Long myTeamSize) { this.myTeamSize = myTeamSize; }

    public List<Map<String, Object>> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<Map<String, Object>> teamMembers) { this.teamMembers = teamMembers; }

    public List<Map<String, Object>> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<Map<String, Object>> recentActivities) { this.recentActivities = recentActivities; }

    public List<Map<String, Object>> getUpcomingTasks() { return upcomingTasks; }
    public void setUpcomingTasks(List<Map<String, Object>> upcomingTasks) { this.upcomingTasks = upcomingTasks; }

    public List<Map<String, Object>> getNotifications() { return notifications; }
    public void setNotifications(List<Map<String, Object>> notifications) { this.notifications = notifications; }

    public Map<String, Object> getQuickStats() { return quickStats; }
    public void setQuickStats(Map<String, Object> quickStats) { this.quickStats = quickStats; }

    public List<Map<String, Object>> getAlerts() { return alerts; }
    public void setAlerts(List<Map<String, Object>> alerts) { this.alerts = alerts; }

    public Map<String, Object> getWidgets() { return widgets; }
    public void setWidgets(Map<String, Object> widgets) { this.widgets = widgets; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getDashboardType() { return dashboardType; }
    public void setDashboardType(String dashboardType) { this.dashboardType = dashboardType; }

    @Override
    public String toString() {
        return "DashboardResponse{" +
                "userId=" + userId +
                ", activeProjectsCount=" + activeProjectsCount +
                ", pendingApprovalsCount=" + pendingApprovalsCount +
                ", generatedAt=" + generatedAt +
                '}';
    }
}
