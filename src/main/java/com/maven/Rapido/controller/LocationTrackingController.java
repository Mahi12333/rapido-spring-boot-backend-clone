package com.maven.Rapido.controller;

import com.maven.Rapido.payload.request.driver.DriverLocationDTO;
import com.maven.Rapido.payload.request.driver.DriverLocationSendUserDTO;
import com.maven.Rapido.serviceImp.DriverWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;



@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationTrackingController {
    private final DriverWebSocketService webSocketService;


    @MessageMapping("/send-to-user")
    public void updateLocation(@Payload DriverLocationSendUserDTO location) {
        webSocketService.sendLocationToUser(location);
    }

    @MessageMapping("/update-location")
    public void updateDriverLocation(@Payload DriverLocationDTO locationDTO) {
        System.out.println("✅ Received location from driver: " + locationDTO);
        webSocketService.handleLocationUpdate(locationDTO);
    }

    @MessageMapping("/driver-exit-driver-mode")
    public void removeDriverFromRedis(@Payload String driverId) {
        webSocketService.removeDriver(driverId);
    }


}
