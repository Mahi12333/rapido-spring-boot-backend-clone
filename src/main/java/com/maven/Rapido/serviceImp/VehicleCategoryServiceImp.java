package com.maven.Rapido.serviceImp;

import com.maven.Rapido.emun.VehicleType;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.VehicleCategory;
import com.maven.Rapido.payload.request.vehicle.VehicalCreateDTO;
import com.maven.Rapido.repository.VehicleTypeRepository;
import com.maven.Rapido.service.CloudinaryService;
import com.maven.Rapido.service.VehicleCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleCategoryServiceImp implements VehicleCategoryService {
    private final CloudinaryService cloudinaryService;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    @Transactional
    public void createVehicleCategory(VehicalCreateDTO request, MultipartFile image) throws IOException {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            log.info("Uploaded profile URL: {}", image.getOriginalFilename());
             imageUrl = cloudinaryService.uploadFile(image, "user_uploads", 35 * 1024 * 1024);
        }
        Set<VehicleType> allowedTypes = EnumSet.of(
                VehicleType.CAR,
                VehicleType.AC_CAR,
                VehicleType.AUTO,
                VehicleType.BIKE
        );
        VehicleType vehicleType;
        try {
            vehicleType = VehicleType.valueOf(request.getName().toUpperCase());
            if (!allowedTypes.contains(vehicleType)) {
                throw new APIException("Vehicle type not allowed: " + request.getName());
            }
        } catch (IllegalArgumentException e) {
            throw new APIException("Invalid vehicle type: " + request.getName());
        }
        VehicleCategory response = new VehicleCategory();
       // response.setCreatedAt(LocalDateTime.now());
        response.setName(vehicleType);
        response.setDescription(request.getDescription());
        response.setImageUrl(imageUrl);
        //response.setUpdatedAt(LocalDateTime.now());

        vehicleTypeRepository.save(response);
    }

    @Override
    @Transactional
    public List<VehicleCategory> getAllVehicleCategory() {
        return vehicleTypeRepository.findAll();
    }
}
