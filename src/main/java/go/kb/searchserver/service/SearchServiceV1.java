package go.kb.searchserver.service;

import go.kb.searchserver.common.exception.CustomException;
import go.kb.searchserver.dto.KeywordRank;
import go.kb.searchserver.dto.SearchResponse;
import go.kb.searchserver.dto.Top10Response;
import go.kb.searchserver.repository.KeywordRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    @Getter
    private final Map<String, Integer> keywordCacheMap = new ConcurrentHashMap<>();
    private final List<KeywordRank> top10 = new ArrayList<>();
    private LocalDateTime top10UpdateTime;
    @Value("${schedule.top10.period}")
    private int top10UpdatePeriod;

    @Override
    public SearchResponse searchBlog(String keyword, String sort, String page, String size) {
        requestValidation(keyword, sort, page, size);
        cacheKeyword(keyword);

        return externalSearchHandler.searchBlog(keyword, sort, Integer.parseInt(page), Integer.parseInt(size));
    }


    private void requestValidation(String keyword, String sort, String page, String size) {
        if (isInvalidKeyword(keyword)) {
            throw new CustomException(INVALID_SEARCH_PARAM_KEYWORD);
        } else if (isInvalidSort(sort)) {
            throw new CustomException(INVALID_SEARCH_PARAM_SORT);
        } else if (isInteger(page) && isValidRange(Integer.parseInt(page), 1, 50)) {
            throw new CustomException(INVALID_SEARCH_PARAM_PAGE);
        } else if (isInteger(page) && isValidRange(Integer.parseInt(size), 1, 50)) {
            throw new CustomException(INVALID_SEARCH_PARAM_SIZE);
        }
    }

    private boolean isInvalidKeyword(String keyword) {
        return keyword == null || keyword.isBlank();
    }

    private boolean isInvalidSort(String sort) {
        return !sort.equals("accuracy") && !sort.equals("recency");
    }

    private boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidRange(int page, int min, int max) {
        return page < min || page > max;
    }

    private void cacheKeyword(String keyword) {
        keywordCacheMap.merge(keyword, 1, Integer::sum);
    }

    @Override
    public Top10Response searchTop10() {
        return new Top10Response(top10UpdateTime, top10UpdatePeriod, top10);
    }

    @Override
    public void clearTop10() {
        top10.clear();
    }

    @Override
    public void putUpdatedTop10(LocalDateTime updateTime, List<KeywordRank> newTop10) {
        if (newTop10.size() == 0) {
            log.error("There's a problem with the Top10 update. Size of New : 0");
            // TODO throw 비즈니스 로직 Exception
            throw new IllegalStateException();
        }
        top10UpdateTime = updateTime;
        top10.addAll(newTop10);

        String updateTop10Log = String.join(", ", top10.stream().map(KeywordRank::toString).toArray(String[]::new));
        log.info("Successfully updated to the new Top10 - updateTime {}, New top10 list : {}", updateTime, updateTop10Log);
    }
}
