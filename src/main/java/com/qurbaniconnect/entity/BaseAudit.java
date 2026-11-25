package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseAudit {
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
