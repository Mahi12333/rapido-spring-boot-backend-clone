package com.maven.Rapido.serviceImp;

import com.maven.Rapido.exception.ResourceNotFoundException;
import com.maven.Rapido.model.User;
import com.maven.Rapido.model.VehicleCategory;
import com.maven.Rapido.payload.request.driver.DriverProfileDTO;
import com.maven.Rapido.payload.response.driver.DriverResponse;
import com.maven.Rapido.repository.UserRepository;
import com.maven.Rapido.repository.VehicleTypeRepository;
import com.maven.Rapido.service.CloudinaryService;
import com.maven.Rapido.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImp implements DriverService {
    private final UserRepository userRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public void saveBasicDetails(Long id, String firstName, String lastName, String userName, String email, String dob, Integer adharNumber) {
        User driver = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setUserName(userName);
        driver.setEmail(email);
        driver.setDob(dob);
        driver.setAdharnumber(adharNumber);
        driver.setStep(1); // Update current step
        userRepository.save(driver);
    }

    @Override
    public void saveVehicleDetails(Long id, String currentAddress, String permanentAddress, Long vehicleId) {
        User driver = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        VehicleCategory existingVehicleCategory = vehicleTypeRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle category not found!"));
        driver.setCurrentAddress(currentAddress);
        driver.setPermanentAddress(permanentAddress);
        driver.setVehicleCategory(existingVehicleCategory);
        driver.setStep(2);
        userRepository.save(driver);
    }

    @Override
    public void saveDocuments(Long id, MultipartFile idProof, MultipartFile drivingLicence, MultipartFile pancard, MultipartFile dobCertificate) {
        // TODO
        User driver = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        ExecutorService executor = Executors.newFixedThreadPool(4); // You can customize pool size

        List<CompletableFuture<String>> futures = new ArrayList<>();

        if (idProof != null && !idProof.isEmpty()) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Uploading ID Proof: {}", idProof.getOriginalFilename());
                    return cloudinaryService.uploadFile(idProof, "driver_uploads", 5 * 1024 * 1024);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executor));
        } else {
            futures.add(CompletableFuture.completedFuture(null));
        }

        if (drivingLicence != null && !drivingLicence.isEmpty()) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Uploading Driving Licence: {}", drivingLicence.getOriginalFilename());
                    return cloudinaryService.uploadFile(drivingLicence, "driver_uploads", 5 * 1024 * 1024);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executor));
        } else {
            futures.add(CompletableFuture.completedFuture(null));
        }

        if (pancard != null && !pancard.isEmpty()) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Uploading Pancard: {}", pancard.getOriginalFilename());
                    return cloudinaryService.uploadFile(pancard, "driver_uploads", 5 * 1024 * 1024);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executor));
        } else {
            futures.add(CompletableFuture.completedFuture(null));
        }

        if (dobCertificate != null && !dobCertificate.isEmpty()) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Uploading DOB Certificate: {}", dobCertificate.getOriginalFilename());
                    return cloudinaryService.uploadFile(dobCertificate, "driver_uploads", 5 * 1024 * 1024);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executor));
        } else {
            futures.add(CompletableFuture.completedFuture(null));
        }

        List<String> urls = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        executor.shutdown();

        // Assuming your User entity has these fields as Strings for URLs
        driver.setIdProof(urls.get(0));
        driver.setDrivingLicence(urls.get(1));
        driver.setPancard(urls.get(2));
        driver.setDobCertificate(urls.get(3));
        driver.setStep(3); // Update step

        userRepository.save(driver);
    }

    @Override
    public DriverResponse getDriverProfile(Long id) {
        User driver = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        DriverResponse driverResponse = new DriverResponse();
        driverResponse.setId(driver.getId());
        driverResponse.setFirstName(driver.getFirstName());
        driverResponse.setLastName(driver.getLastName());
        driverResponse.setPhoneNumber(driver.getPhoneNumber());
        driverResponse.setCountryCode(driver.getCountry_code());
        driverResponse.setFcmToken(driver.getFcm_token());
        driverResponse.setIsVerified(driver.getIsVerified());
        driverResponse.setStatus((driver.getStatus().name()));
        driverResponse.setRole(driver.getRole().getRoleName().name());
        driverResponse.setUserName(driver.getUserName());
        driverResponse.setEmail(driver.getEmail());
        driverResponse.setAccountNonLocked(driver.getAccountNonLocked());
        driverResponse.setAccountNonExpired(driver.getAccountNonExpired());
        driverResponse.setCredentialsNonExpired(driver.getCredentialsNonExpired());
        driverResponse.setEnabled(driver.getEnabled());
        driverResponse.setCredentialsExpiryDate(driver.getCredentialsExpiryDate());
        driverResponse.setIdProof(driver.getIdProof());
        driverResponse.setProfile(driver.getProfile());
        driverResponse.setDrivingLicence(driver.getDrivingLicence());
        driverResponse.setPancard(driver.getPancard());
        driverResponse.setCreatedAt(driver.getCreatedAt());
        driverResponse.setUpdatedAt(driver.getUpdatedAt());
        driverResponse.setDob(driver.getDob());
        driverResponse.setDobCertificate(driver.getDobCertificate());
        driverResponse.setStep(driver.getStep());
        driverResponse.setAdharnumber(driver.getAdharnumber());
        driverResponse.setVehicleType(driver.getVehicleCategory().getName().name());
        return driverResponse;
    }

    @Override
    public String adharVerify(Integer adharNumber) {
        // TODO
        return "";
    }


}
