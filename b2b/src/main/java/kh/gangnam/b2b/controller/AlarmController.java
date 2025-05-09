package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmController {
    // alarm 엔트포인트 Controller

    private final AlarmService alarmService;
}
