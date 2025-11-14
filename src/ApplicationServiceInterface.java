import java.util.List;

/**
 * Interface for application management services.
 * 
 * <p>This interface defines the contract for internship application operations,
 * including the two-step approval process and withdrawal handling.</p>
 * 
 * <p>Application Lifecycle:</p>
 * <ol>
 *   <li>Student applies → PENDING</li>
 *   <li>Company Rep approves → ACCEPTED (awaiting student confirmation)</li>
 *   <li>Student accepts → Slot reserved, other apps withdrawn</li>
 *   <li>Withdrawal requests → Requires staff approval</li>
 * </ol>
 * 
 * @version 1.0
 */
public interface ApplicationServiceInterface {
    /**
     * Student accepts an approved application (confirms placement).
     * 
     * <p>This reserves a slot on the internship and auto-withdraws
     * all other active applications from the same student.</p>
     * 
     * @param student the student accepting the placement
     * @param app the application to accept
     * @return true if acceptance successful, false otherwise
     * @throws ApplicationException if validation fails or no slots available
     */
    boolean accept(Student student, Application app) throws ApplicationException;
    
    /**
     * Student rejects an application (withdraws before confirmation).
     * 
     * @param student the student rejecting
     * @param app the application to reject
     * @return true if rejection successful, false otherwise
     */
    boolean reject(Student student, Application app);
    
    /**
     * Company Representative reviews an application.
     * 
     * @param internshipOpportunity the internship being applied to
     * @param app the application to review
     * @param decision the decision (ACCEPTED or REJECTED)
     */
    void review(InternshipOpportunity internshipOpportunity, Application app, 
                ApplicationStatus decision);
    
    /**
     * Student requests withdrawal of an application.
     * 
     * <p>Creates a WithdrawalRequest that requires staff approval.</p>
     * 
     * @param app the application to withdraw
     */
    void requestForWithdrawal(Application app);
    
    /**
     * Career Center Staff approves or rejects a withdrawal request.
     * 
     * @param staff the staff member making the decision
     * @param app the application with withdrawal request
     * @param decision the decision (APPROVED or REJECTED)
     */
    void decideWithdrawal(CareerCenterStaff staff, Application app, WithdrawalStatus decision);
    
    /**
     * Adds a new application to the system.
     * 
     * @param app the application to add
     */
    void addApplication(Application app);
    
    /**
     * Gets all applications in the system.
     * 
     * @return list of all applications
     */
    List<Application> getAllApplications();
}

