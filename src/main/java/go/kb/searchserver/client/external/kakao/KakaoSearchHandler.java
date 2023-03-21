package go.kb.searchserver.client.external.kakao;


import go.kb.searchserver.client.external.ExternalSearchHandler;
import go.kb.searchserver.client.RestTemplateClient;
import go.kb.searchserver.client.external.dto.ExternalResponse;
import go.kb.searchserver.client.external.dto.KakaoSearchResponse;
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

import static go.kb.searchserver.client.external.kakao.Constants.AUTHORIZATION;

@Slf4j
@Qualifier("KakaoSearchHandler")
@Component
public class KakaoSearchHandler extends ExternalSearchHandler {
    @Value("${client.kakao.host}")
    private String host;
    @Value("${client.kakao.rest-api-key}")
    private String restApiKey;
    @Autowired
    private RestTemplateClient restTemplateClient;

    @Override
    public SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size) {
        log.info("Searching for blog posts with the Kakao Search handler");

        Map<String, String> queryParams = Map.of(
                "query", keyword,
                "sort", sort,
                "page", String.valueOf(page),
                "size", String.valueOf(size)
        );
        URI uri = restTemplateClient.buildUri(host, "/v2/search/blog", queryParams);

        Map<String, String> headers = Map.of(AUTHORIZATION, restApiKey);
        HttpHeaders httpHeaders = restTemplateClient.createHeaders(headers);

        try {
            ResponseEntity<KakaoSearchResponse> responseEntity = restTemplateClient.exchange(uri, httpHeaders, KakaoSearchResponse.class);
            return new SearchResponse(responseEntity.getBody(), sort, page, size);
        } catch (Exception e) {
            log.warn("The search using the Kakao search handler failed");
            return nextHandler.searchBlog(keyword, sort, page, size);
        }
    }


    @Override
    public <T extends ExternalResponse> boolean isValidResponse(ResponseEntity<T> response) {
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    @Autowired
    protected void setNextHandler(@Qualifier("NaverSearchHandler") ExternalSearchHandler searchHandler) {
        this.nextHandler = searchHandler;
    }
}
