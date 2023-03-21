package go.kb.searchserver.service;

import go.kb.searchserver.common.exception.CustomException;
import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.dto.Top10Response;
import go.kb.searchserver.repository.KeywordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static go.kb.searchserver.common.exception.SearchErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// TODO SearchService 내의 로직 테스트는 여기가 맞다
// TODO SearchController의 파라미터 조건을 테스트할 때(default value가 있다는 가정이 필요한 것)는 SearchControllerTest가 맞다. (통합 테스트가 되어야할 듯)

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SearchServiceTest {
    @Autowired
    private SearchService searchService;
    //    @MockBean
    private KeywordRepository mockKeywordRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Spy
    private SearchService spySearchService;
    private static final String VALID_KEYWORD = "valid keyword";
    private static final String VALID_SORT = "accuracy";
    private static final String VALID_PAGE = "1";
    private static final String VALID_SIZE = "1";

    @DisplayName("Top10 조회 결과가 0개일 때 Exception 반환")
    @Test
    public void shouldThrowExceptionWhenCountOfTop10EqualsZero() {
        //when
        when(mockKeywordRepository.findTop10ByOrderByReadCountDesc()).thenReturn(new ArrayList<>());

        //then
        assertThatThrownBy(() -> searchService.searchTop10()).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("Top10 조회 결과가 N개(1<=N<=10)일 때 N개를 결과로 반환")
    @Test
    public void shouldReturnCount10WhenCountOfTop10IsBetween1And10() {
        //given
        List<Keyword> keywords = new ArrayList<>();
        int topCount = 10;
        for (int i = 0; i < topCount; i++) {
            keywords.add(new Keyword("keyword" + i, i));

            //when
            when(mockKeywordRepository.findTop10ByOrderByReadCountDesc()).thenReturn(keywords);

            //then
            assertThat(searchService.searchTop10().getTop10List().size()).isEqualTo(i + 1);
        }
    }

    @DisplayName("키워드가 10개 초과일 때 Top10을 10개까지 반환")
    @Test
    public void shouldReturnCount10WhenCountOfTop10IsMoreThan10(@Autowired KeywordRepository keywordRepository) {
        // 실제 DB에 넣고 Test 해야함(JPA 로직 테스트이기 때문)
        //given
        int testKeywordCount = 11;
        createSampleKeyword(testKeywordCount);

        //when
        Top10Response top10Response = searchService.searchTop10();

        //then
        System.out.println(top10Response);
    }


    @DisplayName("query = null 일 때 에러 코드 SE101(INVALID_SEARCH_PARAM_KEYWORD) 반환")
    @Test
    public void shouldReturnErrorWhenQueryIsNull() {
        assertThatThrownBy(() -> searchService.searchBlog(null, VALID_SORT, VALID_PAGE, VALID_SIZE))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_KEYWORD);
    }

    @DisplayName("query = ' '(공백) 일 때 에러 코드 SE101(INVALID_SEARCH_PARAM_KEYWORD) 반환")
    @Test
    public void shouldReturnErrorWhenQueryEqualsWhiteSpace() {
        assertThatThrownBy(() -> searchService.searchBlog(" ", "accuracy", VALID_PAGE, VALID_SIZE))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_KEYWORD);
    }

    @DisplayName("page가 integer가 아니면, 에러 코드 SE103(INVALID_SEARCH_PARAM_PAGE) 반환")
    @Test
    public void shouldReturnErrorWhenPageIsNotInteger() {
        List<String> exceptionSet = List.of("a", "%", "1.1", "-1.1", "+1.1", "0.0", ".");
        for (String exceptionValue : exceptionSet) {
            System.out.println("exceptionValue : " + exceptionValue);
            assertThatThrownBy(() -> searchService.searchBlog(VALID_KEYWORD, VALID_SORT, VALID_SIZE, exceptionValue))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_PAGE);
        }
    }

    @DisplayName("page<1 또는 page>50 일 때 에러 코드 SE103(INVALID_SEARCH_PARAM_PAGE) 반환")
    @Test
    public void shouldReturnErrorCodeWhenPageIsOutsideValidRange() {
        //given
        String testNegative = "-1";
        String testMinBoundary = "0";
        String testMaxBoundary = "51";
        List<String> testSet = List.of(testNegative, testMinBoundary, testMaxBoundary);

        // then
        for (String testValue : testSet) {
            System.out.println("testValue : " + testValue);
            assertThatThrownBy(() -> searchService.searchBlog(VALID_KEYWORD, VALID_SORT, testValue, VALID_SIZE))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_PAGE);
        }
    }

    @DisplayName("size가 integer가 아니면, 에러 코드 SE104(INVALID_SEARCH_PARAM_SIZE) 반환")
    @Test
    public void shouldReturnErrorWhenSizeIsNotInteger() {
        List<String> exceptionSet = List.of("a", "%", "1.1", "-1.1", "+1.1", "0.0", ".");
        for (String exceptionValue : exceptionSet) {
            System.out.println("exceptionValue : " + exceptionValue);
            assertThatThrownBy(() -> searchService.searchBlog(VALID_KEYWORD, VALID_SORT, exceptionValue, VALID_PAGE))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_SIZE);
        }
    }

    @DisplayName("size<1 또는 size>50 일 때 에러 코드 SE104(INVALID_SEARCH_PARAM_SIZE) 반환")
    @Test
    public void shouldReturnErrorWhenSizeIsOutsideValidRange() {
        //given
        String testNegative = "-1";
        String testMinBoundary = "0";
        String testMaxBoundary = "51";
        List<String> testSet = List.of(testNegative, testMinBoundary, testMaxBoundary);

        // then
        for (String testValue : testSet) {
            System.out.println("testValue : " + testValue);
            assertThatThrownBy(() -> searchService.searchBlog(VALID_KEYWORD, VALID_SORT, VALID_PAGE, testNegative))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_SIZE);
        }
    }


//    @DisplayName("조회 성공")
//    @DisplayName("조회 성공 - 검색 결과가 없는 경우, 성공 응답하고 contents는 빈 리스트 반환")
//    @DisplayName("recency 외 모든 sort 파라미터 인풋 -> accuracy 리턴 체크")
//    @DisplayName("recency일 때 최신순인지 체크(결과의 dateTime)으로 확인")
//    @DisplayName("새로운 키워드가 잘 저장되는지")
//    @DisplayName("increment keyword 테스트 (Single-thread")
//    @DisplayName("increment keyword 테스트 (Multi-thread")
//    @DisplayName("카카오 장애시 네이버에서 검색 결과")
//    @DisplayName("카카오, 네이버 장애시 EXTERNAL_SEARCH_REQUEST_FAILED 반환")

    @DisplayName("")
    @Test
    public void test() {
        //given
        createSampleKeyword(5);

        //when

        //then
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