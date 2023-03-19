package go.kb.searchserver.service;


import go.kb.searchserver.dto.ExternalResponse;
import go.kb.searchserver.dto.NaverSearchResponse;
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
@Qualifier("NaverSearchHandler")
@Component
public class NaverSearchHandler extends ExternalSearchHandler {
    @Value("${client.naver.host}")
    private String host;
    @Value("${client.naver.client-id}")
    private String clientId;
    @Value("${client.naver.client-secret}")
    private String clientSecret;
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size) {
        log.info("naverSearchHandler");

        URI uri = UriComponentsBuilder.fromUriString(host)
                .path("/v1/search/blog.json")
                .queryParam("query", keyword)
                .queryParam("sort", renameSort(sort))
                .queryParam("start", toStart(page, size))
                .queryParam("display", size)
                .encode()
                .build()
                .toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId) // TODO 다른 애들거 참고해서 상수화 시킬 방법을 찾아보자
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        ResponseEntity<NaverSearchResponse> responseEntity = restTemplate.exchange(requestEntity, NaverSearchResponse.class);
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

    private String renameSort(String sort) {
        return sort.equals("accuracy") ? "sim" : "date";
    }

    private int toStart(int page, int size) {
        return (page - 1) * size + 1;
    }

    @Override
    @Autowired
    protected void nextHandler(@Qualifier("FailedToSearchHandler") ExternalSearchHandler searchHandler) {
        nextHandler = searchHandler;
    }
}
