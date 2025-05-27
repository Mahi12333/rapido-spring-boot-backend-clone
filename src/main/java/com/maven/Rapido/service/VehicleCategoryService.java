package com.maven.Rapido.service;

import com.maven.Rapido.model.VehicleCategory;
import com.maven.Rapido.payload.request.vehicle.VehicalCreateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VehicleCategoryService {
    void createVehicleCategory(VehicalCreateDTO request, MultipartFile image) throws IOException;
    List<VehicleCategory> getAllVehicleCategory();
}
