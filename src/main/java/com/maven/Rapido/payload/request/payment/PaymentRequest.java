package com.maven.Rapido.payload.request.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public class PaymentRequest {
    private Long userId;
    private BigDecimal amount;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySingnature;
}
