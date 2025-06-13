package kh.gangnam.b2b.dto.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class CustomPageResponse<T> {
    private List<T> content;        // 실제 데이터 리스트
    private int pageNumber;         // 현재 페이지 번호 (0-based)
    private int pageSize;           // 페이지 당 항목 수
    private long totalElements;     // 전체 항목 수
    private int totalPages;

    public static <T> CustomPageResponse<T> fromPage(Page<T> page) {
        return new CustomPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
