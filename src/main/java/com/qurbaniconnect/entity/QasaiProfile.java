package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "qasai_profiles")
@Getter @Setter @NoArgsConstructor
public class QasaiProfile extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", unique = true)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "city_id")
    private City city;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "area_id")
    private Area area;
    @Column(name = "profile_photo", length = 255)
    private String profilePhoto;
    @Column(name = "cnic_number", nullable = false, unique = true, length = 15)
    private String cnicNumber;
    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;
    @Column(name = "professional_description", length = 1000)
    private String professionalDescription;
    @Enumerated(EnumType.STRING) @Column(name = "verification_status", nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public boolean isApproved() { return verificationStatus == VerificationStatus.APPROVED; }
}
