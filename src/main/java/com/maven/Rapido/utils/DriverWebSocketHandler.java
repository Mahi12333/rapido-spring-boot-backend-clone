//package com.maven.Rapido.utils;
//
//
//import com.maven.Rapido.serviceImp.DriverWebSocketService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequiredArgsConstructor
//public class DriverWebSocketHandler {
//    private final DriverWebSocketService webSocketService;
//
//    @MessageMapping("/driver-update-location-send-to-user")
//    public void updateLocation(@Payload DriverLocationSendUserDTO location) {
//        webSocketService.sendLocationToUser(location);
//    }
//
//    @MessageMapping("/update-location")
//    public void updateDriverLocation(@Payload DriverLocationDTO locationDTO) {
//        webSocketService.handleLocationUpdate(locationDTO);
//    }
//
//    @MessageMapping("/driver-exit-driver-mode")
//    public void removeDriverFromRedis(@Payload String driverId) {
//        webSocketService.removeDriver(driverId);
//    }
//}
