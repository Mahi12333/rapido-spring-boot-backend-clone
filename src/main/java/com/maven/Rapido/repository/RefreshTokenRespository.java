package com.maven.Rapido.repository;


import com.maven.Rapido.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRespository extends JpaRepository<RefreshToken, Long> {
    void deleteByUserId(Long id);
    @Query("SELECT r FROM RefreshToken r WHERE r.refreshtoken = :refresh_token")
    Optional<RefreshToken> findByToken(@Param("refresh_token") String refreshToken);
}
