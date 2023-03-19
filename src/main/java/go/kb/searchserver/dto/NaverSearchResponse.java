package go.kb.searchserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverSearchResponse implements ExternalResponse {

    private String lastBuildDate;
    private Integer total;
    private Integer start;
    private Integer display;
    private List<Item> items;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Item implements ExternalDocument {
        private String title;
        private String link;
        private String description;
        private String bloggername;
        private String bloggerlink;
        private String postdate;

        @Override
        public String getContents() {
            return description;
        }

        @Override
        public String getUrl() {
            return link;
        }

        @Override
        public String getBlogName() {
            return bloggername;
        }

        @Override
        public String getThumbnail() {
            return "";
        }

        @Override
        public LocalDate getDate() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String strDate = "20230101";
            return LocalDate.parse(strDate, formatter);
        }

        @Override
        public LocalTime getTime() {
            return null;
        }
    }

    @Override
    public Integer getTotalCount() {
        return total;
    }


    @Override
    public List<ExternalDocument> getExtDocuments() {
        return new ArrayList<>(items);
    }
}
