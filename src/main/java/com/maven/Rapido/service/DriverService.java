package com.maven.Rapido.service;

import com.maven.Rapido.model.User;
import com.maven.Rapido.payload.request.driver.DriverProfileDTO;
import com.maven.Rapido.payload.response.driver.DriverResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface DriverService {
    void saveBasicDetails(Long id, String firstName, String lastName, String userName, String email, String dob, Integer adharNumber);
    void saveVehicleDetails(Long id, String currentAddress, String permanentAddress, Long vehicleId);
    void saveDocuments(Long id, MultipartFile idProof, MultipartFile drivingLicence, MultipartFile pancard, MultipartFile dobCertificate);
    DriverResponse getDriverProfile(Long id);
    String adharVerify(Integer adharNumber);
}
