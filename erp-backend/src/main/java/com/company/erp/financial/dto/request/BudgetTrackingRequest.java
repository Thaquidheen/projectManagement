package com.company.erp.financial.dto.request;

import com.company.erp.financial.entity.BudgetTrackingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetTrackingRequest {

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Tracking type is required")
    private BudgetTrackingType trackingType;

    private String referenceType;
    private Long referenceId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String currency = "SAR";
    private String description;
    private String category;
    private LocalDateTime transactionDate;

    // Constructors
    public BudgetTrackingRequest() {}

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public BudgetTrackingType getTrackingType() { return trackingType; }
    public void setTrackingType(BudgetTrackingType trackingType) { this.trackingType = trackingType; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}
