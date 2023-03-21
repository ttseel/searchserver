package go.kb.searchserver.controller;

import go.kb.searchserver.client.external.dto.NaverSearchResponse;
import go.kb.searchserver.repository.KeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    EntityManager em;

    @DisplayName("Blog 조회 : query = null 일 때 INVALID_SEARCH_PARAM_KEYWORD")
    @Test
    public void testQueryNull() throws Exception {
        //given
//        mockMvc.perform(get("/search/blog-posting")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("query", "갤러리아"))
//                .andExpect(status().isOk())
//                .andDo(print());

        //when

        //then
    }

    @Test
    @DisplayName("인기 검색어 저장 (동시성 이슈)")
    void savePopularWordMultiThread() throws InterruptedException {
//        // given
//        AtomicInteger successCount = new AtomicInteger();
//        int numberOfExcute = 1000;
//        ExecutorService service = Executors.newFixedThreadPool(100);
//        CountDownLatch latch = new CountDownLatch(numberOfExcute);
//        String searchWord = "이슈";
//
//        // when
//        for (int i = 0; i < numberOfExcute; i++) {
//            service.execute(() -> {
//                try {
//                    popularService.plusPopularSearchWordCount(searchWord);
//                    successCount.getAndIncrement();
//                    log.info("성공");
//                } catch (Exception e) {
//                    log.error("[테스트코드]: {}", e.getMessage());
//                }
//                latch.countDown();
//            });
//        }
//        latch.await();
//
//        Popular findPopular = popularRepository.findBySearchWord(searchWord);
//
//        // then
//        assertEquals(findPopular.getCount(), numberOfExcute);
    }


    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testSearchNaverBlog() {
//        System.out.println("clientId: " + clientId + ", clientSecret: " + clientSecret);

        String base = "http://localhost:8080";
        URI uri = UriComponentsBuilder.fromUriString(base)
                .path("/search/blog-posting/async")
                .queryParam("keyword", "바르셀로나")
                .encode()
                .build()
                .toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .get(uri)
                .build();

        ResponseEntity<NaverSearchResponse> responseEntity = restTemplate.exchange(requestEntity, NaverSearchResponse.class);
        System.out.println(responseEntity.getBody());

    }

    int testCount = 0;

    @Test
    public void multiThreadedTest() throws InterruptedException {
        int numberOfThreads = 5;
        int numberOfRequest = 100000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        Random random = new Random();
        Runnable requestTask = () -> {
            try {
                mockMvc.perform(get("http://localhost:8080/search/save-single")
//                mockMvc.perform(get("http://localhost:8080/search/save-async")
                        .param("keyword", "바르셀로나" + random.nextInt(Integer.MAX_VALUE))
                        .contentType(MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfRequest; i++) {
            testCount = i;
            executorService.submit(requestTask);
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long afterTime = System.currentTimeMillis();
        long elapsedTime = afterTime - beforeTime;
        System.out.println(Thread.currentThread().getName() + " Elapsed time: " + elapsedTime + " ms");
    }

    @Test
    public void singleThreadedTest() throws InterruptedException {
        int numberOfRequest = 100;

        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfRequest; i++) {
            try {
                mockMvc.perform(get("http://localhost:8080/search/blog-posting/async/save")
                        .param("keyword", "바르셀로나" + i)
                        .contentType(MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long afterTime = System.currentTimeMillis();
        long elapsedTime = afterTime - beforeTime;
        System.out.println(Thread.currentThread().getName() + " Elapsed time: " + elapsedTime + " ms");

    }
}