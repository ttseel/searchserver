package go.kb.searchserver.service;

import go.kb.searchserver.dto.KeywordRank;
import go.kb.searchserver.dto.SearchResponse;
import go.kb.searchserver.dto.Top10Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SearchService {
    SearchResponse searchBlog(String keyword, String sort, String page, String size);

    Top10Response searchTop10();

    Map<String, Integer> getKeywordCacheMap();

    void clearTop10();

    void putUpdatedTop10(LocalDateTime updateTime, List<KeywordRank> newTop10);
}
