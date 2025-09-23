// BudgetVarianceResponse.java
package com.company.erp.financial.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BudgetVarianceResponse {
    private Long projectId;
    private String projectName;
    private BigDecimal allocatedBudget;
    private BigDecimal spentAmount;
    private BigDecimal remainingBudget;
    private BigDecimal varianceAmount;
    private BigDecimal variancePercentage;
    private String varianceStatus; // UNDER, OVER, ON_TRACK
    private LocalDateTime lastCalculated;

    // Category-wise breakdown
    private List<CategoryVariance> categoryVariances;

    // Monthly breakdown
    private List<MonthlyVariance> monthlyVariances;

    // Forecast
    private BigDecimal forecastedSpending;
    private BigDecimal projectedVariance;
    private String riskLevel;

    // Inner classes
    public static class CategoryVariance {
        private String category;
        private BigDecimal budgeted;
        private BigDecimal actual;
        private BigDecimal variance;
        private BigDecimal variancePercentage;

        // Constructors, Getters and Setters
        public CategoryVariance() {}

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getBudgeted() { return budgeted; }
        public void setBudgeted(BigDecimal budgeted) { this.budgeted = budgeted; }

        public BigDecimal getActual() { return actual; }
        public void setActual(BigDecimal actual) { this.actual = actual; }

        public BigDecimal getVariance() { return variance; }
        public void setVariance(BigDecimal variance) { this.variance = variance; }

        public BigDecimal getVariancePercentage() { return variancePercentage; }
        public void setVariancePercentage(BigDecimal variancePercentage) { this.variancePercentage = variancePercentage; }
    }

    public static class MonthlyVariance {
        private String month;
        private BigDecimal budgeted;
        private BigDecimal actual;
        private BigDecimal variance;
        private BigDecimal cumulativeVariance;

        // Constructors, Getters and Setters
        public MonthlyVariance() {}

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }

        public BigDecimal getBudgeted() { return budgeted; }
        public void setBudgeted(BigDecimal budgeted) { this.budgeted = budgeted; }

        public BigDecimal getActual() { return actual; }
        public void setActual(BigDecimal actual) { this.actual = actual; }

        public BigDecimal getVariance() { return variance; }
        public void setVariance(BigDecimal variance) { this.variance = variance; }

        public BigDecimal getCumulativeVariance() { return cumulativeVariance; }
        public void setCumulativeVariance(BigDecimal cumulativeVariance) { this.cumulativeVariance = cumulativeVariance; }
    }

    // Constructors
    public BudgetVarianceResponse() {}

    // Main class Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public BigDecimal getAllocatedBudget() { return allocatedBudget; }
    public void setAllocatedBudget(BigDecimal allocatedBudget) { this.allocatedBudget = allocatedBudget; }

    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }

    public BigDecimal getVarianceAmount() { return varianceAmount; }
    public void setVarianceAmount(BigDecimal varianceAmount) { this.varianceAmount = varianceAmount; }

    public BigDecimal getVariancePercentage() { return variancePercentage; }
    public void setVariancePercentage(BigDecimal variancePercentage) { this.variancePercentage = variancePercentage; }

    public String getVarianceStatus() { return varianceStatus; }
    public void setVarianceStatus(String varianceStatus) { this.varianceStatus = varianceStatus; }

    public LocalDateTime getLastCalculated() { return lastCalculated; }
    public void setLastCalculated(LocalDateTime lastCalculated) { this.lastCalculated = lastCalculated; }

    public List<CategoryVariance> getCategoryVariances() { return categoryVariances; }
    public void setCategoryVariances(List<CategoryVariance> categoryVariances) { this.categoryVariances = categoryVariances; }

    public List<MonthlyVariance> getMonthlyVariances() { return monthlyVariances; }
    public void setMonthlyVariances(List<MonthlyVariance> monthlyVariances) { this.monthlyVariances = monthlyVariances; }

    public BigDecimal getForecastedSpending() { return forecastedSpending; }
    public void setForecastedSpending(BigDecimal forecastedSpending) { this.forecastedSpending = forecastedSpending; }

    public BigDecimal getProjectedVariance() { return projectedVariance; }
    public void setProjectedVariance(BigDecimal projectedVariance) { this.projectedVariance = projectedVariance; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}