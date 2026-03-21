/*
 * Signals that a client request is invalid for the current business rules.
 */
package bw.org.bocra.api.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
