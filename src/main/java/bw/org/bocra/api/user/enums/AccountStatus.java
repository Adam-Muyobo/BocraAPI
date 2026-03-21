/*
 * Defines high-level lifecycle states for BOCRA user accounts.
 */
package bw.org.bocra.api.user.enums;

public enum AccountStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    PENDING_REVIEW,
    SUSPENDED,
    DISABLED
}
