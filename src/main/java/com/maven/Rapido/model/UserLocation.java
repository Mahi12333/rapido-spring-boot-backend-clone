package com.maven.Rapido.model;

import com.maven.Rapido.emun.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "driver_locations")
public class UserLocation {
    // TODO
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "driverid", nullable = true)
    private Long driverid;

    @Column(name = "userid", nullable = true)
    private Long userid;

    @Column(name = "pickup_lat", nullable = true)
    private Double pickup_lat;

    @Column(name = "pickup_lng", nullable = true)
    private Double pickup_lng;

    @Column(name = "drop_lat", nullable = true)
    private Double drop_lat;

    @Column(name = "drop_lng", nullable = true)
    private Double drop_lng;

    @Column(name = "distance", nullable = true)
    private  String distance;

    @Column(name = "eta", nullable = true)
    private  String eta;

    @Column(name = "fareEstimate", nullable = true)
    private  Double fareEstimate;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "rideType")
    private VehicleType rideType;


    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
