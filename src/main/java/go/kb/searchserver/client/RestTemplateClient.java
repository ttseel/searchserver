package go.kb.searchserver.client;


import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
public class RestTemplateClient {
    private RestTemplate restTemplate = new RestTemplate();

    public <T> ResponseEntity<T> exchange(URI uri, HttpHeaders headers, Class<T> responseType) {
        var requestEntity = RequestEntity.get(uri).headers(headers).build();
        return restTemplate.exchange(requestEntity, responseType);
    }

    public URI buildUri(String host, String path, Map<String, String> queryParams) {
        var uriBuilder = UriComponentsBuilder.fromUriString(host).path(path);

        for (var entry : queryParams.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }

        return uriBuilder.encode().build().toUri();
    }

    public HttpHeaders createHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();

        for (var entry : headers.entrySet()) {
            httpHeaders.set(entry.getKey(), entry.getValue());
        }

        return httpHeaders;
    }
}