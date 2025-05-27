package com.maven.Rapido.controller;


import com.maven.Rapido.payload.request.driver.DriverCurrentLocationDTO;
import com.maven.Rapido.payload.request.driver.RideRequestDTO;
import com.maven.Rapido.service.DriverLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/v1/location")
@RequiredArgsConstructor
public class LocationController {
    private final DriverLocationService driverLocationService;

    @PostMapping("/driver-location-create")
    public ResponseEntity<?> findNearByDriver(@Valid @RequestBody DriverCurrentLocationDTO request){
        driverLocationService.updateLocation(request);

        return ResponseEntity.ok("Driver location updated successfully");
    }


}
