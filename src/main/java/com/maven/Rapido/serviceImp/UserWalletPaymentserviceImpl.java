package com.maven.Rapido.serviceImp;

import com.maven.Rapido.emun.PaymentStatus;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.exception.ResourceNotFoundException;
import com.maven.Rapido.mapstruct.PaymentMapper;
import com.maven.Rapido.model.UserWallet;
import com.maven.Rapido.model.User;
import com.maven.Rapido.payload.request.payment.PaymentRequest;
import com.maven.Rapido.payload.response.payment.PaymentResponse;
import com.maven.Rapido.repository.UserWalletPaymentRepository;
import com.maven.Rapido.repository.UserRepository;
import com.maven.Rapido.service.UserWalletPaymentservice;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class UserWalletPaymentserviceImpl implements UserWalletPaymentservice {

    @Autowired
    private UserWalletPaymentRepository userWalletPaymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    private RazorpayClient client;

    @PostConstruct
    public void init() throws RazorpayException {
        client = new RazorpayClient(key, secret);
    }


    // Scheduled every 30 minutes
    @Transactional
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanUpUnpaidOrders() {
        log.info("Cleaning up unpaid orders------------");
        List<UserWallet> staleOrders = userWalletPaymentRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.CREATED, LocalDateTime.now().minusMinutes(5));

        for (UserWallet payment : staleOrders) {
            payment.setStatus(PaymentStatus.EXPIRED); // Add EXPIRED enum
            payment.setUpdatedAt(LocalDateTime.now());
            userWalletPaymentRepository.save(payment);
        }
    }

    @Transactional
    @Override
    public PaymentResponse createPaymentOrder(PaymentRequest request) {
        User existingUser = userRepository.findById(request.getUserId()).orElseThrow( () -> new ResourceNotFoundException("User not found with ID:" + request.getUserId()));
        JSONObject options = new JSONObject();
        options.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)));
        options.put("currency", "INR");

        Order order;
        try {
            order = client.orders.create(options);
        } catch (RazorpayException e) {
            throw new APIException("Failed to create Razorpay order");
        }

        UserWallet createPayment = UserWallet.builder()
                .userId(existingUser.getId())
                .amount(request.getAmount())
                .status(PaymentStatus.CREATED)
                .razorpayOrderId(order.get("id"))
                .build();

        userWalletPaymentRepository.saveAndFlush(createPayment);
        return paymentMapper.toCreatePaymentResponse(createPayment);
    }

    @Transactional
    @Override
    public PaymentResponse verifyPayment(PaymentRequest request) throws Exception {
        UserWallet payment = userWalletPaymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId());
        if (payment == null) {
            throw new APIException("Order ID not found.");
        }

        // Check if already paid
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new APIException("Payment already completed for this order ID.");
        }

        if (payment.getStatus() == PaymentStatus.EXPIRED) {
            throw new APIException("Order ID Expired!");
        }

        // Check if order is expired (createdAt + 5 minutes)
        if (payment.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            payment.setStatus(PaymentStatus.EXPIRED);
            payment.setUpdatedAt(LocalDateTime.now());
            userWalletPaymentRepository.save(payment);
            throw new APIException("This payment order has expired. Please Try Again!.");
        }

        // HMAC Signature verification
        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        String expectedSignature = hmacSha256(payload, secret);

        if (!expectedSignature.equals(request.getRazorpaySingnature())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setUpdatedAt(LocalDateTime.now());
            userWalletPaymentRepository.save(payment);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Razorpay signature.");
        }

        // Mark as PAID
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySingnature());
        payment.setStatus(PaymentStatus.PAID);
        payment.setUpdatedAt(LocalDateTime.now());
        userWalletPaymentRepository.save(payment);

        //notificationService.sendPaymentSuccessNotification(payment);
        return paymentMapper.toVerifyPaymentResponse(payment);
    }

   /* @Override
    public void deleteOrderId(PaymentRequest request) {
    }*/

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        return new String(Base64.getEncoder().encode(mac.doFinal(data.getBytes())));
    }




}
