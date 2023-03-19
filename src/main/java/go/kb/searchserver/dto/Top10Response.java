package go.kb.searchserver.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class Top10Response {
    List<KeywordRank> top10List;

    public Top10Response(List<KeywordRank> top10List) {
        this.top10List = top10List;
    }

    @RequiredArgsConstructor
    @Getter
    public static class KeywordRank {
        private final Integer rank;
        private final String keyword;
        private final Integer readCount;
    }
}
