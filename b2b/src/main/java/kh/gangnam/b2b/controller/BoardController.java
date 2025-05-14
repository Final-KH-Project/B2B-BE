package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    // Board 엔드포인트 Controller

    private final BoardService boardService;

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



}
