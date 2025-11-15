/**
 * Exception thrown when authentication fails during user login.
 * 
 * <p>This exception is thrown in scenarios such as:</p>
 * <ul>
 *   <li>Invalid user ID</li>
 *   <li>Incorrect password</li>
 *   <li>Account not approved (for Company Representatives)</li>
 * </ul>
 * 
 * @version 1.0
 */
public class AuthenticationException extends ApplicationException {
    /**
     * Constructs a new AuthenticationException with the specified error message.
     * 
     * @param msg the detail message explaining why authentication failed
     */
    public AuthenticationException(String msg) {
        super(msg);
    }
}

