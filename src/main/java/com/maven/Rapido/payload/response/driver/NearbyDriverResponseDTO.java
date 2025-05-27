package com.maven.Rapido.payload.response.driver;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriverResponseDTO {
    private Long driverId;
    private double driverLat;
    private double driverLng;
    private String vehicleType;

    private String distanceToPickup;     // e.g. "1.12 km"
    private String etaToPickup;          // e.g. "3 mins"

    private String rideDistance;         // e.g. "4.2 km" (pickup → drop)
    private String rideEta;              // e.g. "12 mins"
    private Double fareEstimate;         // e.g. "₹62.0"
}
