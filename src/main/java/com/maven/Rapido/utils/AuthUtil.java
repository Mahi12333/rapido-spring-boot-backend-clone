package com.maven.Rapido.utils;

import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.User;
import com.maven.Rapido.repository.UserRepository;
import com.maven.Rapido.security.jwt.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthUtil {
    @Autowired
    private UserRepository userRepository;

    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication);

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;

                String phoneNumber = userDetails.getPhoneNumber();
                String countryCode = userDetails.getCountryCode();

                log.info("Extracted phone: {}, countryCode: {}", phoneNumber, countryCode);

                User user = userRepository.findByPhoneNumberAndCountryCode(phoneNumber, countryCode)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                log.info("User found: {}", user.getId());
                return user.getId();
            }
        }

        throw new APIException("Invalid authentication type");
    }

//    public User loggedInUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return userRepository.findByUserName(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
}
