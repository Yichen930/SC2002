/**
 * Exception thrown when errors occur during data access operations.
 * 
 * <p>This exception is thrown when file I/O operations fail, such as:</p>
 * <ul>
 *   <li>File not found</li>
 *   <li>Read/write errors</li>
 *   <li>Data parsing failures</li>
 * </ul>
 * 
 * @version 1.0
 */
public class DataAccessException extends ApplicationException {
    /**
     * Constructs a new DataAccessException with the specified error message.
     * 
     * @param msg the detail message explaining the data access failure
     */
    public DataAccessException(String msg) {
        super(msg);
    }
}

