package go.kb.searchserver.config;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.repository.KeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Future;

@Slf4j
@Component
public class AsyncTest {
    @Autowired
    private KeywordRepository keywordRepository;

    @Async("SearchProcessExecutor")
    public Future<String> doAsyncJob(int param) throws InterruptedException {
//        log.info("[{}] param: {}", Thread.currentThread().getName(), param);

        long beforeTime = System.currentTimeMillis();
        keywordRepository.findTop10ByOrderByReadCountDesc();
        long afterTime = System.currentTimeMillis();
        long secDiffTime = afterTime - beforeTime;
        System.out.println(Thread.currentThread().getName() + " : " + secDiffTime);

        return new AsyncResult<>("hello world !!!!");
    }


    @Async("SearchProcessExecutor")
    public Future<String> doInsertAndRead(int keywordNo, int limit) {
        Random random = new Random();

        if (keywordRepository.count() <= limit) {
            log.info("{}", keywordRepository.count());
            Keyword keyword = new Keyword("keyword" + keywordNo, random.nextInt(Integer.MAX_VALUE));
            keywordRepository.save(keyword);
        }

        return new AsyncResult<>("hello world !!!!");
    }
}
