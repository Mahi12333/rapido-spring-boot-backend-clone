package com.maven.Rapido.payload.request.login;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class PhoneDTO {
   private String phone_number;
   private String country_code;
}
