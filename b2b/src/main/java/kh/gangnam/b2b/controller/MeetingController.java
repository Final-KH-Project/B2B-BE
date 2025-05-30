package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.service.ServiceImpl.MeetingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class MeetingController {

    private final MeetingServiceImpl meetingService;
}
