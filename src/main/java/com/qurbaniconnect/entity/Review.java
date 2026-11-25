package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "reviews")
@Getter @Setter @NoArgsConstructor
public class Review extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id")
    private CustomerProfile customer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "qasai_id")
    private QasaiProfile qasai;
    @Column(nullable = false)
    private int rating;
    @Column(name = "review_text", length = 1000)
    private String reviewText;
    @Column(name = "is_visible", nullable = false)
    private boolean visible = true;
    @Column(name = "moderation_note", length = 300)
    private String moderationNote;
}
