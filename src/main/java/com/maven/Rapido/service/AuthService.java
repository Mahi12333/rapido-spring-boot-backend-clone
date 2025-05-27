package com.maven.Rapido.service;

import com.maven.Rapido.model.OtpVerify;
import com.maven.Rapido.payload.request.login.PhoneDTO;
import com.maven.Rapido.payload.request.login.SignupDTO;
import com.maven.Rapido.payload.response.user.UserResponse;

public interface AuthService {
     OtpVerify sendOtp(PhoneDTO request);
     UserResponse registerUser(SignupDTO request);
     OtpVerify resendOtp(PhoneDTO request);
}
