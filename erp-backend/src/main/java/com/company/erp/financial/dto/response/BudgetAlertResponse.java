// BudgetAlertResponse.java
package com.company.erp.financial.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetAlertResponse {
    private Long projectId;
    private String projectName;
    private String projectManager;
    private BigDecimal allocatedBudget;
    private BigDecimal spentAmount;
    private BigDecimal remainingBudget;
    private BigDecimal utilizationPercentage;
    private BigDecimal variancePercentage;
    private String alertLevel; // WARNING, CRITICAL, OVER_BUDGET
    private String alertMessage;
    private LocalDateTime lastUpdated;
    private boolean isOverBudget;
    private boolean requiresAttention;

    // Constructors
    public BudgetAlertResponse() {}

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }

    public BigDecimal getAllocatedBudget() { return allocatedBudget; }
    public void setAllocatedBudget(BigDecimal allocatedBudget) { this.allocatedBudget = allocatedBudget; }

    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }

    public BigDecimal getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(BigDecimal utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }

    public BigDecimal getVariancePercentage() { return variancePercentage; }
    public void setVariancePercentage(BigDecimal variancePercentage) { this.variancePercentage = variancePercentage; }

    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public boolean isOverBudget() { return isOverBudget; }
    public void setOverBudget(boolean overBudget) { this.isOverBudget = overBudget; }

    public boolean isRequiresAttention() { return requiresAttention; }
    public void setRequiresAttention(boolean requiresAttention) { this.requiresAttention = requiresAttention; }
}
