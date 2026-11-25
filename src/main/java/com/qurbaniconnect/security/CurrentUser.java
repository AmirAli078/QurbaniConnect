package com.qurbaniconnect.security;

import com.qurbaniconnect.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {}

    public static UserPrincipal get() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof UserPrincipal p)) {
            throw new UnauthorizedException("Authentication required");
        }
        return p;
    }

    public static Long id() { return get().getId(); }

    public static boolean hasRole(String role) {
        return get().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
