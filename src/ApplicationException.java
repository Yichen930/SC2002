/**
 * Base exception class for application-related errors in the Internship Placement Management System.
 * 
 * <p>This exception serves as the parent class for all custom exceptions in the system,
 * providing a consistent error handling mechanism across different modules.</p>
 * 
 * @version 1.0
 */
public class ApplicationException extends Exception {
    private String message;

    /**
     * Constructs a new ApplicationException with the specified error message.
     * 
     * @param msg the detail message explaining the exception
     */
    public ApplicationException(String msg) {
        super(msg);
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}

