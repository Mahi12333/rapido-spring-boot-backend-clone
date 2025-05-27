package com.maven.Rapido.payload.request.login;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignupDTO {
    private String phoneNumber;
    private String countryCode;
    private Integer otp;
    private String Fcm_Token;
}
