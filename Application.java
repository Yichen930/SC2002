import java.time.LocalDate;
import java.util.Objects;

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
