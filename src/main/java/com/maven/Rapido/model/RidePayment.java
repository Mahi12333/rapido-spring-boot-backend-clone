package com.maven.Rapido.model;


import com.maven.Rapido.emun.PaymentMode;
import com.maven.Rapido.emun.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ride_payments")
public class RidePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "ride_id", nullable = true)
    private Long rideId;

    @Column(name = "user_ride_request_id", nullable = true)
    private Long userRideRequestId;

    @Column(name = "ride_request_id", nullable = false, unique = true)
    private String rideRequestId;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "amount", nullable = true)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = true)
    private PaymentMode paymentMode;



}
