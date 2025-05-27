package com.maven.Rapido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "otp_verify")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "otp")
    private Integer otp;
    @Column(name = "is_verified", nullable = true)
    private String status;
    @Column(name = "expiry")
    private Date expiry;
    @Column(name = "userid", nullable = true)
    private Long userId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
