package com.maven.Rapido.payload.response.payment;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class PaymentResponse {
    private Long userId;
    private BigDecimal amount;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySingnature;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
