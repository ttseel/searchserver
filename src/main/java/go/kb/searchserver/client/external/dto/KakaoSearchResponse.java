package go.kb.searchserver.client.external.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoSearchResponse implements ExternalResponse {

    private List<Document> documents;
    private Meta meta;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Document implements ExternalDocument {
        private String blogname;
        private String contents;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private LocalDateTime datetime;
        private String thumbnail;
        private String title;
        private String url;

        @Override
        public String getBlogName() {
            return blogname;
        }

        @Override
        public LocalDate getDate() {
            return datetime.toLocalDate();
        }

        @Override
        public LocalTime getTime() {
            return datetime.toLocalTime();
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Meta {

        private Integer totalCount;
        private Integer pageableCount;
        private Boolean isEnd;
    }

    @Override
    public Integer getTotalCount() {
        return meta.getTotalCount();
    }

    @Override
    public List<ExternalDocument> getExtDocuments() {
        return new ArrayList<>(documents);
    }
}