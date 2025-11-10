import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Represents an Internship Opportunity in the Internship Placement Management System.
 * 
 * <p>This class models a complete internship posting with all necessary details
 * for students to evaluate and apply, and for the system to manage the
 * application lifecycle.</p>
 * 
 * <p>Key Attributes:</p>
 * <ul>
 *   <li>Basic Info: title, description, company name</li>
 *   <li>Requirements: level (BASIC/INTERMEDIATE/ADVANCED), preferred major(s)</li>
 *   <li>Timeline: open date, close date for applications</li>
 *   <li>Capacity: total slots, filled slots (max 10 slots per internship)</li>
 *   <li>Status: PENDING, APPROVED, REJECTED, FILLED</li>
 *   <li>Visibility: boolean flag to show/hide from students</li>
 * </ul>
 * 
 * <p>Lifecycle States:</p>
 * <ol>
 *   <li>PENDING: Created by Company Rep, awaiting staff approval</li>
 *   <li>APPROVED: Approved by staff, can be made visible to students</li>
 *   <li>REJECTED: Rejected by staff, not visible to students</li>
 *   <li>FILLED: All slots are confirmed, automatically set</li>
 * </ol>
 * 
 * <p>Slot Management:</p>
 * <ul>
 *   <li>Slots reserved when students accept placement confirmations</li>
 *   <li>Slots freed when post-confirmation withdrawals are approved</li>
 *   <li>Status automatically changes to FILLED when all slots are taken</li>
 *   <li>Status reverts to APPROVED when a slot is freed from FILLED state</li>
 * </ul>
 * 
 * <p>Visibility Rules:</p>
 * <ul>
 *   <li>Must be APPROVED before visibility can be toggled</li>
 *   <li>Students only see visible, approved, open, non-filled internships</li>
 *   <li>Students who applied can still view even if visibility is off</li>
 * </ul>
 * 
 * <p>Filtering by Preferred Major:</p>
 * If preferred majors are specified, only students with matching majors
 * will see this internship in their browse/apply views.
 * 
 * @version 1.0
 * @see InternshipStatus
 * @see InternshipLevel
 * @see CompanyRepresentative
 */
public class InternshipOpportunity {
    private String title;
    private String description;
    private InternshipLevel level;
    private List<String> preferredMajor;
    private LocalDate openDate;
    private LocalDate closeDate;
    private InternshipStatus status;
    private String companyName;
    private CompanyRepresentative repInCharge;
    private int totalSlots;
    private int filledSlots;
    private boolean visible;

    public InternshipOpportunity(String title, String companyName, CompanyRepresentative repInCharge) {
        this.title = title;
        this.companyName = companyName;
        this.repInCharge = repInCharge;
        this.status = InternshipStatus.PENDING;
        this.visible = true;
        this.filledSlots = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InternshipLevel getLevel() {
        return level;
    }

    public void setLevel(InternshipLevel level) {
        this.level = level;
    }

    public List<String> getPreferredMajor() {
        return preferredMajor;
    }

    public void setPreferredMajor(List<String> preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public CompanyRepresentative getRepInCharge() {
        return repInCharge;
    }

    public void setRepInCharge(CompanyRepresentative repInCharge) {
        this.repInCharge = repInCharge;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getFilledSlots() {
        return filledSlots;
    }

    public void setFilledSlots(int filledSlots) {
        this.filledSlots = filledSlots;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (visible && status != InternshipStatus.APPROVED) {
            throw new IllegalStateException("Cannot make visible before approval");
        }
        this.visible = visible;
    }


    public boolean isOpenForApplication(LocalDate today) {
        if (status != InternshipStatus.APPROVED || !visible || today == null) {
            return false;
        }
        if (openDate == null || closeDate == null) {
            return false;
        }
        return !today.isBefore(openDate) && 
               !today.isAfter(closeDate) &&
               !isFilled();
    }

    public boolean isFilled() {
        return filledSlots >= totalSlots;
    }

    public int remainingSlots() {
        return Math.max(0, totalSlots - filledSlots);
    }

    public boolean reserveSlot() {
        if (filledSlots < totalSlots) {
            filledSlots++;
            if (filledSlots >= totalSlots) {
                this.status = InternshipStatus.FILLED;
            }
            return true;
        }
        return false;
    }

    public boolean freeSlot() {
        if (filledSlots > 0) {
            filledSlots--;
            if (status == InternshipStatus.FILLED) {
                status = InternshipStatus.APPROVED; // space reopened
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternshipOpportunity that = (InternshipOpportunity) o;
        return Objects.equals(title, that.title) && 
               Objects.equals(companyName, that.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, companyName);
    }

    @Override
    public String toString() {
        return "InternshipOpportunity{" +
                "title='" + title + '\'' +
                ", company='" + companyName + '\'' +
                ", level=" + level +
                ", status=" + status +
                ", slots=" + filledSlots + "/" + totalSlots +
                '}';
    }
}
