package go.kb.searchserver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Top10Response {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime updateTime;
    String updatePeriod;
    List<KeywordRank> top10List;

    public Top10Response(LocalDateTime updateTime, int top10UpdatePeriod, List<KeywordRank> top10List) {
        this.updateTime = updateTime;
        this.updatePeriod = top10UpdatePeriod / 1000 + "sec";
        this.top10List = top10List;
    }
}
