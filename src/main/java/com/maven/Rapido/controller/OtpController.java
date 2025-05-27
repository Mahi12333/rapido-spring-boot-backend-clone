package com.maven.Rapido.controller;


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
@Tag(name = "OtpController", description = "Otp Management")
@RestController
@RequestMapping("/v1/api/otp")
@RequiredArgsConstructor
public class OtpController {

//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyDTO dto) {
//        RideRequest ride = rideRequestRepository.findById(request.getRideRequestId()).orElseThrow();
//
//        if (!ride.getOtp().equals(request.getOtp())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
//        }
//
//        ride.setStatus(RideStatus.STARTED);
//        rideRequestRepository.save(ride);
//
//        // Notify user and driver
//        messagingTemplate.convertAndSend("/topic/user/" + ride.getUserId() + "/ride-started", ride);
//        messagingTemplate.convertAndSend("/topic/driver/" + ride.getAcceptedDriverId() + "/ride-started", ride);
//
//        return ResponseEntity.ok("OTP verified, ride started");
//    }
}
