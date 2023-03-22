package go.kb.searchserver.service;

import go.kb.searchserver.client.RestTemplateClient;
import go.kb.searchserver.client.external.dto.NaverSearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

import static go.kb.searchserver.client.external.naver.Constants.NAVER_CLIENT_ID;
import static go.kb.searchserver.client.external.naver.Constants.NAVER_CLIENT_SECRET;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NaverSearchHandlerTest {
    @Value("${client.naver.host}")
    private String naverHost;
    @Value("${client.naver.client-id}")
    private String clientId;
    @Value("${client.naver.client-secret}")
    private String clientSecret;
    @Autowired
    SearchService searchService;
    @Autowired
    private RestTemplateClient restTemplateClient;

    @DisplayName("네이버 검색 결과 확인")
    @Test
    public void testSearchNaverBlog() {
        System.out.println("clientId: " + clientId + ", clientSecret: " + clientSecret);

        Map<String, String> queryParams = Map.of(
                "query", "바르셀로나"
        );
        URI uri = restTemplateClient.buildUri(naverHost, "/v1/search/blog.json", queryParams);

        Map<String, String> headers = Map.of(
                NAVER_CLIENT_ID, clientId,
                NAVER_CLIENT_SECRET, clientSecret
        );
        HttpHeaders httpHeaders = restTemplateClient.createHeaders(headers);


        ResponseEntity<NaverSearchResponse> responseEntity = restTemplateClient.exchange(uri, httpHeaders, NaverSearchResponse.class);
        assertThat(responseEntity.getBody().getItems().size()).isGreaterThan(0);
    }
}