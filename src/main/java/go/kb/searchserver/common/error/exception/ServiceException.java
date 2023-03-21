package go.kb.searchserver.common.error.exception;

import go.kb.searchserver.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ServiceException extends RuntimeException {
    private final LocalDateTime dateTime = LocalDateTime.now();
    private ErrorCode errorCode;
}
