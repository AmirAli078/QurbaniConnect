package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;
    @Column(length = 160, unique = true)
    private String email;
    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;
    @Column(nullable = false, length = 100)
    private String password;
    @Enumerated(EnumType.STRING) @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;
    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
