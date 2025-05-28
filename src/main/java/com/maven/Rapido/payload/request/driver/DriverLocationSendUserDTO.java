package com.maven.Rapido.payload.request.driver;


import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverLocationSendUserDTO {
    private Long driverId;
    private Double lat;
    private Double lng;
    private String vehicleType; // "BIKE", "CAR"
    private boolean available;
    private String userId;
}
