package go.kb.searchserver.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Top10Response {
    LocalDateTime updateTime;
    String updatePeriod;
    List<KeywordRank> top10List;

    public Top10Response(LocalDateTime updateTime, int top10UpdatePeriod, List<KeywordRank> top10List) {
        this.updateTime = updateTime;
        this.updatePeriod = top10UpdatePeriod / 1000 + "sec";
        this.top10List = top10List;
    }
}
