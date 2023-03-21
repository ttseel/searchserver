package go.kb.searchserver.client.external.dto;


import java.util.List;

public interface ExternalResponse {
    Integer getTotalCount();

    List<ExternalDocument> getExtDocuments();
}
