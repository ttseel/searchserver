package go.kb.searchserver.repository;

import go.kb.searchserver.domain.Keyword;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Random;

@Slf4j
@SpringBootTest
@Transactional
class KeywordRepositoryTest {
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    EntityManager em;

    Random random = new Random();

    @DisplayName("Keyword에 Index 있는게(unique 조건) 훨씬 빠름")
    @Test
    public void testIndexQuerySpeed() {
        int sampleSize = 10000;
        createSampleKeyword(sampleSize);
        em.flush();
        em.clear();

        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            int randomIdx = random.nextInt(sampleSize);
            keywordRepository.findByKeyword("keyword" + randomIdx).get();
        }
        long afterTime = System.currentTimeMillis();
        long secDiffTime = afterTime - beforeTime;
        System.out.println(Thread.currentThread().getName() + " : " + secDiffTime + "밀리 초");
    }

    @Test
    public void testUpdate() {
        int sampleSize = 10000;
        createSampleKeyword(sampleSize);
        em.flush();
        em.clear();

//        List<Keyword> keywordList = keywordRepository.findAll();

        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            int randomIdx = random.nextInt(sampleSize);
            Keyword keyword = keywordRepository.findByKeyword("keyword" + randomIdx).get();
            keyword.increment(7);
//            keywordList.get(randomIdx).increment(7);
//            update(keywordList.get(randomIdx));
        }
        em.flush();
        em.clear();
        long afterTime = System.currentTimeMillis();
        long secDiffTime = afterTime - beforeTime;
        System.out.println(Thread.currentThread().getName() + " : " + secDiffTime);
    }

    @Transactional
    public void update(Keyword keyword) {
        keyword.increment(7);
    }

    @Transactional
    private void createSampleKeyword(int sampleSize) {
        Random random = new Random();
        for (int i = 0; i < sampleSize; i++) {
            Keyword keyword = new Keyword("keyword" + i, random.nextInt(Integer.MAX_VALUE));
            keywordRepository.save(keyword);
        }
    }
}