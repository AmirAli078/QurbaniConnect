package com.qurbaniconnect.security;

import com.qurbaniconnect.entity.User;
import com.qurbaniconnect.enums.AccountStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserPrincipal implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getEmail() != null ? user.getEmail() : user.getPhoneNumber();
        this.password = user.getPassword();
        this.enabled = user.getAccountStatus() == AccountStatus.ACTIVE;
        this.authorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getRoleName().name())).toList();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
