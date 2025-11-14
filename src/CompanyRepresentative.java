import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Company Representative user in the Internship Placement Management System.
 * 
 * <p>Company Representatives manage internship opportunities for their organizations
 * and are responsible for reviewing student applications. This class extends User
 * and adds company-specific functionality.</p>
 * 
 * <p>Core Capabilities:</p>
 * <ul>
 *   <li>Create up to 5 active internship opportunities (max 10 slots each)</li>
 *   <li>Edit pending internships before staff approval</li>
 *   <li>Delete pending internships that haven't been approved</li>
 *   <li>Toggle visibility of approved internships (show/hide to students)</li>
 *   <li>Review and approve/reject student applications</li>
 *   <li>View all applications for their internships</li>
 * </ul>
 * 
 * <p>Account Lifecycle:</p>
 * <ol>
 *   <li>Registration: Representative registers with company details</li>
 *   <li>Pending: Account awaits approval from Career Center Staff</li>
 *   <li>Approved: Can create internships and review applications</li>
 * </ol>
 * 
 * <p>Business Rules:</p>
 * <ul>
 *   <li>Must be approved by staff before creating internships</li>
 *   <li>Maximum 5 "active" internships (APPROVED, visible, not FILLED)</li>
 *   <li>Can only review applications for their own internships</li>
 *   <li>Cannot toggle visibility of pending/rejected internships</li>
 *   <li>User ID is the company email address (e.g., john@techcorp.com)</li>
 * </ul>
 * 
 * <p>The class maintains a list of created internships and provides
 * methods to manage the 5-internship limit and approval workflow.</p>
 * 
 * @version 1.0
 * @see User
 * @see InternshipOpportunity
 * @see Application
 */
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

    public long countActiveInternships() {
        int count = 0;
        for (InternshipOpportunity opp : createdInternships) {
            if (opp != null && 
                opp.getStatus() == InternshipStatus.APPROVED && 
                opp.isVisible() && 
                !opp.isFilled()) {
                count++;
            }
        }
        return count;
    }

    public boolean canCreateMoreInternships() {
        return countActiveInternships() < 5;
    }

    public void removeInternship(InternshipOpportunity opp) {
        createdInternships.remove(opp);
    }
}