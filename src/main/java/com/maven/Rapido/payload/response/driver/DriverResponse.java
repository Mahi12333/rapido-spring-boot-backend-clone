package com.maven.Rapido.payload.response.driver;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private String phoneNumber;
    private String countryCode;
    private String fcmToken;
    private Boolean isVerified;
    private String status;
    private String role;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private LocalDate credentialsExpiryDate;
    private String idProof;
    private String drivingLicence;
    private String pancard;
    private String profile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String dob;
    private String dobCertificate;
    private Integer step;
    private Integer adharnumber;
    private String VehicleType;

}
