package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "qasai_animal_prices")
@Getter @Setter @NoArgsConstructor
public class QasaiAnimalPrice extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "qasai_id")
    private QasaiProfile qasai;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "animal_category_id")
    private AnimalCategory animalCategory;
    @Column(name = "service_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal servicePrice;
    @Column(name = "is_available", nullable = false)
    private boolean available = true;
}
