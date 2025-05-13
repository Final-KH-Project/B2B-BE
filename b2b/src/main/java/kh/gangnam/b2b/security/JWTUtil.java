package kh.gangnam.b2b.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }


    public String createJwt(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰 유효성 검증 및 Claims(페이로드) 추출
     * @param access JWT 문자열
     * @return Claims 객체 (username, role 등 클레임 포함)
     * @throws IllegalArgumentException 유효하지 않은 토큰, 만료, 변조 등 예외 발생 시
     */
    public Claims validateAndParseClaims(String access) {
        try {
            // JWT의 서명과 유효성을 검증하고, Claims(페이로드)를 반환
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(access)
                    .getPayload();
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            // 유효하지 않은 JWT 서명
            throw new IllegalArgumentException("Invalid JWT signature.", e);
        } catch (ExpiredJwtException e) {
            // 만료된 JWT
            throw new IllegalArgumentException("Expired JWT token.", e);
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT
            throw new IllegalArgumentException("Unsupported JWT token.", e);
        } catch (IllegalArgumentException e) {
            // 잘못된 JWT
            throw new IllegalArgumentException("JWT claims is empty.", e);
        }
    }
}
