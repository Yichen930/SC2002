public class ApplicationException extends Exception {
    private String message;

    public ApplicationException(String msg) {
        super(msg);
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}

