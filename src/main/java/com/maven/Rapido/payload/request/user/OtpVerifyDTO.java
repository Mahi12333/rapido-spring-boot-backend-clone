package com.maven.Rapido.payload.request.user;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpVerifyDTO {
    private Integer otp;
    private String rideRequestedId;
    private Long userId;
    private Long driverId;
}
