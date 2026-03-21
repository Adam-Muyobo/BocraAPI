/*
 * Signals that the current request does not have a valid authenticated user context.
 */
package bw.org.bocra.api.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
