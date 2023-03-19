package go.kb.searchserver.service;


import go.kb.searchserver.dto.ExternalResponse;
import go.kb.searchserver.dto.KakaoSearchResponse;
import go.kb.searchserver.dto.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Qualifier("KakaoSearchHandler")
@Component
public class KakaoSearchHandler extends ExternalSearchHandler {
    @Value("${client.kakao.host}")
    private String host;
    @Value("${client.kakao.rest-api-key}")
    private String restApiKey;
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size) {
        log.info("KakaoSearchHandler");

        // TODO 아래 restTemplate한테 요청하는 부분 NaverSearchHandler랑 중복됨. 리팩토링 필요
        URI uri = UriComponentsBuilder.fromUriString(host)
                .path("/v2/search/blog")
                .queryParam("query", keyword)
                .queryParam("sort", sort)
                .queryParam("page", page)
                .queryParam("size", size)
                .encode()
                .build()
                .toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .get(uri)
                .header("Authorization", restApiKey)
                .build();

        ResponseEntity<KakaoSearchResponse> responseEntity = restTemplate.exchange(requestEntity, KakaoSearchResponse.class);

        if (isValidResponse(responseEntity)) {
            return new SearchResponse(responseEntity.getBody(), sort, page, size);
        } else {
            return nextHandler.searchBlog(keyword, sort, page, size);
        }
    }

    @Override
    public <T extends ExternalResponse> boolean isValidResponse(ResponseEntity<T> response) {
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    @Autowired
    protected void nextHandler(@Qualifier("NaverSearchHandler") ExternalSearchHandler searchHandler) {
        this.nextHandler = searchHandler;
    }
}
