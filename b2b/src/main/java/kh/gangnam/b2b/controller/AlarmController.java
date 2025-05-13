package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.alarm.response.ReadAlarm;
import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.service.ServiceImpl.AlarmServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmController {
    // alarm 엔트포인트 Controller

    private final AlarmServiceImpl alarmServiceImpl;


}
