package go.kb.searchserver.repository;

import go.kb.searchserver.domain.Keyword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@Transactional
class KeywordRepositoryTest {
    @Autowired
    KeywordRepository keywordRepository;
    Random random = new Random();

    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();
    }

    @DisplayName("키워드 수가 N개(1<=N<=10)일 때 Top10 조회 결과도 N개(1<=N<=10) 반환")
    @Test
    public void shouldReturnCount10WhenCountOfTop10IsBetween1And10() {

        //given
        int sampleSize = random.nextInt(10) + 1;
        createSampleKeyword(sampleSize);

        //when
        List<Keyword> keywords = keywordRepository.findTop10ByOrderByReadCountDesc();

        //then
        assertThat(keywords.size()).isEqualTo(sampleSize);
    }

    @DisplayName("키워드가 10개 초과일 때 Top10을 10개까지 반환")
    @Test
    public void shouldReturnCount10WhenCountOfTop10IsMoreThan10(@Autowired KeywordRepository keywordRepository) {
        // 실제 DB에 넣고 Test 해야함(JPA 로직 테스트이기 때문)
        //given
        int testKeywordCount = 11;
        createSampleKeyword(testKeywordCount);

        //when
        List<Keyword> keywords = keywordRepository.findTop10ByOrderByReadCountDesc();

        //then
        assertThat(keywords.size()).isEqualTo(10);
    }

    @DisplayName("저장한 값과 검색한 결과가 예상과 일치하는지 확인")
    @Test
    public void findByKeywordWithValidKeywordShouldReturnKeyword() {
        // given
        Keyword keyword = new Keyword("keyword", 10);
        keywordRepository.save(keyword);

        // when
        Optional<Keyword> result = keywordRepository.findByKeyword("keyword");

        // then
        assertThat(result).isPresent().contains(keyword);
    }

    @DisplayName("저장한 값과 검색한 결과가 예상과 일치하는지 확인")
    @Test
    // findTop10ByOrderByReadCountDesc 메소드를 호출하여, ReadCount가 가장 높은 상위 10개 Keyword 리스트가 예상과 일치하는지 확인하는 테스트 케이스입니다.
    public void findTop10ByOrderByReadCountDesc_withValidData_shouldReturnTop10List() {
        // given
        Keyword keyword1 = new Keyword("keyword1", 10);
        Keyword keyword2 = new Keyword("keyword2", 5);
        Keyword keyword3 = new Keyword("keyword3", 2);
        keywordRepository.saveAll(List.of(keyword1, keyword2, keyword3));

        // when
        List<Keyword> actualResult = keywordRepository.findTop10ByOrderByReadCountDesc();

        // then
        assertThat(actualResult).hasSize(3)
                .extracting(Keyword::getKeyword, Keyword::getReadCount)
                .containsExactly(tuple(keyword1.getKeyword(), keyword1.getReadCount()),
                        tuple(keyword2.getKeyword(), keyword2.getReadCount()),
                        tuple(keyword3.getKeyword(), keyword3.getReadCount()));
    }


    private void createSampleKeyword(int sampleSize) {
        Random random = new Random();
        for (int i = 0; i < sampleSize; i++) {
            Keyword keyword = new Keyword("keyword" + i, random.nextInt(Integer.MAX_VALUE));
            keywordRepository.save(keyword);
        }
    }
}