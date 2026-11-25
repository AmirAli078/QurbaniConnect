package com.qurbaniconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "animal_categories")
@Getter @Setter @NoArgsConstructor
public class AnimalCategory extends BaseAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "animal_name", nullable = false, unique = true, length = 40)
    private String animalName;
    @Column(name = "urdu_name", nullable = false, length = 60)
    private String urduName;
    @Column(length = 300)
    private String description;
    @Column(name = "image_path", length = 255)
    private String imagePath;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
