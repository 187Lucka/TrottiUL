package ca.ulaval.trotti_ul.application.auth;

import java.util.Set;

public record DecodedToken(String subject, Set<String> roles, String technicianId) { }
