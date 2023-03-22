package go.kb.searchserver.service;

import go.kb.searchserver.client.external.ExternalSearchHandler;
import go.kb.searchserver.common.error.exception.ServiceException;
import go.kb.searchserver.common.util.UrlUtils;
import go.kb.searchserver.dto.KeywordRank;
import go.kb.searchserver.dto.SearchResponse;
import go.kb.searchserver.dto.Top10Response;
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

import static go.kb.searchserver.common.error.SearchErrorCode.*;

@Slf4j
@Qualifier("SearchServiceImpl")
@Service
public class SearchServiceImpl implements SearchService {

    @Qualifier("KakaoSearchHandler")
    @Autowired
    private ExternalSearchHandler externalSearchHandler;
    @Getter
    private final Map<String, Integer> keywordCacheMap = new ConcurrentHashMap<>();
    private final List<KeywordRank> top10 = new ArrayList<>();
    private LocalDateTime top10UpdateTime;
    @Value("${schedule.top10.period}")
    private int top10UpdatePeriod;

    @Override
    public SearchResponse searchBlog(String query, String sort, String page, String size) {
        sort = correctTheSort(sort);
        requestValidation(query, sort, page, size);

        cacheKeyword(query);

        return externalSearchHandler.searchBlog(query, sort, Integer.parseInt(page), Integer.parseInt(size));
    }

    private String correctTheSort(String sort) {
        return sort.equals("recency") ? sort : "accuracy";
    }


    private void requestValidation(String query, String sort, String page, String size) {
        if (isInvalidQuery(query)) {
            throw new ServiceException(INVALID_SEARCH_PARAM_QUERY);
        } else if (isInvalidSort(sort)) {
            throw new ServiceException(INVALID_SEARCH_PARAM_SORT);
        } else if (!isInteger(page) || isInvalidRange(Integer.parseInt(page), 1, 50)) {
            throw new ServiceException(INVALID_SEARCH_PARAM_PAGE);
        } else if (!isInteger(size) || isInvalidRange(Integer.parseInt(size), 1, 50)) {
            throw new ServiceException(INVALID_SEARCH_PARAM_SIZE);
        }
    }

    private boolean isInvalidQuery(String query) {
        return query == null || query.isBlank();
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

    private boolean isInvalidRange(int page, int min, int max) {
        return page < min || page > max;
    }

    private void cacheKeyword(String query) {
        UrlUtils.parseUrlFromString(query).ifPresentOrElse(
                url -> {
                    String keywordWithoutUrl = determineTheKeywordToCaching(query, url);
                    keywordCacheMap.merge(keywordWithoutUrl, 1, Integer::sum);
                },
                () -> keywordCacheMap.merge(query, 1, Integer::sum));
    }

    private String determineTheKeywordToCaching(String query, String url) {
        int lastIdxOfQuery = query.length() - 1;
        int lastIdxOfUrl = query.indexOf(url) + url.length() - 1;

        if (checkQueryHasKeywordWithoutUrl(lastIdxOfQuery, lastIdxOfUrl)) {
            return query.substring(lastIdxOfUrl + 2);
        } else {
            return url;
        }
    }

    private boolean checkQueryHasKeywordWithoutUrl(int lastIdxOfQuery, int lastIdxOfUrl) {
        return lastIdxOfQuery - lastIdxOfUrl > 1;
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
            log.warn("[Cause : {}]", "Can't find the top 10 to update from database. may be table is empty. check the table KEYWORD");
            return;
        }

        top10UpdateTime = updateTime;
        top10.addAll(newTop10);

        String updateTop10Log = String.join(", ", top10.stream().map(KeywordRank::toString).toArray(String[]::new));
        log.info("Successfully updated to the new Top10 - updateTime {}, New top10 list : {}", updateTime, updateTop10Log);
    }
}
