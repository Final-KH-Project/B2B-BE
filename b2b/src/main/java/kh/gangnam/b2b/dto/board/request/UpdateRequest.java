package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;

import java.util.List;

public record UpdateRequest(String title,
                            String content,
                            BoardType boardType,
                            List<String> imageUrls) {
}
