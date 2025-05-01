package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.login.CustomUserDetails;
import kh.gangnam.b2b.dto.login.JoinDTO;
import kh.gangnam.b2b.dto.login.LoginDTO;
import kh.gangnam.b2b.dto.login.LoginResponse;
import kh.gangnam.b2b.security.JWTUtil;
import kh.gangnam.b2b.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;


    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@RequestBody JoinDTO joinDTO) {

        return authService.joinProcess(joinDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO request) {
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
