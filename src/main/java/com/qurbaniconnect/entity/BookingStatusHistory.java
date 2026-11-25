package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "booking_status_history")
@Getter @Setter @NoArgsConstructor
public class BookingStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id")
    private Booking booking;
    @Enumerated(EnumType.STRING) @Column(name = "previous_status", length = 15)
    private BookingStatus previousStatus;
    @Enumerated(EnumType.STRING) @Column(name = "new_status", nullable = false, length = 15)
    private BookingStatus newStatus;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "changed_by")
    private User changedBy;
    @Column(length = 500)
    private String remarks;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
