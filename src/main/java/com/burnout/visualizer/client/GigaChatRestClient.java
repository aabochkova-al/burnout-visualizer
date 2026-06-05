/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burnout.visualizer.client;

import com.burnout.visualizer.dto.GigaChatTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aleksandra
 */
@Component
public class GigaChatRestClient {
    private final WebClient webClient;
    private final String authKey;
    private final ObjectMapper objectMapper;
    private String cachedToken;
    private long tokenExpiryTime;
    
    public GigaChatRestClient(@Value("${gigachat.auth-key}") String authKey) {
        this.authKey = authKey;
        this.objectMapper = new ObjectMapper();
        this.webClient = WebClient.builder()
                .baseUrl("https://gigachat.devices.sberbank.ru/api/v1")
                .build();
    }
    
    private String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return cachedToken;
        }
        try {
            String responseJson = webClient.post()
                    .uri("/oauth")
                    .header("Authorization", "Bearer " + authKey)
                    .header("RqUID", UUID.randomUUID().toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            GigaChatTokenResponse tokenResponse = objectMapper.readValue(responseJson, GigaChatTokenResponse.class);
            cachedToken = tokenResponse.getAccessToken();
            tokenExpiryTime = System.currentTimeMillis() + 1800000; // 30 минут
            return cachedToken;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения токена GigaChat: " + e.getMessage(), e);
        }
    }
    
    public String generateImage(String prompt) {
        String token = getAccessToken();
        String requestBody = String.format("""
                {
                    "model": "GigaChat",
                    "messages": [
                        { "role": "system", "content": "Ты — художник, создающий яркие образы" },
                        { "role": "user", "content": "%s" }
                    ],
                    "function_call": "auto"
                }
                """, prompt);
        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            Pattern pattern = Pattern.compile("<img\\s+src=\"([a-f0-9-]+)\"");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                return matcher.group(1);
            }
            throw new RuntimeException("ID файла не найден в ответе GigaChat");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации изображения: " + e.getMessage(), e);
        }  
    }
    
    public byte[] downloadFile(String fileId) {
        String token = getAccessToken();
        try {
            return webClient.get()
                    .uri("/files/{fileId}/content", fileId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания файла: " + fileId, e);
        }
    }
}
