package server.exceptions;

public class ExpenseNotFoundException extends RuntimeException {

    /**
     * Constructs a new ExpenseNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ExpenseNotFoundException(String message) {
        super(message);
    }
}
