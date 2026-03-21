/*
 * Signals that a requested domain resource could not be found.
 */
package bw.org.bocra.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
