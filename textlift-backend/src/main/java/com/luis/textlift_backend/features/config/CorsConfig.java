package com.luis.textlift_backend.features.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${CORS_ALLOWED_ORIGINS}") List<String> origins
    ) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-XSRF-TOKEN"
        ));
        configuration.setExposedHeaders(List.of(
                "Set-Cookie"
        ));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
