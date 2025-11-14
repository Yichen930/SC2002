/**
 * Interface for authentication services.
 * 
 * <p>This interface defines the contract for user authentication operations,
 * promoting loose coupling and allowing for multiple authentication
 * implementations (e.g., file-based, database, LDAP).</p>
 * 
 * <p>Design Principles:</p>
 * <ul>
 *   <li>Dependency Inversion: High-level modules depend on this abstraction</li>
 *   <li>Interface Segregation: Focused on authentication concerns only</li>
 *   <li>Open/Closed: New authentication methods can be added without modifying clients</li>
 * </ul>
 * 
 * @version 1.0
 */
public interface AuthServiceInterface {
    /**
     * Authenticates a user with the provided credentials.
     * 
     * @param username the username or user ID
     * @param password the password to verify
     * @return the authenticated User object
     * @throws AuthenticationException if credentials are invalid
     */
    User authenticate(String username, String password) throws AuthenticationException;
    
    /**
     * Logs out the specified user.
     * 
     * @param user the user to log out
     */
    void logout(User user);
    
    /**
     * Sets the user repository for authentication lookups.
     * 
     * @param users the list of users to authenticate against
     */
    void setUsers(java.util.List<User> users);
    
    /**
     * Gets all users in the system.
     * 
     * @return list of all users
     */
    java.util.List<User> getUsers();
}

