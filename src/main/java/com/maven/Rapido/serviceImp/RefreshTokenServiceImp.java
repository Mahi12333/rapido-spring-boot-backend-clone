package com.maven.Rapido.serviceImp;

import com.maven.Rapido.exception.ResourceNotFoundException;
import com.maven.Rapido.model.RefreshToken;
import com.maven.Rapido.repository.RefreshTokenRespository;
import com.maven.Rapido.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImp implements RefreshTokenService {
    private final RefreshTokenRespository refreshTokenRepository;

    @Override
    public void deleteByUserId(Long id) {
        refreshTokenRepository.deleteByUserId(id);
    }

    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    @Override
    public void deleteByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        refreshTokenRepository.delete(refreshToken);
    }
}
