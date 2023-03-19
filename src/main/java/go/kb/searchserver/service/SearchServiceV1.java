package go.kb.searchserver.service;

import go.kb.searchserver.common.exception.CustomException;
import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.dto.SearchResponse;
import go.kb.searchserver.dto.Top10Response;
import go.kb.searchserver.repository.KeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static go.kb.searchserver.common.exception.SearchErrorCode.*;

@Slf4j
@Qualifier("SearchServiceV1")
@Service
public class SearchServiceV1 implements SearchService {

    @Qualifier("KakaoSearchHandler")
    @Autowired
    private ExternalSearchHandler externalSearchHandler;
    @Autowired
    private KeywordRepository keywordRepository;

    public SearchResponse searchBlog(String keyword, String sort, Integer page, Integer size) {
        requestValidation(keyword, sort, page, size);
        return externalSearchHandler.searchBlog(keyword, sort, page, size);
    }

    private void requestValidation(String keyword, String sort, Integer page, Integer size) {
        if (!isValidKeyword(keyword)) {
            throw new CustomException(INVALID_SEARCH_PARAM_KEYWORD);
        } else if (!isValidSort(sort)) {
            throw new CustomException(INVALID_SEARCH_PARAM_SORT);
        } else if (!isValidRange(page, 1, 50)) {
            throw new CustomException(INVALID_SEARCH_PARAM_PAGE);
        } else if (!isValidRange(size, 1, 50)) {
            throw new CustomException(INVALID_SEARCH_PARAM_SIZE);
        }
    }

    private boolean isValidKeyword(String keyword) {
        return keyword != null || !keyword.equals("");
    }

    private boolean isValidSort(String sort) {
        return sort.equals("accuracy") || sort.equals("recency");
    }

    private boolean isValidRange(int page, int min, int max) {
        return page >= min && page <= max;
    }

    @Override
    public void saveAndIncrement(String keyword) {
        Keyword keywordEntity = keywordRepository.findByKeyword(keyword).orElse(new Keyword(keyword, 0));
        keywordEntity.increment();
        keywordRepository.save(keywordEntity);
    }

    @Override
    public Top10Response searchTop10() {
        List<Top10Response.KeywordRank> top10List = new ArrayList<>();
        for (int i = 0; i < keywordRepository.findTop10ByOrderByReadCountDesc().size(); i++) {
            Keyword keyword = keywordRepository.findTop10ByOrderByReadCountDesc().get(i);
            top10List.add(new Top10Response.KeywordRank(i + 1, keyword.getKeyword(), keyword.getReadCount()));
        }
        return new Top10Response(top10List);
    }
}
