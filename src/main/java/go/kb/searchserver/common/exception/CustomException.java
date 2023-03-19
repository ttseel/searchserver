package go.kb.searchserver.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final LocalDateTime dateTime = LocalDateTime.now();
    private ErrorCode errorCode;

//    private String logMessage;
}
