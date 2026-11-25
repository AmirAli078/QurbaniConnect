package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity @Table(name = "qasai_availability")
@Getter @Setter @NoArgsConstructor
public class QasaiAvailability extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "qasai_id")
    private QasaiProfile qasai;
    @Column(name = "available_date", nullable = false)
    private LocalDate availableDate;
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    @Enumerated(EnumType.STRING) @Column(name = "slot_status", nullable = false, length = 15)
    private SlotStatus slotStatus = SlotStatus.AVAILABLE;
}
