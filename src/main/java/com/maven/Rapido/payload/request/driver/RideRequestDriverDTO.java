package com.maven.Rapido.payload.request.driver;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
public class RideRequestDriverDTO {
    private Long userId;
    private Double pickupLattitute;
    private Double pickupLongitude;
    private Double dropLattitute;
    private Double dropLongitude;
    private String vehicleType;
    private BigDecimal estimatedFare;
    private BigDecimal tips;

}
