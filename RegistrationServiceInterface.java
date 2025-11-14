import java.util.List;

/**
 * Interface for user registration services.
 * 
 * <p>This interface defines the contract for user registration and approval
 * operations, decoupling the registration logic from its implementation.</p>
 * 
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Register new Company Representatives</li>
 *   <li>Approve/reject Company Representative accounts</li>
 *   <li>Manage password changes</li>
 * </ul>
 * 
 * @version 1.0
 */
public interface RegistrationServiceInterface {
    /**
     * Registers a new Company Representative.
     * 
     * @param name representative's name
     * @param email company email (used as User ID)
     * @param companyName name of the company
     * @param department department within company
     * @param position job position
     * @param password initial password
     * @return true if registration successful, false otherwise
     */
    boolean registerCompanyRepresentative(String name, String email, String companyName, 
                                         String department, String position, String password);
    
    /**
     * Approves a pending Company Representative account.
     * 
     * @param staff the staff member performing the approval
     * @param rep the representative to approve
     * @return true if approval successful, false otherwise
     */
    boolean approveRepresentative(CareerCenterStaff staff, CompanyRepresentative rep);
    
    /**
     * Rejects a pending Company Representative account.
     * 
     * @param staff the staff member performing the rejection
     * @param rep the representative to reject
     * @return true if rejection successful, false otherwise
     */
    boolean rejectRepresentative(CareerCenterStaff staff, CompanyRepresentative rep);
    
    /**
     * Changes a user's password.
     * 
    * @param userId the user ID (student ID or email for company representatives)
     * @param oldPassword current password for verification
     * @param newPassword new password to set
     * @return true if password changed successfully, false otherwise
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * Sets the user repository for registration operations.
     * 
     * @param users the list of users
     */
    void setUsers(List<User> users);
    
    /**
     * Gets all registered Company Representatives.
     * 
     * @return list of all Company Representatives
     */
    List<CompanyRepresentative> getRepresentatives();
}

