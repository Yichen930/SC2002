/**
 * Utility class for colored console output using ANSI escape codes.
 * 
 * <p>Provides methods to print text in different colors to enhance
 * user interface readability and visual feedback.</p>
 * 
 * @version 1.0
 */
public class ColorUtil {
    // ANSI color codes
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    
    /**
     * Prints success message in green.
     * 
     * @param message the success message to print
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + message + RESET);
    }
    
    /**
     * Prints error message in red.
     * 
     * @param message the error message to print
     */
    public static void printError(String message) {
        System.out.println(RED + message + RESET);
    }
    
    /**
     * Prints warning message in yellow.
     * 
     * @param message the warning message to print
     */
    public static void printWarning(String message) {
        System.out.println(YELLOW + message + RESET);
    }
    
    /**
     * Prints info message in cyan.
     * 
     * @param message the info message to print
     */
    public static void printInfo(String message) {
        System.out.println(CYAN + message + RESET);
    }
    
    /**
     * Prints header message in blue.
     * 
     * @param message the header message to print
     */
    public static void printHeader(String message) {
        System.out.println(BLUE + message + RESET);
    }
    
    /**
     * Returns colored string without printing.
     * 
     * @param message the message to color
     * @param color the ANSI color code
     * @return colored string
     */
    public static String colored(String message, String color) {
        return color + message + RESET;
    }
}
