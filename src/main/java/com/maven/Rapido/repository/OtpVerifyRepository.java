package com.maven.Rapido.repository;

import com.maven.Rapido.model.OtpVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerifyRepository extends JpaRepository<OtpVerify, Long> {
    OtpVerify findByPhoneNumber(String phoneNumber);

    OtpVerify save(OtpVerify otpVerify);

    //void delete(OtpVerify otpVerify);

    @Query("SELECT o FROM OtpVerify o WHERE o.phoneNumber = :phone AND o.countryCode = :countrycode")
    OtpVerify findByPhoneNumberAndUserId(@Param("phone") String phone, @Param("countrycode") String countryCode);

    @Query("SELECT o FROM OtpVerify o WHERE o.phoneNumber = :phone AND o.countryCode = :country_code AND o.otp = :otp")
    Optional<OtpVerify> findByPhoneNumberAndCountryCodeAndOtp(@Param("phone") String phone, @Param("country_code") String countryCode, @Param("otp") Integer otp);

    @Query("SELECT o FROM OtpVerify o WHERE o.phoneNumber = :phone AND o.countryCode = :country_code")
    Optional<OtpVerify> findByPhoneNumberAndCountryCode(@Param("phone") String phone, @Param("country_code") String countryCode);
}
