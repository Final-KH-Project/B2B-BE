package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.dto.auth.JoinDTO;
import kh.gangnam.b2b.dto.auth.LoginDTO;
import kh.gangnam.b2b.dto.auth.LoginResponse;
import kh.gangnam.b2b.entity.UserEntity;
import kh.gangnam.b2b.repository.UserRepository;
import kh.gangnam.b2b.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    /**
     * 회원 가입 로직
     * @param joinDTO
     * @return
     * 이미 존재하는 이름일 경우 (409)
     * 가입 성공 (200)
     */
    public ResponseEntity<String> joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(username + "는 이미 존재하는 아이디입니다.");
        }

        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);

        return ResponseEntity.ok(username + " 회원가입 완료");
    }


    /**
     * 로그인 로직
     * @param request
     * @return
     * 로그인 실패시
     */
    public ResponseEntity<LoginResponse> loginProcess(LoginDTO request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.createJwt(
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority(),
                60 * 60 * 10L
        );

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(token)
                .expiresAt(LocalDateTime.now().plusHours(10))
                .build());
    }
}
