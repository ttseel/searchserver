package go.kb.searchserver.service;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.dto.NaverSearchResponse;
import go.kb.searchserver.repository.KeywordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Random;

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

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testSearchNaverBlog() {
        System.out.println("clientId: " + clientId + ", clientSecret: " + clientSecret);

        URI uri = UriComponentsBuilder.fromUriString(naverHost)
                .path("/v1/search/blog.json")
                .queryParam("query", "바르셀로나")
                .encode()
                .build()
                .toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        ResponseEntity<NaverSearchResponse> responseEntity = restTemplate.exchange(requestEntity, NaverSearchResponse.class);
        responseEntity.getBody();
    }

    @Autowired
    KeywordRepository keywordRepository;

    @Test
    public void testTop10Query() {
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            Keyword keyword = new Keyword("keyword" + i, random.nextInt(Integer.MAX_VALUE));
            keywordRepository.save(keyword);
        }

        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
        List<Keyword> list = keywordRepository.findTop10ByOrderByReadCountDesc();
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        System.out.println("시간차이(msec) : " + secDiffTime);
    }


}