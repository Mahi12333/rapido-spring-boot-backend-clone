package com.maven.Rapido.service;

import com.maven.Rapido.model.VehicleCategory;
import com.maven.Rapido.payload.request.vehicle.VehicalCreateDTO;
import com.maven.Rapido.payload.response.vehicle.VehicleResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public interface VehicleCategoryService {
    void createVehicleCategory(VehicalCreateDTO request, MultipartFile image) throws IOException;
    List<VehicleResponseDTO> getAllVehicleCategory(Locale locale);
}
