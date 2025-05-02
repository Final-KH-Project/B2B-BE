package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.JoinDTO;
import kh.gangnam.b2b.dto.auth.LoginDTO;
import kh.gangnam.b2b.dto.auth.LoginResponse;
import kh.gangnam.b2b.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@RequestBody JoinDTO joinDTO) {

        return authService.joinProcess(joinDTO);
    }
}
