import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Interface for internship management services.
 * 
 * <p>This interface defines the contract for internship opportunity operations,
 * including creation, approval workflow, filtering, and visibility management.</p>
 * 
 * <p>Business Rules Enforced:</p>
 * <ul>
 *   <li>Company Reps can create up to 5 active internships</li>
 *   <li>Max 10 slots per internship</li>
 *   <li>Must be approved by staff before visible to students</li>
 *   <li>Visibility can only be toggled on approved internships</li>
 * </ul>
 * 
 * @version 1.0
 */
public interface InternshipServiceInterface {
    /**
     * Creates a new internship opportunity.
     * 
     * @param staff the staff member creating (or null if by rep)
     * @param app the internship opportunity to create
     * @return true if creation successful, false otherwise
     */
    boolean create(CareerCenterStaff staff, InternshipOpportunity app);
    
    /**
     * Approves a pending internship opportunity.
     * 
     * @param staff the staff member approving
     * @param app the internship to approve
     */
    void approve(CareerCenterStaff staff, InternshipOpportunity app);
    
    /**
     * Rejects a pending internship opportunity.
     * 
     * @param staff the staff member rejecting
     * @param app the internship to reject
     */
    void reject(CareerCenterStaff staff, InternshipOpportunity app);
    
    /**
     * Toggles the visibility of an internship opportunity.
     * 
     * @param opp the internship to toggle
     * @throws IllegalStateException if internship is not approved
     */
    void toggleVisibility(InternshipOpportunity opp);
    
    /**
     * Displays details of an internship opportunity.
     * 
     * @param opp the internship to display
     */
    void showInternshipOpportunity(InternshipOpportunity opp);
    
    /**
     * Gets all visible internship opportunities.
     * 
     * @return set of visible, approved internships
     */
    Set<InternshipOpportunity> getVisibleOpportunities();
    
    /**
     * Gets and displays all internship opportunities.
     * 
     * @return set of all internships
     */
    Set<InternshipOpportunity> showAllInternshipOpportunities();
    
    /**
     * Adds an internship opportunity to the system.
     * 
     * @param opp the internship to add
     */
    void addOpportunity(InternshipOpportunity opp);
    
    /**
     * Gets all internship opportunities.
     * 
     * @return list of all internships
     */
    List<InternshipOpportunity> getAllOpportunities();
    
    /**
     * Gets internships open for application on a given date.
     * 
     * @param today the date to check
     * @return list of open internships
     */
    List<InternshipOpportunity> getOpenOpportunities(LocalDate today);
    
    /**
     * Filters internships by level.
     * 
     * @param opps list of internships to filter
     * @param level the level to filter by
     * @return filtered list
     */
    List<InternshipOpportunity> filterByLevel(List<InternshipOpportunity> opps, InternshipLevel level);
    
    /**
     * Filters internships by date range.
     * 
     * @param opps list of internships to filter
     * @param today the date to check against
     * @return filtered list
     */
    List<InternshipOpportunity> filterByDateRange(List<InternshipOpportunity> opps, LocalDate today);
    
    /**
     * Filters to only visible internships.
     * 
     * @param opps list of internships to filter
     * @return filtered list of visible internships
     */
    List<InternshipOpportunity> filterVisible(List<InternshipOpportunity> opps);
    
    /**
     * Deletes a pending internship opportunity.
     * 
     * @param rep the representative deleting
     * @param opp the internship to delete
     * @return true if deletion successful, false otherwise
     */
    boolean deleteOpportunity(CompanyRepresentative rep, InternshipOpportunity opp);
    
    /**
     * Removes an internship opportunity from the system.
     * 
     * @param opp the internship to remove
     */
    void removeOpportunity(InternshipOpportunity opp);
    
    /**
     * Gets filtered internships for a specific student.
     * 
     * <p>Applies all business rules: visibility, approval status, dates,
     * level eligibility, preferred major matching, not filled.</p>
     * 
     * @param student the student to filter for
     * @return list of eligible internships for the student
     */
    List<InternshipOpportunity> getFilteredOpportunities(Student student);
    
    /**
     * Gets all internship opportunities in the system.
     * 
     * @return list of all internships
     */
    List<InternshipOpportunity> getOpportunities();
}

