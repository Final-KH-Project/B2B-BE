package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.service.ServiceImpl.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    // Chat 엔트포인트 Controller

    private final ChatServiceImpl chatServiceImpl;
}
