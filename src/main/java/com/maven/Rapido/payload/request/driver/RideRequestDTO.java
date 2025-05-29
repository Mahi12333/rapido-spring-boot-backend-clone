package com.maven.Rapido.payload.request.driver;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class RideRequestDTO {
    private Double pickupLattitute;
    private Double pickupLongitude;
    private Double dropLattitute;
    private Double dropLongitude;
    private String vehicleType;
    private Long userId;
}
