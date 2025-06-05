package com.maven.Rapido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ride_requests")
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ride_request_id", nullable = false, unique = true)
    private String rideRequestId;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "pickup_lat", nullable = true)
    private Double pickupLat;
    @Column(name = "pickup_lng", nullable = true)
    private Double pickupLng;

    @Column(name = "drop_lat", nullable = true)
    private Double dropLat;

    @Column(name = "drop-lng", nullable = true)
    private Double dropLng;

    @Column(name = "vehicle_type", nullable = true)
    private String vehicleType;

    @Column(name = "estimated_fare", nullable = true)
    private BigDecimal estimatedFare;

    @Column(name = "tips", nullable = true)
    private BigDecimal tips;

    @Column(name = "status", nullable = true)
    private String status;

    @Column(name = "driver_id", nullable = true)
    private Long acceptedDriverId;

    @Column(name = "created-at", nullable = true)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "otp", nullable = true)
    private Integer otp;

    @Column(name = "otp_status", nullable = true)
    private String otpStatus;
}
