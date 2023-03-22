package go.kb.searchserver.client.external.naver;


import go.kb.searchserver.client.RestTemplateClient;
import go.kb.searchserver.client.external.ExternalSearchHandler;
import go.kb.searchserver.client.external.dto.ExternalResponse;
import go.kb.searchserver.client.external.dto.NaverSearchResponse;
import go.kb.searchserver.dto.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

import static go.kb.searchserver.client.external.naver.Constants.*;

@Slf4j
@Qualifier("NaverSearchHandler")
@Component
public class NaverSearchHandler extends ExternalSearchHandler {
    @Value("${client.naver.host}")
    private String host;
    @Value("${client.naver.client-id}")
    private String clientId;
    @Value("${client.naver.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplateClient restTemplateClient;

    @Override
    public SearchResponse searchBlog(String query, String sort, Integer page, Integer size) {
        log.info("Searching for blog posts with the Naver Search handler");

        int naverPage = convertToNaverPage(page, size);
        int start = toStart(naverPage, size);
        Map<String, String> queryParams = Map.of(
                "query", query,
                "sort", renameSort(sort),
                "start", String.valueOf(start),
                "display", String.valueOf(size)
        );
        URI uri = restTemplateClient.buildUri(host, "/v1/search/blog.json", queryParams);

        Map<String, String> headers = Map.of(
                NAVER_CLIENT_ID, clientId,
                NAVER_CLIENT_SECRET, clientSecret
        );
        HttpHeaders httpHeaders = restTemplateClient.createHeaders(headers);

        try {
            ResponseEntity<NaverSearchResponse> responseEntity = restTemplateClient.exchange(uri, httpHeaders, NaverSearchResponse.class);
            return new SearchResponse(responseEntity.getBody(), sort, naverPage, size);
        } catch (Exception e) {
            log.warn("The search using the Naver search handler failed");
            return nextHandler.searchBlog(query, sort, page, size);
        }
    }

    @Override
    public <T extends ExternalResponse> boolean isValidResponse(ResponseEntity<T> response) {
        return response.getStatusCode().is2xxSuccessful();
    }

    private String renameSort(String sort) {
        return sort.equals("accuracy") ? "sim" : "date";
    }

    private int convertToNaverPage(int page, int size) {
        if (page * size > NAVER_PARAM_PAGE_MAX) {
            page = NAVER_PARAM_PAGE_MAX / size;
        }
        return page;
    }

    private int toStart(int page, int size) {
        if (page * size > NAVER_PARAM_PAGE_MAX) {
            page = NAVER_PARAM_PAGE_MAX / size;
        }
        log.info("toStart - [page: {}], [size: {}]", page, size);
        return (page - 1) * size + 1;
    }

    @Override
    @Autowired
    protected void setNextHandler(@Qualifier("FailedToSearchHandler") ExternalSearchHandler searchHandler) {
        nextHandler = searchHandler;
    }
}
