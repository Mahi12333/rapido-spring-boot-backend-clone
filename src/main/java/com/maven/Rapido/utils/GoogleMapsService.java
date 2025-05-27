package com.maven.Rapido.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    @Value("${google.api.key}")
    private String googleApiKey;

    @Qualifier("googleWebClient")
    private final WebClient googleWebClient;

    public Mono<Map<String, String>> getDistanceAndETA(double originLat, double originLng, double destLat, double destLng) {
        String origin = originLat + "," + originLng;
        String destination = destLat + "," + destLng;

        return googleWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/distancematrix/json")
                        .queryParam("origins", origin)
                        .queryParam("destinations", destination)
                        .queryParam("key", googleApiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    JsonNode element = response.path("rows").get(0).path("elements").get(0);
                    String distance = element.path("distance").path("text").asText();
                    String duration = element.path("duration").path("text").asText();

                    Map<String, String> result = new HashMap<>();
                    result.put("distance", distance);
                    result.put("eta", duration);
                    return result;
                });
    }
}
