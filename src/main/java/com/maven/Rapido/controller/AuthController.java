package com.maven.Rapido.controller;



import com.maven.Rapido.model.OtpVerify;
import com.maven.Rapido.model.RefreshToken;
import com.maven.Rapido.payload.request.login.PhoneDTO;
import com.maven.Rapido.payload.request.login.SignupDTO;
import com.maven.Rapido.payload.response.user.UserResponse;
import com.maven.Rapido.repository.RefreshTokenRespository;
import com.maven.Rapido.security.jwt.JwtUtils;
import com.maven.Rapido.service.AuthService;
import com.maven.Rapido.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Tag(name = "AuthController", description = "Auth Management")
@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRespository refreshTokenRespository;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;


    @Operation(summary = "Create a user-signup ", description = "This API is used to user-signup")
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody PhoneDTO request) {
       OtpVerify response = authService.sendOtp(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Create a user-signup ", description = "This API is used to user-signup")
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupDTO request) {
        UserResponse response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Resend OTP", description = "This API is used to Resend OTP")
    @PostMapping("/resendOtp")
    public ResponseEntity<?> resendOtp(@RequestBody PhoneDTO request) {
        OtpVerify response = authService.resendOtp(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Create a user-refresh ", description = "This API is used to user-resfresh")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken( @RequestHeader("Authorization") String refreshTokenHeader) {
        log.info("refreshTokenHeader--{}",refreshTokenHeader);
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Refresh Token");
        }
        String refreshToken = refreshTokenHeader.substring(7);
        log.info("storedToken-- {}",refreshToken);
        // ✅ Check if token exists in DB
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or Expired Refresh Token"));
//        log.info("storedToken-- {}",storedToken);
        // ✅ Validate JWT Signature & Expiry
        if (!jwtUtils.validateJwtToken(refreshToken, true)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Expired or Invalid Refresh Token");
        }

        // ✅ Check Expiry
        if (storedToken.getExpiryTime().isBefore(Instant.now())) {
            refreshTokenService.deleteByUserId(storedToken.getUserId()); // Remove expired token
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh Token Expired. Please log in again.");
        }

        // ✅ Generate new tokens
        Long userId = jwtUtils.getUserIdFromJwtToken(refreshToken, true);
        String newAccessToken = jwtUtils.generateAccessToken(userId);
        String newRefreshToken = jwtUtils.generateRefreshToken(userId);

        // ✅ Update refresh token in DB
        storedToken.setAccesstoken(newAccessToken);
        storedToken.setRefreshtoken(newRefreshToken);
        storedToken.setExpiryTime(Instant.now().plus(Duration.ofMillis(jwtRefreshExpirationMs))); // Example: 7 days expiry
        refreshTokenRespository.save(storedToken);

        // ✅ Return new tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refresh_token", newRefreshToken);

        return ResponseEntity.ok(tokens);
    }



}
