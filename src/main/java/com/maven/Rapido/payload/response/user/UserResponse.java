package com.maven.Rapido.payload.response.user;

import com.maven.Rapido.emun.UserRole;
import com.maven.Rapido.emun.UserStatus;
import com.maven.Rapido.model.Role;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String phoneNumber;
    private String countryCode;
    private String fcmToken;
    private Boolean isVerified;
    private String status;
    private String role;
    private String accessToken;
    private String refreshToken;


}
