package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.dto.board.request.UpdateBoard;
import kh.gangnam.b2b.service.ServiceImpl.S3TestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/")
public class S3TestController {
    private final S3TestServiceImpl s3TestServiceImpl;

    @PostMapping("/s3")
    public ResponseEntity<?> saveS3Image(@RequestParam("file") MultipartFile postFile) {
        return s3TestServiceImpl.saveS3Image(postFile);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBoard(@RequestBody SaveBoard dto, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return s3TestServiceImpl.saveBoard(dto, userDetails.getUserId());
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> readBoard(@PathVariable(value = "id") Long boardId) {

        return s3TestServiceImpl.readBoard(boardId);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateBoard(@RequestBody UpdateBoard dto) {

        return s3TestServiceImpl.updateBoard(dto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBoard(@RequestParam("query") Long postId) { // postid 값을 확인할 수 있을만한 정보 필요

        return s3TestServiceImpl.deleteBoard(postId);
    }
}
