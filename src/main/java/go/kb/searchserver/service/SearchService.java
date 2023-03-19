package go.kb.searchserver.service;

import go.kb.searchserver.dto.SearchResponse;
import go.kb.searchserver.dto.Top10Response;

public interface SearchService {
    SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size);

    void saveAndIncrement(String keyword);

    Top10Response searchTop10();
}
