package com.maven.Rapido.payload.request.driver;


import lombok.*;


//@RedisHash("DriverLocationDTO")
@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class DriverLocationDTO {
    private Long driverId;
    private Double lat;
    private Double lng;
    private String vehicleType; // "BIKE", "CAR"
    private boolean available;
}
