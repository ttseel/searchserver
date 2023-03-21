package go.kb.searchserver.service;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.repository.KeywordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class BatchServiceTest {

    @Autowired
    BatchService batchService;
    @Autowired
    KeywordRepository keywordRepository;
    @MockBean
    SearchService mockSearchService;
    @Autowired
    EntityManager em;

    @Test
    public void eachInsertPerformanceTest() {
        // insert만 500개 시간 비교
        int keywordCount = 500;
        Map<String, Integer> cacheKeywordMap = new HashMap<>();

        // 개별 insert
        for (int i = 0; i < keywordCount; i++) {
            cacheKeywordMap.put("keyword" + i, 1);
        }

        long beforeTime = System.currentTimeMillis();
        cacheKeywordMap.forEach((keyword, count) -> {
            Optional<Keyword> keywordEntity = keywordRepository.findByKeyword(keyword);
            if (keywordEntity.isEmpty()) {
                keywordRepository.save(new Keyword(keyword, count));
            }
        });
        em.flush();
        em.clear();
        long afterTime = System.currentTimeMillis();
        long secDiffTime = afterTime - beforeTime;
        System.out.println("each insert performance : " + Thread.currentThread().getName() + " : " + secDiffTime);
    }

    @Test
    public void bulkInsertPerformanceTest() {
        // insert만 500개 시간 비교
        int keywordCount = 500;
        Map<String, Integer> cacheKeywordMap = new HashMap<>();

        when(mockSearchService.getKeywordCacheMap()).thenReturn(cacheKeywordMap);

        // batch insert (saveAll)
        for (int i = 0; i < keywordCount; i++) {
            cacheKeywordMap.put("keyword" + i, 1);
        }
        long beforeTime = System.currentTimeMillis();
        batchService.batchUpdateKeyword();
        em.flush();
        em.clear();
        long afterTime = System.currentTimeMillis();
        long secDiffTime = afterTime - beforeTime;
        System.out.println("bulk insert performance : " + Thread.currentThread().getName() + " : " + secDiffTime);
    }
}