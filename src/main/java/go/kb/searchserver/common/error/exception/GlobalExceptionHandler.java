package go.kb.searchserver.common.error.exception;

import go.kb.searchserver.common.error.CommonErrorCode;
import go.kb.searchserver.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ServiceException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(ServiceException e) {
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        log.error("Unexpected error occurred - class : {}, message : {}", e.getClass(), e.getMessage());
        return ErrorResponse.toResponseEntity(CommonErrorCode.UNEXPECTED_SYSTEM_ERROR);
    }
}
