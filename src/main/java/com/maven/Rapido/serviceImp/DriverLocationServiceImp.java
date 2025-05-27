package com.maven.Rapido.serviceImp;

import com.maven.Rapido.mapstruct.LocationMapper;
import com.maven.Rapido.model.DriverLocation;
import com.maven.Rapido.payload.request.driver.DriverCurrentLocationDTO;
import com.maven.Rapido.repository.DriverLocationRepository;
import com.maven.Rapido.service.DriverLocationService;
import com.maven.Rapido.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DriverLocationServiceImp implements DriverLocationService {

    private final DriverLocationRepository repository;
    private final LocationMapper locationMapper;

    public void updateLocation(DriverCurrentLocationDTO dto) {
        DriverLocation entity = locationMapper.toEntity(dto);
        repository.save(entity);
    }
}
