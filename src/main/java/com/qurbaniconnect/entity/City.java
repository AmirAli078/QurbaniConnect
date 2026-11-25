package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cities")
@Getter @Setter @NoArgsConstructor
public class City {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "city_name", nullable = false, length = 80)
    private String cityName;
    @Column(name = "state_or_province", nullable = false, length = 80)
    private String stateOrProvince;
    @Column(nullable = false, length = 60)
    private String country = "Pakistan";
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
