/*
 * Represents structured error details returned from global exception handling.
 */
package bw.org.bocra.api.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        boolean success,
        String message,
        String errorCode,
        Map<String, String> fieldErrors,
        Instant timestamp
) {
}
