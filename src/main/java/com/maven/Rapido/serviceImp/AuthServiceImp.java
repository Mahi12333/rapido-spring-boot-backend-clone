package com.maven.Rapido.serviceImp;

import com.maven.Rapido.emun.UserRole;
import com.maven.Rapido.emun.UserStatus;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.exception.ResourceNotFoundException;
import com.maven.Rapido.model.OtpVerify;
import com.maven.Rapido.model.RefreshToken;
import com.maven.Rapido.model.Role;
import com.maven.Rapido.model.User;
import com.maven.Rapido.payload.request.login.PhoneDTO;
import com.maven.Rapido.payload.request.login.SignupDTO;
import com.maven.Rapido.payload.response.user.UserResponse;
import com.maven.Rapido.repository.OtpVerifyRepository;
import com.maven.Rapido.repository.RefreshTokenRespository;
import com.maven.Rapido.repository.RoleRepository;
import com.maven.Rapido.repository.UserRepository;
import com.maven.Rapido.security.jwt.JwtUtils;
import com.maven.Rapido.security.jwt.OtpAuthenticationToken;
import com.maven.Rapido.security.jwt.UserDetailsImpl;
import com.maven.Rapido.service.AuthService;
import com.maven.Rapido.utils.EmailService;
import com.maven.Rapido.utils.EmailTamplate.EmailotpContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private final OtpVerifyRepository otpVerifyRepository;
    private final EmailotpContent emailotpContent;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRespository refreshTokenRespository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private MessageSource messageSource;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Override
    public OtpVerify sendOtp(PhoneDTO request) {
        return handleOtpGeneration(request, false);
    }

    @Override
    public OtpVerify resendOtp(PhoneDTO request) {
        return handleOtpGeneration(request, true);
    }

    @Transactional
    @Override
    public UserResponse registerUser(SignupDTO request) {
        String phone = request.getPhoneNumber();
        String countryCode = request.getCountryCode();
        Integer Otp = request.getOtp();
        String fcmToken = request.getFcm_Token();
        Authentication authentication = authenticationManager.authenticate(
                new OtpAuthenticationToken(phone, countryCode, Otp)
        );
        log.info("authentication-- {}",authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        log.info("userId---{}", userId);
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        existingUser.setFcm_token(fcmToken);
        existingUser.setStatus(UserStatus.ACTIVE);

        Role userRole = roleRepository.findByRoleName(UserRole.USER)
                .orElseThrow(() -> new ResourceNotFoundException("USER role not found"));
        existingUser.setRole(userRole);

        User updatedUser = userRepository.saveAndFlush(existingUser);

        String accessToken = jwtUtils.generateAccessToken(updatedUser.getId());
        String refreshToken = jwtUtils.generateRefreshToken(updatedUser.getId());

        //refreshTokenRespository.deleteByUserId(saveUser.getId());
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(updatedUser.getId());
        refreshTokenEntity.setAccesstoken(accessToken);
        refreshTokenEntity.setRefreshtoken(refreshToken);
        refreshTokenEntity.setExpiryTime(Instant.now().plus(Duration.ofMinutes(jwtRefreshExpirationMs))); // Set expiry to 7 days
        refreshTokenRespository.saveAndFlush(refreshTokenEntity);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getPhoneNumber(),
                updatedUser.getCountry_code(),
                updatedUser.getFcm_token(),
                updatedUser.getIsVerified(),
                updatedUser.getStatus().name(),
                updatedUser.getRole().getRoleName().name(),
                accessToken,
                refreshToken
        );

    }


    private int generateOtp() {
        return (int) (Math.random() * 900000) + 100000; // Generates 4-digit OTP
    }

    private void sendOtpByPhone(int otp, String phoneNumber, Date expiry) {
        log.info("Sending OTP {} to phone number {} (expires at {})", otp, phoneNumber, expiry);
        String subject = "Your OTP for Verification";
        String name = "User";
        String htmlContent = emailotpContent.getEmailContent(otp, name, phoneNumber, subject);

        emailService.sendOtpByPhone(otp, phoneNumber, expiry); // Assuming WebClient-based API call is inside this method
    }

    /*private void sendOtpByPhone(int otp, String phoneNumber, Date expiry) {
        log.info("Sending OTP {} to phone number {} (expires at {})", otp, phoneNumber, expiry);
        String subject = "Your OTP for Verification";
        String name = "User";
        String htmlContent =  emailotpContent.getEmailContent(otp, name, phoneNumber, subject);
        emailService.sendEmail(info, recipientEmail.trim(), subject, htmlContent);
    }*/

    private OtpVerify handleOtpGeneration(PhoneDTO request, boolean isResend) {
        String phone = request.getPhone_number();
        String countryCode = request.getCountry_code();
        String combinedPhone = countryCode + phone;

        log.info("Received request to {} OTP for phone: {}", isResend ? "resend" : "send", combinedPhone);

        if (isNullOrEmpty(phone) || isNullOrEmpty(countryCode)) {
            throw new APIException("error.phone.required");
        }
        Optional<User> userDB = userRepository.findByPhoneNumberAndCountryCode(phone, countryCode);
        if (userDB.isPresent() && userDB.get().getRole().getRoleName() == UserRole.DRIVER) {
            throw new APIException("error.driver.registered");
        }

        OtpVerify existingOtp = otpVerifyRepository.findByPhoneNumberAndUserId(phone, countryCode);
        Date now = new Date();

        if (existingOtp != null && now.before(existingOtp.getExpiry())) {
            sendOtpByPhone(existingOtp.getOtp(), combinedPhone, existingOtp.getExpiry());
            return existingOtp;
        }

        int newOtp = generateOtp();
        Date expiry = new Date(System.currentTimeMillis() + 2 * 60 * 1000); // 2 minutes

        OtpVerify otp = Optional.ofNullable(existingOtp).orElse(new OtpVerify());
        otp.setPhoneNumber(phone);
        otp.setCountryCode(countryCode);
        otp.setOtp(newOtp);
        otp.setExpiry(expiry);
        otp.setStatus("not verified");

        otpVerifyRepository.saveAndFlush(otp);
        sendOtpByPhone(newOtp, combinedPhone, expiry);
        return otp;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


}