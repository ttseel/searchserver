package go.kb.searchserver.dto;

import go.kb.searchserver.client.external.dto.ExternalDocument;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class Posting {
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