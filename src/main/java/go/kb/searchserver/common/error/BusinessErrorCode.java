package go.kb.searchserver.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum BusinessErrorCode {
    TOP10_UPDATE_ERROR(INTERNAL_SERVER_ERROR, "BE101", "인기검색어 업데이트에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
