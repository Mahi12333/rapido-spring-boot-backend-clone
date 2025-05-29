package com.maven.Rapido.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL}")
    private String frontendUrls;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration(); //"https://rapido-project-spring-boot.onrender.com"
        List<String> origins = Arrays.asList(frontendUrls.split(","));
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        //config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Required if using cookies or Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    //! Handle Interceptor------------------
    /*@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logsInterceptor)
                .addPathPatterns("/v1/api/auth/**");
        //  .excludePathPatterns("/api/auth/public/**");
    }*/

}
