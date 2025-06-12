package kh.gangnam.b2b.dto.work.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    // 성공 응답을 위한 static factory method
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<String> success(String message) {
        return new ApiResponse<>(true, null, message);
    }

    // 실패 응답을 위한 static factory method
    public static ApiResponse<String> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
