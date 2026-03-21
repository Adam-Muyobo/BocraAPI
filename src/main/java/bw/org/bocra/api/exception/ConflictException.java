/*
 * Signals that a request conflicts with the current persisted state.
 */
package bw.org.bocra.api.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
