package com.maven.Rapido.controller;


import com.maven.Rapido.payload.request.payment.PaymentRequest;
import com.maven.Rapido.payload.response.CommonResponseDTO;
import com.maven.Rapido.payload.response.payment.PaymentResponse;
import com.maven.Rapido.service.UserWalletPaymentservice;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "PaymentController", description = "Payment Management")
@RestController
@RequestMapping("/v1/api/payment")
@RequiredArgsConstructor
public class WalletPaymentController {
    private final UserWalletPaymentservice userWalletPaymentservice;

    @PostMapping("/create-payment-order")
    public ResponseEntity<?> createPaymentOrder(@RequestBody PaymentRequest request) {
        // TODO Create Payment Order
       PaymentResponse response = userWalletPaymentservice.createPaymentOrder(request);
        CommonResponseDTO<PaymentResponse> responseBody = new CommonResponseDTO<>(
                response,
                "Payment OrderId Successfully Create!"
        );
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequest request) throws Exception{
        // TODO Verify Payment
        PaymentResponse response = userWalletPaymentservice.verifyPayment(request);
        CommonResponseDTO<PaymentResponse> responseBody = new CommonResponseDTO<>(
                response,
                "Payment Successfully Done!"
        );
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /*@PostMapping("/delete-orderId")
    public ResponseEntity<?> deleteOrderId(@RequestBody PaymentRequest request){
        userWalletPaymentservice.deleteOrderId(request);
        return new  ResponseEntity<>("OrderId Successfully Delete!", HttpStatus.OK);
    }*/



}
