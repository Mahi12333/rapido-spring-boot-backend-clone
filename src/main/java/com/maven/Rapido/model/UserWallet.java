package com.maven.Rapido.model;


import com.maven.Rapido.emun.PaymentMode;
import com.maven.Rapido.emun.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_wallet")
@Builder
public class UserWallet extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "razorpayOrderId", nullable = true)
    private String razorpayOrderId;
    @Column(name = "razorpayPaymentId", nullable = true)
    private String razorpayPaymentId;
    @Column(name = "razorpaySignature", nullable = true)
    private String razorpaySignature;

    @Column(name = "amount", nullable = true)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = true)
    private PaymentMode paymentMode;

}
