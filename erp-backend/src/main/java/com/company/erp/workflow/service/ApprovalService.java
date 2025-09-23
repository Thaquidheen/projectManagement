package com.company.erp.workflow.service;

import com.company.erp.common.exception.BusinessException;
import com.company.erp.common.exception.ResourceNotFoundException;
import com.company.erp.common.exception.UnauthorizedAccessException;
import com.company.erp.common.security.UserPrincipal;
import com.company.erp.financial.entity.Quotation;
import com.company.erp.financial.entity.QuotationStatus;
import com.company.erp.financial.repository.QuotationRepository;
import com.company.erp.user.entity.User;
import com.company.erp.user.repository.UserRepository;
import com.company.erp.workflow.dto.request.ApprovalRequest;
import com.company.erp.workflow.dto.request.BulkApprovalRequest;
import com.company.erp.workflow.dto.response.ApprovalResponse;
import com.company.erp.workflow.dto.response.PendingApprovalsResponse;
import com.company.erp.workflow.entity.Approval;
import com.company.erp.workflow.entity.ApprovalStatus;
import com.company.erp.workflow.repository.ApprovalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Process single approval decision
     */
    public ApprovalResponse processApproval(ApprovalRequest request) {
        logger.info("Processing approval for quotation: {} with action: {}",
                request.getQuotationId(), request.getAction());

        UserPrincipal currentUser = getCurrentUser();
        validateApprovalAccess(currentUser);

        Quotation quotation = quotationRepository.findByIdWithProjectAndItems(request.getQuotationId())
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", "id", request.getQuotationId()));

        // Validate quotation can be processed
        if (!quotation.isSubmitted()) {
            throw new BusinessException("QUOTATION_NOT_SUBMITTABLE",
                    "Quotation cannot be processed in " + quotation.getStatus() + " status");
        }

        User approver = getUserById(currentUser.getId());

        // Create or update approval record
        Approval approval = approvalRepository.findByQuotationIdAndApproverId(
                        request.getQuotationId(), currentUser.getId())
                .orElse(new Approval(quotation, approver));

        // Process the approval action
        switch (request.getAction().toUpperCase()) {
            case "APPROVE":
                approval.approve(request.getComments());
                quotation.approve(approver);
                updateProjectBudget(quotation);
                logger.info("Quotation {} approved by {}", quotation.getId(), approver.getFullName());
                break;

            case "REJECT":
                approval.reject(request.getComments());
                quotation.reject(approver, request.getComments());
                logger.info("Quotation {} rejected by {}", quotation.getId(), approver.getFullName());
                break;

            case "CHANGES_REQUESTED":
                approval.requestChanges(request.getComments());
                quotation.setStatus(QuotationStatus.UNDER_REVIEW);
                logger.info("Changes requested for quotation {} by {}", quotation.getId(), approver.getFullName());
                break;

            default:
                throw new BusinessException("INVALID_APPROVAL_ACTION",
                        "Invalid approval action: " + request.getAction());
        }

        approvalRepository.save(approval);
        quotationRepository.save(quotation);

        // TODO: Send notification to project manager
        logger.info("Notification would be sent to project manager for quotation {}", quotation.getId());

        return convertToApprovalResponse(approval);
    }

    /**
     * Process bulk approval decisions
     */
    public List<ApprovalResponse> processBulkApprovals(BulkApprovalRequest request) {
        logger.info("Processing bulk approval for {} quotations with action: {}",
                request.getQuotationIds().size(), request.getAction());

        UserPrincipal currentUser = getCurrentUser();
        validateApprovalAccess(currentUser);

        List<ApprovalResponse> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Long quotationId : request.getQuotationIds()) {
            try {
                ApprovalRequest singleRequest = new ApprovalRequest(quotationId, request.getAction(), request.getComments());
                ApprovalResponse result = processApproval(singleRequest);
                results.add(result);
            } catch (Exception e) {
                logger.warn("Failed to process approval for quotation {}: {}", quotationId, e.getMessage());
                errors.add("Quotation " + quotationId + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            logger.warn("Bulk approval completed with {} errors out of {} requests",
                    errors.size(), request.getQuotationIds().size());
            // Could throw partial success exception or return error details
        }

        logger.info("Bulk approval completed successfully for {} out of {} quotations",
                results.size(), request.getQuotationIds().size());

        return results;
    }

    /**
     * Get pending approvals for current user
     */
    @Transactional(readOnly = true)
    public Page<PendingApprovalsResponse> getPendingApprovals(Pageable pageable) {
        UserPrincipal currentUser = getCurrentUser();
        validateApprovalAccess(currentUser);

        Page<Quotation> pendingQuotations = quotationRepository.findByStatusWithProject(
                QuotationStatus.SUBMITTED, pageable);

        return pendingQuotations.map(this::convertToPendingApprovalResponse);
    }

    /**
     * Get approval history for a quotation
     */
    @Transactional(readOnly = true)
    public List<ApprovalResponse> getApprovalHistory(Long quotationId) {
        UserPrincipal currentUser = getCurrentUser();

        // Validate access to quotation
        Quotation quotation = quotationRepository.findByIdWithProjectAndItems(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", "id", quotationId));

        // Project managers can see history of their own quotations
        // Account managers can see all histories
        if (!currentUser.hasAnyRole("SUPER_ADMIN", "ACCOUNT_MANAGER") &&
                !quotation.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You don't have access to this quotation's approval history");
        }

        List<Approval> approvals = approvalRepository.findByQuotationIdOrderByCreatedDateDesc(quotationId);

        return approvals.stream()
                .map(this::convertToApprovalResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get approval statistics
     */
    @Transactional(readOnly = true)
    public ApprovalStatistics getApprovalStatistics(Long approverId)