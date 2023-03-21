package go.kb.searchserver.client.external.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ExternalDocument {
    String getTitle();

    String getContents();

    String getUrl();

    String getBlogName();

    String getThumbnail();

    LocalDate getDate();

    LocalTime getTime();
}
