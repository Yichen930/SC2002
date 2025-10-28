import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private boolean isApproved;

    private final List<InternshipOpportunity> createdInternships = new ArrayList<>();

    public CompanyRepresentative(String id, String name, String companyName) {
        setId(id);
        setName(name);
        this.companyName = companyName;
        this.isApproved = false;
    }

    public boolean getIsApproved() {
        return isApproved;
    }
    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String dept) {
        this.department = dept;
    }

    public String getPosition() {
        return position;
    }
    public void setPosition(String pos) {
        this.position = pos;
    }

    public List<InternshipOpportunity> getCreatedInternships() {
        return Collections.unmodifiableList(createdInternships);
    }


    public void createInternship(InternshipOpportunity draft) {
        if (draft == null) return;
        createdInternships.add(draft);
    }
}