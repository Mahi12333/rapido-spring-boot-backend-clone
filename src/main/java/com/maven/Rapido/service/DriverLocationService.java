package com.maven.Rapido.service;

import com.maven.Rapido.payload.request.driver.DriverCurrentLocationDTO;
import jakarta.validation.Valid;

public interface DriverLocationService {
    void updateLocation(DriverCurrentLocationDTO request);
}
