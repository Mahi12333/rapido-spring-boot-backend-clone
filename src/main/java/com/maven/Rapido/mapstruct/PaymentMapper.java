package com.maven.Rapido.mapstruct;


import com.maven.Rapido.model.UserWallet;
import com.maven.Rapido.payload.response.payment.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PaymentMapper {
    //! This tells MapStruct to set the createdAt field on the target object using a custom method getCurrentDateTime()
    //! when mapping from the source to the target.

    //@Mapping(target = "createdAt", expression = "java(getCurrentDateTime())")

    @Mapping(target = "razorpaySingnature", ignore = true)
    PaymentResponse toCreatePaymentResponse(UserWallet createPayment);
    PaymentResponse toVerifyPaymentResponse(UserWallet createPayment);
}
