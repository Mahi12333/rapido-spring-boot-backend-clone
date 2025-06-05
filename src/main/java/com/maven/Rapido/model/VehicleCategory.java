package com.maven.Rapido.model;

import com.maven.Rapido.emun.UserRole;
import com.maven.Rapido.emun.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_categorys",
        indexes = {
                @Index(name = "idx_vehicle_categorys_name", columnList = "name")
        })
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ToString.Exclude
//    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = true, unique = true)
    private String name;

    @Column(name = "slug", nullable = true, unique = true)
    private String Slug;

    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "image_url", nullable = true)
    private String imageUrl;
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;


}
