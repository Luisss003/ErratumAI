package com.luis.textlift_backend.features.textbook.service;

import com.luis.textlift_backend.features.textbook.api.dto.GoogleApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleBooksApi {
    private final String apiEndpoint = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private final RestTemplate restTemplate;

    public GoogleBooksApi(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public GoogleApiResponseDto searchByIsbn(String isbn){
        ResponseEntity<GoogleApiResponseDto> res =
                restTemplate.getForEntity( this.apiEndpoint + isbn, GoogleApiResponseDto.class);
        return res.getBody();
    }

}
