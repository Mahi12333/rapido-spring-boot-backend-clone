package com.maven.Rapido.payload.request.driver;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RideRequestSocketPayload {
    private String rideId;                      // Unique ride request ID
    private Long passengerId;                   // User ID who is requesting the ride

    private Double pickupLat;                   // Pickup latitude
    private Double pickupLng;                   // Pickup longitude

    private Double dropLat;                     // Drop-off latitude
    private Double dropLng;                     // Drop-off longitude

    private String vehicleType;                 // Requested vehicle type (UberX, Auto, etc.)
    private BigDecimal estimatedFare;               // Fare estimate for the ride
    private BigDecimal tips;

    private String distanceToPickup;            // Distance from driver to pickup point (e.g., "1.2 km")
    private String etaToPickup;                 // ETA from driver to pickup point (e.g., "5 mins")

    private String rideDistance;                // Distance from pickup to drop (e.g., "12.5 km")
    private String rideEta;                     // Estimated ride duration (e.g., "30 mins")

    private String status;                      // Ride status: PENDING, ACCEPTED, EXPIRED, etc.

    private LocalDateTime createdAt;            // Timestamp of request creation
    private List<Long> pendingDriverIds;        // List of driver IDs who received this request (Trip Radar style)

    // Optional for frontend display:
    private Double driverLat;                   // (optional) Driver's current latitude
    private Double driverLng;
}
