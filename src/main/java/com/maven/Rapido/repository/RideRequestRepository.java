package com.maven.Rapido.repository;

import com.maven.Rapido.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    @Query("SELECT r FROM RideRequest r WHERE r.rideRequestId = :riderequestid AND r.userId = :user_id")
    RideRequest findByRequestId(@Param("riderequestid") String rideRequestId, @Param("user_id") Long userId);
}
