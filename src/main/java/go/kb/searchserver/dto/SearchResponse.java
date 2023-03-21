package go.kb.searchserver.dto;

import go.kb.searchserver.client.external.dto.ExternalResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchResponse {
    private MetaInfo metaInfo = new MetaInfo();
    private List<Posting> postingList;

    public SearchResponse(ExternalResponse extResponse, String sort, int page, int size) {
        metaInfo.setSort(sort);
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalCount(extResponse.getTotalCount());
        postingList = extResponse.getExtDocuments().stream().map(Posting::new).collect(Collectors.toList());
    }
}
