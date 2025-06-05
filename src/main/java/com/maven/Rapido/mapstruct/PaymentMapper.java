package com.maven.Rapido.mapstruct;


import com.maven.Rapido.model.UserWallet;
import com.maven.Rapido.payload.response.payment.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "razorpaySingnature", ignore = true)
    PaymentResponse toCreatePaymentResponse(UserWallet createPayment);

    PaymentResponse toVerifyPaymentResponse(UserWallet createPayment);
}
