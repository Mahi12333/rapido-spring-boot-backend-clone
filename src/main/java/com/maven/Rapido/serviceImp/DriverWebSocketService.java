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
        redisTemplate.opsForHash().put("drivers", locationDTO.getDriverId(), locationDTO);
    }

    public void sendLocationToUser(DriverLocationSendUserDTO location) {
        log.info("Sending driver location to user: {}", location);
        redisTemplate.opsForHash().put("drivers", location.getDriverId(), location);
        /*messagingTemplate.convertAndSend(
                "/topic/user/" + location.getUserId() + "/driver-location",
                location
        );*/
        // Send to specific user
        messagingTemplate.convertAndSendToUser(
                String.valueOf(location.getUserId()),  // must match Principal.getName()
                "/queue/driver-location",
                location
        );
    }

    public void removeDriver(String driverId) {
        redisTemplate.opsForHash().delete("drivers", driverId);
    }
}
