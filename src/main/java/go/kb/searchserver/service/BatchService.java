package go.kb.searchserver.service;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.dto.KeywordRank;
import go.kb.searchserver.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BatchService {
    @Autowired
    private SearchService searchService;
    @Autowired
    private KeywordRepository keywordRepository;

    @Transactional
    public void batchUpdateKeyword() {
        LocalDateTime updatedAt = LocalDateTime.now();

        Map<String, Integer> copiedCacheMap = copyAndClearCache(searchService.getKeywordCacheMap());

        List<Keyword> newKeywordList = new ArrayList<>();

        copiedCacheMap.forEach((keyword, cacheCount) -> {
            Optional<Keyword> keywordEntity = keywordRepository.findByKeyword(keyword);
            if (keywordEntity.isEmpty()) {
                newKeywordList.add(new Keyword(keyword, 1));
            } else {
                keywordEntity.get().increment(cacheCount); // TODO 네이티브 쿼리 in 쿼리로 최적화 가능. in 쿼리 설정조건 블로그에서 찾아 넣기
            }
        });
        keywordRepository.saveAll(newKeywordList);

        refreshWithUpdateTop10Keywords(updatedAt);
    }

    private void refreshWithUpdateTop10Keywords(LocalDateTime updatedAt) {
        List<KeywordRank> updatedTop10 = new ArrayList<>();
        for (int i = 0; i < keywordRepository.findTop10ByOrderByReadCountDesc().size(); i++) {
            int rank = i + 1;
            Keyword keyword = keywordRepository.findTop10ByOrderByReadCountDesc().get(i);
            updatedTop10.add(new KeywordRank(rank, keyword.getKeyword(), keyword.getReadCount()));
        }

        searchService.clearTop10();
        searchService.putUpdatedTop10(updatedAt, updatedTop10);
    }

    private Map<String, Integer> copyAndClearCache(Map<String, Integer> cache) {
        Map<String, Integer> copied = new ConcurrentHashMap<>();
        cache.forEach((key, value) -> {
            copied.put(key, value);
            cache.remove(key);
        });
        return copied;
    }

    @Transactional
    private void insertNewKeywords(List<Keyword> newKeywordList) {
        keywordRepository.saveAll(newKeywordList);
    }
}
