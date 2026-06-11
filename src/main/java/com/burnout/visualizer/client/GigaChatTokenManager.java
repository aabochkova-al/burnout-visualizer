/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author aleksandra
 */
@Component
public class GigaChatTokenManager {
    private final RestTemplate restTemplate;
    private final String authKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String cachedToken;
    private long tokenExpiryTime;

    public GigaChatTokenManager(@Qualifier("restTemplateWithDisabledSSL") RestTemplate restTemplate,
                                @Value("${gigachat.auth-key}") String authKey) {
        this.restTemplate = restTemplate;
        this.authKey = authKey;
    }

    public synchronized String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return cachedToken;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + authKey);
            headers.set("RqUID", UUID.randomUUID().toString());

            HttpEntity<String> entity = new HttpEntity<>("scope=GIGACHAT_API_PERS", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                HttpMethod.POST,
                entity,
                String.class
            );
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            cachedToken = jsonNode.get("access_token").asText();
            tokenExpiryTime = System.currentTimeMillis() + 30 * 60 * 1000;
            return cachedToken;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения токена GigaChat: " + e.getMessage(), e);
        }
    }
}
