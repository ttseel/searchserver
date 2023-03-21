package go.kb.searchserver.config;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.repository.KeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class AsyncTestTest {
    @Autowired
    AsyncTest asyncTest;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    EntityManager em;

    @Test
    public void testAsync() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            asyncTest.doAsyncJob(i + 1);
        }

        Thread.sleep(180 * 1000); // main 쓰레드가 끝나면 async-thread도 끝나버림
    }

    @Test
    public void test() throws InterruptedException {
        log.info("start createSampleKeyword");
        createSampleKeyword(10000 * 100);
        log.info("end createSampleKeyword");

        int queryCount = 10000 * 30;
        for (int i = 0; i < queryCount; i++) {
            asyncTest.doAsyncJob(i + 1);
        }

        while (true) {
            if (AsyncConfig.getSearchExecutor().getActiveCount() == 0) {
                break;
            }
        }
    }

    @DisplayName("ThreadPoolTaskExecutor로 한꺼번에 DB에 insert를 하면 Thread-safe 하지 못함")
    @Test
    public void testThreadPoolTaskExecutorIsNotThreadSafe() throws InterruptedException {
        int limitCount = 500;
        int queryCount = 10000 * 10;
        for (int i = 1; i <= queryCount; i++) {
            asyncTest.doInsertAndRead(i + 1, limitCount);
        }

        while (true) {
            if (AsyncConfig.getSearchExecutor().getActiveCount() == 0) {
                assertThat(keywordRepository.count()).isGreaterThan(500);
                break;
            }
        }
    }


    @Transactional
    private void createSampleKeyword(int sampleSize) {
        Random random = new Random();
        List<Keyword> keywordList = new ArrayList<>();
        for (int i = 0; i < sampleSize; i++) {
            Keyword keyword = new Keyword("keyword" + i, random.nextInt(Integer.MAX_VALUE));
            keywordList.add(keyword);
        }
        keywordRepository.saveAll(keywordList);
        em.flush();
        em.clear();
    }


    Map<String, Integer> synchroMap = new ConcurrentHashMap<>();

    @Test
    public void synchroTest() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            synchroMap.put("A" + i, i);
        }
        Thread thread = new Thread(() -> {
            System.out.println("size :" + synchroMap.size());
            synchroMap.put("B", 1);
            System.out.println("Thread");
        });

        synchroMap.forEach((key, value) -> {
            synchroMap.remove(key);
        });

        thread.start();

    }

}