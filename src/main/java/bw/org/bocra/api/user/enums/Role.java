/*
 * Defines the application roles available for BOCRA authorization decisions.
 */
package bw.org.bocra.api.user.enums;

public enum Role {
    ADMIN,
    STAFF,
    APPLICANT,
    REVIEWER,
    SUPER_ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}
