package com.maven.Rapido.controller;


import com.maven.Rapido.emun.OtpStatus;
import com.maven.Rapido.emun.RideStatus;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.RideRequest;
import com.maven.Rapido.payload.request.user.OtpVerifyDTO;
import com.maven.Rapido.repository.RideRequestRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final RideRequestRepository rideRequestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyDTO request) {
        RideRequest ride = rideRequestRepository
                .findByRequestIdForOtpVerify(request.getRideRequestedId(), request.getDriverId(), request.getUserId());
        if(ride == null){
            throw new APIException("Ride request not found or invalid");
        }

        if (!ride.getOtp().equals(request.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        ride.setStatus(RideStatus.STARTED.name());
        ride.setOtpStatus(OtpStatus.VERIFIED.name());
        rideRequestRepository.save(ride);

        // Notify user and driver
        messagingTemplate.convertAndSend("/topic/user/" + ride.getUserId() + "/ride-started", ride);
        messagingTemplate.convertAndSend("/topic/driver/" + ride.getAcceptedDriverId() + "/ride-started", ride);

        return ResponseEntity.ok("OTP verified, ride started");

        //TODO
        // Notification to user and driver about ride start
    }
}
