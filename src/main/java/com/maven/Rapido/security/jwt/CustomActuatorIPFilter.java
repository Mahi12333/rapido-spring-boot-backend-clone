package com.maven.Rapido.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
public class CustomActuatorIPFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String remoteAddr = request.getRemoteAddr();
        if (!remoteAddr.equals("127.0.0.1") && !remoteAddr.equals("::1")) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
