package com.maven.Rapido.payload.request.driver;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DriverCurrentLocationDTO {
    private Double lat;
    private Double lng;
    private Long driverId;
}
