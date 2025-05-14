package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;
import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.dto.auth.request.JoinDTO;
import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.service.ServiceImpl.BoardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    // Board 엔드포인트 Controller

    private final BoardService boardService;
    private final BoardServiceImpl boardServiceImpl;
  

    @PostMapping
    public ResponseEntity<BoardSaveResponse> create(@RequestBody SaveRequest saveRequest) {
        //BoardResponse response=boardService.saveBoard(saveRequest);
        System.out.println(">>>>"+saveRequest);
        return ResponseEntity.ok(boardService.saveBoard(saveRequest));
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<BoardResponse>> getList(
            @PathVariable("type") int type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(boardService.getList(type,page,size));
    }

    @GetMapping("/{type}/{boardId}")
    public ResponseEntity<BoardResponse> get(
            @PathVariable("type") int type,
            @PathVariable("boardId") Long boardId
           ) {

        return ResponseEntity.ok(boardService.get(type,boardId));
    }

    @PutMapping("/{type}/{boardId}")
    public ResponseEntity<BoardResponse> update(
            @PathVariable("type") int type,
            @PathVariable("boardId") Long boardId,
            @RequestBody UpdateRequest dto

    ) {
        return ResponseEntity.ok(boardService.update(type, boardId, dto));
    }




    @PostMapping("/s3")
    public ResponseEntity<?> saveS3Image(@RequestParam(value = "file", required = false) MultipartFile postFile) {
        return boardServiceImpl.saveS3Image(postFile);
    }

    @PostMapping("/save")
    public ResponseEntity<BoardDTO> saveBoard(@RequestBody SaveBoard dto, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return boardServiceImpl.saveBoard(dto, userDetails.getUserId());
    }
}
