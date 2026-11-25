package com.qurbaniconnect.entity;

import com.qurbaniconnect.enums.DocumentType;
import com.qurbaniconnect.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "qasai_verification_documents")
@Getter @Setter @NoArgsConstructor
public class QasaiVerificationDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "qasai_id")
    private QasaiProfile qasai;
    @Enumerated(EnumType.STRING) @Column(name = "document_type", nullable = false, length = 30)
    private DocumentType documentType;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    @Enumerated(EnumType.STRING) @Column(name = "verification_status", nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
