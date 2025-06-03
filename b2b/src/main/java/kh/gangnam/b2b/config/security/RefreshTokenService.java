package kh.gangnam.b2b.config.security;

import kh.gangnam.b2b.entity.auth.Refresh;
import kh.gangnam.b2b.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshRepository refreshRepository;

    // 리프레시 토큰 만료 시간 (application에서 주입)
    // 로그인 성공 시 리프레시 토큰 생성에 사용
    @Value("${jwt.token.refresh-expiration}")
    private long refreshExpirationMs;

    public boolean existsByRefresh(String refreshToken) {
        return refreshRepository.existsByRefresh(refreshToken);
    }

    public void deleteByEmployeeId(long employeeId) {
        refreshRepository.deleteByEmployeeId(employeeId);
    }

    public void save(Long employeeId, String refreshToken) {


        Refresh refreshEntity = Refresh.builder()
                .employeeId(employeeId)
                .refresh(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs/1000))
                .build();
        refreshRepository.save(refreshEntity);
    }
}
