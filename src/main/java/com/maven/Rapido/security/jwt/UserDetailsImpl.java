package com.maven.Rapido.security.jwt;

import com.maven.Rapido.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String phoneNumber;
    private final String countryCode;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String phoneNumber, String countryCode, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getPhoneNumber(),
                user.getCountry_code(),
                Collections.emptyList()
        );
    }



    public Long getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // or throw UnsupportedOperationException if not used
    }

    @Override
    public String getUsername() {
        //TODO
        //return phoneNumber; // Spring uses this for identifying the user
        return String.valueOf(id); //! ✅ Return userId as String
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
