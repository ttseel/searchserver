package go.kb.searchserver.dto;


import java.util.List;

public interface ExternalResponse {
    Integer getTotalCount();

    List<ExternalDocument> getExtDocuments();
}
