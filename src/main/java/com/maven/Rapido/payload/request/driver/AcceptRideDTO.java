package com.maven.Rapido.payload.request.driver;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptRideDTO {
    private String rideRequestId;
    private Long driverId;
    private Long userId;
}
