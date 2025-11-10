import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a student's application to an internship opportunity.
 * 
 * <p>This class models the complete application lifecycle from submission
 * through review, acceptance, and potential withdrawal. It serves as the
 * central link between Students and InternshipOpportunities.</p>
 * 
 * <p>Application Lifecycle:</p>
 * <ol>
 *   <li>PENDING: Student submits application, awaiting company review</li>
 *   <li>ACCEPTED: Company Rep approves, awaiting student confirmation</li>
 *   <li>CONFIRMED: Student accepts placement (slot reserved, other apps withdrawn)</li>
 *   <li>REJECTED: Company Rep rejects application</li>
 *   <li>WITHDRAWN: Student withdraws (pre or post confirmation)</li>
 * </ol>
 * 
 * <p>Two-Step Approval Process:</p>
 * <ol>
 *   <li>Company Representative reviews and approves → Status: ACCEPTED</li>
 *   <li>Student accepts placement confirmation → Slot reserved</li>
 * </ol>
 * 
 * <p>Withdrawal Handling:</p>
 * <ul>
 *   <li>Pre-Confirmation: Direct withdrawal, no approval needed</li>
 *   <li>Post-Confirmation: Creates WithdrawalRequest, requires Staff approval</li>
 *   <li>Approved withdrawals free the internship slot</li>
 * </ul>
 * 
 * <p>Status Terminology:</p>
 * The application uses internal enum values (PENDING, ACCEPTED, REJECTED, WITHDRAWN)
 * but displays user-friendly text to students ("Pending", "Successful", 
 * "Unsuccessful", "Withdrawn") as per assignment requirements.
 * 
 * <p>Timestamps:</p>
 * The application tracks creation and last update dates using LocalDate.
 * These are used for auditing and reporting purposes.
 * 
 * @version 1.0
 * @see ApplicationStatus
 * @see Student
 * @see InternshipOpportunity
 * @see WithdrawalRequest
 */
public class Application {
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Student student;
    private InternshipOpportunity opp;
    private WithdrawalRequest withdrawal;

    public Application(Student student, InternshipOpportunity opp) {
        this.student = student;
        this.opp = opp;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }

    public Student getStudent() {
        return student;
    }

    public InternshipOpportunity getOpportunity() {
        return opp;
    }

    public boolean isSuccessful() {
        return status == ApplicationStatus.ACCEPTED;
    }

    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }

    public boolean isConfirmed() {
        return status == ApplicationStatus.ACCEPTED;
    }

    public boolean isActive() {
        return status == ApplicationStatus.PENDING || status == ApplicationStatus.ACCEPTED;
    }

    public WithdrawalRequest getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(WithdrawalRequest withdrawal) {
        this.withdrawal = withdrawal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(student, that.student) && 
               Objects.equals(opp, that.opp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, opp);
    }

    @Override
    public String toString() {
        return "Application{" +
                "status=" + status +
                ", student=" + (student != null ? student.getName() : "null") +
                ", opportunity=" + (opp != null ? opp.getTitle() : "null") +
                '}';
    }
}
