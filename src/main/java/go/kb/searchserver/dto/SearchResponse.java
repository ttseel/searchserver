package go.kb.searchserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Getter
    @Setter
    static class MetaInfo {
        private String sort;
        private int page;
        private int size;
        private int totalCount;
    }

    @Getter
    static class Posting {
        private String title;
        private String contents;
        private String url;
        private String blogName;
        private String thumbnail;
        private LocalDate date;
        private LocalTime time;

        public Posting(ExternalDocument extDocument) {
            title = extDocument.getTitle();
            contents = extDocument.getContents();
            url = extDocument.getUrl();
            blogName = extDocument.getBlogName();
            thumbnail = extDocument.getThumbnail();
            date = extDocument.getDate();
            time = extDocument.getTime();
        }
    }
}
