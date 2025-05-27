package com.maven.Rapido.security.jwt;


import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.OtpVerify;
import com.maven.Rapido.model.User;
import com.maven.Rapido.repository.OtpVerifyRepository;
import com.maven.Rapido.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OtpAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpVerifyRepository otpVerifyRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phone = authentication.getPrincipal().toString();
        String otp = authentication.getCredentials().toString();
        String countryCode = ((OtpAuthenticationToken) authentication).getCountryCode();

//        Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
//        if (userOpt.isEmpty()) throw new APIException("Invalid phone number");
//        User user = userOpt.get();
        OtpVerify otpVerify = otpVerifyRepository
                .findByPhoneNumberAndCountryCode(phone, countryCode)
                .orElseThrow(() -> new APIException("OTP not found or invalid"));

        if (!otp.equals(String.valueOf(otpVerify.getOtp()))) {
            throw new APIException("Invalid OTP");
        }
        // Delete OTP after successful registration
        otpVerifyRepository.delete(otpVerify);
        Optional<User> userOpt = userRepository.findByPhoneNumberAndCountryCode(phone, countryCode);
        User newUser;
        if (userOpt.isPresent()) {
            newUser = userOpt.get();
        } else {
            // If new registration, create user
            newUser = new User();
            newUser.setPhoneNumber(phone);
            newUser.setCountry_code(countryCode);
            newUser.setIsVerified(true);
            newUser = userRepository.save(newUser); // ⬅ persist to get generated ID
        }

        //log.info("newUser----{}", newUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(
                newUser.getId(),
                newUser.getPhoneNumber(),
                newUser.getCountry_code(),
                Collections.emptyList()
        );

        return new OtpAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
