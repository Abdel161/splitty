package server.exceptions;

public class SystemErrorException extends RuntimeException {

    /**
     * Constructs a new SystemErrorException with a message.
     * This exception is thrown to indicate that the something went wrong while retrieving something from the db.
     *
     * @param message the message corresponding to the exception.
     */
    public SystemErrorException(String message) {
        super(message);
    }
}
