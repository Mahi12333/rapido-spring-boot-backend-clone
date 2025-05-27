package com.maven.Rapido.repository;

import com.maven.Rapido.emun.VehicleType;
import com.maven.Rapido.model.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.OptionalDouble;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleCategory, Long> {
    OptionalDouble findByName(VehicleType vehicleType);
}
