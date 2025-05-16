package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.service.ServiceImpl.BoardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/")
public class BoardController {

    // Board 엔드포인트 Controller

    private final BoardServiceImpl boardServiceImpl;
}
