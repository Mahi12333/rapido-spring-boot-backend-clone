package com.maven.Rapido.mapstruct;


import com.maven.Rapido.model.DriverLocation;
import com.maven.Rapido.payload.request.driver.DriverCurrentLocationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);


    @Mapping(source = "driverId", target = "driverid")
    DriverLocation toEntity(DriverCurrentLocationDTO dto);
}
