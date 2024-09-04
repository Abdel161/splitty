package server.exceptions;

public class InvalidExpenseException extends RuntimeException {

    /**
     * Constructs a new InvalidExpenseException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidExpenseException(String message) {
        super(message);
    }
}
