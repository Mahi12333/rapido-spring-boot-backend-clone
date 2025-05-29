package com.maven.Rapido.serviceImp;

import com.maven.Rapido.payload.request.driver.DriverLocationDTO;
import com.maven.Rapido.payload.request.driver.DriverLocationSendUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverWebSocketService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public void handleLocationUpdate(DriverLocationDTO locationDTO) {
        //redisTemplate.opsForHash().put("drivers", locationDTO.getDriverId(), locationDTO);
        redisTemplate.opsForHash().put("drivers", String.valueOf(locationDTO.getDriverId()), locationDTO);

    }


    public void sendLocationToUser(DriverLocationSendUserDTO location) {
        // Fetch driver location from Redis
        Object driverData = redisTemplate.opsForHash().get("drivers", location.getDriverId().toString());

        if (driverData instanceof DriverLocationDTO) {
            DriverLocationDTO dto = (DriverLocationDTO) driverData;

            // Build the object to send to the user
            DriverLocationSendUserDTO sendDTO = DriverLocationSendUserDTO.builder()
                    .driverId(dto.getDriverId())
                    .lat(dto.getLat())
                    .lng(dto.getLng())
                    .vehicleType(dto.getVehicleType())
                    .available(dto.isAvailable())
                    .userId(location.getUserId())
                    .build();

            log.info("Sending driver location to user: {}", sendDTO);

            messagingTemplate.convertAndSendToUser(
                    location.getUserId().toString(),         // Must match the Principal.getName() (String)
                    "/queue/driver-location",  // Must match destination prefix
                    sendDTO
            );

        } else {
            log.error("Driver location not found or invalid for driverId: {}", location.getUserId());
        }
    }


    public void removeDriver(Long driverId) {
        redisTemplate.opsForHash().delete("drivers", driverId.toString());
    }
}
