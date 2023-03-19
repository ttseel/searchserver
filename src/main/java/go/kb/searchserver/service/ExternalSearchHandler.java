package go.kb.searchserver.service;

import go.kb.searchserver.dto.ExternalResponse;
import go.kb.searchserver.dto.SearchResponse;
import org.springframework.http.ResponseEntity;

public abstract class ExternalSearchHandler {

    protected ExternalSearchHandler nextHandler;

    public abstract SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size);

    protected abstract void nextHandler(ExternalSearchHandler externalSearchHandler);

    public abstract <T extends ExternalResponse> boolean isValidResponse(ResponseEntity<T> response);

}
