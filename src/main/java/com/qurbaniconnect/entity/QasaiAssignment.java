package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "qasai_assignments")
@Getter @Setter @NoArgsConstructor
public class QasaiAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "qasai_id")
    private QasaiProfile qasai;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "assigned_by")
    private User assignedBy;
    @Enumerated(EnumType.STRING) @Column(name = "assignment_status", nullable = false, length = 15)
    private AssignmentStatus assignmentStatus = AssignmentStatus.PENDING;
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();
    @Column(name = "response_at")
    private LocalDateTime responseAt;
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}
