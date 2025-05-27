package com.maven.Rapido.controller;


import com.maven.Rapido.payload.request.driver.AcceptRideDTO;
import com.maven.Rapido.payload.request.driver.DriverLocationDTO;
import com.maven.Rapido.payload.request.driver.RideRequestDTO;
import com.maven.Rapido.payload.request.driver.RideRequestDriverDTO;
import com.maven.Rapido.payload.response.driver.NearbyDriverResponseDTO;
import com.maven.Rapido.serviceImp.RideRequestService;
import com.maven.Rapido.utils.GoogleMapsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/v1/requestRide")
@RequiredArgsConstructor
public class RequestRideController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final GoogleMapsService googleMapsService;
    private final RideRequestService rideRequestService;

    @PostMapping("/display-drivers-to-user")
    public ResponseEntity<?> findNearbyDrivers(@Valid @RequestBody RideRequestDTO rideRequest) {
        double pickupLat = rideRequest.getPickupLattitute();
        double pickupLng = rideRequest.getPickupLongitude();
        double dropLat = rideRequest.getDropLattitute();
        double dropLng = rideRequest.getDropLongitude();
        String vehicleType = rideRequest.getVehicleType(); // may be null

        // 1. Get all drivers from Redis
        Map<Object, Object> allDriversMap = redisTemplate.opsForHash().entries("drivers");

        List<DriverLocationDTO> allDrivers = allDriversMap.values().stream()
                .map(obj -> (DriverLocationDTO) obj)
                .collect(Collectors.toList());

        // 2. Distance (pickup → drop)
        Map<String, String> pickupToDrop = googleMapsService
                .getDistanceAndETA(pickupLat, pickupLng, dropLat, dropLng)
                .block();

        String rideDistance = pickupToDrop.get("distance");
        String rideEta = pickupToDrop.get("eta");
        String fare = estimateFare(rideDistance);

        // 3. Prepare grouped response
        Map<String, List<NearbyDriverResponseDTO>> groupedByVehicleType = new HashMap<>();
        Map<String, Double> distanceSumMap = new HashMap<>();
        Map<String, Long> etaSumMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (DriverLocationDTO driver : allDrivers) {
            if (vehicleType != null && !driver.getVehicleType().equalsIgnoreCase(vehicleType)) {
                continue; // Skip if user filtered for a specific type
            }

            double distToUser = haversineDistance(pickupLat, pickupLng, driver.getLat(), driver.getLng());

            if (distToUser <= 3.0) {
                Map<String, String> driverToPickup = googleMapsService
                        .getDistanceAndETA(driver.getLat(), driver.getLng(), pickupLat, pickupLng)
                        .block();

                String etaStr = driverToPickup.get("eta").replaceAll("[^0-9]", "").trim();
                long etaMinutes = etaStr.isEmpty() ? 0 : Long.parseLong(etaStr);

                String type = driver.getVehicleType();
                NearbyDriverResponseDTO dto = NearbyDriverResponseDTO.builder()
                        .driverId(driver.getDriverId())
                        .driverLat(driver.getLat())
                        .driverLng(driver.getLng())
                        .vehicleType(type)
                        .distanceToPickup(driverToPickup.get("distance"))
                        .etaToPickup(driverToPickup.get("eta"))
                        .rideDistance(rideDistance)
                        .rideEta(rideEta)
                        .fareEstimate(Double.valueOf(fare))
                        .build();

                groupedByVehicleType.computeIfAbsent(type, k -> new ArrayList<>()).add(dto);
                distanceSumMap.merge(type, distToUser, Double::sum);
                etaSumMap.merge(type, etaMinutes, Long::sum);
                countMap.merge(type, 1, Integer::sum);
            }
        }

        // 4. Final response formatting
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (String type : groupedByVehicleType.keySet()) {
            int count = countMap.get(type);
            double avgDist = distanceSumMap.get(type) / count;
            long avgEta = etaSumMap.get(type) / count;

            Map<String, Object> group = new LinkedHashMap<>();
            group.put("vehicleType", type);
            group.put("nearbyDrivers", groupedByVehicleType.get(type));
            group.put("averageDistanceToPickupKm", String.format("%.2f km", avgDist));
            group.put("averagePickupETATime", avgEta + " mins");
            group.put("rideDistance", rideDistance);
            group.put("rideEta", rideEta);
            group.put("fareEstimate", fare);

            responseList.add(group);
        }

        return ResponseEntity.ok(responseList);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                        Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) *
                        Math.sin(dLon / 2);
        return EARTH_RADIUS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private String estimateFare(String distanceText) {
        try {
            double km = Double.parseDouble(distanceText.replace(" km", "").trim());
            double baseFare = 20;
            double perKmRate = 10;
            return "₹" + (baseFare + km * perKmRate);
        } catch (Exception e) {
            return "N/A";
        }
    }

    /* @PostMapping("/request-ride")
    public ResponseEntity<?> findNearbyDrivers(@Valid @RequestBody RideRequestDTO rideRequest) {

        double pickupLat = rideRequest.getPickupLattitute();
        double pickupLng = rideRequest.getPickupLongitude();
        double dropLat = rideRequest.getDropLattitute();
        double dropLng = rideRequest.getDropLongitude();
        String vechileType = rideRequest.getVehicleType(); // if vehicleType is BIKE  than will only show BIKE related or if veicleTypeis CAR than only show CAR ...

        // 1. Get all drivers from Redis
        Map<Object, Object> allDriversMap = redisTemplate.opsForHash().entries("drivers");

        List<DriverLocationDTO> allDrivers = allDriversMap.values().stream()
                .map(obj -> (DriverLocationDTO) obj)
                .collect(Collectors.toList());

        // 2. Distance (pickup → drop)
        //Map<String, String> pickupToDrop = googleMapsService.getDistanceAndETA(pickupLat, pickupLng, dropLat, dropLng);
        Map<String, String> pickupToDrop = googleMapsService
                .getDistanceAndETA(pickupLat, pickupLng, dropLat, dropLng)
                .block();

        String rideDistance = pickupToDrop.get("distance");
        String rideEta = pickupToDrop.get("eta");
        String fare = estimateFare(rideDistance);

        // 3. Prepare grouped response
        Map<String, List<NearbyDriverResponseDTO>> groupedByVehicleType = new HashMap<>();
        Map<String, Double> distanceSumMap = new HashMap<>();
        Map<String, Long> etaSumMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (DriverLocationDTO driver : allDrivers) {
            double distToUser = haversineDistance(pickupLat, pickupLng, driver.getLat(), driver.getLng());

            if (distToUser <= 3.0) {
                // Distance & ETA: driver → pickup
//                Map<String, String> driverToPickup = googleMapsService.getDistanceAndETA(
//                        driver.getLat(), driver.getLng(), pickupLat, pickupLng
//                );

                Map<String, String> driverToPickup = googleMapsService
                        .getDistanceAndETA(driver.getLat(), driver.getLng(), pickupLat, pickupLng)
                        .block();

                String etaStr = driverToPickup.get("eta").replaceAll("[^0-9]", "").trim();
                long etaMinutes = etaStr.isEmpty() ? 0 : Long.parseLong(etaStr);

                String vehicleType = driver.getVehicleType();

                NearbyDriverResponseDTO dto = NearbyDriverResponseDTO.builder()
                        .driverId(driver.getDriverId())
                        .driverLat(driver.getLat())
                        .driverLng(driver.getLng())
                        .vehicleType(vehicleType)
                        .distanceToPickup(driverToPickup.get("distance"))
                        .etaToPickup(driverToPickup.get("eta"))
                        .rideDistance(rideDistance)
                        .rideEta(rideEta)
                        .fareEstimate(fare)
                        .build();

                groupedByVehicleType.computeIfAbsent(vehicleType, k -> new ArrayList<>()).add(dto);

                distanceSumMap.merge(vehicleType, distToUser, Double::sum);
                etaSumMap.merge(vehicleType, etaMinutes, Long::sum);
                countMap.merge(vehicleType, 1, Integer::sum);
            }
        }

        // 4. Final response formatting
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (String vehicleType : groupedByVehicleType.keySet()) {
            int count = countMap.get(vehicleType);
            double avgDist = distanceSumMap.get(vehicleType) / count;
            long avgEta = etaSumMap.get(vehicleType) / count;

            Map<String, Object> group = new LinkedHashMap<>();
            group.put("vehicleType", vehicleType);
            group.put("nearbyDrivers", groupedByVehicleType.get(vehicleType));
            group.put("averageDistanceToPickupKm", String.format("%.2f km", avgDist));
            group.put("averagePickupETATime", avgEta + " mins");
            group.put("rideDistance", rideDistance);
            group.put("rideEta", rideEta);
            group.put("fareEstimate", fare);

            responseList.add(group);
        }

        return ResponseEntity.ok(responseList);
    }*/

    /*@PostMapping("/request-ride")
    public ResponseEntity<?> findNearbyDrivers(@Valid @RequestBody RideRequestDTO rideRequest) {

        double pickupLat = rideRequest.getPickupLattitute();
        double pickupLng = rideRequest.getPickupLongitude();
        double dropLat = rideRequest.getDropLattitute();
        double dropLng = rideRequest.getDropLongitude();

        Map<Object, Object> allDriversMap = redisTemplate.opsForHash().entries("drivers");

        List<DriverLocationDTO> allDrivers = allDriversMap.values().stream()
                .map(obj -> (DriverLocationDTO) obj)
                .collect(Collectors.toList());

        // Step 1: Distance & ETA from pickup to drop
        Map<String, String> pickupToDrop = googleMapsService.getDistanceAndETA(
                pickupLat, pickupLng, dropLat, dropLng
        );
        String rideDistance = pickupToDrop.get("distance"); // "4.2 km"
        String rideEta = pickupToDrop.get("eta");           // "12 mins"
        String fare = estimateFare(rideDistance);           // ₹ calculated

        // Step 2: Prepare response grouping by vehicleType
        Map<String, List<NearbyDriverResponseDTO>> groupedDrivers = new HashMap<>();
        Map<String, Double> distanceSumMap = new HashMap<>();
        Map<String, Long> etaSumMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (DriverLocationDTO driver : allDrivers) {
            double distToPickup = haversineDistance(pickupLat, pickupLng, driver.getLat(), driver.getLng());

            if (distToPickup <= 3.0) {
                Map<String, String> etaMap = googleMapsService.getDistanceAndETA(driver.getLat(), driver.getLng(), pickupLat, pickupLng);

                String etaStr = etaMap.get("eta").replaceAll("[^0-9]", "").trim();
                long etaToPickup = etaStr.isEmpty() ? 0 : Long.parseLong(etaStr);
                String vehicleType = driver.getVehicleType();

                NearbyDriverResponseDTO dto = NearbyDriverResponseDTO.builder()
                        .driverId(driver.getDriverId())
                        .driverLat(driver.getLat())
                        .driverLng(driver.getLng())
                        .vehicleType(vehicleType)
                        .distanceToPickup(String.format("%.2f km", distToPickup))
                        .etaToPickup(etaMap.get("eta"))
                        .rideDistance(rideDistance)
                        .rideEta(rideEta)
                        .fareEstimate(fare)
                        .build();

                groupedDrivers.computeIfAbsent(vehicleType, k -> new ArrayList<>()).add(dto);

                distanceSumMap.merge(vehicleType, distToPickup, Double::sum);
                etaSumMap.merge(vehicleType, etaToPickup, Long::sum);
                countMap.merge(vehicleType, 1, Integer::sum);
            }
        }

        // Step 3: Build final response
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (String vehicleType : groupedDrivers.keySet()) {
            int count = countMap.get(vehicleType);
            double avgDist = distanceSumMap.get(vehicleType) / count;
            long avgEta = etaSumMap.get(vehicleType) / count;

            Map<String, Object> group = new LinkedHashMap<>();
            group.put("vehicleType", vehicleType);
            group.put("nearbyDrivers", groupedDrivers.get(vehicleType));
            group.put("averageDistanceToPickupKm", String.format("%.2f km", avgDist));
            group.put("averagePickupETATime", avgEta + " mins");
            group.put("rideDistance", rideDistance);
            group.put("rideEta", rideEta);
            group.put("fareEstimate", fare);

            responseList.add(group);
        }

        return ResponseEntity.ok(responseList);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private String estimateFare(String distanceText) {
        try {
            double km = Double.parseDouble(distanceText.replace(" km", "").trim());
            double baseFare = 20;
            double perKmRate = 10;
            return "₹" + (baseFare + km * perKmRate);
        } catch (Exception e) {
            return "N/A";
        }
    } */


//    @PostMapping("/request-ride")
//    public ResponseEntity<?> requestRide(@RequestBody RideRequestDTO request) {
//        try {
//            List<Map<String, Object>> nearbyDriverGroups = rideRequestService.processRideRequest(request);
//            return ResponseEntity.ok(nearbyDriverGroups);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }


    @PostMapping("/request")
    public ResponseEntity<?> requestRide(@RequestBody RideRequestDriverDTO request) {
        try {
             rideRequestService.broadcastRideToNearbyDrivers(request);
            return ResponseEntity.ok("Ride request broadcasted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptRide(@RequestBody AcceptRideDTO request) {
           rideRequestService.acceptRide(request);
        return ResponseEntity.ok("Ride accepted successfully");
    }

}
