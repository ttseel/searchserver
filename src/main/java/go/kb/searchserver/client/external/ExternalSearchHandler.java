package go.kb.searchserver.client.external;

import go.kb.searchserver.client.external.dto.ExternalResponse;
import go.kb.searchserver.dto.SearchResponse;
import org.springframework.http.ResponseEntity;

public abstract class ExternalSearchHandler {

    protected ExternalSearchHandler nextHandler;

    public abstract SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size);

    protected abstract void setNextHandler(ExternalSearchHandler externalSearchHandler);

    public abstract <T extends ExternalResponse> boolean isValidResponse(ResponseEntity<T> response);

}
