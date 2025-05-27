package com.maven.Rapido.payload.request.driver;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverAcceptedPayload {
    private Long driverId;
    private String driverName;
    private String phoneNumber;
    private double currentLat;
    private double currentLng;
    private String vehicleType;
    private String vehicleNumber;
}
