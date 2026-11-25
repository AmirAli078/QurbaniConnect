package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "booking_items")
@Getter @Setter @NoArgsConstructor
public class BookingItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "animal_category_id")
    private AnimalCategory animalCategory;
    @Column(name = "animal_name_snapshot", nullable = false, length = 40)
    private String animalNameSnapshot;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
}
