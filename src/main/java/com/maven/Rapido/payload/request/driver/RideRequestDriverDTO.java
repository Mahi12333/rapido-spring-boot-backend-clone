package com.maven.Rapido.payload.request.driver;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class RideRequestDriverDTO {
    private Long userId;
    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;
    private String vehicleType;
    private Double estimatedFare;

}
