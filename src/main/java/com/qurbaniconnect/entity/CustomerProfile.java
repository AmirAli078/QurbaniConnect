package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "customer_profiles")
@Getter @Setter @NoArgsConstructor
public class CustomerProfile extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", unique = true)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "city_id")
    private City city;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "area_id")
    private Area area;
    @Column(name = "complete_address", length = 300)
    private String completeAddress;
    @Column(name = "profile_photo", length = 255)
    private String profilePhoto;
}
