package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "areas")
@Getter @Setter @NoArgsConstructor
public class Area {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "city_id")
    private City city;
    @Column(name = "area_name", nullable = false, length = 100)
    private String areaName;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
