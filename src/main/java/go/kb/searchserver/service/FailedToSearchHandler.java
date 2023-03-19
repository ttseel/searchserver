package go.kb.searchserver.service;

import go.kb.searchserver.common.exception.CustomException;
import go.kb.searchserver.dto.ExternalResponse;
import go.kb.searchserver.dto.SearchResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static go.kb.searchserver.common.exception.SearchErrorCode.SEARCH_REQUEST_FAILED;

@Qualifier("FailedToSearchHandler")
@Component
public class FailedToSearchHandler extends ExternalSearchHandler {
    @Override
    public SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size) {
//        throw new SearchRequestFailedException(HttpStatus.SERVICE_UNAVAILABLE, "블로그 서버로부터 응답이 없습니다");
        throw new CustomException(SEARCH_REQUEST_FAILED);
    }

    @Override
    protected void nextHandler(ExternalSearchHandler externalSearchHandler) {
        nextHandler = null;
    }

    @Override
    public <T extends ExternalResponse> boolean isValidResponse(ResponseEntity<T> response) {
        return false;
    }
}
