package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "admin_price_limits")
@Getter @Setter @NoArgsConstructor
public class AdminPriceLimit extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "animal_category_id", unique = true)
    private AnimalCategory animalCategory;
    @Column(name = "minimum_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal minimumPrice;
    @Column(name = "maximum_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal maximumPrice;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
