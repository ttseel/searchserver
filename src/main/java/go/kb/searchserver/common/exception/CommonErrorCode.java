package go.kb.searchserver.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    UNEXPECTED_SYSTEM_ERROR(INTERNAL_SERVER_ERROR, "CE101", "시스템에 에러가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
