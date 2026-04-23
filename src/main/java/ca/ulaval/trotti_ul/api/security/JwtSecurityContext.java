package ca.ulaval.trotti_ul.api.security;


import java.security.Principal;
import java.util.Optional;
import java.util.Set;

import jakarta.ws.rs.core.SecurityContext;

public class JwtSecurityContext implements SecurityContext {

    private final String accountId;
    private final Set<String> roles;
    private final String technicianId;
    private final boolean secure;

    public JwtSecurityContext(String accountId,
                              Set<String> roles,
                              String technicianId,
                              boolean secure) {
        this.accountId = accountId;
        this.roles = roles;
        this.technicianId = technicianId;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> accountId;
    }

    @Override
    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }

    public Optional<String> getTechnicianId() {
        return Optional.ofNullable(technicianId);
    }
}