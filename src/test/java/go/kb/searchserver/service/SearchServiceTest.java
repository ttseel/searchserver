package go.kb.searchserver.service;

import go.kb.searchserver.client.RestTemplateClient;
import go.kb.searchserver.common.error.exception.ServiceException;
import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.dto.KeywordRank;
import go.kb.searchserver.dto.Top10Response;
import go.kb.searchserver.repository.KeywordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static go.kb.searchserver.common.error.SearchErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// TODO SearchService 내의 로직 테스트는 여기가 맞다
// TODO SearchController의 파라미터 조건을 테스트할 때(default value가 있다는 가정이 필요한 것)는 SearchControllerTest가 맞다. (통합 테스트가 되어야할 듯)

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SearchServiceTest {
    @Autowired
    SearchService searchService;
    @Autowired
    KeywordRepository keywordRepository;
    @MockBean
    RestTemplateClient mockRestTemplateClient;

    private static final String VALID_KEYWORD = "valid keyword";
    private static final String VALID_SORT = "accuracy";
    private static final String VALID_PAGE = "1";
    private static final String VALID_SIZE = "1";


    @DisplayName("query = null 일 때 에러 코드 SE101(INVALID_SEARCH_PARAM_KEYWORD) 반환")
    @Test
    public void shouldReturnErrorWhenQueryIsNull() {
        assertThatThrownBy(() -> searchService.searchBlog(null, VALID_SORT, VALID_PAGE, VALID_SIZE))
                .isInstanceOf(ServiceException.class)
                .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_KEYWORD);
    }

    @DisplayName("query = ' '(공백) 일 때 에러 코드 SE101(INVALID_SEARCH_PARAM_KEYWORD) 반환")
    @Test
    public void shouldReturnErrorWhenQueryEqualsWhiteSpace() {
        assertThatThrownBy(() -> searchService.searchBlog(" ", "accuracy", VALID_PAGE, VALID_SIZE))
                .isInstanceOf(ServiceException.class)
                .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_KEYWORD);
    }

    @DisplayName("page가 integer가 아니면, 에러 코드 SE103(INVALID_SEARCH_PARAM_PAGE) 반환")
    @Test
    public void shouldReturnErrorWhenPageIsNotInteger() {
        List<String> exceptionSet = List.of("a", "%", "1.1", "-1.1", "+1.1", "0.0", ".");
        for (String exceptionValue : exceptionSet) {
            System.out.println("exceptionValue : " + exceptionValue);
            assertThatThrownBy(() -> searchService.searchBlog(VALID_KEYWORD, VALID_SORT, VALID_SIZE, exceptionValue))
                    .isInstanceOf(NumberFormatException.class);
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
                    .isInstanceOf(ServiceException.class)
                    .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_PAGE);
        }
    }

    @DisplayName("size가 integer가 아니면, 에러 코드 SE104(INVALID_SEARCH_PARAM_SIZE) 반환")
    @Test
    public void shouldReturnErrorWhenSizeIsNotInteger() {
        List<String> exceptionSet = List.of("a", "%", "1.1", "-1.1", "+1.1", "0.0", ".");
        for (String exceptionValue : exceptionSet) {
            System.out.println("exceptionValue : " + exceptionValue);
            try {
                searchService.searchBlog(VALID_KEYWORD, VALID_SORT, VALID_PAGE, exceptionValue);
            } catch (Exception e) {
                assertThatThrownBy(() -> searchService.searchBlog(VALID_KEYWORD, VALID_SORT, VALID_PAGE, exceptionValue))
                        .isInstanceOf(NumberFormatException.class);
            }
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
                    .isInstanceOf(ServiceException.class)
                    .extracting("errorCode").isEqualTo(INVALID_SEARCH_PARAM_SIZE);
        }
    }

    @DisplayName("recency 외 모든 sort 파라미터 인풋 -> accuracy 리턴 체크")
    public void shouldSortParameterExceptRecencyMustBeAccuracy() {
        //given
        createSampleKeyword(5);

        //when

        //then
    }

    //    @DisplayName("recency일 때 최신순인지 체크(결과의 dateTime)으로 확인")

    @DisplayName("Top10 조회 결과가 0개일 때 0개 반환")
    @Test
    public void shouldThrowExceptionWhenCountOfTop10EqualsZero() {
        // given
        LocalDateTime updateTime = LocalDateTime.now();
        List<KeywordRank> top10 = List.of();

        searchService.putUpdatedTop10(updateTime, top10);

        // when
        Top10Response actualResponse = searchService.searchTop10();

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getTop10List().size()).isEqualTo(0);
    }
//    @DisplayName("increment keyword 테스트 (Single-thread")
//    @DisplayName("increment keyword 테스트 (Multi-thread")
//    @DisplayName("카카오 장애시 네이버에서 검색 결과")
//    @DisplayName("카카오, 네이버 장애시 EXTERNAL_SEARCH_REQUEST_FAILED 반환")

    @DisplayName("")
    @Test
    public void test() {
        //given

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