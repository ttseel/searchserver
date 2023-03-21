package go.kb.searchserver.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString(of = {"rank", "keyword", "readCount"})
public class KeywordRank {
    private final Integer rank;
    private final String keyword;
    private final Integer readCount;
}