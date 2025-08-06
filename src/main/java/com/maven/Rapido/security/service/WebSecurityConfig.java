package com.maven.Rapido.security.service;

import com.maven.Rapido.security.jwt.AuthEntryPointJwt;
import com.maven.Rapido.security.jwt.AuthTokenFilter;
import com.maven.Rapido.security.jwt.CustomActuatorIPFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final AuthEntryPointJwt unauthorizedHandler;
    //private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final AuthTokenFilter authTokenFilter;
    private final CustomActuatorIPFilter customActuatorIPFilter;


    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails adminUser = User.withUsername("actuator_admin")
                .password(passwordEncoder().encode("StrongInternalPassword123!"))
                .roles("ACTUATOR_ADMIN")
                .build();
        return new InMemoryUserDetailsManager(adminUser);
    }


    @Bean
    //@Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
       /* http.csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/public/**")
        );*/
        //http.securityMatcher("/**"); // ✅ Explicitly apply to all other paths
        http.cors(withDefaults());
        //! Disable the Csrf Token
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/api/auth/**").permitAll()
                .requestMatchers("/v1/api/feed/presign-file-upload/**").permitAll() // file upload presign url
                .requestMatchers("/api/csrf-token").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/socket/**").permitAll()
                .requestMatchers("/app/**").permitAll()
                .requestMatchers("/topic/**").permitAll()
                .requestMatchers("/queue/**").permitAll()
                 .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/redis-test/**").permitAll()
                .anyRequest().authenticated());
                //.anyRequest().authenticated());
                /*http.oauth2Login(oauth2 ->
                oauth2.successHandler(oAuth2LoginSuccessHandler));*/

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler));

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        log.info("Security Configuration Loaded Successfully");

        return http.build();
    }

    /**
     * Security configuration for Actuator endpoints.
     * This configuration allows access to health and info endpoints without authentication,
     * while securing other actuator endpoints to be accessible only from a specific IP range.
     */

//    @Bean
//    @Order(1)
//    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/actuator/**")
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
//                        .requestMatchers("/actuator/**").hasRole("ACTUATOR_ADMIN")
//                )
//                .httpBasic(withDefaults()) // Use HTTP Basic Auth for internal tools
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(customActuatorIPFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }





    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Increased security strength
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("webSecurityCustomizer-----");
        return (web) -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-ui.html"
        );
    }

}
