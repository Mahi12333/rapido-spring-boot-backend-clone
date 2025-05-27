package com.maven.Rapido.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class OtpAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal; // phone number
    private final Object credentials; // otp
    private final String countryCode;

    public OtpAuthenticationToken(Object principal, String countryCode, Object credentials) {
        super(null);
        this.principal = principal;
        this.countryCode = countryCode;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public OtpAuthenticationToken(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = userDetails;
        this.credentials = null;
        this.countryCode = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
