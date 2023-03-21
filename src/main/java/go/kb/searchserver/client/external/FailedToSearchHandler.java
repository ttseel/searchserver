package go.kb.searchserver.client.external;

import go.kb.searchserver.common.error.exception.ServiceException;
import go.kb.searchserver.client.external.dto.ExternalResponse;
import go.kb.searchserver.dto.SearchResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static go.kb.searchserver.common.error.SearchErrorCode.SEARCH_REQUEST_FAILED;

@Qualifier("FailedToSearchHandler")
@Component
public class FailedToSearchHandler extends ExternalSearchHandler {
    @Override
    public SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size) {
        throw new ServiceException(SEARCH_REQUEST_FAILED);
    }

    @Override
    protected void setNextHandler(ExternalSearchHandler externalSearchHandler) {
        nextHandler = null;
    }

    @Override
    public <T extends ExternalResponse> boolean isValidResponse(ResponseEntity<T> response) {
        return false;
    }
}
