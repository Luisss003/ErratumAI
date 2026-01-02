package com.luis.textlift_backend.features.upload.service;

import com.luis.textlift_backend.features.upload.api.dto.VirusTotalResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class VirusTotalApi {
    private final String endpoint = "https://www.virustotal.com/api/v3/files/";
    private final RestTemplate restTemplate;
    private final String apiKey;

    public VirusTotalApi(RestTemplate restTemplate,
                         @Value("${virustotal.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public enum Verdict { SAFE, UNSAFE, RETRY_LATER }

    public Verdict check(String hash) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-apikey", apiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<VirusTotalResponseDto> resp = restTemplate.exchange(
                    endpoint + hash,
                    HttpMethod.GET,
                    entity,
                    VirusTotalResponseDto.class
            );

            VirusTotalResponseDto body = resp.getBody();
            if (body == null || body.data() == null || body.data().attributes() == null) {
                return Verdict.UNSAFE;
            }

            var attributes = body.data().attributes();
            var stats = attributes.lastAnalysisStats();
            if (stats == null) return Verdict.UNSAFE;

            boolean hasYaraHits = attributes.crowdsourcedYaraResults() != null
                    && !attributes.crowdsourcedYaraResults().isEmpty();
            boolean hasMalicious = stats.malicious() != null && stats.malicious() > 0;
            boolean hasSuspicious = stats.suspicious() != null && stats.suspicious() > 0;

            return (hasYaraHits || hasMalicious || hasSuspicious) ? Verdict.UNSAFE : Verdict.SAFE;

        } catch (RestClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.TOO_MANY_REQUESTS || status.is5xxServerError()) {
                return Verdict.RETRY_LATER;
            }
            return Verdict.UNSAFE;
        } catch (RestClientException e) {
            return Verdict.RETRY_LATER;
        }
    }
}
