package com.maven.Rapido.service;

import com.maven.Rapido.payload.request.payment.PaymentRequest;
import com.maven.Rapido.payload.response.payment.PaymentResponse;

public interface UserWalletPaymentservice {
    PaymentResponse createPaymentOrder(PaymentRequest request);
    PaymentResponse verifyPayment(PaymentRequest request) throws Exception;
//    void deleteOrderId(PaymentRequest request);
}
