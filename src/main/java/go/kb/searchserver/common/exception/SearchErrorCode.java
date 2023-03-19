package go.kb.searchserver.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum SearchErrorCode implements ErrorCode {
    /* 4XX */
    INVALID_SEARCH_PARAM_KEYWORD(BAD_REQUEST, "SE101", "잘못된 쿼리 요청입니다"),
    INVALID_SEARCH_PARAM_SORT(BAD_REQUEST, "SE102", "부적절한 sort 값 입니다"),
    INVALID_SEARCH_PARAM_PAGE(BAD_REQUEST, "SE103", "부적절한 page 값 입니다"),
    INVALID_SEARCH_PARAM_SIZE(BAD_REQUEST, "SE104", "부적절한 size 값 입니다"),

    /* 5XX */
    SEARCH_REQUEST_FAILED(INTERNAL_SERVER_ERROR, "SE111", "시스템에 에러가 발생했습니다"),
    EXTERNAL_SEARCH_REQUEST_FAILED(SERVICE_UNAVAILABLE, "SE112", "검색 요청이 실패했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
