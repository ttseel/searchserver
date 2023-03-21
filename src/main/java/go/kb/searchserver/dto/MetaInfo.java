package go.kb.searchserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaInfo {
    private String sort;
    private int page;
    private int size;
    private int totalCount;
}