package com.maven.Rapido.payload.request.driver;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileDTO {
    //!  Step--1
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String dob;
    private Integer step;
    private Integer adharNumber;
    //!  Step--2
    private String currentAddress;
    private String permanentAddress;
    private Long vehicleId;
}
