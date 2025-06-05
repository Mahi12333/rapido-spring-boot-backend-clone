package com.maven.Rapido.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.model.OtpVerify;
import com.maven.Rapido.payload.request.driver.DriverProfileDTO;
import com.maven.Rapido.payload.response.CommonResponseDTO;
import com.maven.Rapido.service.DriverService;
import com.maven.Rapido.utils.AuthUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Tag(name = "DriverController", description = "Driver Management")
@RestController
@RequestMapping("/v1/api/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;
    private final AuthUtil authUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping(value = "/create-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createOrUpdateDriverProfile(
            @RequestPart("profile") String profileDTO,
            @RequestPart(name = "idProof", required = false) MultipartFile idProof,
            @RequestPart(name = "drivingLicence", required = false) MultipartFile drivingLicence,
            @RequestPart(name = "pancard", required = false) MultipartFile pancard,
            @RequestPart(name = "dobCertificate", required = false) MultipartFile dobCertificate) {


        ObjectMapper objectMapper = new ObjectMapper();
        DriverProfileDTO request;
        try {
            request = objectMapper.readValue(profileDTO, DriverProfileDTO.class);
        } catch (JsonProcessingException e) {
            log.info("Error parsing JSON: {}", e.getMessage());
            throw new APIException("Invalid JSON format");
        }

        if (request.getStep() == 3) {
            // In step 3, check if required files are available
            if (idProof == null || drivingLicence == null || pancard == null || dobCertificate == null) {
                throw new APIException("error.driver.documents.required");
            }
            driverService.saveDocuments(request.getId(), idProof, drivingLicence, pancard, dobCertificate);
        } else if (request.getStep() == 1) {
            log.info("Save the Data for step 1 for user ID: {}", request.getId());
            driverService.saveBasicDetails(request.getId(), request.getFirstName(), request.getFirstName(), request.getUserName(), request.getEmail(), request.getDob(), request.getAdharNumber());
        } else if (request.getStep() == 2) {
            log.info("Save the Data for step 2 for user ID: {}", request.getId());
            driverService.saveVehicleDetails(request.getId(), request.getCurrentAddress(), request.getPermanentAddress(), request.getVehicleId());
        } else {
            return ResponseEntity.badRequest().body("Invalid step");
        }

        CommonResponseDTO<Object> responseBody = new CommonResponseDTO<>(
                null,
                "success.driver.profile.created"
        );
        return ResponseEntity.ok(responseBody);
    }


    @GetMapping("/get-profile")
    public ResponseEntity<?> getDriverProfile() {
        Long id = authUtil.loggedInUserId();
        log.info("Fetching driver profile for user ID: {}", id);
        return ResponseEntity.ok(driverService.getDriverProfile(id));
    }

    @PostMapping("/adhar-verify")
    public ResponseEntity<?> adharVerify(@Valid @RequestBody Integer AdharNumber ){
        String response = driverService.adharVerify(AdharNumber);
        return ResponseEntity.ok("Verify successfully Done!");
    }



}
