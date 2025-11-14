import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * System logger for recording user actions and system events.
 * 
 * <p>Logs are written to logs/system.log with timestamps and user context.
 * This provides audit trail and debugging capabilities.</p>
 * 
 * @version 1.0
 */
public class SystemLogger {
    private static final String LOG_FILE = "logs/system.log";
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs a user action.
     * 
     * @param action the action type (LOGIN, LOGOUT, APPLICATION, etc.)
     * @param userId the user ID performing the action
     * @param details additional details about the action
     */
    public static void log(String action, String userId, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] %s: User %s - %s", 
            timestamp, action, userId, details);
        writeToFile(logEntry);
    }
    
    /**
     * Logs a system event without user context.
     * 
     * @param action the action type
     * @param details event details
     */
    public static void logSystem(String action, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] %s: %s", 
            timestamp, action, details);
        writeToFile(logEntry);
    }
    
    /**
     * Writes log entry to file.
     * 
     * @param logEntry the formatted log entry
     */
    private static void writeToFile(String logEntry) {
        try {
            // Create logs directory if it doesn't exist
            new java.io.File("logs").mkdirs();
            
            // Append to log file
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(logEntry);
            }
        } catch (IOException e) {
            // Silently fail - don't disrupt user experience
            System.err.println("Warning: Could not write to log file");
        }
    }
}
