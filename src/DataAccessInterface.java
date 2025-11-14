import java.util.List;

/**
 * Interface for data access operations.
 * 
 * <p>This interface defines the contract for loading data from text files.
 * Per assignment requirements, the system supports read-only data loading
 * (no persistence/saving). All data exists in memory during runtime.</p>
 * 
 * <p>Design Benefits:</p>
 * <ul>
 *   <li>Separation of Concerns: Data access logic separate from business logic</li>
 *   <li>Testability: Easy to mock for unit testing</li>
 *   <li>Flexibility: Can swap implementations (different file formats) without changing clients</li>
 *   <li>Interface Segregation: Only includes operations actually needed (load-only)</li>
 * </ul>
 * 
 * @version 1.0
 */
public interface DataAccessInterface {
    /**
     * Loads users from persistent storage.
     * 
     * @param filename the name/path of the data source
     * @return list of loaded users
     * @throws DataAccessException if loading fails
     */
    List<User> loadUsers(String filename) throws DataAccessException;
    
    /**
     * Loads internship opportunities from persistent storage.
     * 
     * @param filename the name/path of the data source
     * @param users list of users to link Company Representatives
     * @return list of loaded internships
     * @throws DataAccessException if loading fails
     */
    List<InternshipOpportunity> loadInternships(String filename, List<User> users) throws DataAccessException;
    
    /**
     * Loads applications from persistent storage.
     * 
     * @param filename the name/path of the data source
     * @param users list of users to link Students
     * @param internships list of internships to link opportunities
     * @return list of loaded applications
     * @throws DataAccessException if loading fails
     */
    List<Application> loadApplications(String filename, List<User> users, 
                                      List<InternshipOpportunity> internships) throws DataAccessException;
}

