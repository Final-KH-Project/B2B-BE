package kh.gangnam.b2b.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.dto.auth.request.JoinRequest;
import kh.gangnam.b2b.dto.auth.request.LoginRequest;
import kh.gangnam.b2b.service.ServiceImpl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginDTO, HttpServletResponse response) {
        return authServiceImpl.login(loginDTO, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        return authServiceImpl.logout(request, response);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> join(@RequestBody JoinRequest joinRequest) {
        return ResponseEntity.ok(authServiceImpl.join(joinRequest));
    }

    @GetMapping("/check-loginId")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam(name = "loginId") String loginId) {
        return ResponseEntity.ok(authServiceImpl.checkLoginId(loginId));
    }

}
