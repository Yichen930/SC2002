import java.time.LocalDate;

/**
 * Represents a post-confirmation withdrawal request in the Internship Placement Management System.
 * 
 * <p>When a student has already confirmed an internship placement but needs to withdraw,
 * they must submit a withdrawal request for Career Center Staff approval. This ensures
 * proper tracking and allows staff to review legitimate withdrawal reasons.</p>
 * 
 * <p>Workflow:</p>
 * <ol>
 *   <li>Student creates withdrawal request with reason → Status: PENDING</li>
 *   <li>Career Center Staff reviews request</li>
 *   <li>If approved: Application marked WITHDRAWN, slot freed → Status: APPROVED</li>
 *   <li>If rejected: Placement maintained → Status: REJECTED</li>
 * </ol>
 * 
 * <p>Note: Pre-confirmation withdrawals do not require this process and can be done directly.</p>
 * 
 * @version 1.0
 * @see WithdrawalStatus
 * @see CareerCenterStaff
 */
public class WithdrawalRequest {
    private WithdrawalStatus status;
    private String withdrawalReason;
    private LocalDate requestDate;
    private Student applicant;
    private CareerCenterStaff decidedBy;

    /**
     * Constructs a new withdrawal request.
     * 
     * @param applicant the student requesting withdrawal
     * @param reason the reason for withdrawal (e.g., "time clash", "health issues")
     */
    public WithdrawalRequest(Student applicant, String reason) {
        this.applicant = applicant;
        this.withdrawalReason = reason;
        this.requestDate = LocalDate.now();
        this.status = WithdrawalStatus.PENDING;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    public String getWithdrawalReason() {
        return withdrawalReason;
    }

    public void setWithdrawalReason(String reason) {
        this.withdrawalReason = reason;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public Student getApplicant() {
        return applicant;
    }

    public CareerCenterStaff getDecidedBy() {
        return decidedBy;
    }

    public void decide(CareerCenterStaff decider, WithdrawalStatus decision) {
        this.decidedBy = decider;
        this.status = decision;
    }
}

