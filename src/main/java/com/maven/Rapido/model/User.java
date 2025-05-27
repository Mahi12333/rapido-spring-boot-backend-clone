package com.maven.Rapido.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maven.Rapido.emun.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "phone_number"),
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = true)
    private String userName;

    @Column(name = "firstname", nullable = true)
    private String firstName;

    @Column(name = "lastname", nullable = true)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = true, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = true)
    private String password;

    private Boolean accountNonLocked = true;
    private Boolean accountNonExpired = true;
    private Boolean credentialsNonExpired = true;
    private Boolean enabled = true;

    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;

    private String twoFactorSecret;
    private Boolean isTwoFactorEnabled = true;

    @Column(name = "signUpMethod", nullable = true)
    private String signUpMethod;

    @Lob
    @Column(name = "id_proof", nullable = true)
    private String idProof;

    @Lob
    @Column(name = "driving_licence", nullable = true)
    private String drivingLicence;

    @Lob
    @Column(name = "pan_card", nullable = true)
    private String pancard;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "fcm_token", nullable = true)
    private String fcm_token;

    @Lob
    @Column(name = "profile", nullable = true)
    private String profile;

    @Column(name = "country_code", nullable = true)
    private String country_code;

    @Lob
    @Column(name = "currentaddress", nullable = true)
    private String currentAddress;

    @Lob
    @Column(name = "permanentaddress", nullable = true)
    private String permanentAddress;

    @Column(name = "is_verified", nullable = true)
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @Column(name = "dob", nullable = true)
    private String dob;

    @Lob
    @Column(name = "dob_certificate", nullable = true)
    private String dobCertificate;

    @Column(name = "step", nullable = true)
    private Integer step;

    @Column(name = "adhar_number", nullable = true)
    private Integer adharnumber;


    @OneToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = true)
    private VehicleCategory vehicleCategory;


}
