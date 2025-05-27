package com.maven.Rapido.payload.request.radis;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RadisDTO {
    private Long driverId;
    private String vehicleType;
    private Long userId;
    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;
    private Double estimatedFare;
    private String distanceToPickup;            // Distance from driver to pickup point (e.g., "1.2 km")
    private String etaToPickup;                 // ETA from driver to pickup point (e.g., "5 mins")
    private String rideDistance;                // Distance from pickup to drop (e.g., "12.5 km")
    private String rideEta;
    private String rideRequestId;
}
