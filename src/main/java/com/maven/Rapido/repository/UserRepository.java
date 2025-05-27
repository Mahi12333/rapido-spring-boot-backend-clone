package com.maven.Rapido.repository;

import com.maven.Rapido.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);

    User save(User user);

    void delete(User user);

   @Query("SELECT u FROM User u WHERE u.phoneNumber = :phone AND u.country_code = :country_code")
    Optional<User> findByPhoneNumberAndCountryCode(@Param("phone") String phone, @Param("country_code") String countryCode);
}
