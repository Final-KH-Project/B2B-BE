package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import kh.gangnam.b2b.dto.board.response.CommentUpdateResponse;
import kh.gangnam.b2b.dto.board.response.EditResponse;
import kh.gangnam.b2b.dto.board.response.MessageResponse;
import kh.gangnam.b2b.repository.board.CommentUpdateRequest;
import kh.gangnam.b2b.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

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

    @PostMapping("/comment/save")
    public ResponseEntity<CommentSaveResponse> saveComment(@RequestBody CommentSaveRequest dto, @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        return ResponseEntity.ok(boardService.saveComment(dto,employeeDetails.getEmployeeId()));
    }

    @GetMapping("/comment/{boardId}")
    public ResponseEntity<List<CommentSaveResponse>> getCommentList(@PathVariable("boardId") Long boardId,
                                                                    @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        return ResponseEntity.ok(boardService.getCommentList(boardId,employeeDetails.getEmployeeId()));
    }

    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<MessageResponse> commentDelete(@PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(boardService.commentDeleteBoard(commentId));
    }

    @PutMapping("/comment/update")
    public ResponseEntity<CommentUpdateResponse> updateComment(@RequestBody CommentUpdateRequest dto,
                                                               @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        return ResponseEntity.ok(boardService.updateComment(dto, employeeDetails.getEmployeeId()));
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<BoardResponse>> getList(
            @PathVariable("type") int type,
            @RequestParam(defaultValue = "1", value = "page") int page) {

        return ResponseEntity.ok(boardService.getListBoard(type, page));
    }

    @GetMapping("/read/{boardId}")
    public ResponseEntity<BoardResponse> get(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal CustomEmployeeDetails employeeDetails
    ) {
        return ResponseEntity.ok(boardService.getBoard(boardId,employeeDetails.getEmployeeId()));
    }

    @PutMapping("/update/{boardId}")
    public ResponseEntity<BoardResponse> update(@PathVariable("boardId") Long boardId, @RequestBody UpdateRequest dto) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, dto));
    }

    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<MessageResponse> delete(@PathVariable("boardId") Long boardId) {
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
