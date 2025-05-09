package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.request.JoinDTO;
import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.service.ServiceImpl.BoardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/")
public class BoardController {

    // Board 엔드포인트 Controller

    private final BoardServiceImpl boardServiceImpl;

    @PostMapping("/s3")
    public ResponseEntity<?> saveS3Image(@RequestParam(value = "postFile", required = false) MultipartFile postFile) {
        return null; // 나중에? 세이브 하는 로직 만들기
    }

    @PostMapping("/save")
    public ResponseEntity<BoardDTO> saveBoard(SaveBoard dto) {

        return boardServiceImpl.saveBoard(dto);
    }
}
