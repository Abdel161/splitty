package server.exceptions;

public class InvalidPayloadException extends RuntimeException {

    /**
     * Constructs a new InvalidPayloadException with a message
     *
     * @param message of the exception
     */
    public InvalidPayloadException(String message) {
        super(message);
    }
}
