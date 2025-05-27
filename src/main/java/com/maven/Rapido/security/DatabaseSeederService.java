package com.maven.Rapido.security;

import com.maven.Rapido.emun.UserRole;
import com.maven.Rapido.model.Role;
import com.maven.Rapido.repository.RoleRepository;
import com.maven.Rapido.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DatabaseSeederService implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;


    @Override
    public void run(String... args) throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("SYSTEM", null));

        Role userRole = roleRepository.findByRoleName(UserRole.USER)
                .orElseGet(() -> roleRepository.save(new Role(UserRole.USER)));

        Role sellerRole = roleRepository.findByRoleName(UserRole.DRIVER)
                .orElseGet(() -> roleRepository.save(new Role(UserRole.DRIVER)));

        Role delivaryRole = roleRepository.findByRoleName(UserRole.DELIVERY)
                .orElseGet(() -> roleRepository.save(new Role(UserRole.DELIVERY)));

        Role adminRole = roleRepository.findByRoleName(UserRole.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(UserRole.ADMIN)));


    }
}
