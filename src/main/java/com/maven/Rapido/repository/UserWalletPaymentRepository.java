package com.maven.Rapido.repository;


import com.maven.Rapido.emun.PaymentStatus;
import com.maven.Rapido.model.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserWalletPaymentRepository extends JpaRepository<UserWallet, Long> {
    @Query("SELECT uw FROM UserWallet uw WHERE uw.status = :paymentStatus AND uw.createdAt < :time")
    List<UserWallet> findByStatusAndCreatedAtBefore(
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("time") LocalDateTime time
    );


    UserWallet findByRazorpayOrderId(String razorpayOrderId);
}
