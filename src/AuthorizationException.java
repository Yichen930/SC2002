/**
 * Exception thrown when a user attempts to perform an action they are not authorized to perform.
 * 
 * <p>This exception is used to enforce role-based access control, preventing users from
 * accessing functionality outside their permissions (e.g., students trying to approve internships).</p>
 * 
 * @version 1.0
 */
public class AuthorizationException extends ApplicationException {
    /**
     * Constructs a new AuthorizationException with the specified error message.
     * 
     * @param msg the detail message explaining the authorization failure
     */
    public AuthorizationException(String msg) {
        super(msg);
    }
}

