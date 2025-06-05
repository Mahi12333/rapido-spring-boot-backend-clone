package com.maven.Rapido.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.OtpVerify;
import com.maven.Rapido.model.VehicleCategory;
import com.maven.Rapido.payload.request.vehicle.VehicalCreateDTO;
import com.maven.Rapido.payload.response.CommonResponseDTO;
import com.maven.Rapido.payload.response.vehicle.VehicalResponse;
import com.maven.Rapido.payload.response.vehicle.VehicleResponseDTO;
import com.maven.Rapido.service.VehicleCategoryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Tag(name = "VehicleCategoryController", description = "VehicleCategory Management")
@RestController
@RequestMapping("/v1/api/vehiclecategory")
@RequiredArgsConstructor
public class VehicleCategoryController {
    private final VehicleCategoryService vehicleCategoryService;
    private final MessageSource messageSource;

    @PostMapping(value = "/create-vehicle-category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createVehicleCategory(@RequestPart("vehicalCreateDTO") @Valid String vehicalCreateDTO,
                                                   @Parameter(schema = @Schema(type = "string", format = "binary", required = false, description = "Local Image Upload"))
                                                        @RequestPart(value = "image", required = false) MultipartFile image, Locale locale) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        VehicalCreateDTO request;
        try {
            request = objectMapper.readValue(vehicalCreateDTO, VehicalCreateDTO.class);
            log.info("Received request to create vehicle category: {}", request);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON request: {}", e.getMessage());
            throw new APIException("Invalid JSON format");
        }
        vehicleCategoryService.createVehicleCategory(request, image);
        return ResponseEntity.ok("Vehicle category created successfully");
    }

    @GetMapping("/get-all-vehicle-category")
    public ResponseEntity<?> getAllVehicleCategory(Locale locale) {
        List<VehicleResponseDTO> response = vehicleCategoryService.getAllVehicleCategory(locale);
        String message = messageSource.getMessage("success.vehicle.category", null, locale);
        CommonResponseDTO<List<VehicleResponseDTO>> responseBody = new CommonResponseDTO<>(
                response,
                message
        );
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
