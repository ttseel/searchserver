package go.kb.searchserver.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UrlUtilsTest {
    @Test
    void testUrlParser() {
        List<String> urlTestSet = List.of(
                "https://www.kakao.com/api/123",
                "https://www.kakao.com/api/123",
                "http://www.kakao.com/api/123",
                "www.kakao.com/api/123",
                "kakao.com/api/123",
                "http://www.kakao.com/api/123/!",
                "http://www.kakao.com/api/123/@",
                "http://www.kakao.com/api/123/#",
                "http://www.kakao.com/api/123/$",
                "http://www.kakao.com/api/123/%",
                "http://www.kakao.com/api/123/^",
                "http://www.kakao.com/api/123/&",
                "http://www.kakao.com/api/123/*",
                "http://www.kakao.com/api/123/(",
                "http://www.kakao.com/api/123/)",
                "http://www.kakao.com/api/123/+",
                "http://www.kakao.com/api/123/-",
                "http://www.kakao.com/api/123/?",
                "http://www.kakao.com/api/123/'",
                "http://www.kakao.com/api/123/\""
        );

        for (String url : urlTestSet) {
            String parsed = UrlUtils.parseUrlFromString(url + " " + "검 색 어").orElse("");
            assertThat(parsed).isEqualTo(url);
        }
    }

    @Test
    void testParsingKeywordFromQuery() {
        String keyword = "this is keyword";
        String testQuery = "http://www.kakao.com/api/123" + " " + keyword;
        String url = UrlUtils.parseUrlFromString(testQuery).orElseThrow();

        int lastIdxOfUrl = testQuery.indexOf(url) + url.length() - 1;
        int lastIdxOfQuery = testQuery.length() - 1;


        if (checkQueryHasKeywordWithoutUrl(lastIdxOfQuery, lastIdxOfUrl)) {
            assertThat(testQuery.substring(lastIdxOfUrl + 2)).isEqualTo(keyword);
        } else {
            throw new IllegalStateException("testQuery should have keyword : " + keyword);
        }
    }

    private boolean checkQueryHasKeywordWithoutUrl(int lastIdxOfQuery, int lastIdxOfUrl) {
        return lastIdxOfQuery - lastIdxOfUrl > 1;
    }
}