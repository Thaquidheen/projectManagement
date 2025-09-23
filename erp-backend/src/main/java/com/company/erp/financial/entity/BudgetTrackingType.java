package com.company.erp.financial.entity;

public enum BudgetTrackingType {
    ALLOCATION("Budget Allocation", "Initial budget allocation to project"),
    SPENDING("Budget Spending", "Money spent from project budget"),
    ADJUSTMENT("Budget Adjustment", "Budget increase or decrease"),
    REALLOCATION("Budget Reallocation", "Budget moved between projects"),
    FORECAST("Budget Forecast", "Projected spending update");

    private final String displayName;
    private final String description;

    BudgetTrackingType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
