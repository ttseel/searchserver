package go.kb.searchserver.service;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.dto.KeywordRank;
import go.kb.searchserver.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        List<Keyword> insertKeywordList = new ArrayList<>();
        Map<Integer, List<String>> updateKeywordList = new HashMap<>();
        Map<Integer, List<Long>> updateIdList = new HashMap<>();

        copiedCacheMap.forEach((keyword, cacheCount) -> {
            Optional<Keyword> keywordEntity = keywordRepository.findByKeyword(keyword);
            if (keywordEntity.isEmpty()) {
                insertKeywordList.add(new Keyword(keyword, cacheCount));
            } else {
                updateKeywordList.computeIfAbsent(cacheCount, k -> new ArrayList<>()).add(keyword);
                updateIdList.computeIfAbsent(cacheCount, k -> new ArrayList<>()).add(keywordEntity.get().getId());
            }
        });

        bulkInsertKeyword(insertKeywordList);
        bulkUpdateKeywordCount(updateKeywordList);

        refreshWithUpdateTop10Keywords(updatedAt);
    }

    private void bulkInsertKeyword(List<Keyword> insertKeywordList) {
        keywordRepository.saveAll(insertKeywordList);
    }


    private void bulkUpdateKeywordCount(Map<Integer, List<String>> updateKeywordList) {
        updateKeywordList.forEach((cacheCount, keywordList) -> {
            List<List<String>> keywordBatchSet = divideKeywordListByBatchSize(keywordList, 100);
            keywordBatchSet.forEach(keywordBatch -> keywordRepository.bulkIncrementCount(cacheCount, keywordBatch));
        });
    }

    private List<List<String>> divideKeywordListByBatchSize(List<String> keywordList, int batchSize) {
        return IntStream.range(0, keywordList.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / batchSize))
                .values().stream()
                .map(indices -> indices.stream().map(keywordList::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
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
        Map<String, Integer> copied = new HashMap<>();
        cache.forEach((key, value) -> {
            copied.put(key, value);
            cache.remove(key);
        });
        return copied;
    }
}
