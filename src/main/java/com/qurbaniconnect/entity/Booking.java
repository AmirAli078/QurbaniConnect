package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "bookings")
@Getter @Setter @NoArgsConstructor
public class Booking extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "booking_reference", nullable = false, unique = true, length = 30)
    private String bookingReference;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id")
    private CustomerProfile customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "qasai_id")
    private QasaiProfile qasai;
    @Enumerated(EnumType.STRING) @Column(name = "booking_type", nullable = false, length = 20)
    private BookingType bookingType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "city_id")
    private City city;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "area_id")
    private Area area;
    @Column(name = "service_address", nullable = false, length = 300)
    private String serviceAddress;
    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;
    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "availability_id")
    private QasaiAvailability availability;
    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(name = "price_status", nullable = false, length = 15)
    private PriceStatus priceStatus = PriceStatus.FINAL;
    @Enumerated(EnumType.STRING) @Column(name = "booking_status", nullable = false, length = 15)
    private BookingStatus bookingStatus = BookingStatus.PENDING;
    @Column(name = "customer_notes", length = 500)
    private String customerNotes;
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingItem> items = new ArrayList<>();

    public void addItem(BookingItem item) { item.setBooking(this); items.add(item); }
}
