package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.dto.board.response.EditResponse;
import kh.gangnam.b2b.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;
import kh.gangnam.b2b.service.ServiceImpl.BoardServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/save")
    public ResponseEntity<BoardSaveResponse> create(@RequestBody SaveRequest saveRequest, @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        return ResponseEntity.ok(boardService.saveBoard(saveRequest, employeeDetails.getEmployeeId()));
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<BoardResponse>> getList(
            @PathVariable("type") int type,
            @RequestParam(defaultValue = "1", value = "page") int page,
            @RequestParam(defaultValue = "10", value = "size") int size) {

        return ResponseEntity.ok(boardService.getListBoard(type, page, size));
    }

    @GetMapping("/{type}/{boardId}")
    public ResponseEntity<BoardResponse> get(
            @PathVariable("type") int type,
            @PathVariable("boardId") Long boardId
    ) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    @PutMapping("/update/{boardId}")
    public ResponseEntity<BoardResponse> update(@PathVariable("boardId") Long boardId, @RequestBody UpdateRequest dto) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, dto));
    }

    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<String> delete(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.deleteBoard(boardId));
    }

    @GetMapping("/edit/{boardId}")
    public ResponseEntity<EditResponse> edit(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.editBoard(boardId));
    }

    @PostMapping("/s3")
    public ResponseEntity<String> s3Upload(@RequestParam("file") MultipartFile postFile) {
        return ResponseEntity.ok(boardService.saveS3Image(postFile));
    }
}
