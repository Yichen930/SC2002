import java.time.LocalDate;

public class WithdrawalRequest {
    private WithdrawalStatus status;
    private String withdrawalReason;
    private LocalDate requestDate;
    private Student applicant;
    private CareerCenterStaff decidedBy;

    public WithdrawalRequest(Student applicant, String reason) {
        this.applicant = applicant;
        this.withdrawalReason = reason;
        this.requestDate = LocalDate.now();
        this.status = null;
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

