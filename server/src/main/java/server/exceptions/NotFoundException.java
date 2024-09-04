package server.exceptions;

public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with a message.
     *
     * @param message the message of the exception.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
