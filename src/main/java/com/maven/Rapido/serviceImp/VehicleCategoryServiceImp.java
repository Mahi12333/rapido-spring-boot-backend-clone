package com.maven.Rapido.serviceImp;

import com.maven.Rapido.emun.VehicleType;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.VehicleCategory;
import com.maven.Rapido.payload.request.vehicle.VehicalCreateDTO;
import com.maven.Rapido.payload.response.vehicle.VehicleResponseDTO;
import com.maven.Rapido.repository.VehicleTypeRepository;
import com.maven.Rapido.service.CloudinaryService;
import com.maven.Rapido.service.VehicleCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleCategoryServiceImp implements VehicleCategoryService {
    private final CloudinaryService cloudinaryService;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public void createVehicleCategory(VehicalCreateDTO request, MultipartFile image) throws IOException {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            log.info("Uploaded profile URL: {}", image.getOriginalFilename());
             imageUrl = cloudinaryService.uploadFile(image, "user_uploads", 35 * 1024 * 1024);
        }

        String normalizedName = request.getName().toUpperCase().trim();
        String slug;

        switch (normalizedName) {
            case "CAR":
                request.setName("Car");
                slug = VehicleType.CAR.name();
                break;
            case "BIKE":
                request.setName("Bike");
                slug = VehicleType.BIKE.name();
                break;
            case "AUTO":
                request.setName("Auto");
                slug = VehicleType.AUTO.name();
                break;
            case "AC CAR":
            case "AC_CAR":
                request.setName("AC Car");
                slug = VehicleType.AC_CAR.name();
                break;
            default:
                throw new APIException("Invalid vehicle type provided: " + request.getName());
        }

        VehicleCategory response = new VehicleCategory();
        response.setName(request.getName());
        response.setSlug(slug);
        response.setDescription(request.getDescription());
        response.setImageUrl(imageUrl);
        response.setUpdatedAt(LocalDateTime.now());
        response.setCreatedAt(LocalDateTime.now());

        vehicleTypeRepository.save(response);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAllVehicleCategory(Locale locale) {
        List<VehicleCategory> categories = vehicleTypeRepository.findAll();

        return categories.stream().map(category -> {
            String slug = category.getSlug(); // e.g., CAR, BIKE, etc.

            // Lookup localized name from message bundle
            String localizedName = messageSource.getMessage(
                    "vehicle.name." + slug.toLowerCase(),
                    null,
                    slug, // fallback
                    locale
            );
            log.info("localizedName-----{}",localizedName);

            return new VehicleResponseDTO(
                    category.getId(),
                    localizedName,
                    category.getDescription(),
                    category.getImageUrl(),
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );
        }).collect(Collectors.toList());
    }

}
