import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
        this.visible = visible;
    }


    public boolean isOpenForApplication(LocalDate today) {
        return status == InternshipStatus.APPROVED && 
               visible && 
               today != null &&
               !today.isBefore(openDate) && 
               !today.isAfter(closeDate) &&
               !isFilled();
    }

    public boolean isFilled() {
        return filledSlots >= totalSlots;
    }

    public int remainingSlots() {
        return Math.max(0, totalSlots - filledSlots);
    }

    public void reserveSlot() {
        if (!isFilled()) {
            filledSlots++;
        }
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
