package com.maven.Rapido.serviceImp;


import com.maven.Rapido.emun.OtpStatus;
import com.maven.Rapido.emun.RideStatus;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.RideRequest;
import com.maven.Rapido.model.User;
import com.maven.Rapido.payload.request.driver.*;
import com.maven.Rapido.payload.request.notification.PushNotificationRequest;
import com.maven.Rapido.repository.RideRequestRepository;
import com.maven.Rapido.repository.UserRepository;
import com.maven.Rapido.service.FCMService;
import com.maven.Rapido.utils.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideRequestService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final GoogleMapsService googleMapsService;
    private final RideRequestRepository rideRequestRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;


    private static final String RIDE_KEY_PREFIX = "ride:request:";

    // Here will be change -- how to identify each driver mobile device for send ride request of user.
    public void broadcastRideToNearbyDrivers(RideRequestDriverDTO rideRequest) {
        double pickupLat = rideRequest.getPickupLattitute();
        double pickupLng = rideRequest.getPickupLongitude();
        double dropLat = rideRequest.getDropLattitute();
        double dropLng = rideRequest.getDropLongitude();
        String vehicleType = rideRequest.getVehicleType();
        BigDecimal estimatedFare = rideRequest.getEstimatedFare();
        BigDecimal tips = rideRequest.getTips();
        Long userId = rideRequest.getUserId();

        // Null and range checks
        if (pickupLat == 0 || pickupLng == 0 || dropLat == 0 || dropLng == 0) {
            throw new APIException("Pickup/Drop location must be provided");
        }

        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            throw new APIException("Vehicle type must be specified");
        }

        if (estimatedFare == null || estimatedFare.compareTo(BigDecimal.ZERO) <= 0) {
            throw new APIException("Estimated fare must be greater than 0");
        }

        if (tips == null || tips.compareTo(BigDecimal.ZERO) < 0) {
            throw new APIException("Tips cannot be negative");
        }

        if (userId == null || userId <= 0) {
            throw new APIException("Invalid user ID");
        }

        // Retrieve all active drivers
        Map<Object, Object> allDriversMap = redisTemplate.opsForHash().entries("drivers");
        List<DriverLocationDTO> allDrivers = allDriversMap.values().stream()
                .map(obj -> (DriverLocationDTO) obj)
                .toList();

        // Filter nearby drivers within 3 km
        List<DriverLocationDTO> nearbyDrivers = allDrivers.stream()
                .filter(driver -> driver.getVehicleType().equalsIgnoreCase(vehicleType))
                .filter(driver -> haversineDistance(pickupLat, pickupLng, driver.getLat(), driver.getLng()) <= 3.0)
                .toList();

        if (nearbyDrivers.isEmpty()) {
            throw new RuntimeException("No nearby drivers found");
        }

        // Calculate pickup to drop details once
        Map<String, String> pickupToDrop = googleMapsService
                .getDistanceAndETA(pickupLat, pickupLng, dropLat, dropLng)
                .block();

        String rideDistance = pickupToDrop.get("distance");
        String rideEta = pickupToDrop.get("eta");

        //Ride Id
        String rideRequestId = "RD" + UUID.randomUUID().toString().substring(0, 8);

        // Here will be change -- how to identify the each driver mobile device for send ride request of user.
        // For each nearby driver, calculate distance/ETA from driver to pickup, then broadcast
        for (DriverLocationDTO driver : nearbyDrivers) {
            Map<String, String> driverToPickup = googleMapsService
                    .getDistanceAndETA(driver.getLat(), driver.getLng(), pickupLat, pickupLng)
                    .block();

            Optional<User> DBdriver = userRepository.findById(driver.getDriverId());
            if(DBdriver.isEmpty()){
                throw new APIException("Driver not Exists" + driver.getDriverId());
            }
            User Driver = DBdriver.get();

            RideRequestSocketPayload payload = RideRequestSocketPayload.builder()
                    .rideId(rideRequestId)
                    .passengerId(userId)
                    .pickupLat(pickupLat)
                    .pickupLng(pickupLng)
                    .dropLat(dropLat)
                    .dropLng(dropLng)
                    .vehicleType(vehicleType)
                    .estimatedFare(estimatedFare)
                    .tips(tips)
                    .distanceToPickup(driverToPickup.get("distance"))
                    .etaToPickup(driverToPickup.get("eta"))
                    .rideDistance(rideDistance)
                    .rideEta(rideEta)
                    .build();

            messagingTemplate.convertAndSend("/topic/driver/" + driver.getDriverId() + "/ride-request", payload);

            // Send The Push Notification
            PushNotificationRequest request = new PushNotificationRequest();
            request.setTitle("Title");
            request.setMessage("Message");
            request.setToken(Driver.getFcm_token());
            try {
                fcmService.sendPushNotificationToToken(request);
            } catch (Exception e) {
                log.error("Failed to send FCM notification to driver: {}", driver.getDriverId(), e);
            }

        }

        // Save ride request to database
        RideRequest rideRequestEntity = RideRequest.builder()
                .rideRequestId(rideRequestId)
                .userId(userId)
                .pickupLat(pickupLat)
                .pickupLng(pickupLng)
                .dropLat(dropLat)
                .dropLng(dropLng)
                .vehicleType(vehicleType)
                .estimatedFare(estimatedFare)
                .tips(tips)
                .status(RideStatus.PENDING.name())
                .createdAt(LocalDateTime.now())
                .build();

        rideRequestRepository.saveAndFlush(rideRequestEntity);

        // Save ride to Redis for 30 seconds
        RideRequestSocketPayload redisPayload = RideRequestSocketPayload.builder()
                .rideId(rideRequestId)
                .passengerId(userId)
                .pickupLat(pickupLat)
                .pickupLng(pickupLng)
                .dropLat(dropLat)
                .dropLng(dropLng)
                .vehicleType(vehicleType)
                .estimatedFare(estimatedFare)
                .tips(tips)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .pendingDriverIds(nearbyDrivers.stream().map(DriverLocationDTO::getDriverId).toList())
                .build();

        redisTemplate.opsForValue().set(RIDE_KEY_PREFIX + rideRequestId, redisPayload, 30, TimeUnit.SECONDS);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public void acceptRide(AcceptRideDTO request) {
        String rideRequestId = request.getRideRequestId();
        Long driverId = request.getDriverId();
        Long userId = request.getUserId();
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new APIException("User not found with this Id"));

        String redisKey = RIDE_KEY_PREFIX + rideRequestId;

        // 1. Get ride data from Redis
        RideRequestSocketPayload ride = (RideRequestSocketPayload) redisTemplate.opsForValue().get(redisKey);
        if (ride == null) {
            throw new APIException("Ride request expired or not found.");
        }

        // 2. Validate ride status
        if (!ride.getStatus().equalsIgnoreCase(RideStatus.PENDING.name())) {
            throw new APIException("Ride already accepted or expired.");
        }

        // 3. Find ride request in DB
        RideRequest rideRequest = rideRequestRepository.findByRequestId(rideRequestId, userId);
        if (rideRequest == null) {
            throw new APIException("Ride request not found for the user.");
        }

        // 4. Mark ride as accepted
        rideRequest.setStatus(RideStatus.ACCEPTED.name());
        rideRequest.setAcceptedDriverId(driverId);
        rideRequest.setOtp(existingUser.getRideOtp());
        rideRequest.setOtpStatus(OtpStatus.PENDING.name());
        rideRequestRepository.save(rideRequest);

        // 5. Delete ride request from Redis
        //redisTemplate.delete(redisKey);

        // 6. Notify other drivers
        List<Long> otherDrivers = ride.getPendingDriverIds().stream()
                .filter(id -> !id.equals(String.valueOf(driverId)))
                .toList();

        for (Long otherId : otherDrivers) {
            messagingTemplate.convertAndSend("/topic/driver/" + otherId.toString() + "/ride-expired", rideRequestId);
        }

        // 7. Fetch driver details
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new APIException("Driver not found."));

        // 9. Fetch driver's location from Redis ("drivers" hash, assuming correct format)
        DriverLocationDTO driverLocation = (DriverLocationDTO) redisTemplate
                .opsForHash()
                .get("drivers", driverId.toString());

        double currentLat = driverLocation != null ? driverLocation.getLat() : 0;
        double currentLng = driverLocation != null ? driverLocation.getLng() : 0;

        // 9. Prepare payload
        DriverAcceptedPayload payload = DriverAcceptedPayload.builder()
                .driverId(driverId)
                .driverName(driver.getUserName())
                .phoneNumber(driver.getPhoneNumber())
                .currentLat(currentLat)
                .currentLng(currentLng)
                .vehicleType(driver.getVehicleCategory().getName()) // assuming it's a String
                .vehicleNumber(driver.getPhoneNumber())           // use actual vehicle number
                .otp(existingUser.getRideOtp())
                .build();

        // 10. Notify user
        messagingTemplate.convertAndSend("/topic/user/" + ride.getPassengerId().toString() + "/driver-accepted", payload);

        //TODO
        // send notification to user with otp about Ride accept.
    }



}
