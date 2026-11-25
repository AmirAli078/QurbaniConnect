package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "roles")
@Getter @Setter @NoArgsConstructor
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING) @Column(name = "role_name", nullable = false, unique = true, length = 30)
    private RoleName roleName;
}
