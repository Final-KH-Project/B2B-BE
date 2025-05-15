package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;
import kh.gangnam.b2b.service.ServiceImpl.BoardServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;
    private final BoardServiceImpl boardServiceImpl;

    @PostMapping
    public ResponseEntity<BoardSaveResponse> create(@RequestBody SaveRequest saveRequest) {
        return ResponseEntity.ok(boardService.saveBoard(saveRequest));
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<BoardResponse>> getList(
            @PathVariable("type") int type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(boardService.getListBoard(type,page,size));
    }

    @GetMapping("/{type}/{boardId}")
    public ResponseEntity<BoardResponse> get(
            @PathVariable("type") int type,
            @PathVariable("boardId") Long boardId
           ) {
        return ResponseEntity.ok(boardService.getBoard(type,boardId));
    }

    @PutMapping("/{type}/{boardId}")
    public ResponseEntity<BoardResponse> update(
            @PathVariable("type") int type,
            @PathVariable("boardId") Long boardId,
            @RequestBody UpdateRequest dto

    ) {
        return ResponseEntity.ok(boardService.updateBoard(type, boardId, dto));
    }
}
